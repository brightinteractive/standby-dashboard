package com.brightinteractive.standbydashboard.monitoring.exception;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

public class MonitoringException extends RuntimeException
{
	public MonitoringException()
	{
	}

	public MonitoringException(String message)
	{
		super(message);
	}

	public MonitoringException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MonitoringException(Throwable cause)
	{
		super(cause);
	}
}
