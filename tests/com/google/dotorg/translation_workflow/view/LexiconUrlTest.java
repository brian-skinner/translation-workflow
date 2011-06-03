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

package com.google.dotorg.translation_workflow.view;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests the {@link LexiconUrl} class
 * 
 * @author Brian Douglas Skinner
 */
public class LexiconUrlTest {
  @Test
  public void testGetTermIdFromUrl() {
    assertEquals("B0028700", LexiconUrl.getTermIdFromUrl(
        "http://localhost:8888/term/B0028700?foo=bar"));
    assertEquals("X1028701", LexiconUrl.getTermIdFromUrl(
        "https://code.google.com/p/translation-workflow/term/X1028701"));
  }
}
