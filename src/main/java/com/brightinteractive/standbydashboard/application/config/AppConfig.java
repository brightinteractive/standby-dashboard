package com.brightinteractive.standbydashboard.application.config;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

import com.brightinteractive.mail.config.MailConfig;
import com.brightinteractive.mail.service.SilentMailSender;
import com.brightinteractive.spring.utils.BrightConfigUtils;
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

	@Bean
	public MailSender getMailSender(@Value("${smtp.host}") String smtpHost,
									@Value("${smtp.port}") int smtpPort)
	{
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(smtpHost);
		mailSender.setPort(smtpPort);
		return new SilentMailSender(mailSender);
	}

	
	@Bean
	public static VelocityEngine getVelocityEngine() throws IOException
	{
		return MailConfig.getVelocityEngineWithClassPathLoader();
	}
}
