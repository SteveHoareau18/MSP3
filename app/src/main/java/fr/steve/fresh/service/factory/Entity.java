package fr.steve.fresh.service.factory;

/**
 * Abstract class representing a generic entity with a unique ID and a comparison mechanism.
 * <p>
 * This class provides a unique ID for each entity, which is automatically assigned when an entity is created.
 * It also implements the {@link Comparable} interface to allow sorting or comparison between entities.
 * </p>
 */
public abstract class Entity implements Comparable<Entity> {

    private static int nextId = 0;  // Static counter for generating unique IDs
    private final int id;  // Unique identifier for each entity

    /**
     * Constructs a new entity with a unique ID.
     * <p>
     * The ID is assigned automatically using a static counter which ensures that each entity has a unique ID.
     * </p>
     */
    public Entity() {
        this.id = nextId++;
    }

    /**
     * Returns the unique identifier of the entity.
     *
     * @return the ID of the entity
     */
    public int getId() {
        return id;
    }

    /**
     * Compares this entity with another entity.
     * <p>
     * The comparison should be defined by subclasses implementing this method.
     * </p>
     *
     * @param entity the entity to be compared
     * @return a negative integer, zero, or a positive integer as this entity is less than,
     * equal to, or greater than the specified entity
     */
    public abstract int compareTo(Entity entity);
}
