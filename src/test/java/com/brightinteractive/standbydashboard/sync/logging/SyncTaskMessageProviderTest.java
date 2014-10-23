package com.brightinteractive.standbydashboard.sync.logging;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.brightinteractive.standbydashboard.application.logging.EventInMemoryAppender;

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
		when(eventInMemoryAppender.getEventMessages()).thenReturn(expectedMessages);
	}

	@Test
	public void testGetSyncTaskLogMessagesReturnsEmptyListIfAppenderNotFound()
	{
		when(syncTaskMessageProvider.findLogAppender()).thenReturn(null);

		assertNotNull(syncTaskMessageProvider.getSyncTaskLogMessages());
		assertTrue(syncTaskMessageProvider.getSyncTaskLogMessages().isEmpty());
	}

	@Test
	public void testGetSyncTaskLogMessagesReturnsMessagesFromExpectedAppender()
	{
		List<Appender> appenders = new ArrayList<Appender>();
		appenders.add(new ConsoleAppender());
		appenders.add(eventInMemoryAppender);

		when(syncTaskMessageProvider.getSyncTaskAppenders()).thenReturn(appenders.iterator());

		Assert.assertEquals(expectedMessages, syncTaskMessageProvider.getSyncTaskLogMessages());
	}

}