package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserUpdateRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.model.user.Role;
import org.example.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Users management",
        description = "Endpoints for managing authentication and user registration")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;

    @PutMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user role",
            description = "Update set of user roles by user id")
    public UserResponseDto updateUserRolesById(@PathVariable Long id,
                                               Set<Role> roles) {
        return userService.updateUserRolesById(id, roles);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile info",
            description = "Get profile's info of the authorized user")
    public UserResponseDto getMyInfo() {
        return userService.getMyInfo();
    }

    @PatchMapping("/me")
    @Operation(summary = "Update profile info",
            description = "Update my profile info")
    public UserResponseDto updateMyInfo(UserUpdateRequestDto userRequestDto) {
        return userService.updateMyInfo(userRequestDto);
    }
}
