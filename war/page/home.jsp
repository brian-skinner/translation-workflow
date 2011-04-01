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
<%@ page import="com.google.dotorg.translation_workflow.model.Cloud" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Volunteer" %>
  
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
  String signInUrl = userService.createLoginURL(request.getRequestURI());
  
  Cloud cloud = Cloud.open();
%>

<%
  if (user != null) {
    Volunteer volunteer = cloud.getVolunteerByUser(user);
    if (volunteer == null) {
      response.sendRedirect("profile.jsp?token=" + token);
    } else {
      response.sendRedirect("my_projects.jsp?token=" + token);
    }
  } else {
    if ((token != null) && token.equals("alpha")) {
      response.sendRedirect("/site-config/about.jsp?token=" + token);
    } else { %>
      <html>
        <head></head>
        <body>empty</body>
      </html>
    <%
    }
  }

  cloud.close();
%>
