package fr.steve.fresh.repository;

import java.util.ArrayList;

import fr.steve.fresh.entity.Course;
import fr.steve.fresh.service.factory.Repository;

public class CourseRepository extends Repository<Course> {
    public CourseRepository() {
        super(new ArrayList<>());
    }

    @Override
    protected Class<Course> getEntityClass() {
        return Course.class;
    }
}
