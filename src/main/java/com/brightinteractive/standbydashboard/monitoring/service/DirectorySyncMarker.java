package com.brightinteractive.standbydashboard.monitoring.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.io.*;
import java.util.*;

import org.apache.commons.io.*;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.util.FileObjectUtils;

/**
 * @author Bright Interactive
 */
public class DirectorySyncMarker	
{
	private static final String MARKER_FILE_NAME = ".sync-marker";
		
	private String sourceDirectoryPath;
	private String destinationDirectoryPath;

	public DirectorySyncMarker(String sourceDirectoryPath, String destinationDirectoryPath)
	{
		Validate.notEmpty(sourceDirectoryPath);
		Validate.notEmpty(destinationDirectoryPath);
		
		this.sourceDirectoryPath = sourceDirectoryPath;
		this.destinationDirectoryPath = destinationDirectoryPath;
	}

	public void writeSourceMarker() throws IOException
	{
		FileSystemManager fileSystemManager = VFS.getManager();
		OutputStream sourceMarker = fileSystemManager.resolveFile(getSourceDirectoryFilePath()).getContent().getOutputStream();
		IOUtils.write(generateMarkerFileContents(), sourceMarker);
		sourceMarker.close();
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

	public boolean markersMatch() throws IOException
	{
		boolean markersMatch = false;
		
		FileSystemManager fileSystemManager = VFS.getManager();
		FileObject sourceMarkerObject = fileSystemManager.resolveFile(getSourceDirectoryFilePath());
		FileObject destinationMarkerObject = fileSystemManager.resolveFile(getDestinationDirectoryPath());
		
		if(sourceMarkerObject.exists() && destinationMarkerObject.exists())
		{
			InputStream sourceMarker = sourceMarkerObject.getContent().getInputStream();
			InputStream destinationMarker = destinationMarkerObject.getContent().getInputStream();
						
			markersMatch = IOUtils.contentEquals(sourceMarker, destinationMarker);	
							
			IOUtils.closeQuietly(sourceMarker);
			IOUtils.closeQuietly(destinationMarker);
		}
		
		return markersMatch;
	}
}
