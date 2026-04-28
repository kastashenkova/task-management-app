package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.RoleDto;
import org.example.dto.user.UserUpdateRequestDto;
import org.example.dto.user.registration.UserResponseDto;
import org.example.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto updateUserRoleById(@PathVariable Long id,
                                              @RequestBody RoleDto roleDto) {
        return userService.updateUserRoleById(id, roleDto);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile info",
            description = "Get profile's info of the authorized user")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public UserResponseDto getMyInfo() {
        return userService.getMyInfo();
    }

    @PatchMapping("/me")
    @Operation(summary = "Update profile info",
            description = "Update my profile info")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public UserResponseDto updateMyInfo(@RequestBody UserUpdateRequestDto userRequestDto) {
        return userService.updateMyInfo(userRequestDto);
    }
}
