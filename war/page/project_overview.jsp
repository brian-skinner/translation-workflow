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
<%@ page import="com.google.dotorg.translation_workflow.model.Language" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Project" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Translation" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Volunteer" %>
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

  boolean readOnly = !userService.isUserAdmin();
  
  Cloud cloud = Cloud.open();
  String projectId = request.getParameter("project");
  Project project = cloud.getProjectById(projectId);
  List<Project> projects = cloud.getAllProjects();
  String foo = null;
  
  String placeholderDescription = 
    "The _____ project includes hundreds articles from ____ with information about ____. " +
    "A few dozen committed volunteers are working hard to get all the articles translated " +
    "by the end of the year. The ____ Foundation is sponsoring the effort, and has pledged " +
    "to donate $1 for every definition completed by December 31.  The donations will all go " +
    "to the ____ Children's Hospital in ___ to fund _____.";

  String usProjectName = (project != null) ? project.getUsName() : "";
  String usProjectDescription = (project != null) ? project.getUsDescription() : placeholderDescription;
  String projectLanguageCode = (project != null) ? project.getLanguageCode() : ""; 
  List<String> projectLanguages = new ArrayList<String>();
  
  String lexiconExampleProjectName = "Lexicon Example Project";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="/resource/translation-workflow.css">
  <title><%= siteName %> - <c:out value="<%= usProjectName %>"/></title>
  <script type="text/javascript" language="javascript">
    lexiconEntryList = {
      // TODO: replace this hard-coded this list with a
      // list derived on the fly from lexicon.xml
      "abdicate": "A0000001",
      "bat": "B0000001",
      "Champagne": "C0000001",
      "drowsy": "D0000001",
      "ebullient": "E0000001",
      "frostbite": "F0000001",
      "grandmother": "G0000001",
      "hatchway": "H0000001",
      "impoverish": "I0000001",
      "jockey": "J0000001",
      "kiln": "K0000001",
      "lagoon": "L0000001",
      "megalith": "M0000001",
      "noble-minded": "N0000001",
      "opponent": "O0000001",
      "panic": "P0000001",
      "quarterly": "Q0000001",
      "refracture": "R0000001",
      "smokestack": "S0000001",
      "tapeworm": "T0000001",
      "U-shaped": "U0000001",
      "vaccary": "V0000001",
      "wallet": "W0000001",
      "xenogamy": "X0000001",
      "yacht": "Y0000001",
      "zirconium": "Z0000001"
    };
    
    getUrlForTermId = function(id) {
      var uri = "<%= request.getRequestURI() %>";
      var urlWithUri = "<%= request.getRequestURL() %>";
      var baseUrl = urlWithUri.replace(uri, '');
      var url = baseUrl + "/term/" + id;
      return url;
    };
      
    getArticleStringFromArticleList = function() {
      var string = "";
      for (var term in lexiconEntryList) {
        string += "\"" + term + "\",\"" + getUrlForTermId(lexiconEntryList[term]) + "\"\n";
      }
      return string;
    };
    
    populateProjectForm = function() {
      document.getElementById('Name').value = "<%= lexiconExampleProjectName %>";
      document.getElementById('Description').value = "<%= placeholderDescription %>";
      var selectElement = document.getElementById('Language');
      for (var i = 0; i < selectElement.length; i++) {
        // default to Arabic for this Example Project
        if (selectElement.options[i].value == "AR") {
          selectElement.selectedIndex = i;
        }
      }
    };
    
    populateArticleForm = function() {
      var url = "<%= request.getRequestURL() %>";
      var uri = "<%= request.getRequestURI() %>";
      var partWeWant = url.replace(uri, '');
      document.getElementById('Articles').value = getArticleStringFromArticleList();
    };
  </script>
  
</head>

