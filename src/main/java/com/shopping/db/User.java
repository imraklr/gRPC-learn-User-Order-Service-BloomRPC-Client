package com.shopping.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int id;
    private String username;
    private String name;
    private int age;
    private String gender;
    private int noOfOrders;
}
