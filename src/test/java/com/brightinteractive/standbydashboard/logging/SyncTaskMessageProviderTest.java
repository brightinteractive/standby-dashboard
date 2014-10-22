package com.brightinteractive.standbydashboard.logging;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class SyncTaskMessageProviderTest
{
	@Spy
	private SyncTaskMessageProvider syncTaskMessageProvider = new SyncTaskMessageProvider();
	@Mock
	private EventInMemoryAppender eventInMemoryAppender;
	private List<String> expectedMessages = new ArrayList<String>();


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		expectedMessages.add("Some message");
		Mockito.when(eventInMemoryAppender.getEventMessages()).thenReturn(expectedMessages);
	}

	@Test
	public void testGetSyncTaskLogMessagesReturnsEmptyListIfAppenderNotFound()
	{
		Mockito.when(syncTaskMessageProvider.findLogAppender()).thenReturn(null);

		assertNotNull(syncTaskMessageProvider.getSyncTaskLogMessages());
		assertTrue(syncTaskMessageProvider.getSyncTaskLogMessages().isEmpty());
	}

	@Test
	public void testGetSyncTaskLogMessagesReturnsMessagesFromExpectedAppender()
	{
		List<Appender> appenders = new ArrayList<Appender>();
		appenders.add(new ConsoleAppender());
		appenders.add(eventInMemoryAppender);

		Mockito.when(syncTaskMessageProvider.getSyncTaskAppenders()).thenReturn(appenders.iterator());

		assertEquals(expectedMessages, syncTaskMessageProvider.getSyncTaskLogMessages());
	}

}