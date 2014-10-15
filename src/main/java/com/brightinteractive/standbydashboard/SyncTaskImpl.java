package com.brightinteractive.standbydashboard;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
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
	private String srcDir;

	public SyncTaskImpl()
	{
		super.setProject(project);
		super.setSrcDirIsBase(true);
	}

	@Override
	@Value("${source.directory}")
	public void setSource(String absolutePath)
	{
		System.out.println("setSource..." + absolutePath);
		srcDir = absolutePath;
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

	@Value("${source.excludes}")
	public void setExcludes(String excludes) throws FileSystemException
	{
		FileObject source = resolveFile(srcDir);
		ExcludingFileSelector fileSelector = new ExcludingFileSelector();
		fileSelector.setExcludes(excludes);

		FileObject[] files = source.findFiles(fileSelector);

		List<String> includeFilenames = new ArrayList<String>();

		for (FileObject file: files)
		{
			includeFilenames.add(file.getName().getBaseName());
		}

		super.setIncludes(StringUtils.join(includeFilenames, ','));
	}

	@Override
	public void execute()
	{
		System.out.println("executing...");
		log.info("executing...");
		super.execute();
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
