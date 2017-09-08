package com.example.raoelson.fgu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raoelson.fgu.Model.Contact;
import com.example.raoelson.fgu.R;

import java.util.List;

/**
 * Created by Raoelson on 25/08/2017.
 */

public class AnnuaireAdapter extends RecyclerView.Adapter<AnnuaireAdapter.ViewHolder> {
    private Context mContext;
    private List<Contact> mContacts;
    // endregion

    // region Constructors
    public AnnuaireAdapter(Context context, List<Contact> contacts) {
        mContext = context;
        mContacts = contacts;
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
        if(position % 2 ==0 ){
            holder.itemView.setBackgroundResource(R.drawable.listviews);
            holder.btnAppel.setBackgroundResource(R.drawable.listviews);
            holder.btnMessage.setBackgroundResource(R.drawable.listviews);
        }
        else{
            holder.itemView.setBackgroundResource(R.drawable.listview);
            holder.btnAppel.setBackgroundResource(R.drawable.listview);
            holder.btnMessage.setBackgroundResource(R.drawable.listview);
        }

        final Contact contact = mContacts.get(position);
        if (contact != null) {
            setUpDisplayName(holder.txtNomPrenom, contact);
            setUpPro(holder.txtTaffe, contact);
            setUpDisplayAdresse(holder.txtAdresse, contact);
            setUpDisplayPhone(holder.txtPhone, contact);
            setUpDisplayEmail(holder.txtEmail, contact);
            holder.txtADRESSE.setText(Html.fromHtml("<b>Adresse :</b> "));
            holder.txtPHONE.setText(Html.fromHtml("<b>Tel :</b> "));
            holder.txtEMAIL.setText(Html.fromHtml("<b>Email :</b> "));
        }
        holder.btnAppel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:"+getItem(position).getC_tel()));
                    v.getContext().startActivity(callIntent);
                    //mContext.CoverridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

                catch (Exception ex){
                    Toast.makeText(mContext,"yourActivity is not founded",Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{""+getItem(position).getC_mail()});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT   , "");
                try {
                    v.getContext().startActivity(Intent.createChooser(i, "Envoie du mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    private void setUpDisplayName(TextView tv, Contact contact) {
        String displayName = String.valueOf(contact.getC_nom()+" "+contact.getC_prenom());
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
        String displayName = (contact.getC_adresse()+", "+contact.getC_postal()+
                ", "+contact.getC_pays());
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }

    private void setUpDisplayPhone(TextView tv, Contact contact) {
        String displayName = contact.getC_tel();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }
    private void setUpDisplayEmail(TextView tv, Contact contact) {
        String displayName = contact.getC_mail();
        if (!TextUtils.isEmpty(displayName)) {
            tv.setText(displayName);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomPrenom;
        TextView txtAdresse,txtADRESSE;
        TextView txtTaffe;
        TextView txtPhone,txtPHONE;
        TextView txtEmail,txtEMAIL;
        ImageButton btnAppel,btnMessage;

        public ViewHolder(View view) {
            super(view);
            txtNomPrenom = (TextView) view.findViewById(R.id.nomPrenom);
            txtAdresse = (TextView) view.findViewById(R.id.adresse);
            txtADRESSE = (TextView) view.findViewById(R.id.txtAdresse);
            txtTaffe = (TextView) view.findViewById(R.id.taffe);
            txtPhone = (TextView) view.findViewById(R.id.phone);
            txtPHONE = (TextView) view.findViewById(R.id.txtPhone);
            txtEmail = (TextView) view.findViewById(R.id.txtemail);
            txtEMAIL = (TextView) view.findViewById(R.id.txtEmail);
            btnAppel = (ImageButton) view.findViewById(R.id.btnAppel);
            btnMessage = (ImageButton) view.findViewById(R.id.btnMessage);
        }
    }

    public Contact getItem(int paramInt) {
        // TODO Auto-generated method stub
        return mContacts.get(paramInt);
    }

}
