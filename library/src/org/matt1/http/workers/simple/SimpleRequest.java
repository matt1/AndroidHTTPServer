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

	private HttpMethod mMethod;
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

	public HttpMethod getMethod() {
		return mMethod;
	}

	public void setMethod(HttpMethod pMethod) {
		mMethod = pMethod;
	}

	public String getResource() {
		return mResource;
	}

	public void setResource(String pResource) {
		mResource = pResource;
	}
	
	
}
