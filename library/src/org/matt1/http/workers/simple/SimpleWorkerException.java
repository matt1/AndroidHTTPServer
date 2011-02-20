package org.matt1.http.workers.simple;

import org.matt1.http.utils.HttpStatus;

/**
 * <p>
 * Exception used to communicate HTTP errors during pack worker failures
 * </p>
 * @author Matt
 *
 */
public class SimpleWorkerException extends Exception {

	/** Auto-generated serial ID */
	private static final long serialVersionUID = 6399757752615173636L;
	
	/** HTTP status code related to this exception (e.g. 404 not found, 500 server error etc) */
	private HttpStatus mStatus;
	
	/**
	 * <p>
	 * Gets the HTTP status code assocaited with this exception
	 * </p>
	 * @return
	 */
	public HttpStatus getStatus() {
		return mStatus;
	}
	
	/**
	 * <p>
	 * Creates a new simple worker exception.
	 * </p>
	 * @param pStatus
	 */
	public SimpleWorkerException(HttpStatus pStatus) {
		super();
		mStatus = pStatus;
	}
	
	/**
	 * <p>
	 * Creates a new simple worker exception.
	 * </p>
	 * @param pStatus
	 */
	public SimpleWorkerException(HttpStatus pStatus, String pMessage) {
		super(pMessage);
		mStatus = pStatus;
	}
	
	/**
	 * <p>
	 * Creates a new simple worker exception.
	 * </p>
	 * @param pStatus
	 */
	public SimpleWorkerException(HttpStatus pStatus, Exception pCause) {
		super(pCause);
		mStatus = pStatus;
	}
	
	/**
	 * <p>
	 * Creates a new simple worker exception.
	 * </p>
	 * @param pStatus
	 */
	public SimpleWorkerException(HttpStatus pStatus, String pMessage, Exception pCause) {
		super(pMessage, pCause);
		mStatus = pStatus;
	}
	
}
