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
  padding-top: 1em;
  padding-bottom: 0.5em;
}
h2 {
  padding-top: 1em;
}
li {
  padding-bottom: 1em;
}
</style>

<%
  String siteName = Website.getInstance().getName();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="/resource/translation-workflow.css">
  <title><%= siteName %> - Help</title>
</head>

<body>
  <%@ include file="/resource/header.jsp" %>

  <table>
    <tr>
      <td>
        <h2>Find articles to translate or review</h2>
        
        <p><%= siteName %> allows translators to quickly find documents to translate 
        or review, and to monitor progress as individuals or as part of a group.</p>
        
        <dl>
          <dt>Register as a translator and/or reviewer</dt>
          <dd>Create a profile with a nickname the languages you speak</dd>
          
          <dt>Find and join discussion groups</dt>
          <dd>Meet other translators working in your language and share questions and tips</dd>
          
          <dt>Browse current projects</dt>
          <dd>Learn more about existing translation projects or competitions in your language(s)</dd>
          
          <dt>Select and claim articles to translate</dt>
          <dd>Search available articles and claim those you’d like to work on in Google Translator Toolkit</dd>
          
          <dt>Monitor your progress</dt>
          <dd>Keep track of your completed articles and see how others are doing</dd>
        </dl>
        &nbsp;
        
        <hr/>
        
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
      
        <hr/>
        
        <h2>FAQs</h2>
        
        <dl>
          <dt>How do I get articles to translate?</dt>
          <dd>Once you have registered on the ‘my profile’ page of <%= siteName %>, 
          click on the ‘my translations’ tab to begin choosing articles. You can 
          either search for an article on a topic that you are interested in, or 
          click on ‘Try my luck’ to see a random selection of available articles. 
          You can preview an article in its original language before deciding if 
          you would like to translate it. Once you are certain, click on ‘I will 
          translate this’ to claim the article. You will now see your selection 
          listed under ‘My translations’. Click on ‘edit translation’ to begin 
          working in Google Translator Toolkit. </dd>
        </dl>
        &nbsp;

        <hr/>
        
        <h2>On the "my translations" page, what are the 4 sections for?</h2>
        
        <p>All participants will see all four sections of the "my translations" page.</p>
        
        <dl>
          <dt>My Translations</dt>
          <dd>This section shows the list of articles that you have volunteered 
          to translate, and that you may have started working on. You can also 
          get more articles to translate by clicking on "Show / hide items to 
          translate".</dd>
          
          <dt>My Newly Authored Articles</dt>
          <dd>This section shows the list of articles that you have written yourself. 
          You can also add a new article by entering the URL of the article and 
          clicking "Add".</dd>
          
          <dt>My Articles to Review</dt>
          <dd>This section shows the list of articles that you have volunteered 
          to review, and may have started to review already. You can also get 
          more items to review by clicking on "Show / hide items to review", but 
          that feature will only appear once other participants have already 
          finished translating some articles and have marked them as ready for review.</dd>
          
          <dt>My Completed Articles</dt>
          <dd>This section shows the list of articles that you translated which 
          are now completely finished: articles which are 100% translated and 
          have been reviewed.</dd>
        </dl>

      </td>
      <td>
        <!-- <img src="/site-config/home-page-image.png"></img>  -->
      </td>
    </tr>
  </table>
  <%@ include file="/resource/footer.jsp" %>
</body>
</html>