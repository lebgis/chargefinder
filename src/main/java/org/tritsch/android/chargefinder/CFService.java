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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import android.util.Log;
import junit.framework.Assert;

public class CFService {
    private static final String TAG = "CFServices";
    private static final String BASE_URL = "http://chargefinder.tritsch.org/stations.php";

    private static String getString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while((line = reader.readLine()) != null) { sb.append(line + "\n"); }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally { try { is.close(); } catch(Exception e) { e.printStackTrace(); Assert.fail(); }
        }
        return sb.toString();
    }

    public List<Station> lookup(final String point_x, final String point_y, final String radius) {
        List<Station> stations = new ArrayList<Station>();

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            String url = "" + BASE_URL + "?point_x=" + point_x + "&point_y=" + point_y + "&radius=" + radius;
            response = httpClient.execute(new HttpGet(url));
            Assert.assertNotNull(response);

            HttpEntity entity = response.getEntity();
            Assert.assertNotNull(entity);
            String resultString = getString(entity.getContent());
            Assert.assertNotNull(resultString);
            JSONObject resultObject = new JSONObject(resultString);
            Assert.assertNotNull(resultObject);
            JSONArray stationsObject = resultObject.getJSONArray("stations");
            Assert.assertNotNull(stationsObject);
            for(int i=0; i<stationsObject.length(); i++) {
                JSONObject station = stationsObject.getJSONObject(i);
                Assert.assertNotNull(station);

                Station newStation = new Station();
                newStation.name = station.getString("name");
                newStation.x = station.getDouble("st_x");
                newStation.y = station.getDouble("st_y");

                Assert.assertTrue(stations.add(newStation));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        return stations;
    }
                
    public CFService() {
    }
}