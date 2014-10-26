package com.brightinteractive.mail.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import org.springframework.mail.*;

import org.apache.log4j.Logger;

public class SilentMailSender implements MailSender
{
	private Logger log = Logger.getLogger(this.getClass());
	private MailSender mailSender;

	public SilentMailSender(MailSender mailSender)
	{
		this.mailSender = mailSender;
	}

	@Override
	public void send(SimpleMailMessage simpleMailMessage)
	{
		try
		{
			mailSender.send(simpleMailMessage);
		}
		catch (MailException ex)
		{
			log.error("Error sending message", ex);
		}
	}

	@Override
	public void send(SimpleMailMessage[] simpleMailMessages)
	{
		try
		{
			mailSender.send(simpleMailMessages);
		}
		catch (MailException ex)
		{
			log.error("Error sending messages", ex);
		}
	}
}
