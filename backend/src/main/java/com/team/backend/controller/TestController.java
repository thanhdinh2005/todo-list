package com.team.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
  @GetMapping
  public String testController() {
    log.info("App is running");

    try {
      // Mock Error
      int result = 10 / 0;
    } catch (Exception e) {
      log.error("Error: ", e);
    }

    return "Hello World!";
  }
}
