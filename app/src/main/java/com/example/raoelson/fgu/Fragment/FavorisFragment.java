package com.example.raoelson.fgu.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.raoelson.fgu.APiRest.ApiClient;
import com.example.raoelson.fgu.Activity.AnnuaireActivity;
import com.example.raoelson.fgu.Activity.MapsActivity;
import com.example.raoelson.fgu.Adapter.AnnuaireAdapter;
import com.example.raoelson.fgu.Adapter.FavorisAdapter;
import com.example.raoelson.fgu.Adapter.RecyclerAdapter;
import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.Model.Favoris;
import com.example.raoelson.fgu.Outils.ProgressBar;
import com.example.raoelson.fgu.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Raoelson on 25/08/2017.
 */

public class FavorisFragment extends Fragment {


    RecyclerView recyclerView;
    ApiClient apiClient;
    List<Contact> favorisList;
    SharedPreferences sharedpreferences;
    ProgressBar progressBar;
    String idUserConnecte;
    TextView text_vide;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment__favoris, container, false);
        recyclerView  = (RecyclerView) v.findViewById(R.id.myrecyclerview);
        apiClient = new ApiClient(getContext());
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        progressBar = new ProgressBar(getContext(), "France Guichet unique", "Chargment en cours...");
        favorisList = new ArrayList<>();
        idUserConnecte = sharedpreferences.getString("idUser", null);
        text_vide = (TextView) v.findViewById(R.id.text_vide);
        Affichage();
        return v;
    }
    public void Affichage() {
        progressBar.Show();
        Call<List<Contact>> call = apiClient.affichageFavoris(idUserConnecte);
        call.clone().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                chargementListe(response.body());
                progressBar.Dismiss();
                //Log.d("test", "message" + response.body());
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.d("test", "-fail--" + t.getMessage());
                progressBar.Dismiss();
            }
        });
    }
    public void chargementListe(List<Contact> contactList) {
        if(contactList.size() == 0){
            text_vide.setVisibility(View.VISIBLE);
        }else{
            text_vide.setVisibility(View.INVISIBLE);
        }
        FavorisAdapter adapter = new FavorisAdapter(getContext(), contactList,idUserConnecte,
                recyclerView,text_vide);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //fab.startAnimation(animation);
    }
}
