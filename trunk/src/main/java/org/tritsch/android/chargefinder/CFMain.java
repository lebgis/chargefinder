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

import android.app.AlertDialog;

import android.content.DialogInterface;

import android.graphics.drawable.Drawable;

import android.location.Address;
import android.location.Geocoder;

import android.os.Bundle;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

/**
 * <code>CFMain</code> is the Main Activity class.
 *
 * This class sets up the main view, including the menue and all overlays on the map.
 *
 * @author <a href="mailto:roland@tritsch.org">Roland Tritsch</a>
 * @version $Id$
 *
 * @composed 1 - 1 MapView
 * @composed 1 - 1 CFChargeStationsOverlay
 * @composed 1 - 1 CFRangeOverlay
 */

public final class CFMain extends MapActivity {
    private static final String TAG = "CFMain";

    /**
     * <code>GEOPOINT_FACTOR</code> is used to convert from/to Long/Latitude.
     */
    protected static final double GEOPOINT_FACTOR = 1E6;

    /**
     * <code>mapView</code> shows the map on screen.
     */
    private MapView mapView;

    /**
     * <code>stationsOverlay</code> show all stations on the map.
     */
    private CFChargeStationsOverlay stationsOverlay;

    /**
     * <code>rangeOverlay</code> shows the range of the car on the map (as a grey circle).
     */
    private CFRangeOverlay rangeOverlay;

    /**
     * Creates a new <code>CFMain</code> instance.
     */
    public CFMain() {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: CFMain()");
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: CFMain()");
    }

    @Override
    protected boolean isRouteDisplayed() {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: isRouteDisplayed()");
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: isRouteDisplayed()");
        return false;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: onCreate()");
        super.onCreate(savedInstanceState);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "associate this activity with a/the (main) view");
        this.setContentView(R.layout.main);
 
        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "get the mapview and init/show the zoom buttons");
        mapView = (MapView) this.findViewById(R.id.mapview);
        Assert.assertNotNull(mapView);
        mapView.setBuiltInZoomControls(true);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "get and translate the default address into a location and get the default range");
        GeoPoint defaultLocation = getLocationFromAddress(getResources().getString(R.string.default_address));
        Assert.assertNotNull(defaultLocation);
        int defaultRange = Integer.parseInt(getResources().getString(R.string.default_range));

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "create an overlay with all the charging stations on it that are within a range from the location and display it");
        Drawable stationMarker = getResources().getDrawable(R.drawable.plug_tiny_red);
        Assert.assertNotNull(stationMarker);
        stationsOverlay = new CFChargeStationsOverlay(stationMarker, this);
        mapView.getOverlays().add(stationsOverlay);
        stationsOverlay.update(defaultLocation, defaultRange);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "create the range overlay (a transparent grey cricle) and display it");
        rangeOverlay = new CFRangeOverlay();
        rangeOverlay.setRange(defaultRange);
        mapView.getOverlays().add(rangeOverlay);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "make the map move to the default location");
        mapView.getController().animateTo(defaultLocation);
        mapView.getController().setZoom(Integer.parseInt(getResources().getString(R.string.default_zoom_level))); 
        mapView.invalidate();

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: onCreate()");
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: onCreateOptionsMenu()");

        MenuInflater inflater = getMenuInflater();
        Assert.assertNotNull(inflater);
        inflater.inflate(R.menu.main, menu);

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: onCreateOptionsMenu()");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: onOptionsItemSelected()");

        boolean done = false;
        switch (item.getItemId()) {
        case R.id.lookup:
            showDialogLookup();
            done = true;
            break;
        case R.id.about:
            showDialogAbout();
            done = true;
            break;
        default:
            done = super.onOptionsItemSelected(item);
            break;
        }

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: onOptionsItemSelected()");
        return done;
    }

    /**
     * <code>showDialogLookup</code> builds the lookup dialog.
     * The Lookup Menue asks for an address and a range
     * and moves the map to that address.
     */
    private void showDialogLookup() {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: showDialogLookup()");

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "get/init the Lookup Dialog");
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
            public void onClick(final DialogInterface dialog, final int whichButton) {
                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: showDialogLookup.positiveButton.onClick()");

                if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "access the text fields to read the values");
                EditText addressEditText = (EditText) textEntryView.findViewById(R.id.address_edit);
                Assert.assertNotNull(addressEditText);
                EditText rangeEditText = (EditText) textEntryView.findViewById(R.id.range_edit);
                Assert.assertNotNull(rangeEditText);

                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Address: " + addressEditText.getText().toString());
                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Range: " + rangeEditText.getText().toString());

                GeoPoint location = getLocationFromAddress(addressEditText.getText().toString());
                if(location != null) {
                    int range = Integer.parseInt(rangeEditText.getText().toString());

                    if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "update the stations overlay ");   
                    stationsOverlay.update(location, range);
                    rangeOverlay.setRange(range);

                    mapView.getController().animateTo(location);
                    mapView.getController().setZoom(Integer.parseInt(getResources().getString(R.string.default_zoom_level))); 
                    mapView.invalidate();
                }

                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: showDialogLookup.positiveButton.onClick()");
                return;
            }
        });
        lookup.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: showDialogLookup.negativeButton.onClick()");
                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: showDialogLookup.negativeButton.onClick()");
            }
        });

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "show the lookup dialog");
        lookup.show();

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: showDialogLookup()");
        return;
    }

    /**
     * <code>showDialogAbout</code> builds and shows the About Dialog.
     */
    private void showDialogAbout() {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: showDialogAbout()");
        AlertDialog.Builder about = new AlertDialog.Builder(this);
        Assert.assertNotNull(about);
        about.setPositiveButton("OK", null);
        about.setTitle(R.string.about_title);
        about.setMessage(R.string.about_message);
        about.show();
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: showDialogAbout()");
        return;
    }

    /**
     * <code>getLocationFromAddress</code> translates an Address into a GeoPoint
     *
     * @param address a <code>String</code> value
     * @return a <code>GeoPoint</code> value
     */
    private GeoPoint getLocationFromAddress(final String address) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: getLocationFromAddress()");
        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Address: " + address);

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
        GeoPoint location = new GeoPoint((int) (addresses.get(0).getLatitude() * GEOPOINT_FACTOR), (int) (addresses.get(0).getLongitude() * GEOPOINT_FACTOR));
        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Location: " + location.toString());

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: getLocationFromAddress()");
        return location;
    }
}
