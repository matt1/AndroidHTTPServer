package org.matt1.http.utils.headers;

import java.util.Date;

/**
 * <p>
 * Standard date HTTP header response
 * </p>
 * @author Matt
 *
 */
public class DateHttpHeader extends HttpHeader {

	public String toString() {
		return "Date: " + String.valueOf(new Date()) + System.getProperty("line.separator");
	}
	
}
