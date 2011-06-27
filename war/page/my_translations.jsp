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
  String searchParam = request.getParameter("search");
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
    showChooseArticlesForm = function(projectId) {
      var rowForChooseMoreButton = document.getElementById('RowForChooseMoreButton'+projectId);
      rowForChooseMoreButton.style.display = "none";
      var rowForChooseMoreForm = document.getElementById('RowForChooseMoreForm'+projectId);
      rowForChooseMoreForm.style.display = "table-row";
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
          <% if (itemsToTranslate.isEmpty()) { %>
            <th colspan=4"></th>
          <% } else { %>
            <th>Original</th>
            <th>Translation</th>
            <form action="/refresh_progress" method="post">
              <th>Status
                  <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
                  <input type="hidden" name="projectId" value="<%=project.getId()%>">
                  <input type="submit" value="Refresh progress" onclick="javascript:lockPage()" />
              </th>
            </form>
            <th>Action</th>
          <% } %>
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
                <td>Sorry, we haven't yet been able to give you access to this in Translator Toolkit.
                  <form action="/claim_item" method="post">
                    <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
                    <input type="hidden" name="projectId" value="<%= project.getId() %>">
                    <input type="hidden" name="languageCode" value="<%= languageCode %>">
                    <input type="hidden" name="translationId" value="<%= item.getId() %>">
                    <input type="hidden" name="action" value="<%= ClaimServlet.Action.ATTEMPT_TO_SHARE_AGAIN.toString() %>">
                    <input 
                        type="submit" 
                        value="Try again" 
                        onclick="javascript:lockPage()" 
                        ></input>
                  </form>
                </td>
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
        String rowId = "";
        if (project != null) {
          rowId = rowId + project.getId();
        }
        List<Translation> itemsAvailableToTranslate;
        if (showAll) {
          itemsAvailableToTranslate = cloud.getAllTranslationItemsToTranslate(project);
        } else {
          itemsAvailableToTranslate= cloud.searchTranslationItemsToTranslate(project, searchParam);
        }
        boolean needItems = itemsToTranslate.isEmpty();
        boolean chooseMoreIsOpen = needItems || (searchParam != null);
        %>
        
        <% if (!chooseMoreIsOpen) { %>
          <tr id="RowForChooseMoreButton<%= rowId %>" class="add-articles <%= needItems ? "open-choices" : "closed-choices" %>"">
            <th style="vertical-align:top;" colspan="4">
              <input 
                  type="button" 
                  id="chooseArticlesButton"
                  value="Choose more" 
                  onclick="javascript:showChooseArticlesForm(<%= project.getId() %>);"
                  ></input>
            </th>
          </tr>
        <% } %>
        <tr id="RowForChooseMoreForm<%= rowId %>" style="display:<%= chooseMoreIsOpen ? "table-row" : "none" %>;" class="add-articles <%= needItems ? "open-choices" : "closed-choices" %>"">
          <th style="vertical-align:top;"><span>Choose articles</span></th>
          <td colspan="3">
          <%
            String divId = "DivForItemsToTranslate";
            if (project != null) {
              divId = divId + project.getId();
            }
          %>
            <div id=<%= divId %> style="display:<%= (showAll || needItems || true) ? "block" : "none" %>;">
              <% if (project != null) { %>
                <div style="margin-bottom:0.8em;">
                  <input 
                      type="text"
                      id="searchTerm"
                      name="searchTerm"
                      onkeydown="if (event.keyCode == 13) document.getElementById('searchButton').click()"
                      size="20"
                      placeholder="Wedding"></input>
                  <input 
                      type="button" 
                      id="searchButton"
                      value="Search" 
                      onclick="window.location='/my_translations?search='+document.getElementById('searchTerm').value;"
                      ></input>
                  &nbsp; or &nbsp;
                  <input 
                      type="button" 
                      id="searchButton"
                      value="Try your luck" 
                      onclick="window.location='/my_translations?search=_random_';"
                      ></input>
                 </div>
              <% } %>

              <table class="search-results">
                <% boolean firstRow = true; 
                   for (Translation translation : itemsAvailableToTranslate) { %>
                  <tr>
                    <td><%= translation.getOriginalTitle() %></td>
                    <td>
                      <form action="/claim_item" method="post">
                        <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
                        <input type="hidden" name="projectId" value="<%= project.getId() %>">
                        <input type="hidden" name="languageCode" value="<%= languageCode %>">
                        <input type="hidden" name="translationId" value="<%= translation.getId() %>">
                        <input type="hidden" name="action" value="<%= ClaimServlet.Action.CLAIM_FOR_TRANSLATION.toString() %>">
                        <input 
                            type="submit" 
                            value="I will translate this" 
                            onclick="javascript:lockPage()" 
                            <% if (!mayClaimMore) {%> 
                              disabled="disabled"
                              title="Please finish the items you have already volunteered before signing up for more!"
                            <% } %>
                            ></input>
                      </form>
                    </td>
                    <td><a href="<%= translation.getOriginalUrl() %>" target="_blank">Preview</a></td>
                    <% if (firstRow) { %> 
                      <td rowspan="<%= itemsAvailableToTranslate.size() %>">
                        <input 
                            type="button" 
                            id="clearButton"
                            value="Clear" 
                            style="height:100%"
                            onclick="window.location='/my_translations';"
                            ></input>
                      </td>
                    <% firstRow = false; %> 
                    <% } %> 
                  </tr>
                <% } %>
              </table>
              <% if (!showAll) {%> 
                <!-- disable this feature for now
                  <input type="button" value="Show more items to translate" onclick="window.location='/my_translations?show=all'"></input>
                -->
              <% } %>
            </div>
          </td>
        </tr>
      <% } %>
    </table>
    <p>&nbsp;</p>
  
    <h2>My Newly Authored Articles</h2>
    <table cellspacing="0" cellpadding="4" class="listing">
      <%
        for (Project project : projects) {  
          List<Translation> authoredItems = cloud.getTranslationItemsAuthoredByUser(user, project);
          String languageCode = project.getLanguageCode();
          String languageName = cloud.getLanguageByCode(languageCode).getName();
      %>
        <tr>
          <th rowspan="<%= 2 + Math.max(1, authoredItems.size()) %>" style="width:15%; font-size:large; color:#aaa; text-align:center;">
            <c:out value="<%=project.getName()%>"/> (<%=languageName%>)
          </th>
          <th>Article</th>
        </tr>
        <% for (Translation item : authoredItems) { 
            String translatorId = item.getTranslatorId();
            Volunteer translator = (translatorId == null) ? null : cloud.getVolunteerByUserId(translatorId);
            String reviewerId = item.getReviewerId();
            Volunteer reviewer = (reviewerId == null) ? null : cloud.getVolunteerByUserId(reviewerId);
        %>
          <tr>
            <td class="term" colspan="2"><a href="<%= item.getOriginalUrl() %>" target="_blank"><%= item.getOriginalTitle() %></a></td>
          </tr>
        <% } %>
        <tr class="add-articles">
          <form action="/claim_item" method="post">
            <%
            String lowercaseCode = languageCode.toLowerCase();
            String exampleArticle = "http://" + lowercaseCode + ".wikipedia.org/wiki/Jimmy_Wales";
            %>
            <td colspan="1">
              <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
              <input type="hidden" name="projectId" value="<%=project.getId()%>">
              <input type="hidden" name="languageCode" value="<%= languageCode %>">
              <input type="hidden" name="translationId" value="0">
              <input type="hidden" name="action" value="<%=ClaimServlet.Action.ADD_NEWLY_AUTHORED_ITEM.toString()%>">
              <input 
                  type="text"
                  id="newArticle"
                  name="newArticle"
                  size="60"
                  placeholder="<%=exampleArticle%>"></input>
              <input type="hidden" name="projectId" value="<%=project.getId()%>">
              <input type="submit" value="Add" onclick="javascript:lockPage()" />
            </td>
          </form>           
        </tr>
      <% } %>
    </table>    
    <p>&nbsp;</p>
    
    <h2>My Articles to Review</h2>
  
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
            <td class="term"><%=(item.getTranslatedTitle() == null) ? "" : "<a target=\"_blank\" href=\"" + item.getToolkitArticleUrl() + "\">view translation</a>"%></td>
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
  
  <script type="text/javascript" language="javascript">
    document.getElementById('searchTerm').focus();
  </script>
    
  <%@ include file="/resource/footer.jsp" %>
</body>

</html>
