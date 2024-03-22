package com.gary.springbootmultithreadingexample.controller;

import com.gary.springbootmultithreadingexample.entity.User;
import com.gary.springbootmultithreadingexample.service.UserService;
import com.gary.springbootmultithreadingexample.utils.TimeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TimeHelper timeHelper;

    @PostMapping(value = "/users",
            consumes={MediaType.MULTIPART_FORM_DATA_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity saveUsers(@RequestParam(value = "files") MultipartFile[] files) {
        for(MultipartFile file : files) {
            userService.saveUser(file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value="/users",
            produces={MediaType.APPLICATION_JSON_VALUE})
    public CompletableFuture<ResponseEntity> findAllUsers() {
        return userService.findAllUsers()
                .thenApply(ResponseEntity::ok);
               // .exceptionally(handleGetUsersFailure);

    }

    @GetMapping(value="/getUsersByThread",
            produces={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getUsers() {
        timeHelper.start();
        CompletableFuture<List<User>> users1 = userService.findAllUsers();
        CompletableFuture<List<User>> users2 = userService.findAllUsers();
        CompletableFuture<List<User>> users3 = userService.findAllUsers();

        CompletableFuture.allOf(users1, users2, users3).join();
        System.out.println("Total Elapsed Time for getUsersByThread: " + timeHelper.timeElapsed() + " ms");
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
