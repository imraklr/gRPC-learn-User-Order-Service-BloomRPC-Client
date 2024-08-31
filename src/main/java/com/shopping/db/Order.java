package com.shopping.db;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * The domain object
 */
@Getter
@Setter
public class Order {
    private int userId;
    private int orderId;
    private int numberOfItems;
    private double amount;
    private Date orderDate;
}
