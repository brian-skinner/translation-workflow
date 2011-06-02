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
import com.google.dotorg.translation_workflow.model.Translation.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

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
  private List<Country> allCountries;
  private List<Language> allLanguages;
  private HashMap<String, LexiconTerm> lexiconTerms;
  
  private Cloud() {}
  
  private static PersistenceManager getNewPersistenceManager() {
    return pmfInstance.getPersistenceManager();
  }
  
  public static Cloud open() {
    Cloud cloud = new Cloud();
    cloud.pm = Cloud.getNewPersistenceManager();
    return cloud;
  }
  
  public void close() {
    pm.close();
  }

  public PersistenceManager getPersistenceManager() {
    return pm;
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
  
  public Translation getTranslationByIds(int projectId, int translationId) {
    Key key = new KeyFactory.Builder(Project.class.getSimpleName(), projectId)
      .addChild(Translation.class.getSimpleName(), translationId).getKey();
    return pm.getObjectById(Translation.class, key);
  }

  public Translation getTranslationByKeyString(String keyString) {
    Key key = KeyFactory.createKey(Translation.class.getSimpleName(), keyString);
    Translation translation = pm.getObjectById(Translation.class, key);
    return translation;
  }
  
  public List<Country> getAllCountries() {
    if (allCountries == null) {
      allCountries = readCountriesFromConfigFile();
    }
    return allCountries;
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
  
  // TODO: refactor to make a base method that can be used by both 
  // readCountriesFromConfigFile and readLanguagesFromConfigFile
  private List<Country> readCountriesFromConfigFile() {
    List<Country> countries = new ArrayList<Country>();

    String countriesFile = "WEB-INF/content-config/countries.csv";
    ResourceFileReader countriesFileReader;
    try {
      countriesFileReader = new ResourceFileReader(countriesFile);
    } catch (FileNotFoundException e) {
      logger.log(Level.SEVERE, "Error: FileNotFoundException reading " + countriesFile, e);
      return countries;
    }
    
    try {
      countriesFileReader.skipCsvHeader();
      String[] fields;
      while ((fields = countriesFileReader.readCsvLine()) != null) {
        Country country = new Country();
        country.setCode(fields[0]);
        country.setName(fields[1]);
        countries.add(country);
      }
    } catch (IOException e) {
      logger.severe("Error: IOException reading " + countriesFile);
      return countries;
    }

    return countries;
  }
  
  // TODO: refactor to make a base method that can be used by both 
  // readCountriesFromConfigFile and readLanguagesFromConfigFile
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
  
  public boolean isNicknameAvailable(String nickname) {
    if (nickname == null || nickname.isEmpty()) {
      return false;
    }
    
    for (Volunteer volunteer : getAllVolunteers()) {
      if (nickname.equals(volunteer.getNickname())) {
        return false;
      }
    }
    return true;
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
      if (project.includesLanguageCode(languageCode)) {
        selectedProjects.add(project);
      }
    }
    return selectedProjects;
  }

  public List<Project> getProjectsForUser(User user) {
    List<Project> selectedProjects = new ArrayList<Project>();
    Volunteer volunteer = getVolunteerByUser(user);
    if (volunteer != null) {
      List<String> volunteerLanguages = volunteer.getLanguageCodes();
      for (Project project : getAllProjects()) {
        if (volunteerLanguages.contains(project.getLanguageCode())) {
          selectedProjects.add(project);
        }
      }
    }
    return selectedProjects;
  }
  
  public void refreshTranslationStatusFromToolkit(User user, Project project) {
    TranslatorToolkitUtil toolkit = new TranslatorToolkitUtil();
    List<Translation> translations = getTranslationItemsForUser(user, project);
    for (Translation translation : translations) {
      toolkit.refreshStatus(translation);
    }
  }
  
  @SuppressWarnings(value = {"unchecked"})
  public List<Translation> getReviewerTranslationsForUser(User user, Project project) {
    List<Translation> translations = null;
    
    Query query = pm.newQuery(Translation.class);
    query.setFilter("reviewerId == userIdParam && project == projectParam");
    query.declareParameters("String userIdParam, String projectParam");
    
    try {
      translations = (List<Translation>) query.execute(user.getUserId(), project);
    } finally {
      query.closeAll();
    }
    
    return translations;
  }
  
  @SuppressWarnings(value = {"unchecked"})
  public List<Translation> getTranslatorTranslationsForUser(User user, Project project) {
    List<Translation> translations = null;
    
    Query query = pm.newQuery(Translation.class);
    query.setFilter("translatorId == userIdParam && project == projectParam");
    query.declareParameters("String userIdParam, String projectParam");
    
    try {
      translations = (List<Translation>) query.execute(user.getUserId(), project);
    } finally {
      query.closeAll();
    }
  
    return translations;
  }
  
  public List<Translation> getTranslationItemsForTranslator(User user, Project project) {
    List<Translation> translations = getTranslatorTranslationsForUser(user, project);
    List<Translation> returnValues = new ArrayList<Translation>();

    for (Translation translation : translations) {
      Stage stage = translation.getStage();
      if ((stage == Stage.CLAIMED_FOR_TRANSLATION) || 
           (stage == Stage.AVAILABLE_TO_REVIEW) || 
           (stage == Stage.CLAIMED_FOR_REVIEW)) {
        returnValues.add(translation);
      }
    }    
    return returnValues;
  }
  
  public List<Translation> getTranslationItemsForReviewer(User user, Project project) {
    List<Translation> returnValues = new ArrayList<Translation>();
    List<Translation> translations = getReviewerTranslationsForUser(user, project);
    
    for (Translation translation : translations) {
      Stage stage = translation.getStage();
      if (stage == Stage.CLAIMED_FOR_REVIEW) {
        returnValues.add(translation);
      }
    }    
    return returnValues;
  }
  
  public List<Translation> getTranslationItemsForUser(User user, Project project) {
    List<Translation> returnValues = new ArrayList<Translation>();
    
    for (Translation translation : getTranslatorTranslationsForUser(user, project)) {
      returnValues.add(translation);
    }
    
    for (Translation translation: getReviewerTranslationsForUser(user, project)) {
      returnValues.add(translation);
    }
    return returnValues;
  }
  
  public List<Translation> getTranslationItemsCompletedByUser(User user, Project project) {
    List<Translation> returnValues = new ArrayList<Translation>();
    
    for (Translation translation : getTranslationItemsForUser(user, project)) {
      if (translation.getStage() == Stage.COMPLETED) {
        returnValues.add(translation);
      }
    }
    
    return returnValues;
  }
  
  public List<Translation> getAllTranslationItemsToTranslate(Project project) {
    return getTranslationItemsToTranslate(project, -1);
  }

  public List<Translation> getSomeTranslationItemsToTranslate(Project project) {
    int numberOfItemsToReturn = 5;
    return getTranslationItemsToTranslate(project, numberOfItemsToReturn);
  }

  @SuppressWarnings(value = {"unchecked"})
  private List<Translation> getTranslationItemsToTranslate(
      Project project, int numberOfItemsToReturn) {
    List<Translation> translations = null;
    List<Translation> returnValues = new ArrayList<Translation>();
    
    Query query = pm.newQuery(Translation.class);
    query.setFilter("project == projectParam");
    query.declareParameters("String projectParam");
    
    try {
      translations = (List<Translation>) query.execute(project);
    } finally {
      query.closeAll();
    }
    
    if (translations != null) {
      List<Translation> availableItems = new ArrayList<Translation>();
      for (Translation translation : translations) {
        if (!translation.isDeleted() && translation.isAvailableToTranslate()) {
          availableItems.add(translation);
        }
      }

      if (!availableItems.isEmpty()) {
        int startAt;
        if (numberOfItemsToReturn == -1) {
          startAt = 0;
          numberOfItemsToReturn = availableItems.size();
        } else {
          numberOfItemsToReturn = Math.min(numberOfItemsToReturn, availableItems.size());
          int lastPossibleStartingPoint = availableItems.size() - numberOfItemsToReturn;
          Random generator = new Random();
          startAt = generator.nextInt(lastPossibleStartingPoint);
        }
        for (int i = startAt; i < (startAt + numberOfItemsToReturn); i++) {
          returnValues.add(availableItems.get(i));
        }
      }
    }
    
    return returnValues;
  }

  public Project createProject() {
    Project project = createRecord(Project.class);
    project.setDeleted(false);
    return project;
  }
  
  public Volunteer createVolunteer(User user) {
    Key key = KeyFactory.createKey(Volunteer.class.getSimpleName(), user.getUserId());
    Volunteer instance = new Volunteer();
    instance.setKey(key);
    pm.makePersistent(instance);
    return instance;
  }
  
}
