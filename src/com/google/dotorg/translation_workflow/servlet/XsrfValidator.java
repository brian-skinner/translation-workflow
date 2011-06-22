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
import com.google.dotorg.translation_workflow.SimpleDigest;
import com.google.dotorg.translation_workflow.io.TranslatorToolkitSettings;

import java.util.logging.Logger;

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
public class XsrfValidator {
  private static final Logger logger = Logger.getLogger(XsrfValidator.class.getName());

  private String sessionId;
  private String expectedXsrfToken;

  public XsrfValidator(String sessionId) {
    this.sessionId = sessionId;
  }
  
  public boolean isValid(String xsrfToken) {
    boolean valid = ((xsrfToken != null) && xsrfToken.equals(getExpectedXsrfToken()));
    if (!valid) {
      logger.warning("XSRF token failure, " +
          "Expected: " + getExpectedXsrfToken() + ", Received: " + xsrfToken);
    }
    return valid;
  }

  public String getExpectedXsrfToken() {
    if (expectedXsrfToken == null) {
      expectedXsrfToken = calculateExpectedXsrfToken();
    }
    return expectedXsrfToken;
  }
  
  public String calculateExpectedXsrfToken() {
    String expected = null;
    TranslatorToolkitSettings settings = new TranslatorToolkitSettings();
    settings.readConfigFile();
    String password = settings.getPassword();
    if (password != null) {
      SimpleDigest digest = new SimpleDigest(password);
      UserService userService = UserServiceFactory.getUserService();
      User user = userService.getCurrentUser();
      String userId = user.getUserId();
      expected = digest.digest(sessionId, userId);
    }
    return expected;
  }
  
}
