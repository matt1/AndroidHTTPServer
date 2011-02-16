package org.matt1.http.utils;

/**
 * <p>
 * HTTP Response codes
 * </p>
 * @author Matt
 *
 */
public enum HttpStatus {
	
	/** Ok */
	HTTP200("200 - OK"),
	
	/** Bad request */
	HTTP400("400 - Bad Request"),
	
	/** Unauthorised */
	HTTP401("401 - Unauthorised"),
	
	/** Forbidden */
	HTTP403("402 - Forbidden"),
	
	/** Not found */
	HTTP404("404 - Not Found"),
	
	/** Method not allowed */
	HTTP405("405 - Method Not Allowed"),
	
	/** I'm a teapot - yep, really {@link http://tools.ietf.org/html/rfc2324} */
	HTTP418("418 - I'm a teapot"),
	
	/** Internal server error */
	HTTP500("500 - Internal Server Error"),
	
	/** Not implemented */
	HTTP501("501 - Not Implemented"), 
	
	/** Service unavailable */
	HTTP503("503 - Service unavailable");
	
	private String mDescription;
	
	HttpStatus(String pDescription) {
		mDescription = pDescription;
	}
	
	/**
	 * <p>
	 * Gets the textual description of a status code
	 * </p>
	 * @return
	 */
	public String getDescription() {
		return mDescription;
	}
	
}
