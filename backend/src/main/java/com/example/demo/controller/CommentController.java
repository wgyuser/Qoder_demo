package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.ResultCode;
import com.example.demo.model.Comment;
import com.example.demo.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品评论 REST API 控制器
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 获取所有评论
     * GET /api/comments
     */
    @GetMapping
    public Result<List<Comment>> getAllComments() {
        return Result.success(commentService.findAll());
    }

    /**
     * 根据商品 ID 获取评论列表
     * GET /api/comments/product/{productId}
     */
    @GetMapping("/product/{productId}")
    public Result<List<Comment>> getCommentsByProductId(@PathVariable Long productId) {
        return Result.success(commentService.findByProductId(productId));
    }

    /**
     * 根据 ID 获取评论
     * GET /api/comments/{id}
     */
    @GetMapping("/{id}")
    public Result<Comment> getCommentById(@PathVariable Long id) {
        return commentService.findById(id)
                .map(Result::success)
                .orElse(Result.error(ResultCode.NOT_FOUND, "评论不存在: id=" + id));
    }

    /**
     * 创建新评论
     * POST /api/comments
     */
    @PostMapping
    public Result<Comment> createComment(@Valid @RequestBody Comment comment) {
        return Result.created(commentService.create(comment));
    }

    /**
     * 更新评论
     * PUT /api/comments/{id}
     */
    @PutMapping("/{id}")
    public Result<Comment> updateComment(@PathVariable Long id, @Valid @RequestBody Comment comment) {
        return commentService.update(id, comment)
                .map(Result::success)
                .orElse(Result.error(ResultCode.NOT_FOUND, "评论不存在: id=" + id));
    }

    /**
     * 删除评论
     * DELETE /api/comments/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        if (commentService.delete(id)) {
            return Result.success();
        }
        return Result.error(ResultCode.NOT_FOUND, "评论不存在: id=" + id);
    }
}
