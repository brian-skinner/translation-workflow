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
import com.google.dotorg.translation_workflow.model.Volunteer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    

    // read the input parameters from the client and validate them all before using the values
    Validator textValidator = Validator.ALPHA_NUMERIC; 

    String rawNickname = request.getParameter("nickname");
    String nickname = textValidator.filter(rawNickname);
    
    String rawCountry = request.getParameter("country");
    String country = textValidator.filter(rawCountry);
    
    String recognition = request.getParameter("recognition");
    boolean anonymous = !"public".equals(recognition);
    
    Cloud cloud = Cloud.open();
    
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
    volunteer.setCountry(country);
    volunteer.setAnonymous(anonymous);
    volunteer.setLanguageCodes(selectedLanguages);
    cloud.close();

    response.sendRedirect("/page/my_translations.jsp");
  }

}
