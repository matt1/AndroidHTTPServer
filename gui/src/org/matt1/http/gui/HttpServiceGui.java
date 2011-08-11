package org.matt1.http.gui;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.matt1.http.R;
import org.matt1.http.Server;
import org.matt1.http.events.ServerEventListener;
import org.matt1.utils.Logger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class HttpServiceGui extends Activity {

	/** System vibration */
	private Vibrator mVibration;
	
	private Server mHttpServer;
	private Thread mServerThread;

	private InetAddress mInterface;
	
	private Spinner mAddressSpinner;

	
	/** Handler for server event reporting */
	private Handler mUpdateHandler = new Handler();
	private class EventMessage implements Runnable {
		private String mMessage;
		public EventMessage(String pMessage) {mMessage = pMessage;}
		public void run() {updateStatus(mMessage);}
	}
	
	/** Handler for address spinner select */
	private class AddressSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> pParent, View pView, int pPos, long pId) {
	    	mInterface = (InetAddress) pParent.getItemAtPosition(pPos);	
	    }

	    public void onNothingSelected(AdapterView<?> parent) {}
	}
	
	private ServerEventListener mServerEvents = new ServerEventListener() {
		
		@Override
		public void onServerReady(String pAddress, int pPort) {			
			mUpdateHandler.post(new EventMessage("Server ready on " + pAddress + ":" + String.valueOf(pPort)));
		}
		
		@Override
		public void onRequestServed(String pResource) {
			mUpdateHandler.post(new EventMessage("Served " + pResource));
		}
		
		@Override
		public void onRequestError(String pResource) {
			mUpdateHandler.post(new EventMessage("Server sent error response " + pResource));
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		setContentView(R.layout.main);
        
		// start button
		findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {       
		    	vibrate();
		    	startServer();
		    }
		});
		        
		// stop button
		findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {       
		    	vibrate();
		    	stopServer();
		    }
		});
		
		// address spinner		
    	getInterfaces();    	
		
    	mVibration = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    	updateStatus("Tap on Start Server below to start serving");
		       
    }
    
    private void vibrate() {
    	mVibration.vibrate(35);
    }
    
    private void updateStatus(String pMessage) {
    	
    	((EditText)findViewById(R.id.status)).append(pMessage +"\n");

    }
    
    private void startServer() {
    	
    	mAddressSpinner.setEnabled(false);
    	mHttpServer = new Server(mInterface, 8080, "/");
		mHttpServer.setRequestListener(mServerEvents);
		mServerThread = new Thread(mHttpServer);
		mServerThread.start();
    }
    

    private void stopServer() {
    	updateStatus("Stopping server ...");
    	if (mHttpServer != null) {
    		mHttpServer.stop();
    	}
    	if (mServerThread != null) {
    		mServerThread.interrupt();
    	}
    	mAddressSpinner.setEnabled(true);
		updateStatus("Server stopped.");
    }
    
    public void onStop() {
    	stopServer();
    	super.onStop();
    }
   

	private void getInterfaces() {
		try {
			ArrayList<InetAddress> addresses = new ArrayList<InetAddress>();
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
								
				NetworkInterface intf = en.nextElement();
				
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()  && inetAddress instanceof Inet4Address) {
						addresses.add(inetAddress);						
					}
				}
			}
			
			mAddressSpinner = (Spinner) findViewById(R.id.address);
			ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, addresses.toArray());
			mAddressSpinner.setAdapter(adapter);
			mAddressSpinner.setOnItemSelectedListener(new AddressSelectedListener());
			mAddressSpinner.setEnabled(true);
			
		} catch (SocketException e) {
			Logger.error("Problem enumerating network interfaces");
		}
	}
	
}
