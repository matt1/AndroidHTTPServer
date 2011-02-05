package org.matt1.http.utils;

/**
 * <p>
 * Type for setting the content type of a response
 * </p>
 * @author Matt
 *
 */
public class ContentTypeHTTPHeader extends HTTPHeader {

	public ContentTypeHTTPHeader(String pType) {
		super("Content-type", pType);
	}
	
}
