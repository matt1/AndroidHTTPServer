package org.matt1.http.workers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Vector;

import org.matt1.http.utils.headers.ContentTypeHttpHeader;
import org.matt1.http.utils.headers.HttpHeader;
import org.matt1.http.utils.response.HttpStatus;
import org.matt1.utils.Logger;

import android.os.Handler;
import android.os.Looper;
import android.webkit.MimeTypeMap;

/**
 * <p>
 * A simple HTTP worker that provides basic file serving capabilities
 * </p>
 * @author Matt
 *
 */
public class SimpleWorker extends AbstractWorker {

	public Handler mHandler;
	private Socket mSocket;
	private int mTimeout = 30000;
	private static final int BUFFER_SIZE = 8192;
	
	private static File wwwRoot;
	
	public SimpleWorker() {
		Logger.debug("New worker initialised.");
	}

	@Override
	public void InitialiseWorker(Socket pSocket, File pRootDirectory) {
		InitialiseWorker(pSocket, mTimeout, pRootDirectory);		
	}

	@Override
	public void InitialiseWorker(Socket pSocket, int pTimeout,	File pRootDirectory) {
		mSocket = pSocket;
		mTimeout = pTimeout;
		wwwRoot = pRootDirectory;		
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
			
			// Setup some socket bits and pieces
			mSocket.setSoTimeout(mTimeout);
			mSocket.setTcpNoDelay(true);
			
			BufferedReader reader =  new BufferedReader(new InputStreamReader(mSocket.getInputStream()), BUFFER_SIZE);
			String request = reader.readLine();
			
			if (request == null || "".equals(request)) {
				Logger.error("HTTP Request was null or zero-length");
				writeStatus(mSocket, HttpStatus.HTTP400);
			}
			
			// TODO: malformed request handler.
			String resource = request.split(" ")[1];
			
			if (!request.startsWith("GET")) {
				// Bad method
				writeStatus(mSocket, HttpStatus.HTTP405);
			} else {
				// Good method
				
				// TODO security
				resource = cleanResource(resource);
				
				File file = new File(wwwRoot.getAbsolutePath() + resource);
				if (!file.canRead()) {
					writeStatus(mSocket, HttpStatus.HTTP404);
				}
				
				FileInputStream fileReader = new FileInputStream(file);
				byte[] fileContent = new byte[(int) file.length()];
				fileReader.read(fileContent, 0, (int) file.length());
				fileReader.close();
				String ext = MimeTypeMap.getFileExtensionFromUrl(resource);
				String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
				if (null == type || "".equals("null")) {
					type = "text/html";
				}
				
				List<HttpHeader> headers = new Vector<HttpHeader>();
				headers.add(new ContentTypeHttpHeader(type));
				
				writeResponse(fileContent, mSocket, headers, HttpStatus.HTTP200);
			}
		
		} catch (SocketTimeoutException ste) {
			Logger.error("Socket timed out after " + mTimeout + "ms when trying to serve thread");
		} catch (IOException e) {
			Logger.error("IOException when trying to serve thread!");
			writeStatus(mSocket, HttpStatus.HTTP500);
		} finally {
			Logger.debug("Worker " + this.toString() + " finsihed.");
		}

	}

	/**
	 * <p>
	 * Take the requested resource and clean anything out which might cause a problem, such as ".." etc.
	 * </p>
	 * @param pResource String to clean
	 * @return Cleaned string
	 */
	protected String cleanResource(String pResource) {
		String result = pResource.replace("..", "");
		result = result.replace("//", "/");
		
		return result;
	}
}
