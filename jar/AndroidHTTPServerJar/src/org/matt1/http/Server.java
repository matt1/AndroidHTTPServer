package org.matt1.http;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.matt1.http.workers.AbstractWorker;
import org.matt1.utils.Logger;

import android.os.Handler;

public class Server implements Runnable {

	public Handler mHandler;
	
	private static final int INITIAL_THREADS = 3;
	private static final int MAXIMUM_THREADS = 10;
	private static final int QUEUE_TIMEOUT = 30000;
	private static final int MAX_SOCKET_BACKLOG = 80;
	private static final int PORT = 8080;
	
	private static final File mWebRoot = new File("/sdcard/wwwroot");
	
	public static final String SERVER_NAME = "AndroidHTTPServer (android/linux)";
	
	private BlockingQueue<Runnable> queue;
	private ThreadPoolExecutor executorService;

	private ServerSocket mSocket;
	
	private Boolean mRunFlag;
	private InetAddress mInterface;
	
	public Server(InetAddress pInterface) {
		mRunFlag = true;
		mInterface = pInterface;
	}
	
	public void stop() {
		try {
			if (mSocket != null) {
				mSocket.close();
			}
		} catch (IOException e) {
			Logger.debug("IOException closing socket.");
		}
	}
	
	public void run() {
		
		Logger.debug("Server thread initialised!  Attempting to start server.");
		
		//Looper.prepare();
        Socket workerSocket = null;  
		
        if (!mWebRoot.canRead()) {
        	Logger.error("Cannot start server as unable to read root directory at " + mWebRoot.getAbsolutePath());
        	return;
        }
                
        Logger.debug("Server initialised - building thread pool...");
        Logger.debug("Initial threads: " + INITIAL_THREADS);
        Logger.debug("Maximum threads: " + MAXIMUM_THREADS);
        Logger.debug("Queue timeout: " + QUEUE_TIMEOUT + "ms");
        
    	queue = new ArrayBlockingQueue<Runnable>(MAXIMUM_THREADS * 2);
    	executorService = new ThreadPoolExecutor(
    			INITIAL_THREADS, 
    			MAXIMUM_THREADS, 
    			QUEUE_TIMEOUT, 
    			TimeUnit.MILLISECONDS, 
    			queue);

    	Logger.debug("Thread pool constructed, starting server socket...");
        
		try {
			
			mSocket = new ServerSocket(PORT, MAX_SOCKET_BACKLOG, mInterface);
			Logger.debug("Listening on " +  PORT + ".  Ready to serve.");
			
			while (mRunFlag) {							
				workerSocket = mSocket.accept();	
				AbstractWorker worker = AbstractWorker.getWorkerInstance(workerSocket, mWebRoot);
				executorService.execute((Runnable) worker);
				Logger.debug("Got a new request in from " + workerSocket.getInetAddress().getHostAddress());				
			}
			
			Logger.debug("Server cleanly exited listen loop. Serving stopped.");
			
		} catch (RejectedExecutionException rej) {
			Logger.error("Executor for failed for " + workerSocket.getInetAddress().getHostAddress());
			try {
				workerSocket.close();
			} catch (IOException e) {
				Logger.error("Also failed to close socket for failed executor!");
			}
		} catch (IOException e) {
			Logger.error("Unexpected IOException on socket - thread might have be in the process of being killed?");
		}
	}

	/**
	 * <p>
	 * Gets the root File object.
	 * </p>
	 * @return
	 */
	public static synchronized File getRoot() {
		return mWebRoot;
	}
	
}
