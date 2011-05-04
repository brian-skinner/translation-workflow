// Copyright 2011 Google Inc.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//      http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.dotorg.translation_workflow.servlet;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.dotorg.translation_workflow.model.Cloud;
import com.google.dotorg.translation_workflow.model.Language;
import com.google.dotorg.translation_workflow.model.Project;
import com.google.dotorg.translation_workflow.model.Translation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

  // -------------------------------------------------------------------
  // Congratulations, if you're reading this comment, you're probably 
  // one of first in the world to look at this code!  
  //
  // We checked in this first draft once we had the initial features 
  // working and the basic structure in place, and now the next step 
  // is to get a proper code review and start improving the quality 
  // of the code.  All the code below this line is eagerly awaiting 
  // your review comments.
  //-------------------------------------------------------------------

/**
 * @author Brian Douglas Skinner
 */
public class ProjectServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    boolean userCanEdit = userService.isUserAdmin();
    if (userCanEdit) {
      Cloud cloud = Cloud.open();
      
      // read the input parameters from the client and validate them all before using the values
      Validator textValidator = Validator.ALPHA_NUMERIC; 
      
      String rawProjectId = request.getParameter("projectId");
      int projectId = Integer.parseInt(rawProjectId);
      
      String rawName = request.getParameter("name");
      String name = textValidator.filter(rawName);
      
      String rawLanguageCode = request.getParameter("languageCode");
      Language language = cloud.getLanguageByCode(rawLanguageCode);
      
      String rawDescription = request.getParameter("description");
      String description = textValidator.filter(rawDescription);
      
      String rawCsvArticleList = request.getParameter("articles");
      
      Project project = (projectId == 0) ? cloud.createProject() : cloud.getProjectById(projectId);
      
      String nukeRequested = request.getParameter("nuke_translations");
      if (nukeRequested != null) {
        cloud.getPersistenceManager().deletePersistentAll(project.getTranslations());
      }
      
      String deleteRequested = request.getParameter("delete_translations");
      if (deleteRequested != null) {
        for (Translation translation : project.getTranslations()) {
          String parameterName = "translation_" + translation.getId();
          String value = request.getParameter(parameterName);
          if (value != null) {
            translation.setDeleted(true);
          }
        }
      }
      
      if (!name.isEmpty()) {
        project.setName(name);
      }
      project.setDescription(description);
      project.setLanguageCode(language.getCode());
      
      if (!rawCsvArticleList.isEmpty()) {
        PersistenceManager pm = cloud.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.begin();
        String[] lines = rawCsvArticleList.split("\n");
        List<Translation> newTranslations = new ArrayList<Translation>();
        for (String line : lines) {
          line = line.replaceAll("\"", "");
          String[] fields = line.split(",");
          String articleName = textValidator.filter(fields[0]);
          URL url = new URL(fields[1]);
          Translation translation = project.createTranslation(articleName, url.toString());
          newTranslations.add(translation);
        }
        pm.makePersistentAll(newTranslations);
        tx.commit();
      }
      cloud.close();
  
      if (rawCsvArticleList.isEmpty()) {
        response.sendRedirect("/page/all_projects.jsp");
      } else {
        response.sendRedirect("/page/project_overview.jsp?project=" + projectId);
      }
    } else {
      response.sendRedirect("/page/all_projects.jsp");
    }
  }
  
}
