package com.brightinteractive.mail.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.*;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;

@Component
public class Emailer
{
	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private MailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;

	public void sendSimpleMailMessage(SimpleMailMessage message,
									  Map model,
									  String templateName)
	{

		message.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
																	"email-templates/"+templateName,
																	"UTF-8",
																	model));
		try
		{
			mailSender.send(message);
		}
		catch (MailException ex)
		{
			log.error("Error sending message", ex);
		}
	}
}
