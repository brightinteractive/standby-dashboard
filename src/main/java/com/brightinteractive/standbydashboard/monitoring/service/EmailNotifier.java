package com.brightinteractive.standbydashboard.monitoring.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.brightinteractive.mail.templating.VelocityTemplates;
import org.apache.log4j.Logger;

@Component
public class EmailNotifier implements Notifier
{
	private Logger log = Logger.getLogger(this.getClass());

	@Value("${standby.name}")
	String standbyName;

	@Value("${monitor.notify.email.from}")
	String monitorNotifyEmailFrom;

	@Value("${monitor.notify.email.to}")
	String monitorNotifyEmailTo;

	@Autowired
	MailSender mailSender;

	@Autowired
	VelocityTemplates velocityTemplates;

	@Override
	public void monitoringError(Monitor monitor, Throwable throwable)
	{
		ModelMap model = getCommonModelMap(monitor);
		model.addAttribute("throwable", throwable);

		SimpleMailMessage message = new SimpleMailMessage(getMonitorMessageTemplate());
		message.setSubject(String.format("Monitoring Error : %s", standbyName));
		message.setText(velocityTemplates.merge(model, "monitor-error.vm"));

		log.info("Sending monitoring error email");
		mailSender.send(message);
	}

	@Override
	public void monitoringAlert(Monitor monitor)
	{
		SimpleMailMessage message = new SimpleMailMessage(getMonitorMessageTemplate());
		message.setSubject(String.format("Monitoring Alert : %s", standbyName));
		message.setText(velocityTemplates.merge(getCommonModelMap(monitor), "monitor-alert.vm"));
		
		log.info("Sending monitoring alert email");
		mailSender.send(message);
	}

	@Override
	public void monitoringCleared(Monitor monitor)
	{
		SimpleMailMessage message = new SimpleMailMessage(getMonitorMessageTemplate());
		message.setSubject(String.format("Monitoring Alert Cleared : %s", standbyName));
		message.setText(velocityTemplates.merge(getCommonModelMap(monitor), "monitor-alert-cleared.vm"));
		
		log.info("Sending monitoring alert cleared email");
		mailSender.send(message);

	}

	private SimpleMailMessage getMonitorMessageTemplate()
	{
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(monitorNotifyEmailFrom);
		message.setTo(monitorNotifyEmailTo);

		return message;
	}

	private ModelMap getCommonModelMap(Monitor monitor)
	{
		ModelMap model = new ModelMap();
		model.addAttribute("monitor", monitor);
		model.addAttribute("standbyName", standbyName);
		return model;
	}
}
