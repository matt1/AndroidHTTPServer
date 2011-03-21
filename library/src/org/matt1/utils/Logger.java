package org.matt1.utils;

import android.util.Log;

/**
 * <p>
 * Facade for standard logger with a given tag
 * </p>
 * @author Matt
 *
 */
public class Logger {

	/** Logger tag */
	private static final String LOGGING_TAG = "matt1.http";
	
	/**
	 * <p>
	 * Logs a debug message
	 * </p>
	 * @param pMessage
	 */
	public static void debug(String pMessage) {
		Log.d(LOGGING_TAG, pMessage);
	}
	
	/**
	 * <p>
	 * Logs an info message
	 * </p>
	 * @param pMessage
	 */
	public static void info(String pMessage) {
		Log.i(LOGGING_TAG, pMessage);
	}
	
	/**
	 * <p>
	 * Logs an error message
	 * </p>
	 * @param pMessage
	 */
	public static void error(String pMessage) {
		Log.e(LOGGING_TAG, pMessage);
	}
	
	/**
	 * <p>
	 * Logs a warning message
	 * </p>
	 * @param pMessage
	 */
	public static void warn(String pMessage) {
		Log.w(LOGGING_TAG, pMessage);
	}
	
}
