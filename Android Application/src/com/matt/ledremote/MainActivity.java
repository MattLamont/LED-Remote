package com.matt.ledremote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	
	//Activity context that can be passed to other functions
	private Context mContext;
	
	//The layout that contains all other items
	private LinearLayout containerLayout;
	
	//The seek bars used to determine rgb color and brightness
	private SeekBar redSeekBar;
	private SeekBar greenSeekBar;
	private SeekBar blueSeekBar;
	
	//The titles of the seek bars
	private TextView redSeekBarTitle;
	private TextView greenSeekBarTitle;
	private TextView blueSeekBarTitle;
	
	//the textviews containing the guage for each color
	private TextView redGauge;
	private TextView greenGauge;
	private TextView blueGauge;
	
	//private variables to hold the color values at all times
	private int redSaturation;
	private int greenSaturation;
	private int blueSaturation;
	
	//container views for the seekbars
	private LinearLayout redContainer;
	private LinearLayout greenContainer;
	private LinearLayout blueContainer;
	
	//shared preferences used to retrieve previous application data
	private SharedPreferences sharedPreferences;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_main );
		mContext = this;
		containerLayout = (LinearLayout) findViewById( R.id.container_layout );
		StatusView = (TextView) findViewById( R.id.status_view );
		setupColorSeekBars();
	}
	
	protected void onStart()
	{
		Log.d( TAG , "onStart called");
		startConnection();
		retrievePersistentData();
		super.onStart();
		
	}
	
	protected void onRestart()
	{
		super.onRestart();
		Log.d( TAG , "onRestart called");
		startConnection();
		retrievePersistentData();
	}
	
	
	protected void onStop()
	{
		super.onStop();
		Log.d(TAG, "onStop called");
		savePersistentData();
		disconnect();
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "onDestroy called");
		savePersistentData();
		disconnect();
	}
	
	private void setupColorSeekBars()
	{
		//get the seek bars
		redSeekBar = ( SeekBar ) findViewById( R.id.red_seek_bar );
		greenSeekBar = ( SeekBar ) findViewById( R.id.green_seek_bar );
		blueSeekBar = ( SeekBar ) findViewById( R.id.blue_seek_bar );
		
		//get the seek bar titles 
		redSeekBarTitle = ( TextView ) findViewById( R.id.red_seek_bar_title );
		greenSeekBarTitle = ( TextView ) findViewById( R.id.green_seek_bar_title );
		blueSeekBarTitle = ( TextView ) findViewById( R.id.blue_seek_bar_title );
		
		//add custom typeface
		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/advent_bold.ttf");
		
		//set the textviews with the custom typeface
		redSeekBarTitle.setTypeface( type );
		greenSeekBarTitle.setTypeface( type );
		blueSeekBarTitle.setTypeface( type );
		
		
		//set the seek bar names and values
		redSeekBarTitle.setText( "RED" );
		greenSeekBarTitle.setText( "GREEN" );
		blueSeekBarTitle.setText( "BLUE" );
		
		//get the color gauge text views
		redGauge = ( TextView ) findViewById( R.id.red_guage );
		greenGauge = ( TextView ) findViewById( R.id.green_guage );
		blueGauge = ( TextView ) findViewById( R.id.blue_guage );
		
		redGauge.setTypeface( type );
		greenGauge.setTypeface( type );
		blueGauge.setTypeface( type );
		
		//set values for the gauges
		redGauge.setText( String.valueOf( redSeekBar.getProgress() ) );
		greenGauge.setText( String.valueOf( greenSeekBar.getProgress() ) );
		blueGauge.setText( String.valueOf( blueSeekBar.getProgress() ) );
		
		//set typeface of status view
		StatusView.setTypeface( type );
		
		//add background image for seekbars
		redContainer = ( LinearLayout ) findViewById( R.id.red_container );
		greenContainer = ( LinearLayout ) findViewById( R.id.green_container );
		blueContainer = ( LinearLayout ) findViewById( R.id.blue_container );
		
		//setup the touch listeners for the seek bars
		redSeekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) 
			{
				redSaturation = arg0.getProgress();
				redGauge.setText( String.valueOf( redSaturation ));
				updateLeds( 0 , redSaturation );
			}
			
		});
		
		greenSeekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) 
			{
				greenSaturation = arg0.getProgress();
				greenGauge.setText( String.valueOf( greenSaturation ));
				updateLeds( 1 , greenSaturation );
			}
			
		});

		blueSeekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
		{

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) 
			{
				blueSaturation = arg0.getProgress();
				blueGauge.setText( String.valueOf( blueSaturation ));
				updateLeds( 2 , blueSaturation );
			}
			
		});
	
	}
	
	private void updateLeds( int led , int saturation )
	{
		String message = "";
		message += led;
		
		if( saturation < 10 )
		{
			message += "00";
			message += saturation;
		}
		
		else if( saturation < 100 && saturation > 9 )
		{
			message += "0";
			message += saturation;
		}
		
		else
		{
			message += saturation;
		}
		
		writeData( message );
	}
	
	private void savePersistentData()
	{
		sharedPreferences.edit().clear();
		sharedPreferences.edit().putInt( "redSaturation" , redSaturation ).commit();
		sharedPreferences.edit().putInt( "greenSaturation" , greenSaturation ).commit();
		sharedPreferences.edit().putInt( "blueSaturation" , blueSaturation ).commit();
	}
	
	private void retrievePersistentData()
	{
		sharedPreferences = this.getSharedPreferences( "com.matt.ledremote" , 0 );
		
		redSaturation = sharedPreferences.getInt( "redSaturation" , 0 );
		greenSaturation = sharedPreferences.getInt( "greenSaturation" , 0 );
		blueSaturation = sharedPreferences.getInt( "blueSaturation" , 0 );
		
		redSeekBar.setProgress( redSaturation );
		greenSeekBar.setProgress( greenSaturation );
		blueSeekBar.setProgress( blueSaturation );
		
		redGauge.setText( String.valueOf( redSeekBar.getProgress() ) );
		greenGauge.setText( String.valueOf( greenSeekBar.getProgress() ) );
		blueGauge.setText( String.valueOf( blueSeekBar.getProgress() ) );
		
	}

	

	
	
	
	
	
	
	

	
	private final String address = "20:14:04:16:28:39";
	
	private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private final String TAG = "LED_REMOTE";
	
	private BluetoothAdapter mBluetoothAdapter = null;
	
	private BluetoothSocket btSocket = null;
	
	private OutputStream outStream = null;
	
	private InputStream inStream = null;
	
	private Handler handler = new Handler();
	
    private final byte delimiter = 10;
    
    private boolean stopWorker = false;
    
    private int readBufferPosition = 0;
    
    private byte[] readBuffer = new byte[1024];
    
    private TextView StatusView;
    
    private String status = "";
    
    public boolean BLUETOOTH_CONNECTED;
	
	

	
	public void startConnection()
	{
		CheckBt();
		Connect();
	}
	
	private void CheckBt() 
	{
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
 
                if (!mBluetoothAdapter.isEnabled()) {
                        Toast.makeText(getApplicationContext(), "Bluetooth Disabled !",
                                        Toast.LENGTH_SHORT).show();
                }
 
                if (mBluetoothAdapter == null) {
                        Toast.makeText(getApplicationContext(),
                                        "Bluetooth null !", Toast.LENGTH_SHORT)
                                        .show();
                }
        }
       
	public void Connect() 
	{
            Log.d(TAG, address);
            updateStatus( address ); 
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if( device == null )
            {
            	Log.d(TAG,"device is null");
            }
            Log.d(TAG, "Connecting to ... " + device);
            updateStatus( "Connecting to ... " + device );
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "canceled discovery");
            updateStatus( "canceled discovery" );
            
            Method m = null;
			try 
			{
				m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
			} 
			catch (NoSuchMethodException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            try 
            {
				btSocket = (BluetoothSocket) m.invoke(device, 1);
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        		
      
            Log.d(TAG, "created socket");
            updateStatus( "created socket" );
            try 
            {
				btSocket.connect();
				Log.d(TAG, "Connection made.");
				updateStatus( "Connection made." );
				writeData( "5000" );
				containerLayout.setBackgroundResource( R.drawable.gaussian_grey );
			} 
            catch (IOException e) 
            {
				// TODO Auto-generated catch block
				e.printStackTrace();
				containerLayout.setBackgroundResource( R.drawable.red_gaussian );
			}
            
            
           
            beginListenForData();
    }
	
	private void disconnect()
	{
		Log.d(TAG, "closing");

        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException e) {
                Log.e(TAG, "isBt IOE", e);              
            }
            inStream = null;
        }
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException e) {
                Log.e(TAG, "outStream IOE", e);              
            }
            outStream = null;
        }
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "socket IOE", e);                
            }
            btSocket = null;
        }
        Log.d(TAG, "closed");       
    }
	

	private void writeData(String data) 
	{
        try 
        {
                outStream = btSocket.getOutputStream();
        } 
        catch (IOException e) 
        {
                Log.d(TAG, "Bug BEFORE Sending stuff", e);
                updateStatus( "Bug BEFORE Sending stuff" );
        }

        String message = data;
        byte[] msgBuffer = message.getBytes();

        try 
        {
                outStream.write(msgBuffer);
        } 
        catch (IOException e) 
        {
                Log.d(TAG, "Bug while sending stuff", e);
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
                                   handler.post(new Runnable()
                                   {
                                       public void run()
                                       {                                           
                                    	   updateStatus( data );     
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
       Log.d(TAG, "made it to end of beginListenforData");
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
