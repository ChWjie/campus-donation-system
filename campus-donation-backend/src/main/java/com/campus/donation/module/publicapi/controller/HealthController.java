package com.campus.donation.module.publicapi.controller;

import com.campus.donation.common.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 公共健康检查接口
 */
@RestController
@RequestMapping("/public")
public class HealthController {

    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "campus-donation-backend");
        result.put("timestamp", LocalDateTime.now());
        return R.ok(result);
    }
}
