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

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.dotorg.translation_workflow.io.TranslatorToolkitUtil;
import com.google.dotorg.translation_workflow.model.Cloud;
import com.google.dotorg.translation_workflow.model.Language;
import com.google.dotorg.translation_workflow.model.LexiconTerm;
import com.google.dotorg.translation_workflow.model.Project;
import com.google.dotorg.translation_workflow.model.Translation;
import com.google.dotorg.translation_workflow.view.LexiconUrl;
import com.google.dotorg.translation_workflow.view.HtmlPageView;
import com.google.dotorg.translation_workflow.view.LexiconTermView;
import com.google.gdata.data.gtt.DocumentEntry;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * @author Mahesh Balumuri (mbalumuri@google.com)
 */
public class ClaimServlet extends HttpServlet {
  private static final Logger logger = Logger.getLogger(ClaimServlet.class.getName());
  private TranslatorToolkitUtil toolkitUtil;
  
  public static enum Action {
    ADD_NEWLY_AUTHORED_ITEM,
    CLAIM_FOR_TRANSLATION,
    ATTEMPT_TO_SHARE_AGAIN,
    UNCLAIM_FOR_TRANSLATION,
    MARK_TRANSLATION_COMPLETE,
    CLAIM_FOR_REVIEW,
    UNCLAIM_FOR_REVIEW,
    MARK_REVIEW_COMPLETE
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    Cloud cloud = Cloud.open();

    // read the input parameters from the client and validate them all before using the values
    String rawProjectId = request.getParameter("projectId");
    int projectId = Integer.parseInt(rawProjectId);
    
    String rawLanguageCode = request.getParameter("languageCode");
    Language language = cloud.getLanguageByCode(rawLanguageCode);
    
    String rawTranslationId = request.getParameter("translationId");
    int translationId = Integer.parseInt(rawTranslationId);
    
    String rawActionName = request.getParameter("action");
    Action action = Action.valueOf(rawActionName);
    
    String requestUrl = request.getRequestURL().toString();

    String claimerId = user.getUserId();    

    XsrfValidator xsrfValidator = new XsrfValidator(request.getSession().getId());
    String xsrfTokenReceived = request.getParameter("xsrfToken");
    
    if (xsrfValidator.isValid(xsrfTokenReceived)) {
      Translation translation = null;
      if (translationId != 0) {
        translation = cloud.getTranslationByIds(projectId, translationId);
      }
      
      logger.info("User: " + claimerId + ", Translation: " + translationId + ", Action: " + action);
      
      switch (action) {
        case ADD_NEWLY_AUTHORED_ITEM:
          String rawNewArticleUrl = request.getParameter("newArticle");
          URL newArticleUrl = new URL(rawNewArticleUrl);

          Project project = cloud.getProjectById(projectId);
          String[] parts = newArticleUrl.toString().split("/");
          String articleName = parts[parts.length - 1];
          translation = project.createTranslation(articleName, newArticleUrl.toString(), "-", "-");
          if (translation != null) {
            translation.claimForTranslation(claimerId);
            translation.markTranslationComplete();
          }
          break;
        case CLAIM_FOR_TRANSLATION:
          translation.claimForTranslation(claimerId);
          DocumentEntry docEntry = isLocallyServedContent(translation, requestUrl)
              ? attemptToUploadToTranslatorToolkit(
                  translation, getLocallyServedConent(translation, cloud)) 
              : attemptToUploadToTranslatorToolkit(translation);
          attemptToShareDocumentWithUser(translation, user);
          break;
        case ATTEMPT_TO_SHARE_AGAIN:
          attemptToShareDocumentWithUser(translation, user);
          break;
        case UNCLAIM_FOR_TRANSLATION:
          translation.releaseClaimForTranslation();
          attemptToUnshareDocumentWithUser(translation, user);
          break;
        case MARK_TRANSLATION_COMPLETE:
          translation.markTranslationComplete();
          attemptToUnshareDocumentWithUser(translation, user);
          break;
        case CLAIM_FOR_REVIEW:
          translation.claimForReview(claimerId);
          attemptToShareDocumentWithUser(translation, user);
          break;
        case UNCLAIM_FOR_REVIEW:
          translation.releaseClaimForReview();
          attemptToUnshareDocumentWithUser(translation, user);
          break;
        case MARK_REVIEW_COMPLETE:
          String rawReviewScore = request.getParameter("reviewScore");
          int reviewScore = Integer.parseInt(rawReviewScore);
          translation.markReviewComplete(reviewScore);
          attemptToUnshareDocumentWithUser(translation, user);
          break;
      }
    }
    
