package com.gary.springbootmultithreadingexample.service;

import com.gary.springbootmultithreadingexample.entity.User;
import com.gary.springbootmultithreadingexample.respository.UserRepository;
import com.gary.springbootmultithreadingexample.utils.TimeHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final TimeHelper timeHelper;
    Object target;
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Async
    public CompletableFuture<List<User>> saveUser(MultipartFile file) {
        timeHelper.start();
        List<User> users = parseCSVFile(file);
        logger.info("Saving a list of users of size {}", users.size());
        users = repository.saveAll(users);
        Instant end = Instant.now();
        logger.info("Total time {} ms", timeHelper.timeElapsed());
        return CompletableFuture.completedFuture(users);
    }

    @Async
    public CompletableFuture<List<User>> findAllUsers() {
        logger.info("Get a list of users by {}", Thread.currentThread().getName());
        timeHelper.start();
        List<User> users = repository.findAll();
        Instant end = Instant.now();
        logger.info("Total time {}", timeHelper.timeElapsed());
        return CompletableFuture.completedFuture(users);
    }

    private List<User> parseCSVFile(final MultipartFile file) {
        final List<User> users = new ArrayList<>();
        try{
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(",");
                    final User user = new User();
                    user.setName(data[0]);
                    user.setEmail(data[1]);
                    user.setGender(data[2]);
                    users.add(user);
                }
                return users;
            }
        } catch (final IOException e) {
            logger.error("Failed to parse CSV file {}", e);
            throw new RuntimeException("Failed to parse CSV file {}", e);
        }
    }
}
