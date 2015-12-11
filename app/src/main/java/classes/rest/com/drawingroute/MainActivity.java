package classes.rest.com.drawingroute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import Util.DirectionsJSONParser;
import Util.GPSTracker;
import Util.URLDownloader;

public class MainActivity extends FragmentActivity {

    private int zoom1 = 14;

    GoogleMap   map;
    ArrayList   markerPoints;

    double      currLatitude;
    double      currLongitude;

    boolean     cameraMoved = false;

    MarkerOptions markerOptions;
    MarkerOptions markerOptionsDes;

    Marker marker;
    Marker marker1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing
        markerPoints = new ArrayList();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();

        if(map!=null){

            // Enable MyLocation Button in the Map
            map.setMyLocationEnabled(true);
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    currLatitude = location.getLatitude( );
                    currLongitude = location.getLongitude( );

                    if ( !cameraMoved ){
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng( currLatitude, currLongitude ), zoom1);
                        map.moveCamera(cameraUpdate);
                        cameraMoved = true;
                    }
                }
            });
            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    latLng = new LatLng( currLatitude, currLongitude );
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom1);
                    map.animateCamera(cameraUpdate);
                }
            });

            //Add Automatic my location and a hardcode one
            //Add the random location to the map
            LatLng destiny = new LatLng( 20.679384, -103.361018 );

            markerPoints = new ArrayList();

            //Current Location

            GPSTracker gps = new GPSTracker( getApplicationContext( ) );

            if(gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                //Add pints
                markerPoints.add( destiny );
                markerPoints.add( new LatLng( latitude, longitude ) );

                markerOptions = new MarkerOptions( );
                markerOptionsDes = new MarkerOptions( );

                //Set destination position
                markerOptionsDes.position( destiny ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                //Set my location
                markerOptions.position( new LatLng( latitude, longitude ) ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            } else {
                gps.showSettingsAlert();
            }

            // Checks, whether start and end locations are captured
            if(markerPoints.size() >= 2){
                LatLng origin = (LatLng) markerPoints.get(0);
                LatLng dest = (LatLng) markerPoints.get(1);

                // Getting URL to the Google Directions API
                String url = URLDownloader.getDirectionsUrl( origin, dest );

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

                //Add markers with additional info

//                Marker marker = map.addMarker(
//                        markerOptions
//                                .title( "Ubicación" )
//                                .snippet( "Text" ) );
//
//                Marker marker1 = map.addMarker(
//                        markerOptionsDes
//                                .title( "Destino" )
//                                .snippet( "Text" ) );
//
//                marker.showInfoWindow( );
//                marker1.showInfoWindow( );
            }
        }
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = URLDownloader.downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        public String[] pointData;
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser( );

                // Starts parsing data
                routes = parser.parse(jObject);
                pointData = DirectionsJSONParser.TimeDistance( jObject );

            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j <path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width( 4 );
                lineOptions.color( Color.MAGENTA );

            }

            //Add markers with additional info

                marker = map.addMarker(
                        markerOptions
                                .title( pointData[2] )
                                .snippet( "Distancia: " + pointData[0]
                                        + " Tiempo de llegada: " + pointData[1] ) );

                marker1 = map.addMarker(
                        markerOptionsDes
                                .title( pointData[3] )
                                .snippet( "Distancia: " + pointData[0]
                                        + " Tiempo de llegada: " + pointData[1] ) );

                marker.showInfoWindow( );
                marker1.showInfoWindow( );

            map.addPolyline(lineOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}