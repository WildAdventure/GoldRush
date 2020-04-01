/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.goldrush.utils;

import java.util.HashMap;
import java.util.Map;

public class Monospace {
	
	//private static final int SPACE_BETWEEN_CHARS = 1;
	private static final int DEFAULT_CHAR_WIDTH = 5;
	
	private static final char SMALLEST = '´';
	private static final char SPACE = ' ';
	
	private static Map<Character, Integer> characters = new HashMap<Character, Integer>();
	static {
		register("#$%&+-/0123456789=?ABCDEFGHJKLMNOPQRSTUVWXYZ\\^_abcdeghjmnopqrsuvwxyzñÑáéóúü", 5);
		register("\"()*<>fk{}", 4);
		register(" I[]t", 3);
		register("'´lí", 2);
	}
	
	
	public static String fill(String input, final int width) {
		int currentLength = 0;
		for (char c : input.toCharArray()) {
			if (characters.containsKey(Character.valueOf(c))) {
				currentLength += characters.get(Character.valueOf(c));
			} else {
				currentLength += DEFAULT_CHAR_WIDTH;
			}
		}
		
		if (currentLength < width) {
			int spacesToAdd = (width - currentLength) / 3;
			currentLength += spacesToAdd * 3;
			input += createStringOfChar(SPACE, spacesToAdd);
			if (currentLength < width) {
				input += createStringOfChar(SMALLEST, width - currentLength);
			}
		}
		
		return input;
	}
	
	private static void register(String chars, int length) {
		for (char c : chars.toCharArray()) {
			characters.put(c, length);
		}
	}
	
	private static String createStringOfChar(char c, int length) {
		char[] array = new char[length];
		for (int i = 0; i < length; i++) {
			array[i] = c;
		}
		return new String(array);
	}
}
