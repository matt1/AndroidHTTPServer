package org.matt1.http.workers.simple;

import org.matt1.http.utils.HttpMethod;


/**
 * <p>
 * A "request" package used to pass details of a request down to a worker for processing. 
 * </p>
 * @author Matt
 *
 */
public class SimpleRequest {

	/** The method for this request, e.g. "GET" etc */
	private HttpMethod mMethod;
	
	/** The requested URI for this request - might or might not be an actual file! */
	private String mResource;
	
	/**
	 * <p>
	 * Creates a new Work Package for a worker to use to process the request.
	 * </p>
	 * @param pMethod
	 * @param pResource
	 */
	public SimpleRequest(HttpMethod pMethod, String pResource) {
		mMethod = pMethod;
		mResource = pResource;
	}

	/**
	 * <p>
	 * Gets the HTTP method for this request
	 * </p>
	 * @return
	 */
	public HttpMethod getMethod() {
		return mMethod;
	}

	/**
	 * <p>
	 * Sets the HTTP method used for this request
	 * </p>
	 * 
	 * @param pResponse
	 */
	public void setMethod(HttpMethod pMethod) {
		mMethod = pMethod;
	}

	/**
	 * <p>
	 * Gets the resource requested for this request
	 * </p>
	 * @return
	 */
	public String getResource() {
		return mResource;
	}

	/**
	 * <p>
	 * Sets the resource requested
	 * </p>
	 * 
	 * @param pResponse
	 */
	public void setResource(String pResource) {
		mResource = pResource;
	}
	
	
}
