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

package com.google.dotorg.translation_workflow.io;

import com.google.gdata.client.gtt.GttService;
import com.google.gdata.data.Link;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.gtt.DocumentEntry;
import com.google.gdata.data.gtt.DocumentSource;
import com.google.gdata.data.gtt.GlossariesElement;
import com.google.gdata.data.gtt.SourceLanguage;
import com.google.gdata.data.gtt.TargetLanguage;
import com.google.gdata.data.gtt.TmsElement;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.com.google.common.base.Preconditions;
import com.google.dotorg.translation_workflow.model.Translation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class TranslatorToolkitUtil {
  private static final String TOOLKIT_URL = "http://translate.google.com/toolkit/";
  public static final String FEED_URL = TOOLKIT_URL + "feeds/documents";
  public static final String DOC_FEED_URL = TOOLKIT_URL + "feeds/documents/";
  public static final String DOC_ACL_URL = TOOLKIT_URL + "feeds/acl/documents/";
  public static final String ARTICLE_URL = TOOLKIT_URL + "workbench?did=";
  public static final String MEMORY_URL = TOOLKIT_URL + "feeds/tm/";
  public static final String GLOSSARY_URL = TOOLKIT_URL + "feeds/glossary/";

  private static final Logger logger = Logger.getLogger(TranslatorToolkitUtil.class.getName());

  private GttService service;
  private String glossaryId;
  private String translationMemoryId;
  
  private static class FakeHtmlFile extends File {
    private String htmlContent;
    
    public FakeHtmlFile(String htmlContent) {
      super("resource/empty-file.html");
      this.htmlContent = htmlContent;
    }
    
    @Override
    public long length() {
      return htmlContent.length();
    }
  }
  
  private static class FakeFileSource extends MediaFileSource {
    private static String HTML_MEDIA_TYPE = ContentType.TEXT_HTML.toString();
    private String htmlContent;
    
    public FakeFileSource(String htmlContent) {
      super(new FakeHtmlFile(htmlContent), HTML_MEDIA_TYPE);
      this.htmlContent = htmlContent;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(htmlContent.getBytes("UTF-8"));
    }
  }

  public TranslatorToolkitUtil() {
    service = signIntoGttService();
  }
  
  public GttService getGttService() {
    return service;
  }
  
  public String getDocIdFromDocumentEntry(DocumentEntry docEntry) {
    Preconditions.checkState(docEntry.getEditLink().getHref().equals(docEntry.getId()));
    return getDocIdFromIdUrl(docEntry.getId());
  }
  
  /* Converts GTT doc URLs into GTT docIds
   * from:
   *    http://translate.google.com/toolkit/feeds/documents/00002be3vpt9wxs
   * to:
   *    00002be3vpt9wxs
   */
  private String getDocIdFromIdUrl(String url) {
    String docId = url.replace(DOC_FEED_URL, "");
    return docId;
  }
  
  public DocumentEntry uploadTranslation(Translation translation) 
      throws IOException, ServiceException {
    String sourceLangCode = "EN";
    String targetLangCode = translation.getLanguageCode();
    String qualifiedTitle = translation.getOriginalTitle() + " (" + targetLangCode + ")";
    String htmlContent = translation.getHtmlContent();
    return uploadHtmlDocument(
        sourceLangCode, targetLangCode, qualifiedTitle, translation.getOriginalUrl(), htmlContent);
  }

  public void refreshStatus(Translation translation) {
    if (translation.hasBeenUploadedToTranslatorToolkit()) {
      String docFeedUrl = translation.getToolkitFeedUrl();
      DocumentEntry de = fetch(docFeedUrl);
      if (de != null) {
        translation.setNumberOfSourceWords(de.getNumberOfSourceWords().getValue());
        translation.setPercentComplete(de.getPercentComplete().getValue());
      }
    } else {
      try {
        uploadTranslation(translation);
      } catch (ServiceException e) {
        // pass
      } catch (IOException e) {
        // pass
      }
    }
  }
  
  public DocumentEntry fetch(String docFeedUrl) {
    try {
      URL url = new URL(docFeedUrl);
      DocumentEntry entry = service.getEntry(url, DocumentEntry.class);
      return entry;
    } catch (ServiceException e) {
      // TODO: replace with real exception handling
      logger.severe(" ~~~~~~~~~~ DOH! ~~~~~~~~~ fetch got a ServiceException:\n" + e.toString());
      return null;
    } catch (IOException e) {
      // TODO: replace with real exception handling
      logger.severe(" ~~~~~~~~~~ DOH! ~~~~~~~~~ fetch got a IOException");
      return null;
    }
  }
  
  public void shareDocumentWithUser(Translation translation, User user, String roleName) {
    String emailId = user.getEmail();
    String aclUrl = DOC_ACL_URL + translation.getToolkitDocIdTail();
    AclScope scope = new AclScope(AclScope.Type.USER, emailId);
    AclRole role = new AclRole(roleName);

    AclEntry entry = new AclEntry();
    entry.setRole(role);
    entry.setScope(scope);
    int attempts = 0;
    boolean failed = true;
    emailId = emailId.toLowerCase();
    if(roleName.equalsIgnoreCase("reader")){
      String FeedUrl = DOC_ACL_URL + translation.getToolkitDocIdTail();
      try {
        AclFeed aclFeed = service.getFeed(new URL(FeedUrl), AclFeed.class);
        int i = 1;
        for (AclEntry feedEntry : aclFeed.getEntries()) {
          if(feedEntry.getRole().getValue().equalsIgnoreCase("reader")){
            String aclFeedUrl = aclUrl + '/' + feedEntry.getScope().getValue();
            service.delete(new URL(aclFeedUrl));
            logger.info("Unsharing with user : "+
              feedEntry.getScope().getValue() + "'"
              + ", role = '" + feedEntry.getRole().getValue() + "'");
          }
        }
      } catch (ServiceException e) {
          logger.severe("Error unsharing with user");
      } catch (IOException e) {
          logger.severe("Error unsharing with user" );
      }
    }
    // TODO: have this retry a few times if it fails
    try {
      service.insert(new URL(aclUrl), entry);
      logger.info("Shared with user " + emailId);
    } catch (ServiceException e) {
      logger.severe("Error sharing with user " + emailId);
    } catch (IOException e) {
      logger.severe("Error sharing with user " + emailId);
    }
  }
  
  public void unshareDocumentWithUser(Translation translation, User user) {
    String emailId = user.getEmail();
    String aclUrl = DOC_ACL_URL + translation.getToolkitDocIdTail();

    boolean failed = true;
    emailId = emailId.toLowerCase();

    String aclFeedUrl = aclUrl + '/' + emailId;
    // TODO: have this retry a few times if it fails
    try {
      service.delete(new URL(aclFeedUrl));
      logger.info("Unshare with user " + emailId);
    } catch (ServiceException e) {
      logger.severe("Error unsharing with user " + emailId);
    } catch (IOException e) {
      logger.severe("Error unsharing with user " + emailId);
    }
  }
  
  public boolean hasAccess(Translation translation, User user){
    String emailId = user.getEmail();
    String aclUrl = DOC_ACL_URL + translation.getToolkitDocIdTail();
    AclScope scope = new AclScope(AclScope.Type.USER, emailId);
    
    int attempts = 0;
    boolean failed = false;
    emailId = emailId.toLowerCase();
    String FeedUrl = DOC_ACL_URL + translation.getToolkitDocIdTail();
    try {
      AclFeed aclFeed = service.getFeed(new URL(FeedUrl), AclFeed.class);
      for (AclEntry feedEntry : aclFeed.getEntries()) {
        if(feedEntry.getScope().getValue().equals(user.getEmail())){
          failed = true;
        }
      }
    } catch (ServiceException e) {
      logger.severe("Unable to retrieve the access role for the document " +
          translation.getId() +"  to user " + user.getUserId() );
      return false;
    } catch (IOException e) {
      logger.severe("Error in retrieving the access for " + emailId);
      return false;
    }
    return failed;
  }
  
  private DocumentEntry uploadHtmlDocument(
      String sourceLangCode, String targetLangCode, String title, String articleUrl,
      String htmlContent)
      throws IOException, ServiceException {
    
    if (service == null) {
      return null;
    } else {
      DocumentEntry entry = new DocumentEntry();
      entry.setSourceLanguage(new SourceLanguage(sourceLangCode));
      entry.setTargetLanguage(new TargetLanguage(targetLangCode));
      entry.setTitle(new PlainTextConstruct(title));
      if (htmlContent != null) {
        FakeFileSource fakeFileSource = new FakeFileSource(htmlContent);
        entry.setMediaSource(fakeFileSource);
      } else {
        DocumentSource.Type type = 
            isWikipediaArticle(articleUrl) ? DocumentSource.Type.WIKI : DocumentSource.Type.HTML;
        DocumentSource docSource = new DocumentSource(type, articleUrl);
        entry.setDocumentSource(docSource);
      }
      
      setGlossary(entry, glossaryId);
      setTranslationMemory(entry, translationMemoryId);
      
      URL feedUrl = new URL(DOC_FEED_URL);
      return service.insert(feedUrl, entry);     
    }
  }
  
  // returns true if the given URL points to a Wikipedia article
  private boolean isWikipediaArticle(String articleUrl) {
    articleUrl.startsWith("http://en.wikipedia.org/");
    return true;
  }
  
  private void setGlossary(DocumentEntry entry, String glossaryId) {
    if (glossaryId != null && !glossaryId.isEmpty()) {
      GlossariesElement glossariesElement = new GlossariesElement();
      String url = GLOSSARY_URL + glossaryId;
      Link link = new Link();
      link.setHref(url);

      glossariesElement.addLink(link);
      entry.setGlossary(glossariesElement);
    }
  }

  // Its fine for the empty string to fail here, instead of using the global TM
  private void setTranslationMemory(DocumentEntry entry, String tmId) {
    if (tmId != null) {
      TmsElement tm = new TmsElement();
      String url = MEMORY_URL + tmId;
      Link tmLink = new Link();
      tmLink.setHref(url);

      tm.addLink(tmLink);
      entry.setTranslationMemory(tm);
    }
  }
  
  // TODO: this doesn't work yet -- it throws a ServiceException
  public String tryToReadBackTheTranslatedContent(DocumentEntry de) {
    String docUrl = de.getEditLink().getHref();
    String exportUrl = docUrl.replace("feeds/documents/", "feeds/documents/export/");
    MediaContent mediaContext = new MediaContent();
    mediaContext.setUri(exportUrl);
    StringBuilder builder = new StringBuilder();
    try {
      MediaSource ms = service.getMedia(mediaContext);
      InputStream inStream = ms.getInputStream();
      Reader reader = new InputStreamReader(inStream, "UTF-8");
      int read;
      do {
        final char[] buffer = new char[0x10000];
        read = reader.read(buffer, 0, buffer.length);
        if (read > 0) {
          builder.append(buffer, 0, read);
        }
      } while (read >= 0);
    } catch (IOException e) {
      builder.append("IOException");
    } catch (ServiceException e) {
      builder.append("ServiceException");
    }
      
    return builder.toString();
  }
  
  private GttService signIntoGttService() {
    TranslatorToolkitSettings settings = new TranslatorToolkitSettings();
    settings.readConfigFile();
    String gttAppName = settings.getGttAppName();
    String userName = settings.getUserName();
    String password = settings.getPassword();
    glossaryId = settings.getGlossaryId();
    translationMemoryId = settings.getTranslationMemoryId();
    
    GttService service = new GttService(gttAppName);
    try {
      service.setUserCredentials(userName, password);
      return service;
    } catch (AuthenticationException e) {
      logger.log(
          Level.SEVERE, "Error: AuthenticationException from GttService for user " + userName, e);
      return null;
    }
  }

}
