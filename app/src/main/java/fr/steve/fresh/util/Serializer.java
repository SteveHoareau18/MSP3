package fr.steve.fresh.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * A utility class for serializing and deserializing objects to and from JSON.
 * <p>
 * This class uses Gson to convert objects of type {@code T} to JSON strings and vice versa. It is parameterized
 * with a type {@code T} that extends {@link Serializable} to ensure that only serializable objects are processed.
 * </p>
 *
 * @param <T> the type of objects that this serializer can handle, which must implement {@link Serializable}
 */
public class Serializer<T extends Serializable> {

    private final Gson gson;

    /**
     * Constructs a new {@code Serializer} instance with a configured Gson instance.
     * <p>
     * The Gson instance is configured to pretty-print the JSON, serialize null values, and disable HTML escaping.
     * </p>
     */
    public Serializer() {
        this.gson = createInstance();
    }

    /**
     * Creates a new instance of Gson with specific configurations.
     * <p>
     * Configurations include pretty-printing, serialization of null values, and disabling HTML escaping.
     * </p>
     *
     * @return a configured {@link Gson} instance
     */
    private Gson createInstance() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    /**
     * Serializes an object of type {@code T} to a JSON string.
     * <p>
     * This method converts the provided object to its JSON representation using the configured Gson instance.
     * </p>
     *
     * @param type the object to be serialized
     * @return a JSON string representing the serialized object
     */
    public String serialize(T type) {
        return this.gson.toJson(type);
    }

    /**
     * Deserializes a JSON string into an object of type {@code T}.
     * <p>
     * This method converts the provided JSON string into an object of the specified class using the configured
     * Gson instance.
     * </p>
     *
     * @param c    the class of the object to be deserialized
     * @param json the JSON string to be deserialized
     * @return an object of type {@code T} created from the JSON string
     */
    public T deserialize(Class<T> c, String json) {
        return this.gson.fromJson(json, c);
    }
}
