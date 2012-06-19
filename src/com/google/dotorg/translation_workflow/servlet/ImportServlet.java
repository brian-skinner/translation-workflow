// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.dotorg.translation_workflow.servlet;

import com.google.dotorg.translation_workflow.io.SpreadsheetClient;
import com.google.dotorg.translation_workflow.model.Cloud;
import com.google.dotorg.translation_workflow.model.Project;
import com.google.dotorg.translation_workflow.model.Translation;
import com.google.gdata.data.spreadsheet.ListEntry;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mbalumuri@google.com (Mahesh Balumuri)
 *
 */
public class ImportServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(ImportServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    String rawProjectId = request.getParameter("projectId");
    String rawSheetId = request.getParameter("sheetId");
    SpreadsheetClient sheetClient = new SpreadsheetClient();
    Cloud cloud = Cloud.open();
    int projectId = Integer.parseInt(rawProjectId);
    Project project = cloud.getProjectById(projectId);
    PersistenceManager pm = cloud.getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    tx.begin();
    List<Translation> newTranslations = new ArrayList<Translation>();
    try {
      for (ListEntry cells : sheetClient.getWorksheetEntries(rawSheetId)) {
        try {
          String articleName = cells.getCustomElements().getValue("Title");
          URL url = new URL(cells.getCustomElements().getValue("URL"));
          String category = cells.getCustomElements().getValue("Categories");
          String difficulty = cells.getCustomElements().getValue("Difficulty");
          String status = cells.getCustomElements().getValue("Status");
          cells.getCustomElements().setValueLocal("Status", "done");
          Translation translation =
              project.createTranslation(articleName, url.toString(), category, difficulty);
        } catch (MalformedURLException e) {
          logger.warning("Invalid URL : " + cells.getCustomElements().getValue("URL"));
          cells.getCustomElements().setValueLocal("Status", "Invalid URL");
          //cells.update();
        }
        cells.update();
      }
      pm.makePersistentAll(newTranslations);
      tx.commit();

    } catch (Exception e) {
      // TODO(mbalumuri): Auto-generated catch block
      e.printStackTrace();
    }
    cloud.close();
    try {
      response.sendRedirect("/project_overview?project=" + rawProjectId);
    } catch (IOException e) {
      // TODO(mbalumuri): Auto-generated catch block
      e.printStackTrace();
    }
  }

}
