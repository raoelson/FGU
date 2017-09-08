package com.example.raoelson.fgu.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raoelson.fgu.APiRest.ApiClient;
import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.Outils.GPSTracker;
import com.example.raoelson.fgu.Outils.ProgressBar;
import com.example.raoelson.fgu.R;
import com.example.raoelson.fgu.Outils.MapWrapperLayout;
import com.example.raoelson.fgu.Outils.OnInfoWindowElemTouchListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
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

/**
 * Created by Raoelson on 25/08/2017.
 */

public class AcceuilFragment extends Fragment {
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    MapView mapView;

    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton1, infoButton2;
    static final LatLng latlng1 = new LatLng(28.5355, 77.3910);
    static final LatLng latlng2 = new LatLng(28.6208768, 77.3726377);
    MapWrapperLayout mapWrapperLayout;
    private ViewGroup infoWindow;


    ProgressBar progressBar;
    GPSTracker gpsTracker;
    private Location mLocation;
    Button btnAvocat_, btnAssurance_,
            btnBanque_, btnExprt_, btnCommunication_,
            btnNotaire_;

    TextView txtNomPrenom, txtPartenaire;
    TextView txtAdresse;
    TextView txtTaffe;
    TextView txtPhone;
    TextView txtEmail;
    ImageButton btnAppel, btnMessage;
    ImageView _btnRecherche_;

    ApiClient apiClient;

    LatLng latLng;

    EditText EditQuery;

