package com.xafero.slr.util.hack;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.transfer.NoRepositoryLayoutException;
import org.eclipse.aether.util.repository.layout.MavenDefaultLayout;

@SuppressWarnings("deprecation")
public class MavenLayoutProvider implements RepositoryLayoutProvider {

	public RepositoryLayout newRepositoryLayout(
			RepositorySystemSession session, RemoteRepository repository)
			throws NoRepositoryLayoutException {
		return new MavenLayout();
	}

	private static class MavenLayout implements RepositoryLayout {
		private final MavenDefaultLayout layout = new MavenDefaultLayout();

		public URI getLocation(Metadata metadata, boolean upload) {
			return layout.getPath(metadata);
		}

		public URI getLocation(Artifact artifact, boolean upload) {
			return layout.getPath(artifact);
		}

		public List<Checksum> getChecksums(Metadata metadata, boolean upload,
				URI location) {
			return Collections.emptyList();
		}

		public List<Checksum> getChecksums(Artifact artifact, boolean upload,
				URI location) {
			return Collections.emptyList();
		}
	}
}