package br.com.zenker.calcula_percurso_e_tempo_gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.text.Editable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import static android.net.Uri.parse;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout distanceTextInputLayout;
    private TextInputLayout timeTextInputLayout;
    private TextInputLayout searchTextInputLayout;
    private double latitude;
    private double longitude;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_PERMISSION_CODE_GPS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        distanceTextInputLayout = findViewById(R.id.distanceTextInputLayout);
        timeTextInputLayout = findViewById(R.id.timeTextInputLayout);
        searchTextInputLayout = findViewById(R.id.searchTextInputLayout);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((v) -> {
            //Uri uri = Uri.parse("geo:%f, %f?q=%s");
            String search = searchTextInputLayout.getEditText().getText().toString();
            searchTextInputLayout.getEditText().setText("");
            Uri uri = parse(getString(R.string.uri_map, latitude, longitude, search));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
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
        };

    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void GrantGpsPermission(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_CODE_GPS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CODE_GPS)
        {
            if(grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, getString(R.string.no_gps_no_app), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void ActivateGps(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
                Toast.makeText(this, getString(R.string.active_gps), Toast.LENGTH_SHORT).show();
            }
            else {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 2000, 10, locationListener);
                Toast.makeText(this, getString(R.string.gps_on), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, getString(R.string.no_gps_no_app), Toast.LENGTH_SHORT).show();
        }
    }

    public void DeactivateGps(View view) {
        if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            locationManager.removeUpdates(locationListener);
            Toast.makeText(this, getString(R.string.gps_disabled), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, getString(R.string.gps_already_off), Toast.LENGTH_SHORT).show();
        }
    }
}
