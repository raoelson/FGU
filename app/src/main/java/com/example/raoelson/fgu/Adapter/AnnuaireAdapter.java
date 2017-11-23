package com.example.raoelson.fgu.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raoelson.fgu.APiRest.ApiClient;
import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.Outils.GPSTracker;
import com.example.raoelson.fgu.Outils.ProgressBar;
import com.example.raoelson.fgu.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Raoelson on 25/08/2017.
 */

public class AnnuaireAdapter extends RecyclerView.Adapter<AnnuaireAdapter.ViewHolder> {
    private Context mContext;
    private List<Contact> mContacts;
    // endregion
    int partieEnd = 0;
    ApiClient apiClient;
    String idConnecte;
    private int lastPosition = 0;
    ProgressBar progressBar;
    GPSTracker gps;

    // region Constructors
    public AnnuaireAdapter(Context context, List<Contact> contacts, String idConnecte) {
        mContext = context;
        mContacts = contacts;
        partieEnd = contacts.size();
        apiClient = new ApiClient(context);
        this.idConnecte = idConnecte;
        progressBar = new ProgressBar(mContext, "France Guichet unique", "Chargment en cours...");
        gps = new GPSTracker(mContext);

        // endregion
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_annuaire, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Contact contact = mContacts.get(position);
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.drawable.listviews);
            holder.btnAppel.setBackgroundResource(R.drawable.listviews);
            holder.btnMessage.setBackgroundResource(R.drawable.listviews);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.listview);
            holder.btnAppel.setBackgroundResource(R.drawable.listview);
            holder.btnMessage.setBackgroundResource(R.drawable.listview);
        }
        Integer imagePlaceholder = R.drawable.favorite_heart_button_blacn;
        if (contact.getFavoris() == 1) {
            imagePlaceholder = R.drawable.favorite_heart_button;
        }
        Drawable drawable = mContext.getResources().getDrawable(imagePlaceholder);
        holder.btnFavorite.setImageDrawable(drawable);
        if (contact != null) {
            setUpDisplayName(holder.txtNomPrenom, contact);
            setUpPro(holder.txtTaffe, contact);
            setUpDisplayAdresse(holder.txtAdresse, contact);
            setUpDisplayPhone(holder.linearLayoutPhone, holder.txtPhone, contact);
            setUpDisplayEmail(holder.linearLayoutEmail, holder.txtEmail, contact);
            setUpDisplayOuverture(holder.txtOuverture, contact);
            setUpDisplayFermeture(holder.txtFermeture, contact);
            holder.txtADRESSE.setText(Html.fromHtml("<b>Adresse :</b> "));
            /*if(contact.getC_mail().equalsIgnoreCase("null")){
                holder.txtEMAIL.setVisibility(View.INVISIBLE);
            }
            if(contact.getC_tel().equalsIgnoreCase("null")){
                holder.txtPHONE.setVisibility(View.INVISIBLE);
            }*/

            holder.txtPHONE.setText(Html.fromHtml("<b>Tel :</b> "));
            holder.txtEMAIL.setText(Html.fromHtml("<b>Email :</b> "));
        }
        holder.btnAppel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + getItem(position).getC_tel()));
                    v.getContext().startActivity(callIntent);
                    //mContext.CoverridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } catch (Exception ex) {
                    Toast.makeText(mContext, "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"" + getItem(position).getC_mail()});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    v.getContext().startActivity(Intent.createChooser(i, "Envoie du mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if ((partieEnd - 1) == position) {
            holder.LinearLayoutEnd.setVisibility(View.VISIBLE);
            holder.Totalannuaire.setText(partieEnd + " Etablisement(s)");
        } else {
            holder.LinearLayoutEnd.setVisibility(View.GONE);
        }

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPosition = position;
                progressBar.Show();
                updateModification(idConnecte, "" + contact.getC_id(), "" + contact.getFavoris());
            }
        });

        holder.btnIteration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent intent = new Intent("custom-message");
                    intent.putExtra("longitude",""+getItem(position).getC_longitude());
                    intent.putExtra("latitude",""+getItem(position).getC_latitude());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }else{
                    double latitude = gps.getLocation().getLatitude();
                    double longitude = gps.getLocation().getLongitude();
                    progressBar.Dismiss();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                            "http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr="
                                    + getItem(position).getC_latitude() + "," + getItem(position).getC_longitude() + ""));
                    mContext.startActivity(intent);
                }
                /*if(mCallback != null){
                    mCallback.onHandleSelection(1);
                }*/

            }
        });

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    private void setUpDisplayName(TextView tv, Contact contact) {
        String displayName = String.valueOf(contact.getC_etablissement());
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }


    private void setUpPro(TextView tv, Contact contact) {
        String displayName = contact.getC_profession();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }

    private void setUpDisplayAdresse(TextView tv, Contact contact) {
        String displayName = (contact.getC_adresse() + ", " + contact.getC_postal() +
                ", " + contact.getC_pays());
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }

    private void setUpDisplayPhone(LinearLayout linearLayout, TextView tv, Contact contact) {
        String displayName = contact.getC_tel();
        if (displayName != null) {
            if (!TextUtils.isEmpty(displayName)) {
                linearLayout.setVisibility(View.VISIBLE);
                tv.setText(displayName);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }

    }

    private void setUpDisplayEmail(LinearLayout linearLayout, TextView tv, Contact contact) {
        String displayName = contact.getC_mail();
        if (displayName != null) {
            if (!TextUtils.isEmpty(displayName)) {
                linearLayout.setVisibility(View.VISIBLE);
                tv.setText(displayName);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    private void setUpDisplayOuverture(TextView tv, Contact contact) {
        String displayName = contact.getC_ouverture();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }

    private void setUpDisplayFermeture(TextView tv, Contact contact) {
        String displayName = contact.getC_fermeture();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }

    public void updateModification(String user, String contact, String action) {
        Call<String> call = apiClient.getModificationFavorie(user, contact, action);
        //String reponse = apiClient.getModificationFavorie(user,contact,action);
        call.clone().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Log.d("test"," message"+response.body());
                if (response.body().equals("1")) {
                    Toast.makeText(mContext, "Ajouté à votre liste des favoris", Toast.LENGTH_LONG).show();
                } else if (response.body().equals("3")) {
                    Toast.makeText(mContext, "Retiré à votre liste des favoris", Toast.LENGTH_LONG).show();
                }
                LatLng _latLng = new LatLng(48.856614, 2.352222);
                ChargemntNew(_latLng);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("test", " message " + t.getMessage());
                progressBar.Dismiss();
            }
        });
    }

    public void ChargemntNew(LatLng latLng) {
        Call<List<Contact>> call = apiClient.affichageSeach("", "", "",
                "" + latLng.longitude, "" + latLng.latitude, idConnecte);
        call.clone().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                mContacts.clear();
                mContacts = response.body();
                notifyItemChanged(lastPosition);
                notifyDataSetChanged();
                progressBar.Dismiss();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.d("test", "fail " + t.getMessage());
                progressBar.Dismiss();
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomPrenom, txtOuverture;
        TextView txtAdresse, txtADRESSE, Totalannuaire;
        TextView txtTaffe, txtFermeture;
        TextView txtPhone, txtPHONE;
        TextView txtEmail, txtEMAIL;
        ImageButton btnAppel, btnMessage,btnIteration;
        LinearLayout linearLayoutEmail;
        LinearLayout linearLayoutPhone;
        LinearLayout LinearLayoutEnd;
        ImageView btnFavorite;

        public ViewHolder(View view) {
            super(view);
            txtNomPrenom = (TextView) view.findViewById(R.id.nomPrenom);
            txtAdresse = (TextView) view.findViewById(R.id.adresse);
            txtADRESSE = (TextView) view.findViewById(R.id.txtAdresse);
            txtTaffe = (TextView) view.findViewById(R.id.taffe);
            txtPhone = (TextView) view.findViewById(R.id.phone);
            txtPHONE = (TextView) view.findViewById(R.id.txtPhone);
            txtEmail = (TextView) view.findViewById(R.id.txtemail);
            Totalannuaire = (TextView) view.findViewById(R.id.Totalannuaire);
            txtEMAIL = (TextView) view.findViewById(R.id.txtEmail);
            btnAppel = (ImageButton) view.findViewById(R.id.btnAppel);
            btnMessage = (ImageButton) view.findViewById(R.id.btnMessage);
            btnIteration = (ImageButton) view.findViewById(R.id.btnIteration);
            txtOuverture = (TextView) view.findViewById(R.id.txtOuverture);
            txtFermeture = (TextView) view.findViewById(R.id.txtFermeture);
            txtADRESSE = (TextView) view.findViewById(R.id.txtAdresse);
            linearLayoutEmail = (LinearLayout) view.findViewById(R.id.linearEmail);
            linearLayoutPhone = (LinearLayout) view.findViewById(R.id.linearPhone);
            LinearLayoutEnd = (LinearLayout) view.findViewById(R.id.LinearLayoutEnd);
            btnFavorite = (ImageView) view.findViewById(R.id.btnFavorite);
        }
    }

    public Contact getItem(int paramInt) {
        // TODO Auto-generated method stub
        return mContacts.get(paramInt);
    }

}
