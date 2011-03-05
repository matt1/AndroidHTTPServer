package org.matt1.http.workers;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * <p>
 * Factory to create workers for handling requests based on pattern matching the requested resource.
 * </p>
 * @author Matt
 *
 */
public class WorkerFactory {

	/** Stores patterns and classes for handling requests */
	private Map<Pattern, Class<AbstractWorker>> mWorkers = Collections.synchronizedMap(new TreeMap<Pattern, Class<AbstractWorker>>());
	
	/** Static instance */
	private static WorkerFactory mInstance = new WorkerFactory();
	
	/**
	 * <p>
	 * Gets static worker instance
	 * </p>
	 * @return
	 */
	public static WorkerFactory getInstance() {
		return mInstance;
	}
	
	/**
	 * <p>
	 * Private constructor currently does nothing of note
	 * </p>
	 */
	private WorkerFactory() {
		
	}
	
	/**
	 * <p>
	 * Adds a worker to the workers collection.  Sorted by <em>natural order</em> of the patterns!
	 * </p>
	 * @param pPattern
	 * @param pWorkerClass
	 */
	public void addWorker(Pattern pPattern, Class<AbstractWorker> pWorkerClass) {		
		mWorkers.put(pPattern, pWorkerClass);		
	}
	
	/**
	 * <p>
	 * Selects an appropriate worker based on the requested resource.  If nothing suitable is found then the
	 * default SimpleWrokerDispatcher is returned.
	 * </p>
	 * @param pRequest
	 * @return
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public AbstractWorker getWorker(String pRequest) throws IllegalAccessException, InstantiationException {
		
		AbstractWorker result = null;
		Pattern workerPattern;
		
		for (Entry<Pattern, Class<AbstractWorker>> e : mWorkers.entrySet()) {			
			workerPattern = e.getKey();
			if (workerPattern.matcher(pRequest).matches()) {
				result = e.getValue().newInstance();
				break;
			}						
		}
		
		if (result == null) {
			result = SimpleWorkerDispatcher.class.newInstance();
		}
		
		return result;
		
	}
}
