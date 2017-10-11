package com.example.raoelson.fgu.Activity;

/**
 * Created by Raoelson on 29/08/2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raoelson.fgu.APiRest.ApiClient;
import com.example.raoelson.fgu.Adapter.AnnuaireAdapter;
import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnnuaireActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ApiClient apiClient;
    List<Contact> contactList;
    TextView txtCount;
    String recherche = "";

    //
    EditText EditQuery;
    ImageView btnRecherche;
    LinearLayout linearRecherche, layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annuaire);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiClient = new ApiClient(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.myrecyclerview);
        Bundle extras = getIntent().getExtras();
        recherche = extras.getString("search");
        //
        EditQuery = (EditText) findViewById(R.id.EditQuery);
        btnRecherche = (ImageView) findViewById(R.id.btnRecherche);
        //btnShow = (ImageView) v.findViewById(R.id.btnShow);
        linearRecherche = (LinearLayout) findViewById(R.id.linearRecherche);
        layout = (LinearLayout) findViewById(R.id.layout);
        /*spinnerZone = (Spinner) v.findViewById(R.id.spinnerZone);
        spinnerEtablissement = (Spinner) v.findViewById(R.id.spinnerEtablissement);*/

        txtCount = (TextView) findViewById(R.id.txtCount);
        contactList = new ArrayList<>();
        AfficheSearch(recherche);
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
                    AfficheSearch(EditQuery.getText().toString());
                } else {
                    btnRecherche.setBackgroundResource(R.drawable.ic_search_black_24dp);
                    AfficheSearch("");
                }
            }

            private boolean filterLongEnough() {
                return EditQuery.getText().toString().trim().length() > 0;
            }
        };
        EditQuery.addTextChangedListener(fieldValidatorTextWatcher);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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
    public void chargementListe(List<Contact> contactList) {
        if (contactList.size() == 0) {
            txtCount.setText("Aucun résultat trouvé");
        } else if (contactList.size() == 1) {
            txtCount.setText(contactList.size() + " établissement trouvé");
        } else {
            txtCount.setText(contactList.size() + " établissements trouvés");
        }
        AnnuaireAdapter adapter = new AnnuaireAdapter(getBaseContext(), contactList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public void AfficheSearch(String id) {
        Call<List<Contact>> call = apiClient.affichageSeach("",id,"","","");
        call.clone().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                chargementListe(response.body());
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.d("test", "-fail--" + t.getMessage());
            }
        });
    }
}
