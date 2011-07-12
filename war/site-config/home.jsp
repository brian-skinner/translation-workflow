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
<%@ page import="com.google.dotorg.translation_workflow.Website" %>

<%-- ---------------------------------------------------------------
   Congratulations, if you're reading this comment, you're probably 
   one of first in the world to look at this code!  
  
   We checked in this first draft once we had the initial features 
   working and the basic structure in place, and now the next step 
   is to get a proper code review and start improving the quality 
   of the code.  All the code below this line is eagerly awaiting 
   your review comments.
-------------------------------------------------------------- --%>

  <%@ include file="/resource/stopwatch.jsp" %>

<style>
dt {
  font-weight: bold;
  font-size: medium;
  padding-top: 1.5em;
  padding-bottom: 0.5em;
}
dd {
  font-size: medium;
  padding-bottom: 0.5em;
}
</style>

<%
  UserService userService = UserServiceFactory.getUserService();
  String siteName = Website.getInstance().getName();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="/resource/translation-workflow.css">
  <title><%= siteName %></title>
</head>

<body>
  <%@ include file="/resource/header.jsp" %>

  <table>
    <tr>
      <td>
        <h2>Find articles to translate or review</h2>
        
        <p style="font-size:medium;"><%= siteName %> allows translators to quickly find documents to translate 
        or review, and to monitor progress as individuals or as part of a group.</p>
        
        <dl>
          <dt>Register as a translator and/or reviewer</dt>
          <dd>Create a profile with a nickname the languages you speak</dd>
          
          <dt>Find and join discussion groups</dt>
          <dd>Meet other translators working in your language and share questions and tips</dd>
          
          <dt>Browse current projects</dt>
          <dd>Learn more about existing translation projects or competitions in your language(s)</dd>
          
          <dt>Select and claim articles to translate</dt>
          <dd>Search available articles and claim those you’d like to work on in Google Translator Toolkit</dd>
          
          <dt>Monitor your progress</dt>
          <dd>Keep track of your completed articles and see how others are doing</dd>
        </dl>

      </td>
      <td style="vertical-align:top;">
        <% if (userService.getCurrentUser() == null) { %>  
          <input 
              id="delete-button"
              type="submit"
              value="Sign up"
              style="font-size:xx-large; width:10em; height:3.5em; padding:1em; margin:3em 1em; color:blue;"
              onclick="window.location='<%= userService.createLoginURL("/")%>';"/>
        <% } %>
      </td>
    </tr>
  </table>

  
  <%@ include file="/resource/footer.jsp" %>
  </body>
</html>