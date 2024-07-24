package fr.steve.fresh.crud.crud;

import java.util.function.Supplier;

public interface ICrud<T> {
    void create(String name);

    T read(int i);

    void update(Supplier<T> tSupplier);

    void delete(T t);

    void reload();
}
