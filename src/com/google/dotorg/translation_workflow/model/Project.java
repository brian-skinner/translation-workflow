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
import com.google.appengine.api.users.User;
import com.google.dotorg.translation_workflow.model.Translation.Stage;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Order;
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
 * A model object that represents an entire translation project, like the
 * 2011 Medical Glossary Project.
 * 
 * Persistent via JDO.
 * 
 * @author Brian Douglas Skinner
 */
@PersistenceCapable
public class Project {
  
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey private Key key;

  @Persistent private String name;
  @Persistent private String description;
  @Persistent private String languageCode;
  @Persistent private boolean hasBeenDeleted;

  @Persistent(mappedBy = "project")
  @Element(dependent = "true")
  @Order(extensions = @Extension(vendorName="datanucleus", key="list-ordering", value="originalTitle asc"))
  private List<Translation> translations;
  
  /**
   * @return the id
   */
  public long getId() {
    return key.getId();
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  public String getLanguageCode() {
    return languageCode;
  }
  
  public boolean includesLanguageCode(String languageCode) {
    return (languageCode.equals(getLanguageCode()));
  }
  
  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }
  
  public boolean isDeleted() {
    return hasBeenDeleted;
  }

  public List<Translation> getTranslationItemsForTranslator(User user) {
    List<Translation> returnValues = new ArrayList<Translation>();
    for (Translation translation : getTranslations()) {
      String translatorId = translation.getTranslatorId();
      Stage stage = translation.getStage();
      if (user.getUserId().equals(translatorId) && 
          ((stage == Stage.CLAIMED_FOR_TRANSLATION) || 
           (stage == Stage.AVAILABLE_TO_REVIEW) || 
           (stage == Stage.CLAIMED_FOR_REVIEW))) {
        returnValues.add(translation);
      }
    }    
    return returnValues;
  }
  
  public List<Translation> getTranslationItemsForReviewer(User user) {
    List<Translation> returnValues = new ArrayList<Translation>();
    for (Translation translation : getTranslations()) {
      String reviewerId = translation.getReviewerId();
      Stage stage = translation.getStage();
      if (user.getUserId().equals(reviewerId) && (stage == Stage.CLAIMED_FOR_REVIEW)) {
        returnValues.add(translation);
      }
    }    
    return returnValues;
  }
  
  public List<Translation> getTranslationItemsForUser(User user) {
    List<Translation> returnValues = new ArrayList<Translation>();
    for (Translation translation : getTranslations()) {
      String userId = user.getUserId();
      String translatorId = translation.getTranslatorId();
      String reviewerId = translation.getReviewerId();
      if (userId.equals(translatorId) || userId.equals(reviewerId)) {
        returnValues.add(translation);
      }
    }    
    return returnValues;
  }
  
  public List<Translation> getTranslationItemsCompletedByUser(User user) {
    List<Translation> returnValues = new ArrayList<Translation>();
    for (Translation translation : getTranslationItemsForUser(user)) {
      if (translation.getStage() == Stage.COMPLETED) {
        returnValues.add(translation);
      }
    }    
    return returnValues;
  }
  
  public String getTranslationListInCsvFormat() {
    StringBuilder results = new StringBuilder();
    for (Translation translation : getTranslations()) {
      results.append(translation.getOriginalTitle());
      results.append(",");
      results.append(translation.getOriginalUrl());
      results.append("\n");
    }
    return results.toString();
  }
  
  public boolean mayUserClaimMoreForTranslation(User user) {
    int MAX_UNFINISHED_ITEMS = 4;
    int unfinishedItems = 0;
    for (Translation translation : getTranslations()) {
      if (translation.isUserTheTranslator(user) && 
          translation.isAtStage(Stage.CLAIMED_FOR_TRANSLATION)) {
        unfinishedItems++;
      }
    }
    return (unfinishedItems < MAX_UNFINISHED_ITEMS);
  }
  
  public boolean hasTranslationsAvailableForReview(User user) {
    boolean available = false;
    for (Translation translation : getTranslations()) {
      if (translation.isAvailableToReview() && !translation.isUserTheTranslator(user)) {
        available = true;
      }
    }
    return available;
  }
  
  public boolean hasTranslationWithTitle(String title) {
    return (getTranslationWithTitle(title) != null);
  }
  
  public List<Translation> getTranslations() {
    if (translations == null) {
      translations = new ArrayList<Translation>();
    }
    return translations;
  }

  public Translation getTranslationWithTitle(String title) {
    for (Translation translation : getTranslations()) {
      if (title.equals(translation.getOriginalTitle())) {
        return translation;
      }
    }
    return null;
  }
  
  public Translation createTranslation(String title, String url) {
    if (hasTranslationWithTitle(title)) {
      return null;
    } else {
      Translation translation = new Translation();
      translation.setDeleted(false);
      translation.setOriginalTitle(title);
      translation.setOriginalUrl(url);
      translation.setLanguageCode(getLanguageCode());
      getTranslations().add(translation);
      return translation;
    }
  }
  
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  
  public void setDeleted(boolean deleted) {
    this.hasBeenDeleted = deleted;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }
  
}
