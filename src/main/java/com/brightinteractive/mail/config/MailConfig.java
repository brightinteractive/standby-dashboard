package com.brightinteractive.mail.config;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.io.IOException;
import java.util.Properties;

import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import org.apache.velocity.app.VelocityEngine;

public class MailConfig
{
	public static VelocityEngine getVelocityEngineWithClassPathLoader() throws IOException
	{
		VelocityEngineFactoryBean factory = new VelocityEngineFactoryBean();
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		factory.setVelocityProperties(props);

		return factory.createVelocityEngine();
	}
}
