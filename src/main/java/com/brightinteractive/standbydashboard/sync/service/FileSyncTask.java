package com.brightinteractive.standbydashboard.sync.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.tasks.SyncTask;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FileSyncTask extends SyncTask
{
	private Logger log = Logger.getLogger(this.getClass());

	private Project project = new Project();

	private String destinationDirectory;
	private String sourceDirectory;
	private String includes;
	private String excludes;

	protected boolean deleteMissingSourceFiles = false;
	private String[] ignoreMissingSource;


	public FileSyncTask()
	{
		super.setProject(project);
		super.setSrcDirIsBase(true);
	}

	@Value("${fileSync.destination.directory}")
	public void setDestinationDirectory(String destinationDirectory)
	{
		super.setDestDir(destinationDirectory);
		this.destinationDirectory = destinationDirectory;
	}

	@Value("${fileSync.source.directory}")
	public void setSourceDirectory(String sourceDirectory)
	{
		super.setSrcDir(sourceDirectory);
		this.sourceDirectory = sourceDirectory;
	}

	@Value("${fileSync.destination.deleteMissing}")
	public void setDeleteMissingSourceFiles(boolean deleteMissingSourceFiles)
	{
		this.deleteMissingSourceFiles = deleteMissingSourceFiles;
	}

	@Value("${fileSync.destination.ignoreMissing}")
	public void setIgnoreMissingSource(String ignoreMissingSource)
	{
		this.ignoreMissingSource = ignoreMissingSource.split(",");
	}

	@Value("${fileSync.source.includes}")
	public void setIncludeList(String includes)
	{
		this.includes = includes;
	}

	@Value("${fileSync.source.excludes}")
	public void setExcludeList(String excludes)
	{
		this.excludes = excludes;
	}

	@Override
	public void execute()
	{
		try
		{
			setIncludedFiles();
		}
		catch (FileSystemException e)
		{
			log.error("Could not initialise the include file list", e);
		}

		log.info("Sync starting...");
		super.execute();
		log.info("Sync complete");
	}

	private void setIncludedFiles() throws FileSystemException
	{
		if (includesSpecified())
		{
			setIncludes(includes);
		}
		else if (excludesSpecified())
		{
			setIncludesFromExcludes();
		}
		else
		{
			setIncludeAllFiles();
		}
	}

	private void setIncludeAllFiles() throws FileSystemException
	{
		setIncludesWithFileSelector(new TopLevelAllFileSelector(sourceDirectory));
	}

	private boolean excludesSpecified()
	{
		return !StringUtils.isEmpty(excludes);
	}

	private boolean includesSpecified()
	{
		return !StringUtils.isEmpty(includes);
	}

	public void setIncludesFromExcludes() throws FileSystemException
	{
		setIncludesWithFileSelector(new TopLevelExcludingFileSelector(excludes));
	}

	private void setIncludesWithFileSelector(FileSelector fileSelector) throws FileSystemException
	{
		List<String> includeFilenames = new ArrayList<String>();

		for (FileObject file : resolveFile(sourceDirectory).findFiles(fileSelector))
		{
			includeFilenames.add(file.getName().getBaseName());
		}

		super.setIncludes(StringUtils.join(includeFilenames, ','));
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
	protected void handleMissingSourceFile(final FileObject destFile) throws Exception
	{
		log.info(destFile.getURL() + " found in destination but not in source.");

		if (isIgnored(destFile))
		{
			log.info(destFile.getURL() + " was not deleted.");
			return;
		}
		log.info(destFile.getURL() + " was deleted.");

		destFile.delete(Selectors.SELECT_SELF);
	}

	private boolean isIgnored(final FileObject destFile)
	{
		if (ignoreMissingSource != null)
		{
			for (String ignore : ignoreMissingSource)
			{
				if (ignore != null && destFile.getName().getBaseName().matches(ignore))
				{
					return true;
				}
			}
		}
		return false;
	}
}
