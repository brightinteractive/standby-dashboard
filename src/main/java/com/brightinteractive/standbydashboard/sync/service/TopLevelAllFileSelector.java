package com.brightinteractive.standbydashboard.sync.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import org.apache.commons.vfs2.*;
import org.apache.log4j.Logger;

public class TopLevelAllFileSelector implements FileSelector
{
	private Logger log = Logger.getLogger(this.getClass());
	
	private String topLevelPath;

	public TopLevelAllFileSelector(String topLevelPath)
	{
		this.topLevelPath = topLevelPath;
	}

	@Override
	public boolean includeFile(FileSelectInfo fileInfo) throws Exception
	{
		String filePath = fileInfo.getFile().getName().getPath();
		FileObject topLevelFolder = VFS.getManager().resolveFile(topLevelPath);
		String topLevelPath = topLevelFolder.getName().getPath();
				
		log.debug(String.format("File included if %s != %s",filePath, topLevelPath));
		
		return !filePath.equals(topLevelPath);
	}

	@Override
	public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception
	{
		return fileInfo.getDepth() < 1;
	}
}
