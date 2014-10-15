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
				<h1>Standby settings</h1>
				<label for="schedule">Schedule</label><input id="schedule" type="text" readonly="readonly" value='<c:out value="${settings.schedule}"/>'>
				<label for="source">Source</label><input id="source" type="text" readonly="readonly" value='<c:out value="${settings.source}"/>'>
				<label for="destination">Destination</label><input id="destination" type="text" readonly="readonly" value='<c:out value="${settings.destination}"/>'>
				<label for="included">Included</label><input id="included" type="text" readonly="readonly" value='<c:out value="${settings.included}"/>'>
				<label for="excluded">Excluded</label><input id="excluded" type="text" readonly="readonly" value='<c:out value="${settings.excluded}"/>'>
				<label for="ignored">Ignored</label><input id="ignored" type="text" readonly="readonly" value='<c:out value="${settings.ignored}"/>'>
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
