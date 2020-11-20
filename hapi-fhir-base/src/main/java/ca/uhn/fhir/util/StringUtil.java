package ca.uhn.fhir.util;

/*-
 * #%L
 * HAPI FHIR - Core Library
 * %%
 * Copyright (C) 2014 - 2020 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.CharArrayWriter;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.StringTokenizer;

public class StringUtil {

	/**
	 * If a string ends with a given character, remove that character from the end of the string (as many times as it occurs at the end)
	 */
	public static String chompCharacter(String theInput, char theCharacter) {
		String retVal = theInput;
		while (retVal != null && retVal.length() > 0 && retVal.charAt(retVal.length() - 1) == theCharacter) {
			retVal = retVal.substring(0, retVal.length() - 1);
		}
		return retVal;
	}

	public static String normalizeStringForSearchIndexing(String theString) {
		if (theString == null) {
			return null;
		}

		CharArrayWriter outBuffer = new CharArrayWriter(theString.length());

		/*
		 * The following block of code is used to strip out diacritical marks from latin script
		 * and also convert to upper case. E.g. "j?mes" becomes "JAMES".
		 *
		 * See http://www.unicode.org/charts/PDF/U0300.pdf for the logic
		 * behind stripping 0300-036F
		 *
		 * See #454 for an issue where we were completely stripping non latin characters
		 * See #832 for an issue where we normalize korean characters, which are decomposed
		 */
		String string = Normalizer.normalize(theString, Normalizer.Form.NFD);
		for (int i = 0, n = string.length(); i < n; ++i) {
			char c = string.charAt(i);
			if (c >= '\u0300' && c <= '\u036F') {
				continue;
			} else {
				outBuffer.append(c);
			}
		}

		return new String(outBuffer.toCharArray()).toUpperCase();
	}

	public static String toUtf8String(byte[] theBytes) {
		byte[] bytes = theBytes;
		if (theBytes.length >= 3) {
			if (theBytes[0] == -17 && theBytes[1] == -69 && theBytes[2] == -65) {
				bytes = Arrays.copyOfRange(theBytes, 3, theBytes.length);
			}
		}
		return new String(bytes, StandardCharsets.UTF_8);
	}

	/**
	 * Check the input string has prefix token 
	 * 
	 * e.g. For a display text of “Body height”:
	 *	searching on "Bo" or "he" should find it
	 *	searching on "ei" or "dy" should not find it
	 * 
	 * @param theInput       the input string
	 * @param thePrefix      the prefix string of a token
	 * @return Return true if a input string token separated by space start with the prefix 
	 */
	public static boolean isStartsWithIgnoreCase(final String theInput, final String thePrefix) {
		
		if (theInput == null || thePrefix == null)
			return false;
		
		StringTokenizer tok = new StringTokenizer(theInput);
		while (tok.hasMoreTokens()) {
			if (org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(tok.nextToken(), thePrefix))
				return true;
		}
		
		return false;
	}
}
