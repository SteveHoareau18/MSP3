package fr.steve.fresh.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fr.steve.fresh.MainActivity;
import fr.steve.fresh.dialog.dialog.Dialog;
import fr.steve.fresh.dialog.page.IPage;
import fr.steve.fresh.entity.Errand;
import fr.steve.fresh.entity.Product;

/**
 * The type ProductDialog.
 * Manages the dialog for creating, viewing, and editing products.
 */
public class ProductDialog extends Dialog<ProductDialog.Page> {

    private Errand errand;
    private Product product;

    /**
     * Instantiates a new ProductDialog.
     *
     * @param activity the activity
     */
    public ProductDialog(Activity activity) {
        super(activity);
    }

    /**
     * Sets the errand.
     *
     * @param errand the errand
     * @return the ProductDialog instance
     */
    public ProductDialog setErrand(Errand errand) {
        this.errand = errand;
        return this;
    }

    /**
     * Sets the product.
     *
     * @param product the product
     * @return the ProductDialog instance
     */
    public ProductDialog setProduct(Product product) {
        this.product = product;
        return this;
    }

    /**
     * Sets the activity.
     *
     * @param activity the activity
     * @return the ProductDialog instance
     */
    public ProductDialog setActivity(Activity activity) {
        super.setActivity(activity);
        return this;
    }

    /**
     * Opens the dialog for the specified page.
     *
     * @param page the page to open
     *             - MAIN: to create a new product
     *             - GET: to view a product's details
     *             - EDIT: to edit an existing product
     *             - DELETE: (not implemented) to delete a product
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void open(Page page) {
        EditText input_name = new EditText(getActivity());
        EditText input_quantity = new EditText(getActivity());
        input_quantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText input_unit = new EditText(getActivity());

        switch (page) {
            case MAIN:
                buildAlertDialog("Course: " + errand.getName(), () ->
                                new LinearLayoutBuilder(getActivity()).add(() -> new LinearLayoutBuilder(getActivity()).add(() -> {
                                            TextView textView = new TextView(getActivity());
                                            textView.setText("Nom du produit: ");
                                            return textView;
                                        }).add(() -> input_name)
                                        .add(() -> {
                                            TextView textView = new TextView(getActivity());
                                            textView.setText("Quantité: ");
                                            return textView;
                                        })
                                        .add(() -> input_quantity)
                                        .add(() -> {
                                            TextView textView = new TextView(getActivity());
                                            textView.setText("Unité: ");
                                            return textView;
                                        })
                                        .add(() -> input_unit).build()).build(),
                        "Créer", ((dialog, which) -> {
                            boolean emptyInputName = input_name.getText().toString().isEmpty();
                            boolean emptyQuantity = input_quantity.getText().toString().isEmpty();

                            if (emptyInputName || emptyQuantity) {
                                StringBuilder error;
                                if (emptyInputName && emptyQuantity) {
                                    error = new StringBuilder("Les champs Nom du produit et Quantité sont requis");
                                } else {
                                    error = new StringBuilder("Le champ ");
                                    if (emptyInputName) {
                                        error.append("Nom du produit est requis");
                                    } else {
                                        error.append("Quantité est requis");
                                    }
                                }
                                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                            } else {
                                MainActivity.getActivityReference().get().getProductCrud(errand).create(input_name.getText().toString(), Integer.parseInt(input_quantity.getText().toString()),
                                        input_unit.getText().toString());
                            }
                        }),
                        "Annuler", (dialog, which) -> dialog.cancel());
                break;

            case GET:
                buildAlertDialog("Produit: " + product.getQuantity() + " " + product.getName() + (product.getUnit().isEmpty() ? "" : product.getUnit()),
                        () -> new LinearLayoutBuilder(getActivity()).add(() -> {
                            Button button = new Button(getActivity());
                            button.setText("Modifier");

                            button.setOnClickListener(v -> open(Page.EDIT));
                            return button;
                        }).build(),
                        "Marquer comme pris", (dialog, which) -> {
                            MainActivity.getActivityReference().get().getProductCrud(errand).update(
                                    () -> {
                                        product.take();
                                        return product;
                                    }
                            );
                            Toast.makeText(getActivity(), "Vous avez pris votre produit", Toast.LENGTH_LONG).show();
                        },
                        "Retour", (dialog, which) -> dialog.cancel());
                break;

            case EDIT:
                buildAlertDialog("Produit: " + product.getQuantity() + " " + product.getName() + (product.getUnit() == null || product.getUnit().isEmpty() ? "" : product.getUnit()),
                        () -> new LinearLayoutBuilder(getActivity()).add(() -> {
                                    TextView textView = new TextView(getActivity());
                                    textView.setText("Nom: ");
                                    return textView;
                                }).add(() -> {
                                    input_name.setText(product.getName());
                                    return input_name;
                                })
                                .add(() -> {
                                    TextView textView = new TextView(getActivity());
                                    textView.setText("Quantité: ");
                                    return textView;
                                })
                                .add(() -> {
                                    input_quantity.setText(product.getQuantity() + "");
                                    return input_quantity;
                                })
                                .add(() -> {
                                    TextView textView = new TextView(getActivity());
                                    textView.setText("Unité: ");
                                    return textView;
                                })
                                .add(() -> {
                                    String value = "";
                                    if (product.getUnit() != null)
                                        input_unit.setText(product.getUnit());

                                    input_unit.setText(value);
                                    return input_unit;
                                }).build(),
                        "Enregistrer", (dialog, which) -> MainActivity.getActivityReference().get().getProductCrud(errand).update(() -> {
                            if (input_name.getText().toString().isEmpty() || input_quantity.getText().toString().isEmpty()) {
                                Toast.makeText(getActivity(), "Les champs nom et quantité sont requis.", Toast.LENGTH_LONG).show();
                                dialog.cancel();
                                return null;
                            }
                            product.setName(input_name.getText().toString());
                            product.setQuantity(Integer.parseInt(input_quantity.getText().toString()));
                            product.setUnit(input_unit.getText().toString());
                            return product;
                        }),
                        "Annuler", (dialog, which) -> dialog.cancel());
                break;

            case DELETE:
                break;
        }
    }

    /**
     * The enum Page.
     * Represents different pages or states of the ProductDialog.
     */
    public enum Page implements IPage {
        MAIN,
        GET,
        EDIT,
        DELETE;
    }
}
