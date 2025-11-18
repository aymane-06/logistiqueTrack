package com.logitrack.logitrack.controllers;


import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    @GetMapping("/me")
    public Object getCurrentUser(HttpSession session, @RequestBody Map<String, String> requestBody) {
        String ssID= requestBody.get("sessionId");
        return session.getAttribute(ssID);
    }

}
