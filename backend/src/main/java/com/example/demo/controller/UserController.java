package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.ResultCode;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 REST API 控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取所有用户
     * GET /api/users
     */
    @GetMapping
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.findAll());
    }

    /**
     * 根据 ID 获取用户
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(Result::success)
                .orElse(Result.error(ResultCode.NOT_FOUND, "用户不存在: id=" + id));
    }

    /**
     * 创建新用户
     * POST /api/users
     */
    @PostMapping
    public Result<User> createUser(@Valid @RequestBody User user) {
        return Result.created(userService.create(user));
    }

    /**
     * 更新用户
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return userService.update(id, user)
                .map(Result::success)
                .orElse(Result.error(ResultCode.NOT_FOUND, "用户不存在: id=" + id));
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        if (userService.delete(id)) {
            return Result.success();
        }
        return Result.error(ResultCode.NOT_FOUND, "用户不存在: id=" + id);
    }

    /**
     * 健康检查接口
     * GET /api/users/health
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务运行正常");
    }
}
