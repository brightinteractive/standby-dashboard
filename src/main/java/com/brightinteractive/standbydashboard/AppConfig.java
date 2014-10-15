package com.brightinteractive.standbydashboard;

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

/**
 * @author Bright Interactive
 */
@Configuration
@EnableScheduling
@ComponentScan(basePackages = {"com.brightinteractive"},
		excludeFilters = @ComponentScan.Filter(value = Controller.class, type = FilterType.ANNOTATION))
public class AppConfig
{
	public static String APPLICATION_PROPERTIES_FILE = "settings";
	public static String APP_CONTEXT_NAME = "standby-dashboard";

	@Bean
	public static PropertyPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		return BrightConfigUtils
			.getStandardPropertyPlaceholderConfigurer(APPLICATION_PROPERTIES_FILE, APP_CONTEXT_NAME);
	}

	@Bean(name = "scheduler")
	public Scheduler getScheduler()
	{
		return new SchedulerImpl();
	}

	@Bean(name = "syncTask")
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public SyncTask getSyncTask()
	{
		return new SyncTaskImpl();
	}
}
