package com.dem_ams.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    @GetMapping
    public ResponseEntity<String> getPublic() {
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping("/fibonacci/{number}")
    public ResponseEntity<String> getFibonacci(
            @PathVariable String number) {
        int input = Integer.parseInt(number);
        int[] arr = new int[input];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = fibonacci(i);
        }
        return ResponseEntity.ok(Arrays.toString(arr));
    }

    @GetMapping("/factorial/{number}")
    public ResponseEntity<String> getFactorial(
            @PathVariable String number) {
        int input = Integer.parseInt(number);
        int output = factorial(input);
        return ResponseEntity.ok(String.valueOf(output));
    }

    // input the number of elements of the fibonacci sequence
    private int fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    private int factorial(int n) {
        if (n == 0) return 1;
        return n * factorial(n - 1);
    }
}
