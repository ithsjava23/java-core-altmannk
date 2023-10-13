package org.example.warehouse;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Warehouse {
    //Private field
    private final String name;
    private final List<ProductRecord> products = new ArrayList<>();
    private final static List<ProductRecord> changedProducts = new ArrayList<>();

    //Private constructor
    private Warehouse(String name) {
        this.name = name;
    }

    //Methods
    // getter for warehouse name
    public String getName() {
        return name;
    }

    //create an instance of warehouse with an empty name
    public static Warehouse getInstance() {
        return new Warehouse("");
    }

    //create an instance of warehouse with a name
    public static Warehouse getInstance(String name) {
        return new Warehouse(name);
    }

    //check if warehouse is empty
    public boolean isEmpty() {
        return products.isEmpty();
    }

    //get a list of products (copy of the internal list)
    public List<ProductRecord> getProducts() {
        return List.of(products.toArray(new ProductRecord[0]));
    }

    //add a new product to the warehouse
    public ProductRecord addProduct(UUID id, String productName, Category category, BigDecimal price) {
        //check if product name is null or empty
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("Product name can't be null or empty.");
        }
        //check if the category is null
        if (category == null) {
            throw new IllegalArgumentException("Category can't be null.");
        }
        //check if id is null, if so generate a new UUID
        if (id == null) {
            id = UUID.randomUUID();
        }
        //sets price to zero if it's null
        if (price == null) {
            price = BigDecimal.ZERO;
        }

        //to use in lambda expression
        UUID finalId = id;
        //check if a product with the same id already exists
        if (products.stream().anyMatch(product -> product.id().equals(finalId))) {
            throw new IllegalArgumentException("Product with that id already exists, use updateProduct for updates.");
        }
        // create a new ProductRecord instance, adds new product to products
        ProductRecord prod = new ProductRecord(id, productName, category, price);
        products.add(prod);
        return prod; // returns the newly added product
    }

    //get a product by its UUID
    public Optional<ProductRecord> getProductById(UUID id) {
        return products.stream()
                .filter(product -> product.id().equals(id))
                .findFirst();
    }

    //update the price of prod
    public void updateProductPrice(UUID id, BigDecimal newPrice) {
        // checks if a product with the id exists in the 'products'
        Optional<ProductRecord> prodChange = getProductById(id);
        if (prodChange.isEmpty()) {
            throw new IllegalArgumentException("Product with that id doesn't exist.");
        }

        prodChange.ifPresent(product -> {
            //update the price of the product
            ProductRecord updatedProduct = new ProductRecord(product.id(), product.productName(), product.category(), newPrice);

            //replace the old product with the updated one in the products list
            products.replaceAll(p -> p.id().equals(id) ? updatedProduct : p);

            //add the updated product to the list of changed products
            changedProducts.add(product);
        });
    }

    //get list of changed product
    public static List<ProductRecord> getChangedProducts() {
        return changedProducts;
    }

    //group products by category
    public Map<Category, List<ProductRecord>> getProductsGroupedByCategories() {
        return products.stream()
                .collect(Collectors.groupingBy(ProductRecord::category));
    }

    //get products by category
    public List<ProductRecord> getProductsBy(Category category) {
        return products.stream()
                .filter(product -> product.category().equals(category))
                .collect(Collectors.toList());
    }
}
