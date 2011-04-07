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

package com.google.dotorg.translation_workflow;

import com.google.dotorg.translation_workflow.io.ResourceFileReader;

import java.io.FileNotFoundException;
import java.io.IOException;

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
public class Website {
  private static Website website;
  private static String configFile = "WEB-INF/content-config/site-name-and-logo.csv";
  private String siteName = null;
  private String siteLogo = null;
  
  private Website() {
    try {
      ResourceFileReader fileReader = new ResourceFileReader(configFile);
      fileReader.skipCsvHeader();
      String[] fields = fileReader.readCsvLine();
      siteName = fields[0];
      siteLogo = fields[1];
    } catch (FileNotFoundException e) {
      // pass
    } catch (IOException e) {
      // pass
    }
  }
  
  public static Website getInstance() {
    if (website == null) {
      website = new Website();
    }
    return website;
  }
  
  public String getName() {
    return siteName;
  }
  
  public String getLogoFilename() {
    return siteLogo;
  }
}
