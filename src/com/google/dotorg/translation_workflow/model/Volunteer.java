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

import com.google.appengine.api.datastore.Key;

import java.util.Arrays;
import java.util.List;

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
 * A model object that represents a user who can volunteer to translate or
 * review translations.
 * 
 * Persistent via JDO.
 * 
 * @author Brian Douglas Skinner
 */
@PersistenceCapable
public class Volunteer {

  // TODO: index off of User object instead of Identity key 
  // @Persistent(valueStrategy = IdGeneratorStrategy. IDENTITY)
  @PrimaryKey private Key key;

  @Persistent private String nickname;
  @Persistent private String city;
  @Persistent private boolean anonymous;
  @Persistent private String languages;

  public String getNickname() {
    return nickname;
  }
  
  public String getCity() {
    return city;
  }
  
  public boolean isAnonymous() {
    return anonymous;
  }
  
  public List<String> getLanguages() {
    String[] arrayOfCodes = (languages.isEmpty() ? new String[0] : languages.split(","));
    return Arrays.asList(arrayOfCodes);
  }

  public void setKey(Key key) {
    this.key = key;
  }
  
  public Volunteer setNickname(String nickname) {
    this.nickname = nickname;
    return this;
  }
  
  public Volunteer setCity(String city) {
    this.city = city;
    return this;
  }
  
  public Volunteer setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
    return this;
  }

  public Volunteer setLanguages(List<String> languageCodes) {
    String commaSeparatedValues = join(languageCodes, ",");
    this.languages = commaSeparatedValues;
    return this;
  }
  
  // TODO: double check to make sure this isn't available in a standard google/app-engine library
  // TODO: replace with with com.google.common.base.Joiner
  // http://guava-libraries.googlecode.com/svn/tags/release08/javadoc/index.html
  private String join(List<String> list, String delimiter) {
    if (list.isEmpty()) {
      return "";
    } else {
      StringBuilder results = new StringBuilder();
      results.append(list.get(0));
      for (int i = 1; i < list.size(); i++) {
        results.append(delimiter).append(list.get(i));
      }
      return results.toString();
    }
  }
}
