package ru.itmo.labs.soa_lab1.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.labs.soa_lab1.repository.FlatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itmo.labs.soa_lab1.repository.entity.*;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    
    @Autowired
    private FlatRepository flatRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Очищаем таблицы
        flatRepository.deleteAll();
        
        // Создаем и сохраняем 4 квартиры
        createSampleFlats();
        System.out.println("=== 4 ТЕСТОВЫЕ КВАРТИРЫ СОХРАНЕНЫ В БАЗУ ===");
    }
    
    private void createSampleFlats() {
        // Квартира 1
        Coordinates coords1 = new Coordinates();
        coords1.setX(10L);
        coords1.setY(-100.0f);
        
        House house1 = new House();
        house1.setName("Elite Tower");
        house1.setYear(2020L);
        house1.setNumberOfFloors(25);
        house1.setNumberOfLifts(4);
        
        Flat flat1 = new Flat();
        flat1.setName("Luxury Apartment");
        flat1.setCoordinates(coords1);
        flat1.setCreationDate(java.time.LocalDate.of(2024, 1, 15));
        flat1.setArea(100);
        flat1.setNumberOfRooms(3);
        flat1.setLivingSpace(75.5);
        flat1.setFurnish(Furnish.DESIGNER);
        flat1.setTransport(Transport.ENOUGH);
        flat1.setHouse(house1);
        flat1.setPrice(15000000.0);
        flat1.setHasBalcony(true);
        
        flatRepository.save(flat1);
        
        // Квартира 2
        Coordinates coords2 = new Coordinates();
        coords2.setX(20L);
        coords2.setY(200.5f);
        
        House house2 = new House();
        house2.setName("Comfort House");
        house2.setYear(2015L);
        house2.setNumberOfFloors(9);
        house2.setNumberOfLifts(2);
        
        Flat flat2 = new Flat();
        flat2.setName("Comfort Flat");
        flat2.setCoordinates(coords2);
        flat2.setCreationDate(java.time.LocalDate.of(2024, 1, 16));
        flat2.setArea(65);
        flat2.setNumberOfRooms(2);
        flat2.setLivingSpace(45.0);
        flat2.setFurnish(Furnish.FINE);
        flat2.setTransport(Transport.LITTLE);
        flat2.setHouse(house2);
        flat2.setPrice(8000000.0);
        flat2.setHasBalcony(false);
        
        flatRepository.save(flat2);
        
        // Квартира 3
        Coordinates coords3 = new Coordinates();
        coords3.setX(30L);
        coords3.setY(-500.0f);
        
        House house3 = new House();
        house3.setName("Sky Residence");
        house3.setYear(2022L);
        house3.setNumberOfFloors(30);
        house3.setNumberOfLifts(6);
        
        Flat flat3 = new Flat();
        flat3.setName("Studio Downtown");
        flat3.setCoordinates(coords3);
        flat3.setCreationDate(java.time.LocalDate.of(2024, 1, 17));
        flat3.setArea(45);
        flat3.setNumberOfRooms(1);
        flat3.setLivingSpace(35.2);
        flat3.setFurnish(Furnish.LITTLE);
        flat3.setTransport(Transport.FEW);
        flat3.setHouse(house3);
        flat3.setPrice(5000000.0);
        flat3.setHasBalcony(true);
        
        flatRepository.save(flat3);
        
        // Квартира 4
        Coordinates coords4 = new Coordinates();
        coords4.setX(40L);
        coords4.setY(300.2f);
        
        House house4 = new House();
        house4.setName("City View");
        house4.setYear(2018L);
        house4.setNumberOfFloors(12);
        house4.setNumberOfLifts(3);
        
        Flat flat4 = new Flat();
        flat4.setName("Family Apartment");
        flat4.setCoordinates(coords4);
        flat4.setCreationDate(java.time.LocalDate.of(2024, 1, 18));
        flat4.setArea(120);
        flat4.setNumberOfRooms(4);
        flat4.setLivingSpace(90.0);
        flat4.setFurnish(Furnish.FINE);
        flat4.setTransport(Transport.ENOUGH);
        flat4.setHouse(house4);
        flat4.setPrice(12000000.0);
        flat4.setHasBalcony(true);
        
        flatRepository.save(flat4);
    }
}