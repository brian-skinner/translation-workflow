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

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.dotorg.translation_workflow.model.Cloud;
import com.google.dotorg.translation_workflow.model.Project;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RefreshProgressServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RefreshProgressServlet.class.getName());
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      UserService userService = UserServiceFactory.getUserService();
      User user = userService.getCurrentUser();

      // read the input parameters from the client and validate them all before using the values
      String rawProjectId = request.getParameter("projectId");
      int projectId = Integer.parseInt(rawProjectId);
      
      Cloud cloud = Cloud.open();
      Project project = cloud.getProjectById(projectId);
      cloud.refreshTranslationStatusFromToolkit(user, project);
      cloud.close();
      
      logger.info("Update translation status for user:" + user.getNickname() + " and project: " + project.getName());
      
      response.sendRedirect("/my_translations?project=" + projectId);
    }

}
