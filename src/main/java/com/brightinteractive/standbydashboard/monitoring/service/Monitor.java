package com.brightinteractive.standbydashboard.monitoring.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

public interface Monitor
{
	boolean isSuccessful();

	void reset();

	boolean shouldAlert();

	String getName();

	String getAlertMessage();
}
