package com.gary.springbootmultithreadingexample.service;

import com.gary.springbootmultithreadingexample.dto.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeReminderService {
    private final EmployeeService employeeService;
    public CompletableFuture<Void> sendReminderToEmployees() {
        Executor executor = Executors.newFixedThreadPool(5);
        File jsonFile = employeeService.readJsonFile();
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Fetching employees: " + Thread.currentThread().getName());
            try {
                return employeeService.fetchEmployeeListBySupplyAsyncWithCustomPool(jsonFile);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executor).thenApplyAsync(employees -> {
            System.out.println("Filting the employees for new employees with pending training: " + Thread.currentThread().getName());
            return employees.stream().filter(employee -> employee.isNewJoiner() && employee.isLearningPending())
                    .collect(Collectors.toList());
        }, executor).thenApplyAsync(employees -> {
            System.out.println("Get emails for those new employees with pending training: " + Thread.currentThread().getName());
            return employees.stream().map(Employee::getEmail)
                    .collect(Collectors.toList());
        }, executor).thenAcceptAsync((emails) -> {
            System.out.println("Sending emails to new employees with pending training: " + Thread.currentThread().getName()
                    + ", the number of employees: " + emails.size());
            emails.forEach(EmployeeReminderService::sendEmail);
        });

        return voidCompletableFuture;
    }

    public static void sendEmail(String email) {
        System.out.println("Sending training reminder email to: " + email);
    }
}
