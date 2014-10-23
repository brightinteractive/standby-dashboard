package com.brightinteractive.standbydashboard.application.config;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

import com.brightinteractive.spring.utils.BrightConfigUtils;
import com.brightinteractive.standbydashboard.sync.service.*;
import com.brightinteractive.standbydashboard.sync.service.FileSyncTask;
import com.brightinteractive.standbydashboard.application.controller.SettingsBean;

/**
 * @author Bright Interactive
 */
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

	@Bean(name = "scheduler")
	public FileSyncScheduler getScheduler()
	{
		return new FileSyncScheduler();
	}

	@Bean(name = "settings")
	public SettingsBean getSettings()
	{
		return new SettingsBean();
	}

	@Bean(name = "syncTask")
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public FileSyncTask getSyncTask()
	{
		return new FileSyncTask();
	}
}
