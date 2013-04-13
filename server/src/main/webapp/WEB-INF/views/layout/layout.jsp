<%--
  Created by IntelliJ IDEA.
  User: dtuerk
  Date: 26.03.13
  Time: 13:48
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>

    <title>Jambel</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" href="<%= pageContext.getServletContext().getContextPath() %>/static/styles/styles.css" type="text/css">

</head>

<body>
<div>
    <div class="header">
        <tiles:insertAttribute name="header"/>
    </div>

    <div>
        <tiles:insertAttribute name="menu"/>
    </div>

    <br/>

    <!-- Content -->
    <div style="width: 98.5%; height: auto; margin-left: 1.5%">
        <tiles:insertAttribute name="content"/>
    </div>
    <div>
        <tiles:insertAttribute name="footer"/>
    </div>
</div>
</body>
</html>
