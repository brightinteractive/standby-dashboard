package com.brightinteractive.mail.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.springframework.mail.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class SilentMailSenderTest
{
	SilentMailSender silentMailSender;
	
	@Mock
	MailSender mailSender;
	@Mock
	SimpleMailMessage simpleMailMessage;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		
		silentMailSender = new SilentMailSender(mailSender);
		doThrow(new MailSendException("Cannot send email")).when(mailSender).send(any(SimpleMailMessage.class));
		doThrow(new MailSendException("Cannot send email")).when(mailSender).send(any(SimpleMailMessage[].class));
		
	}

	@Test
	public void testSendSingleFailsSilently()
	{
		silentMailSender.send(simpleMailMessage);
	}

	@Test
	public void testSendMultipleFailsSilently()
	{
		silentMailSender.send(new SimpleMailMessage[]{simpleMailMessage});
	}
}