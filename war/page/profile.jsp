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
<%@ page import="com.google.dotorg.translation_workflow.model.Country" %>
<%@ page import="com.google.dotorg.translation_workflow.model.Language" %>
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

  <%@ include file="/resource/stopwatch.jsp" %>
  <%@ include file="/resource/xsrf-token.jsp" %>

<%
  UserService userService = UserServiceFactory.getUserService();
  User user = userService.getCurrentUser();
  if (user == null) {
    response.sendRedirect("/");
  }
  String userNickname = user.getNickname(); // example: "foo@bar.com"
  String userShortNickname = userNickname.split("@")[0]; // example: "foo"
  String siteName = Website.getInstance().getName();
  
  Cloud cloud = Cloud.open();
  Volunteer volunteer = cloud.getVolunteerByUser(user);
  
  String volunteerNickname = (volunteer != null) ? volunteer.getNickname() : "";
  String volunteerCountry = (volunteer != null) ? volunteer.getCountry() : "";
  String userType = (volunteer != null) ? volunteer.getUserType(): "";
  List<String> volunteerLanguageCodes = (volunteer != null) ? volunteer.getLanguageCodes() : new ArrayList<String>();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="/resource/translation-workflow.css">
  <title><%= siteName %> - My Profile</title>
  <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
  <script type="text/javascript" language="javascript">
    validateuserNickname = function(){
      var nickname= $("#nickname").val();
      var nicknameErrorSpan = document.getElementById('nickname-error');
      var nicknameTipSpan = document.getElementById('nickname-tip');
      var saveButton = document.getElementById('save-button');
      
      $.post("validate_nickname", { name: nickname },
        function(data) {
        var results = data.getElementsByTagName("result")[0];
        var result = results.childNodes[0];
        if(result.nodeValue == 0){
          nicknameErrorSpan.style.display = "none";
          nicknameTipSpan.style.display = "inline";
          saveButton.disabled = false;         
        }
        else{
          nicknameErrorSpan.style.display = "inline";
          nicknameTipSpan.style.display = "none";
          saveButton.disabled = true;
        }
      });
    }
    
    confirmDelete = function() {
      return confirm("Are you sure you want to completely delete your profile forever?");
    };
    
    validateForm = function() {
      var nickname = $("#nickname").val();
      var trimmed = nickname.replace(/^\s+|\s+$/g, '');
      var languages = $(".languages:checked").length;
      var isAdmin = $("#isAdmin").val();
      var userType = 1;
      userRole =$("#userRole").val();
      if (isAdmin == "false" && userRole == "") {
        userType = $("#userType:checked").length;  
      }
      if (trimmed.length==0 || languages==0 || userType==0) {
        alert("Please fill all the information.");
        return false;
      }
      return true;
    }
    
  </script>
</head>

