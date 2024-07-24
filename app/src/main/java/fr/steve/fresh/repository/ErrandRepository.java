package fr.steve.fresh.repository;

import java.util.ArrayList;

import fr.steve.fresh.entity.Errand;
import fr.steve.fresh.service.factory.Repository;

public class ErrandRepository extends Repository<Errand> {
    public ErrandRepository() {
        super(new ArrayList<>());
    }

    @Override
    protected Class<Errand> getEntityClass() {
        return Errand.class;
    }
}
