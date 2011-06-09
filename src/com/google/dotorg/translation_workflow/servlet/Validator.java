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

/**
 * Never, under any circumstances, trust data that comes from the browser
 * 
 * @author Brian Douglas Skinner
 */
public class Validator {
  private static final String LATIN_HTML_SAFE_CHARACTERS = "\u0041-\u0240";  // unicode block
  private static final String COMBINING_DIACRITICAL_MARKS = "\u0300-\u036F"; // unicode block
  private static final String WHITELISTED_CHARACTERS = "-._ \\d"; // regex
  private static final String ACCEPTED_CHARACTERS =
      LATIN_HTML_SAFE_CHARACTERS + COMBINING_DIACRITICAL_MARKS + WHITELISTED_CHARACTERS;
  
  /* Accept A-Z a-z 0-9 - _ . and the space character. 
   * Accept Unicode points for individual diacritical marks.
   * Accept Unicode points for Latin characters that include diacritical marks.
   */
  public static final Validator ALPHA_NUMERIC = new Validator("[^" + ACCEPTED_CHARACTERS + "]");
  
  private String filterOutRegex;
  
  private Validator(String filterOutRegex) {
    this.filterOutRegex = filterOutRegex;
  }
  
  public String filter(String input) {
    if (input == null) {
      return "";
    }
    return input.replaceAll(filterOutRegex,"");
  }
}
