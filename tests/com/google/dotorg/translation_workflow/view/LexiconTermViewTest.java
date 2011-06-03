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

import com.google.dotorg.translation_workflow.model.LexiconTerm;
import org.junit.Test;

/**
 * Tests the {@link LexiconTermView} class
 * 
 * @author Brian Douglas Skinner
 */
public class LexiconTermViewTest {
  @Test
  public void testGetHtmlSnippet_nullTerm() {
    String expected = "";
    expected += "<div style=\"font-size:large; font-family: arial, helvetica, sans-serif;\">";
    expected += "<p class=\"class=notranslate\">Error: term not found</p>";
    expected += "</div>";
    assertEquals(expected, LexiconTermView.getHtmlSnippet(null));
  }

  @Test
  public void testGetHtmlSnippet_simpleTerm() {
    String[] definitions = {
        "A professional rider of horses in races.",
        "A dealer in horses; a horse trader.",
        "A cheat; one given to sharp practice in trade."};
    LexiconTerm term = new LexiconTerm("J0000001", "jockey", "n.", definitions);
    String expected = "";
    expected += "<div style=\"font-size:large; font-family: arial, helvetica, sans-serif;\">";
    expected += "<h1>jockey</h1>";
    expected += "<p class=\"class=notranslate\"><i>n.</i></p>";
    expected += "<ul>";
    expected += "<li>" + definitions[0] + "</li>";
    expected += "<li>" + definitions[1] + "</li>";
    expected += "<li>" + definitions[2] + "</li>";
    expected += "</ul>";
    expected += "</div>";
    assertEquals(expected, LexiconTermView.getHtmlSnippet(term));
  }
  
}
