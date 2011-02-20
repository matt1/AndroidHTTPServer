package org.matt1.http.workers;

import java.net.Socket;
import java.util.Vector;

import org.matt1.http.utils.HttpMethod;
import org.matt1.http.utils.HttpStatus;
import org.matt1.http.utils.headers.ContentTypeHttpHeader;
import org.matt1.http.utils.headers.HttpHeader;
import org.matt1.http.workers.simple.SimpleRequest;
import org.matt1.http.workers.simple.SimpleResponse;
import org.matt1.http.workers.simple.SimpleWorkerInterface;
import org.matt1.http.workers.simple.SimpleWorkerException;
import org.matt1.http.workers.simple.implementations.SimpleDirectoryWorker;
import org.matt1.http.workers.simple.implementations.SimpleFileWorker;
import org.matt1.utils.Logger;

import android.os.Looper;

/**
 * <p>
 * Dispatches requests to simple workers
 * </p>
 * @author Matt
 *
 */
public class SimpleWorkerDispatcher extends AbstractWorker {

	private Socket mSocket;
	private SimpleRequest mRequest;
	
	@Override
	public void InitialiseWorker(HttpMethod pMethod, String pResource, Socket pSocket) {
		mRequest = new SimpleRequest(pMethod, pResource);
		mSocket = pSocket;
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
		
		try {
				SimpleWorkerInterface sp;
				
				if (mRequest.getResource().endsWith("/")) {
					sp = new SimpleDirectoryWorker();
				} else {
					sp = new SimpleFileWorker();
				}
								
				SimpleResponse response = sp.handlePackage(mRequest);
				
				Vector<HttpHeader> headers = new Vector<HttpHeader>();
				headers.add(new ContentTypeHttpHeader(response.getMimeType()));
				
				writeResponse(response.getResponse(), mSocket, headers, HttpStatus.HTTP200);
			
		} catch (SimpleWorkerException e) {
			Logger.debug("PackWorker threw exception: " + e.getStatus().toString());
			writeStatus(mSocket, e.getStatus());
		}

	}

	
}
