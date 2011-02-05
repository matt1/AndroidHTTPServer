package org.matt1.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.matt1.utils.Logger;

import android.os.Handler;
import android.os.Looper;
import android.webkit.MimeTypeMap;

public class Worker implements Runnable {

	public Handler mHandler;
	
	private Socket mSocket;
	
	private int mTimeout = 30000;
	
	private static final int BUFFER_SIZE = 8192;
	
	
	
	private static final String wwwRoot = "/sdcard/wwwroot";
	
	private enum headerType {
		separator,
		contentType,
		contentLength
	}
	
	private enum responseType {
		ok,
		notFound,
		error,
		notSupported
	}
	
	public Worker(Socket pSocket) {
		mSocket = pSocket;
	}
	
	public Worker(Socket pSocket, int pTimeout) {
		mSocket = pSocket;
		mTimeout = pTimeout;
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
				return;
			}
			
			// TODO: malformed request handler.
			String resource = request.split(" ")[1];
			
			if (!request.startsWith("GET")) {
				// Bad method
				sendError(400);
			} else {
				// Good method
				
				// TODO security
				resource = cleanResource(resource);
				
				File file = new File(wwwRoot + resource);
				if (!file.canRead()) {
					sendError(404);
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
				
				writeResponse(fileContent, type);
			}
		
		} catch (SocketTimeoutException ste) {
			Logger.error("Socket timed out after " + mTimeout + "ms when trying to serve thread");
			sendError(500);
		} catch (IOException e) {
			Logger.error("IOException when trying to serve thread!");
			sendError(500);
		} finally {
			Logger.debug("Worker " + this.toString() + " finsihed.");
		}

	}

	private String cleanResource(String pResource) {
		String result = pResource.replace("..", "");
		result = result.replace("//", "/");
		
		return result;
	}
	
	private void sendError(int pError) {
		if (pError == 404) {
			writeResponse("404 - file not found.");
		} if (pError == 500) {
			writeResponse("500 - server error.");
		}
		writeResponse("400 - bad request.");
	}
	
	private byte[] doHeader(headerType pType, Object pValue) {
		String result = "";
		if (pType == headerType.contentLength) {
			result = "Content-length: " + pValue.toString() + System.getProperty("line.separator");
		} else if (pType == headerType.contentType) {
			result = "Content-type: " + pValue.toString() + System.getProperty("line.separator");
		} else if (pType == headerType.separator) {
			result = System.getProperty("line.separator");
		}
		return result.getBytes();
	}
	
	private byte[] doResponse(responseType pType) {
		String result = "HTTP/1.0 500 Error" + System.getProperty("line.separator");
		if (pType == responseType.ok) {
			result = "HTTP/1.0 200 OK" + System.getProperty("line.separator");
		} else if (pType == responseType.notFound) {
			result = "HTTP/1.0 404 Not found" + System.getProperty("line.separator");
		}
		return result.getBytes();
	}
	
	private void writeResponse(byte[] pData, String pContentType) {
		try {
			
			if (!mSocket.isClosed() && mSocket.isConnected()) {
			
				OutputStream outStream = mSocket.getOutputStream();
				
				outStream.write(doResponse(responseType.ok));
				outStream.write(doHeader(headerType.contentLength, pData.length));
				outStream.write(doHeader(headerType.contentType, pContentType));
				outStream.write(doHeader(headerType.separator, null));
				outStream.write(pData, 0, pData.length);
				
				outStream.close();
			} else {
				Logger.debug("Socket was closed or disconnected before we could send response!");
			}
			
		} catch (SocketException se) {
			Logger.debug("Got socket exception: " + se.getMessage());
		} catch (IOException e) {
			Logger.error("IOException when trying to write response!");
		}
		
	}
	
	private void writeResponse(String pResponse) {
		writeResponse(pResponse.getBytes(), "text/html");
		
		
	}
	
	
}
