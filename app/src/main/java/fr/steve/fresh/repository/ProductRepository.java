package fr.steve.fresh.repository;

import java.util.ArrayList;

import fr.steve.fresh.entity.Product;
import fr.steve.fresh.service.factory.Repository;

public class ProductRepository extends Repository<Product> {
    public ProductRepository() {
        super(new ArrayList<>());
    }

    @Override
    protected Class<Product> getEntityClass() {
        return Product.class;
    }
}
