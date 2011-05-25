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
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.dotorg.translation_workflow.Website" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Cloud" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Language" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Project" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Volunteer" %>
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
  if (user == null) {
    response.sendRedirect("/");
  }
  String siteName = Website.getInstance().getName();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="/resource/translation-workflow.css">
  <title><%= siteName %> - My Projects</title>
</head>

<body>
  <%@ include file="/resource/header.jsp" %>

  <h2>My Projects</h2>
  
  <table class="listing" cellpadding="0" cellspacing="0" border="0">
    <tbody>
      <%
      Cloud cloud = Cloud.open();
      Volunteer volunteer = cloud.getVolunteerByUser(user);
      if ((volunteer != null) && (volunteer.getLanguageCodes() != null)) {
        List<String> languageCodes = volunteer.getLanguageCodes();
        for (String languageCode : languageCodes) {
          Language language = cloud.getLanguageByCode(languageCode);
          String languageName = language.getName();
          %>
          <tr>
            <td style="vertical-align:top; background-color:#e5ecf9; color:#aaa; width:15%; font-size:large; "><%= languageName %></td>
            <%
            List<Project> projects = cloud.getProjectsForLanguage(languageCode);
            if (projects.isEmpty()) {
              %>
              <td>there are no projects in <%= languageName %></td>
              <td></td>
            <% } else { %>
              <td>
              <% for (Project project : projects) { %>
                <div style="font-size:large; padding-bottom:0.5em;"><a href="my_translations.jsp?project=<%= project.getId() %>&language=<%= languageCode %>"><%= project.getUsName() %></a></div>
                <div><%= project.getUsDescription() %></div><br />
              <% } %>
              </td>
            <% } %>
          </tr>
          <%
        }
      }
      cloud.close();
      %>
      <tr>
        <td colspan="3">
          <input 
              type="button"
              value="Add more languages that I speak"
              onclick="window.location='profile.jsp'" />
        </td>
      </tr>
    </tbody>
  </table>
  <%@ include file="/resource/footer.jsp" %>
</body>
</html>