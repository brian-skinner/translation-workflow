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
public class LexiconUrl {
  private static int LENGTH_OF_ID_STRING = 8;
  
  /*
   * Given a url like "http://localhost:8888/term/B0028700?foo=bar", returns
   * the term id "B0028700".
   */
  public static String getTermIdFromUrl(String url) {
    String prefix = "/term/";
    int start = url.indexOf(prefix) + prefix.length();
    int end = start + LENGTH_OF_ID_STRING;
    String termId = url.substring(start, end);
    return termId;
  }
}
