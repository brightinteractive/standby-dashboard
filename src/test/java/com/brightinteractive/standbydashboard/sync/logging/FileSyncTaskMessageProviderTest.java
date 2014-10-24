package com.brightinteractive.standbydashboard.sync.logging;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.brightinteractive.standbydashboard.application.logging.EventInMemoryAppender;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.junit.*;
import org.mockito.*;

public class FileSyncTaskMessageProviderTest
{
	@Spy
	private FileSyncTaskMessageProvider fileSyncTaskMessageProvider = new FileSyncTaskMessageProvider();
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
		when(fileSyncTaskMessageProvider.findLogAppender()).thenReturn(null);

		assertNotNull(fileSyncTaskMessageProvider.getSyncTaskLogMessages());
		assertTrue(fileSyncTaskMessageProvider.getSyncTaskLogMessages().isEmpty());
	}

	@Test
	public void testGetSyncTaskLogMessagesReturnsMessagesFromExpectedAppender()
	{
		List<Appender> appenders = new ArrayList<Appender>();
		appenders.add(new ConsoleAppender());
		appenders.add(eventInMemoryAppender);

		when(fileSyncTaskMessageProvider.getSyncTaskAppenders()).thenReturn(appenders.iterator());

		Assert.assertEquals(expectedMessages, fileSyncTaskMessageProvider.getSyncTaskLogMessages());
	}

}