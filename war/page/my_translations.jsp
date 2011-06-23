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

  <%@ include file="/resource/stopwatch.jsp" %>
  <%@ include file="/resource/xsrf-token.jsp" %>

<%
  UserService userService = UserServiceFactory.getUserService();
  User user = userService.getCurrentUser();
  if (user == null) {
    response.sendRedirect("/");
  }
  
  String siteName = Website.getInstance().getName();
  
  String projectId = request.getParameter("project");
  String showParam = request.getParameter("show");
  boolean showAll = "all".equals(showParam);

  Cloud cloud = Cloud.open();
  
  List<Project> projects = new ArrayList<Project>();
  if (projectId != null) {
    Project project = cloud.getProjectById(projectId);
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
  <script type="text/javascript" language="javascript">
    showHideItemsToTranslate = function(projectId) {
      var rowForItemsToTranslate = document.getElementById('RowForItemsToTranslate'+projectId);
      var divForItemsToTranslate = document.getElementById('DivForItemsToTranslate'+projectId);
      if (rowForItemsToTranslate.className == "open-choices") {
        rowForItemsToTranslate.className = "closed-choices";
        divForItemsToTranslate.style.display = "none";
      } else {
        rowForItemsToTranslate.className = "open-choices";
        divForItemsToTranslate.style.display = "block";
      }
    };
    
    showHideItemsToReview = function(projectId) {
      var rowForItemsToReview = document.getElementById('RowForItemsToReview'+projectId);
      var divForItemsToReview = document.getElementById('DivForItemsToReview'+projectId);
      if (rowForItemsToReview.className == "open-choices") {
        rowForItemsToReview.className = "closed-choices";
        divForItemsToReview.style.display = "none";
      } else {
        rowForItemsToReview.className = "open-choices";
        divForItemsToReview.style.display = "block";
      }
    };
  </script>
  
</head>
  
<body>
  <%@ include file="/resource/header.jsp" %>
  
  <h2>My Translations</h2>
  
  <% if (projects.isEmpty()) { %>
    <input 
        type="button"
        value="Add more languages that I speak"
        onclick="window.location='my_profile'" />
  <% } else { %>
    <table cellspacing="0" cellpadding="4" class="listing">
      <% for (Project project : projects) { 
          List<Translation> itemsToTranslate = cloud.getTranslationItemsForTranslator(user, project);
          boolean mayClaimMore = project.mayUserClaimMoreForTranslation(user);
          String languageCode = project.getLanguageCode();
          String languageName = cloud.getLanguageByCode(languageCode).getName();
        %>
        <tr>
          <th rowspan="<%= 2 + itemsToTranslate.size() %>" style="width:15%; font-size:large; color:#aaa; text-align:center; vertical-align:top;"><%= project.getName() %> (<%= languageName %>)</th>
          <th>Original</th>
          <th>Translation</th>
          <th>Status
            <form action="/refresh_progress" method="post">
              <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
              <input type="hidden" name="projectId" value="<%=project.getId()%>">
              <input type="submit" value="Refresh progress" onclick="javascript:lockPage()" />
            </form>
          </th>
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
                   <span class="muted"><c:out value='<%=(reviewerId == null) ? "" : "by " + reviewer.getNickname()%>'/> </span>  
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
                    <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
                    <input type="hidden" name="projectId" value="<%=project.getId()%>">
                    <input type="hidden" name="languageCode" value="<%=project.getLanguageCode()%>">
                    <input type="hidden" name="translationId" value="<%=item.getId()%>">
                    <input type="hidden" name="action" value="<%=ClaimServlet.Action.MARK_TRANSLATION_COMPLETE.toString()%>">
                    <input type="submit" value="Request a review" onclick="javascript:lockPage()" />
                  </form>
                  </div>
                <% } else { %>
                  <form action="/claim_item" method="post">
                    <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
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
        
        <% 
        String rowId = "RowForItemsToTranslate";
        if (project != null) {
            rowId = rowId + project.getId();
        }
        %>
        
        <tr id=<%= rowId %> class="closed-choices">
          <td colspan="3">
          <%
            String divId = "DivForItemsToTranslate";
            if (project != null) {
              divId = divId + project.getId();
            }
            List<Translation> itemsAvailableToTranslate = showAll
                ? cloud.getAllTranslationItemsToTranslate(project)
                : cloud.getSomeTranslationItemsToTranslate(project); 
          %>
            <div id=<%= divId %> style="display:<%= showAll ? "block" : "none" %>;">
              <table>
                <% for (Translation translation : itemsAvailableToTranslate) { %>
                  <tr>
                    <td><%= translation.getOriginalTitle() %></td>
                    <td>
                      <form action="/claim_item" method="post">
                        <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
                        <input type="hidden" name="projectId" value="<%= project.getId() %>">
                        <input type="hidden" name="languageCode" value="<%= languageCode %>">
                        <input type="hidden" name="translationId" value="<%= translation.getId() %>">
                        <input type="hidden" name="action" value="<%= ClaimServlet.Action.CLAIM_FOR_TRANSLATION.toString() %>">
                        <input type="submit" value="I will translate this" onclick="javascript:lockPage()" />
                      </form>
                    </td>
                    <td><a href="<%= translation.getOriginalUrl() %>" target="_blank">Preview</a></td>
                  </tr>
                <% } %>
              </table>
              <% if (!showAll) {%> 
                <input type="button" value="Show more items to translate" onclick="window.location='/my_translations?show=all'"></input>
              <% } %>
            </div>
          </td>
          <td style="vertical-align:top;">
            <% if (project != null) { %>
              <input 
                  type="button" 
                  value="Show / hide items to translate" 
                  <% if (mayClaimMore) {%> 
                    onclick="javascript:showHideItemsToTranslate(<%= project.getId() %>);"
                  <% } else { %>
                    onclick="window.alert('Please finish the items you have already volunteered for and then check back here for more!');"
                  <% } %>
                  ></input>
            <% } %>
          </td>
        </tr>
      <% } %>
    </table>
    <p>&nbsp;</p>
  
    <h2>My Items to Review</h2>
  
    <%@ include file="/site-config/my-items-to-review-text.jsp" %>

    <table cellspacing="0" cellpadding="4" class="listing">
      <%
        for (Project project : projects) {
           List<Translation> itemsToReview = cloud.getTranslationItemsForReviewer(user, project);
           String languageCode = project.getLanguageCode();
           String languageName = cloud.getLanguageByCode(languageCode).getName();
           boolean translationsAvailable = project.hasTranslationsAvailableForReview(user);
           %>
        <tr>
          <th rowspan="<%=2 + itemsToReview.size()%>" style="width:15%; font-size:large; color:#aaa; text-align:center;">
           <c:out value="<%=project.getName()%>"/> (<%=languageName%>)
          </th>
          <th>Original</th>
          <th>Translation</th>
          <th>Translated by</th>
          <th>Review Score</th>
          <th>Action</th>
        </tr>
        <%
          for (Translation item : itemsToReview) { 
            String translatorId = item.getTranslatorId();
            Volunteer translator = cloud.getVolunteerByUserId(translatorId);
            %>
          <tr>
            <td class="term"><a href="<%=item.getOriginalUrl()%>" target="_blank"><%=item.getOriginalTitle()%></a></td>
            <td class="term"><%=(item.getTranslatedTitle() == null) ? "" : "<a  target=\"_blank\" href=\"" + item.getToolkitArticleUrl() + "\">view translation</a>"%></td>
            <td><c:out value="<%=translator.getNickname()%>"/></td>
            <td>
              <form action="/claim_item" method="post">
                <select name="reviewScore">
                  <option value="0">0</option>
                  <option value="1">1</option>
                  <option value="2">2</option>
                  <option value="3">3</option>
                </select>
            </td>
            <td>
                <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
                <input type="hidden" name="projectId" value="<%=project.getId()%>">
                <input type="hidden" name="languageCode" value="<%=project.getLanguageCode()%>">
                <input type="hidden" name="translationId" value="<%=item.getId()%>">
                <input type="hidden" name="action" value="<%=ClaimServlet.Action.MARK_REVIEW_COMPLETE.toString()%>">
                <input type="submit" value="Mark this as successfully reviewed!" onclick="javascript:lockPage()" />
              </form>
              <form action="/claim_item" method="post">
                <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
                <input type="hidden" name="projectId" value="<%=project.getId()%>">
                <input type="hidden" name="languageCode" value="<%=project.getLanguageCode()%>">
                <input type="hidden" name="translationId" value="<%=item.getId()%>">
                <input type="hidden" name="action" value="<%=ClaimServlet.Action.UNCLAIM_FOR_REVIEW.toString()%>">
                <input type="submit" value="Let someone else review this item" onclick="javascript:lockPage()" />
              </form>
            </td>
          </tr>
        <% } %>

        <% 
        String rowId = "RowForItemsToReview";
        if (project != null) {
            rowId = rowId + project.getId();
        }
        %>

        <tr id=<%= rowId %> class="closed-choices">
          <td style="vertical-align:top;">
            <% if (translationsAvailable) { %>
              <input 
                  type="button" 
                  value="I want a new item to review" 
                  onclick="javascript:showHideItemsToReview(<%= project.getId() %>);" ></input>
            <% } else if (itemsToReview.size() > 0) { %>
                No more translations are available to review.
            <% } else { %>
                No translations are available to review. 
            <% } %>
          </td>
          <td colspan="4">
          
          <%
            String divId = "DivForItemsToReview";
            if (project != null) {
              divId = divId + project.getId();
            }
          %>
          
            <div id=<%= divId %> style="display:none;">
              <table>
                <% for (Translation translation : project.getTranslations()) { %>
                  <% if (translation.getLanguageCode().equals(languageCode) && !translation.isDeleted() && 
                      translation.isAvailableToReview() && !translation.isUserTheTranslator(user)) { %>
                    <tr>
                      <td><a href="<%= translation.getOriginalUrl() %>" target="_blank"><%= translation.getOriginalTitle() %></a></td>
                      <td>
                        <form action="/claim_item" method="post">
                          <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
                          <input type="hidden" name="projectId" value="<%= project.getId() %>">
                          <input type="hidden" name="languageCode" value="<%= languageCode %>">
                          <input type="hidden" name="translationId" value="<%= translation.getId() %>">
                          <input type="hidden" name="action" value="<%= ClaimServlet.Action.CLAIM_FOR_REVIEW.toString() %>">
                          <input type="submit" value="I will review this" onclick="javascript:lockPage()" />
                        </form>
                      </td>
                    </tr>
                  <% } %>
                <% } %>
              </table>
            </div>
          </td>
        </tr>
      <% } %>
    </table>
  
    <p>&nbsp;</p>
  
    <h2>My Completed Items</h2>
    <table cellspacing="0" cellpadding="4" class="listing">
      <%
        for (Project project : projects) {  
          List<Translation> completedItems = cloud.getTranslationItemsCompletedByUser(user, project);
          String languageCode = project.getLanguageCode();
          String languageName = cloud.getLanguageByCode(languageCode).getName();
      %>
        <tr>
          <th rowspan="<%= 1 + Math.max(1, completedItems.size()) %>" style="width:15%; font-size:large; color:#aaa; text-align:center;">
            <c:out value="<%=project.getName()%>"/> (<%=languageName%>)
          </th>
          <th>Original</th>
          <th>Translation</th>
          <th>Translated by</th>
          <th>Reviewed by</th>
          <th>Review Score</th>
        </tr>
        <% if (completedItems.isEmpty()) { %>
          <tr>
            <td colspan="4">your finished articles will show up here</td>
          </tr>
        <% } else { %>
          <% for (Translation item : completedItems) { 
              String translatorId = item.getTranslatorId();
              Volunteer translator = (translatorId == null) ? null : cloud.getVolunteerByUserId(translatorId);
              String reviewerId = item.getReviewerId();
              Volunteer reviewer = (reviewerId == null) ? null : cloud.getVolunteerByUserId(reviewerId);
          %>
            <tr>
              <td class="term"><a href="<%= item.getOriginalUrl() %>" target="_blank"><%= item.getOriginalTitle() %></a></td>
              <td class="term"><%= (item.getTranslatedTitle() == null) ? "" : "<a target=\"_blank\" href=\"" + item.getToolkitArticleUrl() + "\">view translation</a>" %></td>
              <td><c:out value='<%= (translator == null) ? "" : translator.getNickname()%>'/></td>
              <td><c:out value='<%= (reviewer == null) ? "" : reviewer.getNickname()%>'/></td>
              <td><%= item.getReviewScore()%></td>
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
