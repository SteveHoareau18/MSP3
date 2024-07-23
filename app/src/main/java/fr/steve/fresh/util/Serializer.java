package fr.steve.fresh.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

public class Serializer<T extends Serializable> {

    private final Gson gson;

    public Serializer() {
        this.gson = createInstance();
    }

    private Gson createInstance() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    public String serialize(T type) {
        return this.gson.toJson(type);
    }

    public T deserialize(Class<T> c, String json) {
        return this.gson.fromJson(json, c);
    }
}