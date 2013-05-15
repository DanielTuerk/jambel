<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<h1>Help</h1>

<h3>Setup Jambel</h3>
<ul>
    <li>create a new JSON file with a unique file name (e.g. you project name or jambel IP)</li>
    <li>adding the jobs (<stron>WARNING!</stron> somtimes you have to use the IP address instead of the hostname for your Jenkins!!)</li>
</ul>
<p>Example:</p>
<pre>
    {
    "jobs": [
    {
    "jenkinsJobUrl" : "https://192.168.192.90/job/test-jambel/",
    "updateMode" : posting,
    "initialJobStatePoll" : false
    },

    {
    "jenkinsJobUrl" : "https://192.168.192.90/job/AFDS_VEB_incremental/",
    "updateMode" : posting,
    "initialJobStatePoll" : false
    }
    ],

    "signalLight": {
    "green": "top",
    "host": "ampel4.dev.jambit.com",
    "port": 10001,
    "readTimeout": 500,
    "keepAliveInterval": 30000
    },

    "httpPort": 10000
    }
</pre>

<h3>Setup Jenkins</h3>
<ul>
    <li>install Notification Plugin in Jenkins</li>
    <li>active the Notification Plugin in the job configuration</li>
    <li>choose method 'HTTP'</li>
    <li>enter URL 'http://jambel.jambit.com/jambel/notifications/jenkins'</li>
</ul>