    private OnInfoWindowElemTouchListener infoButtonListener;
    LinearLayout layout1;
    View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_acceuil, null);
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        layout1 = (LinearLayout) view.findViewById(R.id.layout);


        if (infoWindow == null) {
            infoWindow = (ViewGroup) inflater.inflate(R.layout.item_annuaire_map, null);
        } else {
            ((ViewGroup) infoWindow.getParent()).removeView(infoWindow);
        }


        btnAvocat_ = (Button) view.findViewById(R.id.btnAvocat_);
        btnAssurance_ = (Button) view.findViewById(R.id.btnAssurance_);
        btnBanque_ = (Button) view.findViewById(R.id.btnBanque_);
        EditQuery = (EditText) view.findViewById(R.id.EditQuery__);
        _btnRecherche_ = (ImageView) view.findViewById(R.id._btnRecherche_);

        btnExprt_ = (Button) view.findViewById(R.id.btnExprt_);
        btnCommunication_ = (Button) view.findViewById(R.id.btnCommunication_);
        btnNotaire_ = (Button) view.findViewById(R.id.btnNotaire_);
        bntRecherche();
        gpsTracker = new GPSTracker(getContext());
        apiClient = new ApiClient(getContext());
        progressBar = new ProgressBar(getContext(), "France Guichet unique", "Chargment en cours...");


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

                    _btnRecherche_.setBackgroundResource(R.drawable.ic_clear_black_24dp);
                    AffichageResultat(EditQuery.getText().toString(),
                            new LatLng(46.214306, 1.857436));
                } else {
                    _btnRecherche_.setBackgroundResource(R.drawable.ic_search_black_24dp);
                    AffichageResultat(EditQuery.getText().toString(),
                            new LatLng(46.214306, 1.857436));
                }
            }

            private boolean filterLongEnough() {
                return EditQuery.getText().toString().trim().length() > 0;
            }
        };
        EditQuery.addTextChangedListener(fieldValidatorTextWatcher);
        progressBar.Show();
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public void bntRecherche() {
        final LatLng _latLng = new LatLng(46.214306, 1.857436);
        btnAvocat_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.Show();
                AffichageResultat("avocat", _latLng);
            }
        });
        btnAssurance_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.Show();
                AffichageResultat("assurance", _latLng);

            }
        });
        btnBanque_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.Show();
                AffichageResultat("banque", _latLng);

            }
        });
        btnExprt_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.Show();
                AffichageResultat("expert comptable", _latLng);
            }
        });
        btnCommunication_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.Show();
                AffichageResultat("communication", _latLng);
            }
        });
        btnNotaire_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.Show();
                AffichageResultat("notaire", _latLng);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void init() {
        mapWrapperLayout = (MapWrapperLayout) view.findViewById(R.id.map_relative_layout);
        mapView = (MapView) view.findViewById(R.id.map);
        mapWrapperLayout.removeView(layout1);

        txtNomPrenom = (TextView) infoWindow.findViewById(R.id.nomPrenom);
        txtAdresse = (TextView) infoWindow.findViewById(R.id.adresse);
        txtTaffe = (TextView) infoWindow.findViewById(R.id.taffe);
        txtPhone = (TextView) infoWindow.findViewById(R.id.phone);
        txtEmail = (TextView) infoWindow.findViewById(R.id.txtemail);
        btnAppel = (ImageButton) infoWindow.findViewById(R.id.btnAppel);
        btnMessage = (ImageButton) infoWindow.findViewById(R.id.btnMessage);

        MapsInitializer.initialize(getContext());
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    Configuration();


                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }
                mLocation = gpsTracker.getLocation();

                if (mLocation == null) {
                    latLng = new LatLng(46.214306, 1.857436);
                } else {
                    latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                }

                mapWrapperLayout.init(googleMap, getPixelsFromDp(getContext(), 39 + 20));
                infoButtonListener = new OnInfoWindowElemTouchListener(btnAppel, getResources()
                        .getDrawable(R.color.colorActive),
                        getResources().getDrawable(R.color.colorActive)) {
                    @Override
                    protected void onClickConfirmed(View v, Marker marker) {
                        // Here we can perform some action triggered after clicking the button
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

                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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
                                txtPhone.setText(json_data.getString("tel"));
                                txtEmail.setText(json_data.getString("email"));
                                txtNomPrenom.setText(json_data.getString("nom"));
                                if (!json_data.getString("partenaire").equalsIgnoreCase("")) {
                                    txtPartenaire.setVisibility(View.VISIBLE);
                                    txtPartenaire.setText("Notre Partenaire");
                                } else {
                                    txtPartenaire.setVisibility(View.INVISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /*if(infoWindow.getParent()!=null){
                            ((ViewGroup)mapWrapperLayout.getParent()).removeView(mapWrapperLayout);
                        }*/

                        infoButtonListener.setMarker(marker);
                        mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                        return infoWindow;
                    }
                });
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                AffichageResultat("", latLng);
            }
        });

    }

    public void AffichageResultat(String recherche, final LatLng latLng) {
        mMap.clear();

        Call<List<Contact>> call = apiClient.affichageSeach("", recherche, "",
                "" + latLng.longitude, "" + latLng.latitude);
        call.clone().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.body().size() == 0) {
                    //txtCountAccueil.setText("Aucun résultat trouvé");
                } else {
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

                        reponses = reponses + "\",\"nom\":\"";

                        reponses = reponses + contact.getC_nom() + " " + contact.getC_prenom()
                                + "\"}";
                        reponses = reponses + "]";
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng coordonnees = new LatLng(contact.getC_latitude(), contact.getC_longitude());
                        markerOptions.position(coordonnees);
                        markerOptions.snippet(reponses);
                        if (!contact.getC_type().equalsIgnoreCase("")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.mapmarker));
                        } else {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.placeholder32));
                        }

                        mMap.addMarker(markerOptions);
                        CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(8).bearing(0).tilt(45).build();
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

    public void Configuration() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage(getContext().getResources().getString(R.string.permission_rationale_location));
            dialog.setPositiveButton(getContext().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getContext().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getContext().getString(R.string.refuser), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        Configuration();
                    }

                } else {
                    getActivity().finish();
                    Toast.makeText(getContext(), "\n" +
                            "Cette demande nécessite l'octroi de licences de localisation", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

