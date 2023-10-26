package org.example.warehouse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Category {
    //Private Field
    private final String name;
    private final static Map<String, Category> sameInstanceSameName = new HashMap<>();

    //Private Constructor
    private Category(String name) {
        this.name = name;
    }

    //Method
    public String getName() {
        return name;
    }

    public static Category of(String name) {
        if (name == null)
            throw new IllegalArgumentException("Category name can't be null");

        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        // return new category name
        if (sameInstanceSameName.containsKey(name)) {
            return sameInstanceSameName.get(name);
        }
        Category category = new Category(name);
        sameInstanceSameName.put(name, category);
        return category;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
