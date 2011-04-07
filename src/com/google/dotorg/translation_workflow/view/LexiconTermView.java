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

package com.google.dotorg.translation_workflow.view;

import com.google.dotorg.translation_workflow.model.LexiconTerm;

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
public class LexiconTermView {
  private LexiconTerm term;
  
  public static String getHtmlSnippet(LexiconTerm term) {
    LexiconTermView view = new LexiconTermView(term);
    return view.getHtmlSnippet();
  }
  
  public LexiconTermView(LexiconTerm term) {
    this.term = term;
  }
  
  public String getHtmlSnippet() {
    StringBuilder out = new StringBuilder();
    out.append("<div style=\"font-size:large; font-family: arial, helvetica, sans-serif;\">");
    if (term == null) {
      out.append("<p class=\"class=notranslate\">Error: term not found</p>");
    } else {
      out.append("<div style=\"font-size:large; font-family: arial, helvetica, sans-serif;\">");
      out.append("<h1>" + term.getTerm() + "</h1>");
      out.append("<p class=\"class=notranslate\"><i>" + term.getPartOfSpeech() + "</i></p>");
      out.append("<ul>");
      for (String definition : term.getDefinitions()) {
        out.append("<li>" + definition + "</li>");
      }
      out.append("</ul>");
    }
    out.append("</div>");

    return out.toString();
  }
}
