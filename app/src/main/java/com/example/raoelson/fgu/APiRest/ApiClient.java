package com.example.raoelson.fgu.APiRest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.Model.Favoris;
import com.example.raoelson.fgu.Outils.Message;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maoris on 16/05/2017.
 */

public class ApiClient {
    Context mContext;
    public ApiClient(Context context) {
        this.mContext = context;
    }

    public Retrofit getClient() {
        //String url = "http://10.0.3.2/FGU/";
        String url = "http://dev.startcheme.com/android/FGU/";
        //String url = "http://192.168.88.23/FGU/";
        Retrofit retrofit = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (Exception e) {
            Log.d("res", "test" + e.getMessage());
            return null;
        }
        return retrofit;
    }

    public Boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnectedOrConnecting()){
            if(networkInfo.getType()!=ConnectivityManager.TYPE_WIFI &&
                    networkInfo.getType()!=ConnectivityManager.TYPE_MOBILE){
               return false;
            }
            return true;
        }
        return false;
    }


    //
   /* public Call<List<Contact>> affichaResultat() {
        Retrofit retrofit = this.getClient();
        ApiInterface api = retrofit.create(ApiInterface.class);
        return api.getContactNear("loadcontact");
    }*/

    public Call<List<Contact>> affichageSeach(String id,String profession,String partenaire,
                                              String longitude,String latitude,String user) {
        Retrofit retrofit = this.getClient();
        ApiInterface api = retrofit.create(ApiInterface.class);
        return api.getContactNearSearch(id,profession,partenaire,longitude,latitude,user);
    }

    public Call<JsonObject> getInscription(String nom, String prenom, String email, String password) {
        Retrofit retrofit = this.getClient();
        ApiInterface api = retrofit.create(ApiInterface.class);
        return api.getInscription(email, password, nom, prenom);
    }

    public Call<Contact> getProfil(String id) {
        Retrofit retrofit = this.getClient();
        ApiInterface api = retrofit.create(ApiInterface.class);
        return api.getProfil(id);
    }

    public Call<Object> affichageSeach_(String id,String profession,String partenaire,
                                              String longitude,String latitude) {
        Retrofit retrofit = this.getClient();
        ApiInterface api = retrofit.create(ApiInterface.class);
        return api.getContactNearSearch_(id,profession,partenaire,longitude,latitude);
    }

    public Call<String> getModification(String id,String nom, String prenom, String email, String password,
                                        String adresse,String civilite) {
        Retrofit retrofit = this.getClient();
        ApiInterface api = retrofit.create(ApiInterface.class);
        return api.getModification(email, password, nom, prenom,id,adresse,civilite);
    }

    public Call<List<Contact>> affichageFavoris(String id) {
        Retrofit retrofit = this.getClient();
        ApiInterface api = retrofit.create(ApiInterface.class);
        return api.getFavories(id);
    }

    public Call<String> getModificationFavorie(String user,String contact,String action) {
        Retrofit retrofit = this.getClient();
        ApiInterface api = retrofit.create(ApiInterface.class);
        Log.d("test"," data dada "+"?user="+user +"&contact="+contact+"&longitude="+action);
        return api.getModificationFavorie(user,contact,action);
    }

}
