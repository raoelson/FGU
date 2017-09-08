package com.example.raoelson.fgu.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

/**
 * Created by Raoelson on 25/08/2017.
 */

public class AnnuaireFragment extends Fragment {
    RecyclerView recyclerView;
    ApiClient apiClient;
    List<Contact> contactList;
    TextView txtCount;

    //
    EditText EditQuery;
    ImageView btnRecherche;
    LinearLayout linearRecherche, layout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_annuaire, container, false);
        apiClient = new ApiClient(getContext());
        recyclerView = (RecyclerView) v.findViewById(R.id.myrecyclerview);

        //
         EditQuery = (EditText) v.findViewById(R.id.EditQuery);
        btnRecherche = (ImageView) v.findViewById(R.id.btnRecherche);
        //btnShow = (ImageView) v.findViewById(R.id.btnShow);
        linearRecherche = (LinearLayout) v.findViewById(R.id.linearRecherche);
        layout = (LinearLayout) v.findViewById(R.id.layout);
        /*spinnerZone = (Spinner) v.findViewById(R.id.spinnerZone);
        spinnerEtablissement = (Spinner) v.findViewById(R.id.spinnerEtablissement);*/

        txtCount = (TextView) v.findViewById(R.id.txtCount);
        contactList = new ArrayList<>();
        affichaResultat();
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
                }else{
                    btnRecherche.setBackgroundResource(R.drawable.ic_search_black_24dp);
                    AfficheSearch(EditQuery.getText().toString());
                }
            }
            private boolean filterLongEnough() {
                return EditQuery.getText().toString().trim().length() > 0;
            }
        };
        EditQuery.addTextChangedListener(fieldValidatorTextWatcher);
        return v;
    }
    private void affichaResultat(){
        /*Call<List<Contact>> call = apiClient.affichaResultat();
        call.clone().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                chargementListe(response.body());
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Toast.makeText(getContext(),"fail"+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });*/
    }

    public void ChargementRecherche(){
        final String recherche = EditQuery.getText().toString();


       /* btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShowFenetre) {
                    linearRecherche.setVisibility(View.VISIBLE);
                    btnShow.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    ShowFenetre = false;
                } else {
                    linearRecherche.setVisibility(View.GONE);
                    btnShow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    ShowFenetre = true;
                }
            }
        });*/

    }
    public void chargementListe(List<Contact> contactList){
        if(contactList.size() == 0){
            txtCount.setText("Aucun résultat trouvé");
        }else if(contactList.size() == 1){
            txtCount.setText(contactList.size()+" établissement trouvé");
        }else{
            txtCount.setText(contactList.size()+" établissements trouvés");
        }
        AnnuaireAdapter adapter = new AnnuaireAdapter(getContext(),contactList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public void AfficheSearch(String id){
        /*Call<List<Contact>> call = apiClient.affichageSeach(id,"","");
        call.clone().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                chargementListe(response.body());
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.d("test","-fail--"+t.getMessage());
            }
        });*/
    }

}
