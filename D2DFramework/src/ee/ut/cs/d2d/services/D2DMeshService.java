package ee.ut.cs.d2d.services;

import ee.ut.cs.d2d.network.D2DBluetooth;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class D2DMeshService extends Service {
	
	private final String TAG = D2DMeshService.class.getSimpleName();
	
	private final IBinder mBinder = new MyBinder();
	
	D2DBluetooth btDevice = null;
	
	@Override  
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		 
		if (btDevice!=null){
			Log.d(TAG, "discovery can be called without instantiation");
		}
		
		//it works as expected
		//proper policies should be put in place
		//works together with startScanScheduler(); 
		forcedStop();
		
		return Service.START_NOT_STICKY;
	}
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public void onDestroy(){
		Log.d(TAG, "service finished");
		
		//scheduler service is stopped here
		//works together with startScanScheduler() and forcedStop();
		stopScanScheduler();
		
		super.onDestroy();
		
		
		
	}
		
	public class MyBinder extends Binder {
		public D2DMeshService getMeshServiceInstance() {
			return D2DMeshService.this;
		}

	}
	
	
	public void btOn(){
		btDevice.D2DOn();
	}
	
	public void btOff(){
		btDevice.D2DOff();
	}
	
	public void btDiscovery(){
		btDevice.D2DDiscovery();
	}
	

	public void setServiceContext(Context context){
		if (btDevice==null){
			Log.d(TAG, "device instance is null");
			btDevice = new D2DBluetooth(context);
			
			//works together with stopScanScheduler() and forcedStop();
			startScanScheduler();
		}
		
	}
	
	public void startScanScheduler(){
		Intent intentScheduler = new Intent(D2DScanScheduler.D2DSCANSCHEDULER_ACTION_SCAN);
		sendBroadcast(intentScheduler);
		
	}
	
	
	public void stopScanScheduler(){
		 Intent intent = new Intent(this, D2DScanService.class);
	 	 PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent, 
	               PendingIntent.FLAG_CANCEL_CURRENT);
	 	    
	 	 AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE); 
	 	 alarmManager.cancel(pending);
	 	   
	 	 Log.d(TAG, "alarm service stopped");
	}
	
	
	public void forcedStop(){
		Intent intentStop = new Intent(this, D2DMeshService.class);
 	    stopService(intentStop);
	}

	
	
	
	/**
	 * testing method
	 */
	public void print(){
		Log.d(TAG, "Service method called");
	}

}