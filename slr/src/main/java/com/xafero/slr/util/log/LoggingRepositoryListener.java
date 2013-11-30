package com.xafero.slr.util.log;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingRepositoryListener implements RepositoryListener {
	private static final Logger log = LoggerFactory
			.getLogger(LoggingRepositoryListener.class);

	public void artifactDeployed(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void artifactDeploying(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void artifactDescriptorInvalid(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void artifactDescriptorMissing(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void artifactDownloaded(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void artifactDownloading(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void artifactInstalled(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void artifactInstalling(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void artifactResolved(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void artifactResolving(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void metadataDeployed(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void metadataDeploying(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void metadataDownloaded(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void metadataDownloading(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void metadataInstalled(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void metadataInstalling(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void metadataInvalid(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void metadataResolved(RepositoryEvent e) {
		log.debug("{}", e);
	}

	public void metadataResolving(RepositoryEvent e) {
		log.debug("{}", e);
	}
}