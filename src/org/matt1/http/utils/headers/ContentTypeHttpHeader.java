package org.matt1.http.utils.headers;

/**
 * <p>
 * Type for setting the content type of a response
 * </p>
 * @author Matt
 *
 */
public class ContentTypeHttpHeader extends HttpHeader {

	
	/** Constants for Android string optimisations */
	private static final String HEADER_CONTENT_TYPE = "Content-type";
	
	public ContentTypeHttpHeader(String pType) {
		super(HEADER_CONTENT_TYPE, pType);
	}
	
}
