package com.brightinteractive.standbydashboard.application.controller;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Value;

import net.redhogs.cronparser.CronExpressionDescriptor;

/**
 * @author Bright Interactive
 */
public class SettingsBean
{
	private String source;
	private String destination;
	private String schedule;
	private String ignored;
	private String excluded;

	private String included;

	public String getSource()
	{
		return source;
	}

	public String getDestination()
	{
		return destination;
	}

	public String getSchedule()
	{
		return schedule;
	}

	public String getIgnored()
	{
		return ignored;
	}

	public String getExcluded()
	{
		return excluded;
	}

	public String getIncluded()
	{
		return included;
	}

	@Value("${source.directory}")
	public void setSource(String source)
	{
		this.source = source;
	}

	@Value("${source.excludes}")
	public void setExcluded(String excluded)
	{
		this.excluded = excluded;
	}

	@Value("${destination.ignoreMissing}")
	public void setIgnored(String ignored)
	{
		this.ignored = ignored;
	}

	@Value("${destination.directory}")
	public void setDestination(String destination)
	{
		this.destination = destination;
	}

	@Value("${schedule.cron}")
	public void setSchedule(String schedule)
	{
		try
		{
			this.schedule = CronExpressionDescriptor.getDescription(schedule);
		}
		catch (ParseException e)
		{
			this.schedule = schedule;
		}
	}

	@Value("${source.includes}")
	public void setIncluded(String included)
	{
		this.included = included;
	}
}
