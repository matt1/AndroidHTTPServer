package org.matt1.http.utils;

/**
 * <p>
 * Represents a HTTP Header, e.g. "Content-type: text/html"
 * </p>
 * @author Matt
 *
 */
public class HTTPHeader {

	private String mKey;
	
	private String mValue;

	/**
	 * <p>
	 * Creates a new HTTP Header element.
	 * </p>
	 */
	public HTTPHeader() {

	}
	
	/**
	 * <p>
	 * Creates a new HTTP Header element.
	 * </p>
	 * @param pKey The key to use (do not include the colon character)
	 * @param pValue The value to use
	 */
	public HTTPHeader(String pKey, String pValue) {
		mKey = pKey;
		mValue = pValue;
	}
	
	/**
	 * <p>
	 * Get the header value
	 * </p>
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(mKey).append(": ").append(mValue).append(System.getProperty("line.separator"));
		return buffer.toString();
	}
	
	/**
	 * <p>
	 * Get the header value as a byte array
	 * </p>
	 */
	public byte[] getBytes() {
		return toString().getBytes();
	}
	
	public String getKey() {
		return mKey;
	}

	public void setKey(String pKey) {
		mKey = pKey;
	}

	public String getValue() {
		return mValue;
	}

	public void setValue(String pValue) {
		mValue = pValue;
	}
	
	
	
}
