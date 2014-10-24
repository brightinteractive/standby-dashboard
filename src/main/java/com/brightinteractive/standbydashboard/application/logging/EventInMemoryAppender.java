package com.brightinteractive.standbydashboard.application.logging;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class EventInMemoryAppender extends AppenderSkeleton
{
	public static final long DEFAULT_MAXIMUM_EVENTS_TO_KEEP = 1;

	List<LoggingEvent> eventsList = new ArrayList();
	long maximumEventsToKeep = DEFAULT_MAXIMUM_EVENTS_TO_KEEP;

	@Override
	protected void append(LoggingEvent event)
	{
		eventsList.add(event);
		enforceEventLimit();
	}

	private void enforceEventLimit()
	{
		if (eventsList.size() > maximumEventsToKeep)
		{
			eventsList.remove(0);
		}
	}

	public List<String> getEventMessages()
	{
		List<String> messages = new ArrayList<String>();
		for (LoggingEvent loggingEvent : eventsList)
		{
			messages.add(getLayout().format(loggingEvent));
		}

		return messages;
	}

	public void setMaximumEventsToKeep(long a_maximumEventsToKeep)
	{
		maximumEventsToKeep = a_maximumEventsToKeep;
	}


	@Override
	public void close()
	{
	}

	@Override
	public boolean requiresLayout()
	{
		return true;
	}
}
