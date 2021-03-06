package com.example.sulta.tplan.view.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sulta.tplan.R;
import com.example.sulta.tplan.database.SqlAdapter;
import com.example.sulta.tplan.model.Trip;
import com.example.sulta.tplan.model.TripNote;
import com.example.sulta.tplan.view.services.ReminderService;
import com.example.sulta.tplan.view.utilities.MyNotificationManager;

import java.util.ArrayList;
import java.util.Calendar;

public class HeadlessActivity extends Activity implements View.OnClickListener, LocationListener {

    private Intent recievingIntent;
    private Trip recievingTrip;
    private int tripId;
    private ReminderService myService;
    boolean isBound = false;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;

    private double longitude;
    private double latitude;

    private LocationManager locmgr;
    private MyNotificationManager myNotificationManager;
    ///views
    private TextView headlessTitle;
    private TextView headlessDate;
    private TextView from;
    private TextView headlessStartpoint;
    private TextView to;
    private TextView headlessEndpoint;
    private TextView notes;
    private TextView headlessNotes;
    private SqlAdapter db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headless);

        locmgr = (LocationManager) getSystemService((Context.LOCATION_SERVICE));
        myNotificationManager = MyNotificationManager.getInstance();

        recievingIntent = this.getIntent();
        tripId = recievingIntent.getIntExtra("tripId", -1);
        db=new SqlAdapter(HeadlessActivity.this);
        recievingTrip=db.selectTripById(tripId);
        headlessTitle = (TextView) findViewById(R.id.headless_title);
        headlessStartpoint = (TextView) findViewById(R.id.headless_startpoint);
        headlessEndpoint = (TextView) findViewById(R.id.headless_endpoint);
        headlessDate= (TextView) findViewById(R.id.headless_date);

        headlessNotes = (TextView) findViewById(R.id.headless_notes);
        findViewById(R.id.TripLaterBtn).setOnClickListener(this);
        findViewById(R.id.deleteTripBtn).setOnClickListener(this);
        findViewById(R.id.playTripDetailsBtn).setOnClickListener(this);


        headlessTitle.setText(recievingTrip.getTitle());
        headlessStartpoint.setText(recievingTrip.getStartPointName());
        headlessEndpoint.setText(recievingTrip.getEndPointName());
        headlessDate.setText(recievingTrip.getDate());
        if (recievingTrip.getNotes()!=null) {
            ArrayList<TripNote> noteArrayList  =new TripNote().convertFromStringToObject(recievingTrip.getNotes());
           String temp="";
            for (int i=0;i<noteArrayList.size();i++) {
                if(noteArrayList.get(i).isChecked()==true) {
                    temp += noteArrayList.get(i).getText()+" | Done\n";
                }
                else {
                    temp += noteArrayList.get(i).getText()+" | Wait\n";

                }
            }
            headlessNotes.setText(temp);
        }
        else
            headlessNotes.setText("there is no notes ");
    }

    private ServiceConnection myconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ReminderService.MyLocalBinder binder = (ReminderService.MyLocalBinder) iBinder;
            myService = binder.geService();
            isBound = true;
            snoozeAlarm();
            Toast.makeText(myService, "service bounded", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TripLaterBtn:
                //TODO implement
                myNotificationManager.CancelNotification(HeadlessActivity.this, recievingTrip.getId());
                Toast.makeText(HeadlessActivity.this, "You will be notified after 5 min", Toast.LENGTH_SHORT).show();
                finish();
                startService();
                break;
            case R.id.deleteTripBtn:
                //TODO implement
                myNotificationManager.CancelNotification(HeadlessActivity.this, recievingTrip.getId());
                Toast.makeText(HeadlessActivity.this, "your tirp is cancelled ", Toast.LENGTH_SHORT).show();
                finish();
                recievingTrip.setStatus("Cancelled");
                db.updateTrip(recievingTrip);
                break;
            case R.id.playTripDetailsBtn:
                //TODO implement
                Toast.makeText(HeadlessActivity.this, "start your trip on Maps", Toast.LENGTH_SHORT).show();

                finish();
                recievingTrip.setStatus("Done");
                db.updateTrip(recievingTrip);
                getCurrentLocation();

                break;
        }
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(HeadlessActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HeadlessActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //
            if (ActivityCompat.shouldShowRequestPermissionRationale(HeadlessActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(HeadlessActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_LOCATION
                );

            }
        } else {
            Location location = locmgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                //
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                myNotificationManager.CancelNotification(HeadlessActivity.this, recievingTrip.getId());
                HeadlessActivity.this.finish();
                Intent mintent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&daddr=" + recievingTrip.getEndPoint().getLatitude() + recievingTrip.getEndPoint().getLongitude()));
                startActivity(mintent);
            } else {
                locmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    locmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();

                            Toast.makeText(HeadlessActivity.this, "picked your location successfully", Toast.LENGTH_SHORT).show();
                            // getCompleteAddressString(location.getLatitude(), location.getLongitude());
                            Intent mintent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr=" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&daddr=" + recievingTrip.getEndPoint().getLatitude() + recievingTrip.getEndPoint().getLongitude()));
                            startActivity(mintent);
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    });

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(HeadlessActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                    }
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            myNotificationManager.CancelNotification(HeadlessActivity.this, recievingTrip.getId());
            HeadlessActivity.this.finish();
            Intent mintent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&daddr=" + recievingTrip.getEndPoint().getLatitude() + recievingTrip.getEndPoint().getLongitude()));
            startActivity(mintent);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locmgr.removeUpdates(this);
        if (isBound){
            stopService();
            isBound=false;
        }
    }
    private void startService() {
        Intent intent = new Intent(HeadlessActivity.this, ReminderService.class);
        bindService(intent, myconnection, Context.BIND_AUTO_CREATE);
    }

    public void stopService() {
        this.stopService(new Intent(this, ReminderService.class));
        this.unbindService(myconnection);
    }
    private void snoozeAlarm() {

        Toast.makeText(myService, "alarm snoozed", Toast.LENGTH_SHORT).show();
        myService.snoozeAlarm(this, recievingTrip.getId(), 1 * 60 * 1000l);//send request conde from trip id
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound){
        stopService();
        isBound=false;
        }
    }


}

