package com.cs.googlemaproute;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashaikh on 8/8/2016.
 */
public class DrawRoute {

    double from_lat = 24.905954;
    double from_log = 67.0803505;
    double to_lat = 24.9053485;
    double to_log = 67.079119;
    String google_key = "";
    Polyline line;
    GoogleMap g_map;
    Context context;
    float zoom_level = 13.0f;
    String color_hash = "#05b1fb";
    public onDrawRoute callInterface;
    boolean show_loader = true;
    String loader_msg = "Please wait...";

    public DrawRoute(onDrawRoute dlg, Context ctx) {
        this.context = ctx;
        this.callInterface = dlg;
    }

    public static DrawRoute getInstance(onDrawRoute dlg, Context ctx) {
        return new DrawRoute(dlg, ctx);
    }

    public interface onDrawRoute {
        public void afterDraw(String result);
    }

    public DrawRoute setFromLatLong(double fromlat, double fromlog) {
        this.from_lat = fromlat;
        this.from_log = fromlog;
        return this;
    }

    public DrawRoute setToLatLong(double tolat, double tolog) {
        this.to_lat = tolat;
        this.to_log = tolog;
        return this;
    }

    public DrawRoute setGmapAndKey(String googlekey, GoogleMap gmap) {
        this.google_key = googlekey;
        this.g_map = gmap;
        return this;
    }

    public DrawRoute setZoomLevel(float zoomlevel) {
        this.zoom_level = zoomlevel;
        return this;
    }

    public DrawRoute setColorHash(String colorhash) {
        this.color_hash = colorhash;
        return this;
    }

    public DrawRoute setLoader(boolean showloader){
        this.show_loader = showloader;
        return this;
    }

    public DrawRoute setLoaderMsg(String loadermsg){
        this.loader_msg = loadermsg;
        return this;
    }

    public void run() {
        if (google_key.equals("")) {
            Toast.makeText(context, "Please set google map key", Toast.LENGTH_SHORT).show();
            return;
        }
        final String dUrl = generateURL(from_lat, from_log, to_lat, to_log, google_key);
        new connectAsyncTask(dUrl).execute();
    }

    public String generateURL(double sourcelat, double sourcelog, double destlat, double destlog, String google_key) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=" + google_key);
        return urlString.toString();
    }

    public void drawPath(String result) {

        try {
            //Convert string to jsona and parse
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            if (line != null) {
                line.remove();
            }
            line = g_map.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor(color_hash))
                    .geodesic(true)
            );
        } catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String urls;

        connectAsyncTask(String urlPass) {
            urls = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (show_loader == true) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(loader_msg);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String server_response = "";
            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return server_response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (show_loader == true) {
                progressDialog.hide();
            }
            if (result != null) {
                drawPath(result);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(from_lat, from_log)).zoom(zoom_level).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                g_map.animateCamera(cameraUpdate);
            }
            callInterface.afterDraw("" + result);
        }
    }


}
