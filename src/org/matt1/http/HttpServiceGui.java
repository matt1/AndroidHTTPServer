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
import android.widget.TextView;

public class HttpServiceGui extends Activity {

	private Server mHttpServer;
	private Boolean mRunFlag = true;
	private List<InetAddress> mInterfaces = new ArrayList<InetAddress>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        
		getInterfaces();
		Logger.debug("Starting server on :" + mInterfaces.get(0).getHostAddress());
		mHttpServer = new Server(mRunFlag, mInterfaces.get(0));
		mHttpServer.run();
        
        TextView tv = new TextView(this);
        tv.setText("Http Service has been started on " + mInterfaces.get(0).getHostAddress());
        setContentView(tv);
        
    }
    
    public void onStop() {
    	mRunFlag = false;
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
