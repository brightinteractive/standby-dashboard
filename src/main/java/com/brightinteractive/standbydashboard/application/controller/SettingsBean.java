package com.brightinteractive.standbydashboard.application.controller;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.redhogs.cronparser.CronExpressionDescriptor;

@Component
public class SettingsBean
{
	@Value("${fileSync.source.directory}")
	private String syncSource;
	@Value("${fileSync.destination.directory}")
	private String syncDestination;
	@Value("${fileSync.schedule.cron}")
	private String schedule;
	@Value("${fileSync.destination.ignoreMissing}")
	private String syncIgnored;
	@Value("${fileSync.source.excludes}")
	private String syncExcluded;
	@Value("${fileSync.source.includes}")
	private String syncIncluded;

	@Value("${monitor.fileSync.schedule.cron}")
	private String monitorSchedule;
	@Value("${monitor.fileSync.source.directory}")
	private String monitorSource;
	@Value("${monitor.fileSync.destination.directory}")
	private String monitorDestination;
	@Value("${monitor.fileSync.minutesAfterLastConfirmedSyncToAlert}")
	private String monitorThreshold;

	@Value("${monitor.notify.email.from}")
	private String monitorNotifyFrom;
	@Value("${monitor.notify.email.to}")
	private String monitorNotifyTo;

	public String getSyncSource()
	{
		return syncSource;
	}

	public String getSyncDestination()
	{
		return syncDestination;
	}

	public String getSyncSchedule()
	{
		try
		{
			return CronExpressionDescriptor.getDescription(schedule);
		}
		catch (ParseException e)
		{
			return schedule;
		}
	}

	public String getSyncIgnored()
	{
		return syncIgnored;
	}

	public String getSyncExcluded()
	{
		return syncExcluded;
	}

	public String getSyncIncluded()
	{
		return syncIncluded;
	}

	public String getMonitorSchedule()
	{
		return monitorSchedule;
	}

	public String getMonitorSource()
	{
		return monitorSource;
	}

	public String getMonitorDestination()
	{
		return monitorDestination;
	}

	public String getMonitorThreshold()
	{
		return monitorThreshold;
	}

	public String getMonitorNotifyFrom()
	{
		return monitorNotifyFrom;
	}

	public String getMonitorNotifyTo()
	{
		return monitorNotifyTo;
	}
}
