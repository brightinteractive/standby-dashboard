package com.brightinteractive.standbydashboard.sync.logging;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.util.*;

import org.springframework.stereotype.Service;

import com.brightinteractive.standbydashboard.sync.service.FileSyncTask;
import org.apache.commons.collections.iterators.EnumerationIterator;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import com.brightinteractive.standbydashboard.application.logging.EventInMemoryAppender;

/**
 * @author Bright Interactive
 */
@Service
public class FileSyncTaskMessageProvider
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
		return new EnumerationIterator(Logger.getLogger(FileSyncTask.class.getName()).getAllAppenders());
	}
}
