package com.xafero.slr.util.hack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;

import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.transfer.NoRepositoryLayoutException;
import org.eclipse.aether.transfer.NoTransporterException;
import org.junit.Test;

import com.xafero.slr.util.MavenHelper;

public class HackTest {

	@Test
	public void testPrio() {
		SimpleRepositoryConnectorFactory srcf = new SimpleRepositoryConnectorFactory();
		assertEquals(0.0, srcf.getPriority(), 0);
	}

	@Test
	public void testMavenLayout() throws NoRepositoryLayoutException {
		MavenLayoutProvider mlp = new MavenLayoutProvider();
		RepositoryLayout rl = mlp.newRepositoryLayout(null, null);
		Metadata metadata = (new DummyMetadata()).groupId("g").artifactId("a")
				.version("v").type("t");
		boolean upload = false;
		assertEquals("g/a/v/t", rl.getLocation(metadata, upload) + "");
		URI location = null;
		assertEquals(0, rl.getChecksums(metadata, upload, location).size());
	}

	@Test(expected = RuntimeException.class)
	public void testSimpleTransport() {
		new SimpleTransportProvider(String.class);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testNullTransporter() throws NoTransporterException {
		SimpleTransportProvider stp = new SimpleTransportProvider(
				DummyTransportFactory.class);
		RemoteRepository repo = MavenHelper.newCentralRepository();
		assertNull(stp.newTransporter(null, repo));
	}
}