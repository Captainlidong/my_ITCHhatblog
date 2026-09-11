package com.example.service;

import com.example.entity.Blog;
import com.example.entity.UserBehavior;
import com.example.mapper.BlogMapper;
import com.example.mapper.UserBehaviorMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Resource
    private UserBehaviorMapper userBehaviorMapper;

    @Resource
    private BlogMapper blogMapper;

    // 获取所有用户行为数据
    private Map<Integer, Map<Integer, Double>> buildUserItemMatrix() {
        List<UserBehavior> behaviors = userBehaviorMapper.getAllBehaviors();
        Map<Integer, Map<Integer, Double>> matrix = new HashMap<>();

        for (UserBehavior ub : behaviors) {
            matrix.computeIfAbsent(ub.getUserId(), k -> new HashMap<>());
            double score = getBehaviorScore(ub.getBehaviorType());
            matrix.get(ub.getUserId()).put(ub.getBlogId(), score);
        }

        return matrix;
    }

    // 将行为类型转为权重分数
    private double getBehaviorScore(byte type) {
        switch (type) {
            case 1: return 1.0; // 浏览
            case 2: return 2.0; // 点赞
            case 3: return 3.0; // 收藏
            default: return 0.0;
        }
    }

    // 计算用户相似度（余弦）
    public Map<Integer, Double> calculateUserSimilarity(Integer targetUserId) {
        Map<Integer, Map<Integer, Double>> matrix = buildUserItemMatrix();
        Map<Integer, Double> similarities = new HashMap<>();
        Map<Integer, Double> targetVector = matrix.get(targetUserId);

        if (targetVector == null) return similarities;

        for (Integer userId : matrix.keySet()) {
            if (userId.equals(targetUserId)) continue;
            Map<Integer, Double> otherVector = matrix.get(userId);
            double similarity = cosineSimilarity(targetVector, otherVector);
            similarities.put(userId, similarity);
        }

        return similarities.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }

    // 余弦相似度计算
    private double cosineSimilarity(Map<Integer, Double> v1, Map<Integer, Double> v2) {
        Set<Integer> commonKeys = new HashSet<>(v1.keySet());
        commonKeys.retainAll(v2.keySet());

        if (commonKeys.isEmpty()) return 0.0;

        double dotProduct = commonKeys.stream().mapToDouble(k -> v1.get(k) * v2.get(k)).sum();
        double normV1 = Math.sqrt(v1.values().stream().mapToDouble(x -> x * x).sum());
        double normV2 = Math.sqrt(v2.values().stream().mapToDouble(x -> x * x).sum());

        return dotProduct / (normV1 * normV2 + 1e-8);
    }

    // 基于用户协同过滤推荐
    public List<Blog> userBasedCF(Integer userId) {
        Map<Integer, Double> similarUsers = calculateUserSimilarity(userId);
        Set<Integer> recommendedBlogIds = new HashSet<>();

        for (Integer similarUserId : similarUsers.keySet()) {
            List<UserBehavior> behaviors = userBehaviorMapper.getByUserId(similarUserId);
            behaviors.forEach(b -> recommendedBlogIds.add(b.getBlogId()));
        }

        return blogMapper.selectAll(null).stream()
                .filter(b -> !b.getUserId().equals(userId) && recommendedBlogIds.contains(b.getId()))
                .limit(5)
                .collect(Collectors.toList());
    }
}
