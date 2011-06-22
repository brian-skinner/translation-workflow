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
 * Never, under any circumstances, trust data that comes from the browser
 * 
 * @author Brian Douglas Skinner
 */
public class TextValidator {
  /* Accept A-Z a-z 0-9 - _ . and the space character. 
   * Accept Unicode points for individual diacritical marks.
   * Accept Unicode points for Latin characters that include diacritical marks.
   */
  private static final String LATIN_HTML_SAFE_CHARACTERS = "\u0041-\u0240";  // unicode block
  private static final String COMBINING_DIACRITICAL_MARKS = "\u0300-\u036F"; // unicode block
  private static final String WHITELISTED_CHARACTERS = "-._ \\d"; // regex
  private static final String ACCEPTED_CHARACTERS =
      LATIN_HTML_SAFE_CHARACTERS + COMBINING_DIACRITICAL_MARKS + WHITELISTED_CHARACTERS;
  
  public static final TextValidator BRIEF_STRING = new TextValidator(100);
  public static final TextValidator TEXT_BLURB = new TextValidator(3000);
  
  private int maxLength;
  
  private TextValidator(int maxLength) {
    this.maxLength = maxLength;
  }

  public String filter(String input) {
    if (input == null) {
      return "";
    }
    String filterOutRegex = "[^" + ACCEPTED_CHARACTERS + "]";
    String filtered = input.replaceAll(filterOutRegex,"");
    String trimmed = filtered.trim();
    if (trimmed.length() > maxLength) {
      trimmed = trimmed.substring(0, maxLength);
    }
    return trimmed;
  }
}
