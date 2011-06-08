package com.google.dotorg.translation_workflow.servlet;

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
      
      response.sendRedirect(
          "/my_translations?project=" + projectId);
    }

}
