package com.xafero.slr.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.version.Version;

import com.xafero.slr.util.hack.SimpleRepositoryConnectorFactory;
import com.xafero.slr.util.log.LoggingRepositoryListener;
import com.xafero.slr.util.log.LoggingTransferListener;

public class MavenHelper {

	public static RemoteRepository newCentralRepository() {
		return newCentralRepository("http://repo1.maven.org/maven2/");
	}

	public static RemoteRepository newCentralRepository(String url) {
		return new RemoteRepository.Builder("central", "default", url).build();
	}

	public static RepositorySystem newSystem() {
		// Create a new service locator
		DefaultServiceLocator locator = MavenRepositorySystemUtils
				.newServiceLocator();
		// Create a special connector factory which simply works
		locator.addService(RepositoryConnectorFactory.class,
				SimpleRepositoryConnectorFactory.class);
		// Create a new system
		return locator.getService(RepositorySystem.class);
	}

	public static RepositorySystemSession newSession(RepositorySystem system) {
		return newSession(system, false, false);
	}

	public static RepositorySystemSession newSession(RepositorySystem system,
			boolean verboseTransfer, boolean verboseRepoEvents) {
		// Create a new session
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils
				.newSession();
		// Create a new local repository
		LocalRepository localRepo = new LocalRepository("target/local-repo");
		LocalRepositoryManager repoMgr = system.newLocalRepositoryManager(
				session, localRepo);
		session.setLocalRepositoryManager(repoMgr);
		// Set listeners
		if (verboseTransfer)
			session.setTransferListener(new LoggingTransferListener());
		if (verboseRepoEvents)
			session.setRepositoryListener(new LoggingRepositoryListener());
		// Take that!
		return session;
	}

	public static CollectRequest newCollectRequest(Artifact artifact,
			RemoteRepository repo) {
		CollectRequest cr = new CollectRequest();
		cr.setRootArtifact(artifact);
		cr.addRepository(repo);
		return cr;
	}

	public static VersionRangeRequest newVersionRequest(Artifact artifact,
			RemoteRepository repo) {
		VersionRangeRequest vrr = new VersionRangeRequest();
		vrr.setArtifact(artifact);
		vrr.addRepository(repo);
		return vrr;
	}

	public static List<Version> listAllVersions(RepositorySystem system,
			RepositorySystemSession session, Artifact artifact,
			RemoteRepository repo) throws VersionRangeResolutionException {
		VersionRangeResult versRsp = MavenHelper.getAllVersions(system,
				session, artifact, repo);
		return versRsp.getVersions();
	}

	public static Version getLatestVersion(RepositorySystem system,
			RepositorySystemSession session, Artifact artifact,
			RemoteRepository repo) throws VersionRangeResolutionException {
		VersionRangeResult versRsp = MavenHelper.getAllVersions(system,
				session, artifact, repo);
		return versRsp.getHighestVersion();
	}

	public static VersionRangeResult getAllVersions(RepositorySystem system,
			RepositorySystemSession session, Artifact artifact,
			RemoteRepository repo) throws VersionRangeResolutionException {
		VersionRangeRequest versReq = MavenHelper.newVersionRequest(artifact,
				repo);
		return system.resolveVersionRange(session, versReq);
	}

	public static String[] toStringArray(List<Version> rawVersions) {
		String[] versions = new String[rawVersions.size()];
		for (int i = 0; i < rawVersions.size(); i++)
			versions[i] = rawVersions.get(i) + "";
		return versions;
	}

	public static Artifact newArtifact(String args) {
		try {
			return new DefaultArtifact(args);
		} catch (IllegalArgumentException iae) {
			// Try adding a suffix!
			final String VersionSuffix = ":[0,)";
			if (!args.endsWith(VersionSuffix))
				return newArtifact(args + VersionSuffix);
			throw iae;
		}
	}

	public static CollectResult getDependencies(RepositorySystem system,
			RepositorySystemSession session, Artifact artifact,
			RemoteRepository repo) throws DependencyCollectionException {
		Dependency dep = new Dependency(artifact, "compile");
		CollectRequest collReq = new CollectRequest();
		collReq.setRoot(dep);
		collReq.addRepository(repo);
		return system.collectDependencies(session, collReq);
	}

	public static Artifact[] fetchDependencies(RepositorySystem system,
			RepositorySystemSession session, Artifact artifact,
			RemoteRepository repo) throws DependencyCollectionException,
			DependencyResolutionException {
		CollectResult collRsp = getDependencies(system, session, artifact, repo);
		DependencyRequest depReq = MavenHelper.newDependencyRequest(collRsp);
		DependencyResult depRsp = system.resolveDependencies(session, depReq);
		Artifact[] deps = new Artifact[depRsp.getArtifactResults().size()];
		int i = 0;
		for (ArtifactResult ar : depRsp.getArtifactResults())
			deps[i++] = ar.getArtifact();
		return deps;
	}

	public static DependencyRequest newDependencyRequest(CollectResult collRsp) {
		DependencyNode node = collRsp.getRoot();
		DependencyRequest depReq = new DependencyRequest();
		depReq.setRoot(node);
		return depReq;
	}

	public static URL[] toFileUrls(Artifact[] deps) {
		URL[] urls = new URL[deps.length];
		for (int i = 0; i < deps.length; i++)
			try {
				urls[i] = deps[i].getFile().toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		return urls;
	}
}