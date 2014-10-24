package com.brightinteractive.standbydashboard.application.config;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import com.brightinteractive.spring.utils.BrightConfigUtils;
import com.brightinteractive.standbydashboard.application.controller.SettingsBean;
import com.brightinteractive.standbydashboard.sync.service.FileSyncScheduler;
import com.brightinteractive.standbydashboard.sync.service.FileSyncTask;
import org.apache.velocity.app.VelocityEngine;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = { "com.brightinteractive" },
			   excludeFilters = @ComponentScan.Filter(value = Controller.class, type = FilterType.ANNOTATION))
public class AppConfig
{
	public static String APPLICATION_PROPERTIES_FILE = "ApplicationSettings";
	public static String APP_CONTEXT_NAME = "standby-dashboard";

	@Bean
	public static PropertyPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		PropertyPlaceholderConfigurer configurer = BrightConfigUtils
			.getStandardPropertyPlaceholderConfigurer(APPLICATION_PROPERTIES_FILE, APP_CONTEXT_NAME);
		configurer.setIgnoreUnresolvablePlaceholders(true);
		return configurer;
	}

//	@Bean(name = "scheduler")
//	public FileSyncScheduler getScheduler()
//	{
//		return new FileSyncScheduler();
//	}
//
//	@Bean(name = "settings")
//	public SettingsBean getSettings()
//	{
//		return new SettingsBean();
//	}
//
//	@Bean(name = "syncTask")
//	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
//	public FileSyncTask getSyncTask()
//	{
//		return new FileSyncTask();
//	}

	@Value("${smtp.host}")
	private String smtpHost;

	@Value("${smtp.port}")
	private int smtpPort;

	@Bean
	public MailSender getMailSender()
	{
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(smtpHost);
		mailSender.setPort(smtpPort);
		return mailSender;
	}

	@Value("${monitor.notify.email.from}")
	private String monitorNotifyEmailFrom;
	
	@Value("${monitor.notify.email.to}")
	private String monitorNotifyEmailTo;

	@Bean(name = "monitorMessageTemplate")
	public SimpleMailMessage getMonitorMessageTemplate()
	{
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(monitorNotifyEmailFrom);
		message.setTo(monitorNotifyEmailTo);

		return message;
	}

	@Bean
	public VelocityEngine getVelocityEngineFactoryBean() throws IOException
	{
		VelocityEngineFactoryBean factory = new VelocityEngineFactoryBean();
		Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        factory.setVelocityProperties(props);		

		return factory.createVelocityEngine();
	}
}
