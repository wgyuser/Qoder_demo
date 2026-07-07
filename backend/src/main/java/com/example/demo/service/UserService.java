package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户服务层 - 使用内存存储作为示例
 */
@Service
public class UserService {

    private final Map<Long, User> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService(PasswordEncoder passwordEncoder) {
        // 初始化一些示例数据（密码: 123456）
        String encodedPwd = passwordEncoder.encode("123456");
        User user1 = new User(null, "张三", "zhangsan@example.com", "13800138001");
        user1.setPassword(encodedPwd);
        user1.setRole("USER");
        create(user1);

        User user2 = new User(null, "李四", "lisi@example.com", "13800138002");
        user2.setPassword(encodedPwd);
        user2.setRole("USER");
        create(user2);

        User user3 = new User(null, "王五", "wangwu@example.com", "13800138003");
        user3.setPassword(encodedPwd);
        user3.setRole("USER");
        create(user3);
    }

    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }

    public Optional<User> findByUsername(String username) {
        return userStore.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public User create(User user) {
        Long id = idGenerator.getAndIncrement();
        user.setId(id);
        userStore.put(id, user);
        return user;
    }

    public Optional<User> update(Long id, User user) {
        if (!userStore.containsKey(id)) {
            return Optional.empty();
        }
        user.setId(id);
        userStore.put(id, user);
        return Optional.of(user);
    }

    public boolean delete(Long id) {
        return userStore.remove(id) != null;
    }
}
