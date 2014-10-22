package com.brightinteractive.standbydashboard.sync.logging;

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
		Mockito.when(eventInMemoryAppender.getEventMessages()).thenReturn(expectedMessages);
	}

	@Test
	public void testGetSyncTaskLogMessagesReturnsEmptyListIfAppenderNotFound()
	{
		Mockito.when(syncTaskMessageProvider.findLogAppender()).thenReturn(null);

		Assert.assertNotNull(syncTaskMessageProvider.getSyncTaskLogMessages());
		Assert.assertTrue(syncTaskMessageProvider.getSyncTaskLogMessages().isEmpty());
	}

	@Test
	public void testGetSyncTaskLogMessagesReturnsMessagesFromExpectedAppender()
	{
		List<Appender> appenders = new ArrayList<Appender>();
		appenders.add(new ConsoleAppender());
		appenders.add(eventInMemoryAppender);

		Mockito.when(syncTaskMessageProvider.getSyncTaskAppenders()).thenReturn(appenders.iterator());

		Assert.assertEquals(expectedMessages, syncTaskMessageProvider.getSyncTaskLogMessages());
	}

}