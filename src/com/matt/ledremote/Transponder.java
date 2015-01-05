package com.matt.ledremote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Transponder extends MainActivity 
{
	
	private Context mContext;
	
	private final String address = "00:13:12:25:73:17";
	
	private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private final String TAG = "LED_REMOTE";
	
	private BluetoothAdapter mBluetoothAdapter;
	
	private BluetoothSocket btSocket;
	
	private OutputStream outStream;
	
	private InputStream inStream;
	
	private Handler handler = new Handler();
	
    private final byte delimiter = 10;
    
    private boolean stopWorker = false;
    
    private int readBufferPosition = 0;
    
    private byte[] readBuffer = new byte[1024];
    
    private TextView StatusView;
    
    private String status = "";
    
    public boolean BLUETOOTH_CONNECTED;
	
	
	
	public Transponder( Context context , Activity activity )
	{
		mContext = context;
		StatusView = ( TextView ) activity.findViewById( R.id.status_view );
		BLUETOOTH_CONNECTED = false;
		
	}
	
	public void startConnection()
	{
		CheckBt();
		Connect();
		beginListenForData();
	}
	
	private void CheckBt() 
	{
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) 
        {
        	updateStatus( "Bluetooth Disabled!" );
        }

        if (mBluetoothAdapter == null) 
        {
        	updateStatus( "Bluetooth null!" );
        }
}

    public void Connect() 
    {
            Log.d(TAG, address);
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            updateStatus( "Connecting to ... " + device );
            mBluetoothAdapter.cancelDiscovery();
            Method m;
            try 
            {

                //btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                
				try {
					m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
					try 
	                {
						btSocket = (BluetoothSocket) m.invoke(device, 1);
					} 
	                catch (IllegalArgumentException e) 
	                {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
	                catch (IllegalAccessException e) 
	                {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
	                catch (InvocationTargetException e) 
	                {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				catch (NoSuchMethodException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                btSocket.connect();
                updateStatus( "Connection made." );
                BLUETOOTH_CONNECTED = true;
            } 
            catch (IOException e) 
            {
                try 
                {
                	btSocket.close();
                } 
                catch (IOException e2) 
                {
                	updateStatus( "Unable to end the connection" );
                }
                updateStatus( "Socket creation failed" );
                e.printStackTrace();
            }
            
    }

	public void writeData(String data) 
	{
	        try 
	        {
	                outStream = btSocket.getOutputStream();
	        } 
	        catch (IOException e) 
	        {
	        	updateStatus( "Bug BEFORE Sending stuff" );
	        }
	
	        String message = "Sending " + data;
	        updateStatus( message );
	        byte[] msgBuffer = message.getBytes();
	
	        try 
	        {
	                outStream.write(msgBuffer);
	        } 
	        catch (IOException e) 
	        {
	        	updateStatus( "Bug while sending stuff" );
	        }
	}
	
	
	
	public void beginListenForData()   
	{
        try 
        {
        	inStream = btSocket.getInputStream();
        } 
        catch (IOException e) 
        {
        }
        
       Thread workerThread = new Thread(new Runnable()
       {
           public void run()
           {                
              while(!Thread.currentThread().isInterrupted() && !stopWorker)
              {
                   try
                   {
                       int bytesAvailable = inStream.available(); 
                    
                       if(bytesAvailable > 0)
                       {
                           byte[] packetBytes = new byte[bytesAvailable];
                           inStream.read(packetBytes);
                           for(int i=0;i<bytesAvailable;i++)
                           {
                               byte b = packetBytes[i];
                               if(b == delimiter)
                               {
                                   byte[] encodedBytes = new byte[readBufferPosition];
                                   System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                   final String data = new String(encodedBytes, "US-ASCII");
                                   readBufferPosition = 0;
                                   Log.d(TAG , data);
                                   handler.post(new Runnable()
                                   {
                                       public void run()
                                       {
                                    	   Log.d(TAG, data);
                                    	   updateStatus( data );
                                         
                                           /* You also can use StatusView.setText(data); it won't display multilines
                                           */
                                              
                                       }
                                   });
                               }
                               else
                               {
                                   readBuffer[readBufferPosition++] = b;
                               }
                           }
                       }
                   }
                   catch (IOException ex)
                   {
                       stopWorker = true;
                   }
              }
           }
       });

       workerThread.start();
       Log.d(TAG,"made it here!!!!");
   }
	
	public void updateStatus( String newStatus )
	{
		String temp = status;
		status = ">>  ";
		status += newStatus;
		status += "\n";
		status += temp;
		StatusView.setText( status );
	}
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

