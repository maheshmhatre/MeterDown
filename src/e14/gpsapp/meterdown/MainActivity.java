package e14.gpsapp.meterdown;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import e14.gpsapp.meterdown.R;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	TextView textFare;
	TextView textTime;
	TextView message;
	TextView txtAccuracy;
	LocationManager lm;
	LocationListener ll;
	double totalD=0;
	double fare=0;
	double idleRun=0;
	int mCount=0;
	int hour=0;
	int min=0;
	double dist1=0;
	double multiplier = 9.87;
	long time;
	double basedist;
	double baseFare;
	double ac=0;
	double countAc=0;
	boolean checkTime=false;

	boolean onoff = false;
	private String settingsTAB = "ApplicationSettings";
	private SharedPreferences prefs;
	AlertDialog.Builder myAlertDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Summary");
        myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface arg0, int arg1) {
        	 finish();
         }});
        
        Typeface face=Typeface.createFromAsset(getAssets(),
        "fonts/ABEAKRG.TTF");
        
        textFare = (TextView)findViewById(R.id.textfare);
        textTime = (TextView)findViewById(R.id.texttime);
      //  textFare.setTypeface(face);
      //  textTime.setTypeface(face);
        
        TextView t1 = (TextView)findViewById(R.id.textView1);
        TextView t2 = (TextView)findViewById(R.id.textView2);
        TextView t3 = (TextView)findViewById(R.id.textView3);
        message = (TextView)findViewById(R.id.message);
        txtAccuracy = (TextView)findViewById(R.id.textAccuracy);
        
        //txtAccuracy.setTypeface(face);
        //t1.setTypeface(face);
        //t2.setTypeface(face);
        //t3.setTypeface(face);
        //message.setTypeface(face);
        message.setText("Waiting for GPS signal..");
        
       final Button btnonoff = (Button)findViewById(R.id.btnonoff);
        
        btnonoff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				if(!onoff)
				{
					onoff = true;
					//btnonoff.setBackgroundResource(R.drawable.stop);
					btnonoff.setText("Stop");
				}
				else
				{
					onoff = false;
					myAlertDialog.setMessage("The distance travelled is: "+String.format("%.2f",totalD)+"km. Toatal fare is: Rs. "+Double.toString(fare)+"/-");
					myAlertDialog.show();
				}
				
			}
		});
        
        Button btnSettings = (Button)findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//setContentView(R.layout.settings);
				startActivity(new Intent("e14.gpsapp.meterdown.settings"));
			}
		});
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
 
            @Override
            public void run() {
                // TODO Auto-generated method stub
                timerMethod();
            }
        }, 0,1000);
        
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        ll = new mylocationlistener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,ll);
        
        
        
        prefs = getSharedPreferences(settingsTAB, 0);
        String city = prefs.getString("city", "Mumbai");
        String bkm = prefs.getString("basekm", "1.6");
        String bfare = prefs.getString("basefare", "15");
        String rate = prefs.getString("rate", "9.87");
        
        // Assign the value of preferences
        multiplier = Double.parseDouble(rate);
        basedist = Double.parseDouble(bkm);
        baseFare = Double.parseDouble(bfare);
        // get the current time to calc fare
		Date dt = new Date();
        int h = (dt.getHours())*100;
        int m = dt.getMinutes();
		int t = h+m;
		//message.setText(Integer.toString(t)+"  "+Double.toString(totalD)+" "+Double.toString(idleFare));
		if(t < 500 && !checkTime)
		{
			multiplier = multiplier*1.25;
			checkTime = true;
		}
		
		// check whether the gps is active
	    if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        buildAlertMessageNoGps();
	    }
    }
    
    private void timerMethod()
    {
    this.runOnUiThread(generate);
    }
    private Runnable generate= new Runnable() {
 
        @Override
        public void run() {
 
        	if(onoff)
        	{
        		if(mCount >= 60)
        		{
        			mCount = 0;
        			min++;
        			calcIdleTime(totalD);
        		}
        		if(min >= 60)
        		{
        			mCount = 0;
        			min = 0;
        			hour++;
        		}
            textTime.setText(Integer.toString(hour)+":"+Integer.toString(min)+":"+Integer.toString(mCount));
        		
        		mCount++;
        	}
        	else
        	{
        		mCount = 0;
        		textTime.setText(Integer.toString(mCount));
        	}
        }
    };
    
	public void calcIdleTime(double dist)
	{
		double diff = dist - dist1; 
		if(diff < 0.1)
		{
			//int ticker = (int)multiplier/10;
			//idleFare = idleFare + ticker;
			idleRun = idleRun + 0.1;
		}
		dist1 = dist;
		
	}
    
        class mylocationlistener implements LocationListener{
        	int c=0;
        	double lat1;
        	double lon1;
			@Override
			public void onLocationChanged(Location location) {
				if(location != null && onoff)
				{
					double pLong=location.getLongitude();
					double pLat=location.getLatitude();
				    float accuracy = location.getAccuracy();
				    setAccuracy(accuracy);
				    //long time = location.getTime();
				    //textTime.setText(Long.toString(time));
						if(c==0)
						{
							lat1=pLat;
							lon1=pLong;
							message.setText("");
							c++;
						}
						getRealDistance(pLat,pLong);

				
			}
			
			}

			public void getRealDistance(double lat2,double lon2)
			{
				double d2r = Math.PI / 180;
	            double dlong = (lon1 - lon2) * d2r;
	            double dlat = (lat1 - lat2) * d2r;
	            
	            double a = Math.pow(Math.sin(dlat / 2.0), 2)
                + Math.cos(lat2 * d2r) * Math.cos(lat1 * d2r)
                * Math.pow(Math.sin(dlong / 2.0), 2);
	            
	            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	            
	            double d = 6367 * c;
	            
	            lat1=lat2;
	            lon1=lon2;
	            totalD=totalD+d;
	            
	            //textDistance.setText(Double.toString(totalD));
				if(!onoff)
				{
				totalD = 0;	
				}
	            
	            if(totalD <= basedist)
	            {
	            	fare = baseFare;
	            }
	            else
	            {
	            	fare = ((totalD+idleRun) * multiplier);
	            }
	            
	            //textFare.setText(String.format("%.2f", Double.toString(totalD)));
	             
	            String fstring = String.format("%.2f", fare);
	            textFare.setText(fstring);
	            //message.setText(" distance "+Double.toString(totalD));
			}

			public void setAccuracy(double acc)
			{
				countAc++;
				ac = ac + acc;
				double avg = ac/countAc;
				
				if(avg <= 10)
				{
					txtAccuracy.setText("Excellent");
				}
				if(avg >10 && avg <=20)
				{
					txtAccuracy.setText("Good");
				}
				if(avg > 20 && avg <=30)
				{
					txtAccuracy.setText("Moderate");
				}
				if(avg > 30)
				{
					txtAccuracy.setText("Bad");
				}
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
        	
        	
        }
 

    @Override
		public void onBackPressed() {
    	AlertDialog.Builder warningDialog;
        warningDialog = new AlertDialog.Builder(this);
        warningDialog.setTitle("QUIT");
        warningDialog.setMessage("Are you sure you want to quit?");
        warningDialog.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface arg0, int arg1) {
        	 finish();
 			//super.onBackPressed();
         }});
        warningDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }});
        warningDialog.show();
		}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	
	  private void buildAlertMessageNoGps() {
		    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("Warning");
		    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
		                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		               }
		           })
		           .setNegativeButton("No", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
		                    dialog.cancel();
		               }
		           });
		    final AlertDialog alert = builder.create();
		    alert.show();
		}
	
}
