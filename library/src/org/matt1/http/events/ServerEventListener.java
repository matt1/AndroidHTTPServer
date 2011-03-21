package org.matt1.http.events;

/**
 * <p>
 * Listener interface for Server events.  Hosting applications can listen to these events to provide
 * feedback etc
 * </p>
 * @author Matt
 *
 */
public interface ServerEventListener {

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
	
	/**
	 * <p>
	 * The server is ready to start handling requests.
	 * </p>
	 */
	public abstract void onServerReady(String pAddress, int pPort);
	
}
