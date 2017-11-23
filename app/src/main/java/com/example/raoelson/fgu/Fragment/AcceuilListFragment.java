package com.example.raoelson.fgu.Fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autocomplete.DetailsCallback;
import com.example.autocomplete.OnPlaceSelectedListener;
import com.example.autocomplete.PlacesAutocompleteTextView;
import com.example.autocomplete.model.AddressComponent;
import com.example.autocomplete.model.AddressComponentType;
import com.example.autocomplete.model.Place;
import com.example.autocomplete.model.PlaceDetails;
import com.example.raoelson.fgu.APiRest.ApiClient;
import com.example.raoelson.fgu.Adapter.AnnuaireAdapter;
import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.Outils.GPSTracker;
import com.example.raoelson.fgu.Outils.MapWrapperLayout;
import com.example.raoelson.fgu.Outils.MyRecyclerScroll;
import com.example.raoelson.fgu.Outils.OnInfoWindowElemTouchListener;
import com.example.raoelson.fgu.Outils.ProgressBar;
import com.example.raoelson.fgu.Outils.Utils;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Raoelson on 06/10/2017.
 */

public class AcceuilListFragment extends Fragment {
    ImageView btnMap;
    RecyclerView recyclerView;
    ApiClient apiClient;
    List<Contact> contactList;
    private static final String id_search = "id_search";
    GPSTracker gps = null;

    public AcceuilListFragment newInstance(String text) {
        AcceuilListFragment mFragment = new AcceuilListFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(id_search, String.valueOf(text));
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    //
    EditText EditQuery, textRechercheVille;
    ImageView btnRecherche, btnRechercheVille;
    LinearLayout linearRecherche, layout;
    Button btnAvocat_, btnAssurance_,
            btnBanque_, btnExprt_, btnCommunication_,
            btnNotaire_;
    ImageButton btnGetPosition;
    PlacesAutocompleteTextView mAutocomplete;
    int getPostion = 0;
    ProgressBar progressBar;
    SharedPreferences sharedpreferences;
    String idUserConnecte = null;
    LatLng latLngChemin = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view;
        view = inflater.inflate(R.layout.fragment_acceuil_list, null);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        idUserConnecte = sharedpreferences.getString("idUser", null);
        mAutocomplete = (PlacesAutocompleteTextView) view.findViewById(R.id.autocomplete_);
        EditQuery = (EditText) view.findViewById(R.id.EditQuery);
        textRechercheVille = (EditText) view.findViewById(R.id.textRechercheVille);
        btnMap = (ImageView) view.findViewById(R.id.btnMap);
        btnAvocat_ = (Button) view.findViewById(R.id.btnAvocat_);
        btnAssurance_ = (Button) view.findViewById(R.id.btnAssurance_);
        btnBanque_ = (Button) view.findViewById(R.id.btnBanque_);
        btnGetPosition = (ImageButton) view.findViewById(R.id.btnGetPosition);
        btnExprt_ = (Button) view.findViewById(R.id.btnExprt_);
        btnCommunication_ = (Button) view.findViewById(R.id.btnCommunication_);
        btnNotaire_ = (Button) view.findViewById(R.id.btnNotaire_);
        //fab = (FrameLayout) view.findViewById(R.id.myfab_main);
        apiClient = new ApiClient(getContext());
        EditQuery = (EditText) view.findViewById(R.id.EditQuery);
        btnRecherche = (ImageView) view.findViewById(R.id.btnRecherche);
        linearRecherche = (LinearLayout) view.findViewById(R.id.linearRecherche);
        layout = (LinearLayout) view.findViewById(R.id.layout);
        progressBar = new ProgressBar(getContext(), "France Guichet unique", "Chargment en cours...");
        btnRechercheVille = (ImageView) view.findViewById(R.id.btnRechercheVille);
        contactList = new ArrayList<>();
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AcceuilFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_fragmentholder, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        /*if(!getArguments().getString(id_search).equalsIgnoreCase("")){
            textRechercheVille.setText(getArguments().getString(id_search));
            recherche = getArguments().getString(id_search);
            List<Address> addressList = RechercheVille();
            if (addressList != null) {
                for (int i = 0; i < addressList.size(); i++) {
                    Address myAddress = addressList.get(i);
                    LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                    AfficheSearch("", latLng);
                }
            }
        }else {
            AfficheSearch("", new LatLng(48.856614, 2.352222));
        }*/
        AfficheSearch("", new LatLng(48.856614, 2.352222));
        AffichageRecherche();
        /*btnRechercheVille.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recherche = textRechercheVille.getText().toString();
                List<Address> addressList = RechercheVille();
                if (addressList != null) {
                    for (int i = 0; i < addressList.size(); i++) {
                        Address myAddress = addressList.get(i);
                        LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                        AfficheSearch("", latLng);
                    }
                }
            }
        });*/
        /*textRechercheVille.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    EditQuery.setSingleLine(true);
                    if (!textRechercheVille.getText().toString().equalsIgnoreCase("")) {
                        recherche = textRechercheVille.getText().toString();
                        List<Address> addressList = RechercheVille();
                        if (addressList != null) {
                            for (int i = 0; i < addressList.size(); i++) {
                                Address myAddress = addressList.get(i);
                                LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                                AfficheSearch("", latLng);
                            }
                        }
                    }
                    //recherche(EditQuery.getText().toString());
                    return false;
                }
                return false;
            }
        });*/

        bntRecherche();
        mAutocomplete.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                mAutocomplete.setSingleLine(true);
                mAutocomplete.setSelection(mAutocomplete.getText().toString().length());
                progressBar.Show();
                mAutocomplete.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(final PlaceDetails details) {
                        Log.d("test", "details " + details.address_components.size());
                        for (AddressComponent component : details.address_components) {
                            for (AddressComponentType type : component.types) {
                                List<Address> addressList = RechercheVille(component.long_name);
                                if (addressList != null) {
                                    for (int i = 0; i < addressList.size(); i++) {
                                        Address myAddress = addressList.get(i);
                                        LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                                        AfficheSearch("", latLng);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(final Throwable failure) {
                        Log.d("test", "failure " + failure);
                        progressBar.Dismiss();
                    }
                });
            }
        });
        TextWatcher fieldValidatorTextWatcher_ = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (filterLongEnough()) {

                } else {
                    AfficheSearch("",
                            new LatLng(48.856614, 2.352222));
                }
            }

