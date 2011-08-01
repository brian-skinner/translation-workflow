// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.dotorg.translation_workflow.servlet;

import java.io.IOException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.dotorg.translation_workflow.model.Cloud;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mbalumuri@google.com (Mahesh Balumuri)
 * 
 */
public class NicknameValidateServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String nickname = request.getParameter("name");
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    
    Cloud cloud = Cloud.open();
    if(cloud.isNicknameValid(nickname,user))
    {
      response.setContentType("text/xml");
      response.setHeader("Cache-Control", "no-cache");
      response.getWriter().write("<result>0</result>");      
    } else {
      response.setContentType("text/xml");
      response.setHeader("Cache-Control", "no-cache");
      response.getWriter().write("<result>1</result>");      
    }

    
  }

}
