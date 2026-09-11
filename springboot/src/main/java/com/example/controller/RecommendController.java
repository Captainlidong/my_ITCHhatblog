package com.example.controller;

import com.example.entity.Blog;
import com.example.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Resource
    private RecommendationService recommendationService;

    /**
     * 用户协同过滤推荐
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Blog>> recommendForUser(@PathVariable Integer userId) {
        List<Blog> recommendations = recommendationService.userBasedCF(userId);
        return ResponseEntity.ok(recommendations);
    }
}
