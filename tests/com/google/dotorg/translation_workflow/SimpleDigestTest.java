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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests the {@link SimpleDigest} class
 * 
 * @author Brian Douglas Skinner
 */
public class SimpleDigestTest {
  @Test
  public void testEncodedStringValue_sixBytes() {
    byte[] byteArray = new byte[] {127, -128, 0, 1, 2, 3};
    String result = SimpleDigest.encodedStringValue(byteArray);
    assertEquals("1doxcsgd1f", result);
  }
  
  @Test
  public void testEncodedStringValue_emptyBytes() {
    byte[] byteArray = new byte[] {};
    String result = SimpleDigest.encodedStringValue(byteArray);
    assertEquals("0", result);
  }
  
  @Test
  public void testEncodedStringValue_zeroByte() {
    byte[] byteArray = new byte[] {0};
    String result = SimpleDigest.encodedStringValue(byteArray);
    assertEquals("0", result);
  }
  
  @Test
  public void testEncodedStringValue_oneByte() {
    byte[] byteArray = new byte[] {1};
    String result = SimpleDigest.encodedStringValue(byteArray);
    assertEquals("1", result);
  }
  
  @Test
  public void testDigest_unsaltedFox() {
    String outerSalt = null;
    SimpleDigest token = new SimpleDigest(outerSalt);
    String result = token.digest("the quick brown fox", null);
    assertEquals("2wbxh2mu7eoh2j1ukr027zkf8", result);
  }
  
  @Test
  public void testDigest_simplerUnsaltedFox() {
    SimpleDigest token = new SimpleDigest();
    String result = token.digest("the quick brown fox", null);
    assertEquals("2wbxh2mu7eoh2j1ukr027zkf8", result);
  }

  @Test
  public void testDigest_saltedFox() {
    String outerSalt = "outerSalt";
    SimpleDigest token = new SimpleDigest(outerSalt);
    String result = token.digest("the quick brown fox", "innerSalt");
    assertEquals("3gyn1e82hbm7uknbgc7lsyui9", result);
  }

  @Test (expected=NullPointerException.class)
  public void testDigest_unsaltedNull() {
    String outerSalt = null;
    SimpleDigest token = new SimpleDigest(outerSalt);
    String result = token.digest(null, null);
  }

  @Test
  public void testDigest_unsaltedEmptyString() {
    String outerSalt = null;
    SimpleDigest token = new SimpleDigest(outerSalt);
    String result = token.digest("", null);
    assertEquals("ck2u8j60r58fu0sgyxrigm3cu", result);
  }
  
}
