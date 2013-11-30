package com.xafero.slr.util.hack;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.transport.Transporter;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.transfer.NoTransporterException;

public class SimpleTransportProvider implements TransporterProvider {
	private final TransporterFactory[] factories;

	public SimpleTransportProvider(Class<?>... factoryClasses) {
		factories = new TransporterFactory[factoryClasses.length];
		for (int i = 0; i < factoryClasses.length; i++)
			try {
				factories[i] = (TransporterFactory) factoryClasses[i]
						.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
	}

	public Transporter newTransporter(RepositorySystemSession session,
			RemoteRepository repository) throws NoTransporterException {
		Transporter result = null;
		for (TransporterFactory factory : factories) {
			try {
				Transporter trpt = factory.newInstance(session, repository);
				if (trpt != null) {
					result = trpt;
					break;
				}
			} catch (NoTransporterException nte) {
			}
		}
		if (result == null)
			throw new UnsupportedOperationException(
					"No transporter found for '" + repository.getProtocol()
							+ "'!");
		return result;
	}
}