package org.matt1.http.utils;

import java.util.Date;

/**
 * <p>
 * Standard date HTTP header response
 * </p>
 * @author Matt
 *
 */
public class DateHTTPHeader extends HTTPHeader {

	public String toString() {
		return "Date: " + String.valueOf(new Date()) + System.getProperty("line.separator");
	}
	
}
