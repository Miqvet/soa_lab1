package ru.itmo.labs.soa_lab1.repository.entity;

public class Flat {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int area; //Максимальное значение поля: 784, Значение поля должно быть больше 0
    private int numberOfRooms; //Максимальное значение поля: 8, Значение поля должно быть больше 0
    private double livingSpace; //Значение поля должно быть больше 0
    private Furnish furnish; //Поле может быть null
    private Transport transport; //Поле не может быть null
    private House house; //Поле не может быть null
}