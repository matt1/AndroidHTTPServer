package org.matt1.http.utils.headers;

/**
 * <p>
 * Type for setting the content type of a response
 * </p>
 * @author Matt
 *
 */
public class ContentTypeHttpHeader extends HttpHeader {

	public ContentTypeHttpHeader(String pType) {
		super("Content-type", pType);
	}
	
}
