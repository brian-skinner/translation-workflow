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
<%@ page import="com.google.dotorg.translation_workflow.model.Translation.Stage" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Volunteer" %>

<!-- TODO: we should try to remove this dependency on "servlet" -->
<%@ page import="com.google.dotorg.translation_workflow.servlet.ClaimServlet" %>
<%@ page import="java.util.ArrayList" %>
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
  
  String projectId = request.getParameter("project");

  Cloud cloud = Cloud.open();
  
  List<Project> projects = new ArrayList<Project>();
  if (projectId != null) {
    Project project = cloud.getProjectById(projectId);
    cloud.refreshTranslationStatusFromToolkit(user, project);
    projects.add(project);
  } else {
    projects = cloud.getProjectsForUser(user);
  }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="/resource/translation-workflow.css">
  <title><%= siteName %> - My Translations</title>
</head>
  
<body>
  <%@ include file="/resource/header.jsp" %>
  
  <h2>My Translations</h2>
  
  <% if (projects.isEmpty()) { %>
    <input 
        type="button"
        value="Add more languages that I speak"
        onclick="window.location='profile.jsp'" />
  <% } else { %>
    <table cellspacing="0" cellpadding="4" class="listing">
      <% for (Project project : projects) { 
          List<Translation> itemsToTranslate = project.getTranslationItemsForTranslator(user);
          boolean mayClaimMore = project.mayUserClaimMoreForTranslation(user);
          String languageCode = project.getLanguageCode();
          String languageName = cloud.getLanguageByCode(languageCode).getName();
        %>
        <tr>
          <th rowspan="<%= 2 + itemsToTranslate.size() %>" style="width:15%; font-size:large; color:#aaa; text-align:center;"><%= project.getName() %> (<%= languageName %>)</th>
          <th>Original</th>
          <th>Translation</th>
          <th>Status</th>
          <th>Action</th>
        </tr>
        <% for (Translation item : itemsToTranslate) { 
             String reviewerId = item.getReviewerId();
             Volunteer reviewer = (reviewerId == null) ? null : cloud.getVolunteerByUserId(reviewerId);
            %>
          <tr>
            <td class="term"><a href="<%= item.getOriginalUrl() %>" target="_blank"><%= item.getOriginalTitle() %></a></td>
            <% if (!item.hasBeenUploadedToTranslatorToolkit()) { %>
              <td>Error: This item has not yet been successfully uploaded to Translator Toolkit.</td>
            <% } else { %>
              <% if (!item.isSharedWithUser(user)) { %>
                <td>Error: This item has not yet been successfully shared with you in Translator Toolkit.</td>
              <% } else { 
                String messageToUser = "When you have finished your translation, mark translation as complete and save and close Google Translator Toolkit";
                %>
                <td class="term">
                  <% if (item.getToolkitArticleUrl() != null) { %>
                    <a href="<%= item.getToolkitArticleUrl() %>"
                        onclick="javascript:window.alert('<%= messageToUser %>');"
                        target="_blank"><%= item.getTranslatedTitle() %></a>
                  <% } %>
                </td>
              <% } %>
            <% } %>
            <td>
              <% if (item.getReviewerId() != null) { %>
                   Waiting for review<br/>
                   <span class="muted"><%=(reviewerId == null) ? "" : "by " + reviewer.getNickname()%> </span>  
              <% } else { %>
                <% if (item.getStage() == Stage.AVAILABLE_TO_REVIEW) { %>
                     Waiting for review<br/>
                     <span class="muted">unclaimed</span>
                <% } else { %>
                  <div style="border: solid thin #bcd; width: 100px; height: 1em;">
                    <div style="background-color: #06c; width: <%=item.getPercentComplete()%>%; height: 100%;" />
                  </div>
                  <div class="muted" style="text-align:center;"><%=item.getPercentComplete()%>% translated</div>
                <% } %>
              <% } %>
            </td>
            <td>
              <% if ((item.getReviewerId() == null) && (item.getStage() != Stage.AVAILABLE_TO_REVIEW)) { %>
                <% if (item.getPercentComplete() > 75) { %>
                  <div>
                  <form action="/claim_item" method="post">
                    <input type="hidden" name="projectId" value="<%=project.getId()%>">
                    <input type="hidden" name="languageCode" value="<%=project.getLanguageCode()%>">
                    <input type="hidden" name="translationId" value="<%=item.getId()%>">
                    <input type="hidden" name="action" value="<%=ClaimServlet.Action.MARK_TRANSLATION_COMPLETE.toString()%>">
                    <input type="submit" value="Request a review" onclick="javascript:lockPage()" />
                  </form>
                  </div>
                <% } else { %>
                  <form action="/claim_item" method="post">
                    <input type="hidden" name="projectId" value="<%=project.getId()%>">
                    <input type="hidden" name="languageCode" value="<%=project.getLanguageCode()%>">
                    <input type="hidden" name="translationId" value="<%=item.getId()%>">
                    <input type="hidden" name="action" value="<%=ClaimServlet.Action.UNCLAIM_FOR_TRANSLATION.toString()%>">
                    <input type="submit" value="Let someone else do this item" onclick="javascript:lockPage()" />
                  </form>
                <% } %>
              <% } %>
            </td>
          </tr>
        <% } %>
        <tr>
          <td>
            <% if (project != null) { %>
              <input 
                  type="button" 
                  value="I want a new item to translate" 
                  <% if (mayClaimMore) {%> 
                    onclick="window.location='pick_item_to_translate.jsp?project=<%=project.getId()%>&language=<%=project.getLanguageCode()%>'"
                  <% } else { %>
                    onclick="window.alert('Please finish the items you have already volunteered for and then check back here for more!');" 
                  <% } %>
                  ></input>
            <% } %>
          </td>
          <td></td>
          <td></td>
          <td></td>
        </tr>
      <% } %>
    </table>
    <p>&nbsp;</p>
  
    <h2>My Items to Review</h2>
  
    <table cellspacing="0" cellpadding="4" class="listing">
      <%
        for (Project project : projects) {
           List<Translation> itemsToReview = project.getTranslationItemsForReviewer(user);
           String languageCode = project.getLanguageCode();
           String languageName = cloud.getLanguageByCode(languageCode).getName();
           boolean translationsAvailable = project.hasTranslationsAvailableForReview(user);
           %>
        <tr>
          <th rowspan="<%=2 + itemsToReview.size()%>" style="width:15%; font-size:large; color:#aaa; text-align:center;"><%=project.getName()%> (<%=languageName%>)</th>
          <th>Original</th>
          <th>Translation</th>
          <th>Translated by</th>
          <th>Action</th>
        </tr>
        <%
          for (Translation item : itemsToReview) { 
            String translatorId = item.getTranslatorId();
            Volunteer translator = cloud.getVolunteerByUserId(translatorId);
            %>
          <tr>
            <td class="term"><a href="<%=item.getOriginalUrl()%>" target="_blank"><%=item.getOriginalTitle()%></a></td>
            <td class="term"><%=(item.getTranslatedTitle() == null) ? "" : "<a href=\"" + item.getToolkitArticleUrl() + "\">view translation</a>"%></td>
            <td><%=translator.getNickname()%></td>
            <td>
              <form action="/claim_item" method="post">
                <input type="hidden" name="projectId" value="<%=project.getId()%>">
                <input type="hidden" name="languageCode" value="<%=project.getLanguageCode()%>">
                <input type="hidden" name="translationId" value="<%=item.getId()%>">
                <input type="hidden" name="action" value="<%=ClaimServlet.Action.UNCLAIM_FOR_REVIEW.toString()%>">
                <input type="submit" value="Let someone else review this item" onclick="javascript:lockPage()" />
              </form>
              <form action="/claim_item" method="post">
                <input type="hidden" name="projectId" value="<%=project.getId()%>">
                <input type="hidden" name="languageCode" value="<%=project.getLanguageCode()%>">
                <input type="hidden" name="translationId" value="<%=item.getId()%>">
                <input type="hidden" name="action" value="<%=ClaimServlet.Action.MARK_REVIEW_COMPLETE.toString()%>">
                <input type="submit" value="Mark this as successfully reviewed!" onclick="javascript:lockPage()" />
              </form>
            </td>
          </tr>
        <% } %>
        <tr>
          <td>
            <% if (translationsAvailable) { %>
            <input 
                type="button" 
                value="I want a new item to review" 
                onclick="window.location='pick_item_to_translate.jsp?project=<%=project.getId()%>&language=<%=project.getLanguageCode()%>'" ></input>
            <% } else if (itemsToReview.size() > 0) { %>
                No more translations are available to review.
            <% } else { %>
                No translations are available to review. 
            <% } %>
          </td>
          <td></td>
          <td></td>
          <td></td>
        </tr>
      <% } %>
    </table>
  
    <p>&nbsp;</p>
  
    <h2>My Completed Items</h2>
    <table cellspacing="0" cellpadding="4" class="listing">
      <%
        for (Project project : projects) {  
          List<Translation> completedItems = project.getTranslationItemsCompletedByUser(user);
          String languageCode = project.getLanguageCode();
          String languageName = cloud.getLanguageByCode(languageCode).getName();
      %>
        <tr>
          <th rowspan="<%=1 + completedItems.size()%>" style="width:15%; font-size:large; color:#aaa; text-align:center;"><%=project.getName()%> (<%=languageName%>)</th>
          <th>Original</th>
          <th>Translation</th>
          <th>Finished on</th>
        </tr>
        <% if (completedItems.isEmpty()) { %>
          <tr>
            <td colspan="2">your finished articles will show up here</td>
            <td></td>
          </tr>
        <% } else { %>
          <% for (Translation item : completedItems) { %>
            <tr>
              <td class="term"><a href="<%=item.getOriginalUrl()%>" target="_blank"><%=item.getOriginalTitle()%></a></td>
              <td class="term"><%=(item.getTranslatedTitle() == null) ? "" : "<a href=\"" + item.getToolkitArticleUrl() + "\">view translation</a>"%></td>
              <td></td>
            </tr>
          <% } %>
        <% } %>
      <% } %>
    </table>

  <% } %>
  <% cloud.close(); %>
    
  <%@ include file="/resource/footer.jsp" %>
</body>
</html>
