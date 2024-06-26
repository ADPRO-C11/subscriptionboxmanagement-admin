package id.ac.ui.cs.advprog.snackscription_subscriptionbox.repository;


import id.ac.ui.cs.advprog.snackscription_subscriptionbox.model.SubscriptionBox;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionBoxRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private SubscriptionBoxRepository subscriptionBoxRepository;

    @Test
    void testSave() {
        SubscriptionBox subscriptionBox = new SubscriptionBox("Basic", "Monthly", 100, null, "Basic monthly subscription box");

        // Mock the behavior for hasThreeSimilarNames
        TypedQuery<SubscriptionBox> mockTypedQueryForSimilarNames = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb WHERE LOWER(sb.name) LIKE LOWER(:name)", SubscriptionBox.class))
                .thenReturn(mockTypedQueryForSimilarNames);
        when(mockTypedQueryForSimilarNames.setParameter("name", "%Basic%")).thenReturn(mockTypedQueryForSimilarNames);
        when(mockTypedQueryForSimilarNames.getResultList()).thenReturn(Collections.emptyList()); // No similar names

        // Mock the behavior for existsByNameAndType
        TypedQuery<SubscriptionBox> mockTypedQueryForNameAndType = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb WHERE LOWER(sb.name) = LOWER(:name) AND LOWER(sb.type) = LOWER(:type)", SubscriptionBox.class))
                .thenReturn(mockTypedQueryForNameAndType);
        when(mockTypedQueryForNameAndType.setParameter("name", "Basic")).thenReturn(mockTypedQueryForNameAndType);
        when(mockTypedQueryForNameAndType.setParameter("type", "MONTHLY")).thenReturn(mockTypedQueryForNameAndType); // Match exact case
        when(mockTypedQueryForNameAndType.getResultList()).thenReturn(Collections.emptyList()); // No existing box with same name and type

        SubscriptionBox savedSubscriptionBox = subscriptionBoxRepository.save(subscriptionBox);
        assertEquals(subscriptionBox, savedSubscriptionBox);
        verify(entityManager, times(1)).persist(subscriptionBox);
    }

    @Test
    void testFindAll() {
        SubscriptionBox subscriptionBox1 = new SubscriptionBox("Basic", "Monthly", 100, null, "Basic monthly subscription box");
        SubscriptionBox subscriptionBox2 = new SubscriptionBox("Premium", "Monthly", 200, null, "Premium monthly subscription box");

        TypedQuery<SubscriptionBox> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items", SubscriptionBox.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(subscriptionBox1, subscriptionBox2));

        List<SubscriptionBox> subscriptionBoxes = subscriptionBoxRepository.findAll();

        assertEquals(2, subscriptionBoxes.size());
        verify(entityManager, times(1)).createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items", SubscriptionBox.class);
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindById() {
        SubscriptionBox subscriptionBox = new SubscriptionBox("Basic", "Monthly", 100, null, "Basic monthly subscription box");
        subscriptionBox.setId("1");

        TypedQuery<SubscriptionBox> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.id = :id", SubscriptionBox.class)).thenReturn(query);
        when(query.setParameter("id", "1")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(subscriptionBox));

        Optional<SubscriptionBox> optionalSubscriptionBox = subscriptionBoxRepository.findById("1");

        assertEquals(Optional.of(subscriptionBox), optionalSubscriptionBox);
        verify(entityManager, times(1)).createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.id = :id", SubscriptionBox.class);
        verify(query, times(1)).setParameter("id", "1");
        verify(query, times(1)).getResultStream();
    }

    @Test
    void testFindByIdSubscriptionNotFound() {
        TypedQuery<SubscriptionBox> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.id = :id", SubscriptionBox.class)).thenReturn(query);
        when(query.setParameter("id", "nonexistentId")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        Optional<SubscriptionBox> result = subscriptionBoxRepository.findById("nonexistentId");

        assertFalse(result.isPresent());
        verify(entityManager, times(1)).createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.id = :id", SubscriptionBox.class);
        verify(query, times(1)).setParameter("id", "nonexistentId");
        verify(query, times(1)).getResultStream();
    }

    @Test
    void testUpdate() {
        SubscriptionBox subscriptionBox = new SubscriptionBox("Basic", "Monthly", 100, null, "Basic monthly subscription box");

        when(entityManager.merge(subscriptionBox)).thenReturn(subscriptionBox);

        SubscriptionBox updatedSubscriptionBox = subscriptionBoxRepository.update(subscriptionBox);

        assertEquals(subscriptionBox, updatedSubscriptionBox);
        verify(entityManager, times(1)).merge(subscriptionBox);
    }

    @Test
    void testDelete() {
        SubscriptionBox subscriptionBox = new SubscriptionBox("Basic", "Monthly", 100, null, "Basic monthly subscription box");
        subscriptionBox.setId("1");

        TypedQuery<SubscriptionBox> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.id = :id", SubscriptionBox.class)).thenReturn(query);
        when(query.setParameter("id", "1")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(subscriptionBox));

        doNothing().when(entityManager).remove(subscriptionBox);

        subscriptionBoxRepository.delete("1");

        verify(entityManager, times(1)).createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.id = :id", SubscriptionBox.class);
        verify(query, times(1)).setParameter("id", "1");
        verify(query, times(1)).getResultStream();
        verify(entityManager, times(1)).remove(subscriptionBox);
    }

    @Test
    void testDeleteSubscriptionNotFound() {
        TypedQuery<SubscriptionBox> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.id = :id", SubscriptionBox.class)).thenReturn(query);
        when(query.setParameter("id", "1")).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.empty());

        assertThrows(IllegalArgumentException.class, () -> subscriptionBoxRepository.delete("1"));

        verify(entityManager, times(1)).createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.id = :id", SubscriptionBox.class);
        verify(query, times(1)).setParameter("id", "1");
        verify(query, times(1)).getResultStream();
    }

    @Test
    void testFindByPriceLessThan() {
        SubscriptionBox subscriptionBox1 = new SubscriptionBox("Basic", "Monthly", 100, null, "Basic monthly subscription box");

        TypedQuery<SubscriptionBox> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.price < :price", SubscriptionBox.class)).thenReturn(query);
        when(query.setParameter("price", 150)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(subscriptionBox1));

        List<SubscriptionBox> result = subscriptionBoxRepository.findByPriceLessThan(150);

        assertEquals(1, result.size());
        assertEquals(subscriptionBox1, result.get(0));
        verify(entityManager, times(1)).createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.price < :price", SubscriptionBox.class);
        verify(query, times(1)).setParameter("price", 150);
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindByPriceGreaterThan() {
        SubscriptionBox subscriptionBox2 = new SubscriptionBox("Premium", "Monthly", 200, null, "Premium monthly subscription box");

        TypedQuery<SubscriptionBox> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.price > :price", SubscriptionBox.class)).thenReturn(query);
        when(query.setParameter("price", 150)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(subscriptionBox2));

        List<SubscriptionBox> result = subscriptionBoxRepository.findByPriceGreaterThan(150);

        assertEquals(1, result.size());
        assertEquals(subscriptionBox2, result.getFirst());
        verify(entityManager, times(1)).createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.price > :price", SubscriptionBox.class);
        verify(query, times(1)).setParameter("price", 150);
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindByPriceEquals() {
        SubscriptionBox subscriptionBox1 = new SubscriptionBox("Basic", "Monthly", 100, null, "Basic monthly subscription box");

        TypedQuery<SubscriptionBox> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.price = :price", SubscriptionBox.class)).thenReturn(query);
        when(query.setParameter("price", 100)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(subscriptionBox1));

        List<SubscriptionBox> result = subscriptionBoxRepository.findByPriceEquals(100);

        assertEquals(1, result.size());
        assertEquals(subscriptionBox1, result.get(0));
        verify(entityManager, times(1)).createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.price = :price", SubscriptionBox.class);
        verify(query, times(1)).setParameter("price", 100);
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindByName() {
        SubscriptionBox subscriptionBox1 = new SubscriptionBox("Basic Box", "Monthly", 100, null, "Basic monthly subscription box");
        SubscriptionBox subscriptionBox2 = new SubscriptionBox("Basic Box", "Quarterly", 200, null, "Premium monthly subscription box");

        TypedQuery<SubscriptionBox> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.name = :name", SubscriptionBox.class)).thenReturn(query);
        when(query.setParameter("name", "Basic Box")).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(subscriptionBox1, subscriptionBox2));

        Optional<List<SubscriptionBox>> result = subscriptionBoxRepository.findByName("Basic Box");

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        verify(entityManager, times(1)).createQuery("SELECT sb FROM SubscriptionBox sb LEFT JOIN FETCH sb.items WHERE sb.name = :name", SubscriptionBox.class);
        verify(query, times(1)).setParameter("name", "Basic Box");
        verify(query, times(1)).getResultList();
    }

    @Test
    void testFindDistinctNames() {
        TypedQuery<String> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT DISTINCT sb.name FROM SubscriptionBox sb", String.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList("Basic Box", "Premium Box"));

        Optional<List<String>> result = subscriptionBoxRepository.findDistinctNames();

        assertTrue(result.isPresent());
        assertEquals(2, result.get().size());
        assertTrue(result.get().contains("Basic Box"));
        assertTrue(result.get().contains("Premium Box"));
        verify(entityManager, times(1)).createQuery("SELECT DISTINCT sb.name FROM SubscriptionBox sb", String.class);
        verify(query, times(1)).getResultList();
    }
}
