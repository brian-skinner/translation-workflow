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
public class InputFilter {
  // Accept only A-Z, a-z, 0-9, -, _, and the space character. Discard everything else.
  public static final InputFilter ALPHA_NUMERIC_FILTER = new InputFilter("[^-_ \\da-zA-Z]");
  
  private String filterOutRegex;
  
  private InputFilter(String filterOutRegex) {
    this.filterOutRegex = filterOutRegex;
  }
  
  public String filter(String input) {
    if (input == null) {
      return "";
    }
    return input.replaceAll(filterOutRegex,"");
  }
}
