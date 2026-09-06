package com.sageb18.distributedorderinventory.dao;

import com.sageb18.distributedorderinventory.model.Product;
import com.sageb18.distributedorderinventory.repository.ProductRepository;
import com.sageb18.distributedorderinventory.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ProductService. The ProductRepository is mocked, so nothing
 * here talks to a real (or local) DynamoDB.
 */
@ExtendWith(MockitoExtension.class)
public class JpaProductDaoTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProductAssignsAGeneratedProductId() {
        Product product = new Product(null, "widget", new BigDecimal("19.99"), 5);

        productService.createProduct(product);

        assertThat(product.getProductId()).isNotBlank();
        assertThatCode(() -> UUID.fromString(product.getProductId())).doesNotThrowAnyException();
    }

    @Test
    void createProductOverwritesAnyClientSuppliedProductId() {
        Product product = new Product("client-supplied-id", "widget", new BigDecimal("19.99"), 5);

        productService.createProduct(product);

        assertThat(product.getProductId()).isNotEqualTo("client-supplied-id");
    }

    @Test
    void createProductPassesTheProductToTheRepositoryWithTheRemainingFieldsUntouched() {
        Product product = new Product(null, "widget", new BigDecimal("19.99"), 5);

        productService.createProduct(product);

        ArgumentCaptor<Product> handedToRepository = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(handedToRepository.capture());

        Product saved = handedToRepository.getValue();
        assertThat(saved.getName()).isEqualTo("widget");
        assertThat(saved.getPrice()).isEqualByComparingTo("19.99");
        assertThat(saved.getStock()).isEqualTo(5);
        assertThat(saved.getProductId()).isNotBlank();
    }

    @Test
    void createProductReturnsWhateverTheRepositoryPersisted() {
        Product persisted = new Product("persisted-id", "widget", new BigDecimal("24.50"), 7);
        when(productRepository.save(any(Product.class))).thenReturn(persisted);

        Product created = productService.createProduct(new Product(null, "widget", new BigDecimal("19.99"), 5));

        assertThat(created).isSameAs(persisted);
        assertThat(created.getProductId()).isEqualTo("persisted-id");
        assertThat(created.getPrice()).isEqualByComparingTo("24.50");
        assertThat(created.getStock()).isEqualTo(7);
    }

    @Test
    void createProductGeneratesADistinctProductIdPerCall() {
        Product first = new Product(null, "widget", new BigDecimal("19.99"), 1);
        Product second = new Product(null, "widget", new BigDecimal("19.99"), 1);

        productService.createProduct(first);
        productService.createProduct(second);

        assertThat(first.getProductId()).isNotEqualTo(second.getProductId());
    }

    @Test
    void getProductByIdReturnsTheProductFoundByTheRepository() {
        Product product = new Product("product-1", "widget", new BigDecimal("19.99"), 5);
        when(productRepository.findById("product-1")).thenReturn(product);

        Product found = productService.getProductById("product-1");

        assertThat(found.getProductId()).isEqualTo("product-1");
        assertThat(found.getName()).isEqualTo("widget");
        assertThat(found.getPrice()).isEqualByComparingTo("19.99");
        assertThat(found.getStock()).isEqualTo(5);
    }

    @Test
    void getProductByIdReturnsNullWhenTheProductDoesNotExist() {
        assertThat(productService.getProductById("missing-id")).isNull();
    }

    @Test
    void getAllProductsReturnsEveryProductFromTheRepository() {
        Product first = new Product("product-1", "widget", new BigDecimal("19.99"), 1);
        Product second = new Product("product-2", "gadget", new BigDecimal("4.25"), 2);
        when(productRepository.findAll()).thenReturn(List.of(first, second));

        Collection<Product> products = productService.getAllProducts();

        assertThat(products).containsExactly(first, second);
    }

    @Test
    void getAllProductsReturnsAnEmptyCollectionWhenNoProductsExist() {
        assertThat(productService.getAllProducts()).isEmpty();
    }
}
