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

// TODO: we should consider removing this dependency on "model"
import com.google.dotorg.translation_workflow.model.LexiconTerm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class LexiconFileReader {
  ResourceFileReader reader;

  public LexiconFileReader(String configFile) throws FileNotFoundException {
    reader = new ResourceFileReader(configFile);
  }
  
  public HashMap<String, LexiconTerm> readTerms(String configFile) throws IOException {
    // TODO: replace this with cleaner code using Pattern / Matcher
    //   String regxForTerm = "\\<bltkw>(.*?)\\</bltkw>";
    //   Pattern patternFormTerm = Pattern.compile(regxForTerm);
    //   Matcher matcher = patternFormTerm.matcher(line);
    //   while (matcher.find()) {
    //     term.term = matcher.group(1);
    //   }
    HashMap<String, LexiconTerm> terms = new HashMap<String, LexiconTerm>();
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.startsWith("<ar")) {
        line += getRemainderOfElement("ar");
        LexiconTerm term = new LexiconTerm();
        List<String> definitionList = new ArrayList<String>();
        term.termId = line.substring(8, 16); // TODO: fix the ugly hack
        term.term = getSnippet(line, "k");
        term.partOfSpeech = getSnippet(line, "pos");
        int fromIndex = 0;
        int nextFromIndex;
        while ((nextFromIndex = hasNextSnippet(line, "dtrn", fromIndex)) != -1) {
          definitionList.add(getSnippet(line, "dtrn", fromIndex));
          fromIndex = nextFromIndex;
        }
        term.definitions = definitionList.toArray(new String[definitionList.size()]);
        terms.put(term.termId, term);
      }
    }
    return terms;
  }
  
  private String getRemainderOfElement(String tag) throws IOException {
    String remainder = "";
    String line;
    while (((line = reader.readLine()) != null) && !line.trim().startsWith(endTag(tag))) {
      remainder += line;
    }
    return remainder;
  }

  private int hasNextSnippet(String line, String tag, int fromIndex) {
    int start = line.indexOf(startTag(tag), fromIndex);
    return (start == -1) ? -1 : line.indexOf(endTag(tag), start);
  }

  private String getSnippet(String line, String tag, int fromIndex) {
    int foundAt = line.indexOf(startTag(tag), fromIndex);
    int start = foundAt + startTag(tag).length();
    return (foundAt == -1) ? "" : line.substring(start, line.indexOf(endTag(tag), start));
  }
  
  private String startTag(String tag) {
    return "<" + tag + ">";
  }
  
  private String endTag(String tag) {
    return "</" + tag + ">";
    }
  
  private String getSnippet(String line, String tag) {
    return getSnippet(line, tag, 0);
  }

}
