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
<%@ page import="com.google.appengine.api.utils.SystemProperty" %>

<%-- ---------------------------------------------------------------
   Congratulations, if you're reading this comment, you're probably 
   one of first in the world to look at this code!  
  
   We checked in this first draft once we had the initial features 
   working and the basic structure in place, and now the next step 
   is to get a proper code review and start improving the quality 
   of the code.  All the code below this line is eagerly awaiting 
   your review comments.
-------------------------------------------------------------- --%>

</div>

<div class="footer" align="center"> 
  <%@ include file="/site-config/footer-text.jsp" %>
    <% 
      long started = Long.parseLong((String)pageContext.getAttribute("stopwatch"));
      long finished = System.currentTimeMillis();
      long elapsed = finished - started;
    %>
    <div class="loadtime">page load: <%= elapsed %> milliseconds</div>
    <div class="version" 
       title="{App Engine Version: '<%= SystemProperty.version.get() %>', App Version: '<%= SystemProperty.applicationVersion.get() %>'}">
    app version: <%= SystemProperty.applicationVersion.get().split("\\.")[0] %>
    </div>

</div> 
