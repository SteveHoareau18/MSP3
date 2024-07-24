package fr.steve.fresh.repository;

import java.util.ArrayList;
import java.util.Optional;

import fr.steve.fresh.entity.Course;
import fr.steve.fresh.entity.Product;
import fr.steve.fresh.service.factory.Repository;

public class ProductRepository extends Repository<Product> {
    public ProductRepository() {
        super(new ArrayList<>());
    }

    public Optional<Product> findByNameInCourse(String name, Course course) {
        return findAll().stream().filter(x -> x.getName().equalsIgnoreCase(name) && x.getCourseId() == course.getId()).findFirst();
    }

    @Override
    protected Class<Product> getEntityClass() {
        return Product.class;
    }
}
