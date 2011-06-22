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

import com.google.gdata.util.common.base.Nullable;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
public class SimpleDigest {
  private static final String STRING_ENCODING = "UTF-8";
  private static final String HASH_ALGORITHM = "MD5";
  
  private String outerSalt;
  
  public SimpleDigest() {
    this(null);
  }
  
  public SimpleDigest(@Nullable String outerSalt) {
    this.outerSalt = outerSalt;
  }
  
  /**
   * Given a string, returns a hashed digest for that string.
   */
  public String digest(String foo, @Nullable String innerSalt) {
    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      return null;
    }
    
    messageDigest.reset();
    try {
      if (outerSalt != null) {
        messageDigest.update(outerSalt.getBytes(STRING_ENCODING));
      }
      if (innerSalt != null) {
        messageDigest.update(innerSalt.getBytes(STRING_ENCODING));
      }
      messageDigest.update(foo.getBytes(STRING_ENCODING));
    } catch (UnsupportedEncodingException e) {
      return null;
    }
    
    byte[] digestBytes = messageDigest.digest();
    return encodedStringValue(digestBytes);
  }
  
  /**
   * Given a byte array such as {127, -128, 0, 1, 2, 3}, returns 
   * an encoded string value such as "1doxcsgd1f";
   */
  public static String encodedStringValue(byte[] byteArray) {
    int positiveNumber = 1;
    BigInteger bigInt = new BigInteger(positiveNumber, byteArray);
    return bigInt.toString(Character.MAX_RADIX);
  }
}
