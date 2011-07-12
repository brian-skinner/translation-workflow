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

import com.google.appengine.api.users.User;
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
import java.util.logging.Logger;

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
  private static final Logger logger = Logger.getLogger(ProjectServlet.class.getName());

    
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    XsrfValidator xsrfValidator = new XsrfValidator(request.getSession().getId());
    String xsrfTokenReceived = request.getParameter("xsrfToken");
    
    if (!xsrfValidator.isValid(xsrfTokenReceived)) {
      response.sendRedirect("/all_projects");
      return;
    }

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    Cloud cloud = Cloud.open();
    
    boolean userCanEdit = userService.isUserAdmin();
    if (!userCanEdit) {
      response.sendRedirect("/all_projects");
      return;
    }
    
    String rawProjectId = request.getParameter("projectId");
    int projectId = -1;
    try {
      projectId = Integer.parseInt(rawProjectId);
    } catch (NumberFormatException e) {
      logger.warning("Parameter validation failure for projectId: " + rawProjectId);
    }
    if (projectId < 0) {
      // TODO: ideally we should go to an error page here
      response.sendRedirect("/all_projects");
      return;
    }
    
    Project project = (projectId == 0) ? cloud.createProject() : cloud.getProjectById(projectId);
    if (project == null) {
      logger.warning("Project not found for projectId: " + projectId);
      // TODO: ideally we should go to an error page here
      response.sendRedirect("/all_projects");
      return;
    } 

    String delete = request.getParameter("deleteProject");
    if (delete != null) {
      // slight sanity check, to reduce risk of some bug accidentally causing a delete
      if ("yes, really delete this project".equals(delete)) {
        project.setDeleted(true);
        response.sendRedirect("/all_projects");
        cloud.close();
        return;
      } else {
        logger.severe("DELETE project request with bad arg: " + rawProjectId);
        response.sendRedirect("/all_projects");
        return;
      }
    }

    
    // read the input parameters from the client and validate them all before using the values
    TextValidator nameValidator = TextValidator.BRIEF_STRING;
    String rawName = request.getParameter("name");
    String name = nameValidator.filter(rawName);
    // TODO: refactor common error logging between ProfileServlet and ProjectServlet
    if (!name.equals(rawName)) {
      logger.warning("Input validation failure for Name, " +
          "Raw: " + rawName + ", Filtered: " + name);
    }
    if (name.isEmpty()) {
      name = "New Project";
    }
    
    TextValidator descriptionValidator = TextValidator.TEXT_BLURB;
    String rawDescription = request.getParameter("description");
    String description = descriptionValidator.filter(rawDescription);
    // TODO: refactor common error logging between ProfileServlet and ProjectServlet
    if (!description.equals(rawDescription)) {
      logger.warning("Input validation failure for Description, " +
          "Raw: " + rawDescription + ", Filtered: " + description);
    }
    
    String rawLanguageCode = request.getParameter("languageCode");
    Language language = cloud.getLanguageByCode(rawLanguageCode);
    
    String nukeRequested = request.getParameter("nuke_translations");
    if (nukeRequested != null) {
      logger.info("Nuking Translations, User: " + user.getUserId());
      cloud.getPersistenceManager().deletePersistentAll(project.getTranslations());
    }
    
    String deleteRequested = request.getParameter("delete_translations");
    if (deleteRequested != null) {
      for (Translation translation : project.getTranslations()) {
        String parameterName = "translation_" + translation.getId();
        String value = request.getParameter(parameterName);
        if (value != null) {
          logger.info("Deleting translation: " + translation.getId() + ", User: " + user.getUserId());
          translation.setDeleted(true);
        }
      }
    }
    
    project.setName(name);
    project.setDescription(description);
    if (language != null) {
      project.setLanguageCode(language.getCode());
    }
    
    String rawCsvArticleList = request.getParameter("articles");
    if (!rawCsvArticleList.isEmpty()) {
      PersistenceManager pm = cloud.getPersistenceManager();
      Transaction tx = pm.currentTransaction();
      tx.begin();
      String[] lines = rawCsvArticleList.split("\n");
      List<Translation> newTranslations = new ArrayList<Translation>();
      for (String line : lines) {
        line = line.replaceAll("\"", "");
        String[] fields = line.split(",");
        String articleName = nameValidator.filter(fields[0]);
        URL url = new URL(fields[1]);
        String category = nameValidator.filter(fields[2]);
        String difficulty = nameValidator.filter(fields[3]);
        Translation translation =
            project.createTranslation(articleName, url.toString(), category, difficulty);
        newTranslations.add(translation);
      }
      pm.makePersistentAll(newTranslations);
      tx.commit();
    }
    cloud.close();

    response.sendRedirect("/project_overview?project=" + project.getId());
  }
  
}
