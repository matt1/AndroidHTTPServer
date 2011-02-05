package org.matt1.http.utils;

/**
 * <p>
 * Standard Server HTTP header response
 * </p>
 * @author Matt
 *
 */
public class ServerHTTPHeader extends HTTPHeader {

	public String toString() {
		return "Server: AndroidHTTPServer (android/linux)" + System.getProperty("line.separator");
	}
	
}
