package com.xafero.slr.util.log;

import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTransferListener implements TransferListener {
	private static final Logger log = LoggerFactory
			.getLogger(LoggingTransferListener.class);

	public void transferCorrupted(TransferEvent e)
			throws TransferCancelledException {
		log.debug("{}", e);
	}

	public void transferFailed(TransferEvent e) {
		log.debug("{}", e);
	}

	public void transferInitiated(TransferEvent e)
			throws TransferCancelledException {
		log.debug("{}", e);
	}

	public void transferProgressed(TransferEvent e)
			throws TransferCancelledException {
		log.debug("{}", e);
	}

	public void transferStarted(TransferEvent e)
			throws TransferCancelledException {
		log.debug("{}", e);
	}

	public void transferSucceeded(TransferEvent e) {
		log.debug("{}", e);
	}
}