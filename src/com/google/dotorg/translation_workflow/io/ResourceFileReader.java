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

import com.google.gdata.util.common.base.Preconditions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
public class ResourceFileReader {
  private BufferedReader buffer;
  
  public ResourceFileReader(String filePath) throws FileNotFoundException {
    FileReader file = new FileReader(filePath);
    buffer = new BufferedReader(file);
  }
  
  public String readLine() throws IOException {
    Preconditions.checkNotNull(buffer);
    return buffer.readLine();
  }
  
  public void skipCsvHeader() throws IOException {
    readCsvLine();
  }
  
  // TODO: consider using java.util.Scanner to do this
  public String[] readCsvLine() throws IOException {
    Preconditions.checkNotNull(buffer);
    String line = buffer.readLine();
    if (line == null) {
      return null;
    } else {
      line = line.replaceAll("\"", "");
      String[] fields = line.split(",");
      return fields;
    }
  }

}
