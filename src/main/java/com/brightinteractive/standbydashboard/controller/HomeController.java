package com.brightinteractive.standbydashboard.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.io.IOUtils;

@Controller
public class HomeController {

	private final int LOG_LINES_TO_SHOW = 6;
	@Autowired
	private SettingsBean settingsBean;
	@Value("${log.directory}")
	private String logDirectory;

	@RequestMapping(value="/")
	public ModelAndView test(HttpServletResponse response) throws IOException
	{
		Map model = new HashMap();
		List<String> logLines = IOUtils.readLines(new FileInputStream(logDirectory + "/standby-sync.log"), "UTF-8");

		if (logLines.size() > LOG_LINES_TO_SHOW)
		{
			logLines = logLines.subList(logLines.size() - LOG_LINES_TO_SHOW, logLines.size());
		}
		model.put("logLines", logLines);
		model.put("settings", settingsBean);


		return new ModelAndView("home", model);
	}
}
