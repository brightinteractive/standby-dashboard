package com.brightinteractive.standbydashboard.sync.service;

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
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public class FileSyncTaskTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private File sourceDirectory;
	private File destinationDirectory;

	@Before
	public void setUp() throws Exception
	{		
		sourceDirectory = folder.newFolder("source");
		destinationDirectory  = folder.newFolder("destination");
	}
	
	
	@Test
	public void shouldSyncAllFiles() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		FileSyncTask syncTask = createSyncTask();
		syncTask.setIncludeList("lvl0");

		syncTask.execute();

		assertEquals(dirToString(sourceDirectory), dirToString(destinationDirectory));		
	}
	
	@Test
	public void shouldSyncAllFilesInSourceFolderWithoutExplicitIncludesOrExcludes() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		FileSyncTask syncTask = createSyncTask();		

		syncTask.execute();

		assertEquals(dirToString(sourceDirectory), dirToString(destinationDirectory));
	}
	
	@Test
	public void shouldSyncAllFilesInSourceFolderWhenIncludesAndExcludesAreEmpty() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		FileSyncTask syncTask = createSyncTask();
		syncTask.setIncludeList("");
		syncTask.setExcludeList("");

		syncTask.execute();

		assertEquals(dirToString(sourceDirectory), dirToString(destinationDirectory));
	}

	@Test
	public void shouldIncludeOnlySpecifiedFilesAndFolders() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		File ignored = new File(sourceDirectory, "ignored");
		ignored.mkdir();
		createNestedDirs(ignored, "ig", 0, 2);
		FileSyncTask syncTask = createSyncTask();
		createTextFile(sourceDirectory, "test.txt", "test");
		
		
		syncTask.setIncludeList("test.txt,lvl0");
		syncTask.execute();

		assertEquals("ig0.txt(ig-0-text);ig1.txt(ig-1-text);ig2.txt(ig-2-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);test.txt(test);", dirToString(sourceDirectory));
		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);test.txt(test);", dirToString(destinationDirectory));
	}

	@Test
	public void shouldIgnoreMissingSource() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		File ignored = new File(destinationDirectory, "ignored");
		ignored.mkdir();
		createNestedDirs(ignored, "ig", 0, 1);
		FileSyncTask syncTask = createSyncTask();
		syncTask.setIncludeList("lvl0");
		syncTask.setDeleteMissingSourceFiles(false);

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceDirectory));
		assertEquals("ig0.txt(ig-0-text);ig1.txt(ig-1-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationDirectory));
	}

	@Test
	public void shouldDeleteMissingSource() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		File ignored = new File(destinationDirectory, "ignored");
		ignored.mkdir();
		createNestedDirs(ignored, "ig", 0, 1);
		FileSyncTask syncTask = createSyncTask();
		syncTask.setIncludeList("lvl0");
		syncTask.setDeleteMissingSourceFiles(true);

		syncTask.execute();

		assertEquals(dirToString(sourceDirectory), dirToString(destinationDirectory));
	}

	@Test
	public void shouldDeleteMissingSourceExceptIgnored() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		File deleted = new File(destinationDirectory, "delete-me");
		deleted.mkdir();
		createNestedDirs(deleted, "del", 0, 1);
		createTextFile(destinationDirectory, "leave-me", "leave-me-text");
		createTextFile(destinationDirectory, "and-me", "and-me-text");

		FileSyncTask syncTask = createSyncTask();
		syncTask.setIncludeList("lvl0");
		syncTask.setDeleteMissingSourceFiles(true);
		syncTask.setIgnoreMissingSource("leave-me,and-me");

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceDirectory));
		assertEquals("and-me(and-me-text);leave-me(leave-me-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationDirectory));
	}

	@Test
	public void shouldDeleteMissingSourceExceptIgnoredUsingRegexp() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		File deleted = new File(destinationDirectory, "delete-me");
		deleted.mkdir();
		createNestedDirs(deleted, "del", 0, 1);
		File ignored = new File(destinationDirectory, "leave-me");
		ignored.createNewFile();
		FileUtils.writeStringToFile(ignored, "leave-me-text");

		FileSyncTask syncTask = createSyncTask();
		syncTask.setIncludeList("lvl0");
		syncTask.setDeleteMissingSourceFiles(true);
		syncTask.setIgnoreMissingSource("leave-.+");

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceDirectory));
		assertEquals("leave-me(leave-me-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationDirectory));
	}

	@Test
	public void shouldDeleteMissingSourceExceptIgnoredUsingMoreThanOneRegexp() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		File deleted = new File(destinationDirectory, "delete-me");
		deleted.mkdir();
		createNestedDirs(deleted, "del", 0, 1);
		File ignored = new File(destinationDirectory, "leave-me");
		ignored.createNewFile();
		FileUtils.writeStringToFile(ignored, "leave-me-text");

		ignored = new File(destinationDirectory, "also-leave-me");
		ignored.createNewFile();
		FileUtils.writeStringToFile(ignored, "also-leave-me-text");

		FileSyncTask syncTask = createSyncTask();
		syncTask.setIncludeList("lvl0");
		syncTask.setDeleteMissingSourceFiles(true);
		syncTask.setIgnoreMissingSource("leave-.+,also-leave-.+");

		syncTask.execute();

		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceDirectory));
		assertEquals("also-leave-me(also-leave-me-text);leave-me(leave-me-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationDirectory));
	}

	@Test
	public void shouldNotSyncExcludeTopLevelFolder() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		File excluded = new File(sourceDirectory, "excluded");

		excluded.mkdir();

		createNestedDirs(excluded, "ex", 0, 1);
		FileSyncTask syncTask = createSyncTask();

		syncTask.setExcludeList("excluded");
		syncTask.setDeleteMissingSourceFiles(true);

		syncTask.execute();

		assertEquals("ex0.txt(ex-0-text);ex1.txt(ex-1-text);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceDirectory));
		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationDirectory));
	}
	
	@Test
	public void shouldNotSyncExcludeTopLevelFile() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);
		createTextFile(sourceDirectory, "excluded.txt", "excluded");
				
		
		FileSyncTask syncTask = createSyncTask();

		syncTask.setExcludeList("excluded.txt");
		syncTask.setDeleteMissingSourceFiles(true);

		syncTask.execute();

		assertEquals("excluded.txt(excluded);lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(sourceDirectory));
		assertEquals("lvl0.txt(lvl-0-text);lvl1.txt(lvl-1-text);lvl2.txt(lvl-2-text);lvl3.txt(lvl-3-text);", dirToString(destinationDirectory));
	}

	@Test
	public void syncInitialisesFileListOnExecuteIfExcludesDetected() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);

		FileSyncTask syncTask = PowerMockito.spy(createSyncTask());
		syncTask.setExcludeList("excluded");
		syncTask.setDeleteMissingSourceFiles(true);
		syncTask.execute();

		Mockito.verify(syncTask).setIncludesFromExcludes();
	}

	@Test
	public void syncDoesNotInitialisesFileListOnExecuteIfNoExcludesDetected() throws IOException
	{
		createNestedDirs(sourceDirectory, "lvl", 0, 3);

		FileSyncTask syncTask = PowerMockito.spy(createSyncTask());
		syncTask.setIncludes("lvl0");
		syncTask.setDeleteMissingSourceFiles(true);
		syncTask.execute();

		Mockito.verify(syncTask, Mockito.never()).setIncludesFromExcludes();
	}

	private FileSyncTask createSyncTask()
	{
		FileSyncTask syncTask = new FileSyncTask();
		syncTask.setSourceDirectory(sourceDirectory.getAbsolutePath());
		syncTask.setDestinationDirectory(destinationDirectory.getAbsolutePath());
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
}