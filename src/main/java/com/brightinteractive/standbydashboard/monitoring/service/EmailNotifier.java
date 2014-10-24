package com.brightinteractive.standbydashboard.monitoring.service;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.brightinteractive.mail.service.Emailer;
import org.apache.log4j.Logger;

@Component
public class EmailNotifier implements Notifier
{
	private Logger log = Logger.getLogger(this.getClass());

	@Value("${standby.name}")
	private String standbyName;
	
	@Autowired
	Emailer emailer;
	
	@Autowired
	@Qualifier("monitorMessageTemplate")
	private SimpleMailMessage monitorMessageTemplate;

	@Override
	public void monitoringError(Monitor monitor, Throwable throwable)
	{
		ModelMap model = getCommonModelMap(monitor);
		model.addAttribute("throwable", throwable);
		
		SimpleMailMessage message = new SimpleMailMessage(monitorMessageTemplate);
		message.setSubject(String.format("Monitoring Error : %s", standbyName));
		
		emailer.sendSimpleMailMessage(message,
									  model,
									  "monitor-error.vm");
	}

	@Override
	public void monitoringAlert(Monitor monitor)
	{
		ModelMap model = getCommonModelMap(monitor);		
		
		SimpleMailMessage message = new SimpleMailMessage(monitorMessageTemplate);
		message.setSubject(String.format("Monitoring Alert : %s", standbyName));
		
		emailer.sendSimpleMailMessage(message,
									  model,
									  "monitor-alert.vm");
	}

	private ModelMap getCommonModelMap(Monitor monitor)
	{
		ModelMap model = new ModelMap();
		model.addAttribute("monitor", monitor);
		model.addAttribute("standbyName", standbyName);
		return model;
	}
}
