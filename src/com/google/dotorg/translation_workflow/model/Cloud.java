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
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;

// TODO: we should remove these dependencies from "model" to "io"
import com.google.dotorg.translation_workflow.io.LexiconFileReader;
import com.google.dotorg.translation_workflow.io.ResourceFileReader;
import com.google.dotorg.translation_workflow.io.TranslatorToolkitUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

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
 */
public class Cloud {
  private static final Logger logger = Logger.getLogger(Cloud.class.getName());
  
  /* "transactions-optional" is a key value in our jdoconfig.xml */
  private static final PersistenceManagerFactory pmfInstance =
    JDOHelper.getPersistenceManagerFactory("transactions-optional");

  private PersistenceManager pm;
  private List<Language> allLanguages;
  private HashMap<String, LexiconTerm> lexiconTerms;
  
  private Cloud() {}
  
  private PersistenceManager getPersistenceManager() {
    return pmfInstance.getPersistenceManager();
  }
  
  public static Cloud open() {
    Cloud cloud = new Cloud();
    cloud.pm = cloud.getPersistenceManager();
    return cloud;
  }
  
  public void close() {
    pm.close();
  }

  public <T> T createRecord(Class<T> clazz) {
    T instance = null;
    try {
      instance = clazz.newInstance();
    } catch (IllegalAccessException e) {
      // pass
    } catch (InstantiationException e) {
      // pass
    }
    pm.makePersistent(instance);
    return instance;
  }
  
  public <T> T getRecordById(Class<T> clazz, int recordId) {
    T record = null;
    if (recordId != 0) {
      Key k = KeyFactory.createKey(clazz.getSimpleName(), recordId);
      record = pm.getObjectById(clazz, k);
    }
    return record;
  }

  public <T> T getRecordById(Class<T> clazz, String recordId) {
    if ((recordId == null) || (recordId.isEmpty())) {
      return null;
    } else {
      return getRecordById(clazz, Integer.parseInt(recordId));
    }
  }
  
  public Translation getTranslationByIds(String projectId, String translationId) {
    Key key = new KeyFactory.Builder(Project.class.getSimpleName(), Integer.parseInt(projectId))
      .addChild(Translation.class.getSimpleName(), Integer.parseInt(translationId)).getKey();
    return pm.getObjectById(Translation.class, key);
  }

  public Translation getTranslationByKeyString(String keyString) {
    Key key = KeyFactory.createKey(Translation.class.getSimpleName(), keyString);
    Translation translation = pm.getObjectById(Translation.class, key);
    return translation;
  }

  public List<Language> getAllLanguages() {
    if (allLanguages == null) {
      allLanguages = readLanguagesFromConfigFile();
    }
    return allLanguages;
  }

  public HashMap<String, LexiconTerm> getAllLexiconTerms() {
    if (lexiconTerms == null) {
      lexiconTerms = readLexiconTermsFromConfigFile();
    }
    return lexiconTerms;
  }
  
  private List<Language> readLanguagesFromConfigFile() {
    List<Language> languages = new ArrayList<Language>();
    
    String languageFile = "WEB-INF/content-config/languages.csv";
    ResourceFileReader languageFileReader;
    try {
      languageFileReader = new ResourceFileReader(languageFile);
    } catch (FileNotFoundException e) {
      logger.log(Level.SEVERE, "Error: FileNotFoundException reading " + languageFile, e);
      return languages;
    }
    
    String groupsFile = "WEB-INF/content-config/discussion-groups.csv";
    ResourceFileReader groupsFileReader;
    try {
      groupsFileReader = new ResourceFileReader(groupsFile);
    } catch (FileNotFoundException e) {
      logger.log(Level.SEVERE, "Error: FileNotFoundException reading " + groupsFile, e);
      return languages;
    }
    
    try {
      languageFileReader.skipCsvHeader();
      String[] fields;
      while ((fields = languageFileReader.readCsvLine()) != null) {
        Language language = new Language();
        language.setCode(fields[0]);
        language.setName(fields[1]);
        languages.add(language);
      }
    } catch (IOException e) {
      logger.severe("Error: IOException reading " + languageFile);
      return languages;
    }
    
    try {
      groupsFileReader.skipCsvHeader();
      String[] fields;
      while ((fields = groupsFileReader.readCsvLine()) != null) {
        String languageCode = fields[0];
        String groupsUrl = fields[1];
        for (Language language : languages) {
          if (language.getCode().equals(languageCode)) {
            language.setDiscussionLink(groupsUrl);
          }
        }
      }
    } catch (IOException e) {
      logger.severe("Error: IOException reading " + groupsFile);
      return languages;
    }
    
    return languages;
  }
  
