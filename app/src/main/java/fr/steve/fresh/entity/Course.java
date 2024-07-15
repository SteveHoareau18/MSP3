package fr.steve.fresh.entity;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import fr.steve.fresh.service.factory.Entity;

public class Course extends Entity {

    private String name;
    private Date createDate;
    private Optional<Date> toDoDate, doDate;

    public Course() {
        super();
        this.createDate = new Date();
        this.toDoDate = Optional.empty();
        this.doDate = Optional.empty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Optional<Date> getToDoDate() {
        return toDoDate;
    }

    public void setToDoDate(Date toDoDate) {
        this.toDoDate = Optional.of(toDoDate);
    }

    public Optional<Date> getDoDate() {
        return doDate;
    }

    public void setDoDate(Date doDate) {
        this.doDate = Optional.of(doDate);
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return "Course{" +
                "name='" + name + '\'' +
                ", createDate=" + (createDate != null ? sdf.format(createDate) : "null") +
                ", toDoDate=" + (toDoDate.isPresent() ? sdf.format(toDoDate) : "null") +
                ", doDate=" + (doDate.isPresent() ? sdf.format(doDate) : "null") +
                '}';
    }

    @Override
    public int compareTo(Entity entity) {
        if (entity instanceof Course) {
            Course course = (Course) entity;
            return this.createDate.compareTo(course.getCreateDate());
        }
        throw new RuntimeException("Unbound entity in Course");
    }
}
