package ar.com.gepp.mdqmaps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.PowerManager;

public class LocationTracker extends BroadcastReceiver {

    public static final String BROAD_CAST_NAME = "broadCastName";
    public static final String PARAM_LOCATION = "location";

    private PowerManager.WakeLock wakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pow = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pow.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();

        sendLocationToActivity(context);
    }

    private void sendLocationToActivity(Context context) {
        Location currentLocation = LocationProvider.getInstance().getCurrentLocation();
        if (currentLocation != null) {
            double[] location = {currentLocation.getLatitude(), currentLocation.getLongitude()};
            Intent i = new Intent(BROAD_CAST_NAME);
            i.putExtra(PARAM_LOCATION, location);
            context.sendBroadcast(i);
        }

    }
}