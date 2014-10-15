package com.brightinteractive.standbydashboard;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Test;

public class SyncTaskImplTest
{

	private File sourceFile;
	private File destinationFile;

	@org.junit.Before
	public void setUp() throws Exception
	{
		File rootDir = FileUtils.getTempDirectory();
		sourceFile = new File(rootDir, "source");
		sourceFile.mkdir();

		destinationFile = new File(rootDir, "dest");
		destinationFile.mkdir();
	}

	private File createNestedDirs(File parentDir, String prefix, int current, int max) throws IOException
	{
		File levelDir = new File(parentDir, prefix + current);
		levelDir.mkdir();
		File levelFile = new File(levelDir, prefix + current + ".txt");
		FileUtils.writeStringToFile(levelFile, prefix +
											   "-" + current + "-text");
		if (current == max)
		{
			return levelDir;
		}
		else
		{
			return createNestedDirs(levelDir, prefix, ++current, max);
		}
	}

	@org.junit.After
	public void tearDown() throws Exception
	{
		FileUtils.deleteQuietly(sourceFile);
		FileUtils.deleteQuietly(destinationFile);
	}

	@Test
	public void shouldSyncAllFiles() throws IOException
	{
		createNestedDirs(sourceFile, "lvl", 0, 3);
		SyncTaskImpl syncTask = createSyncTask();
		syncTask.setIncludes("lvl0");

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceFile));
		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationFile));
	}

	@Test
	public void shouldIncludeOnlySpecifiedFiles() throws IOException
	{
		createNestedDirs(sourceFile, "lvl", 0, 3);
		File ignored = new File(sourceFile, "ignored");
		ignored.mkdir();
		createNestedDirs(ignored, "ig", 0, 2);
		SyncTaskImpl syncTask = createSyncTask();
		syncTask.setIncludes("lvl0");

		syncTask.execute();

		assertEquals("ig0.txt(ig-0-text);ig1.txt(ig-1-text);ig2.txt(ig-2-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceFile));
		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationFile));
	}

	@Test
	public void shouldIgnoreMissingSource() throws IOException
	{
		createNestedDirs(sourceFile, "lvl", 0, 3);
		File ignored = new File(destinationFile, "ignored");
		ignored.mkdir();
		createNestedDirs(ignored, "ig", 0, 1);
		SyncTaskImpl syncTask = createSyncTask();
		syncTask.setIncludes("lvl0");
		syncTask.setDeleteMissingSourceFiles(false);

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceFile));
		assertEquals("ig0.txt(ig-0-text);ig1.txt(ig-1-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationFile));
	}

	@Test
	public void shouldDeleteMissingSource() throws IOException
	{
		createNestedDirs(sourceFile, "lvl", 0, 3);
		File ignored = new File(destinationFile, "ignored");
		ignored.mkdir();
		createNestedDirs(ignored, "ig", 0, 1);
		SyncTaskImpl syncTask = createSyncTask();
		syncTask.setIncludes("lvl0");
		syncTask.setDeleteMissingSourceFiles(true);

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceFile));
		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationFile));
	}

	@Test
	public void shouldDeleteMissingSourceExceptIgnored() throws IOException
	{
		createNestedDirs(sourceFile, "lvl", 0, 3);
		File deleted = new File(destinationFile, "delete-me");
		deleted.mkdir();
		createNestedDirs(deleted, "del", 0, 1);
		File ignored = new File(destinationFile, "leave-me");
		ignored.createNewFile();
		FileUtils.writeStringToFile(ignored, "leave-me-text");

		SyncTaskImpl syncTask = createSyncTask();
		syncTask.setIncludes("lvl0");
		syncTask.setDeleteMissingSourceFiles(true);
		syncTask.setIgnoreMissingSource("leave-me");

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceFile));
		assertEquals("leave-me(leave-me-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationFile));
	}

	private SyncTaskImpl createSyncTask()
	{
		SyncTaskImpl syncTask = new SyncTaskImpl();
		syncTask.setSource(sourceFile.getAbsolutePath());
		syncTask.setDestination(destinationFile.getAbsolutePath());
		return syncTask;
	}

	private String dirToString(File dir) throws IOException
	{
		Iterator<File> iter = FileUtils.iterateFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		StringBuffer buffer = new StringBuffer();
		while (iter.hasNext())
		{
			File file = iter.next();
			buffer.append(file.getName() +
						  "(" + FileUtils.readFileToString(file) + ")" +
						  ";");
		}
		return buffer.toString();
	}
}