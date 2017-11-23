package com.example.raoelson.fgu.Activity;

/**
 * Created by Raoelson on 29/08/2017.
 */

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raoelson.fgu.APiRest.ApiClient;
import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.Outils.MapWrapperLayout;
import com.example.raoelson.fgu.Outils.OnInfoWindowElemTouchListener;
import com.example.raoelson.fgu.R;
import com.example.raoelson.fgu.test.GetDirectionsData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private ApiClient apiClient;
    String recherche = "";
    //TextView txtCount;
    ImageView btnRecherche;
    EditText EditQuery;

    MapWrapperLayout mapWrapperLayout;
    private ViewGroup infoWindow;
    TextView txtNomPrenom;
    TextView txtAdresse;
    TextView txtTaffe;
    TextView txtPhone;
    TextView txtEmail;
    TextView txtOuverture;
    TextView txtFermeture;
    TextView txtLongitude;
    TextView txtLatitude;
    ImageButton btnAppel, btnMessage,btnItinaire;
    private OnInfoWindowElemTouchListener infoButtonListener;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        /*txtCount = (TextView) findViewById(R.id.txtCount);*/
        btnRecherche = (ImageView) findViewById(R.id.btnRecherche_);
        EditQuery = (EditText) findViewById(R.id.EditQuery_);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.item_annuaire_map, null);

        txtNomPrenom = (TextView) infoWindow.findViewById(R.id.nomPrenom);
        txtAdresse = (TextView) infoWindow.findViewById(R.id.adresse);
        txtTaffe = (TextView) infoWindow.findViewById(R.id.taffe);
        txtPhone = (TextView) infoWindow.findViewById(R.id.phone);
        txtEmail = (TextView) infoWindow.findViewById(R.id.txtemail);
        btnAppel = (ImageButton) infoWindow.findViewById(R.id.btnAppel);
        btnMessage = (ImageButton) infoWindow.findViewById(R.id.btnMessage);
        btnItinaire = (ImageButton) infoWindow.findViewById(R.id.btnItinaire);

        txtOuverture = (TextView) infoWindow.findViewById(R.id.ouverture);
        txtFermeture = (TextView) infoWindow.findViewById(R.id.fermture);
        txtLatitude = (TextView) infoWindow.findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) infoWindow.findViewById(R.id.txtLongitude);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        apiClient = new ApiClient(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        recherche = extras.getString("search");
        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {

                    btnRecherche.setBackgroundResource(R.drawable.ic_clear_black_24dp);
                    AffichageResultat(EditQuery.getText().toString(), recherche);
                } else {
                    btnRecherche.setBackgroundResource(R.drawable.ic_search_black_24dp);
                    AffichageResultat("", recherche);
                }
            }

            private boolean filterLongEnough() {
                return EditQuery.getText().toString().trim().length() > 0;
            }
        };
        EditQuery.addTextChangedListener(fieldValidatorTextWatcher);
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20));
        /*infoButtonListener = new OnInfoWindowElemTouchListener(btnAppel,
                getResources().getDrawable(R.color.colorActive),
                getResources().getDrawable(R.color.colorActive)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                if(txtPhone.getText().toString().equalsIgnoreCase("null")){
                    Toast.makeText(getApplicationContext(),"Cet établissement n'a pas du numéro de téléphone",Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:"+txtPhone.getText().toString()));
                    v.getContext().startActivity(callIntent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

                catch (Exception ex){
                    Toast.makeText(getApplicationContext(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
                }
            }
        };
        btnAppel.setOnTouchListener(infoButtonListener);


        infoButtonListener = new OnInfoWindowElemTouchListener(btnMessage,
                getResources().getDrawable(R.color.colorActive),
                getResources().getDrawable(R.color.colorActive)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                if(txtEmail.getText().toString().equalsIgnoreCase("null")){
                    Toast.makeText(getApplicationContext(),"Cet établissement n'a pas une adresse email",Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{""+txtEmail.getText().toString()});
                    i.putExtra(Intent.EXTRA_SUBJECT, "");
                    i.putExtra(Intent.EXTRA_TEXT   , "");
                    try {
                        v.getContext().startActivity(Intent.createChooser(i, "Envoie du mail..."));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }

                }

                catch (Exception ex){
                    Toast.makeText(getApplicationContext(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
                }
            }
        };
        btnMessage.setOnTouchListener(infoButtonListener);
        infoButtonListener = new OnInfoWindowElemTouchListener(btnItinaire,
                getResources().getDrawable(R.color.colorActive),
                getResources().getDrawable(R.color.colorActive)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }else{
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                            "http://maps.google.com/maps?saddr="+latitude+","+longitude+"&daddr="
                                    + txtLatitude.getText().toString() + "," + txtLongitude.getText().toString() + ""));
                    startActivity(intent);
                }

            }
        };
        btnItinaire.setOnTouchListener(infoButtonListener);*/



        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                try {
                    JSONArray jArray = new JSONArray(marker.getSnippet());

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        txtAdresse.setText(json_data.getString("adresse"));
                        txtTaffe.setText(json_data.getString("profession"));
                        if(json_data.getString("email").equalsIgnoreCase("null")){
                            txtEmail.setVisibility(View.GONE);
                            txtEmail.setText(json_data.getString("email"));
                        }else{
                            txtEmail.setText(json_data.getString("email"));
                        }

                        if(json_data.getString("tel").equalsIgnoreCase("null")){
                            txtPhone.setVisibility(View.GONE);
                            txtPhone.setText(json_data.getString("tel"));
                        }else{
                            txtPhone.setText(json_data.getString("tel"));
                        }
                        txtNomPrenom.setText(json_data.getString("nom"));
                        txtLongitude.setText(json_data.getString("longitude"));
                        txtLatitude.setText(json_data.getString("latitude"));
                        txtOuverture.setText(Html.fromHtml("<b><i>Établissement ouvert: <i></b>")+json_data.getString("ouverture"));
                        txtFermeture.setText(Html.fromHtml("<b><i>Fermé: <i></b>")+json_data.getString("fermeture"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
        AffichageResultat("",recherche);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void AffichageResultat(String id,String profession){
        mMap.clear();
        Call<List<Contact>> call = apiClient.affichageSeach(id,profession,"","","","");
        call.clone().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.body().size() == 0) {
                    //txtCount.setText("Aucun résultat trouvé");
                } else {
                    if (response.body().size() == 1) {
                        //txtCount.setText(response.body().size() + " établissement trouvé");
                    } else {
                        //txtCount.setText(response.body().size() + " établissements trouvés");
                    }
                    Iterator iterator = response.body().iterator();

                    while (iterator.hasNext()) {
                        Contact contact = (Contact) iterator.next();
                        String reponses = "[";
                        reponses = reponses + "{\"adresse\":\"";

                        reponses = reponses + contact.getC_adresse() + ", " + contact.getC_postal() +
                                ", " + contact.getC_pays();

                        reponses = reponses + "\",\"profession\":\"";

                        reponses = reponses + contact.getC_profession();

                        reponses = reponses + "\",\"tel\":\"";

                        reponses = reponses + contact.getC_tel();

                        reponses = reponses + "\",\"email\":\"";

                        reponses = reponses + contact.getC_mail();

                        reponses = reponses + "\",\"partenaire\":\"";

                        reponses = reponses + contact.getC_type();

                        reponses = reponses + "\",\"longitude\":\"";

                        reponses = reponses + contact.getC_longitude();

                        reponses = reponses + "\",\"latitude\":\"";

                        reponses = reponses + contact.getC_latitude();

                        reponses = reponses + "\",\"ouverture\":\"";

                        reponses = reponses + contact.getC_ouverture();

                        reponses = reponses + "\",\"fermeture\":\"";

                        reponses = reponses + contact.getC_fermeture();

                        reponses = reponses + "\",\"nom\":\"";

                        reponses = reponses + contact.getC_etablissement()+ "\"}";
                        reponses = reponses + "]";
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng coordonnees = new LatLng(contact.getC_latitude(), contact.getC_longitude());
                        markerOptions.position(coordonnees);
                        markerOptions.snippet(reponses);
                        if (contact.getC_profession().equalsIgnoreCase("Avocat")) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        } else if (contact.getC_profession().equalsIgnoreCase("banque")) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        } else if (contact.getC_profession().equalsIgnoreCase("Assurance")) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        } else if (contact.getC_profession().equalsIgnoreCase("Notaire")) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                        } else if (contact.getC_profession().equalsIgnoreCase("Communication")) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        } else if (contact.getC_profession().equalsIgnoreCase("expert comptable")) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        } else {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordonnees));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordonnees, 6.5f));
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
            }
        });
    }

    GoogleMap mMapDialog;

    public void affichageDialogue() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_maps);
        dialog.setCancelable(false);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.cancel_btn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.map_dialog);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
                dialog.dismiss();
            }
        });
        SupportMapFragment mapView = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_dialog);
        MapsInitializer.initialize(this);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMapDialog = googleMap;
                mMapDialog.getUiSettings().setMapToolbarEnabled(false);
                mMapDialog.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //LatLng latLng = new LatLng(48.856614, 2.352222);
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.draggable(true);
                markerOptions.title("Votre Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mMapDialog.addMarker(markerOptions);
                markerOptions.position(new LatLng(Double.parseDouble(txtLatitude.getText().toString())
                        , Double.parseDouble(txtLongitude.getText().toString())));
                markerOptions.title("Votre Destination");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMapDialog.addMarker(markerOptions);

                CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(10).bearing(0).tilt(45).build();
                mMapDialog.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                Object dataTransfer[] = new Object[2];
                dataTransfer = new Object[4];
                String url = getDirectionsUrl();
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMapDialog;
                dataTransfer[1] = url;
                dataTransfer[3] = getApplicationContext();
                dataTransfer[2] = new LatLng(Double.parseDouble(txtLatitude.getText().toString())
                        , Double.parseDouble(txtLongitude.getText().toString()));
                getDirectionsData.execute(dataTransfer);
                //AffichageResultat("", latLng);
            }
        });
        dialog.show();

    }

    private String getDirectionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        //googleDirectionsUrl.append("origin=" + 48.856614+ "," + 2.352222);
        //Log.d("test"," "+ latitude+ "," + longitude);
        googleDirectionsUrl.append("origin=" + latitude+ "," + longitude);
        //46.214306,
        //googleDirectionsUrl.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsUrl.append("&destination=" + txtLatitude.getText().toString() + "," + txtLongitude.getText().toString());
        googleDirectionsUrl.append("&key=" + "AIzaSyCAcfy-02UHSu2F6WeQ1rhQhkCr51eBL9g");
        return googleDirectionsUrl.toString();
    }
}
