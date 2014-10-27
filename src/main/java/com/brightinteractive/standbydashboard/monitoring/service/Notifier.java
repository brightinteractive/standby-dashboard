package com.brightinteractive.standbydashboard.monitoring.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

public interface Notifier
{
	public void monitoringError(Monitor monitor, Throwable throwable);

	public void monitoringAlert(Monitor monitor);

	void monitoringCleared(Monitor monitor);
}
