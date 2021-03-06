package com.conductor.ptms.conductor;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StartTripActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    DatabaseReference databaseRouteBus;
    SharedPreferences shared;
    private Button start;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        databaseRouteBus = FirebaseDatabase.getInstance().getReference().child("Bus_Route_time");
        start = (Button) findViewById(R.id.StartTripButton);
        checkLocationPermission();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartTripActivity.this,LocationService.class);
                startService(i);
                shared = getSharedPreferences("Bus_Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
//                editor.putString("selectedPath", selectedRoutePath);
                editor.putInt("Total_tickets", 0);
                editor.putInt("Total_Fare",0);

                editor.commit();
                String bus_id = shared.getString("bus_id","0");
                String route_id = shared.getString("selectedPathRouteId","0");
                String formatedDate;
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat frmt = new SimpleDateFormat("HH:mm");
                formatedDate = frmt.format(c);
                //TODO: create class for bus_route_time table
                BusRouteTime busRouteTime = new BusRouteTime(bus_id,formatedDate,route_id);
                databaseRouteBus.child(bus_id).setValue(busRouteTime);
                Toast.makeText(getApplicationContext(),"Bus entry successfull",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StartTripActivity.this,IssueTicketActivity.class);
                startActivity(intent);
                return;

            }
        });
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(StartTripActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }



}
