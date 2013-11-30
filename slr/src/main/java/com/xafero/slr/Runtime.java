package com.xafero.slr;

import java.net.URL;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

import com.xafero.slr.api.IRuntime;
import com.xafero.slr.util.MavenHelper;
import com.xafero.slr.util.RuntimeHelper;

public class Runtime implements IRuntime {
	private final RepositorySystem system;
	private final RepositorySystemSession session;
	private final RemoteRepository centralRepo;

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
		try {
			Artifact artifact = MavenHelper.newArtifact(args);
			Artifact[] deps = MavenHelper.fetchDependencies(system, session,
					artifact, centralRepo);
			URL[] urls = MavenHelper.toFileUrls(deps);
			return RuntimeHelper.extendClassPath(urls);
		} catch (Exception e) {
			throw new RuntimeException("require", e);
		}
	}
}