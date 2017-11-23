package com.example.raoelson.fgu.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.autocomplete.PlacesApi;
import com.example.autocomplete.adapter.AbstractPlacesAutocompleteAdapter;
import com.example.autocomplete.history.AutocompleteHistoryManager;
import com.example.autocomplete.model.AutocompleteResultType;
import com.example.autocomplete.model.Place;
import com.example.raoelson.fgu.R;


public class PlacesAutocompleteAdapter extends AbstractPlacesAutocompleteAdapter {

    public PlacesAutocompleteAdapter(final Context context, final PlacesApi api, final AutocompleteResultType resultType, final AutocompleteHistoryManager history) {
        super(context, api, resultType, history);
    }

    @Override
    protected View newView(final ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.places_autocomplete_item, parent, false);
    }

    @Override
    protected void bindView(final View view, final Place item) {
        TextView textView = (TextView) view.findViewById(R.id.address_);
        textView.setText(item.description);
    }
}
