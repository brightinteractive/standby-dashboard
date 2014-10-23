package com.brightinteractive.standbydashboard.sync.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.util.*;

import org.apache.commons.vfs2.*;

public class TopLevelAllFileSelector implements FileSelector
{
	private String topLevelPath;
	
	public TopLevelAllFileSelector(String topLevelPath)
	{
		this.topLevelPath = topLevelPath;
	}
	
	@Override
	public boolean includeFile(FileSelectInfo fileInfo) throws Exception
	{		
		return !fileInfo.getFile().getName().getPath().equals(topLevelPath);
	}
	
	@Override
	public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception
	{
		return fileInfo.getDepth() < 1;
	}
}
