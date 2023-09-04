/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.qamp.app.R;

import java.util.Arrays;
import java.util.Locale;

public class MesiboMapActivity extends FragmentActivity implements OnMapReadyCallback {
    /* access modifiers changed from: private */
    public static LocationInfo mLast = null;
    private FusedLocationProviderClient fusedLocationClient;
    /* access modifiers changed from: private */
    public AutocompleteSupportFragment mAutocompleteFragment;
    /* access modifiers changed from: private */
    public boolean mCameraMoving = false;
    /* access modifiers changed from: private */
    public LocationInfo mCurrent = null;
    private TextView mCurrentTextView;
    /* access modifiers changed from: private */
    public Geocoder mGeoCoder = null;
    /* access modifiers changed from: private */
    public GoogleMap mMap;
    private Marker mMarker = null;
    /* access modifiers changed from: private */
    public LocationInfo mSelected = null;
    /* access modifiers changed from: private */
    public MapFragment mapFragment;

    public static class LocationInfo {
        String address = "";
        double lat = 0.0d;
        double lon = 0.0d;
        String name = "";
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, com.google.android.gms.maps.OnMapReadyCallback, com.qamp.app.MessagingModule.MesiboMapActivity, android.app.Activity, androidx.fragment.app.FragmentActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        MesiboMapActivity.super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_address);
        this.mCurrentTextView = (TextView) findViewById(R.id.sendCurrent);
        MesiboUI.Config conf = MesiboUI.getConfig();
        this.mCurrentTextView.setText(conf.sendCurrentLocation);
        ((TextView) findViewById(R.id.sendselected)).setText(conf.sendAnotherLocation);
        this.mCurrentTextView.setEnabled(false);
        this.mCurrentTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MesiboMapActivity.this.setResult(MesiboMapActivity.this.mCurrent);
            }
        });
        this.mGeoCoder = new Geocoder(this, Locale.getDefault());
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        View locationButton = this.mapFragment.getView().findViewWithTag("GoogleMapMyLocationButton");
        if (locationButton != null) {
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(10, 0);
            rlp.addRule(12, -1);
            rlp.setMargins(0, 0, 30, 30);
        }
        this.mapFragment.getMapAsync(this);
        setupAutoCompleteFragment();
    }

    private void setAutoCompleteBounds(LocationInfo l) {
        double degrees = l.lat - Math.toDegrees(100.0d / 6371.0d);
        double degrees2 = l.lat + Math.toDegrees(100.0d / 6371.0d);
        double degrees3 = l.lon - Math.toDegrees((100.0d / 6371.0d) / Math.cos(Math.toRadians(l.lat)));
        double degrees4 = l.lon + Math.toDegrees((100.0d / 6371.0d) / Math.cos(Math.toRadians(l.lat)));
    }

    private void setupAutoCompleteFragment() {
        this.mAutocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        this.mAutocompleteFragment.setPlaceFields(Arrays.asList(new Place.Field[]{Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ICON_URL, Place.Field.LAT_LNG, Place.Field.VIEWPORT}));
        this.mAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            public void onPlaceSelected(Place place) {
                LatLng l = place.getLatLng();
                MesiboMapActivity.this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 16.0f));
                MesiboMapActivity.this.mapFragment.getMapAsync(MesiboMapActivity.this);
                LocationInfo unused = MesiboMapActivity.this.mSelected = new LocationInfo();
                MesiboMapActivity.this.mSelected.address = place.getAddress();
                MesiboMapActivity.this.mSelected.name = place.getName();
                MesiboMapActivity.this.mSelected.lat = l.latitude;
                MesiboMapActivity.this.mSelected.lon = l.longitude;
                MesiboMapActivity.this.setMarker(MesiboMapActivity.this.mSelected);
            }

            public void onError(Status status) {
                if (status != null) {
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void setMarker(LocationInfo location) {
        LatLng l = new LatLng(location.lat, location.lon);
        if (this.mMarker != null) {
            this.mMarker.remove();
        }
        if (TextUtils.isEmpty(location.address)) {
            location.address = MesiboUI.getConfig().unknownTitle;
        }
        if (TextUtils.isEmpty(location.name)) {
            location.name = MesiboUI.getConfig().unknownTitle;
        }
        this.mMarker = this.mMap.addMarker(new MarkerOptions().position(l).title(location.name).snippet(location.address).icon(BitmapDescriptorFactory.defaultMarker(0.0f)));
        this.mMarker.showInfoWindow();
    }

    /* access modifiers changed from: private */
    public void getAddressFromGeocoder(final LocationInfo location) {
        if (!TextUtils.isEmpty(location.address)) {
            setMarker(location);
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        Address a = MesiboMapActivity.this.mGeoCoder.getFromLocation(location.lat, location.lon, 1).get(0);
                        if (a != null) {
                            location.address = a.getAddressLine(0);
                            location.name = a.getSubLocality();
                            MesiboMapActivity.this.mAutocompleteFragment.setCountry(a.getCountryCode());
                            MesiboMapActivity.this.setMarker(location);
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentLocationOnMap(LocationInfo location) {
        if (location != null) {
            this.mCurrentTextView.setEnabled(true);
            this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.lat, location.lon), 16.0f));
            getAddressFromGeocoder(location);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [com.qamp.app.MessagingModule.MesiboMapActivity, android.app.Activity] */
    private void setCurrentLocation() {
        if (this.mCurrent == null) {
            if (mLast != null) {
                setCurrentLocationOnMap(mLast);
            }
            try {
                this.fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    public void onSuccess(Location location) {
                        if (MesiboMapActivity.this.mCurrent != null) {
                            LocationInfo unused = MesiboMapActivity.this.mCurrent = new LocationInfo();
                            MesiboMapActivity.this.mCurrent.lat = location.getLatitude();
                            MesiboMapActivity.this.mCurrent.lon = location.getLongitude();
                        }
                        MesiboMapActivity.this.setCurrentLocationOnMap(MesiboMapActivity.this.mCurrent);
                        LocationInfo unused2 = MesiboMapActivity.mLast = MesiboMapActivity.this.mCurrent;
                    }
                });
                this.fusedLocationClient.getCurrentLocation(102, (CancellationToken) null).addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    public void onSuccess(Location location) {
                        LocationInfo unused = MesiboMapActivity.this.mCurrent = new LocationInfo();
                        MesiboMapActivity.this.mCurrent.lat = location.getLatitude();
                        MesiboMapActivity.this.mCurrent.lon = location.getLongitude();
                        MesiboMapActivity.this.setCurrentLocationOnMap(MesiboMapActivity.this.mCurrent);
                        LocationInfo unused2 = MesiboMapActivity.mLast = MesiboMapActivity.this.mCurrent;
                    }
                });
            } catch (SecurityException e) {
            }
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        setCurrentLocation();
        this.mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng latLng) {
                LocationInfo unused = MesiboMapActivity.this.mSelected = new LocationInfo();
                MesiboMapActivity.this.mSelected.lat = latLng.latitude;
                MesiboMapActivity.this.mSelected.lon = latLng.longitude;
                MesiboMapActivity.this.getAddressFromGeocoder(MesiboMapActivity.this.mSelected);
            }
        });
        this.mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            public void onCameraMove() {
                boolean unused = MesiboMapActivity.this.mCameraMoving = true;
            }
        });
        this.mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            public void onCameraIdle() {
                if (!MesiboMapActivity.this.mCameraMoving) {
                }
            }
        });
        this.mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                LocationInfo l = new LocationInfo();
                l.lat = marker.getPosition().latitude;
                l.lon = marker.getPosition().longitude;
                l.address = marker.getSnippet();
                l.name = marker.getTitle();
                MesiboMapActivity.this.setResult(l);
            }
        });
        try {
            this.mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
        }
    }

    /* access modifiers changed from: private */
    public void setResult(final LocationInfo l) {
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(l.lat, l.lon), 16.0f));
        if (this.mMarker != null) {
            this.mMarker.remove();
        }
        this.mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            public void onSnapshotReady(Bitmap bitmap) {
                try {
                    MessagingFragment.onMapBitmap(SettingsScalingUtilities.createScaledBitmap(bitmap, 800, 600, SettingsScalingUtilities.ScalingLogic.CROP));
                } catch (Exception e) {
                }
                Intent data = new Intent();
                data.putExtra("name", l.name);
                data.putExtra("address", l.address);
                data.putExtra("lat", l.lat);
                data.putExtra("lon", l.lon);
                if (0 != 0) {
                    data.putExtra("image", (byte[]) null);
                }
                MesiboMapActivity.this.setResult(-1, data);
                MesiboMapActivity.this.finish();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        MesiboMapActivity.super.onResume();
        if (this.mMap != null) {
        }
    }
}
