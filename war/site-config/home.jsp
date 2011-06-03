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

<%
  String siteName = Website.getInstance().getName();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="/resource/translation-workflow.css">
  <title>Home <%= siteName %></title>
</head>

<body>
  <%@ include file="/resource/header.jsp" %>

  <table>
    <tr>
      <td>
        <h2>Lorem ipsum!</h2>
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
        Aenean bibendum justo sed tellus tincidunt bibendum. 
        Phasellus ut leo ligula, id auctor mi. 
        Curabitur facilisis consectetur lorem pulvinar suscipit. 
        Vivamus eu sem sit amet lacus sodales laoreet vel in leo. 
        Aenean sit amet elit quam</p>
        <p>Fusce id leo id purus interdum adipiscing. 
        Donec commodo cursus leo, sed interdum enim aliquet vel. 
        Aliquam vitae odio velit. 
        Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; 
        In porta cursus mi nec lobortis</p>
  
        <h3>Phasellus ornare enim eu lectus?</h3>
        <p>Phasellus ornare enim eu lectus tincidunt non dictum lectus varius. 
        Aenean diam erat, convallis at adipiscing at, pharetra at magna. 
        Aenean sed orci ac metus fermentum suscipit.</p>
      </td>
      
      <td>
        <img src="/site-config/home-page-image.png"></img>
      </td>
    </tr>
  </table>

  
  <%@ include file="/resource/footer.jsp" %>
  </body>
</html>