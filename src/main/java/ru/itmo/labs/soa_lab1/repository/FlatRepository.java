package ru.itmo.labs.soa_lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.labs.soa_lab1.repository.entity.Flat;
import ru.itmo.labs.soa_lab1.repository.entity.Furnish;
import ru.itmo.labs.soa_lab1.repository.entity.Transport;

import java.util.List;

@Repository
public interface FlatRepository extends JpaRepository<Flat, Long> {
    // Для AgencyService
    List<Flat> findByHasBalconyOrderByPriceAsc(boolean hasBalcony);
    List<Flat> findByHasBalconyOrderByPriceDesc(boolean hasBalcony);
    List<Flat> findByIdIn(List<Long> ids);

    // Существующие методы...
    List<Flat> findByHasBalcony(boolean hasBalcony);
    List<Flat> findByAreaLessThanEqual(int maxArea);
    List<Flat> findByTransport(Transport transport);
    List<Flat> findByFurnish(Furnish furnish);
    List<Flat> findByNameContainingIgnoreCase(String namePart);
}