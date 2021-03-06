package com.example.raoelson.fgu.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.raoelson.fgu.Activity.AnnuaireActivity;
import com.example.raoelson.fgu.Activity.MapsActivity;
import com.example.raoelson.fgu.Adapter.RecyclerAdapter;
import com.example.raoelson.fgu.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Raoelson on 25/08/2017.
 */

public class RechercheFragment extends Fragment {
    String action = "";
    ImageView  btnRecherche;
    boolean stringShow = false;
    LinearLayout linearRecherche;
    RadioGroup radioGroup;
    EditText EditQuery;
    RadioButton radioButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recherche, container, false);
        setUpRecyclerView(v);
        return v;
    }

    private void setUpRecyclerView(View v) {
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        //btnShow = (ImageView) v.findViewById(R.id.btnShow);
        linearRecherche = (LinearLayout) v.findViewById(R.id.linearRecherche);
        linearRecherche.setVisibility(View.VISIBLE);
        EditQuery = (EditText) v.findViewById(R.id.EditQuery_rec);
        radioGroup = (RadioGroup) v.findViewById(R.id.groupRadio);
        btnRecherche = (ImageView) v.findViewById(R.id.btnRecherche_rec);
        RecyclerAdapter adapter = new RecyclerAdapter(MenuStrings());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        adapter.setRecyclerClickListener(new RecyclerAdapter.RecyclerClickListener() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    action = "avocat";
                } else if (position == 1) {
                    action = "banque";
                } else if (position == 2) {
                    action = "expert comptable";
                } else if (position == 3) {
                    action = "notaire";
                } else if (position == 4) {
                    action = "communication";
                } else {
                    action = "assurance";
                }
                recherche(action);
                /*AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setMessage(getContext().getResources().getString(R.string.message));
                dialog.setPositiveButton(getContext().getResources().getString(R.string.carte), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent intent = new Intent(getContext(), MapsActivity.class);
                        intent.putExtra("search", action);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });
                dialog.setNegativeButton(getContext().getString(R.string.liste), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent intent = new Intent(getContext(), AnnuaireActivity.class);
                        intent.putExtra("search", action);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                    }
                });
                dialog.show();*/
                /*intent.putExtra("search",action);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);*/

            }
        });

        /*btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stringShow) {
                    btnShow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    stringShow = false;
                    linearRecherche.setVisibility(View.GONE);
                } else {
                    btnShow.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    stringShow = true;
                    linearRecherche.setVisibility(View.VISIBLE);
                }
            }
        });*/
        btnRecherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recherche(EditQuery.getText().toString());
            }
        });

        EditQuery.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    EditQuery.setSingleLine(true);
                    recherche(EditQuery.getText().toString());
                    return false;
                }
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        EditQuery.setSingleLine(false);
    }

    private List<String> MenuStrings() {
        List<String> menuList = new ArrayList<>();
        menuList.add("Avocat");
        menuList.add("Banque");
        menuList.add("Expert \n Comptable");
        menuList.add("Notaire");
        menuList.add("Communication");
        menuList.add("Assurance");
        return menuList;
    }

    public void recherche(String id) {
        int position = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) radioGroup.findViewById(position);
        if (radioButton.getText().toString().equalsIgnoreCase("Listes")) {
            Intent intent = new Intent(getContext(), AnnuaireActivity.class);
            intent.putExtra("search", id);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else {
            Intent intent = new Intent(getContext(), MapsActivity.class);
            intent.putExtra("search", id);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

}
