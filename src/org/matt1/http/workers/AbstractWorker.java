package org.matt1.http.workers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Vector;

import org.matt1.http.utils.ContentTypeHTTPHeader;
import org.matt1.http.utils.DateHTTPHeader;
import org.matt1.http.utils.HTTPHeader;
import org.matt1.http.utils.ServerHTTPHeader;
import org.matt1.utils.Logger;

/**
 * <p>
 * An abstract worker provides a basic set of basic common functionality that can be shared by many different types
 * or worker
 * </p>
 * @author Matt
 *
 */
public abstract class AbstractWorker implements Runnable, WorkerInterface {

	private final ServerHTTPHeader mServerHeader = new ServerHTTPHeader();
	
	@Override
	public abstract void InitialiseWorker(Socket pSocket, File pRootDirectory);

	@Override
	public abstract void InitialiseWorker(Socket pSocket, int pTimeout, File pRootDirectory);

	@Override
	public abstract void run();

	/**
	 * <p>
	 * Given the data and the socket, write the response to the client.  Will automatically provide headers for
	 * content-length, but content type should be provided by the worker as a separate header
	 * </p>
	 * @param pData Actual bytes to write
	 * @param pSocket Socket to write to
	 * @param pHeaders Any additional headers to provide
	 */
	protected void writeResponse(byte[] pData, Socket pSocket, List<HTTPHeader> pHeaders) {
		try {
			
			if (!pSocket.isClosed() && pSocket.isConnected()) {
			
				OutputStream outStream = pSocket.getOutputStream();
				
				outStream.write(("HTTP/1.0 200 OK" + System.getProperty("line.separator")).getBytes());
				
				// Do headers
				for (HTTPHeader header : pHeaders) {
					outStream.write(header.getBytes());
				}
				outStream.write(new HTTPHeader("Content-length", String.valueOf(pData.length)).getBytes());
				outStream.write(new DateHTTPHeader().getBytes());
				outStream.write(mServerHeader.getBytes());
				outStream.write(System.getProperty("line.separator").getBytes());
				
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

	/**
	 * <p>
	 * Writes a simple text HTML response out to the socket
	 * </p>
	 * @param pResponse Stream of bytes to write
	 */
	private void writeResponse(String pResponse, Socket pSocket) {
		List<HTTPHeader> headers = new Vector<HTTPHeader>();
		headers.add(new ContentTypeHTTPHeader("text/html"));
		writeResponse(pResponse.getBytes(), pSocket, headers);
	}
	
	
	/**
	 * <p>
	 * Send an error message to the client.  Currently only 404 and 500 are supported, all others default to 400 
	 * (bad request)
	 * </p>
	 * @param pError Error code to use.  
	 */
	protected void sendError(int pError, Socket pSocket) {
		if (pError == 404) {
			writeResponse("404 - file not found.", pSocket);
		} if (pError == 500) {
			writeResponse("500 - server error.", pSocket);
		}
		writeResponse("400 - bad request.", pSocket);
	}
}
