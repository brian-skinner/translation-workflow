<%--
Copyright 2011 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.dotorg.translation_workflow.Website" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Cloud" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Project" %>
<%@ page import="java.util.List" %>

<%-- ---------------------------------------------------------------
   Congratulations, if you're reading this comment, you're probably 
   one of first in the world to look at this code!  
  
   We checked in this first draft once we had the initial features 
   working and the basic structure in place, and now the next step 
   is to get a proper code review and start improving the quality 
   of the code.  All the code below this line is eagerly awaiting 
   your review comments.
-------------------------------------------------------------- --%>

<%
  UserService userService = UserServiceFactory.getUserService();
  User user = userService.getCurrentUser();
  String siteName = Website.getInstance().getName();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="/resource/translation-workflow.css">
  <title><%= siteName %> - All Projects</title>
</head>

<body>
  <%@ include file="/resource/header.jsp" %>
  
  <h2>All projects</h2>
  <table class="listing" cellpadding="0" cellspacing="0" border="0">
    <tbody>
      <tr style="background-color: #e5ecf9;">
        <th>Project</th>
        <th>Description</th>
      </tr>
      <%
        Cloud cloud = Cloud.open();
        List<Project> projects = cloud.getAllProjects();
        for (Project project : projects) {
          %>
          <tr>
            <td style="vertical-align:top;">
              <a href="project_overview.jsp?project=<%= project.getId() %>"><c:out value="<%= project.getUsName() %>"/></a>
            </td>
            <td><c:out value="<%= project.getUsDescription() %>"/></td>
          </tr>
          <%
        }
        cloud.close();
      %>
      <% if (userService.isUserAdmin()) { %>  
        <tr>
          <td><input type="button" value="Create a new project" onclick="window.location='project_overview.jsp?project=0'"></td>
          <td></td>
        </tr>
      <% } %>
    </tbody>
  </table>   
  <%@ include file="/resource/footer.jsp" %>
</body>
</html>
