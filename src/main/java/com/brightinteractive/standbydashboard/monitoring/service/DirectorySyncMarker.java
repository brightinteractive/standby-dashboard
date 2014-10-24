package com.brightinteractive.standbydashboard.monitoring.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.io.*;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.brightinteractive.standbydashboard.monitoring.exception.MonitoringException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.vfs2.*;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

@Component
public class DirectorySyncMarker implements Monitor
{
	private static final String MARKER_FILE_NAME = ".sync-marker";

	private String sourceDirectoryPath;
	private String destinationDirectoryPath;

	private int minutesAfterLastConfirmedSyncToAlert;

	@Autowired
	public DirectorySyncMarker(@Value("${monitor.fileSync.source.directory}") String sourceDirectoryPath,
							   @Value("${monitor.fileSync.destination.directory}") String destinationDirectoryPath,
							   @Value("${monitor.fileSync.minutesAfterLastConfirmedSyncToAlert}") int minutesAfterLastConfirmedSyncToAlert)
	{
		Validate.notEmpty(sourceDirectoryPath);
		Validate.notEmpty(destinationDirectoryPath);

		this.sourceDirectoryPath = sourceDirectoryPath;
		this.destinationDirectoryPath = destinationDirectoryPath;
		this.minutesAfterLastConfirmedSyncToAlert = minutesAfterLastConfirmedSyncToAlert;
	}

	protected void writeSourceMarker()
	{
		OutputStream sourceMarker = null;

		try
		{
			sourceMarker = getVFSManager().resolveFile(getSourceDirectoryFilePath()).getContent().getOutputStream();
			IOUtils.write(generateMarkerFileContents(), sourceMarker);
		}
		catch (IOException e)
		{
			throw new MonitoringException(e);
		}
		finally
		{
			IOUtils.closeQuietly(sourceMarker);
		}
	}

	private String getSourceDirectoryFilePath()
	{
		return FilenameUtils.concat(sourceDirectoryPath, MARKER_FILE_NAME);
	}

	private String getDestinationDirectoryPath()
	{
		return FilenameUtils.concat(destinationDirectoryPath, MARKER_FILE_NAME);
	}

	private String generateMarkerFileContents()
	{
		return String.valueOf(new Date().getTime());
	}

	protected boolean markersMatch()
	{
		InputStream sourceMarkerData = null;
		InputStream destinationMarkerData = null;

		try
		{
			sourceMarkerData = getSourceMarkerData();
			destinationMarkerData = getDestinationMarkerData();
			return IOUtils.contentEquals(getSourceMarkerData(), getDestinationMarkerData());
		}
		catch (IOException e)
		{
			throw new MonitoringException(e);
		}
		finally
		{
			IOUtils.closeQuietly(sourceMarkerData);
			IOUtils.closeQuietly(destinationMarkerData);
		}
	}

	private InputStream getMarkerData(String markerFilePath) throws IOException
	{
		FileObject markerFile = getVFSManager().resolveFile(markerFilePath);

		if (markerFile.exists())
		{
			return markerFile.getContent().getInputStream();
		}

		return IOUtils.toInputStream("");
	}

	private InputStream getSourceMarkerData() throws IOException
	{
		return getMarkerData(getSourceDirectoryFilePath());
	}

	private InputStream getDestinationMarkerData() throws IOException
	{
		return getMarkerData(getDestinationDirectoryPath());
	}

	private FileSystemManager getVFSManager() throws FileSystemException
	{
		return VFS.getManager();
	}

	@Override
	public boolean isSuccessful()
	{
		return markersMatch();
	}

	@Override
	public void reset()
	{
		writeSourceMarker();
	}

	@Override
	public boolean shouldAlert()
	{
		return minutesSinceMarkerWritten() > minutesAfterLastConfirmedSyncToAlert;
	}

	@Override
	public String getName()
	{
		return String.format("Directory Sync Monitor: %s to %s", sourceDirectoryPath, destinationDirectoryPath);
	}

	@Override
	public String getAlertMessage()
	{
		try
		{
			return String.format("A modification made in the source directory on %s has not appeared in the destination directory. The file sync may no longer be running.", timeMarkerWritten().toString("dd/MM/yyyy HH:mm"));
		}
		catch (IOException e)
		{
			throw new MonitoringException(e);
		}
	}

	private int minutesSinceMarkerWritten()
	{
		InputStream sourceMarkerData = null;

		try
		{
			sourceMarkerData = getSourceMarkerData();
			return Minutes.minutesBetween(timeMarkerWritten(), new DateTime()).getMinutes();
		}
		catch (IOException e)
		{
			throw new MonitoringException(e);
		}
		finally
		{
			IOUtils.closeQuietly(sourceMarkerData);
		}
	}

	private DateTime timeMarkerWritten() throws IOException
	{
		return new DateTime(Long.parseLong(IOUtils.toString(getSourceMarkerData())));
	}
}
