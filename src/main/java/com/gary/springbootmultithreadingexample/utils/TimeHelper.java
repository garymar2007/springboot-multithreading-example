package com.gary.springbootmultithreadingexample.utils;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@Data
public class TimeHelper {
    private Instant start;
    private Instant end;

    public void start() {
        this.start = Instant.now();
    }

    public long timeElapsed() {
        this.end = Instant.now();
        return Duration.between(this.start, this.end).toMillis();
    }
}
