package com.example.raoelson.fgu.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raoelson.fgu.R;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;


/**
 * Created by Raoelson on 25/08/2017.
 */

public class FAIFragment extends Fragment implements View.OnClickListener{


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fai, container, false);
        TextView textView = (TextView) v.findViewById(R.id.one);
        textView.setText(Html.fromHtml("<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;France Guichet Unique</b> est un organisme spécialisé dans l'accompagnement et le conseil des entrepreneurs.\n" +
                "        Grâce à 20 d'expérience et à un réseau national de spécialistes nous pouvons :"));
        Button button = (Button) v.findViewById(R.id.btncontact);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@franceguichetunique.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(Intent.createChooser(i, "Envoie du mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.expandButton:
                mExpandLayout.toggle();
                break;
            case R.id.moveChildButton:
                mExpandLayout.moveChild(0);
                break;
            case R.id.moveChildButton2:
                mExpandLayout.moveChild(1);
                break;
            case R.id.moveTopButton:
                mExpandLayout.move(0);
                break;
            case R.id.setCloseHeightButton:
                mExpandLayout.setClosePosition(mExpandLayout.getCurrentPosition());
                break;*/
        }
    }
}
