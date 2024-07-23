package fr.steve.fresh.service.factory;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.steve.fresh.MainActivity;

public abstract class Repository<T extends Entity> {

    private final List<T> list;
    private final Gson gson;

    public Repository(List<T> list) {
        this.list = list;
        this.gson = new Gson();
    }

    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        Class<T> clazz = getEntityClass();
        Map<String, ?> allEntries = MainActivity.getActivityReference().get().getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(clazz.getName())) {
                String json = (String) entry.getValue();
                T entity = gson.fromJson(json, clazz);
                entities.add(entity);
            }
        }
        return entities;
    }

    public void add(T entity) {
        this.list.stream().filter(x -> x.getId() == entity.getId()).findFirst().ifPresent(this.list::remove);
        this.list.add(entity);
        persist(entity);
    }

    public void remove(T entity) {
        Optional<T> find = this.list.stream()
                .filter(x -> x.getId() == entity.getId())
                .findFirst();

        find.ifPresent(list::remove);
        persist(entity);
    }

    @Deprecated(forRemoval = true)
    public <U extends ListView> void setAdapter(U layout, ArrayAdapter<T> adapter) {
        layout.setAdapter(adapter);
    }

    private void persist(T entity) {
        String key = entity.getClass().getName() + "_" + entity.getId();
        String json = gson.toJson(entity);
        MainActivity.getActivityReference().get().edit().putString(key, json).apply();
    }

    @SuppressWarnings("unchecked")
    public Optional<T> findOneById(int id) {
        String key = Entity.class.getName() + "_" + id;
        String json = MainActivity.getActivityReference().get().getString(key, null);
        if (json != null) {
            return Optional.ofNullable(gson.fromJson(json, (Class<T>) Entity.class));
        }
        return Optional.empty();
    }

    protected abstract Class<T> getEntityClass();
}
