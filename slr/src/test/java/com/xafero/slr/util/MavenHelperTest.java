package com.xafero.slr.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.version.Version;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MavenHelperTest {
	private static RepositorySystem system;
	private static RepositorySystemSession session;
	private static RemoteRepository repo;

	private Artifact artifact;

	@BeforeClass
	public static void testSetupOnce() {
		system = MavenHelper.newSystem();
		session = MavenHelper.newSession(system);
		repo = MavenHelper.newCentralRepository();
	}

	@Before
	public void testSetup() {
		artifact = MavenHelper.newArtifact("junit:junit");
	}

	@Test
	public void testVersionListing() throws VersionRangeResolutionException {
		List<Version> versions = MavenHelper.listAllVersions(system, session,
				artifact, repo);
		assertNotNull(versions);
		assertEquals(20, versions.size());
		assertEquals("3.7", versions.get(0) + "");
		assertEquals("4.11", versions.get(versions.size() - 1) + "");
	}

	@Test
	public void testLatestVersion() throws VersionRangeResolutionException {
		Version latest = MavenHelper.getLatestVersion(system, session,
				artifact, repo);
		assertNotNull(latest);
		assertEquals("4.11", latest + "");
	}

	@Test
	public void testDependencies() throws DependencyCollectionException {
		CollectResult deps = MavenHelper.getDependencies(system, session,
				artifact, repo);
		assertNotNull(deps);
		assertEquals("junit:junit:jar:4.11 (compile)", deps + "");
		assertEquals("[]", deps.getExceptions() + "");
		assertEquals("[org.hamcrest:hamcrest-core:jar:1.3 (compile)]", deps
				.getRoot().getChildren() + "");
	}

	@Test
	public void testToStringArray() {
		Version ver = new DummyVersion();
		List<Version> versions = Arrays.asList(ver);
		String[] array = MavenHelper.toStringArray(versions);
		assertArrayEquals(new String[] { ver + "" }, array);
	}

	@Test
	public void testCollectRequest() {
		CollectRequest colRes = MavenHelper.newCollectRequest(artifact, repo);
		assertEquals(artifact, colRes.getRootArtifact());
		assertEquals("", colRes.getRequestContext());
		assertEquals(Arrays.asList(repo), colRes.getRepositories());
		assertEquals(null, colRes.getRoot());
		assertEquals(null, colRes.getTrace());
		assertEquals(0, colRes.getDependencies().size());
		assertEquals(0, colRes.getManagedDependencies().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewArtifact() {
		MavenHelper.newArtifact("?");
	}
}