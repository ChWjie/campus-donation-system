package com.campus.donation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园旧物公益捐赠回收管理系统 - 启动类
 *
 * @author Campus Donation Team
 * @since 2026-02-11
 */
@SpringBootApplication
@MapperScan("com.campus.donation.module.**.mapper")
public class CampusDonationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusDonationApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("校园旧物公益捐赠回收管理系统启动成功!");
        System.out.println("API文档地址: http://localhost:8080/api/swagger-ui.html");
        System.out.println("========================================\n");
    }

}
