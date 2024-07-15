package fr.steve.fresh.service.factory;

public abstract class Entity implements Comparable<Entity> {

    private static int nextId = 0;
    private final int id;

    public Entity() {
        this.id = nextId++;
    }

    public int getId() {
        return id;
    }

    public abstract int compareTo(Entity entity);
}