package com.brightinteractive.standbydashboard;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.util.*;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	private SyncTask syncTask;

	@PostConstruct
	public void afterPropertiesSet() throws Exception
	{
		log.info("out..." + scheduleCron);
	}

	@Scheduled(cron="${schedule.cron}")
	public void fireEvent()
	{
		syncTask.execute();

	}

}
