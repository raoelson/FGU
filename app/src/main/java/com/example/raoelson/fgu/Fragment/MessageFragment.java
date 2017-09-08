package com.example.raoelson.fgu.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.raoelson.fgu.R;
/**
 * Created by Raoelson on 08/09/2017.
 */

public class MessageFragment extends Fragment {
    EditText EditDestinateur,
            EditRecepteur,
            EditObject,
            EditMessage;
    Button btnEnvoye;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        EditDestinateur = (EditText) view.findViewById(R.id.EditDestinateur);
        EditRecepteur = (EditText) view.findViewById(R.id.EditRecepteur);
        EditObject = (EditText) view.findViewById(R.id.EditObject);
        EditMessage = (EditText) view.findViewById(R.id.EditMessage);
        btnEnvoye = (Button) view.findViewById(R.id.btnEnvoye);
        btnEnvoye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (!validate()) {
                    return;
                }*/


            }
        });
        return view;
    }


}
