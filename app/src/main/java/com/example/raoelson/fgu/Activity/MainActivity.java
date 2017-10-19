package com.example.raoelson.fgu.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.example.raoelson.fgu.Fragment.AcceuilFragment;
import com.example.raoelson.fgu.Fragment.CompteFragment;
import com.example.raoelson.fgu.Fragment.FAIFragment;
import com.example.raoelson.fgu.Fragment.RechercheFragment;
import com.example.raoelson.fgu.R;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationItem;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;
import com.luseen.luseenbottomnavigation.BottomNavigation.OnBottomNavigationItemClickListener;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;

    //private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        int[] image = {R.drawable.ic_geo_black_24dp, R.drawable.ic_annuaire_black_24dp, R.drawable.faq_circle,
                R.drawable.ic_compte_black_24dp, R.drawable.deconxion_circle};
        int[] color = {ContextCompat.getColor(this, R.color.colorPrimary), ContextCompat.getColor(this, R.color.colorPrimary)};

        bottomNavigationView.isColoredBackground(false);
        bottomNavigationView.setItemActiveColorWithoutColoredBackground(ContextCompat.getColor(this, R.color.colorPrimary));


        BottomNavigationItem bottomNavigationItem = new BottomNavigationItem
                ("", color[1], image[1]);
        BottomNavigationItem bottomNavigationItem1 = new BottomNavigationItem
                ("", color[1], image[0]);
        BottomNavigationItem bottomNavigationItem2 = new BottomNavigationItem
                ("", color[1], image[3]);
        BottomNavigationItem bottomNavigationItem3 = new BottomNavigationItem
                ("", color[1], image[2]);
        BottomNavigationItem bottomNavigationItem4 = new BottomNavigationItem
                ("", color[1], image[4]);
       /* BottomNavigationItem bottomNavigationItem4 = new BottomNavigationItem
                ("Compte", color[1], image[4]);*/
        /*BottomNavigationItem bottomNavigationItem4 = new BottomNavigationItem
                ("Compte", color[1], 0);*/
        bottomNavigationView.addTab(bottomNavigationItem);
        bottomNavigationView.addTab(bottomNavigationItem1);
        bottomNavigationView.addTab(bottomNavigationItem2);
        bottomNavigationView.addTab(bottomNavigationItem3);
        bottomNavigationView.addTab(bottomNavigationItem4);

        //initialisation fragmemnt par défaut

        Bundle bundle = getIntent().getExtras();
        String activation = "1";
        if(bundle != null){
            activation = getIntent().getStringExtra("activation");
        }
        /*Fragment fragment = new AcceuilFragment().newInstance(activation);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();*/

        bottomNavigationView.setOnBottomNavigationItemClickListener(new OnBottomNavigationItemClickListener() {
            @Override
            public void onNavigationItemClick(int index) {
                Fragment fragment;
                switch (index) {

                    case 0:
                        fragment = new AcceuilFragment();
                        this.CallFragment(fragment);
                        break;
                    case 1:
                        fragment = new RechercheFragment();
                        this.CallFragment(fragment);
                        break;
                    case 2:
                        /*fragment = new MessageFragment();
                        this.CallFragment(fragment);*/
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
                        break;
                    case 3:
                        fragment = new FAIFragment();
                        this.CallFragment(fragment);
                        break;
                    case 4:
                        fragment = new CompteFragment();
                        this.CallFragment(fragment);
                        break;

                }

            }

            private void CallFragment(Fragment fragment) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_profil:
                Intent intent = new Intent(getApplicationContext(), ProfilActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
