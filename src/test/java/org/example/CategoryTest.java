package org.example;

import org.example.warehouse.Category;
import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Category")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryTest {

    @Test
    @DisplayName("has no public constructors")
    @Order(1)
    void shouldHaveNoPublicConstructors() {
        Class<Category> clazz = Category.class;
        Constructor<?>[] constructors = clazz.getConstructors();
        assertEquals(0, constructors.length, "The class should not have any public constructors");
    }

    @Test
    @DisplayName("of returns an instance")
    @Order(2)
    void canBeCreatedFromOfMethod() {
        Category category = Category.of("Name");
        assertThat(category.getName()).isEqualTo("Name");
    }

    @Test
    @DisplayName("with same name always is same instance")
    @Order(3)
    void shouldBeOfSameInstanceForSameName() {
        Category category1 = Category.of("Test");
        Category category2 = Category.of("Test");
        assertThat(category1).isSameAs(category2);
    }

    @Test
    @DisplayName("should always have uppercase first letter")
    @Order(4)
    void shouldAlwaysHaveUppercaseFirstLetter() {
        Category category = Category.of("test");
        assertThat(category.getName()).isEqualTo("Test");
    }

    @Test
    @DisplayName("of throws IllegalArgumentException if name is null")
    @Order(5)
    void ofThrowsIllegalArgumentExceptionIfNameIsNull() {
        assertThatThrownBy(() -> Category.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category name can't be null");
    }
}
