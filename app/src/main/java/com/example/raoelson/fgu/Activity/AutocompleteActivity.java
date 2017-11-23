package com.example.raoelson.fgu.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;


import com.example.autocomplete.DetailsCallback;
import com.example.autocomplete.OnPlaceSelectedListener;
import com.example.autocomplete.PlacesAutocompleteTextView;
import com.example.autocomplete.model.AddressComponent;
import com.example.autocomplete.model.AddressComponentType;
import com.example.autocomplete.model.Place;
import com.example.autocomplete.model.PlaceDetails;
import com.example.raoelson.fgu.R;




public class AutocompleteActivity extends AppCompatActivity {

    PlacesAutocompleteTextView mAutocomplete;
    TextView mStreet;
    TextView mCity;
    TextView mState;
    TextView mZip;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_places_autocomplete);
        mAutocomplete = (PlacesAutocompleteTextView) findViewById(R.id.autocomplete);
        mState = (TextView) findViewById(R.id.state);
        mCity = (TextView) findViewById(R.id.city);
        mZip = (TextView) findViewById(R.id.zip);
        mStreet  = (TextView) findViewById(R.id.street);

        mAutocomplete.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                mAutocomplete.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(final PlaceDetails details) {
                        Log.d("test", "details " + details);
                        mStreet.setText(details.name);
                        /*for (AddressComponent component : details.address_components) {
                            for (AddressComponentType type : component.types) {
                                switch (type) {
                                    case STREET_NUMBER:
                                        break;
                                    case ROUTE:
                                        break;
                                    case NEIGHBORHOOD:
                                        break;
                                    case SUBLOCALITY_LEVEL_1:
                                        break;
                                    case SUBLOCALITY:
                                        break;
                                    case LOCALITY:
                                        mCity.setText(component.long_name);
                                        break;
                                    case ADMINISTRATIVE_AREA_LEVEL_1:
                                        mState.setText(component.short_name);
                                        break;
                                    case ADMINISTRATIVE_AREA_LEVEL_2:
                                        break;
                                    case COUNTRY:
                                        break;
                                    case POSTAL_CODE:
                                        mZip.setText(component.long_name);
                                        break;
                                    case POLITICAL:
                                        break;
                                }
                            }
                        }*/
                    }

                    @Override
                    public void onFailure(final Throwable failure) {
                        Log.d("test", "failure " + failure);
                    }
                });
            }
        });
    }
}
