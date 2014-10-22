package com.brightinteractive.standbydashboard;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

/**
 * @author Bright Interactive
 */
public interface SyncTask
{

	public void setSource(String absolutePath);

	public void setDestination(String a_absolutePath);

	public void execute();
}
