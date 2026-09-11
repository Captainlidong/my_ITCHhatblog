package com.example.controller;

import com.example.common.Result;
import com.example.entity.Collect;
import com.example.service.CollectService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 功能：收藏功能 接口开发
 * 作者：captain_dong
 * 日期：2024/1/26 20:50
 */
@RestController
@RequestMapping("/collect")
public class CollectController {
    @Resource
    CollectService collectService;

    /**
     * 收藏与取消收藏
     * @param collect
     * @return
     */
    @PostMapping("/set")
    public Result set(@RequestBody Collect collect){
        collectService.set(collect);
        return Result.success();
    }
}