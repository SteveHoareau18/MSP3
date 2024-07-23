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

    /**
     * Adds an entity to the list. If an entity with the same ID already exists in the list,
     * it is removed before the new entity is added.
     * <p>
     * This method ensures that the list does not contain duplicate entities based on their IDs.
     * After adding the entity to the list, the entity is persisted.
     * </p>
     *
     * @param entity the entity to be added to the list. If an entity with the same ID exists,
     *               it is replaced by the new entity.
     */
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

    /**
     * Persists the given entity by converting it to a JSON string and storing it
     * in the shared preferences using a unique key.
     * <p>
     * The key is constructed using the class name and the ID of the entity to ensure uniqueness.
     * </p>
     *
     * @param entity the entity to be persisted. It is converted to a JSON string and stored
     *               in the shared preferences.
     */
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
