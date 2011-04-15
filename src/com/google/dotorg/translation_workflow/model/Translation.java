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

// TODO: we should remove this dependency from "model" to "io"
import com.google.dotorg.translation_workflow.io.TranslatorToolkitUtil;
import com.google.gdata.util.common.base.Preconditions;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
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
 * A model object that represents something that can be translated, has been 
 * translated, or is being translated.  
 * 
 * Persistent via JDO.
 * 
 * @author Brian Douglas Skinner
 */
@PersistenceCapable
public class Translation {
  public static enum Stage {
    AVAILABLE_TO_TRANSLATE,    // initial stage
    CLAIMED_FOR_TRANSLATION,   // second stage
    AVAILABLE_TO_REVIEW,       // third stage
    CLAIMED_FOR_REVIEW,        // fourth stage
    COMPLETED                  // final stage
  }
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey private Key key;

  /**
   * The original document that this is a translation of.
   */
  @Persistent private Project project; // bi-directional with Project.translations
  @Persistent private String originalTitle;
  @Persistent private String originalUrl;
  @Persistent private String languageCode;
  @Persistent private boolean hasBeenDeleted;
  
  private String toolkitDocIdTail;
  private int numberOfSourceWords;
  private int percentComplete;
  
  @Persistent private String translatorId;
  @Persistent private String reviewerId;
  @Persistent boolean translationComplete = false;
  @Persistent boolean reviewComplete = false;
  @Persistent private String sharedWithUserIds;
  
  @NotPersistent private String htmlContent;

  public String getHtmlContent() {
    Preconditions.checkState(!hasBeenUploadedToTranslatorToolkit());
    return htmlContent;
  }
  
  public void setHtmlContent(String htmlContent) {
    Preconditions.checkState(!hasBeenUploadedToTranslatorToolkit());
    this.htmlContent = htmlContent;
  }
  
  public long getId() {
    return key.getId();
  }

  public Project getProject() {
    return project;
  }
  
  public String getOriginalTitle() {
    return originalTitle;
  }

  public String getOriginalUrl() {
    return originalUrl;
  }

  public String getLanguageCode() {
    return languageCode;
  }
  
  public boolean isDeleted() {
    return hasBeenDeleted;
  }

  public String getTranslatedTitle() {
    // TODO: this is just a stub for now, to be replaced with
    // an implementation that returns the non-English titles
    return "edit translation";
  }

  public String getToolkitDocIdTail() {
    return toolkitDocIdTail;
  }
  
  public boolean hasBeenUploadedToTranslatorToolkit() {
    return (toolkitDocIdTail != null);
  }
  
  public String getToolkitArticleUrl() {
    String docIdTail = getToolkitDocIdTail();
    return (docIdTail == null) ? null : TranslatorToolkitUtil.ARTICLE_URL + docIdTail;
  }

  public String getToolkitFeedUrl() {
    String docIdTail = getToolkitDocIdTail();
    return (docIdTail == null) ? null : TranslatorToolkitUtil.DOC_FEED_URL + docIdTail;
  }

  public boolean isSharedWithUser(User user) {
    // TODO: this implementation will give false positives in some cases:
    // "abcdef,aaaaa,bbbbb".contains("bcd") == true
    return (sharedWithUserIds != null) && (sharedWithUserIds.contains(user.getUserId()));
  }
  
  public int getNumberOfSourceWords() {
    return numberOfSourceWords;
  }

  public int getPercentComplete() {
    return percentComplete;
  }

  public Stage getStage() {
    if (translatorId == null) {
      return Stage.AVAILABLE_TO_TRANSLATE;
    } else {
      if (reviewerId == null) {
        if (!translationComplete) {
          return Stage.CLAIMED_FOR_TRANSLATION;
        } else {
          return Stage.AVAILABLE_TO_REVIEW;
        }
      } else {
        if (!reviewComplete) {
          return Stage.CLAIMED_FOR_REVIEW;
        } else {
          return Stage.COMPLETED;
        }
      }
    }
  }
  
