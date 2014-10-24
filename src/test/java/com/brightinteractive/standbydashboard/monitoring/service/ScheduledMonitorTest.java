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
	public void testStartupResetMonitorCalled() throws Exception
	{
		scheduledMonitor.startup();

		verify(monitor).reset();
	}

	@Test
	public void testRunMonitorResetIsCalledIfMonitorIsSuccessful() throws Exception
	{
		when(monitor.isSuccessful()).thenReturn(true);

		scheduledMonitor.runMonitor();

		verify(monitor).reset();
	}

	@Test
	public void testRunMonitorNotifierMonitoringAlertIsCalledIfMonitorIsNotSuccessfulAndShouldAlert() throws Exception
	{
		when(monitor.isSuccessful()).thenReturn(false);
		when(monitor.shouldAlert()).thenReturn(true);

		scheduledMonitor.runMonitor();

		verify(notifier).monitoringAlert(monitor);
	}

	@Test
	public void testRunMonitorNotifierMonitoringAlertIsNotCalledIfMonitorIsNotSuccessfulAndShouldNotAlert() throws Exception
	{
		when(monitor.isSuccessful()).thenReturn(false);
		when(monitor.shouldAlert()).thenReturn(false);

		scheduledMonitor.runMonitor();

		verify(notifier, never()).monitoringAlert(monitor);
	}

	@Test
	public void testRunMonitorNotifierMonitoringErrorIsCalledIfMonitoringExcpetionIsThrown()
	{
		MonitoringException exception = new MonitoringException();
		when(monitor.isSuccessful()).thenThrow(exception);

		scheduledMonitor.runMonitor();

		verify(notifier).monitoringError(monitor, exception);
	}


}