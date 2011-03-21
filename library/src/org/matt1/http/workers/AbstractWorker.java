package org.matt1.http.workers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Vector;

import org.matt1.http.events.WorkerEventListener;
import org.matt1.http.utils.HttpMethod;
import org.matt1.http.utils.HttpStatus;
import org.matt1.http.utils.headers.ContentTypeHttpHeader;
import org.matt1.http.utils.headers.HttpHeader;
import org.matt1.http.utils.headers.ServerHttpHeader;
import org.matt1.utils.ByteUtils;
import org.matt1.utils.Logger;

import android.webkit.MimeTypeMap;

/**
 * <p>
 * An abstract worker provides a basic set of basic common functionality that can be shared by many different types
 * or worker
 * </p>
 * @author Matt
 *
 */
public abstract class AbstractWorker implements Runnable {

	private final ServerHttpHeader mServerHeader = new ServerHttpHeader();
	
	protected static final int BUFFER_SIZE = 8192;	
	protected static final int mTimeout = 30000;
	
	/** Constants for Android string performance optimisations */
	protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String HTTP_VERSION = "HTTP/1.0 ";
	private static final String CONTENT_LENGTH = "Content-length";
	private static final String CLEANER_DOUBLE_DOT = "..";
	private static final String CLEANER_DOUBLE_SLASH = "//";
	private static final String CLEANER_SINGLE_SLASH = "/";
	private static final String CLEANER_EMPTY_STRING = "";
	private static final String REQUEST_SEPARATOR = " ";
	protected static final String DEAFUALT_MIMETYPE = "text/html";
	protected static final String NULL = "null";
	
	/** Static mime map as this is a really expensive operation */
	protected static final MimeTypeMap mMimeTypeMap = MimeTypeMap.getSingleton();

	// Event for requests
	protected WorkerEventListener mRequestListener = null;
	
	/**
	 * <p>
	 * Method called to process the request on its own thread.
	 * </p>
	 */
	public abstract void run();
	
	/**
	 * <p>
	 * Initialise the worker with everything it needs to process the request.  This should not actually "do" any
	 * server activity as the worker will not be running as a separate thread at this stage.
	 * </p>
	 * @param pMethod Http Method 
	 * @param pResource Resource
	 * @param pSocket The socket to write the response to
	 */
	public abstract void InitialiseWorker(HttpMethod pMethod, String pResource, Socket pSocket);
	
	/**
	 * <p>
	 * Gets the request line of the incoming request and returns an appropriate type of worker to handle it.
	 * </p>
	 * @param pSocket
	 * @return
	 */
	public static AbstractWorker getWorkerInstance(Socket pSocket, File pRoot) {
 		
		AbstractWorker worker = null;
		if (pSocket == null || pSocket.isClosed()) {
			Logger.warn("Socket was null or closed when trying to serve thread!");
			return worker;
		}
		
		try {
			
			// Setup some socket bits and pieces
			pSocket.setSoTimeout(mTimeout);
			pSocket.setTcpNoDelay(true);
			
			BufferedReader reader =  new BufferedReader(new InputStreamReader(pSocket.getInputStream()), BUFFER_SIZE);
			String request = reader.readLine();
			
			if (request == null || CLEANER_EMPTY_STRING.equals(request)) {
				Logger.error("HTTP Request was null or zero-length");
				worker = new ErrorWorker();
				worker.InitialiseWorker(null, null, pSocket);
				((ErrorWorker) worker).SetError(HttpStatus.HTTP400);
			} else {
				Logger.debug("Request: " + request);
						
				// TODO: malformed request handler.
				String[] tokens = request.split(REQUEST_SEPARATOR);
							
				HttpMethod method = HttpMethod.valueOf(tokens[0]);
				String resource = tokens[1];

				worker = WorkerFactory.getInstance().getWorker(resource);							
				worker.InitialiseWorker(method, resource, pSocket);
			}

		} catch (SocketTimeoutException ste) {
			Logger.error("Socket timed out after " + mTimeout + "ms when trying to serve thread");
			// No socket so don't bother creating error worker.
		} catch (IOException e) {
			Logger.error("IOException when trying to serve thread: " + e.toString());
			worker.createErrorWorker(HttpStatus.HTTP500, pSocket);
		} catch (IllegalAccessException e) {
			Logger.error("IllegalAccessException when trying to create Worker: " + e.toString());
			worker.createErrorWorker(HttpStatus.HTTP500, pSocket);
		} catch (InstantiationException e) {
			Logger.error("InstantiationException when trying to create Worker: " + e.toString());
			worker.createErrorWorker(HttpStatus.HTTP500, pSocket);
		}

		return worker;
		
	}
	
