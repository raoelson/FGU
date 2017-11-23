package com.example.autocomplete.util;

import java.util.Collection;

public interface ArrayAdapterDelegate<T> {
    void setNotifyOnChange(boolean notifyOnChange);

    void clear();

    void addAll(Collection<T> values);

    void notifyDataSetChanged();
}
