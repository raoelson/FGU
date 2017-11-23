package com.example.autocomplete;

import android.support.annotation.NonNull;

import com.example.autocomplete.model.Place;


/**
 * A listener for place selected events that fire when an item is selected from the autocomplete
 * popup
 */
public interface OnPlaceSelectedListener {

    void onPlaceSelected(@NonNull Place place);
}
