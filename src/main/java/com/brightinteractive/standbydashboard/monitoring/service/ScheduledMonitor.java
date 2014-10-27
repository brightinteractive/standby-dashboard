package com.brightinteractive.standbydashboard.monitoring.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.brightinteractive.standbydashboard.monitoring.exception.MonitoringException;
import org.apache.log4j.Logger;

@Component
public class ScheduledMonitor
{
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	Monitor monitor;

	@Autowired
	Notifier notifier;

	@PostConstruct
	public void startup()
	{
		if(!monitor.hasRunBefore())
		{
			monitor.reset();
		}
	}

	@Scheduled(cron = "${monitor.fileSync.schedule.cron}")
	public void runMonitor()
	{
		try
		{
			if (monitor.checkIsSuccessful())
			{
				if(monitor.failedPreviously())
				{
					log.info(String.format("Monitor (%s) alert cleared", monitor.getName() ));
					notifier.monitoringCleared(monitor);
					monitor.clearAlert();
				}
				log.info(String.format("Monitor (%s) successful - reseting monitor", monitor.getName() ));
				monitor.reset();
			}
			else if (monitor.shouldAlert())
			{
				log.info(String.format("Monitor (%s) failed - alerting", monitor.getName()));
				notifier.monitoringAlert(monitor);
			}
			else
			{
				log.info(String.format("Monitor (%s) failed - waiting to alert", monitor.getName()));
			}
		}
		catch (MonitoringException e)
		{
			notifier.monitoringError(monitor, e);
		}
	}
}
