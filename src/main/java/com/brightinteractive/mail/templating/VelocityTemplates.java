package com.brightinteractive.mail.templating;

/*
 * Copyright 2014 Bright Interactive, All Rights Reserved.
 */

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import org.apache.velocity.app.VelocityEngine;

@Component
public class VelocityTemplates
{
	public static final String EMAIL_TEMPLATES_FOLDER_PATH = "email-templates/";
	public static final String ENCODING = "UTF-8";
	@Autowired
	protected VelocityEngine velocityEngine;

	public String merge(Map model, String templateName)
	{
		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
														   EMAIL_TEMPLATES_FOLDER_PATH + templateName,
														   ENCODING,
														   model);
	}
}