    cloud.close();
    response.sendRedirect(
        "/my_translations?project=" + projectId + "&language=" + language.getCode());
  }

  /*
   * Returns true if translation.getOriginalUrl() is pointing to the same
   * server that a known example localUrl points to.
   * 
   * Example: returns true when the two urls are http://localhost:8888/foo/bar
   * and http://localhost:8888/mercury/venus/earth/mars?moon=true.
   */
  private boolean isLocallyServedContent(Translation translation, String localUrl) {
    String schemeServerAndPort = getSchemeServerAndPort(localUrl);
    return translation.getOriginalUrl().startsWith(schemeServerAndPort);
  }
  
  private String getLocallyServedConent(Translation translation, Cloud cloud) {
    String url = translation.getOriginalUrl();
    String termId = LexiconUrl.getTermIdFromUrl(url);
    LexiconTerm term = cloud.getLexiconTermByTermId(termId);
    HtmlPageView page = new HtmlPageView(LexiconTermView.getHtmlSnippet(term));
    return page.getHtml();
  }
  
  /*
   * input:  "http://localhost:8888/some/path/O0063600?foo=bar"
   * output: "http://localhost:8888"
   */
  private String getSchemeServerAndPort(String url) {
    int startPast = url.indexOf("//") + "//".length();
    return url.substring(0, url.indexOf("/", startPast));
  }
  
  private TranslatorToolkitUtil getTranslatorToolkitUtil() {
    if (toolkitUtil == null) {
      try {
        toolkitUtil = new TranslatorToolkitUtil();
      } catch (RuntimeException e) {
        logger.log(Level.SEVERE, "Error: RuntimeException starting TranslatorToolkitUtil", e);
        return null;
      }
    }
    return toolkitUtil;
  }
  
  private DocumentEntry attemptToUploadToTranslatorToolkit(Translation translation) {
    return attemptToUploadToTranslatorToolkit(translation, null);
  }

  private DocumentEntry attemptToUploadToTranslatorToolkit(
      Translation translation, String htmlContent) {
    if (translation.hasBeenUploadedToTranslatorToolkit()) {
      return null;
    }
    
    TranslatorToolkitUtil toolkitUtil = getTranslatorToolkitUtil();
    if (toolkitUtil == null) {
      return null;
    } else {
      DocumentEntry docEntry = null;
      try {
        if (htmlContent != null) {
          translation.setHtmlContent(htmlContent);
        }
        docEntry = toolkitUtil.uploadTranslation(translation);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Error: IOException uploading Translation", e);
        return null;
      } catch (ServiceException e) {
        logger.log(Level.SEVERE, "Error: ServiceException uploading Translation", e);
        return null;
      } catch (RuntimeException e) {
        Throwable cause = e.getCause();
        logger.log(Level.SEVERE, "Error: RuntimeException uploading Translation", cause);
        return null;
      }
      String docId = toolkitUtil.getDocIdFromDocumentEntry(docEntry);
      translation.setToolkitDocId(docId);
      translation.setNumberOfSourceWords(docEntry.getNumberOfSourceWords().getValue());
      translation.setPercentComplete(docEntry.getPercentComplete().getValue());
      return docEntry;
    }
  }
  
  private boolean attemptToShareDocumentWithUser(Translation translation, User user) {
    if (translation.isNewlyAuthoredNotTranslated()) {
      return false;
    }
    
    TranslatorToolkitUtil toolkitUtil = getTranslatorToolkitUtil();
    if (toolkitUtil == null) {
      return false;
    }
    
    try {
      toolkitUtil.shareDocumentWithUser(translation, user, "writer");
    } catch (RuntimeException e) {
      logger.log(Level.SEVERE, "Error: RuntimeException sharing document", e);
      return false;
    }
    translation.addSharedWithUser(user);
    return true;
  }
  
  private boolean attemptToUnshareDocumentWithUser(Translation translation, User user) {
    if (translation.isNewlyAuthoredNotTranslated()) {
      return false;
    }

    TranslatorToolkitUtil toolkitUtil = getTranslatorToolkitUtil();
    if (toolkitUtil == null) {
      return false;
    }

    try {
      toolkitUtil.unshareDocumentWithUser(translation, user);
    } catch (RuntimeException e) {
      logger.log(Level.SEVERE, "Error: RuntimeException unsharing document", e);
      return false;
    }
    return true;
  }
    
}