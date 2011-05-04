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

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

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
 * A model object that represents one of the hundreds of written languages 
 * in the world, like English or Arabic.  
 * 
 * Persistent via JDO.
 * 
 * @author Brian Douglas Skinner
 */
@PersistenceCapable
public class Language {
  /**
   * An ISO 639-1 Code, such as EN, SW, HI or AR
   */
  @PrimaryKey
  @Persistent private String code = null;
  
  @Persistent private String name = null;
  
  /**
   * The URL of the Google Group that volunteers use to discuss translations
   * in this language.
   */
  @Persistent private String discussionLink = null;
    
  public String getCode() {
    return code;
  }
  
  public String getName() {
    return name;
  }

  public String getDiscussionLink() {
    return discussionLink;
  }

  public String getJoinDiscussionLink() {
    return discussionLink + "/subscribe?note=1";
  }

  public Language setCode(String code) {
    this.code = code;
    return this;
  }

  public Language setName(String name) {
    this.name = name;
    return this;
  }

  public Language setDiscussionLink(String discussionLink) {
    this.discussionLink = discussionLink;
    return this;
  }

  
}
