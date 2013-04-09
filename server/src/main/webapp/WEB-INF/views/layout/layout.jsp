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
    <link rel="stylesheet" href="<%= pageContext.getServletContext().getContextPath() %>/static/style/styles.css" type="text/css">

    <script type="text/javascript" src="<%= pageContext.getServletContext().getContextPath() %>/static/javascript/jquery-1.8.3.js"></script>
</head>

<body>
<div>
    <div>
        <tiles:insertAttribute name="header"/>
    </div>

    <div>
        <tiles:insertAttribute name="menu"/>
    </div>

    <br/>

    <!-- Content -->
    <div style="width: 100%; height: auto;">
        <tiles:insertAttribute name="content"/>
    </div>
    <div>
        <tiles:insertAttribute name="footer"/>
    </div>
</div>
</body>
</html>
