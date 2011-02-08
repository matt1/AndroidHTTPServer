package org.matt1.http.workers;

import java.io.File;
import java.net.Socket;
import java.util.Vector;

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
	
	@Override
	public void InitialiseWorker(HttpMethod pMethod, File pResource, Socket pSocket) {
		mResource = pResource;
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
		
		if (mMethod != HttpMethod.GET) {
			// Bad method
			writeStatus(mSocket, HttpStatus.HTTP405);
		} else {
							
			if (!mResource.exists() || !mResource.canRead()) {
				writeStatus(mSocket, HttpStatus.HTTP404);
			} else if (!mResource.isDirectory()) {
				writeStatus(mSocket, HttpStatus.HTTP500);				
			} else {	
				
				File[] children = mResource.listFiles();
				StringBuffer buffer = new StringBuffer();
				
				buffer.append("<h1>").append(mResource.getPath()).append("</h1>");
				
				if (mResource.getParentFile() != null) {
					buffer.append("<a href=\"").append(mResource.getParentFile().getName())
					.append("\">").append(mResource.getParentFile()).append("</a><br />");
				}
				
				for (File child : children) {
					
										
					if (child.isDirectory()) {
						buffer.append("<a href=\"").append(child.getPath())
						.append("\">").append("[DIR]").append(child.getName()).append("</a>");
					} else {
						buffer.append("<a href=\"").append(child.getName())
						.append("\">").append(child.getName()).append("</a>");
					}
					
					buffer.append("<br />");
					
				}
				
				String type = DEAFUALT_MIMETYPE;
									
				Vector<HttpHeader> headers = new Vector<HttpHeader>();
				headers.add(new ContentTypeHttpHeader(type));					
				writeResponse(ByteUtils.getBytesFromString(buffer.toString()), mSocket, headers, HttpStatus.HTTP200);
			}
		} 

	}


}
