package com.brightinteractive.standbydashboard.application.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;

import com.brightinteractive.standbydashboard.sync.logging.SyncTaskMessageProvider;

@Controller
public class HomeController
{
	@Autowired
	private SettingsBean settingsBean;
	@Autowired
	private SyncTaskMessageProvider syncTaskMessageProvider;

	private Logger log = Logger.getLogger(this.getClass());

	@RequestMapping(value = "/")
	public ModelAndView test(HttpServletResponse response) throws IOException
	{
		Map model = new HashMap();

		model.put("logLines", syncTaskMessageProvider.getSyncTaskLogMessages());
		model.put("settings", settingsBean);

		return new ModelAndView("home", model);
	}
}
