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

import com.google.dotorg.translation_workflow.model.Cloud;
import com.google.dotorg.translation_workflow.model.Project;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

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
    InputFilter textSieve = InputFilter.ALPHA_NUMERIC_FILTER; 
    
    String projectId = request.getParameter("projectId");
    String name = textSieve.filter(request.getParameter("name"));
    String language = request.getParameter("language");
    String description = textSieve.filter(request.getParameter("description"));
    String csvArticleList = request.getParameter("articles");
    
    Cloud cloud = Cloud.open();
    
    int id = Integer.parseInt(projectId);
    Project project = (id == 0) ? cloud.createProject() : cloud.getProjectById(projectId);
    if (!name.isEmpty()) {
      project.setName(name);
    }
    project.setDescription(description);
    project.setLanguage(language);
    
    if (!csvArticleList.isEmpty()) {
      String[] lines = csvArticleList.split("\n");
      for (String line : lines) {
        line = line.replaceAll("\"", "");
        String[] fields = line.split(",");
        String articleName = textSieve.filter(fields[0]);
        URL url = new URL(fields[1]);
        project.createTranslation(articleName, url.toString());
      }
    }
    cloud.close();

    if (csvArticleList.isEmpty()) {
      response.sendRedirect("/page/all_projects.jsp");
    } else {
      response.sendRedirect("/page/project_overview.jsp?project=" + projectId);
    }
  }
  
}
