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
import com.google.dotorg.translation_workflow.model.Country;
import com.google.dotorg.translation_workflow.model.Language;
import com.google.dotorg.translation_workflow.model.Volunteer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class ProfileServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(ProfileServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    XsrfValidator xsrfValidator = new XsrfValidator(request.getSession().getId());
    String xsrfTokenReceived = request.getParameter("xsrfToken");
    
    if (!xsrfValidator.isValid(xsrfTokenReceived)) {
      response.sendRedirect("/my_translations");
      return;
    }
    
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    String delete = request.getParameter("deleteProfile");
    if (delete != null) {
      // slight sanity check, to reduce risk of some bug accidentally causing a delete
      if ("yes, really delete this profile".equals(delete)) {
        Cloud cloud = Cloud.open();
        cloud.deleteProfileForUser(user);
        response.sendRedirect("/home");
        cloud.close();
        return;
      } else {
        logger.severe("DELETE profile request failed for user: " + user.getUserId());
        response.sendRedirect("/home");
        return;
      }
    } else {
      Cloud cloud = Cloud.open();
      
      // read the input parameters from the client and validate them all before using the values
      TextValidator nicknameValidator = TextValidator.BRIEF_STRING;
  
      String rawNickname = request.getParameter("nickname");
      String nickname = nicknameValidator.filter(rawNickname);
      // TODO: refactor common error logging between ProfileServlet and ProjectServlet
      if (!nickname.equals(rawNickname)) {
        logger.warning("Input validation failure for Nickname, " +
            "Raw: " + rawNickname + ", Filtered: " + nickname);
      }
      
      String rawCountryCode = request.getParameter("country");
      Country country = cloud.getCountryByCode(rawCountryCode);
      // TODO: refactor common error logging between ProfileServlet and ProjectServlet
      if (country == null && !(rawCountryCode == null || rawCountryCode.isEmpty())) {
        logger.warning("Input validation failure for Country code: " + rawCountryCode);
      }
      
      String userType = request.getParameter("userType");
      // String recognition = request.getParameter("recognition");
      // boolean anonymous = !"public".equals(recognition);
      
      logger.info("Saving profile for User: " + user.getUserId());
      
      List<String> selectedLanguages = new ArrayList<String>();
      for (Language language : cloud.getAllLanguages()) {
        String parameterName = "language_" + language.getCode();
        String value = request.getParameter(parameterName);
        if (value != null) {
          selectedLanguages.add(language.getCode());
        }
      }
  
      Volunteer volunteer = cloud.getVolunteerByUser(user);
      if (volunteer == null) {
        volunteer = cloud.createVolunteer(user);
      }
      if (cloud.isNicknameAvailable(nickname)) {
        volunteer.setNickname(nickname);
      }
      String countryCode = (country == null) ? null : country.getCode();
      volunteer.setCountry(countryCode);
      // volunteer.setAnonymous(anonymous);
      volunteer.setLanguageCodes(selectedLanguages);
      if (volunteer.getUserType() == null && !userService.isUserAdmin()) {
        volunteer.setUserType(userType);
      }
      cloud.close();
      
      response.sendRedirect("/my_translations");
      return;
    }
  }

}
