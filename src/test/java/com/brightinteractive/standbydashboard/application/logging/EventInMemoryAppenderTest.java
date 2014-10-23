package com.brightinteractive.standbydashboard.application.logging;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class EventInMemoryAppenderTest
{
	public static final String LOGGING_EVENT_1_EXPECTED_MESSAGE = "Logging Event 1 Expected Message ";
	public static final String LOGGING_EVENT_2_EXPECTED_MESSAGE = "Logging Event 2 Expected Message ";
	EventInMemoryAppender eventInMemoryAppender;
	
	@Mock
	LoggingEvent knownLoggingEvent1;
	@Mock
	LoggingEvent knownLoggingEvent2;	
	@Mock
	PatternLayout patternLayout;
	
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
				
		Mockito.when(patternLayout.format(knownLoggingEvent1)).thenReturn(LOGGING_EVENT_1_EXPECTED_MESSAGE);
		Mockito.when(patternLayout.format(knownLoggingEvent2)).thenReturn(LOGGING_EVENT_2_EXPECTED_MESSAGE);
		
		eventInMemoryAppender = new EventInMemoryAppender();
		eventInMemoryAppender.setLayout(patternLayout);
	}

	@Test
	public void testGetEventMessagesReturnsMessagesFromStoredEvents()
	{
		eventInMemoryAppender.setMaximumEventsToKeep(2);
		
		eventInMemoryAppender.append(knownLoggingEvent1);
		eventInMemoryAppender.append(knownLoggingEvent2);
		
		assertEquals(eventInMemoryAppender.getEventMessages().size(), 2);
		assertEquals(LOGGING_EVENT_1_EXPECTED_MESSAGE, eventInMemoryAppender.getEventMessages().get(0));
		assertEquals(LOGGING_EVENT_2_EXPECTED_MESSAGE, eventInMemoryAppender.getEventMessages().get(1));
		
	}
	
	@Test
	public void testGetEventEnforcesEventLimit()
	{
		eventInMemoryAppender.setMaximumEventsToKeep(1);
		
		eventInMemoryAppender.append(knownLoggingEvent1);
		eventInMemoryAppender.append(knownLoggingEvent2);
		
		assertEquals(eventInMemoryAppender.getEventMessages().size(), 1);
		assertEquals(LOGGING_EVENT_2_EXPECTED_MESSAGE, eventInMemoryAppender.getEventMessages().get(0));		
	}
}