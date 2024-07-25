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

import fr.steve.fresh.ErrandActivity;
import fr.steve.fresh.MainActivity;
import fr.steve.fresh.crud.crud.Crud;
import fr.steve.fresh.dialog.ProductDialog;
import fr.steve.fresh.entity.Errand;
import fr.steve.fresh.entity.Product;
import fr.steve.fresh.repository.ProductRepository;
import fr.steve.fresh.service.factory.Repository;

/**
 * The type ProductCrud.
 * Manages CRUD operations for products in a errand.
 */
public class ProductCrud extends Crud<Product, ProductDialog> {

    private Errand errand;

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
     * Sets the errand.
     *
     * @param errand the errand
     * @return the ProductCrud instance
     */
    public ProductCrud setErrand(Errand errand) {
        this.errand = errand;

        ErrandActivity.getActivityReference().get().getListProducts().setAdapter(getAdapter());

        ErrandActivity.getActivityReference().get().getListProducts().setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = getSortedProductInCourse().get(position);
            if (selectedProduct.getStatus() == Product.Status.IS_TAKE) return;

            getDialog().setErrand(errand).setActivity(ErrandActivity.getActivityReference().get())
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
        if (!canAddOrUpdate(name, quantity, unit)) return;

        unit = unit.isEmpty()?"":unit.trim();

        AtomicBoolean find = new AtomicBoolean(true);
        String finalUnit = unit;
        Product product = ((ProductRepository) getRepository()).findByNameInCourse(name, errand).orElseGet(() -> {
            find.set(false);
            Product new_product = new Product(errand.getId());
            new_product.setName(name.trim());
            new_product.setQuantity(quantity);
            new_product.setUnit(finalUnit);
            return new_product;
        });

        if (find.get()) {
            product.setQuantity(product.getQuantity() + quantity);
            product.setUnit(unit);
        }

        getRepository().add(product);
        Toast.makeText(getActivity(), "Le produit: " + product.getName() + " a été ajouté dans la course: " + errand.getName(), Toast.LENGTH_SHORT).show();
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

    /**
     * Updates a product in the repository.
     *
     * This method receives a product supplier, extracts the product from the supplier, and checks if the product can be added or updated based on the specified criteria (name, quantity, unit). If the product is valid, the following operations are performed:
     * <ul>
     *     <li>Trims any extra whitespace around the product's name and unit.</li>
     *     <li>Searches for an existing product with the same name in the repository.</li>
     *     <li>If an existing product is found with a different ID, it is deleted and its quantity is added to the current product's quantity.</li>
     *     <li>Adds or updates the product in the repository.</li>
     *     <li>Displays a confirmation message to the user.</li>
     *     <li>Reloads the data after the update.</li>
     * </ul>
     *
     * @param productSupplier A supplier of the product. The product provided by this supplier is the one that will be updated.
     */
    @Override
    public void update(Supplier<Product> productSupplier) {
        Product product = productSupplier.get();

        if (!canAddOrUpdate(product.getName(), product.getQuantity(), product.getUnit())) return;

        product.setUnit(product.getUnit().trim());
        product.setName(product.getName().trim());

        AtomicBoolean find = new AtomicBoolean(true);
        Product findProduct = ((ProductRepository) getRepository()).findByNameInCourse(product.getName(), errand).orElseGet(() -> {
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
     * @param unit     the unit of the product
     * @return true if the product can be added or updated, false otherwise
     */
    public boolean canAddOrUpdate(String name, int quantity, String unit) {
        name = name.trim();
        String regex = "^(?=(?:.*[A-Za-z]){2})[A-Za-z0-9 ]{1,30}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcherName = pattern.matcher(name);

        String regexUnit = "^(?=.*[A-Za-z])[A-Za-z0-9 ]{1,15}$";
        Pattern patternUnit = Pattern.compile(regexUnit);
        Matcher matcherUnit = patternUnit.matcher(unit);

        boolean matchesName = matcherName.matches();
        boolean matchesUnit = matcherUnit.matches();
        if (!matchesName) {
            Toast.makeText(getActivity(), "Le nom du produit ne doit pas être vide et doit avoir au moins 2 lettres et ne doit pas dépasser 30 caractères", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!unit.isEmpty() && !matchesUnit) {
            Toast.makeText(getActivity(), "L'unité du produit peut être au moins 1 lettre et ne doit pas dépasser 15 caractères", Toast.LENGTH_LONG).show();
            return false;
        }
        if (quantity < 1 || quantity > 1000) {
            Toast.makeText(getActivity(), "La quantité doit être positive et supérieure ou égale à 1 et inférieur à 1000", Toast.LENGTH_LONG).show();
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
        ErrandActivity.getActivityReference().get().getTitleProducts().setText(hasItems ? "Liste des produits de la course: " : "Il n'y a pas de produit pour le moment");
        ErrandActivity.getActivityReference().get().getListProducts().setVisibility(hasItems ? View.VISIBLE : View.GONE);
        setAdapter(new ProductCrud.ProductAdapter(getActivity(), getSortedProductInCourse()));
        ErrandActivity.getActivityReference().get().getListProducts().setAdapter(getAdapter());
    }

    /**
     * Gets sorted products in the errand.
     *
     * @return the sorted list of products in the errand
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
        sortedProducts = sortedProducts.stream().filter(x -> x.getCourseId() == errand.getId()).collect(Collectors.toList());
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

            productLine.setText(product.getQuantity() + " " + product.getUnit() + " " + product.getName());
            productLineDate.setText("Crée le: " + MainActivity.getSimpleDateFormat().format(product.getCreateDate()));

            if (product.getStatus() == Product.Status.IS_TAKE && product.getTakeDate() != null) {
                productLineDate.setText(productLineDate.getText() + " et pris le: " + MainActivity.getSimpleDateFormat().format(product.getTakeDate()));
            }

            return convertView;
        }
    }
}