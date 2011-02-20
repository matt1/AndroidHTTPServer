package org.matt1.http.workers.simple;

/**
 * <p>
 * Response to a simple request package.  Contains the mimetype and the actual data.
 * </p>
 * @author Matt
 *
 */
public class SimpleResponse {

	private byte[] mResponse;
	private String mMimeType;
	

	public SimpleResponse(String pMimeType, byte[] pResponse) {
		mResponse = pResponse;
		mMimeType = pMimeType;
	}


	public byte[] getResponse() {
		return mResponse;
	}


	public void setResponse(byte[] pResponse) {
		mResponse = pResponse;
	}


	public String getMimeType() {
		return mMimeType;
	}


	public void setMimeType(String pMimeType) {
		mMimeType = pMimeType;
	}

	
	
}
