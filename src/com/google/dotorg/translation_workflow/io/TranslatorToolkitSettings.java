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

import java.io.FileNotFoundException;
import java.io.IOException;
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
 */
public class TranslatorToolkitSettings {
  public static final String NO_GLOSSARY_KEYWORD = "NONE";
  public static final String PRODUCTION_TRANSLATION_MEMORY_KEYWORD = "PRODUCTION";
  
  private static final Logger logger = Logger.getLogger(TranslatorToolkitUtil.class.getName());

  private String gttAppName;
  private String userName;
  private String password;
  private String glossaryId;
  private String translationMemoryId;

  public void readConfigFile() {
    String configFile = "WEB-INF/content-config/translator-toolkit-account.csv";
    ResourceFileReader reader;
    try {
      reader = new ResourceFileReader(configFile);
    } catch (FileNotFoundException e) {
      logger.log(Level.SEVERE, "Error: FileNotFoundException reading " + configFile, e);
      return;
    }
    String[] fields;
    try {
      reader.skipCsvHeader();
      fields = reader.readCsvLine();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error: IOException reading " + configFile, e);
      return;
    }
    gttAppName = fields[0];
    userName = fields[1];
    password = fields[2];
    
    // NO_GLOSSARY_KEYWORD or empty string will invoke toolkit without glossary.
    if (NO_GLOSSARY_KEYWORD.equals(glossaryId)) {
      glossaryId = null;
    }
    
    // PRODUCTION_TRANSLATION_MEMORY_KEYWORD will cause toolkit to use global 
    // translation memory (which is what happens when no TM is loaded). We want 
    // the empty string to fail, so we don't accidentally use the global TM 
    // during development.
    if (PRODUCTION_TRANSLATION_MEMORY_KEYWORD.equals(translationMemoryId)) {
      translationMemoryId = null;
    }
  }
  
  public String getGttAppName() {
    return gttAppName;
  }

  public String getUserName() {
    return userName;
  }
  
  public String getPassword() {
    return password;
  }
  
  public String getGlossaryId() {
    return glossaryId;
  }
  
  public String getTranslationMemoryId() {
    return translationMemoryId;
  }
  
}
