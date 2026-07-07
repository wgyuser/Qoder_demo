package com.example.demo.service;

import com.example.demo.model.Comment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 商品评论服务层 - 使用内存存储作为示例
 */
@Service
public class CommentService {

    private final Map<Long, Comment> commentStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public CommentService() {
        // 初始化一些示例数据
        Comment comment1 = new Comment(null, 1L, "张三", "这个商品很不错，性价比高！", 5);
        create(comment1);

        Comment comment2 = new Comment(null, 1L, "李四", "质量一般，但价格还可以接受", 3);
        create(comment2);

        Comment comment3 = new Comment(null, 2L, "王五", "非常满意，推荐购买！", 5);
        create(comment3);
    }

    public List<Comment> findAll() {
        return new ArrayList<>(commentStore.values());
    }

    public List<Comment> findByProductId(Long productId) {
        return commentStore.values().stream()
                .filter(comment -> comment.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(commentStore.get(id));
    }

    public Comment create(Comment comment) {
        Long id = idGenerator.getAndIncrement();
        comment.setId(id);
        comment.setCreatedAt(LocalDateTime.now());
        commentStore.put(id, comment);
        return comment;
    }

    public Optional<Comment> update(Long id, Comment comment) {
        if (!commentStore.containsKey(id)) {
            return Optional.empty();
        }
        comment.setId(id);
        Comment existing = commentStore.get(id);
        comment.setCreatedAt(existing.getCreatedAt());
        commentStore.put(id, comment);
        return Optional.of(comment);
    }

    public boolean delete(Long id) {
        return commentStore.remove(id) != null;
    }
}
