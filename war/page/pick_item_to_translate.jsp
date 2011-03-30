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
<%@ page import="com.google.dotorg.translation_workflow.model.Project" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Translation" %>

<!-- TODO: we should try to remove this dependency on "servlet" -->
<%@ page import="com.google.dotorg.translation_workflow.servlet.ClaimServlet" %>
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
  String token = request.getParameter("token");
  String siteName = Website.getInstance().getName();
  
  String projectId = request.getParameter("project");
  String languageCode = request.getParameter("language");
  
  Cloud cloud = Cloud.open();
  Project project = cloud.getProjectById(projectId);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="/resource/translation-workflow.css">
  <title><%= siteName %> - Pick an Item</title>
  <style>
    table th.centered, .centered {
      text-align: center;
    }
    .status {
      color: gray;
    }
  </style>
</head>
  
<body>
  <%@ include file="/resource/header.jsp" %>
  
  <h2>Pick an item to translate</h2>
  <% if (project != null) { %>
    <p><b><%= project.getName() %> (<%= languageCode %>)</b></p>
    <p><%= project.getDescription() %></p>
  <% } %>
  
  <table cellspacing="0" cellpadding="4" class="listing">
    <tr>
      <th></th>
      <th class="centered">Progress</th>
      <th class="centered">Translation</th>
      <th class="centered">Review</th>
    </tr>
  
    <% for (Translation translation : project.getTranslations()) { %>
      <% if (translation.getLanguageCode().equals(languageCode)) { %>
        <tr>
          <td class="term"><a href="<%= translation.getOriginalUrl() %>" target="_blank"><%= translation.getOriginalTitle() %></a></td>
          <td>
            <div style="border: solid thin #bcd; width: 100px; height: 1em;">
              <div style="background-color: #06c; width: <%= translation.getPercentComplete() %>%; height: 100%;" />
            </div>
          </td>
          <td class="centered">
            <% if (translation.isAvailableToTranslate()) { %>
              <form action="/claim_item?token=<%= token %>" method="post">
                <input type="hidden" name="projectId" value="<%= project.getId() %>">
                <input type="hidden" name="language" value="<%= languageCode %>">
                <input type="hidden" name="translationId" value="<%= translation.getId() %>">
                <input type="hidden" name="action" value="<%= ClaimServlet.Action.CLAIM_FOR_TRANSLATION.toString() %>">
                <input type="submit" value="I will translate this" onclick="javascript:lockPage()" />
              </form>
            <% } else { %>
              <div class="status"><%= translation.getTranslationStageMessage() %></div>
            <% } %>
          </td>
          <td class="centered">
            <% if (translation.isAvailableToReview() && !translation.isUserTheTranslator(user)) { %>
              <form action="/claim_item?token=<%= token %>" method="post">
                <input type="hidden" name="projectId" value="<%= project.getId() %>">
                <input type="hidden" name="language" value="<%= languageCode %>">
                <input type="hidden" name="translationId" value="<%= translation.getId() %>">
                <input type="hidden" name="action" value="<%= ClaimServlet.Action.CLAIM_FOR_REVIEW.toString() %>">
                <input type="submit" value="I will review this" onclick="javascript:lockPage()" />
              </form>
            <%  } else { %>
              <span class="status"><%= translation.getReviewStageMessage() %></span>
            <% } %>
          </td>
        </tr>
      <% } %>
    <% } %>
  
  </table>
  
  <% cloud.close(); %>
  
  <%@ include file="/resource/footer.jsp" %>
  </body>
</html>
