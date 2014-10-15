package com.brightinteractive.standbydashboard;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

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
		createTextFile(levelDir, prefix + current + ".txt", prefix + "-" + current + "-text");
		if (current == max)
		{
			return levelDir;
		}
		else
		{
			return createNestedDirs(levelDir, prefix, ++current, max);
		}
	}

	private File createTextFile(File parent, String filename, String content) throws IOException
	{
		File file = new File(parent, filename);
		FileUtils.writeStringToFile(file, content);
		return file;
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
		createTextFile(destinationFile, "leave-me", "leave-me-text");
		createTextFile(destinationFile, "and-me", "and-me-text");

		SyncTaskImpl syncTask = createSyncTask();
		syncTask.setIncludes("lvl0");
		syncTask.setDeleteMissingSourceFiles(true);
		syncTask.setIgnoreMissingSource("leave-me,and-me");

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceFile));
		assertEquals("and-me(and-me-text);leave-me(leave-me-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationFile));
	}

	@Test
	public void shouldDeleteMissingSourceExceptIgnoredUsingRegexp() throws IOException
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
		syncTask.setIgnoreMissingSource("leave-.+");

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceFile));
		assertEquals("leave-me(leave-me-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationFile));
	}

	@Test
	public void shouldDeleteMissingSourceExceptIgnoredUsingMoreThanOneRegexp() throws IOException
	{
		createNestedDirs(sourceFile, "lvl", 0, 3);
		File deleted = new File(destinationFile, "delete-me");
		deleted.mkdir();
		createNestedDirs(deleted, "del", 0, 1);
		File ignored = new File(destinationFile, "leave-me");
		ignored.createNewFile();
		FileUtils.writeStringToFile(ignored, "leave-me-text");

		ignored = new File(destinationFile, "also-leave-me");
		ignored.createNewFile();
		FileUtils.writeStringToFile(ignored, "also-leave-me-text");

		SyncTaskImpl syncTask = createSyncTask();
		syncTask.setIncludes("lvl0");
		syncTask.setDeleteMissingSourceFiles(true);
		syncTask.setIgnoreMissingSource("leave-.+,also-leave-.+");

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceFile));
		assertEquals("also-leave-me(also-leave-me-text);leave-me(leave-me-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationFile));
	}

	@Test
	public void shouldNotSyncExcludeFolder() throws IOException
	{
		createNestedDirs(sourceFile, "lvl", 0, 3);
		File excluded = new File(sourceFile, "excluded");

		excluded.mkdir();

		createNestedDirs(excluded, "ex", 0, 1);
		SyncTaskImpl syncTask = createSyncTask();

		syncTask.setExcludes("excluded");
		syncTask.setDeleteMissingSourceFiles(true);

		syncTask.execute();

		assertEquals("ex0.txt(ex-0-text);ex1.txt(ex-1-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceFile));
		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationFile));
	}

	@Test
	public void syncInitialisesFileListOnExecuteIfExcludesDetected() throws IOException
	{
		createNestedDirs(sourceFile, "lvl", 0, 3);

		SyncTaskImpl syncTask = PowerMockito.spy(createSyncTask());
		syncTask.setExcludes("excluded");
		syncTask.setDeleteMissingSourceFiles(true);
		syncTask.execute();

		Mockito.verify(syncTask).setIncludesBasedOnExcludes();
	}

	@Test
	public void syncDoesNotInitialisesFileListOnExecuteIfNoExcludesDetected() throws IOException
	{
		createNestedDirs(sourceFile, "lvl", 0, 3);

		SyncTaskImpl syncTask = PowerMockito.spy(createSyncTask());
		syncTask.setIncludes("lvl0");
		syncTask.setDeleteMissingSourceFiles(true);
		syncTask.execute();

		Mockito.verify(syncTask, Mockito.never()).setIncludesBasedOnExcludes();
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
		List<String> fileInfoList = new ArrayList<String>();

		while (iter.hasNext())
		{
			File file = iter.next();
			fileInfoList.add(file.getName() +
							 "(" + FileUtils.readFileToString(file) + ")" +
							 ";");
		}
		Collections.sort(fileInfoList);
		return StringUtils.join(fileInfoList, "");
	}
}