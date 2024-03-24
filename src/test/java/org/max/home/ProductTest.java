package org.max.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;

import javax.persistence.PersistenceException;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductTest extends AbstractTest {

    @Test
    @Order(1)
    void getProductCount_whenValid_shouldReturnCorrectCount() {
        // Given
        final Query query = getSession().createQuery("SELECT count(*) FROM ProductsEntity");
        long countBeforeInsert = (long) query.uniqueResult();

        // When
        long expectedCount = 10;

        // Then
        Assertions.assertEquals(expectedCount, countBeforeInsert);
    }

    @Test
    @Order(2)
    void addProduct_whenValid_shouldSave() {
        // Given
        ProductsEntity entity = new ProductsEntity();
        entity.setMenuName("New Product");
        entity.setPrice("420");

        // When
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        // Then
        final Query query = getSession().createQuery("SELECT count(*) FROM ProductsEntity");
        long countAfterInsert = (long) query.uniqueResult();
        Assertions.assertEquals(11, countAfterInsert);

        Optional<ProductsEntity> productOptional = session.createQuery("FROM ProductsEntity WHERE menuName = :menuName", ProductsEntity.class)
                .setParameter("menuName", "New Product")
                .uniqueResultOptional();
        Assertions.assertTrue(productOptional.isPresent());
    }

    @Test
    @Order(3)
    void deleteProduct_whenValid_shouldDelete() {
        // Given
        Session session = getSession();
        Optional<ProductsEntity> productOptional = session.createQuery("FROM ProductsEntity WHERE menuName = :menuName", ProductsEntity.class)
                .setParameter("menuName", "New Product")
                .uniqueResultOptional();
        Assertions.assertTrue(productOptional.isPresent());
        ProductsEntity productToDelete = productOptional.get();

        // When
        session.beginTransaction();
        session.delete(productToDelete);
        session.getTransaction().commit();

        // Then
        Optional<ProductsEntity> deletedProductOptional = session.createQuery("FROM ProductsEntity WHERE menuName = :menuName", ProductsEntity.class)
                .setParameter("menuName", "New Product")
                .uniqueResultOptional();
        Assertions.assertFalse(deletedProductOptional.isPresent());
    }

    @Test
    @Order(4)
    void addProduct_whenNotValid_shouldThrowException() {
        // Given
        ProductsEntity entity = new ProductsEntity();

        // When
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);

        // Then
        Assertions.assertThrows(PersistenceException.class, () -> session.getTransaction().commit());
    }
}
