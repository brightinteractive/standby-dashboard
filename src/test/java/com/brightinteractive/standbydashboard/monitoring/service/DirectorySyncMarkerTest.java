package com.brightinteractive.standbydashboard.monitoring.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

public class DirectorySyncMarkerTest
{
	public static final int DEFAULT_MINUTES_AFTER_LAST_CONFIRMED_SYNC_TO_ALERT = 5;
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	DirectorySyncMarker directorySyncMarker;
	File sourceDirectory;
	File destinationDirectory;

	@Before
	public void setUp()
	{
		sourceDirectory = folder.newFolder("source");
		destinationDirectory = folder.newFolder("destination");

		directorySyncMarker = new DirectorySyncMarker(sourceDirectory.getPath(),
													  destinationDirectory.getPath(),
													  DEFAULT_MINUTES_AFTER_LAST_CONFIRMED_SYNC_TO_ALERT);
	}


	@Test
	public void testWriteSourceMarkerCreatesRecognisableFile() throws Exception
	{
		directorySyncMarker.writeSourceMarker();

		assertTrue(getFirstFileInSourceDir().exists());
	}
	
	@Test
	public void testSourceMarkerExists() throws Exception
	{
		directorySyncMarker.writeSourceMarker();

		assertTrue(directorySyncMarker.sourceMarkerExists());
	}

	@Test
	public void testWriteSourceMarkerContentDiffersBetweenWrites() throws Exception
	{
		directorySyncMarker.writeSourceMarker();
		String writeOne = FileUtils.readFileToString(getFirstFileInSourceDir());
		Thread.sleep(1);
		directorySyncMarker.writeSourceMarker();
		String writeTwo = FileUtils.readFileToString(getFirstFileInSourceDir());

		assertFalse(writeOne.equals(writeTwo));
	}

	@Test
	public void testMarkersMatchIsTrueOnSuccessfulSync() throws Exception
	{
		directorySyncMarker.writeSourceMarker();
		simulateSuccessfulSync();

		assertTrue(directorySyncMarker.markersMatch());
	}

	@Test
	public void testMarkersMatchIsFalseOnFailedFirstSync() throws Exception
	{
		directorySyncMarker.writeSourceMarker();
		simulateSyncNotRunning();

		assertFalse(directorySyncMarker.markersMatch());
	}

	@Test
	public void testMarkersMatchIsFalseOnFailedSecondSync() throws Exception
	{
		directorySyncMarker.writeSourceMarker();
		simulateSuccessfulSync();
		Thread.sleep(1);
		directorySyncMarker.writeSourceMarker();
		simulateSyncNotRunning();

		assertFalse(directorySyncMarker.markersMatch());
	}

	@Test
	public void testMarkersMatchIsFalseOnCorruptedFirstSync() throws Exception
	{
		directorySyncMarker.writeSourceMarker();
		simulateCorruptedSync();

		assertFalse(directorySyncMarker.markersMatch());
	}

	@Test
	public void testMarkersMatchIsFalseOnCorruptedSecondSync() throws Exception
	{
		directorySyncMarker.writeSourceMarker();
		simulateSuccessfulSync();
		Thread.sleep(1);
		directorySyncMarker.writeSourceMarker();
		simulateCorruptedSync();

		assertFalse(directorySyncMarker.markersMatch());
	}
	
	@Test
	public void testShouldAlertWhenThresholdNotExceeded()  throws Exception
	{
		directorySyncMarker.writeSourceMarker();
		simulateSuccessfulSync();
		assertFalse(directorySyncMarker.shouldAlert());
	}
	
	@Test
	public void testShouldAlertWhenThresholdExceeded()  throws Exception
	{
		directorySyncMarker.writeSourceMarker();
		simulateSuccessfulSync();
		simulateSourceMarkerWrittenMinutesAgo(DEFAULT_MINUTES_AFTER_LAST_CONFIRMED_SYNC_TO_ALERT+1);
		
		assertTrue(directorySyncMarker.shouldAlert());
	}
	
