package com.xafero.slr.util.hack;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.transport.Transporter;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.NoTransporterException;

public class DummyTransportFactory implements TransporterFactory {

	public Transporter newInstance(RepositorySystemSession session,
			RemoteRepository repository) throws NoTransporterException {
		return null;
	}

	public float getPriority() {
		return 0;
	}
}