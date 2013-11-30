package com.xafero.slr.util.hack;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.internal.impl.DefaultFileProcessor;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

public class SimpleRepositoryConnectorFactory implements
		RepositoryConnectorFactory {
	private final BasicRepositoryConnectorFactory brcf;

	public SimpleRepositoryConnectorFactory() {
		brcf = new BasicRepositoryConnectorFactory();
		// brcf.setLoggerFactory(new Slf4jLoggerFactory());
		brcf.setFileProcessor(new DefaultFileProcessor());
		brcf.setRepositoryLayoutProvider(new MavenLayoutProvider());
		brcf.setTransporterProvider(new SimpleTransportProvider(
				FileTransporterFactory.class, HttpTransporterFactory.class));
	}

	public RepositoryConnector newInstance(RepositorySystemSession session,
			RemoteRepository repository) throws NoRepositoryConnectorException {
		return brcf.newInstance(session, repository);
	}

	public float getPriority() {
		return brcf.getPriority();
	}
}