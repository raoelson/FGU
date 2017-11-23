package com.example.raoelson.fgu.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.raoelson.fgu.Fragment.AcceuilFragment;
import com.example.raoelson.fgu.Fragment.CompteFragment;
import com.example.raoelson.fgu.Fragment.FAIFragment;
import com.example.raoelson.fgu.Fragment.FavorisFragment;
import com.example.raoelson.fgu.Fragment.RechercheFragment;
import com.example.raoelson.fgu.R;

/**
 * Created by Raoelson on 09/09/2017.
 */

public class PrincipalActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    //private boolean mSearchCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.action_accueil:
                                //switchFragment(0, TAG_FRAGMENT_CALLS);
                                //fragment = new AcceuilFragment();
                                fragment = new AcceuilFragment();
                                this.CallFragment(fragment);
                                return true;
                            case R.id.action_favorie:
                                fragment = new FavorisFragment();
                                this.CallFragment(fragment);
                                return true;
                            case R.id.action_message:
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@franceguichetunique.com"});
                                i.putExtra(Intent.EXTRA_SUBJECT, "");
                                i.putExtra(Intent.EXTRA_TEXT, "");
                                try {
                                    startActivity(Intent.createChooser(i, "Envoie du mail..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            case R.id.action_fai:
                                fragment = new FAIFragment();
                                this.CallFragment(fragment);
                                return true;
                            case R.id.action_compte:
                                fragment = new CompteFragment();
                                this.CallFragment(fragment);
                                return true;
                        }
                        return false;
                    }

                    private void CallFragment(Fragment fragment) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_fragmentholder, fragment)
                                .addToBackStack(null)
                                .commit();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });

        Fragment fragment = new AcceuilFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_fragmentholder, fragment)
                .commit();


    }

    /*private SearchView.OnQueryTextListener onQuerySearchView = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            *//*if (mSearchCheck) {
                AffichageRecherche(s);
            }*//*
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            *//*if (query.length() == 0) {
                Fragment fragment = new CategorieFragment().newInstance(getApplicationContext());
                getSupportFragmentManager().beginTransaction()
               *//**//* .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter,
                        R.anim.pop_exit)*//**//*
                        .replace(R.id.content_frame, fragment)
                        //.addToBackStack(null)
                        .commit();
            }*//*
            return false;
        }
    };
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        /*final MenuItem menuItem_ = menu.findItem(R.id.menu_search);
        menuItem_.setVisible(true);*/

        /*SearchView searchView = (SearchView) menuItem_.getActionView();
        searchView.setQueryHint("Tapez le nom de la  ville");
        ((EditText) searchView.findViewById(R.id.search_src_text))
                .setHintTextColor(getResources().getColor(R.color.colorAccent));
        searchView.setOnQueryTextListener(onQuerySearchView);*/

        /*mSearchCheck = false;*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.menu_search:
                mSearchCheck = true;
                break;*/
            case R.id.action_deconnexion:
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove("motdepasse");
                editor.apply();
                startActivityForResult(new
                        Intent(getApplicationContext(), LoginActivity.class), 100);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}
