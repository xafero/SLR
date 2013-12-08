package com.xafero.slr.util.log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryEvent.EventType;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;
import org.junit.Before;
import org.junit.Test;

import com.xafero.slr.util.MavenHelper;

public class LoggingTest {
	private RepositorySystemSession session;

	@Before
	public void testSetup() {
		RepositorySystem sys = MavenHelper.newSystem();
		session = MavenHelper.newSession(sys);
	}

	@Test
	public void testTransfer() {
		LoggingTransferListener ltl = new LoggingTransferListener();
		assertNotNull(ltl);
		TransferResource tr = new TransferResource("?", "?", null, null);
		TransferEvent event = new TransferEvent.Builder(session, tr).build();
		executeAllMethods(ltl, event);
	}

	@Test
	public void testRepository() {
		LoggingRepositoryListener lrl = new LoggingRepositoryListener();
		assertNotNull(lrl);
		EventType et = EventType.ARTIFACT_DOWNLOADED;
		RepositoryEvent event = new RepositoryEvent.Builder(session, et)
				.build();
		executeAllMethods(lrl, event);
	}

	private static void executeAllMethods(Object obj, Object... args) {
		Class<?> type = obj.getClass();
		for (Method method : type.getMethods()) {
			if (method.getDeclaringClass() == Object.class)
				continue;
			try {
				Object result = method.invoke(obj, args);
				assertNull(result);
			} catch (Exception e) {
				throw new RuntimeException(method.getName(), e);
			}
		}
	}
}