package com.vaccine.controller;

import com.vaccine.dto.ApiResponse;
import com.vaccine.entity.User;
import com.vaccine.repository.DataStore;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private DataStore dataStore;

    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.success(dataStore.getUsers().values().stream()
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        User user = dataStore.getUsers().get(id);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        return ApiResponse.success(user);
    }

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody User user) {
        user.setId(dataStore.generateUserId());
        if (user.getBirthDate() != null) {
            user.setAge(Period.between(user.getBirthDate(), LocalDate.now()).getYears());
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        dataStore.getUsers().put(user.getId(), user);
        return ApiResponse.success(user, "用户创建成功");
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = dataStore.getUsers().get(id);
        if (existingUser == null) {
            return ApiResponse.error("用户不存在");
        }
        if (user.getName() != null) existingUser.setName(user.getName());
        if (user.getPhone() != null) existingUser.setPhone(user.getPhone());
        if (user.getIdCard() != null) existingUser.setIdCard(user.getIdCard());
        if (user.getGender() != null) existingUser.setGender(user.getGender());
        if (user.getBirthDate() != null) {
            existingUser.setBirthDate(user.getBirthDate());
            existingUser.setAge(Period.between(user.getBirthDate(), LocalDate.now()).getYears());
        }
        if (user.getAddress() != null) existingUser.setAddress(user.getAddress());
        if (user.getContraindications() != null) existingUser.setContraindications(user.getContraindications());
        existingUser.setUpdatedAt(LocalDateTime.now());
        return ApiResponse.success(existingUser, "用户更新成功");
    }
}