	/**
	 * <p>
	 * Create a new error worker in case things went wrong
	 * </p>
	 * @param pError
	 * @param pSocket
	 */
	private ErrorWorker createErrorWorker(HttpStatus pError, Socket pSocket) {
		ErrorWorker result = new ErrorWorker();
		result.InitialiseWorker(null, null, pSocket);
		result.SetError(pError);
		return result;
	}
	
	/**
	 * <p>
	 * Take the requested resource and clean anything out which might cause a problem, such as ".." etc.
	 * </p>
	 * @param pResource String to clean
	 * @return Cleaned string
	 */
	protected static String cleanResource(String pResource) {
		String result = pResource.replace(CLEANER_DOUBLE_DOT, CLEANER_EMPTY_STRING);
		result = result.replace(CLEANER_DOUBLE_SLASH, CLEANER_SINGLE_SLASH);
		
		return result;
	}
	
	/**
	 * <p>
	 * Given the data and the socket, write the response to the client.  Will automatically provide headers for
	 * content-length, but content type should be provided by the worker as a separate header
	 * </p>
	 * @param pData Actual bytes to write
	 * @param pSocket Socket to write to
	 * @param pHeaders Any additional headers to provide
	 */
	protected void writeResponse(byte[] pData, Socket pSocket, List<HttpHeader> pHeaders, HttpStatus pStatus) {
		try {
			
			if (!pSocket.isClosed() && pSocket.isConnected()) {
			
				OutputStream outStream = pSocket.getOutputStream();				
				outStream.write(ByteUtils.getBytesFromString(HTTP_VERSION + pStatus.getDescription() + LINE_SEPARATOR));
				
				// Do headers
				for (HttpHeader header : pHeaders) {
					outStream.write(header.getBytes());
				}
				outStream.write(new HttpHeader(CONTENT_LENGTH, String.valueOf(pData.length)).getBytes());
				//outStream.write(new DateHttpHeader().getBytes());
				outStream.write(mServerHeader.getBytes());
				outStream.write(ByteUtils.getBytesFromString(LINE_SEPARATOR));
				
				outStream.write(pData, 0, pData.length);
				
				outStream.close();
				pSocket.close();
			} else {
				Logger.debug("Socket was closed or disconnected before we could send response!");
			}			
		} catch (SocketException se) {
			Logger.debug("Got socket exception: " + se.getMessage());
		} catch (IOException e) {
			Logger.error("IOException when trying to write response!");
		} 	
	}

	/**
	 * <p>
	 * Writes a simple text HTML response out to the socket
	 * </p>
	 * @param pResponse Stream of bytes to write
	 */
	protected void writeResponse(String pResponse, Socket pSocket, HttpStatus pStatus) {
		List<HttpHeader> headers = new Vector<HttpHeader>();
		headers.add(new ContentTypeHttpHeader("text/html"));
		writeResponse(pResponse.getBytes(), pSocket, headers, HttpStatus.HTTP200);
	}
	
	/**
	 * <p>
	 * Send a simple response message (e.g. HTTP 500 - server error")
	 * </p>
	 * @param pSocket
	 * @param pStatus
	 */
	protected void writeStatus(Socket pSocket, HttpStatus pStatus) {
		writeResponse(pStatus.getDescription(), pSocket, pStatus);
	}

	/**
	 * <p>
	 * Triggers the request event if the handler is not null
	 * </p>
	 * @param pResource
	 */
	protected void triggerRequestServedEvent(String pResource) {
		if (mRequestListener != null) {
			mRequestListener.onRequestServed(pResource);
		}
	}
	
	/**
	 * <p>
	 * Triggers the  error event if the handler is not null
	 * </p>
	 * @param pResource
	 */
	protected void triggerRequestErrorEvent(String pResource) {
		if (mRequestListener != null) {
			mRequestListener.onRequestError(pResource);
		}
	}	
	
	public WorkerEventListener getRequestListener() {
		return mRequestListener;
	}

	public void setRequestListener(WorkerEventListener pRequestListener) {
		mRequestListener = pRequestListener;
	}
	
}
