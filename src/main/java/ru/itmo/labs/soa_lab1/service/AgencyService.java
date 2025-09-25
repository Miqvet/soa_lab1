package ru.itmo.labs.soa_lab1.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmo.labs.soa_lab1.repository.FlatRepository;
import ru.itmo.labs.soa_lab1.repository.entity.Flat;

import java.util.*;

@Service
@Transactional
public class AgencyService {
    
    @Autowired
    private FlatRepository flatRepository;

    public Optional<Flat> findWithBalcony(boolean cheapest, boolean withBalcony) {
        List<Flat> flats;
        
        if (cheapest) {
            flats = flatRepository.findByHasBalconyOrderByPriceAsc(withBalcony);
        } else {
            flats = flatRepository.findByHasBalconyOrderByPriceDesc(withBalcony);
        }
        
        return flats.stream().findFirst();
    }

    public Optional<Flat> getMostExpensiveAmongThree(Long id1, Long id2, Long id3) {
        List<Long> ids = Arrays.asList(id1, id2, id3);
        List<Flat> flats = flatRepository.findByIdIn(ids);

        if (flats.size() != 3) {
            return Optional.empty();
        }

        return flats.stream()
                .max(Comparator.comparingDouble(Flat::getPrice));
    }
}