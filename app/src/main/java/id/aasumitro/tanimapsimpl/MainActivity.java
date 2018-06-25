package id.aasumitro.tanimapsimpl;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button mBtnAdd, mBtnManual, mBtnAuto;
    private TextView mLat, mLon, mAddress, mLatInit, mLonInit;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        initView();
        tvSetValue();
        btnOnPressed();

    }

    private void initView() {
        mBtnAdd = findViewById(R.id.btnShowLocation);
        mBtnManual = findViewById(R.id.btnSetManual);
        mBtnAuto = findViewById(R.id.btnSetOto);
        mLatInit = findViewById(R.id.textLat1);
        mLonInit = findViewById(R.id.textLon1);
        mLat = findViewById(R.id.textLat2);
        mLon = findViewById(R.id.textLon2);
        mAddress = findViewById(R.id.textAddress);
    }

    private void btnOnPressed() {
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = "getPosition";
                String latitude = mLatInit.getText().toString();
                String longitude = mLonInit.getText().toString();
                MapsActivity.start(MainActivity.this, status, latitude, longitude);
            }
        });

        mBtnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = "setPosition";
//                String latitude = "2.9321494";
//                String longitude = "122.8962316";
                String latitude = mLatInit.getText().toString();
                String longitude = mLonInit.getText().toString();
                MapsActivity.start(MainActivity.this, status, latitude, longitude);
            }
        });

        mBtnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationProvider(mLocationManager);
            }
        });
    }

    private void tvSetValue() {
        mLatInit.setText("1.4554847");
        mLonInit.setText("124.8250461");

    }

    private void getLocationProvider(LocationManager locationManager) {
        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            Boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            Boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled || !isNetworkEnabled) {
                Toast.makeText(MainActivity.this, "Network or GPS provider is disabled", Toast.LENGTH_LONG).show();
            } else {

                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1800000, //30 min = 1.8 million ms
                            50F,
                            new LoctListnImpl());
                    Log.d("GPS", "GPS Provider enable");

                    getLocationData(locationManager);

                }

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1800000, //30 min = 1.8 million ms
                            50F,
                            new LoctListnImpl());
                    Log.d("Network", "Network Provider enable");

                    getLocationData(locationManager);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLocationData(LocationManager locationManager) {
        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            Geocoder geo = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> address = geo.getFromLocation(latitude, longitude, 1);
            String country = address.get(0).getCountryName();
            String state = address.get(0).getAdminArea();
            String city = address.get(0).getSubAdminArea();
            String districts = address.get(0).getLocality();
            String urbanVillage = address.get(0).getSubLocality();
            String addresses = address.get(0).getAddressLine(0);
            String postalCode = address.get(0).getPostalCode();
            String knownName = address.get(0).getFeatureName();

            String fullAddress =
                    country + ", "
                    + state + ", "
                    + city + ", "
                    + districts + ", "
                    + urbanVillage + ", "
                    + addresses + ", "
                    + postalCode + ", "
                    + knownName ;

            mLat.setText(String.valueOf(latitude));
            mLon.setText(String.valueOf(longitude));
            mAddress.setText(fullAddress);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
