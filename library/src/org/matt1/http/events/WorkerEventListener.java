package org.matt1.http.events;

/**
 * <p>
 * Listener interface for Worker events.  The Server type will listen for these events.
 * </p>
 * @author Matt
 *
 */
public interface WorkerEventListener {

	/**
	 * <p>
	 * A resource was successfully served
	 * </p>
	 * @param pResource
	 */
	public abstract void onRequestServed(String pResource);
	
	/**
	 * <p>
	 * An error was encountered when trying to serve a resource
	 * </p>
	 * @param pResource
	 */
	public abstract void onRequestError(String pResource);
}
