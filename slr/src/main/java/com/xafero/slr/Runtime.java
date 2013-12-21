package com.xafero.slr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

import com.xafero.slr.api.ILogger;
import com.xafero.slr.api.IRuntime;
import com.xafero.slr.util.IOHelper;
import com.xafero.slr.util.MavenHelper;
import com.xafero.slr.util.RuntimeHelper;

public class Runtime implements IRuntime {

	private final RepositorySystem system;
	private final RepositorySystemSession session;
	private final RemoteRepository centralRepo;

	private ILogger logger;

	private Runtime() {
		system = MavenHelper.newSystem();
		session = MavenHelper.newSession(system);
		centralRepo = MavenHelper.newCentralRepository();
	}

	private static Runtime instance;

	public static synchronized IRuntime getInstance() {
		if (instance == null)
			instance = new Runtime();
		return instance;
	}

	public int require(String args) {
		logger.info("Requiring '%s'...", args);
		try {
			// Convert arguments to URL
			URL url = new URL(args);
			File folder = new File("target/wars");
			folder.mkdirs();
			// Translate URL to local file
			String fileName = IOHelper.last(url.getFile().split("/"));
			File file = new File(folder, fileName);
			// If file not already exists, download file
			if (!file.exists() || !file.canRead()) {
				InputStream input;
				// File protocol, so read it directly
				if (url.getProtocol().equalsIgnoreCase("file"))
					input = new FileInputStream(new File(url.getPath()));
				else
					// It's on the web
					input = url.openStream();
				FileOutputStream output = new FileOutputStream(file);
				IOHelper.copy(input, output);
			}
			// If it's a JAR, just put it on class path
			if (file.getName().endsWith(".jar")) {
				int result = RuntimeHelper
						.extendClassPath(file.toURI().toURL());
				logger.info("Added %s URL for '%s'.", result, file);
				return result;
			}
			// Extract WAR file
			if (file.getName().endsWith(".war")) {
				File warFolder = new File(folder, file.getName().replace(
						".war", ""));
				warFolder.mkdirs();
				List<URL> extracted = new LinkedList<URL>();
				JarFile war = new JarFile(file);
				Enumeration<JarEntry> entries = war.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (!entry.getName().endsWith(".jar"))
						continue;
					String libName = IOHelper.last(entry.getName().split("/"));
					File libFile = new File(warFolder, libName);
					if (!libFile.exists() || !libFile.canRead()) {
						InputStream input = war.getInputStream(entry);
						FileOutputStream output = new FileOutputStream(libFile);
						IOHelper.copy(input, output);
					}
					extracted.add(libFile.toURI().toURL());
				}
				war.close();
				// Add all extracted JARs to class path
				URL[] urls = extracted.toArray(new URL[extracted.size()]);
				int result = RuntimeHelper.extendClassPath(urls);
				logger.info("Added %s URL(s) for '%s'.", result, file);
				return result;
			}
			// Just panic if not known!
			throw new IOException("Unknown file '" + file + "'!");
		} catch (MalformedURLException e) {
			// If not an URL, it should be an artifact
			int result = requireMaven(args);
			logger.info("Added %s URL(s) for '%s'.", result, args);
			return result;
		} catch (IOException e) {
			throw new RuntimeException("require", e);
		}
	}

	private int requireMaven(String args) {
		int pos = args.indexOf('|');
		String[] parts = pos == -1 ? new String[] { args } : new String[] {
				args.substring(0, pos), args.substring(pos + 1) };
		String artf = parts[0].trim();
		String repu = parts.length == 1 ? null : parts[1].trim();
		RemoteRepository repo = repu == null ? centralRepo : MavenHelper
				.newCentralRepository("r" + System.nanoTime(), repu);
		return require(artf, repo);
	}

	private int require(String args, RemoteRepository repo) {
		try {
			Artifact artifact = MavenHelper.newArtifact(args);
			Artifact[] deps = MavenHelper.fetchDependencies(system, session,
					artifact, repo);
			URL[] urls = MavenHelper.toFileUrls(deps);
			return RuntimeHelper.extendClassPath(urls);
		} catch (Exception e) {
			throw new RuntimeException("require", e);
		}
	}

	public ILogger getLogger() {
		return logger;
	}

	@Override
	public void setLogger(ILogger logger) {
		this.logger = logger;
	}
}