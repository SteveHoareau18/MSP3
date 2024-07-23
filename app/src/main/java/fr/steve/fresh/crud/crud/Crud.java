package fr.steve.fresh.crud.crud;

import android.app.Activity;
import android.widget.ArrayAdapter;

import fr.steve.fresh.dialog.dialog.Dialog;
import fr.steve.fresh.service.factory.Entity;
import fr.steve.fresh.service.factory.Repository;

public abstract class Crud<T extends Entity, D extends Dialog<?>> implements ICrud<T> {

    private final Repository<T> repository;
    private final D dialog;
    private Activity activity;
    private ArrayAdapter<T> adapter;

    protected Crud(Repository<T> repository, Activity activity, D dialog, ArrayAdapter<T> adapter) {
        this.repository = repository;
        this.activity = activity;
        this.dialog = dialog;
        this.adapter = adapter;
    }

    public Repository<T> getRepository() {
        return repository;
    }

    public Activity getActivity() {
        return activity;
    }

    public Crud<T, D> setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public D getDialog() {
        return dialog;
    }

    public ArrayAdapter<T> getAdapter() {
        return adapter;
    }

    public void setAdapter(ArrayAdapter<T> adapter) {
        this.adapter = adapter;
    }
}
