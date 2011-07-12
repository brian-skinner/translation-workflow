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

<%-- ---------------------------------------------------------------
   Congratulations, if you're reading this comment, you're probably 
   one of first in the world to look at this code!  
  
   We checked in this first draft once we had the initial features 
   working and the basic structure in place, and now the next step 
   is to get a proper code review and start improving the quality 
   of the code.  All the code below this line is eagerly awaiting 
   your review comments.
-------------------------------------------------------------- --%>

<style>
.header {
  background-color: #e5ecf9;
  padding-bottom: 10px;
}
.admin-header {
  background: -webkit-gradient(linear, center bottom, center top, from(#e5ecf9), to(pink));
}
#upper-right {
  font-weight: bold;
  color: gray;
  position: absolute; 
  top: 6px; 
  right: 10px;
}
#upper-right a {
  color: #06C;
  text-decoration: none;
}
#logo {
  margin-top: 0.4em;
  margin-left: 0.4em;
  margin-bottom: 0.4em;
}
.content {
  margin: 2em 0.8em;
}
.navbar {
  background-color: #e5ecf9;
  border-bottom: solid thin #bcd;
  width: 100%;
  list-style: none;
  padding: 0.4em 0px;
  margin: 0;
}
.navbar li {
  display: inline;
  padding: 0.4em 1em; 
  margin: 0;
}
.navbar-item {
  text-align: center;
  font-size: large;
}
.selected-navbar-item {
  background-color: white;
  border-top: solid thin #bcd;
  border-left: solid thin #bcd;
  border-right: solid thin #bcd;
  border-bottom: none;
  -moz-border-radius: 1em 1em 0 0;
  -webkit-border-radius: 1em 1em 0 0;
}
.navbar-item a {
  text-decoration: none;
}
#glass-panel {
  font-size: 1200%;
  color: #fff;
  text-align: center;
  line-height: 4.7em;
  background-color: #999;
  position: absolute;
  top: 0px;
  left: 0px;
  width: 100%;
  height: 200%;
  filter: alpha(opacity=50);
  -moz-opacity:0.5;
  -khtml-opacity: 0.5;
  opacity: 0.5;
  display: none;
}
</style>

<script type="text/javascript" language="javascript">
  lockPage = function() {
    document.getElementById('glass-panel').style.display = 'block';
  }
</script>

<%
  UserService headerUserService = UserServiceFactory.getUserService();
  User headerUser = headerUserService.getCurrentUser();
  String siteLogo = Website.getInstance().getLogoFilename();
%>

<div id="glass-panel">working...</div>  

<div class="<%= ((headerUser != null) && headerUserService.isUserAdmin()) ? "header admin-header" : "header" %>">
  <div>
    <% if (headerUser != null) { %>  
      <span id="upper-right">
        <span>
          <a href="sign_in.jsp"><%= headerUser.getNickname() %></a>
          <%= headerUserService.isUserAdmin() ? "(admin)" : "" %>
        </span>
      | <span><a href="<%= headerUserService.createLogoutURL("/") %>">sign out</a></span>
      </span>
    <% } else { %>
      <span id="upper-right">
        <span>
          <a href="<%= headerUserService.createLoginURL("/") %>">sign in</a>
        </span>
      </span>
    <% } %>
  </div>
  <a href="/"><img id="logo" border="0" src="/site-config/<%= siteLogo %>" ></img></a>
</div>
<%
if (headerUser != null) {
  String homePageSelected = request.getRequestURI().equals("/home") ? "selected-navbar-item" : "";
  String myProfilePageSelected = request.getRequestURI().equals("/my_profile") ? "selected-navbar-item" : "";
  String myTranslationsPageSelected = request.getRequestURI().equals("/my_translations") ? "selected-navbar-item" : "";
  String allProjectsPageSelected = request.getRequestURI().equals("/all_projects") ? "selected-navbar-item" : "";
  String helpPageSelected = request.getRequestURI().equals("/help") ? "selected-navbar-item" : "";
  %>
  <div>
    <ul class="navbar">
        <li class="navbar-item"></li> <!-- hack to create a little whitespace -->
        <li class="navbar-item <%= homePageSelected %> "><a href="/home">home</a></li>
        <li class="navbar-item <%= myProfilePageSelected %>"><a href="/my_profile">my profile</a></li>
        <li class="navbar-item <%= myTranslationsPageSelected %>"><a href="/my_translations">my translations</a></li>
        <li class="navbar-item <%= allProjectsPageSelected %>"><a href="/all_projects">all projects</a></td>
        <li class="navbar-item <%= helpPageSelected %>"><a href="/help">help</a></td>
        <% if (headerUserService.isUserAdmin()) { %>
          <li class="navbar-item"><a href="https://code.google.com/p/translation-workflow/wiki/AdminHelp" target="_blank">admin help</a></li>
        <% } %>
    </ul>
  </div>
<% } %>
<div class="content">
