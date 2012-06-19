// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.dotorg.translation_workflow.io;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;

import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mbalumuri@google.com (Mahesh Balumuri)
 *
 */
public class SpreadsheetClient {

  private static final String SPREADSHEET_URL = "https://spreadsheets.google.com/feeds";

  private SpreadsheetService service;
  private static final Logger logger = Logger.getLogger(SpreadsheetClient.class.getName());
  public static final String SPREADSHEET_LIST_FEED_URL =
      SPREADSHEET_URL + "/spreadsheets/private/full";
  // key url = https://spreadsheets.google.com/feeds/worksheets/key/private/full
  public static final String SPREADSHEET_FEED_URL = SPREADSHEET_URL + "/worksheets/";
  public static final String ACL_FEED_URL = "https://docs.google.com/feeds/acl/private/full/";


  public SpreadsheetClient() {
    TranslatorToolkitSettings settings = new TranslatorToolkitSettings();
    settings.readConfigFile();
    String gttAppName = settings.getGttAppName();
    String userName = settings.getUserName();
    String password = settings.getPassword();
    service = new SpreadsheetService(gttAppName);
    try {
      service.setUserCredentials(userName, password);
      // return service;
    } catch (AuthenticationException e) {
      logger.log(Level.SEVERE,
          "Error: AuthenticationException from SpreadsheetService for user " + userName, e);
      // return null;
    }

  }

  public SpreadsheetService getSpreadsheetService() {
    return service;
  }

  public List<SpreadsheetEntry> getSpreadsheetEntries() throws Exception {
    URL feedUrl = new URL(SPREADSHEET_LIST_FEED_URL);
    SpreadsheetFeed feed = service.getFeed(feedUrl, SpreadsheetFeed.class);
    // AclFeed aclFeed = service.getFeed(new URL(SPREADSHEET_LIST_FEED_URL), AclFeed.class);
    return feed.getEntries();
  }

  public List<ListEntry> getWorksheetEntries(String sheetId) throws Exception {
    String sheetUrl = SPREADSHEET_FEED_URL + sheetId + "/private/full";
    URL feedUrl = new URL(sheetUrl);
    WorksheetFeed worksheetFeed = service.getFeed(feedUrl, WorksheetFeed.class);
    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
    WorksheetEntry worksheet = worksheets.get(0);
    URL listFeedUrl = worksheet.getListFeedUrl();
    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
    return listFeed.getEntries();
  }

}
