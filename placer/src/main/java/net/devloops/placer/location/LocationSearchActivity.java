package net.devloops.placer.location;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.app.ActivityCompat;

import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.CameraPosition;
import com.google.android.libraries.maps.model.LatLng;
import com.google.gson.Gson;

import net.devloops.placer.R;
import net.devloops.placer.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Odey M. Khalaf <odey@devloops.net> on 07/23/19.
 */
public class LocationSearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener {
    double lat, lng;
    private ImageView headerBack, clearAddressField;
    private TextView headerText, btnDone;
    private ProgressBar progressBar;
    private AppCompatAutoCompleteTextView txtEnterAddress;
    private LocationManager locationManager;
    private GoogleMap map;
    private LatLng latLng;
    private ProgressDialog progressDialog;
    private View mapView;
    private final String TAG = "LocationSearchActivity";

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_location_search_new);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.parseColor(getString(R.color.header_background_color)));

        locationManager = new LocationManager(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapView = mapFragment.getView();
            mapFragment.getMapAsync(this);
        }
        init();
    }

    private void init() {
        progressBar = findViewById(R.id.progress);
        headerBack = findViewById(R.id.header_back);
        headerText = findViewById(R.id.header_text);
        btnDone = findViewById(R.id.btnDone);
        txtEnterAddress = findViewById(R.id.txtEnterAddress);
        clearAddressField = findViewById(R.id.clear_address_field);
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtEnterAddress.setText(getIntent().getStringExtra("Address") == null ? "" : getIntent().getStringExtra("Address"));
        txtEnterAddress.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        txtEnterAddress.setOnItemClickListener(this);
        headerBack.setOnClickListener(v -> onBackPressed());
        btnDone.setOnClickListener(view -> {
            try {
                if (txtEnterAddress.length() > 0) {
                    Intent returnIntent = new Intent();
                    LatLng latLng = new LatLng(LocationSearchActivity.this.latLng.latitude, LocationSearchActivity.this.latLng.longitude);
                    returnIntent.putExtra("address", txtEnterAddress.getText().toString());
                    returnIntent.putExtra("LatLng", latLng);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.msg_no_location), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        txtEnterAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String place = txtEnterAddress.getText().toString();
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading.....");
                progressDialog.setCancelable(true);
                progressDialog.show();

                if (!place.equals(""))
                    getLatLngFromPlace(place);

            }
            return false;
        });
        clearAddressField.setOnClickListener(v -> txtEnterAddress.setText(""));
    }

    private void getLocation() {
        locationManager.triggerLocation(new LocationManager.LocationListener() {
            @Override
            public void onLocationAvailable(LatLng latLng) {
                setMap(latLng);
            }

            @Override
            public void onFail(Status status) {
                getLocation();
            }
        });
    }

    private void setMap(LatLng latLng) {
        try {
            this.latLng = latLng;
            progressBar.setVisibility(View.GONE);
            hideKeyboard(this);
            try {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                map.setMyLocationEnabled(true);
//                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.getUiSettings().setRotateGesturesEnabled(true);
                map.getUiSettings().setCompassEnabled(false);

                if (mapView != null &&
                        mapView.findViewById(Integer.parseInt("1")) != null) {

                    View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    // position on top right
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                    layoutParams.setMargins(0, 350, 40, 0);
                }

                int zoomLavel = 15;
                if (getIntent().hasExtra("Lat")) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(Double.parseDouble(Objects.requireNonNull(getIntent().getStringExtra("Lat"))), Double.parseDouble(Objects.requireNonNull(getIntent().getStringExtra("Lang")))))      // Sets the center of the map to Mountain View
                            .zoom(zoomLavel)                   // Sets the zoom
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(this.latLng)      // Sets the center of the map to Mountain View
                            .zoom(zoomLavel)                   // Sets the zoom
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            } catch (Exception e) {
                Logger.e("Erorr::", "" + e.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        Location Data = GooglePlacesAutocompleteAdapter.getLocationFromAddress(this, str);
        try {
            if (txtEnterAddress.length() > 0) {
                setMap(new LatLng(Data.getLatitude(), Data.getLongitude()));
            } else
                Toast.makeText(this, getString(R.string.msg_no_location), Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * GEt's address from latlng
     *
     * @param latLng LatLng
     */
    public void getAddressFromLatLng(final LatLng latLng) {
        if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
            Single.create((SingleOnSubscribe<String>) e -> {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true" + "&key=" + getResources().getString(R.string.google_maps_key))
                        .build();

                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    assert response.body() != null;
                    String responce = Objects.requireNonNull(response.body()).string();
                    Logger.i(TAG, responce);
                    LocationEntity locationEntity = new Gson().fromJson(responce, LocationEntity.class);
                    if (locationEntity.getResults() != null && locationEntity.getResults().size() > 0) {
                        e.onSuccess(locationEntity.getResults().get(0).getFormatted_address());
                        LocationEntity.Location location = locationEntity.getResults().get(0).getGeometry().getLocation();
                        LocationSearchActivity.this.latLng = new LatLng(location.getLat(), location.getLng());
                    }
                }
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<String>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NonNull String s) {
                            txtEnterAddress.setText(s);
                            Logger.i(TAG, s);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    /**
     * GEt's LatLng from address
     *
     * @param place String
     */
    public void getLatLngFromPlace(String place) {
        try {
            Geocoder selected_place_geocoder = new Geocoder(this);
            List<Address> address;
            address = selected_place_geocoder.getFromLocationName(place, 5);
            if (address == null) {
                Toast.makeText(this, R.string.txt_cannot_find_address, Toast.LENGTH_SHORT).show();
            } else {
                Address location = address.get(0);
                lat = location.getLatitude();
                lng = location.getLongitude();
                Logger.i(TAG, location.getCountryName());
            }
            progressDialog.hide();
        } catch (Exception e) {
            e.printStackTrace();
            fetchLatLongFromService fetch_latlng_from_service_abc = new fetchLatLongFromService(
                    place.replaceAll("\\s+", ""));
            fetch_latlng_from_service_abc.execute();
            progressDialog.hide();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null && getContext() != null) {
            map.setOnCameraIdleListener(this);
            map.setOnCameraMoveStartedListener(this);
            map.setOnCameraMoveListener(this);
            map.setOnCameraMoveCanceledListener(this);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
            try {
                getLocation();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    @Override
    public void onCameraIdle() {
        if (latLng != null) {
            try {
                LatLng center = map.getCameraPosition().target;
                getAddressFromLatLng(center);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationManager.onRequestPermissionsResult(requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("StaticFieldLeak")
    public class fetchLatLongFromService extends AsyncTask<Void, Void, StringBuilder> {
        String place;

        fetchLatLongFromService(String place) {
            super();
            this.place = place;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            this.cancel(true);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            try {
                HttpURLConnection conn;
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + this.place + "&sensor=false" + "&key=" + getResources().getString(R.string.google_maps_key);

                URL url = new URL(googleMapUrl);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                return jsonResults;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    JSONObject jsonObj = new JSONObject(result.toString());
                    JSONArray resultJsonArray = jsonObj.getJSONArray("results");
                    JSONObject before_geometry_jsonObj = resultJsonArray.getJSONObject(0);

                    JSONObject geometry_jsonObj = before_geometry_jsonObj.getJSONObject("geometry");

                    JSONObject location_jsonObj = geometry_jsonObj.getJSONObject("location");

                    String lat_helper = location_jsonObj.getString("lat");
                    double lat = Double.parseDouble(lat_helper);
                    String lng_helper = location_jsonObj.getString("lng");
                    double lng = Double.parseDouble(lng_helper);
                    LatLng point = new LatLng(lat, lng);

                    setMap(point);
                    if (isFinishing() || isDestroyed())
                        progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}