package org.matt1.http.workers;

import java.io.File;
import java.net.Socket;

import org.matt1.http.utils.HttpMethod;
import org.matt1.http.utils.HttpStatus;
import org.matt1.utils.Logger;

import android.os.Looper;

/**
 * <p>
 * Simple handler that renders errors in a nice thread-safe way when the AbstractWorker.getWorkerInstance encounters
 * problems.  Normally errors will be handled by the Workers themselves.
 * </p>
 * @author Matt
 *
 */
public class ErrorWorker extends AbstractWorker {

	protected File mResource;
	protected Socket mSocket;
	protected HttpMethod mMethod;
	protected HttpStatus mStatusCode;
	
	@Override
	public void InitialiseWorker(HttpMethod pMethod, File pResource, Socket pSocket) {
		mResource = pResource;
		mMethod = pMethod;
		mSocket = pSocket;
	}
	
	public void SetError(HttpStatus pStatCode) {
		mStatusCode = pStatCode;
	}
	
	
	@Override
	public void run() {
		
		if (Looper.myLooper() == null) {
			Looper.prepare();
		}
        		
		if (mSocket == null || mSocket.isClosed()) {
			Logger.warn("Socket was null or closed when trying to serve thread!");
			return;
		}
		 
		writeStatus(mSocket, mStatusCode);

	}


}
