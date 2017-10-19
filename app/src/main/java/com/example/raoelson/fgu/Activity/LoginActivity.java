package com.example.raoelson.fgu.Activity;

/**
 * Created by Raoelson on 25/08/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.raoelson.fgu.APiRest.ApiClient;
import com.example.raoelson.fgu.APiRest.ApiInterface;
import com.example.raoelson.fgu.Outils.Message;
import com.example.raoelson.fgu.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    EditText _emailText;
    EditText _passwordText;
    Button _loginButton,inscriptionButton;
    ApiClient apiClient;
    Message message;
    SharedPreferences sharedpreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        apiClient = new ApiClient(getApplicationContext());
        message = new Message(this);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        _loginButton = (Button) findViewById(R.id.btn_login);
        inscriptionButton = (Button) findViewById(R.id.btn_inscription);
        _emailText  = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _emailText.setText(sharedpreferences.getString("email",null));
        _passwordText.setText(sharedpreferences.getString("motdepasse",null));
        if (!_emailText.getText().toString().equalsIgnoreCase("") &&
                !_passwordText.getText().toString().equalsIgnoreCase("")) {
            Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
            intent.putExtra("activation","1");
            startActivityForResult(intent, REQUEST_SIGNUP);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        inscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InscriptionActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }
    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authentification ...");
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        if(!apiClient.isOnline()){
            message.Dialogue("France Guichet Unique",
                    "Veuillez activer votre WIFI ou Données mobiles svp");
            return;
        }
        Retrofit retrofit = apiClient.getClient();
        ApiInterface api = retrofit.create(ApiInterface.class);
        Call<JsonObject> call = api.getUrl(_emailText.getText().toString()
                ,_passwordText.getText().toString());
        call.clone().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject obj = new JSONObject(response.body().toString());
                    if (obj.getBoolean("error") == false) {
                        JSONObject userObj = obj.getJSONObject("message");
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove("email");
                        editor.remove("idUser");
                        editor.remove("motdepasse");
                        editor.apply();
                        editor.putString("email",_emailText.getText().toString());
                        editor.putString("idUser",""+userObj.getInt("c_id"));
                        editor.putString("motdepasse",""+_passwordText.getText().toString());
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                        intent.putExtra("activation","1");
                        startActivityForResult(intent, REQUEST_SIGNUP);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }else{
                        message.Dialogue("Erreur","Veuillez vérifier votre login ou mot de passe svp !");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("test","---"+t.getMessage());
            }
        });
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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

        return valid;
    }



}
