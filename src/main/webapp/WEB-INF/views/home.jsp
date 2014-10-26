<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Home</title>
		<link href='<c:url value="/resources/style.css" />' rel="stylesheet">
    </head>
    <body>
		<div class="centre">
			<div id="settings">
				<h1>Standby Sync Settings</h1>
				<label for="syncSchedule">Schedule</label><input id="syncSchedule" type="text" readonly="readonly" value="${settings.syncSchedule}"/>
				<label for="syncSource">Source</label><input id="syncSource" type="text" readonly="readonly" value="${settings.syncSource}"/>
				<label for="syncDestination">Destination</label><input id="syncDestination" type="text" readonly="readonly" value="${settings.syncDestination}"/>
				<label for="syncIncluded">Included</label><input id="syncIncluded" type="text" readonly="readonly" value="${settings.syncIncluded}"/>
				<label for="syncExcluded">Excluded</label><input id="syncExcluded" type="text" readonly="readonly" value="${settings.syncExcluded}"/>
				<label for="syncIgnored">Ignored</label><input id="syncIgnored" type="text" readonly="readonly" value="${settings.syncIgnored}"/>			
				<h1>Standby Monitor Settings</h1>				
                <label for="monitorSource">Source</label><input id="monitorSource" type="text" readonly="readonly" value="${settings.monitorSource}"/>
				<label for="monitorDestination">Destination</label><input id="monitorDestination" type="text" readonly="readonly" value="${settings.monitorDestination}"/>
                <label for="monitorSchedule">Schedule</label><input id="monitorSchedule" type="text" readonly="readonly" value="${settings.monitorSchedule}"/>
                <label for="monitorThreshold">Monitor Threshold (minutes)</label><input id="monitorThreshold" type="text" readonly="readonly" value="${settings.monitorThreshold}"/>
                <label for="monitorNotifyFrom">Notify From</label><input id="monitorNotifyFrom" type="text" readonly="readonly" value="${settings.monitorNotifyFrom}"/>
                <label for="monitorNotifyTo">Notify To</label><input id="monitorNotifyTo" type="text" readonly="readonly" value="${settings.monitorNotifyTo}"/>			
			</div>
			<div id="log">
				<h1>Standby sync log</h1>
				<c:forEach items="${logLines}" var="logLine">
					<p><c:out value="${logLine}"/></p>
				</c:forEach>
			</div>
		</div>
    </body>
</html>
