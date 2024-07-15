package fr.steve.fresh.service.manager;

import java.util.Optional;

import fr.steve.fresh.MainActivity;
import fr.steve.fresh.entity.Course;
import fr.steve.fresh.repository.CourseRepository;
import fr.steve.fresh.service.factory.Entity;
import fr.steve.fresh.service.factory.Repository;

public class EntityManager {

    private final CourseRepository courseRepository;

    public EntityManager(MainActivity mainActivity) {
        this.courseRepository = new CourseRepository(mainActivity);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity, R extends Repository<T>> Optional<R> getRepository(Class<T> clazz) {
        if (clazz == Course.class) {
            return Optional.of((R) courseRepository);
        }
        return Optional.empty();
    }
}