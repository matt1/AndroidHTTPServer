package org.matt1.utils;
/**
 * <p>
 * So according to the author (???) of some of the android classes, getBytes() is very slow on anything prior
 * to Android 2.3 (http://code.google.com/p/android/issues/detail?id=14383#c4) and so the suggestion is to do it
 * yourself!
 * </p>
 * @author Matt
 *
 */
public class ByteUtils {

	public static byte[] getBytesFromString(String pInput) {
		
		byte[] result = new byte[pInput.length()];
		char[] characters = pInput.toCharArray();
		int position = 0;
		
		for (char c : characters)  {
			result[position] = (byte) c;
			position++;
		}
		
		return result;
	}
	
}
