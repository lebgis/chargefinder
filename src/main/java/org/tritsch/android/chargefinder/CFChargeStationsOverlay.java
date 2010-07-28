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

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import android.util.Log;
import junit.framework.Assert;

public class CFChargeStationsOverlay extends ItemizedOverlay {
    private static final String TAG = "CFChargeStationsOverlay";

    private ArrayList<OverlayItem> stations = new ArrayList<OverlayItem>();
    private Context mContext;

    public CFChargeStationsOverlay(Drawable defaultMarker, Context context) {
        super(boundCenterBottom(defaultMarker));
        mContext = context;
        Assert.assertNotNull(mContext);
    }

    public void addOverlay(OverlayItem station) {
        stations.add(station);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return stations.get(i);
    }

    @Override
    public int size() {
        return stations.size();
    }

    @Override
    protected boolean onTap(int i) {
        OverlayItem item = stations.get(i);
        Assert.assertNotNull(item);
        Toast.makeText(mContext, "" + item.getSnippet() + "(" + item.getTitle() + ")", Toast.LENGTH_LONG).show();
        return true;
    }

    public void update(GeoPoint location, int range) {
        String point_x = Double.toString(((double) location.getLatitudeE6()) / 1E6);
        String point_y = Double.toString(((double) location.getLongitudeE6()) / 1E6);
        String radius = Double.toString((range / 64.774831883062347 / 1000)*2);

        CFService service = new CFService();
        List<Station> stations = service.lookup(point_y, point_x, radius);
        Assert.assertNotNull(stations);
        Iterator<Station> i = stations.iterator();
        while(i.hasNext()) {
            Station station = i.next();
            try {            
                GeoPoint stationPoint = new GeoPoint((int) (station.y * 1E6), (int) (station.x * 1E6));
                OverlayItem stationOverlayItem = new OverlayItem(stationPoint, stationPoint.toString(), station.name);
                addOverlay(stationOverlayItem);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        }
        return;
    }
}
