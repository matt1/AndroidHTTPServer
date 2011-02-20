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
	
	private HttpStatus mStatus;
	
	public HttpStatus getStatus() {
		return mStatus;
	}
	
	public SimpleWorkerException(HttpStatus pStatus) {
		super();
		mStatus = pStatus;
	}
	
	public SimpleWorkerException(HttpStatus pStatus, String pMessage) {
		super(pMessage);
		mStatus = pStatus;
	}
	
	public SimpleWorkerException(HttpStatus pStatus, Exception pCause) {
		super(pCause);
		mStatus = pStatus;
	}
	
	public SimpleWorkerException(HttpStatus pStatus, String pMessage, Exception pCause) {
		super(pMessage, pCause);
		mStatus = pStatus;
	}
	
	
}