	@Test
	public void testGetNameContainsSourceAndDestination()
	{
		assertTrue(directorySyncMarker.getName().contains(sourceDirectory.getAbsolutePath()));
		assertTrue(directorySyncMarker.getName().contains(destinationDirectory.getAbsolutePath()));
	}
	
	@Test
	public void testGetAlertMessageNotEmpty()
	{
		directorySyncMarker.writeSourceMarker();
		assertFalse(StringUtils.isEmpty(directorySyncMarker.getAlertMessage()));		
	}
	
	@Test
	public void testGetAlertClearedMessageNotEmpty()
	{
		directorySyncMarker.writeSourceMarker();
		assertFalse(StringUtils.isEmpty(directorySyncMarker.getAlertClearedMessage()));		
	}
	
	@Test
	public void testFailedPreviouslyFalseWhenNoFailure() throws IOException
	{
		directorySyncMarker.writeSourceMarker();
		simulateSuccessfulSync();
		directorySyncMarker.checkIsSuccessful();
		
		assertFalse(directorySyncMarker.failedPreviously());
	}
	
	@Test
	public void testFailedPreviouslyFalseWhenFailureThenAlertCleared() throws IOException
	{
		directorySyncMarker.writeSourceMarker();
		simulateSyncNotRunning();
		directorySyncMarker.checkIsSuccessful();
		assertTrue(directorySyncMarker.failedPreviously());
		simulateSuccessfulSync();
		directorySyncMarker.checkIsSuccessful();
		directorySyncMarker.clearAlert();		
		
		assertFalse(directorySyncMarker.failedPreviously());
	}
		
	@Test
	public void testFailedPreviouslyTrueWhenFailure() throws IOException
	{
		directorySyncMarker.writeSourceMarker();
		simulateSyncNotRunning();
		directorySyncMarker.checkIsSuccessful();
		assertTrue(directorySyncMarker.failedPreviously());
	}
		
	@Test
	public void testFailedPreviouslyTrueWhenFailureFollowedBySuccess() throws IOException
	{
		directorySyncMarker.writeSourceMarker();
		simulateSyncNotRunning();
		directorySyncMarker.checkIsSuccessful();
		assertTrue(directorySyncMarker.failedPreviously());
		simulateSuccessfulSync();
		directorySyncMarker.checkIsSuccessful();
		
		assertTrue(directorySyncMarker.failedPreviously());
	}
	

	private void simulateSourceMarkerWrittenMinutesAgo(int defaultMinutesAfterLastConfirmedSyncToAlert) throws Exception
	{		
		FileUtils.write(new File(sourceDirectory+File.separator+DirectorySyncMarker.SYNC_MARKER_FILE_NAME), 
						String.valueOf(new DateTime().minusMinutes(defaultMinutesAfterLastConfirmedSyncToAlert).toDate().getTime()));
	}


	private File getFirstFileInSourceDir()
	{
		Iterator<File> files = FileUtils.iterateFiles(sourceDirectory, FileFileFilter.FILE, null);
		return files.next();
	}

	private File getFirstFileInDestinationDir()
	{
		Iterator<File> files = FileUtils.iterateFiles(destinationDirectory, FileFileFilter.FILE, null);
		return files.next();
	}

	private void simulateSuccessfulSync() throws IOException
	{
		FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
	}

	private void simulateSyncNotRunning()
	{
	}

	private void simulateCorruptedSync() throws IOException
	{
		simulateSuccessfulSync();
		corruptMarkerInDestinationDir();
	}

	private void corruptMarkerInDestinationDir() throws IOException
	{
		File destinationMarkerFile = getFirstFileInDestinationDir();
		String markerContent = FileUtils.readFileToString(destinationMarkerFile);
		String corruptedContent = markerContent + RandomStringUtils.randomAlphabetic(5);
		FileUtils.write(destinationMarkerFile, corruptedContent);

	}
}