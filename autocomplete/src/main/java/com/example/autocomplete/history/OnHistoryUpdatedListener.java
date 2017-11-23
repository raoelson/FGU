package com.example.autocomplete.history;

import android.support.annotation.NonNull;

import com.example.autocomplete.model.Place;

import java.util.List;



public interface OnHistoryUpdatedListener {
    public void onHistoryUpdated(@NonNull List<Place> updatedHistory);
}
