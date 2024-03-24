package com.gary.springbootmultithreadingexample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private int employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private boolean newJoiner;
    private boolean learningPending;
    private double salary;
    private int rating;
}

