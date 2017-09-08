package com.example.raoelson.fgu.Activity;

/**
 * Created by Raoelson on 29/08/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
        OnMapReadyCallback {

    private GoogleMap mMap;
    private ApiClient apiClient;
    String recherche = "";
    TextView txtCount;
    ImageView btnRecherche;
    EditText EditQuery;

    MapWrapperLayout mapWrapperLayout;
    private ViewGroup infoWindow;
    TextView txtNomPrenom, txtPartenaire;
    TextView txtAdresse;
    TextView txtTaffe;
    TextView txtPhone;
    TextView txtEmail;
    ImageButton btnAppel, btnMessage;
    private OnInfoWindowElemTouchListener infoButtonListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        txtCount = (TextView) findViewById(R.id.txtCount);
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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20));
        infoButtonListener = new OnInfoWindowElemTouchListener(btnAppel,
                getResources().getDrawable(R.color.colorActive),
                getResources().getDrawable(R.color.colorActive)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
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
                        txtPhone.setText(json_data.getString("tel"));
                        txtEmail.setText(json_data.getString("email"));
                        txtNomPrenom.setText(json_data.getString("nom"));
                        if (!json_data.getString("partenaire").equalsIgnoreCase("")) {
                            txtPartenaire.setVisibility(View.VISIBLE);
                            txtPartenaire.setText("Notre Partenaire");
                        }else{
                            txtPartenaire.setVisibility(View.INVISIBLE);
                        }
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
        Call<List<Contact>> call = apiClient.affichageSeach(id,profession,"","","");
        call.clone().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.body().size() == 0) {
                    txtCount.setText("Aucun résultat trouvé");
                } else {
                    if (response.body().size() == 1) {
                        txtCount.setText(response.body().size() + " établissement trouvé");
                    } else {
                        txtCount.setText(response.body().size() + " établissements trouvés");
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

                        reponses = reponses + "\",\"nom\":\"";

                        reponses = reponses + contact.getC_nom() + " " + contact.getC_prenom()
                                + "\"}";
                        reponses = reponses + "]";
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng coordonnees = new LatLng(contact.getC_latitude(), contact.getC_longitude());
                        markerOptions.position(coordonnees);
                        markerOptions.snippet(reponses);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.placeholder32));
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
}