            private boolean filterLongEnough() {
                return mAutocomplete.getText().toString().trim().length() > 0;
            }
        };
        mAutocomplete.addTextChangedListener(fieldValidatorTextWatcher_);

        btnGetPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getPostion = 1;
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);

                } else {

                    showPosition();
                    //init(latLngPostion);
                }
            }
        });
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        gps = new GPSTracker(getContext());
        if (getPostion == 1) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getPostion = 0;
                progressBar.Show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showPosition();
                    }
                }, 3000);

            } else {
                Toast.makeText(getContext(), "Veuillez activer votre position svp!", Toast.LENGTH_SHORT).show();
            }
        }
        if (getPostion == 10) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getPostion = 0;
                progressBar.Show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showPositionMap();
                    }
                }, 3000);

            } else {
                getPostion = 0;
                Toast.makeText(getContext(), "Veuillez activer votre position svp!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void showPosition() {

        if (gps.getLocation() != null) {
            double latitude = gps.getLocation().getLatitude();
            double longitude = gps.getLocation().getLongitude();
            AfficheSearch("",
                    new LatLng(latitude, longitude));

        } else {
            return;
        }

    }

    public void showPositionMap() {
        Log.d("test"," postion "+gps.getLocation());
        if (gps.getLocation() != null) {
            double latitude = gps.getLocation().getLatitude();
            double longitude = gps.getLocation().getLongitude();
            progressBar.Dismiss();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                    "http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr="
                            + latLngChemin.latitude + "," + latLngChemin.longitude + ""));
            startActivity(intent);

        } else {
            return;
        }

    }

    public void AffichageRecherche() {
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
                    AfficheSearch(EditQuery.getText().toString(), new LatLng(48.856614, 2.352222));
                } else {
                    btnRecherche.setBackgroundResource(R.drawable.ic_search_black_24dp);
                    AfficheSearch("", new LatLng(48.856614, 2.352222));
                }
            }

            private boolean filterLongEnough() {
                return EditQuery.getText().toString().trim().length() > 0;
            }
        };
        EditQuery.addTextChangedListener(fieldValidatorTextWatcher);
    }

    public List<Address> RechercheVille(String recherche) {
        List<Address> addressList = null;
        //Log.d("location = ", recherche);

        if (!recherche.equals("")) {
            Geocoder geocoder = new Geocoder(getContext());
            try {
                addressList = geocoder.getFromLocationName(recherche, 5);

            } catch (IOException e) {
                e.printStackTrace();
            }

            /*if (addressList != null) {
                for (int i = 0; i < addressList.size(); i++) {
                    Address myAddress = addressList.get(i);
                    LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                    Log.d("test", " Longitude "
                            + myAddress.getLatitude() + " - " + myAddress.getLongitude());
                    *//*markerOptions.position(latLng);
                    mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));*//*
                }
            }*/
        }
        return addressList;
    }

    public void bntRecherche() {
        //((ViewGroup) recyclerView.getParent()).removeView(recyclerView);
        final LatLng _latLng = new LatLng(48.856614, 2.352222);
        btnAvocat_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AfficheSearch("avocat", _latLng);
            }
        });
        btnAssurance_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AfficheSearch("assurance", _latLng);

            }
        });
        btnBanque_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AfficheSearch("banque", _latLng);

            }
        });
        btnExprt_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AfficheSearch("expert comptable", _latLng);
            }
        });
        btnCommunication_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AfficheSearch("communication", _latLng);
            }
        });
        btnNotaire_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AfficheSearch("notaire", _latLng);
            }
        });

    }

    public void AfficheSearch(final String recherche, final LatLng latLng) {
        progressBar.Show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<List<Contact>> call = apiClient.affichageSeach("", recherche, "",
                        "" + latLng.longitude, "" + latLng.latitude, idUserConnecte);
                call.clone().enqueue(new Callback<List<Contact>>() {
                    @Override
                    public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                        chargementListe(response.body());

                    }

                    @Override
                    public void onFailure(Call<List<Contact>> call, Throwable t) {
                        Log.d("test", "-fail--" + t.getMessage());
                        progressBar.Dismiss();
                    }
                });
            }
        }, 300);
    }

    public void chargementListe(List<Contact> contactList) {
        if (contactList.size() == 0) {
            Toast.makeText(getContext(), "Aucun établisement trouvé ", Toast.LENGTH_SHORT).show();
        }
        AnnuaireAdapter adapter = new AnnuaireAdapter(getContext(), contactList, idUserConnecte);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.myrecyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        progressBar.Dismiss();
        //fab.startAnimation(animation);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d("test", " compteur ");
            double latitude = Double.parseDouble(intent.getStringExtra("latitude"));
            double longitiude = Double.parseDouble(intent.getStringExtra("longitude"));
            latLngChemin = new LatLng(latitude, longitiude);
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getPostion = 10;
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);

            }
            //Toast.makeText(getContext(), " ok ",Toast.LENGTH_SHORT).show();
        }
    };
}