  public boolean isAtStage(Stage stage) {
    return (getStage() == stage);
  }
  
  public String getTranslatorId() {
    return translatorId;
  }
  
  public boolean isUserTheTranslator(User user) {
    return user.getUserId().equals(translatorId);
  }

  public String getReviewerId() {
    return reviewerId;
  }

  public boolean isUserTheReviewer(User user) {
    return user.getUserId().equals(reviewerId);
  }

  public int getProgress() {
    // TODO: this is just a mock-up implementation
    return Math.max(0, (getOriginalTitle().length() - 10)) * 7;
  }
  
  public boolean isAvailableToTranslate() {
    return isAtStage(Stage.AVAILABLE_TO_TRANSLATE);
  }
  
  public boolean isAvailableToReview() {
    return isAtStage(Stage.AVAILABLE_TO_REVIEW);
  }
  
  public String getTranslationStageMessage() {
    switch (getStage()) {
      case AVAILABLE_TO_TRANSLATE:
        return "available";
      case CLAIMED_FOR_TRANSLATION:
        return "assigned";
      case AVAILABLE_TO_REVIEW:
        return "complete";
      case CLAIMED_FOR_REVIEW:
        return "complete";
      case COMPLETED:
        return "complete";
      default:
        return "";
    }
  }
  
  public String getReviewStageMessage() {
    switch (getStage()) {
      case AVAILABLE_TO_TRANSLATE:
        return "";
      case CLAIMED_FOR_TRANSLATION:
        return "";
      case AVAILABLE_TO_REVIEW:
        return "available";
      case CLAIMED_FOR_REVIEW:
        return "in review";
      case COMPLETED:
        return "complete";
      default:
        return "";
    }
  }

  public void setOriginalTitle(String originalTitle) {
    this.originalTitle = originalTitle;
  }

  public void setOriginalUrl(String originalUrl) {
    this.originalUrl = originalUrl;
  }

  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }
  
  public void setDeleted(boolean deleted) {
    this.hasBeenDeleted = deleted;
  }
  
  public void setToolkitDocId(String toolkitDocId) {
    this.toolkitDocIdTail = toolkitDocId;
  }
  
  public void setNumberOfSourceWords(int numberOfSourceWords) {
    this.numberOfSourceWords = numberOfSourceWords;
  }
  
  public void setPercentComplete(int percentComplete) {
    this.percentComplete = percentComplete;
  }
  
  public void claimForTranslation(String translatorId) {
    Preconditions.checkState(isAtStage(Stage.AVAILABLE_TO_TRANSLATE));
    this.translatorId = translatorId;
  }
  
  public void releaseClaimForTranslation() {
    Preconditions.checkState(isAtStage(Stage.CLAIMED_FOR_TRANSLATION));
    this.translatorId = null;
  }
  
  public void markTranslationComplete() {
    Preconditions.checkState(isAtStage(Stage.CLAIMED_FOR_TRANSLATION));
    translationComplete = true;
  }

  public void claimForReview(String reviewerId) {
    Preconditions.checkState(isAtStage(Stage.AVAILABLE_TO_REVIEW));
    this.reviewerId = reviewerId;
  }

  public void releaseClaimForReview() {
    Preconditions.checkState(isAtStage(Stage.CLAIMED_FOR_REVIEW));
    this.reviewerId = null;
  }
  
  public void markReviewComplete() {
    Preconditions.checkState(isAtStage(Stage.CLAIMED_FOR_REVIEW));
    reviewComplete = true;
  }

  public void addSharedWithUser(User user) {
    if (!isSharedWithUser(user)) {
      sharedWithUserIds = (sharedWithUserIds == null) ? "" : sharedWithUserIds;
      sharedWithUserIds += user.getUserId() + ",";
    }
  }
  
}
