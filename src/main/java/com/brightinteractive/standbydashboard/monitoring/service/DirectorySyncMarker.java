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
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

@Component
public class DirectorySyncMarker implements Monitor
{
	private Logger log = Logger.getLogger(this.getClass());

	protected static final String SYNC_MARKER_FILE_NAME = ".sync-marker";
	private static final String FAILED_MARKER_FILE_NAME = ".sync-failed";

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
		writeToDirectory(getSourceMarkerFilePath(), generateMarkerFileContents());
	}

	protected void writeToDirectory(String filePath, String fileContents)
	{
		log.info(String.format("Writing to %s", filePath));
		OutputStream sourceMarker = null;

		try
		{
			sourceMarker = getVFSManager().resolveFile(filePath).getContent().getOutputStream();
			IOUtils.write(fileContents, sourceMarker);
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

	private String getSourceMarkerFilePath()
	{
		return FilenameUtils.concat(sourceDirectoryPath, SYNC_MARKER_FILE_NAME);
	}

	private String getDestinationMarkerFilePath()
	{
		return FilenameUtils.concat(destinationDirectoryPath, SYNC_MARKER_FILE_NAME);
	}

	private String generateMarkerFileContents()
	{
		return String.valueOf(new Date().getTime());
	}

	protected boolean markersMatch()
	{
		InputStream sourceMarkerData = null;
		InputStream destinationMarkerData = null;

		log.info(String.format("Comparing %s and %s", getSourceMarkerFilePath(), getDestinationMarkerFilePath()));

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
		return getMarkerData(getSourceMarkerFilePath());
	}

	private InputStream getDestinationMarkerData() throws IOException
	{
		return getMarkerData(getDestinationMarkerFilePath());
	}

	private FileSystemManager getVFSManager() throws FileSystemException
	{
		return VFS.getManager();
	}

	@Override
	public boolean checkIsSuccessful()
	{
		return markersMatch();
	}

	public void setFailed()
	{
		writeFailedMarker();
	}

	private void writeFailedMarker()
	{
		writeToDirectory(getFailedMarkerFilePath(), generateFailedFileContents());
	}

	private String generateFailedFileContents()
	{
		return "Sync failed";
	}

	private String getFailedMarkerFilePath()
	{
		return FilenameUtils.concat(sourceDirectoryPath, FAILED_MARKER_FILE_NAME);
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

	@Override
	public boolean failedPreviously()
	{
		return failedMarkerExists();
	}

	private boolean failedMarkerExists()
	{
		try
		{
			return getVFSManager().resolveFile(getFailedMarkerFilePath()).exists();
		}
		catch (IOException e)
		{
			throw new MonitoringException(e);
		}
	}

	@Override
	public void clearAlert()
	{
		deleteFailedMarker();
	}

	@Override
	public String getAlertClearedMessage()
	{
		try
		{
			return String.format("A modification made in the source directory on %s has now appeared in the destination directory. The file sync appears to be running again.", timeMarkerWritten().toString("dd/MM/yyyy HH:mm"));
		}
		catch (IOException e)
		{
			throw new MonitoringException(e);
		}
	}

	@Override
	public boolean hasRunBefore()
	{
		return sourceMarkerExists();
	}

	protected boolean sourceMarkerExists()
	{
		try
		{
			return getVFSManager().resolveFile(getSourceMarkerFilePath()).exists();
		}
		catch (IOException e)
		{
			throw new MonitoringException(e);
		}
	}

	private void deleteFailedMarker()
	{
		try
		{
			getVFSManager().resolveFile(getFailedMarkerFilePath()).delete();
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
