package com.campus.donation.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus修复配置
 * 解决Spring Boot 3.x与MyBatis-Plus的factoryBeanObjectType兼容性问题
 */
@Configuration
public class MybatisPlusFixConfig {

    @Bean
    public MybatisPlusPropertiesCustomizer mybatisPlusPropertiesCustomizer() {
        return properties -> {
            // 设置SqlSessionFactory的类型，避免factoryBeanObjectType错误
            properties.getGlobalConfig().setBanner(false);
        };
    }
}
