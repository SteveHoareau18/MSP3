package fr.steve.fresh.service.manager;

import java.util.Optional;

import fr.steve.fresh.entity.Errand;
import fr.steve.fresh.entity.Product;
import fr.steve.fresh.repository.ErrandRepository;
import fr.steve.fresh.repository.ProductRepository;
import fr.steve.fresh.service.factory.Entity;
import fr.steve.fresh.service.factory.Repository;

/**
 * Manager class for handling repositories for different entity types.
 * <p>
 * The EntityManager provides access to specific repositories based on the entity type requested.
 * It currently supports {@link Errand} and {@link Product} entities through their respective repositories.
 * </p>
 */
public class EntityManager {

    private final ErrandRepository errandRepository;
    private final ProductRepository productRepository;

    /**
     * Constructs an EntityManager with instances of CourseRepository and ProductRepository.
     */
    public EntityManager() {
        this.errandRepository = new ErrandRepository();
        this.productRepository = new ProductRepository();
    }

    /**
     * Retrieves the repository associated with the specified entity class.
     * <p>
     * This method returns an {@link Optional} containing the appropriate repository if the entity class
     * matches one of the supported types ({@link Errand} or {@link Product}). If the class does not match
     * any supported type, it returns an empty Optional.
     * </p>
     *
     * @param clazz the class of the entity whose repository is requested
     * @param <T>   the type of the entity
     * @param <R>   the type of the repository
     * @return an {@link Optional} containing the repository if found, otherwise an empty Optional
     * @throws ClassCastException if the class type does not match any known repository type
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity, R extends Repository<T>> Optional<R> getRepository(Class<T> clazz) {
        if (clazz == Errand.class) {
            return Optional.of((R) errandRepository);
        } else if (clazz == Product.class) {
            return Optional.of((R) productRepository);
        }
        return Optional.empty();
    }
}
