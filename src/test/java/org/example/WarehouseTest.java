package org.example;

import org.example.warehouse.Category;
import org.example.warehouse.ProductRecord;
import org.example.warehouse.Warehouse;
import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("A warehouse")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WarehouseTest {

    Warehouse warehouse;

    @Test
    @DisplayName("should have no public constructors")
    @Order(1)
    @Tag("basic")
    void shouldHaveNoPublicConstructors() {
        Class<Warehouse> clazz = Warehouse.class;
        Constructor<?>[] constructors = clazz.getConstructors();
        assertEquals(0, constructors.length, "The class should not have any public constructors");
    }

    @Test
    @DisplayName("is created by calling createWarehouse")
    @Order(2)
    @Tag("basic")
    void isCreatedWithFactory() {
        Warehouse warehouse = Warehouse.getInstance();
        assertThat(warehouse).isNotNull();
    }

    @Test
    @DisplayName("can be created with a name")
    @Order(3)
    @Tag("basic")
    void canBeCreatedUsingAName() {
        Warehouse warehouse = Warehouse.getInstance("MyStore");
        assertThat(warehouse).isNotNull().extracting("name").isEqualTo("MyStore");
    }

    @Test
    @DisplayName("should be the same instance when using the same name")
    @Order(4)
    @Tag("basic")
    void shouldBeSameInstanceForSameName() {
        Warehouse warehouse1 = Warehouse.getInstance("Just a name");
        Warehouse warehouse2 = Warehouse.getInstance("Just a name");

        assertThat(warehouse1).isNotNull();
        assertThat(warehouse2).isNotNull();
        assertThat(warehouse1).isSameAs(warehouse2);
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {

        @BeforeEach
        void createWarehouse() {
            warehouse = Warehouse.getInstance("New warehouse");
        }

        @Test
        @DisplayName("is empty")
        void isEmpty() {
            assertThat(warehouse.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("returns empty list of products")
        void returnsEmptyListOfProducts() {
            assertThat(warehouse.getProducts()).isEmpty();
        }


    }

    @Nested
    @DisplayName("adding one product")
    class AfterAddingProduct {

        ProductRecord addedProduct;
        String UUID_name = "5fc03087-d265-11e7-b8c6-83e29cd24f4c";

        @BeforeEach
        void addingAProduct() {
            warehouse = Warehouse.getInstance("New warehouse");
            addedProduct = warehouse.addProduct(UUID.randomUUID(), "Milk", Category.of("Dairy"), BigDecimal.valueOf(999, 2));
        }

        @Test
        @DisplayName("throws IllegalArgumentException for empty product name")
        void throwsIllegalArgumentExceptionForNoProductname() {
            assertThatThrownBy(() ->
                    warehouse.addProduct(UUID.randomUUID(), "", Category.of("Test"), BigDecimal.valueOf(999, 2)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product name can't be null or empty.");
        }

        @Test
        @DisplayName("throws IllegalArgumentException for null product name")
        void throwsIllegalArgumentExceptionForNullProductName() {
            assertThatThrownBy(() ->
                    warehouse.addProduct(UUID.randomUUID(), null, Category.of("Test"), BigDecimal.valueOf(999, 2)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product name can't be null or empty.");
        }

        @Test
        @DisplayName("throws IllegalArgumentException for null category")
        void throwsIllegalArgumentExceptionForNullCategory() {
            assertThatThrownBy(() ->
                    warehouse.addProduct(UUID.randomUUID(), "Test", null, BigDecimal.valueOf(999, 2)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Category can't be null.");
        }

        @Test
        @DisplayName("assigns id if null")
        void assignsIdIfNull() {
            var productRecord = warehouse.addProduct(null, "Test", Category.of("Test"), BigDecimal.valueOf(999, 2));
            assertThat(productRecord.uuid()).isNotNull();
        }

        @Test
        @DisplayName("sets price to 0 if null")
        void setsPriceTo0IfNull() {
            var productRecord = warehouse.addProduct(UUID.randomUUID(), "Test", Category.of("Test"), null);
            assertThat(productRecord.price()).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("it is no longer empty")
        void itIsNoLongerEmpty() {
            assertThat(warehouse.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("returns list with that product")
        void getAllShouldReturnListWithOneProduct() {
            assertThat(warehouse.getProducts()).containsExactly(addedProduct);
        }

        @Test
        @DisplayName("valid id returns product")
        void getProductByIdShouldReturnProductWithThatId() {
            assertThat(warehouse.getProductById(addedProduct.uuid())).contains(addedProduct);
        }

        @Test
        @DisplayName("invalid id returns empty")
        void getSingleProductWithInvalidIdShouldBeEmpty() {
            assertThat(warehouse.getProductById(UUID.fromString(UUID_name))).isEmpty();
        }

        @Test
        @DisplayName("throws IllegalArgumentException when using existing id")
        void shouldThrowExceptionIfTryingToAddProductWithSameId() {
            assertThatThrownBy(() ->
                    warehouse.addProduct(UUID.randomUUID(), "Milk", Category.of("Dairy"), BigDecimal.valueOf(999, 2)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product with that id already exists, use updateProduct for updates.");
        }
    }

    @Nested
    @DisplayName("after adding multiple products")
    class AfterAddingMultipleProducts {
        List<ProductRecord> addedProducts = new ArrayList<>();
        String UUID_name = "5fc03087-d265-11e7-b8c6-83e29cd24f4c";

        @BeforeEach
        void addingMultipleProducts() {
            warehouse = Warehouse.getInstance("New warehouse");
            addedProducts.add(warehouse.addProduct(UUID.randomUUID(), "Milk", Category.of("Dairy"), BigDecimal.valueOf(999, 2)));
            addedProducts.add(warehouse.addProduct(UUID.randomUUID(), "Apple", Category.of("Fruit"), BigDecimal.valueOf(290, 2)));
            addedProducts.add(warehouse.addProduct(UUID.randomUUID(), "Bacon", Category.of("Meat"), BigDecimal.valueOf(1567, 2)));
        }

        @Test
        @DisplayName("returns list with all products")
        void returnsListWithAllProducts() {
            assertThat(warehouse.getProducts()).isEqualTo(addedProducts);
        }

        @Test
        @DisplayName("changing a products price should be saved")
        void changingAProductsNameShouldBeSaved() {
            warehouse.updateProductPrice(addedProducts.get(1).uuid(), BigDecimal.valueOf(311, 2));
            assertThat(warehouse.getProductById(addedProducts.get(1).uuid())).isNotEmpty()
                    .get()
                    .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(311, 2));
        }

        @Test
        @DisplayName("find changed products is empty")
        void findChangedProductsShouldBeEmpty() {
            assertThat(warehouse.getChangedProducts()).isEmpty();
        }

        @Test
        @DisplayName("find changed products returns product")
        void andChangingOneFindChangedProductsShouldReturnThatProduct() {
            warehouse.updateProductPrice(addedProducts.get(1).uuid(), BigDecimal.valueOf(311, 2));
            assertThat(warehouse.getChangedProducts()).containsOnly(addedProducts.get(1));
        }

        @Test
        @DisplayName("group them by category")
        void getAMapWithAllProductsForEachCategory() {
            Map<Category, List<ProductRecord>> productsOfCategories =
                    Map.of(addedProducts.get(0).category(), List.of(addedProducts.get(0)),
                            addedProducts.get(1).category(), List.of(addedProducts.get(1)),
                            addedProducts.get(2).category(), List.of(addedProducts.get(2)));
            assertThat(warehouse.getProductsGroupedByCategories()).isEqualTo(productsOfCategories);
        }

        @Test
        @DisplayName("list returned from getProducts should be unmodifiable")
        void listReturnedFromGetProductsShouldBeImmutable() {
            var products = warehouse.getProducts();
            assertThatThrownBy(() -> products.remove(0))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when trying to change an invalid id")
        void throwsIllegalArgumentExceptionWhenTryingToChangeAnInvalidId() {
            assertThatThrownBy(() ->
                    warehouse.updateProductPrice(UUID.fromString("9e120341-627f-32be-8393-58b5d655b751"), BigDecimal.valueOf(1000, 2)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product with that id doesn't exist.");
        }

        @Test
        @DisplayName("find all products belonging to a category")
        void findProductsBelongingToACategory() {
            assertThat(warehouse.getProductsBy(Category.of("Meat")))
                    .containsOnly(addedProducts.get(2));
        }

        @Test
        @DisplayName("find multiple products from same category")
        void findMultipleProductsFromSameCategory() {
            addedProducts.add(warehouse.addProduct(UUID.randomUUID(), "Steak", Category.of("Meat"), BigDecimal.valueOf(399, 0)));
            assertThat(warehouse.getProductsBy(Category.of("Meat")))
                    .containsOnly(addedProducts.get(2), addedProducts.get(3));
        }
    }
}
