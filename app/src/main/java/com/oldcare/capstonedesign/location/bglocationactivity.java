package com.oldcare.capstonedesign.location;

import static com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.oldcare.capstonedesign.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;


public class bglocationactivity extends AppCompatActivity {
    private String uid;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1981;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2981;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private LocationSettingsRequest mLocationSettingsRequest;
    private static final String TAG = bglocationactivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bglocationactivity);


        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        uid = preferences.getString("uid", "");

        init();

        Switch switchLocationUpdates = findViewById(R.id.switchLocationUpdates_inmap);

        // 서비스 실행중이면 체크된 상태로 표시
        if (isLocationServiceRunning()){
            switchLocationUpdates.setChecked(true);
        }

        Button btnCheck = findViewById(R.id.check_location);
        btnCheck.setOnClickListener(v -> checkLocation());


        // 스위치의 동작
        switchLocationUpdates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // "Start/Stop Location Updates" 스위치가 켜진 경우
                    // 위치 업데이트 시작하는 작업 수행
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(bglocationactivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                    } else {
                        startLocationService();
                    }
                } else {
                    // "Start/Stop Location Updates" 스위치가 꺼진 경우
                    stopLocationService();
                }
            }
        });
    } // end of onCreate

    private void init() {
        if (mFusedLocationClient == null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationRequest = new LocationRequest.Builder(PRIORITY_BALANCED_POWER_ACCURACY,30 * 1000).build();
         // 30 seconds in milliseconds

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(this, "Result OK", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "Result Cancel", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    private void checkLocation() {
        if (isPermissionGranted()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }
    }

    private boolean isPermissionGranted() {
        for (String permission : PERMISSIONS) {
            if (permission.equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                continue;
            }
            final int result = ContextCompat.checkSelfPermission(this, permission);

            if (PackageManager.PERMISSION_GRANTED != result) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(bglocationactivity.this);
                if (ActivityCompat.checkSelfPermission(bglocationactivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(bglocationactivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
            }
        }).addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                resolveLocationSettings(e);
            }
        });
    }

    public void resolveLocationSettings(Exception exception) {
        ResolvableApiException resolvable = (ResolvableApiException) exception;
        try {
            resolvable.startResolutionForResult(this, REQUEST_CODE_LOCATION_SETTINGS);
        } catch (IntentSender.SendIntentException e1) {
            e1.printStackTrace();
        }
    }

    // 서비스 시작함수
    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), Locationservice.class);
            intent.setAction(location_Constants.ACTION_START_LOCATION_SERVICE);
            intent.putExtra("uid", uid);
            PendingIntent pendingIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                pendingIntent = PendingIntent.getForegroundService(
                        getApplicationContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                );
            }
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), Locationservice.class);
            intent.setAction(location_Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);

            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (Locationservice.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startLocationService();
                        startLocationUpdates();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("위치 권한이 꺼져있습니다.");
                        builder.setMessage("[권한] 설정에서 위치 권한을 허용해야 합니다.");
                        builder.setPositiveButton("설정으로 가기", (dialog, which) -> {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }).setNegativeButton("종료", (dialog, which) -> finish());
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    break;
                }
            }
        }
    } // end of onRequestPermissionsResult

    private double longitude;
    private double latitude;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            longitude = locationResult.getLastLocation().getLongitude();
            latitude = locationResult.getLastLocation().getLatitude();
            mFusedLocationClient.removeLocationUpdates(locationCallback);

            Intent intent = new Intent(bglocationactivity.this, MapActivity_Oldman.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
            finish();
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.i(TAG, "onLocationAvailability - " + locationAvailability);
        }
    };

} // end of Activity