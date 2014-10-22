package com.brightinteractive.standbydashboard.logging;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.util.*;

import org.springframework.stereotype.Service;

import org.apache.commons.collections.iterators.EnumerationIterator;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.brightinteractive.standbydashboard.SyncTaskImpl;

/**
 * @author Bright Interactive
 */
@Service
public class SyncTaskMessageProvider
{
	public List<String> getSyncTaskLogMessages()
	{
		EventInMemoryAppender logAppender = findLogAppender();
		if(logAppender != null)
		{
			return logAppender.getEventMessages();
		}
		
		return Collections.EMPTY_LIST;
	}

	protected EventInMemoryAppender findLogAppender()
	{
		Iterator<Appender> syncTaskAppenders = getSyncTaskAppenders();
		while(syncTaskAppenders.hasNext())
		{
			Appender app = syncTaskAppenders.next();
			if (app instanceof EventInMemoryAppender)
			{
				return ((EventInMemoryAppender) app);
			}
		}
		
		return null;
	}

	protected Iterator<Appender> getSyncTaskAppenders()
	{
		return new EnumerationIterator(Logger.getLogger(SyncTaskImpl.class.getName()).getAllAppenders());
	}
}
