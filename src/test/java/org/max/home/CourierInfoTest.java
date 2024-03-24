package org.max.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import javax.persistence.PersistenceException;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourierInfoTest extends AbstractTest {

    @Test
    @Order(1)
    void getCourierInfo_whenValid_shouldReturn() {
        // Given
        final Query query = getSession().createQuery("FROM CourierInfoEntity");

        // When
        int resultSize = query.list().size();

        // Then
        Assertions.assertEquals(4, resultSize);
    }

    @Test
    @Order(2)
    void addCourierInfo_whenValid_shouldSave() {
        // Given
        CourierInfoEntity entity = new CourierInfoEntity();
        entity.setCourierId((short) 5);
        entity.setFirstName("Alex");
        entity.setLastName("Smith");
        entity.setPhoneNumber("+7 960 123 4567");
        entity.setDeliveryType("car");

        // When
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession().createQuery("FROM CourierInfoEntity WHERE courierId=5");
        CourierInfoEntity courierInfoEntity = (CourierInfoEntity) query.uniqueResult();

        // Then
        Assertions.assertNotNull(courierInfoEntity);
        Assertions.assertEquals("Alex", courierInfoEntity.getFirstName());
    }

    @Test
    @Order(3)
    void deleteCourierInfo_whenValid_shouldDelete() {
        // Given
        final Query query = getSession().createQuery("FROM CourierInfoEntity WHERE courierId=5");
        Optional<CourierInfoEntity> courierInfoEntity = Optional.ofNullable((CourierInfoEntity) query.uniqueResult());
        Assumptions.assumeTrue(courierInfoEntity.isPresent());

        // When
        Session session = getSession();
        session.beginTransaction();
        session.delete(courierInfoEntity.get());
        session.getTransaction().commit();

        // Then
        final Query queryAfterDelete = getSession().createQuery("FROM CourierInfoEntity WHERE courierId=5");
        Optional<CourierInfoEntity> courierInfoEntityAfterDelete = Optional.ofNullable((CourierInfoEntity) queryAfterDelete.uniqueResult());
        Assertions.assertTrue(courierInfoEntityAfterDelete.isEmpty());
    }

    @Test
    @Order(4)
    void addCourierInfo_whenNotValid_shouldThrow() {
        // Given
        CourierInfoEntity entity = new CourierInfoEntity();

        // When
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);

        // Then
        Assertions.assertThrows(PersistenceException.class, () -> session.getTransaction().commit());
    }
}
