package com.example.raoelson.fgu.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.raoelson.fgu.APiRest.ApiClient;
import com.example.raoelson.fgu.Outils.Message;
import com.example.raoelson.fgu.Outils.ProgressBar;
import com.example.raoelson.fgu.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Raoelson on 31/08/2017.
 */

public class InscriptionActivity extends AppCompatActivity {

    EditText _emailText, _nomText, _prenomText;
    EditText _passwordText, _passReplay;
    Button btnsave;
    String email = null;
    String password = null;
    String _password = null;
    String _nom = null;
    String _prenom = null;
    ApiClient apiClient;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = new ProgressBar(this,"France Guichet Unique","Chargement en cours...");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        _nomText = (EditText) findViewById(R.id.edtNom);
        _prenomText = (EditText) findViewById(R.id.edtPrenom);
        _emailText = (EditText) findViewById(R.id.edtEmail);
        _passwordText = (EditText) findViewById(R.id.edtPasswrd);
        _passReplay = (EditText) findViewById(R.id.edtPasswrdReply);
        btnsave = (Button) findViewById(R.id.btnsave);

        apiClient = new ApiClient(getApplicationContext());

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = _emailText.getText().toString();
                password = _passwordText.getText().toString();
                _password = _passReplay.getText().toString();
                _nom = _nomText.getText().toString();
                _prenom = _prenomText.getText().toString();

                if (!validate()) {
                    return;
                }
                Inscription();
            }
        });
    }

    public void Inscription(){
        progressBar.Show();
        Call<JsonObject> call = apiClient.getInscription(_nom,_prenom,email,password);
        call.clone().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject obj = new JSONObject(response.body().toString());
                    if(obj.getBoolean("error") == false){
                        progressBar.Dismiss();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivityForResult(intent, 101);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        Toast.makeText(getApplicationContext(),"Votre compte a été créé avec succès",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Vous avez déjà un compte",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*if(response.body() == -1){
                    Toast.makeText(getApplicationContext(),"Vous avez déjà un compte",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Votre compte a été créé avec succès",Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.Dismiss();
                Log.d("test","fail"+t.getMessage());
            }
        });
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

    public boolean validate() {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Entrez une adresse mail valide");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Entre 4 et 10 caractères alphanumériques");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (_password.isEmpty()) {
            _passReplay.setError("Veuillez remplir ce champs svp");
            valid = false;
        } else {
            if (!password.equalsIgnoreCase(_password)) {
                _passReplay.setError("le mot de passe ne correspond pas ");
            } else {
                _passReplay.setError(null);
            }
        }
        if (_nom.isEmpty()) {
            _nomText.setError("Veuillez remplir ce champs svp");
            valid = false;
        } else {
            _nomText.setError(null);
        }
        if (_prenom.isEmpty()) {
            _prenomText.setError("Veuillez remplir ce champs svp");
            valid = false;
        } else {
            _prenomText.setError(null);
        }

        return valid;
    }
}
