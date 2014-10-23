package com.brightinteractive.standbydashboard.sync.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

import org.apache.log4j.Logger;

public class FileSyncScheduler
{
	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	ApplicationContext context;

	@Scheduled(cron = "${schedule.cron}")
	public void fireEvent()
	{
		FileSyncTask syncTask = context.getBean(FileSyncTask.class);
		syncTask.execute();
	}

}
