package com.example.raoelson.fgu.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
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
import com.example.raoelson.fgu.Outils.ProgressBar;
import com.example.raoelson.fgu.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Raoelson on 25/08/2017.
 */

public class FavorisAdapter extends RecyclerView.Adapter<FavorisAdapter.ViewHolder> {
    private Context mContext;
    private List<Contact> mContacts;
    // endregion
    int partieEnd = 0;
    ApiClient apiClient;
    String idUserConnecte;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TextView textView;

    // region Constructors
    public FavorisAdapter(Context context, List<Contact> contacts,String idUserConnecte,
                          RecyclerView recyclerView,TextView textView) {
        mContext = context;
        mContacts = contacts;
        partieEnd = contacts.size();
        this.apiClient = new ApiClient(context);
        this.idUserConnecte = idUserConnecte;
        progressBar = new ProgressBar(context, "France Guichet unique", "Chargment en cours...");
        this.recyclerView = recyclerView;
        this.textView = textView;
        // endregion
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favories, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void shwHide(){
        if(mContacts.size() == 0){
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }else{
            textView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.drawable.listviews);
            holder.btnAppel.setBackgroundResource(R.drawable.listviews);
            holder.btnMessage.setBackgroundResource(R.drawable.listviews);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.listview);
            holder.btnAppel.setBackgroundResource(R.drawable.listview);
            holder.btnMessage.setBackgroundResource(R.drawable.listview);
        }

        final Contact contact = mContacts.get(position);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        mContext);

                alertDialogBuilder.setIcon(R.drawable.ic_clear_black_24dp);
                alertDialogBuilder.setTitle("Message de confirmation");
                alertDialogBuilder.setMessage("Voulez-vous vraiment supprimer ce favori ?");
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                progressBar.Show();
                                Call<String> call = apiClient.getModificationFavorie(idUserConnecte,
                                        ""+contact.getC_id(),"1");
                                call.clone().enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if(response.body().equals("3")){
                                            mContacts.remove(position);
                                            notifyDataSetChanged();
                                            if(mContacts.size() ==0 ){
                                                shwHide();
                                            }
                                        }
                                        progressBar.Dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        progressBar.Dismiss();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomPrenom, txtOuverture;
        TextView txtAdresse, txtADRESSE, Totalannuaire;
        TextView txtTaffe, txtFermeture;
        TextView txtPhone, txtPHONE;
        TextView txtEmail, txtEMAIL;
        ImageButton btnAppel, btnMessage;
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
