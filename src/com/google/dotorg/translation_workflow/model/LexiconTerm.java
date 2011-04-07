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

package com.google.dotorg.translation_workflow.model;

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
 * A model object that represents one of the thousands of entries in a 
 * medical dictionary.  
 * 
 * @author Brian Douglas Skinner
 */
public class LexiconTerm {
  // TODO: revisit whether these should really be public
  public String termId;
  public String term;
  public String partOfSpeech;
  public String[] definitions;
  
  public String getTermId() {
    return termId;
  }

  public String getTerm() {
    return term;
  }

  public String getPartOfSpeech() {
    return partOfSpeech;
  }
  
  public String[] getDefinitions() {
    return definitions;
  }

}
