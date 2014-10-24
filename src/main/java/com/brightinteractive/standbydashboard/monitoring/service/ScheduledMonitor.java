package com.brightinteractive.standbydashboard.monitoring.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.brightinteractive.standbydashboard.monitoring.exception.MonitoringException;

@Component
public class ScheduledMonitor
{
	@Autowired
	Monitor monitor;

	@Autowired
	Notifier notifier;

	@PostConstruct
	public void startup()
	{
		monitor.reset();
	}

	@Scheduled(cron = "${monitor.fileSync.schedule.cron}")
	public void runMonitor()
	{
		try
		{
			if (monitor.isSuccessful())
			{
				monitor.reset();
			}
			else if (monitor.shouldAlert())
			{
				notifier.monitoringAlert(monitor);
			}
		}
		catch (MonitoringException e)
		{
			notifier.monitoringError(monitor, e);
		}
	}
}
