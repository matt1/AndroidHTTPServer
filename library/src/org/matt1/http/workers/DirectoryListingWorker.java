package org.matt1.http.workers;

import java.io.File;
import java.net.Socket;
import java.util.Vector;

import org.matt1.http.Server;
import org.matt1.http.utils.HttpMethod;
import org.matt1.http.utils.HttpStatus;
import org.matt1.http.utils.headers.ContentTypeHttpHeader;
import org.matt1.http.utils.headers.HttpHeader;
import org.matt1.utils.ByteUtils;
import org.matt1.utils.Logger;

import android.os.Looper;

/**
 * <p>
 * A (naive) worker that generates a directory listing.
 * </p>
 * @author Matt
 *
 */
public class DirectoryListingWorker extends AbstractWorker {

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
				mResource = new File(Server.getRoot() + mResourceString);
				
				if (!mResource.exists() || !mResource.canRead()) {
					writeStatus(mSocket, HttpStatus.HTTP404);
				} else if (!mResource.isDirectory()) {
					writeStatus(mSocket, HttpStatus.HTTP500);				
				} else {	
					
					File[] children = mResource.listFiles();
					StringBuffer buffer = new StringBuffer();
					
					buffer.append("<h1>").append(mResourceString).append("</h1>");
					
					if (children.length == 0) {
						buffer.append("This directory has no files.");
					} else {
						for (File child : children) {
			
							if (child.isDirectory()) {
								buffer.append("[dir] <a href=\"").append(mResourceString).append(child.getName()).append("/\">");
							} else {
								buffer.append("<a href=\"").append(mResourceString).append(child.getName()).append("\">");
							}
							
							
							buffer.append(child.getName());
							buffer.append("</a><br />");
							
						}
					}
					
					String type = DEAFUALT_MIMETYPE;
										
					Vector<HttpHeader> headers = new Vector<HttpHeader>();
					headers.add(new ContentTypeHttpHeader(type));					
					writeResponse(ByteUtils.getBytesFromString(buffer.toString()), mSocket, headers, HttpStatus.HTTP200);
				}
			}			
		} catch (OutOfMemoryError e) {
			Logger.error("OutOfMemoryError when trying to serve " + mResourceString  + e.toString());
			writeStatus(mSocket, HttpStatus.HTTP503);
		}
	} 

}



