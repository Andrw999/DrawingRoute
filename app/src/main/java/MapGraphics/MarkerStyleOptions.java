package MapGraphics;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.Random;

/**
 * Created by andres on 17/12/15.
 */
public class MarkerStyleOptions {

    public static BitmapDescriptor getMarkerColor( ){
        BitmapDescriptor color = null;
        Random random = new Random( );
        int min = 0;
        int max = 8;
        int index = random.nextInt( ( max - min ) + 1 ) + min;
        switch ( index ){
            case 0:
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN );
                break;
            case 1:
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE );
                break;
            case 2:
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN );
                break;
            case 3:
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET );
                break;
            case 4:
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE );
                break;
            case 5:
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE );
                break;
            case 6:
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE );
                break;
            case 7:
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW );
                break;
            case 8:
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED );
                break;
        }
        return color;
    }

    public static int polylineColor(  ){
        int color = 0;
        int index = 0;
        int min = 0;
        int max = 6;
        Random random = new Random( );
        index = random.nextInt( ( max - min ) + 1 ) + min;
        switch ( index ){
            case 0:
                color = Color.MAGENTA;
                break;
            case 1:
                color = Color.YELLOW;
                break;
            case 2:
                color = Color.DKGRAY;
                break;
            case 3:
                color = Color.BLUE;
                break;
            case 4:
                color = Color.GRAY;
                break;
            case 5:
                color = Color.GREEN;
                break;
            case 6:
                color = Color.BLACK;
                break;
        }
        return color;
    }
}
