package com.example.raoelson.fgu.APiRest;


import com.example.raoelson.fgu.Model.Compte;
import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.Model.Favoris;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Maoris on 16/05/2017.
 */

public interface ApiInterface {

    /*@POST("webandroid.php")*/
    @FormUrlEncoded
    @POST("compte/authentification/")
    Call<JsonObject> getUrl(@Field("email") String email,
                            @Field("password") String password);


    @FormUrlEncoded
    @POST("contact/loadcontact")
    Call<List<Contact>> getContactNearSearch(@Field("where") String where,
                                             @Field("filtre") String filtre,
                                             @Field("partenaire") String partenaire,
                                             @Field("longitude") String longitude,
                                             @Field("latitude") String latitude,
                                             @Field("user") String user);

    /*@POST("webandroid.php")*/
    @FormUrlEncoded
    @POST("compte/inscription")
    Call<JsonObject> getInscription(@Field("email") String email,
                                    @Field("password") String password,
                                    @Field("nom") String nom,
                                    @Field("prenom") String prenom);

    @FormUrlEncoded
    @POST("compte/loadProfil")
    Call<Contact> getProfil(@Field("id") String id);


    @FormUrlEncoded
    @POST("contact/loadcontact")
    Call<Object> getContactNearSearch_(@Field("where") String where,
                                       @Field("filtre") String filtre,
                                       @Field("partenaire") String partenaire,
                                       @Field("longitude") String longitude,
                                       @Field("latitude") String latitude);

    @FormUrlEncoded
    @POST("compte/updateCompte")
    Call<String> getModification(@Field("email") String email,
                                 @Field("password") String password,
                                 @Field("nom") String nom,
                                 @Field("prenom") String prenom,
                                 @Field("id") String id,
                                 @Field("adresse") String adresse,
                                 @Field("civilite") String civilite);

    @FormUrlEncoded
    @POST("favoris/loadfavoris")
    Call<List<Contact>> getFavories(@Field("id") String id);

    @FormUrlEncoded
    @POST("favoris/updatefavoris")
    Call<String> getModificationFavorie(@Field("user") String user,
                                 @Field("contact") String contact,
                                 @Field("action") String action);
}
