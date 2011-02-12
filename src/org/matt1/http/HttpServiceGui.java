package org.matt1.http;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.matt1.utils.Logger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class HttpServiceGui extends Activity {

	private Server mHttpServer;
	private Thread mServerThread;
	private Boolean mRunFlag = true;
	private List<InetAddress> mInterfaces = new ArrayList<InetAddress>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		setContentView(R.layout.main);
        
		// start button
		findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {       
		    	startServer();
		    }
		});
		        
		// stop button
		findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {       
		    	stopServer();
		    }
		});
		
        
    }
    
    private void updateStatus(String pMessage) {
    	((TextView)findViewById(R.id.status)).setText(pMessage);
    }
    
    private void startServer() {
    	updateStatus("Getting available network interfaces...");
    	getInterfaces();
    	
    	updateStatus("Starting server on " + mInterfaces.get(0).getHostAddress() + "...");
    	Logger.debug("Starting server on :" + mInterfaces.get(0).getHostAddress());
		mHttpServer = new Server(mRunFlag, mInterfaces.get(0));
		mServerThread = new Thread(mHttpServer);
		mServerThread.start();
		updateStatus("Server started on " + mInterfaces.get(0).getHostAddress() + ".");
    }
    

    private void stopServer() {
    	updateStatus("Stopping server ...");
    	mHttpServer.stop();
    	mServerThread.interrupt();
		updateStatus("Server stopped.");
    }
    
    public void onStop() {
    	mHttpServer.stop();
    	mServerThread.interrupt();
    	super.onStop();
    }
   

	private void getInterfaces() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						Logger.debug("Adding network interface to list: " + inetAddress.getHostAddress());
						mInterfaces.add(inetAddress);
					}
				}
			}
		} catch (SocketException e) {
			Logger.error("Problem enumerating network interfaces");
		}
	}
	
}
