/*
 * Copyright (C) 2010 Roland Tritsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tritsch.android.chargefinder;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.GeoPoint;

import android.os.Bundle;
import android.graphics.drawable.Drawable;

import android.widget.LinearLayout;
import android.widget.EditText;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.LayoutInflater;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Geocoder;
import android.location.Address;

import java.util.List;
import java.util.Locale;

import android.util.Log;
import junit.framework.Assert;

public class CFMain extends MapActivity {
    private static final String TAG = "CFMain";
    private static final String DEFAULT_ADDRESS = "Eichendorff Weg 2, Eltville 65343, Germany";
    private static final int ZOOM_LEVEL = 10;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private MapView mapView;
    private MapController mapController;

    private List<Overlay> mapOverlays;
    private CFChargeStationsOverlay stationsOverlay;
    private CFRangeOverlay rangeOverlay;

    private GeoPoint location = null;
    private int range = 10000; 

    public CFMain() {
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.main); 

        mapView = (MapView) this.findViewById(R.id.mapview);
        Assert.assertNotNull(mapView);
        mapView.setBuiltInZoomControls(true);

        mapController = mapView.getController();
        Assert.assertNotNull(mapController);
        mapOverlays = mapView.getOverlays();
        Assert.assertNotNull(mapOverlays);

        location = getLocationFromAddress(DEFAULT_ADDRESS);
        Assert.assertNotNull(location);

        Drawable stationMarker = this.getResources().getDrawable(R.drawable.plug_tiny_red);
        Assert.assertNotNull(stationMarker);
        stationsOverlay = new CFChargeStationsOverlay(stationMarker, this);
        stationsOverlay.update(location, range);
        mapOverlays.add(stationsOverlay);

        rangeOverlay = new CFRangeOverlay();
        rangeOverlay.setRange(range);
        mapOverlays.add(rangeOverlay);

        mapController.animateTo(location);
        mapController.setZoom(ZOOM_LEVEL); 
        mapView.invalidate();

        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        Assert.assertNotNull(inflater);
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean done = false;
        switch (item.getItemId()) {
        case R.id.lookup:
            menuLookup();
            done = true;
            break;
        case R.id.about:
            menuAbout();
            done = true;
            break;
        default:
            done = super.onOptionsItemSelected(item);
            break;
        }
        return done;
    }

    private void menuLookup() {
        LayoutInflater factory = LayoutInflater.from(this);
        Assert.assertNotNull(factory);
        final View textEntryView = factory.inflate(R.layout.alert_dialog_lookup, null);
        Assert.assertNotNull(textEntryView);
        AlertDialog.Builder lookup = new AlertDialog.Builder(this);
        Assert.assertNotNull(lookup);
        lookup.setIcon(R.drawable.plug_tiny_gray);
        lookup.setTitle(R.string.alert_dialog_lookup_title);
        lookup.setView(textEntryView);
        lookup.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText addressEditText = (EditText) textEntryView.findViewById(R.id.address_edit);
                Assert.assertNotNull(addressEditText);
                EditText rangeEditText = (EditText) textEntryView.findViewById(R.id.range_edit);
                Assert.assertNotNull(rangeEditText);

                GeoPoint newLocation = getLocationFromAddress(addressEditText.getText().toString());
                if(newLocation != null) {
                    location = newLocation;
                    range = Integer.parseInt(rangeEditText.getText().toString());

                    stationsOverlay.update(location, range);
                    rangeOverlay.setRange(range);

                    mapController.animateTo(location);
                    mapController.setZoom(ZOOM_LEVEL); 
                    mapView.invalidate();
                }
                return;
            }
        });
        lookup.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        lookup.show();

        return;
    }

    private void menuAbout() {
        AlertDialog.Builder about = new AlertDialog.Builder(this);
        Assert.assertNotNull(about);
        about.setPositiveButton("OK", null);
        about.setTitle(R.string.about_title);
        about.setMessage(R.string.about_message);
        about.show();
        return;
    }

    private GeoPoint getLocationFromAddress(final String address) {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geoCoder.getFromLocationName(address, 1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        if(addresses.size() == 0) return null;
        Assert.assertTrue(addresses.size() == 1);
        GeoPoint location = new GeoPoint((int) (addresses.get(0).getLatitude() * 1E6), (int) (addresses.get(0).getLongitude() * 1E6));
        return location;
    }

    private class GPSUpdateLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Assert.assertNotNull(loc);
                
            GeoPoint p = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));
            mapController.animateTo(p);
            mapController.setZoom(ZOOM_LEVEL);                
            mapView.invalidate();

            return;
        }

        @Override
        public void onProviderDisabled(String provider) {
            return;
        }

        @Override
        public void onProviderEnabled(String provider) {
            return;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            return;
        }
    }
}