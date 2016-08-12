package ar.com.gepp.mdqmaps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ar.com.gepp.mdqmaps.services.PointService;
import ar.com.gepp.mdqmaps.services.dto.CircleDTO;
import ar.com.gepp.mdqmaps.services.dto.PointDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String BASE_URL = "http://10.160.11.123:3000/";
    public static final String CIRCLE = "circle";

    private GoogleMap mMap;
    private LatLng defaultPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createAlarm();
        setContentView(R.layout.activity_maps);

        registerReceiver(broadcastReceiver, new IntentFilter(LocationTracker.BROAD_CAST_NAME));

        LocationProvider locationProvider = LocationProvider.getInstance();
        locationProvider.configureIfNeeded(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void createAlarm() {
        Intent intent = new Intent(this, LocationTracker.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
                LocationProvider.ONE_MINUTE, pendingIntent);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (defaultPosition == null) {
            defaultPosition = new LatLng(-38.000404, -57.556197);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this, "No se tienen permisos para Ubicación", Toast.LENGTH_LONG).show();
        }
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.addMarker(new MarkerOptions().position(defaultPosition).title("Posición Genérica"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, 16f));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final CircleDTO circle = getCircleDTO(intent);
            if (circle != null) {
                PointService service = getPointService();

                HashMap<String, Object> body = new HashMap<>();
                body.put(CIRCLE, circle);
                Call<List<PointDTO>> call = service.byAll(body);
                call.enqueue(new Callback<List<PointDTO>>() {
                    @Override
                    public void onResponse(Call<List<PointDTO>> call, Response<List<PointDTO>> response) {
                        drawMarkers(response.body(), circle);
                    }

                    @Override
                    public void onFailure(Call<List<PointDTO>> call, Throwable t) {
                        // Log error here since request failed
                    }
                });
            }
        }

        private PointService getPointService() {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            return retrofit.create(PointService.class);
        }

        @NonNull
        private CircleDTO getCircleDTO(Intent intent) {
            Bundle b = intent.getExtras();
            double[] location = b.getDoubleArray(LocationTracker.PARAM_LOCATION);
            CircleDTO circle = null;
            if (location != null) {
                circle = new CircleDTO();
                circle.setLat(location[0]);
                circle.setLon(location[1]);
                circle.setRadius(500);
            }
            return circle;
        }
    };

    /**
     * Draw the new markers
     *
     * @param points
     * @param circle
     */
    private void drawMarkers(final List<PointDTO> points, final CircleDTO circle) {
        mMap.clear();
        LatLng position;
        for (PointDTO point : points) {
            double algo = point.getLocation()[0];
            double algo2 = point.getLocation()[1];
            position = new LatLng(algo2, algo);
            mMap.addMarker(new MarkerOptions().position(position).title(point.getName()));
        }
        mMap.addMarker(new MarkerOptions().position(new LatLng(circle.getLat(), circle.getLon())).title("Mi ubicación").icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
        ));
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(circle.getLat(), circle.getLon()))
                .radius(circle.getRadius())
                .strokeWidth(1f)
                .strokeColor(Color.argb(255, 176, 196, 222))
                .fillColor(Color.argb(50, 175, 238, 238)));
    }

}
