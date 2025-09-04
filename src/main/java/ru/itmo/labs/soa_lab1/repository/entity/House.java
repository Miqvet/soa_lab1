package ru.itmo.labs.soa_lab1.repository.entity;

public class House {

    private String name; //Поле может быть null
    private long year; //Максимальное значение поля: 370, Значение поля должно быть больше 0
    private long numberOfFloors; //Значение поля должно быть больше 0
    private Integer numberOfLifts; //Значение поля должно быть больше 0
}