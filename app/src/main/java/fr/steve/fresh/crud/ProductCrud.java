package fr.steve.fresh.crud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fr.steve.fresh.CourseActivity;
import fr.steve.fresh.MainActivity;
import fr.steve.fresh.crud.crud.Crud;
import fr.steve.fresh.dialog.ProductDialog;
import fr.steve.fresh.entity.Course;
import fr.steve.fresh.entity.Product;
import fr.steve.fresh.service.factory.Repository;

public class ProductCrud extends Crud<Product, ProductDialog> {

    private Course course;

    public ProductCrud(Activity activity, Repository<Product> productRepository) {
        super(productRepository, activity, new ProductDialog(activity), new ProductCrud.ProductAdapter(activity, productRepository.findAll()));
    }

    public ProductCrud setCourse(Course course) {
        this.course = course;

        CourseActivity.getActivityReference().get().getListProducts().setAdapter(getAdapter());

        CourseActivity.getActivityReference().get().getListProducts().setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = getSortedProductInCourse().get(position);
            if (selectedProduct.getStatus() == Product.Status.IS_TAKE) return;

            getDialog().setCourse(course).setActivity(CourseActivity.getActivityReference().get())
                    .setProduct(selectedProduct).open(ProductDialog.Page.GET);


            reload();
        });

        reload();
        return this;
    }

    public void create(String name, int quantity, String unit) {
        Product product = new Product(course.getId());
        product.setName(name.isEmpty() ? "Course" : name);
        product.setQuantity(quantity);
        product.setUnit(unit.isEmpty() ? "" : unit);
        getRepository().add(product);
        Toast.makeText(getActivity(), "Le produit: " + product.getName() + " a été ajouté dans la course: " + course.getName(), Toast.LENGTH_SHORT).show();
        reload();
    }

    @Override
    public void create(String name) {
        Product product = new Product(course.getId());
        product.setName(name.isEmpty() ? "Course" : name);
        product.setQuantity(0);
        product.setUnit("");
        Toast.makeText(getActivity(), "Le produit: " + product.getName() + " a été ajouté dans la course: " + course.getName(), Toast.LENGTH_SHORT).show();
        getRepository().add(product);
        reload();
    }

    @Override
    public Product read(int id) {
        return getRepository().findOneById(id).orElseGet(() -> {
            Toast.makeText(getActivity().getApplicationContext(), "Product not found", Toast.LENGTH_LONG).show();
            return null;
        });
    }

    @Override
    public void update(Supplier<Product> productSupplier) {
        Product product = productSupplier.get();
        if (product.getName().isEmpty()) product.setName("Produit");
        getRepository().add(product);
        Toast.makeText(getActivity(), "Le produit " + product.getName() + " a été mis à jour", Toast.LENGTH_LONG).show();
        reload();
    }

    @Override
    public void delete() {

    }

    @Override
    public void reload() {
        boolean hasItems = !getSortedProductInCourse().isEmpty();
        CourseActivity.getActivityReference().get().getTitleProducts().setText(hasItems ? "Liste des produits de la course: " : "Il n'y a pas de produit pour le moment");
        CourseActivity.getActivityReference().get().getListProducts().setVisibility(hasItems ? View.VISIBLE : View.GONE);
        setAdapter(new ProductCrud.ProductAdapter(getActivity(), getSortedProductInCourse()));
        CourseActivity.getActivityReference().get().getListProducts().setAdapter(getAdapter());
    }

    public List<Product> getSortedProductInCourse() {
        List<Product> sortedProducts = getRepository().findAll();
        sortedProducts.sort((o1, o2) -> {
            if (o1.getTakeDate() == null && o2.getTakeDate() == null) {
                return o1.getCreateDate().compareTo(o2.getCreateDate());
            } else {
                if (o1.getTakeDate() != null) {
                    return -1;
                }
                return 1;
            }
        });
        sortedProducts = sortedProducts.stream().filter(x -> x.getCourseId() == course.getId()).collect(Collectors.toList());
        return sortedProducts;
    }

    public static class ProductAdapter extends ArrayAdapter<Product> {

        public ProductAdapter(@NonNull Activity activity, @NonNull List<Product> productList) {
            super(activity.getApplicationContext(), 0, productList);
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Product product = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            TextView productLine = convertView.findViewById(android.R.id.text1);
            TextView productLineDate = convertView.findViewById(android.R.id.text2);

            assert product != null;

            productLine.setText(product.getName() + " " + product.getQuantity() + " " + product.getUnit());
            productLineDate.setText("Crée le: " + MainActivity.getSimpleDateFormat().format(product.getCreateDate()));

            if (product.getStatus() == Product.Status.IS_TAKE && product.getTakeDate() != null) {
                productLineDate.setText(productLineDate.getText() + " et pris le: " + MainActivity.getSimpleDateFormat().format(product.getTakeDate()));
            }

            return convertView;
        }
    }
}
