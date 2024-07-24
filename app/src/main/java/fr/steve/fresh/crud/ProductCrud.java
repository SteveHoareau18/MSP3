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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fr.steve.fresh.CourseActivity;
import fr.steve.fresh.MainActivity;
import fr.steve.fresh.crud.crud.Crud;
import fr.steve.fresh.dialog.ProductDialog;
import fr.steve.fresh.entity.Course;
import fr.steve.fresh.entity.Product;
import fr.steve.fresh.repository.ProductRepository;
import fr.steve.fresh.service.factory.Repository;

/**
 * The type ProductCrud.
 * Manages CRUD operations for products in a course.
 */
public class ProductCrud extends Crud<Product, ProductDialog> {

    private Course course;

    /**
     * Instantiates a new ProductCrud.
     *
     * @param activity          the activity
     * @param productRepository the product repository
     */
    public ProductCrud(Activity activity, Repository<Product> productRepository) {
        super(productRepository, activity, new ProductDialog(activity), new ProductCrud.ProductAdapter(activity, productRepository.findAll()));
    }

    /**
     * Sets the course.
     *
     * @param course the course
     * @return the ProductCrud instance
     */
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

    /**
     * Creates a new product.
     *
     * @param name     the name of the product
     * @param quantity the quantity of the product
     * @param unit     the unit of the product
     */
    public void create(String name, int quantity, String unit) {
        if (!canAddOrUpdate(name, quantity)) return;

        AtomicBoolean find = new AtomicBoolean(true);
        Product product = ((ProductRepository) getRepository()).findByNameInCourse(name, course).orElseGet(() -> {
            find.set(false);
            Product new_product = new Product(course.getId());
            new_product.setName(name.trim());
            new_product.setQuantity(quantity);
            new_product.setUnit(unit.isEmpty() ? "" : unit);
            return new_product;
        });

        if (find.get()) {
            product.setQuantity(product.getQuantity() + quantity);
        }

        getRepository().add(product);
        Toast.makeText(getActivity(), "Le produit: " + product.getName() + " a été ajouté dans la course: " + course.getName(), Toast.LENGTH_SHORT).show();
        reload();
    }

    @Override
    public void create(String name) {
        create(name, 1, "");
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

        if (!canAddOrUpdate(product.getName(), product.getQuantity())) return;

        AtomicBoolean find = new AtomicBoolean(true);
        Product findProduct = ((ProductRepository) getRepository()).findByNameInCourse(product.getName(), course).orElseGet(() -> {
            find.set(false);
            return product;
        });

        int new_quantity = 0;
        if (find.get() && findProduct.getId() != product.getId()) {
            new_quantity += findProduct.getQuantity();
            delete(findProduct);
        }
        product.setQuantity(product.getQuantity() + new_quantity);

        getRepository().add(product);
        Toast.makeText(getActivity(), "Le produit " + product.getName() + " a été mis à jour", Toast.LENGTH_LONG).show();
        reload();
    }

    /**
     * Checks if a product can be added or updated.
     *
     * @param name     the name of the product
     * @param quantity the quantity of the product
     * @return true if the product can be added or updated, false otherwise
     */
    private boolean canAddOrUpdate(String name, int quantity) {
        String regex = "^(?=(?:.*[A-Za-z]){2})[A-Za-z0-9]{1,30}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        boolean matches = matcher.matches();
        if (!matches) {
            Toast.makeText(getActivity(), "Le nom du produit ne doit être vide et doit avoir au moins 2 lettres et ne doit pas dépasser 30 caractères", Toast.LENGTH_LONG).show();
            return false;
        }
        if (quantity < 1) {
            Toast.makeText(getActivity(), "La quantité doit être positive et supérieure ou égale à 1", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void delete(Product product) {
        getRepository().remove(product);
        reload();
    }

    @Override
    public void reload() {
        boolean hasItems = !getSortedProductInCourse().isEmpty();
        CourseActivity.getActivityReference().get().getTitleProducts().setText(hasItems ? "Liste des produits de la course: " : "Il n'y a pas de produit pour le moment");
        CourseActivity.getActivityReference().get().getListProducts().setVisibility(hasItems ? View.VISIBLE : View.GONE);
        setAdapter(new ProductCrud.ProductAdapter(getActivity(), getSortedProductInCourse()));
        CourseActivity.getActivityReference().get().getListProducts().setAdapter(getAdapter());
    }

    /**
     * Gets sorted products in the course.
     *
     * @return the sorted list of products in the course
     */
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

    /**
     * The type ProductAdapter.
     * Adapter class for displaying products in a list view.
     */
    public static class ProductAdapter extends ArrayAdapter<Product> {

        /**
         * Instantiates a new ProductAdapter.
         *
         * @param activity    the activity
         * @param productList the list of products
         */
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