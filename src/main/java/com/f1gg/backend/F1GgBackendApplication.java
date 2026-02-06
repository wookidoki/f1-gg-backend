package com.f1gg.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// 🔥 수정된 부분: scanBasePackages 추가
// "com.f1gg" 패키지 아래에 있는 모든 파일을 다 읽으라고 지시함
@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class},
    scanBasePackages = "com.f1gg" 
)
public class F1GgBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(F1GgBackendApplication.class, args);
    }
}