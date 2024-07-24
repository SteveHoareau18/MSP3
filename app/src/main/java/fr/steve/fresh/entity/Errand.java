package fr.steve.fresh.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.steve.fresh.MainActivity;
import fr.steve.fresh.service.factory.Entity;

public class Errand extends Entity implements Serializable {

    private final List<Product> productList;
    private String name;
    private Date createDate;
    private Date toDoDate, doDate;
    private Status status;

    public Errand() {
        super();
        this.createDate = new Date();
        this.status = Status.TO_DO;
        this.productList = new ArrayList<>();
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

    public Date getToDoDate() {
        return toDoDate;
    }

    public void setToDoDate(Date toDoDate) {
        this.toDoDate = toDoDate;
    }

    public Date getDoDate() {
        return doDate;
    }

    public void setDoDate(Date doDate) {
        this.doDate = doDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == Status.FINISH) setDoDate(new Date());
        this.status = status;
    }

    public List<Product> getProductList() {
        return productList;
    }

    @NonNull
    @Override
    public String toString() {
        return MainActivity.COURSE_SERIALIZER.serialize(this);
    }

    @Override
    public int compareTo(Entity entity) {
        if (entity instanceof Errand) {
            Errand errand = (Errand) entity;
            return this.createDate.compareTo(errand.getCreateDate());
        }
        throw new RuntimeException("Unbound entity in Course");
    }

    public enum Status {
        TO_DO,
        DOING,
        FINISH
    }
}
