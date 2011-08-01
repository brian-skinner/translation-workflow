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
  font-size: small;
  padding-top: 1.5em;
  padding-bottom: 0.4em;
}
dd {
  font-size: small;
  padding-bottom: 0.2em;
}
.sign-up-button {
  background: #115cce url(/site-config/button.png) no-repeat center 0;
  border: 1px solid #2528e8;
  -moz-box-shadow: 2px 2px 3px #999;
  -webkit-box-shadow: 2px 2px 3px #999;
  box-shadow: 2px 2px 3px #999;
  -moz-border-radius: 3px;
  -webkit-border-radius: 3px;
  border-radius: 3px;
  color: #fff;
  display: block;
  font-size: 1.5em;
  height: 2em;
  margin: 5px;
  max-width: 247px;
  padding: 5px 0;
  text-align: center;
  text-decoration: none;
  width: 8em;
}
.sign-up-button:hover {
  text-decoration: none;
}
.cta {
  background: #edf6ff;
  border: 1px solid #bfdfff;
  float: right;
  margin: .5em 0;
  padding: 10px 0;
  width: 99.5%;
}
.cta p {
  color: #333;
  font-size: 1em;
  line-height: 1.4em;
  margin: 0 23px;
  text-align: center;
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
        <h2>How to get started</h2>
        
        <ol>
          <li><b>Sign in</b> to <%= siteName %> with your Google account</li>
          
          <li><b>Create a profile</b> by clicking on the ‘my profile’ tab, entering 
          a nickname and selecting the language(s) that you speak. Your nickname 
          will be used to identify you to other translators when you have claimed 
          or completed articles. When you choose a language, you will be invited 
          to sign up for a discussion group so that you can meet other translators 
          in that language to share tips and ask for help.</li>
          
          <li><b>Find and claim articles to translate</b> by clicking on the 
          ‘my translations’ tab. You can search for an article on a topic you 
          are interested in by entering keywords into the search box, or choose 
          from a small set of options by clicking on ‘Try my luck’. When you 
          have chosen an article, claim it by clicking on ‘I will translate this’.</li>
          
          <li><b>Translate your chosen article</b> by clicking on ‘edit translation’. 
          This will open a new tab with the article loaded into Google Translator 
          Toolkit. You can now translate the article. When you have finished your 
          translation, mark the translation as complete (by clicking on "Edit" > 
          "Translation Complete") and then click on "Save and close".</li>
          
          <li><b>Display your progress</b> by returning to the ‘my translations’ 
          tab. Click on the ‘Refresh progress’ button to see the ‘Status’ column 
          change from ‘0% translated’ to ‘100% translated’.</li>
          
          <li><b>Request a review</b> of your article by clicking on the ‘request a review’ 
          link. This will send your article to the ‘My Items to Review’ list of 
          other volunteers/competition judges, depending on whether your project 
          has been set up as a volunteer project or a competition.</li>
        </ol>
        </td>
      <td style="vertical-align:top;">
        <% if (userService.getCurrentUser() == null) { %>  
        <div class="cta"><input 
              id="sign-up-button"
              type="submit"
              value="Sign up"
              class="sign-up-button"
              onclick="window.location='<%= userService.createLoginURL("/")%>';"/>
              <p><a href="/home">Home .. »</a></p> 
            </div> 
        <% } %>
      </td>
    </tr>
  </table>

  <%@ include file="/resource/footer.jsp" %>
  </body>
</html>