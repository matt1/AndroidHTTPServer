package org.matt1.http.workers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.Vector;

import org.matt1.http.Server;
import org.matt1.http.utils.HttpMethod;
import org.matt1.http.utils.HttpStatus;
import org.matt1.http.utils.headers.ContentTypeHttpHeader;
import org.matt1.http.utils.headers.HttpHeader;
import org.matt1.utils.Logger;

import android.os.Looper;
import android.webkit.MimeTypeMap;

/**
 * <p>
 * A very simple HTTP worker that provides basic file serving capabilities.  Could be optimised by reading in files
 * in blocks rather than optimistically eating as much memory as it likes.
 * </p>
 * @author Matt
 *
 */
public class SimpleWorker extends AbstractWorker {

	private File mResource;
	private Socket mSocket;
	private HttpMethod mMethod;
	private String mResourceString; 
	
	@Override
	public void InitialiseWorker(HttpMethod pMethod, String pResource, Socket pSocket) {
		mResourceString = pResource;
		mMethod = pMethod;
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
			
			if (mMethod != HttpMethod.GET) {
				// Bad method
				writeStatus(mSocket, HttpStatus.HTTP405);
			} else {
						
				mResource = new File(Server.getRoot() + URLDecoder.decode(mResourceString));
				
				if (!mResource.exists() || !mResource.canRead()) {
					writeStatus(mSocket, HttpStatus.HTTP404);
				} else {								
					FileInputStream fileReader = new FileInputStream(mResource);
					byte[] fileContent = new byte[(int) mResource.length()];
					fileReader.read(fileContent, 0, (int) mResource.length());
					fileReader.close();
					String ext = MimeTypeMap.getFileExtensionFromUrl(mResource.getAbsolutePath());
					String type = mMimeTypeMap.getMimeTypeFromExtension(ext);
					if (null == type || NULL.equals(type)) {
						type = DEAFUALT_MIMETYPE;
					}					
					Vector<HttpHeader> headers = new Vector<HttpHeader>();
					headers.add(new ContentTypeHttpHeader(type));					
					writeResponse(fileContent, mSocket, headers, HttpStatus.HTTP200);
				}
			}
		
		} catch (SocketTimeoutException ste) {
			Logger.error("Socket timed out after " + mTimeout + "ms when trying to serve thread");
		} catch (IOException e) {
			Logger.error("IOException when trying to serve " + mResourceString + e.toString());
			writeStatus(mSocket, HttpStatus.HTTP500);
		} catch (OutOfMemoryError e) {
			Logger.error("OutOfMemoryError when trying to serve " + mResourceString  + e.toString());
			writeStatus(mSocket, HttpStatus.HTTP503);
		}

	}


}
