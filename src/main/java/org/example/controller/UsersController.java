package org.example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users management",
        description = "Endpoints for managing authentication and user registration")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {
}
