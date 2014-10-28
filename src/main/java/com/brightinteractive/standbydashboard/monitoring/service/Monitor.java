package com.brightinteractive.standbydashboard.monitoring.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

public interface Monitor
{
	boolean checkIsSuccessful();

	void reset();

	boolean shouldAlert();

	String getName();

	String getAlertMessage();

	void setFailed();

	boolean failedPreviously();

	void clearAlert();

	String getAlertClearedMessage();

	boolean hasRunBefore();
}
