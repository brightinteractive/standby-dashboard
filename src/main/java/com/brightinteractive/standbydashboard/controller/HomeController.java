package com.brightinteractive.standbydashboard.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.redhogs.cronparser.CronExpressionDescriptor;
import org.apache.commons.io.FileUtils;

@Controller
public class HomeController {

	private String source;
	private String destination;
	private String schedule;

	@Value("${source.directory}")
	public void setSource(String source)
	{
		this.source = source;
	}
	@Value("${destination.directory}")
	public void setDestination(String destination)
	{
		this.destination = destination;
	}

	@Value("${schedule.cron}")
	public void setSchedule(String schedule)
	{
		try
		{
			this.schedule = CronExpressionDescriptor.getDescription(schedule);
		}
		catch (ParseException e)
		{
			this.schedule = schedule;
		}
	}

	@RequestMapping(value="/")
	public ModelAndView test(HttpServletResponse response) throws IOException
	{
		Map model = new HashMap();
		model.put("log", FileUtils.readFileToString(new File("sync.log")));
		model.put("source", source);
		model.put("destination", destination);
		model.put("schedule", schedule);

		return new ModelAndView("home", model);
	}


}
