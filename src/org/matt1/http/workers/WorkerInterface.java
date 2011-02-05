package org.matt1.http.workers;

import java.io.File;
import java.net.Socket;

/**
 * <p>
 * Interface that all HTTP workers will need to implement to be able to respond to requests to serve a file.
 * </p>
 * @author Matt
 *
 */
public interface WorkerInterface {

	/**
	 * <p>
	 * Initialise a worker ready to begin handling a request.
	 * </p>
	 * @param pSocket Socket to serve
	 * @param pRootDirectory Root directory that this worker should operate from
	 */
	public void InitialiseWorker(Socket pSocket, File pRootDirectory);
	
	/**
	 * <p>
	 * Initialise a worker ready to begin handling a request.
	 * </p>
	 * @param pSocket Socket to serve
	 * @param pTimeout The amount of time to wait before timing out.
	 * @param pRootDirectory Root directory that this worker should operate from
	 */
	public void InitialiseWorker(Socket pSocket, int pTimeout, File pRootDirectory);
	
	/**
	 * <p>
	 * Gets the worker to start processing the request.
	 * </p>
	 */
	public void run();
	
}
