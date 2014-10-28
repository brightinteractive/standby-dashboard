package com.brightinteractive.standbydashboard.monitoring.service;

import static org.mockito.Mockito.*;

import com.brightinteractive.standbydashboard.monitoring.exception.MonitoringException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ScheduledMonitorTest
{
	@Mock
	Monitor monitor;
	@Mock
	Notifier notifier;
	ScheduledMonitor scheduledMonitor = new ScheduledMonitor();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		scheduledMonitor.monitor = monitor;
		scheduledMonitor.notifier = notifier;
	}

	@Test
	public void testStartupResetMonitorCalledWhenMonitorHasNotRunBefore() throws Exception
	{
		when(monitor.hasRunBefore()).thenReturn(true);
		scheduledMonitor.startup();

		verify(monitor, never()).reset();
	}

	@Test
	public void testStartupResetMonitorNotCalledWhenMonitorHasRunBefore() throws Exception
	{
		scheduledMonitor.startup();

		verify(monitor).reset();
	}

	@Test
	public void testRunMonitorResetIsCalledIfMonitorIsSuccessful() throws Exception
	{
		when(monitor.checkIsSuccessful()).thenReturn(true);

		scheduledMonitor.runMonitor();

		verify(monitor).reset();
	}

	@Test
	public void testRunMonitorNotifierMonitoringAlertIsCalledIfMonitorIsNotSuccessfulAndShouldAlert() throws Exception
	{
		when(monitor.checkIsSuccessful()).thenReturn(false);
		when(monitor.shouldAlert()).thenReturn(true);

		scheduledMonitor.runMonitor();

		verify(notifier).monitoringAlert(monitor);
	}

	@Test
	public void testRunMonitorNotifierMonitoringAlertIsNotCalledIfMonitorIsNotSuccessfulAndShouldNotAlert() throws Exception
	{
		when(monitor.checkIsSuccessful()).thenReturn(false);
		when(monitor.shouldAlert()).thenReturn(false);

		scheduledMonitor.runMonitor();

		verify(notifier, never()).monitoringAlert(monitor);
	}

	@Test
	public void testRunMonitorMonitorSetFailedIsCalledIfMonitorIsNotSuccessfulAndShouldAlert() throws Exception
	{
		when(monitor.checkIsSuccessful()).thenReturn(false);
		when(monitor.shouldAlert()).thenReturn(true);

		scheduledMonitor.runMonitor();

		verify(monitor).setFailed();
	}

	@Test
	public void testRunMonitorMonitorSetFailedIsNotCalledIfMonitorIsNotSuccessfulAndShouldNotAlert() throws Exception
	{
		when(monitor.checkIsSuccessful()).thenReturn(false);
		when(monitor.shouldAlert()).thenReturn(false);

		scheduledMonitor.runMonitor();

		verify(monitor, never()).setFailed();
	}

	@Test
	public void testRunMonitorNotifierMonitoringErrorIsCalledIfMonitoringExceptionIsThrown()
	{
		MonitoringException exception = new MonitoringException();
		when(monitor.checkIsSuccessful()).thenThrow(exception);

		scheduledMonitor.runMonitor();

		verify(notifier).monitoringError(monitor, exception);
	}

	@Test
	public void testRunMonitorMonitoringClearedIsCalledIfMonitorIsSuccessfulAndFailedPreviously() throws Exception
	{
		when(monitor.checkIsSuccessful()).thenReturn(true);
		when(monitor.failedPreviously()).thenReturn(true);

		scheduledMonitor.runMonitor();

		verify(notifier).monitoringCleared(monitor);
	}

	@Test
	public void testRunMonitorMonitoringClearedIsNotCalledIfMonitorIsSuccessfulButNotFailedPreviously() throws Exception
	{
		when(monitor.checkIsSuccessful()).thenReturn(true);
		when(monitor.failedPreviously()).thenReturn(false);

		scheduledMonitor.runMonitor();

		verify(notifier, never()).monitoringCleared(monitor);
	}

	@Test
	public void testRunMonitorMonitoringAlertClearedIsCalledIfMonitorIsSuccessfulAndFailedPreviously() throws Exception
	{
		when(monitor.checkIsSuccessful()).thenReturn(true);
		when(monitor.failedPreviously()).thenReturn(true);

		scheduledMonitor.runMonitor();

		verify(monitor).clearAlert();
	}

	@Test
	public void testRunMonitorMonitoringAlertClearedIsNotCalledIfMonitorIsSuccessfulButFailedPreviously() throws Exception
	{
		when(monitor.checkIsSuccessful()).thenReturn(true);
		when(monitor.failedPreviously()).thenReturn(false);

		scheduledMonitor.runMonitor();

		verify(monitor, never()).clearAlert();
	}

}