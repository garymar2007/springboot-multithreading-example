package com.gary.springbootmultithreadingexample.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gary.springbootmultithreadingexample.SpringbootMultithreadingExampleApplication;
import com.gary.springbootmultithreadingexample.dto.Employee;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class EmployeeService {

    public Void saveEmployeeWithRunAsyncFutureByForkJoinPoolDefaultPool(File jsonFile) throws ExecutionException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Employee> employees = objectMapper.readValue(jsonFile, new TypeReference<List<Employee>>() {});

                    //write logic to save list of employee to database
                    //repository.saveAll(employees);
                    System.out.println("Thread: " + Thread.currentThread().getName());
                    employees.stream().forEach(System.out::println);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return runAsyncFuture.get();
    }

    public Void saveEmployeeWithRunAsyncFutureLambdaByCustomPool(File jsonFile) throws ExecutionException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        Executor executor = Executors.newFixedThreadPool(5);
        CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(() -> {

                try {
                    List<Employee> employees = objectMapper.readValue(jsonFile, new TypeReference<List<Employee>>() {});

                    //write logic to save list of employee to database
                    //repository.saveAll(employees);
                    System.out.println("Thread: " + Thread.currentThread().getName());
                    employees.stream().forEach(System.out::println);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }, executor);
        return runAsyncFuture.get();
    }

    public File readJsonFile() {
        URL resource = SpringbootMultithreadingExampleApplication.class.getClassLoader().getResource("employee.json");
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        } else {
            System.out.println("File found: " + resource.getPath());
        }
        return new File(resource.getPath());
    }

    public List<Employee> fetchEmployeeListBySupplyAsync(File jsonFile) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Employee>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync with defaultPool Executed by Thread: " + Thread.currentThread().getName());
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(jsonFile, new TypeReference<List<Employee>>() {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return listCompletableFuture.get();
    }

    public List<Employee> fetchEmployeeListBySupplyAsyncWithCustomPool(File jsonFile) throws ExecutionException, InterruptedException {
        Executor executor = Executors.newFixedThreadPool(5);
        CompletableFuture<List<Employee>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync with customPool Executed by Thread: " + Thread.currentThread().getName());
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(jsonFile, new TypeReference<List<Employee>>() {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor);

        return listCompletableFuture.get();
    }
}
