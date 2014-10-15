package com.brightinteractive.standbydashboard;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import org.springframework.beans.factory.annotation.Value;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.tasks.CopyTask;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;

/**
 * @author Bright Interactive
 */
public class SyncTaskImpl extends CopyTask implements SyncTask
{
	private Project project = new Project();
	protected boolean deleteMissingSourceFiles = false;
	private String ignoreMissingSource;
	private Logger log = Logger.getLogger(SyncTaskImpl.class);

	public SyncTaskImpl()
	{
		super.setProject(project);
		super.setSrcDirIsBase(true);
	}

	@Override
	@Value("${source.directory}")
	public void setSource(String absolutePath)
	{
		super.setSrcDir(absolutePath);
	}

	@Override
	@Value("${destination.directory}")
	public void setDestination(String absolutePath)
	{
		super.setDestDir(absolutePath);
	}

	@Value("${destination.deleteMissing}")
	public void setDeleteMissingSourceFiles(boolean deleteMissingSourceFiles)
	{
		this.deleteMissingSourceFiles = deleteMissingSourceFiles;
	}

	@Value("${destination.ignoreMissing}")
	public void setIgnoreMissingSource(String ignoreMissingSource)
	{
		this.ignoreMissingSource = ignoreMissingSource;
	}

	@Value("${source.includes}")
	public void setIncludes(String includes)
	{
		super.setIncludes(includes);
	}

	@Override
	public void execute()
	{
		log.info("executing started");
		super.execute();
		log.info("execution completed");
	}

	@Override
	public void handleOutput(String output)
	{
		log.info(output);
	}

	@Override
	protected boolean detectMissingSourceFiles()
	{
		return deleteMissingSourceFiles;
	}

	@Override
	protected void handleMissingSourceFile(final FileObject destFile)
		throws Exception
	{
		if (ignoreMissingSource != null && destFile.getName().getURI().endsWith("/" + ignoreMissingSource))
		{
			return;
		}
		else
		{
			log("deleting " + destFile.getURL());
			destFile.delete(Selectors.SELECT_SELF);
		}
	}

}
