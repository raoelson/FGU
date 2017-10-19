package com.example.raoelson.fgu.Fragment;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raoelson.fgu.APiRest.ApiClient;
import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.Outils.MapWrapperLayout;
import com.example.raoelson.fgu.Outils.OnInfoWindowElemTouchListener;
import com.example.raoelson.fgu.Outils.ProgressBar;
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

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Raoelson on 06/10/2017.
 */

public class AcceuilFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    ProgressBar progressBar;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    double latitude, longitude;
    MapWrapperLayout mapWrapperLayout;
    private ViewGroup infoWindow;
    ApiClient apiClient;
    TextView txtCountAccueil;
    SupportMapFragment mapFragment;
    TextView txtNomPrenom;
    TextView txtAdresse;
    TextView txtTaffe;
    TextView txtPhone;
    TextView txtEmail;
    TextView txtLongitude;
    TextView txtLatitude;
    TextView txtOuverture;
    TextView txtFermeture;
    ImageButton btnAppel, btnMessage, btnItinaire;
    ImageView _btnRecherche_;
    private OnInfoWindowElemTouchListener infoButtonListener;
    Button btnAvocat_, btnAssurance_,
            btnBanque_, btnExprt_, btnCommunication_,
            btnNotaire_;
    EditText EditQuery;
    Boolean test = false;
    Integer active_gps = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view;
        view = inflater.inflate(R.layout.fragment_acceuil, null);
        txtCountAccueil = (TextView) view.findViewById(R.id.txtCountAccueil);
        infoWindow = (ViewGroup) inflater.inflate(R.layout.item_annuaire_map, null);
        txtNomPrenom = (TextView) infoWindow.findViewById(R.id.nomPrenom);
        txtOuverture = (TextView) infoWindow.findViewById(R.id.ouverture);
        txtFermeture = (TextView) infoWindow.findViewById(R.id.fermture);
        txtAdresse = (TextView) infoWindow.findViewById(R.id.adresse);
        txtTaffe = (TextView) infoWindow.findViewById(R.id.taffe);
        txtPhone = (TextView) infoWindow.findViewById(R.id.phone);
        txtLatitude = (TextView) infoWindow.findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) infoWindow.findViewById(R.id.txtLongitude);
        txtEmail = (TextView) infoWindow.findViewById(R.id.txtemail);
        btnAppel = (ImageButton) infoWindow.findViewById(R.id.btnAppel);
        btnMessage = (ImageButton) infoWindow.findViewById(R.id.btnMessage);
        btnItinaire = (ImageButton) infoWindow.findViewById(R.id.btnItinaire);
        mapWrapperLayout = (MapWrapperLayout) view.findViewById(R.id.map_relative_layout);
        btnAvocat_ = (Button) view.findViewById(R.id.btnAvocat_);
        btnAssurance_ = (Button) view.findViewById(R.id.btnAssurance_);
        btnBanque_ = (Button) view.findViewById(R.id.btnBanque_);
        EditQuery = (EditText) view.findViewById(R.id.EditQuery__);
        _btnRecherche_ = (ImageView) view.findViewById(R.id._btnRecherche_);
        EditQuery = (EditText) view.findViewById(R.id.EditQuery__);
        btnExprt_ = (Button) view.findViewById(R.id.btnExprt_);
        btnCommunication_ = (Button) view.findViewById(R.id.btnCommunication_);
        btnNotaire_ = (Button) view.findViewById(R.id.btnNotaire_);
        apiClient = new ApiClient(getContext());
        progressBar = new ProgressBar(getContext(), "France Guichet unique", "Chargment en cours...");
        bntRecherche();
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            getActivity().finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                test = false;
                if (filterLongEnough()) {
                    _btnRecherche_.setBackgroundResource(R.drawable.ic_clear_black_24dp);
                    AffichageResultat(EditQuery.getText().toString(),
                            new LatLng(48.856614, 2.352222));
                } else {
                    _btnRecherche_.setBackgroundResource(R.drawable.ic_search_black_24dp);
                    AffichageResultat(EditQuery.getText().toString(),
                            new LatLng(48.856614, 2.352222));
                }
            }

            private boolean filterLongEnough() {
                return EditQuery.getText().toString().trim().length() > 0;
            }
        };
        EditQuery.addTextChangedListener(fieldValidatorTextWatcher);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
      /*  LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
            active_gps = 1;
        }*/
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void bntRecherche() {

        final LatLng _latLng = new LatLng(48.856614, 2.352222);
        btnAvocat_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test = false;
                progressBar.Show();
                AffichageResultat("avocat", _latLng);
            }
        });
        btnAssurance_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test = false;
                progressBar.Show();
                AffichageResultat("assurance", _latLng);

            }
        });
        btnBanque_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test = false;
                progressBar.Show();
                AffichageResultat("banque", _latLng);

            }
        });
        btnExprt_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test = false;
                progressBar.Show();
                AffichageResultat("expert comptable", _latLng);
            }
        });
        btnCommunication_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test = false;
                progressBar.Show();
                AffichageResultat("communication", _latLng);
            }
        });
        btnNotaire_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test = false;
                progressBar.Show();
                AffichageResultat("notaire", _latLng);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                /*ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);*/
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                /*ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);*/
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (mGoogleApiClient.isConnected()) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        test = true;
        if (active_gps == 0) {
            //init(latLng);
        } else if (active_gps == 1) {
           /* init(latLng);*/
            active_gps = 2;
        } else {
            return;
        }
        //init(latLng);
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mapWrapperLayout.init(googleMap, getPixelsFromDp(getContext(), 39 + 20));
        infoButtonListener = new OnInfoWindowElemTouchListener(btnAppel, getResources()
                .getDrawable(R.color.colorActive),
                getResources().getDrawable(R.color.colorActive)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                if (txtPhone.getText().toString().equalsIgnoreCase("null")) {
                    Toast.makeText(getContext(), "Cet établissement n'a pas du numéro de téléphone", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + txtPhone.getText().toString()));
                    v.getContext().startActivity(callIntent);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } catch (Exception ex) {
                    Toast.makeText(getContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                }
            }
        };
        btnAppel.setOnTouchListener(infoButtonListener);

        infoButtonListener = new OnInfoWindowElemTouchListener(btnMessage, getResources()
                .getDrawable(R.color.colorActive),
                getResources().getDrawable(R.color.colorActive)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                if (txtEmail.getText().toString().equalsIgnoreCase("null")) {
                    Toast.makeText(getContext(), "Cet établissement n'a pas une adresse email", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"" + txtEmail.getText().toString()});
                    i.putExtra(Intent.EXTRA_SUBJECT, "");
                    i.putExtra(Intent.EXTRA_TEXT, "");
                    try {
                        v.getContext().startActivity(Intent.createChooser(i, "Envoie du mail..."));
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    Toast.makeText(getContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                }
            }
        };
        btnMessage.setOnTouchListener(infoButtonListener);
        infoButtonListener = new OnInfoWindowElemTouchListener(btnItinaire, getResources()
                .getDrawable(R.color.colorActive),
                getResources().getDrawable(R.color.colorActive)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
              /*  if (mLastLocation == null) {
                    //Toast.makeText(getContext(), "Veuillez activer votre GPS svp!", Toast.LENGTH_SHORT).show();
                } else {
                    affichageDialogue();
                }*/
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                } else {
                   /* googleDirectionsUrl.append("origin=" + latitude + "," + longitude);
                    //46.214306,
                    //googleDirectionsUrl.append("&destination="+end_latitude+","+end_longitude);
                    googleDirectionsUrl.append("&destination=" + txtLatitude.getText().toString() + "," + txtLongitude.getText().toString());
*/
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                            "http://maps.google.com/maps?saddr="+latitude+","+longitude+"&daddr="
                                    + txtLatitude.getText().toString() + "," + txtLongitude.getText().toString() + ""));
                    startActivity(intent);
                }

            }
        };
        btnItinaire.setOnTouchListener(infoButtonListener);
        LatLng _latLng = new LatLng(48.856614, 2.352222);
        init(_latLng);
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public void init(LatLng latLng) {
        progressBar.Show();
        MapsInitializer.initialize(getContext());
        if (mapFragment != null) {
            mapFragment.onCreate(null);
            mapFragment.onResume();
        }
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                try {
                    JSONArray jArray = new JSONArray(marker.getSnippet());
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        txtAdresse.setText(json_data.getString("adresse"));
                        txtTaffe.setText(json_data.getString("profession"));
                        if (json_data.getString("email").equalsIgnoreCase("null")) {
                            txtEmail.setVisibility(View.GONE);
                            txtEmail.setText(json_data.getString("email"));
                        } else {
                            txtEmail.setText(json_data.getString("email"));
                        }

                        if (json_data.getString("tel").equalsIgnoreCase("null")) {
                            txtPhone.setVisibility(View.GONE);
                            txtPhone.setText(json_data.getString("tel"));
                        } else {
                            txtPhone.setText(json_data.getString("tel"));
                        }
                        txtNomPrenom.setText(json_data.getString("nom"));
                        txtLongitude.setText(json_data.getString("longitude"));
                        txtLatitude.setText(json_data.getString("latitude"));
                        txtOuverture.setText(Html.fromHtml("<b><i>Établissement ouvert: <i></b>") + json_data.getString("ouverture"));
                        txtFermeture.setText(Html.fromHtml("<b><i>Fermé: <i></b>") + json_data.getString("fermeture"));
                        //Log.d("test"," "+json_data.getString("ouverture"));
                        /*

                        txtFermeture.setText(json_data.getString("fermeture"));*/
                        /*if (!json_data.getString("partenaire").equalsIgnoreCase("")) {
                            txtPartenaire.setVisibility(View.VISIBLE);
                            txtPartenaire.setText("Notre Partenaire");
                        } else {
                            txtPartenaire.setVisibility(View.INVISIBLE);
                        }*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                infoButtonListener.setMarker(marker);
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
        AffichageResultat("", latLng);
    }

    public void AffichageResultat(String recherche, final LatLng latLng) {
        mMap.clear();
        Call<List<Contact>> call = apiClient.affichageSeach("", recherche, "",
                "" + latLng.longitude, "" + latLng.latitude);
        call.clone().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.body().size() == 0) {
                    // txtCountAccueil.setVisibility(View.VISIBLE);
                    if (test) {
                        //txtCountAccueil.setText("Votre position n'a donné aucun résultat ");
                        test = true;
                        LatLng latLng = new LatLng(48.856614, 2.352222);
                        init(latLng);
                    } else {
                        //txtCountAccueil.setText("Aucun résultat trouvé");
                        test = false;
                    }

                    progressBar.Dismiss();

                } else {
                    if (test) {
                        test = true;
                    } else {
                        //txtCountAccueil.setText(response.body().size() + " établissement(s) trouvé(s)");
                        test = false;
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

                        reponses = reponses + contact.getC_etablissement() + "\"}";
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
                        CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(11).bearing(0).tilt(45).build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                }
                progressBar.Dismiss();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.d("test", "fail " + t.getMessage());
                progressBar.Dismiss();
            }
        });
        //mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
    }

    GoogleMap mMapDialog;

    public void affichageDialogue() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_maps);
        dialog.setCancelable(false);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.cancel_btn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.map_dialog);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
                dialog.dismiss();
            }
        });
        SupportMapFragment mapView = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map_dialog);
        MapsInitializer.initialize(getContext());
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
                dataTransfer[3] = getActivity();
                dataTransfer[2] = new LatLng(Double.parseDouble(txtLatitude.getText().toString())
                        , Double.parseDouble(txtLongitude.getText().toString()));
                getDirectionsData.execute(dataTransfer);
                //AffichageResultat("", latLng);
            }
        });

        //dialog.show();

        dialog.show();
    }

    private String getDirectionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        //googleDirectionsUrl.append("origin=" + 48.856614+ "," + 2.352222);
        googleDirectionsUrl.append("origin=" + latitude + "," + longitude);
        //46.214306,
        //googleDirectionsUrl.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsUrl.append("&destination=" + txtLatitude.getText().toString() + "," + txtLongitude.getText().toString());
        googleDirectionsUrl.append("&key=" + "AIzaSyCAcfy-02UHSu2F6WeQ1rhQhkCr51eBL9g");
        return googleDirectionsUrl.toString();
    }
}
