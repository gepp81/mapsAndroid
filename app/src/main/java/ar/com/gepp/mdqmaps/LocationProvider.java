package ar.com.gepp.mdqmaps;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by guillermo on 11/08/16.
 */
public class LocationProvider {

    private static LocationProvider instance = null;
    private static Context context;

    public static final int ONE_MINUTE = 1000 * 60;

    private static Location currentLocation;

    private LocationProvider() {

    }

    public static LocationProvider getInstance() {
        if (instance == null) {
            instance = new LocationProvider();
        }

        return instance;
    }

    public void configureIfNeeded(Context ctx) {
        if (context == null) {
            context = ctx;
            configureLocationUpdates();
        }
    }

    private void configureLocationUpdates() {
        final LocationRequest locationRequest = createLocationRequest();
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                startLocationUpdates(googleApiClient, locationRequest);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
        googleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        });

        googleApiClient.connect();
    }

    private static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(ONE_MINUTE);
        return locationRequest;
    }

    private static void startLocationUpdates(GoogleApiClient client, LocationRequest request) {
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }
        });
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}
