package fr.steve.fresh.repository;

import java.util.ArrayList;

import fr.steve.fresh.MainActivity;
import fr.steve.fresh.entity.Course;
import fr.steve.fresh.service.factory.Repository;

public class CourseRepository extends Repository<Course> {
    public CourseRepository(MainActivity mainActivity) {
        super(new ArrayList<>(), mainActivity);
    }

    @Override
    protected Class<Course> getEntityClass() {
        return Course.class;
    }
}
