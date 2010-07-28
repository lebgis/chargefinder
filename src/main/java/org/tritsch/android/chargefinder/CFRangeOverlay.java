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

import com.google.android.maps.Overlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import com.google.android.maps.GeoPoint;

import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import android.util.Log;
import junit.framework.Assert;

public class CFRangeOverlay extends Overlay {
    private static final String TAG = "CFRangeOverlay";
    
    private Paint circlePaint = new Paint();
    private Paint borderPaint = new Paint();

    private int range = 10000;

    public CFRangeOverlay() {
        circlePaint.setARGB(64, 75, 75, 75);

        borderPaint.setARGB(255, 255, 255, 255);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(5);
    }

    public void setRange(int range) {
        this.range = range;
        return;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        GeoPoint center = mapView.getMapCenter();
        Projection projection = mapView.getProjection();
        Point p = projection.toPixels(center, null);
        float radius = projection.metersToEquatorPixels(range);

        canvas.drawCircle(p.x, p.y, radius, circlePaint);
        canvas.drawCircle(p.x, p.y, radius, borderPaint);

        super.draw(canvas, mapView, shadow);
        return;
    }
}
