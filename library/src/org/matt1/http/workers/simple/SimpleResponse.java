package org.matt1.http.workers.simple;

/**
 * <p>
 * Response to a simple request package.  Contains the mimetype and the actual data.
 * </p>
 * @author Matt
 *
 */
public class SimpleResponse {

	/** The actual response that will be sent to the client */
	private byte[] mResponse;
	
	/** The mime type of the data that will be sent to the client, e.g. "text/html" etc */
	private String mMimeType;
	
	/**
	 * <p>
	 * Creates a new simple response object
	 * </p>
	 * @param pMimeType
	 * @param pResponse
	 */
	public SimpleResponse(String pMimeType, byte[] pResponse) {
		mResponse = pResponse;
		mMimeType = pMimeType;
	}

	/**
	 * <p>
	 * Gets the response data that is to be sent to the client
	 * </p>
	 * 
	 * @return
	 */
	public byte[] getResponse() {
		return mResponse;
	}

	/**
	 * <p>
	 * Sets the response data to be sent to the client
	 * </p>
	 * 
	 * @param pResponse
	 */
	public void setResponse(byte[] pResponse) {
		mResponse = pResponse;
	}

	/**
	 * <p>
	 * Gets the mime type
	 * </p>
	 * 
	 * @return
	 */
	public String getMimeType() {
		return mMimeType;
	}

	/**
	 * <p>
	 * Sets the mime type
	 * </p>
	 * 
	 * @param pMimeType
	 */
	public void setMimeType(String pMimeType) {
		mMimeType = pMimeType;
	}

}
