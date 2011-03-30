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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

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
public class EnsureAlphaTesterTokenFilterImpl implements Filter {

  private String expected;
  private String allow;
  
  /**
   * @throws ServletException  
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    expected = filterConfig.getInitParameter("expected-token");
    allow = filterConfig.getInitParameter("allow");
  }

  @Override
  public void destroy() {}

  /**
   * @throws IOException  
   * @throws ServletException 
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    String actual = request.getParameter("token");

    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      String path = httpRequest.getServletPath();
      if (allow.equals(path) || path.startsWith("/_ah/")) {
        filterChain.doFilter(request, response);
        return;
      }
    }
    
    if (expected.equals(actual)) {
      filterChain.doFilter(request, response);
    } else {
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      PrintWriter writer = response.getWriter();
      String from = "Request from: " + actual;
      writer.append("<html><head></head><body><h1>" + from + "</h1></body></html>");      
    }
  }

}
