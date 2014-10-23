package com.brightinteractive.standbydashboard.sync.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.util.Arrays;
import java.util.List;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;

public class TopLevelExcludingFileSelector implements FileSelector
{
	private List<String> excludes;
	
	public TopLevelExcludingFileSelector(String excludeString)
	{
		this.excludes = Arrays.asList(excludeString.split(","));
	}

	@Override
	public boolean includeFile(FileSelectInfo fileInfo) throws Exception
	{
		FileName filename = fileInfo.getFile().getName();
		return fileInfo.getDepth() > 0 && !excludes.contains(filename.getBaseName());
	}

	@Override
	public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception
	{
		return fileInfo.getDepth() < 1;
	}
}
