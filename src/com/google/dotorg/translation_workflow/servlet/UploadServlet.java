// Copyright 2011 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
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

import javax.servlet.ServletException;
import java.io.*;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
// -------------------------------------------------------------------
// Congratulations, if you're reading this comment, you're probably
// one of first in the world to look at this code!
//
// We checked in this first draft once we had the initial features
// working and the basic structure in place, and now the next step
// is to get a proper code review and start improving the quality
// of the code. All the code below this line is eagerly awaiting
// your review comments.
// -------------------------------------------------------------------

/**
 * @author Mahesh Balumuri (mbalumuri@google.com)
 */
public class UploadServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(UploadServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String rawProjectId = request.getParameter("projectId");
    try {
      ServletFileUpload upload = new ServletFileUpload();
      upload.setSizeMax(1048576);
      UserService userService = UserServiceFactory.getUserService();
      User user = userService.getCurrentUser();
      Cloud cloud = Cloud.open();

      int projectId = Integer.parseInt(rawProjectId);
      Project project = cloud.getProjectById(projectId);
      TextValidator nameValidator = TextValidator.BRIEF_STRING;

      try {
        FileItemIterator iterator = upload.getItemIterator(request);
        int articlesLength = 0;
        while (iterator.hasNext()) {
          FileItemStream item = iterator.next();
          InputStream in = item.openStream();

          if (item.isFormField()) {
          } else {
            String fieldName = item.getFieldName();
            String fileName = item.getName();
            String contentType = item.getContentType();
            String fileContents = null;
            if (!contentType.equalsIgnoreCase("text/csv")) {
              logger.warning("Invalid filetype upload " + contentType);
              response.sendRedirect("/project_overview?project=" + rawProjectId
                  + "&msg=invalid_type");
            }
            try {
              fileContents = IOUtils.toString(in);
              PersistenceManager pm = cloud.getPersistenceManager();
              Transaction tx = pm.currentTransaction();
              tx.begin();
              String[] lines = fileContents.split("\n");
              List<Translation> newTranslations = new ArrayList<Translation>();
              articlesLength = lines.length;
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
            } finally {
              IOUtils.closeQuietly(in);
            }

          }
        }
        cloud.close();
        logger.info(articlesLength + " articles uploaded from csv to the project "
            + project.getName());

        response.sendRedirect("/project_overview?project=" + rawProjectId);
      } catch (SizeLimitExceededException e) {

        logger.warning("Exceeded the maximum size (" + e.getPermittedSize() + ") of the file ("
            + e.getActualSize() + ")");
        response.sendRedirect("/project_overview?project=" + rawProjectId + "&msg=size_exceeded");
      }
    } catch (Exception ex) {

      throw new ServletException(ex);

    }
  }
}
