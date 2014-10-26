package com.brightinteractive.standbydashboard.monitoring.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import com.brightinteractive.mail.templating.VelocityTemplates;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class EmailNotifierTest
{
	public static final String TEST_STANDBY_NAME = "Test Standby";
	EmailNotifier emailNotifier;
	@Mock
	private MailSender mailSender;
	@Mock
	private VelocityTemplates velocityTemplates;
	@Mock
	Monitor monitor;
	
	@Captor
	ArgumentCaptor<SimpleMailMessage> messageArgumentCaptor;
	
	@Captor
	ArgumentCaptor<Map> velocityMapArgumentCaptor;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		emailNotifier = new EmailNotifier();
		emailNotifier.mailSender =  mailSender;
		emailNotifier.velocityTemplates = velocityTemplates;
		emailNotifier.standbyName = TEST_STANDBY_NAME;
		
		when(velocityTemplates.merge(any(Map.class), anyString())).thenReturn("Some merged text");
	}

	@Test
	public void testMonitoringErrorSendsEmail() throws Exception
	{
		Exception exception = new Exception("Error");
		emailNotifier.monitoringError(monitor, exception);
		
		verify(mailSender).send(any(SimpleMailMessage.class));
	}
	
	@Test
	public void testMonitoringErrorEmailSubjectContainStandbyName() throws Exception
	{
		emailNotifier.monitoringError(monitor, new Exception("Error"));
		
		verify(mailSender).send(messageArgumentCaptor.capture());
		assertTrue(messageArgumentCaptor.getValue().getSubject().contains(TEST_STANDBY_NAME));
	}
	
	@Test
	public void testMonitoringErrorTextCanContainMonitor() throws Exception
	{
		emailNotifier.monitoringError(monitor, new Exception("Error"));
		
		verify(velocityTemplates).merge(velocityMapArgumentCaptor.capture(),anyString());
		assertTrue(velocityMapArgumentCaptor.getValue().containsValue(monitor));
	}
	
	@Test
	public void testMonitoringErrorTextCanContainThrowable() throws Exception
	{
		Exception exception = new Exception("Error");
		emailNotifier.monitoringError(monitor, exception);
		
		verify(velocityTemplates).merge(velocityMapArgumentCaptor.capture(),anyString());
		assertTrue(velocityMapArgumentCaptor.getValue().containsValue(exception));
	}

	@Test
	public void testMonitoringAlertSendEmail() throws Exception
	{		
		emailNotifier.monitoringAlert(monitor);
		
		verify(mailSender).send(any(SimpleMailMessage.class));
	}
	
	@Test
	public void testMonitoringAlertEmailSubjectContainStandbyName() throws Exception
	{		
		emailNotifier.monitoringAlert(monitor);
		
		verify(mailSender).send(messageArgumentCaptor.capture());
		assertTrue(messageArgumentCaptor.getValue().getSubject().contains(TEST_STANDBY_NAME));
	}
	
	@Test
	public void testMonitoringAlertTextCanContainMonitor() throws Exception
	{
		emailNotifier.monitoringAlert(monitor);
		
		verify(velocityTemplates).merge(velocityMapArgumentCaptor.capture(),anyString());
		assertTrue(velocityMapArgumentCaptor.getValue().containsValue(monitor));
	}
}