<body>
  <%@ include file="/resource/header.jsp" %>
  <form action="/project" method="post">
  
  <h2>Project Overview</h2>

  <table cellpadding="0" cellspacing="18" border="0">
    <tbody>
      <input type="hidden" name="projectId" value="<%= projectId %>"></input>
      <tr id="AttrRowNickname" style="&quot;display: table-row&quot;">
        <td nowrap valign="top" id="AttrLabelCellName"><span class="label">Name:</span></td> 
        <td id="AttrValueCellName">
          <input 
              type="text"
              name="name"
              value="<c:out value="<%= usProjectName %>"/>"
              <%= (readOnly) ? "disabled=\"disabled\"" : "" %>
              size="30"
              id="Name">
        </td>
      </tr>
      <tr>
        <td nowrap valign="top" id="AttrLabelCellLanguage"><span class="label">Language:</span></td> 
        <td id="AttrValueCellLangauge">
          <select 
              name="languageCode"
              id="Language"
              <%= (readOnly) ? "disabled=\"disabled\"" : "" %>>
            <option></option>
            <%
            List<Language> allLanguages = cloud.getAllLanguages();
            for (Language language : allLanguages) {
              %>
              <option 
                  value="<%= language.getCode() %>" 
                  <%= (projectLanguageCode.equals(language.getCode()) ? "selected" : "") %>><%= language.getName() %></option> 
            <% } %>
          </select>
        </td>
      </tr>

      <tr>
        <td nowrap valign="top" id="AttrLabelCellDescription"><span class="label">Description:</span></td> 
        <td id="AttrValueCellDescription">
          <textarea 
              style="font-family: arial, helvetica, sans-serif;"
              <%= (readOnly) ? "disabled=\"disabled\"" : "" %>
              id="Description" name="description"
              placeholder="<%= placeholderDescription %>"
              rows="6" cols="80"><c:out value="<%= usProjectDescription %>"/></textarea>
        </td>
      </tr>

      <tr>
        <td></td>
        <td>
          <% if (!readOnly) { %>
            <% if (projects.isEmpty()) { %>
              <input type="button" 
                  value="start the <%= lexiconExampleProjectName %>"
                  style="background-color:pink;"
                  onclick="javascript:populateProjectForm();"/>
            <% } %>      
            <input type="submit" value="Save" style="font-size:large;"/>
          <% } %>      
        </td>
      </tr>
    </tbody>
  </table>

  <hr/>
  <h2>Statistics</h2>
  
  <table cellpadding="0" cellspacing="18" border="0">
    <tbody>
      <tr style="&quot;display: table-row&quot;">
        <td nowrap valign="top"><span class="label">Original Word Count:</span></td> 
        <td>5,395<span class="muted">&nbsp;&nbsp;(fake example data)</span></td>
      </tr>
      <tr style="&quot;display: table-row&quot;">
        <td nowrap valign="top"><span class="label">Translated Word Count:</span></td> 
        <td>3,958<span class="muted">&nbsp;&nbsp;(fake example data)</span></td>
      </tr>
      <tr style="&quot;display: table-row&quot;">
        <td nowrap valign="top"><span class="label">% Translated:</span></td> 
        <td>60%<span class="muted">&nbsp;&nbsp;(fake example data)</span></td>
      </tr>
      <tr style="&quot;display: table-row&quot;">
        <td nowrap valign="top"><span class="label">% Reviewed:</span></td> 
        <td>40%<span class="muted">&nbsp;&nbsp;(fake example data)</span></td>
      </tr>
      <tr style="&quot;display: table-row&quot;">
        <td nowrap valign="top"><span class="label">% Published:</span></td> 
        <td>25%<span class="muted">&nbsp;&nbsp;(fake example data)</span></td>
      </tr>
      <tr style="&quot;display: table-row&quot;">
        <td nowrap valign="top"><span class="label">Unique contributors:</span></td> 
        <td>38<span class="muted">&nbsp;&nbsp;(fake example data)</span></td>
      </tr>
      <tr style="&quot;display: table-row&quot;">
        <td nowrap valign="top"><span class="label">Views of translated pages:</span></td> 
        <td>82,140<span class="muted">&nbsp;&nbsp;(fake example data)</span></td>
      </tr>
    </tbody>
  </table>

  <hr/>
  <h2>Articles</h2>

  <table class="listing" cellpadding="0" cellspacing="0" border="0">
    <tbody>
      <tr style="background-color: #e5ecf9;">
        <th></th>
        <th>Original</th>
        <th>Word count</th>
        <th>Translation</th>
        <th style="text-align:center;">% Translated</th>
        <th style="text-align:center;">Status</th>
        <th>Translator</th>
        <th>Reviewer</th>
      </tr>
      <%
      List<Translation> translations = (project == null) ? 
          new ArrayList<Translation>() : 
          project.getTranslations();
      for (Translation translation : translations) {
        if (!translation.isDeleted()) {
            String translatorId = translation.getTranslatorId();
            Volunteer translator = (translatorId == null) ? null : cloud.getVolunteerByUserId(translatorId);
            String reviewerId = translation.getReviewerId();
            Volunteer reviewer = (reviewerId == null) ? null : cloud.getVolunteerByUserId(reviewerId);
          %>
          <tr>
            <td>
              <% if (!readOnly) { %>
                <input type="checkbox" name="translation_<%= translation.getId() %>"></input>
              <% } %>
            </td>
            <td class="term"><a href="<%= translation.getOriginalUrl() %>" target="_blank"><%= translation.getOriginalTitle() %></a></td>
            <td style="text-align:right;"><%= translation.getNumberOfSourceWords() %></td>
            <td><a href="<%= translation.getToolkitArticleUrl() %>" target="_blank">translation</a></td>
            <td style="text-align:center;"><%= translation.getPercentComplete() %>% translated</td>
            <% if (translation.isDeleted()) { %>
              <td style="text-align:center;">DELETED</td>
            <% } else { %>
              <td style="text-align:center;"><%= translation.getStage().toString().toLowerCase().replaceAll("_", " ") %></td>
            <% } %>
            <td class="muted"><c:out value='<%=(translator == null) ? "" : translator.getUsNickname()%>'/></td>
            <td class="muted"><c:out value='<%=(reviewer == null) ? "" : reviewer.getUsNickname()%>'/></td>
          </tr>
        <% } %>
      <% } %>
      
      <% if (!readOnly) { %>
        <% if (!translations.isEmpty()) { %>
          <tr>
            <td><input type="checkbox" name="delete_translations"></input></td>
            <td colspan="10"><input type="submit" value="Delete selected articles" style="font-size:large;"/></td>
          </tr>
          <% if (usProjectName.equals("READY_TO_BE_NUKED")) { %>
            <tr>
              <td><input type="checkbox" name="nuke_translations"></input></td>
              <td colspan="10"><input type="submit" value="Nuke ALL articles" style="font-size:large;"/></td>
            </tr>
          <% } %>
        <% } %>
        <tr>
          <td></td>
          <td colspan="9">
            <textarea 
                rows="24" cols="110" id="Articles" name="articles"
                placeholder="&quot;Oral rehydration therapy&quot;,&quot;http://en.wikipedia.org/wiki/Oral_rehydration_therapy&quot;"></textarea>
          </td>
          <td>
            <% if (usProjectName.equals(lexiconExampleProjectName) && translations.isEmpty()) { %>
              <input type="button" 
                  value="start the '<%= lexiconExampleProjectName %>' articles" 
                  style="background-color:pink;"
                  onclick="javascript:populateArticleForm();"/>
            <% } %>
            <input type="submit" value="Add articles" style="font-size:large;"/>
          </td>
        </tr>
      <% } %>
      
    </tbody>
  </table>
  
  </form>
  
  <% if ((project != null) && userService.isUserAdmin()) { %>
    <p></p>
    <hr/>
    <h2>Article list in <a href="http://en.wikipedia.org/wiki/Comma-separated_values" target="_blank">CSV format</a></h2>
    <div style="margin-left:5%;">
      <p>You can copy and paste from this list if you are creating a new project for 
      a different language and want to use some or all of the same articles.</p>
      <textarea rows="24" cols="80" disabled="disabled"><%= project.getTranslationListInCsvFormat() %></textarea>
    </div>
  <% } %>

  <% cloud.close(); %>
  
  <%@ include file="/resource/footer.jsp" %>
</body>
</html>
