package MapGraphics;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by andres on 17/12/15.
 */
public class MarkerStyleOptions {

    public static BitmapDescriptor getMarkerColor(int index ){
        BitmapDescriptor color = null;
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
            default:
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED );
                break;
        }
        return color;
    }

    public static int polylineColor( int index ){
        int color = 0;
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
            default:
                color = Color.BLACK;
                break;
        }
        return color;
    }
}