<body>
  <%@ include file="/resource/header.jsp" %>
  <h2>My Profile - <%= userNickname %></h2>
  
  <table>
    <tr>
      <td width="80%">

        <form action="/profile" method="post">
          <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
          <table cellpadding="0" cellspacing="18" border="0">
            <tbody>
              <tr id="AttrRowNickname" style="&quot;display: table-row&quot;">
                <td nowrap valign="top" id="AttrLabelCellNickname"><span class="label">Nickname:</span></td> 
                <td id="AttrValueCellNickname">
                  <div class="errorbox-good">
                    <input 
                        type="text" 
                        id="nickname"
                        name="nickname"
                        value="<c:out value="<%= volunteerNickname %>"/>" 
                        size="30" 
                        placeholder="<%= userShortNickname %>" 
                        onkeyup="javascript:validateuserNickname()"
                        onchange="javascript:validateuserNickname()">
                    <span class="error-message" id="nickname-error" style="display:none">That name is already taken.</span>
                    <span id="nickname-tip" >Your nickname will be visible to other people using this website</span>
                  </div>
                  
                </td>
              </tr>
              
              <tr>
                <td nowrap valign="top"><span class="label">Country:</span></td>
                <td>
                  <select name="country">
                    <%
                      List<Country> countries = cloud.getAllCountries();
                      for (Country country : countries) {
                        boolean selected = country.getCode().equals(volunteerCountry); %>
                        <option value="<%= country.getCode() %>"<%= selected ? "selected=\"selected\"" : "" %>><%= country.getName() %></option>
                    <% } %>
                  </select>
                </td>
              </tr>
              
              <tr>
                <td nowrap valign="top" id="AttrLabelCellLanguage"><span class="label">Pick the languages you read, write and speak:</span></td> 
                <td>
                  <div class="language-list">
                  <table>
                    <%
                      List<Language> languages = cloud.getAllLanguages();
                      for (Language language : languages) {
                        boolean selected = volunteerLanguageCodes.contains(language.getCode()); %>
                        <tr>
                          <td>
                            <input 
                                type="checkbox" 
                                name="language_<%= language.getCode() %>"
                                value="<%= language.getCode() %>"
                                onclick="document.getElementById('discussion_<%= language.getCode() %>').style.display = (this.checked ? 'inline' : 'none')"
                                id="Language_<%= language.getCode() %>"
                                <%= selected ? "checked" : "" %>
                                class="languages"/>
                          </td>
                          <td><%= language.getName() %></td>
                          <td><span id="discussion_<%= language.getCode() %>" style="display:<%= selected ? "inline" : "none" %>;">
                            <% String link = language.getDiscussionLink();
                                 if (link != null) { %>
                                   - <a target= "_blank" href="<%= link %>">discussion group</a> on Google Groups - 
                                   <a target="_blank" href="<%= language.getJoinDiscussionLink() %>">Join</a> 
                            <% } %>
                          </span></td>
                        </tr>
                    <% } %>
                  </table>
                  </div>
                </td>
              </tr>
              <% if ((!userService.isUserAdmin() && userType == null) || volunteer == null ) { %>
              <tr>
                <td nowrap valign="top" id="AttrLabelCellLanguage">
                  <span class="label">Select that apply:</span>
                </td>
                <td>
                  <input 
                      type="radio"
                      name="userType"
                      id="userType"
                      value="Translator"/>&nbsp;Translator<br>
                  <input
                      type="radio"
                      name="userType"
                      id="userType"
                      value="Reviewer"/>&nbsp;Reviewer
                </td>
              </tr>
              <% } %>
              <tr>
                <td></td>
                <td><input id="save-button" type="submit" value="Save" style="font-size:large;" onclick="return validateForm()"/></td>
              </tr>
              
            </tbody>
          </table>
        </form>
        <input type="hidden" id="userRole" value="<%=userType %>">  
        <input type="hidden" value="<%=userService.isUserAdmin() %>" id="isAdmin">
      </td>
      
      <% if (volunteer != null) { %>
        <td width="20%" style="border-left:solid thin #bcd; vertical-align:top; padding-left:2em; color:gray;">
          <h3>Delete profile</h3>
          <form action="/profile" method="post">
            <div>You can delete your profile from Google Translator Community at any time.  If you delete your profile, you: 
              <ul>
                <li>will not be able to use this website, unless you sign up again</li>
                <li>will not be able to participate in Google Translator Community projects</li>
                <li>will permanently lose your lists of articles</li>
              </ul>
            </div>
            <input type="hidden" name="xsrfToken" value="<%= pageContext.getAttribute("xsrfToken") %>">
            <input type="hidden" name="deleteProfile" value="yes, really delete this profile"></input>
            <input 
                id="delete-button"
                type="submit"
                value="Delete my profile permanently"
                style="font-size:normal;"
                onclick="return confirmDelete();"/>
          </form>
        </td>
      <% } %>
    </tr>
  </table>
  

  <% cloud.close(); %>

  <%@ include file="/resource/footer.jsp" %>
</body>
</html>