  private HashMap<String, LexiconTerm> readLexiconTermsFromConfigFile() {
    String configFile = "WEB-INF/content-config/lexicon.xml";
    LexiconFileReader reader;
    HashMap<String, LexiconTerm> results = null;
    try {
      reader = new LexiconFileReader(configFile);
    } catch (FileNotFoundException e) {
      logger.log(Level.SEVERE, "Error: FileNotFoundException reading " + configFile, e);
      return new HashMap<String, LexiconTerm>();
    }
    try {
      return reader.readTerms(configFile);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error: IOException reading " + configFile, e);
      return new HashMap<String, LexiconTerm>();
    }
  }
  
  @SuppressWarnings("unchecked")
  private <T> List<T> getAllInstances(Class<T> clazz) {
    String query = "select from " + clazz.getName();
    List<T> instances = (List<T>) pm.newQuery(query).execute();
    return instances;
  }

  public List<Project> getAllProjects() {
    return getAllInstances(Project.class);
  }
  
  public List<Volunteer> getAllVolunteers() {
    return getAllInstances(Volunteer.class);
  }
  
  public Project getProjectById(String projectId) {
    return getRecordById(Project.class, projectId);
  }
  
  public Project getProjectById(int projectId) {
    return getRecordById(Project.class, projectId);
  }

  public Volunteer getVolunteerByUser(User user) {
    return getVolunteerByUserId(user.getUserId());
  }
  
  public Volunteer getVolunteerByUserId(String userId) {
    Key key = KeyFactory.createKey(Volunteer.class.getSimpleName(), userId);
    Volunteer volunteer = null;
    try {
      volunteer = pm.getObjectById(Volunteer.class, key);
    } catch (JDOObjectNotFoundException e) {
      return null;
    }
    return volunteer;
  }
  
  public List<String> getAllVolunteerNicknames() {
    List<String> returnValues = new ArrayList<String>();
    for (Volunteer volunteer : getAllVolunteers()) {
      String nickname = volunteer.getNickname();
      if ((nickname != null) && !nickname.isEmpty()) {
        returnValues.add(nickname);
      }
    }
    return returnValues;
  }
  
  public LexiconTerm getLexiconTermByTermId(String termId) {
    return getAllLexiconTerms().get(termId);
  }
  
  public Language getLanguageByCode(String languageCode) {
    // TODO: this could be done in O(1) if speed were important
    for (Language language : getAllLanguages()) {
      if (language.getCode().equals(languageCode)) {
        return language;
      }
    }
    return null;
  }
  
  public List<Project> getProjectsForLanguage(String languageCode) {
    List<Project> selectedProjects = new ArrayList<Project>();
    for (Project project : getAllProjects()) {
      if (project.includesLanguage(languageCode)) {
        selectedProjects.add(project);
      }
    }
    return selectedProjects;
  }

  public List<Project> getProjectsForUser(User user) {
    List<Project> selectedProjects = new ArrayList<Project>();
    Volunteer volunteer = getVolunteerByUser(user);
    if (volunteer != null) {
      List<String> volunteerLanguages = volunteer.getLanguages();
      for (Project project : getAllProjects()) {
        if (volunteerLanguages.contains(project.getLanguage())) {
          selectedProjects.add(project);
          refreshTranslationStatusFromToolkit(user, project);
        }
      }
    }
    return selectedProjects;
  }
  
  public void refreshTranslationStatusFromToolkit(User user, Project project) {
    TranslatorToolkitUtil toolkit = new TranslatorToolkitUtil();
    List<Translation> translations = project.getTranslationItemsForUser(user);
    for (Translation translation : translations) {
      toolkit.refreshStatus(translation);
    }
  }
  
  public Project createProject() {
    return createRecord(Project.class);
  }
  
  public Volunteer createVolunteer(User user) {
    Key key = KeyFactory.createKey(Volunteer.class.getSimpleName(), user.getUserId());
    Volunteer instance = new Volunteer();
    instance.setKey(key);
    pm.makePersistent(instance);
    return instance;
  }
  
}
