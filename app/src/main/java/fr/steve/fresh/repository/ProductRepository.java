package fr.steve.fresh.repository;

import java.util.ArrayList;
import java.util.Optional;

import fr.steve.fresh.entity.Errand;
import fr.steve.fresh.entity.Product;
import fr.steve.fresh.service.factory.Repository;

public class ProductRepository extends Repository<Product> {
    public ProductRepository() {
        super(new ArrayList<>());
    }

    public Optional<Product> findByNameInCourse(String name, Errand errand) {
        return findAll().stream().filter(x -> x.getName().equalsIgnoreCase(name) && x.getCourseId() == errand.getId()).findFirst();
    }

    @Override
    protected Class<Product> getEntityClass() {
        return Product.class;
    }
}
