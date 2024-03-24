package com.gary.springbootmultithreadingexample;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gary.springbootmultithreadingexample.dto.Employee;
import com.gary.springbootmultithreadingexample.service.EmployeeService;
import com.gary.springbootmultithreadingexample.utils.TimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class SpringbootMultithreadingExampleApplication implements CommandLineRunner {
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private TimeHelper timeHelper;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMultithreadingExampleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Print 1000 employee by using RunAsync with default ForkJoinPool.commonPool() AGAINST CutomerPool");
		timeHelper.start();
		File jsonFile = employeeService.readJsonFile();
		employeeService.saveEmployeeWithRunAsyncFutureByForkJoinPoolDefaultPool(jsonFile);
		long defaultTimer =  timeHelper.timeElapsed();

		timeHelper.start();
		File jsonFile1 = employeeService.readJsonFile();
		employeeService.saveEmployeeWithRunAsyncFutureLambdaByCustomPool(jsonFile1);
		System.out.println("Total Elapsed Time in customerPool: " + timeHelper.timeElapsed() + " ms");
		System.out.println("Total Elapsed Time in defaultPool: " + defaultTimer + " ms");

		System.out.println("Print 1000 employee by using supplyAsync with default ForkJoinPool.commonPool() AGAINST CutomerPool");
		timeHelper.start();
		File jsonFile2 = employeeService.readJsonFile();
		employeeService.fetchEmployeeListBySupplyAsync(jsonFile2);
		long defaultTimer1 =  timeHelper.timeElapsed();

		timeHelper.start();
		File jsonFile3 = employeeService.readJsonFile();
		employeeService.fetchEmployeeListBySupplyAsyncWithCustomPool(jsonFile3);
		System.out.println("Total Elapsed Time in customerPool: " + timeHelper.timeElapsed() + " ms");
		System.out.println("Total Elapsed Time in defaultPool: " + defaultTimer1 + " ms");
	}

	/**
	 * If we want to run some background task asynchronously and do not want to return anything from that task, then
	 * use CompletableFuture.runAsync() method.  It takes a Runnable object and returns CompletableFuture<Void>.
	 *  1. CompletableFuture.runAsync(Runnable) - Returns a new CompletableFuture that is asynchronously completed by a task
	 *      running in the ForkJoinPool.commonPool() after it runs the given action.
	 *  2. CompletableFuture.runAsync(Runnable, Executor)
	 *
	 * If we want to run some background task asynchronously and want to return anything from that task, we
	 * should use CompletableFuture.supplyAsync() method.  It takes a Supplier<T> and return CompletableFuture<T>
	 *     where T is the type of the value obtained by calling the given supplier.
	 */

}
