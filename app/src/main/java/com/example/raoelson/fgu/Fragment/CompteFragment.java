package com.example.raoelson.fgu.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.raoelson.fgu.APiRest.ApiClient;
import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.Outils.ProgressBar;
import com.example.raoelson.fgu.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Raoelson on 08/09/2017.
 */

public class CompteFragment extends Fragment{
    SharedPreferences sharedpreferences;
    String id;
    ApiClient apiClient;
    EditText nom, prenom, tel, adresse, EditConfirme, password, email;
    Spinner spinnerCivile;
    ArrayAdapter<CharSequence> adapter;
    Button btn_modifier;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_compte, container, false);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        id = sharedpreferences.getString("idUser", null);
        nom = (EditText) v.findViewById(R.id.EditNom);
        prenom = (EditText) v.findViewById(R.id.EditPrenom);
        tel = (EditText) v.findViewById(R.id.EditPhone);
        adresse = (EditText) v.findViewById(R.id.EditAdresse);
        EditConfirme = (EditText) v.findViewById(R.id.EditConfirme);
        password = (EditText) v.findViewById(R.id.EditPassword);
        email = (EditText) v.findViewById(R.id.EditEmail);
        spinnerCivile = (Spinner) v.findViewById(R.id.spinnerCivile);
        btn_modifier = (Button) v.findViewById(R.id.btn_modifier);
        apiClient = new ApiClient(getContext());
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.civile, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCivile.setAdapter(adapter);
        progressBar = new ProgressBar(getContext(),"France Guichet Unique",
                "Chargement en cours...");
        EnableText(false);
        AffichageProfil();
        btn_modifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_modifier.getText().toString().equalsIgnoreCase("activer")){
                    EnableText(true);
                    btn_modifier.setText("Modifier");
                }else{
                    if (!validate()) {
                        return;
                    }
                    updateCompte();
                    EnableText(false);
                    btn_modifier.setText("Activer");
                }

            }
        });

        return v;
    }

    public void updateCompte(){
        progressBar.Show();
        Call<String> call = apiClient.getModification(id,nom.getText().toString(),
                prenom.getText().toString(),email.getText().toString(),password.getText().toString(),
                adresse.getText().toString(),spinnerCivile.getSelectedItem().toString());
        call.clone().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(getContext(),"Votre profil a été bien modifié",Toast.LENGTH_SHORT).show();
                progressBar.Dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.Dismiss();
            }
        });
    }

    public void AffichageProfil() {
        progressBar.Show();
        Call<Contact> call = apiClient.getProfil(id);
        call.clone().enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                ChargementText(response.body());
                progressBar.Dismiss();
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                progressBar.Dismiss();
                Log.d("test", " fail" + t.getMessage());
            }
        });
    }

    public void ChargementText(Contact contact) {
        nom.setText(contact.getC_nom());
        prenom.setText(contact.getC_prenom());
        email.setText(contact.getC_mail());
        tel.setText(contact.getC_tel());
        //info.setText(contact.getC_description());
        adresse.setText(contact.getC_adresse());
        /*if (!contact.getC_civilite().equals(null)) {
            int position = 0;
            if (contact.getC_civilite().equalsIgnoreCase("madame")) {
                position = 1;
            }
            spinnerCivile.setSelection(position);
        }*/
    }

    public boolean validate() {
        boolean valid = true;

        if (email.getText().toString().isEmpty() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Entrez une adresse mail valide");
            valid = false;
        } else {
            email.setError(null);
        }

        if (password.getText().toString().length() > 0) {
            if (password.getText().toString().length() < 4 ||
                    password.getText().toString().length() > 10) {
                password.setError("Entre 4 et 10 caractères alphanumériques");
                valid = false;
            } else {
                password.setError(null);
            }

            if (!EditConfirme.getText().toString().equalsIgnoreCase(password.getText().toString())) {
                EditConfirme.setError("le mot de passe ne correspond pas ");
            } else {
                EditConfirme.setError(null);
            }
        } else {
            password.setError(null);
        }
        if (nom.getText().toString().isEmpty()) {
            nom.setError("Veuillez remplir ce champs svp");
            valid = false;
        } else {
            nom.setError(null);
        }
        if (prenom.getText().toString().isEmpty()) {
            prenom.setError("Veuillez remplir ce champs svp");
            valid = false;
        } else {
            prenom.setError(null);
        }

        return valid;
    }

    public void EnableText(boolean rep) {
        nom.setEnabled(rep);
        prenom.setEnabled(rep);
        email.setEnabled(rep);
        tel.setEnabled(rep);
        EditConfirme.setEnabled(rep);
        password.setEnabled(rep);
        //info.setText(contact.getC_description());
        adresse.setEnabled(rep);
        /*if (!contact.getC_civilite().equals(null)) {
            int position = 0;
            if (contact.getC_civilite().equalsIgnoreCase("madame")) {
                position = 1;
            }
            spinnerCivile.setSelection(position);
        }*/
    }
}
