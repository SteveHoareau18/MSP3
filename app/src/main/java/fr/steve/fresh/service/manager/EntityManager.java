package fr.steve.fresh.service.manager;

import java.util.Optional;

import fr.steve.fresh.entity.Course;
import fr.steve.fresh.entity.Product;
import fr.steve.fresh.repository.CourseRepository;
import fr.steve.fresh.repository.ProductRepository;
import fr.steve.fresh.service.factory.Entity;
import fr.steve.fresh.service.factory.Repository;

public class EntityManager {

    private final CourseRepository courseRepository;
    private final ProductRepository productRepository;

    public EntityManager() {
        this.courseRepository = new CourseRepository();
        this.productRepository = new ProductRepository();
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity, R extends Repository<T>> Optional<R> getRepository(Class<T> clazz) {
        if (clazz == Course.class) {
            return Optional.of((R) courseRepository);
        } else if (clazz == Product.class) {
            return Optional.of((R) productRepository);
        }
        return Optional.empty();
    }
}