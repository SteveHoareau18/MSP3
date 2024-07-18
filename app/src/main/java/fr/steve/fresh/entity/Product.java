package fr.steve.fresh.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

import fr.steve.fresh.MainActivity;
import fr.steve.fresh.service.factory.Entity;

public class Product extends Entity implements Serializable {

    private final int course_id;
    private final Date createDate;
    private String name;
    private int quantity;
    private String unit;
    private Date takeDate;
    private Status status;

    public Product(int course_id) {
        super();
        this.course_id = course_id;
        this.createDate = new Date();
        status = Status.TO_TAKE;
    }

    public int getCourseId() {
        return course_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int compareTo(Entity entity) {
        if (entity instanceof Product) {
            Product product = (Product) entity;
            return this.createDate.compareTo(product.getCreateDate());
        }
        throw new RuntimeException("Unbound entity in Product");
    }

    public void take() {
        this.takeDate = new Date();
        status = Status.IS_TAKE;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getTakeDate() {
        return takeDate;
    }

    public Status getStatus() {
        return status;
    }

    @NonNull
    @Override
    public String toString() {
        return MainActivity.PRODUCT_SERIALIZER.serialize(this);
    }

    public enum Status {
        TO_TAKE,
        IS_TAKE,
    }
}
