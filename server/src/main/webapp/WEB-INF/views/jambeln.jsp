<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:forEach items="${initializers}" var="initializer" varStatus="counter">

    <c:if test="${counter.index % 3 == 0}">
        <div style="clear: both"></div>
    </c:if>

    <div class="box" style="float:left">
        <h4><c:out value="${initializer.signalLight.configuration.hostAndPort}"/>&nbsp;(
            <c:choose>
                <c:when test="${initializer.signalLight.available}">
                    Online
                </c:when>
                <c:otherwise>Offline</c:otherwise>
            </c:choose>)</h4>

        <p><c:out value="${initializer.hub.status}"/></p>

        <p>Received Jobs:</p>
        <c:forEach items="${initializer.hub.lastStates}" var="jobState">
            <div><c:out value="${jobState.key.url}"/>:<c:out value="${jobState.value.lastResult}"/></div>
        </c:forEach>
        <p>Configured Jobs:</p>
        <c:forEach items="${initializer.jobInitializer.jambelConfiguration.jobs}" var="job">
            <div><c:out value="${job.jenkinsJobUrl}"/>, <c:out value="${job.updateMode}"/>, <c:out value="${job.pollingInterval}"/>, <c:out
                    value="${job.initialJobStatePoll}"/></div>
        </c:forEach>
    </div>
</c:forEach>

<div style="clear: both"></div>
