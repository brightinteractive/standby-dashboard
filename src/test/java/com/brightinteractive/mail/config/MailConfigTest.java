package com.brightinteractive.mail.config;

import static org.junit.Assert.*;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;

public class MailConfigTest
{
	@Test
	public void testGetVelocityEngineWithClassPathLoader() throws Exception
	{
		VelocityEngine velocityEngine = MailConfig.getVelocityEngineWithClassPathLoader();
		assertNotNull(velocityEngine.getTemplate("email-templates/simple-template-on-classpath.vm"));		
	}
}