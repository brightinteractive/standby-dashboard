package com.brightinteractive.mail.templating;

import static org.junit.Assert.*;

import java.util.*;

import com.brightinteractive.mail.config.MailConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;

public class VelocityTemplatesTest
{
	
	VelocityEngine velocityEngine;
	VelocityTemplates velocityTemplates;

	@Before
	public void setUp() throws Exception
	{
		velocityEngine = MailConfig.getVelocityEngineWithClassPathLoader();
		velocityTemplates = new VelocityTemplates();
		velocityTemplates.velocityEngine = velocityEngine;
	}

	@Test
	public void testMerge() throws Exception
	{
		Map<String, Object> model = new HashMap<String, Object>();
		
		String someValue = RandomStringUtils.randomAscii(10);
		model.put("key", someValue);
		
		String content = velocityTemplates.merge(model, "simple-template-on-classpath.vm");
		
		assertTrue(content.contains(someValue));
	}
}