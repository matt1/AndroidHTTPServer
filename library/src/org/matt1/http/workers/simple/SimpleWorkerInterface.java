package org.matt1.http.workers.simple;


/**
 * <p>
 * An interface for simple workers
 * </p>
 * @author Matt
 *
 */
public interface SimpleWorkerInterface {

	/**
	 * <p>
	 * Take a request in the form of a work package and process it, resulting in a response with the 
	 * appropriate details, such as the data and the content type.
	 * </p>
	 * @param pPackage
	 * @return
	 */
	public SimpleResponse handlePackage(SimpleRequest pPackage) throws SimpleWorkerException;
	
}
