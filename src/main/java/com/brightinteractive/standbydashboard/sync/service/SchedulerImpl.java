package com.brightinteractive.standbydashboard.sync.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

import org.apache.log4j.Logger;

/**
 * @author Bright Interactive
 */
public class SchedulerImpl implements Scheduler
{
	private static final Logger log = Logger.getLogger(SchedulerImpl.class);

	@Value("${schedule.cron}")
	private String scheduleCron;

	@Autowired
	ApplicationContext context;

	@Scheduled(cron = "${schedule.cron}")
	public void fireEvent()
	{
		SyncTask syncTask = context.getBean(SyncTask.class);
		syncTask.execute();
	}

}
