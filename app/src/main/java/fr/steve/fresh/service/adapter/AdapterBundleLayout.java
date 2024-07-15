package fr.steve.fresh.service.adapter;

import android.app.Activity;
import android.widget.ArrayAdapter;

import fr.steve.fresh.service.factory.Entity;
import fr.steve.fresh.service.factory.Repository;

public class AdapterBundleLayout<R extends Repository<T>, T extends Entity> {

    private final Activity activity;
    private int layout;
    private R repository;

    public AdapterBundleLayout(Activity activity) {
        this.activity = activity;
    }

    public AdapterBundleLayout<R, T> setLayout(int layout) {
        this.layout = layout;
        return this;
    }

    public AdapterBundleLayout<R, T> setItems(R repository) {
        this.repository = repository;
        return this;
    }

    public ArrayAdapter<T> build() {
        return new ArrayAdapter<>(this.activity, this.layout, this.repository.findAll());
    }
}
