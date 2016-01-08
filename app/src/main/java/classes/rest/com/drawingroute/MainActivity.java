package classes.rest.com.drawingroute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import MapGraphics.MarkerStyleOptions;
import SQL.AdminSQLiteOpenHelper;
import Util.DirectionsJSONParser;
import Util.URLDownloader;


/**
 * Now will do it static again
 */
public class MainActivity extends FragmentActivity {

    private int zoom1 = 14;

    GoogleMap   map;
    ArrayList   markerPoints;

    double      currLatitude;
    double      currLongitude;

    boolean     cameraMoved = false;

    Marker[] dbMarkers;

    LatLng[] newMarkers;

    //DATABASE
    public void select( ) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper( this.getApplicationContext( ), "points", null, 1 );

        //Create and/or open a database that will be used for reading and writing.
        SQLiteDatabase db = admin.getWritableDatabase( );

        Cursor row = db.rawQuery( "SELECT id, name, latitude, longitude FROM client", null );

        dbMarkers = new Marker[ row.getColumnCount( ) ];
        newMarkers = new LatLng[ row.getColumnCount( ) ];
        for( int i = 0; i < row.getColumnCount( ); i++ ){
            if( row.moveToNext( ) ){
                dbMarkers[ i ] = map.addMarker( new MarkerOptions( )
                        .position( new LatLng( row.getDouble( 2 ), row.getDouble( 3 ) ) )
                        .title( "Id: " + row.getInt( 0 ) )
                        .snippet( "Nombre: " + row.getString( 1 ) )
                        .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE ) ) );
                newMarkers[ i ] = new LatLng( row.getDouble( 2 ), row.getDouble( 3 ) );
            }
        }
        db.close( );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing
        markerPoints = new ArrayList();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById( R.id.map );

        // Getting Map for the SupportMapFragment
        map = fm.getMap();

        if( map!=null ){

            // Enable MyLocation Button in the Map
            map.setMyLocationEnabled( true );

            select( );
            //Focus camera and stuff like that
            map.setOnMyLocationChangeListener(
                    new GoogleMap.OnMyLocationChangeListener() {
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

            // Setting onclick event listener for the map
            map.setOnMapClickListener( new GoogleMap.OnMapClickListener( ) {
                @Override
                public void onMapClick( LatLng point ) {
                    markerPoints.clear();
                    map.clear();
                    select( );

                    markerPoints.add( point );

                    //add marker push marker
                    map.addMarker( new MarkerOptions( )
                            .position( point )
                            .title( "Ubicación del sinestro" )
                            .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE ) ) );

                    // Checks, whether start and end locations are captured
                    //Draw lines from my point to the rest of the points

                    LatLng origin = (LatLng) markerPoints.get( 0 );

                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            //Call a similar ParserTask method that set the info to the clicked marker
                            //Would be better not ti show them since the beginning

                            return false;
                        }
                    });

                    //make bucle
                    for( int i = 0; i < dbMarkers.length; i++ ){
                        // Getting URL to the Google Directions API
                        LatLng dest = newMarkers[ i ];
                        String url = URLDownloader.getDirectionsUrl( origin, dest );

                        DownloadTask downloadTask = new DownloadTask( );

                        // Start downloading json data from Google Directions API
                        downloadTask.execute( url );
                    }

                }
            });
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
        @Override
        protected void onPostExecute( String result ) {
            super.onPostExecute( result );

            ParserTask parserTask = new ParserTask( );

            // Invokes the thread for parsing the JSON data
            parserTask.execute( result );

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
                pointData = DirectionsJSONParser.timeDistance( jObject );

            }catch(Exception e){
                e.printStackTrace( );
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

                    points.add( position );
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll( points );
                lineOptions.width( 4 );
                lineOptions.color( MarkerStyleOptions.polylineColor(  ) );
            }

            map.addPolyline( lineOptions );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}