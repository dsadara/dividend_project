package com.example.dividend.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    // trie가 하나만 존재해야 하고, 코드의 일관성을 유지하기 위해 Bean으로 생성
    @Bean
    public Trie<String, String> trie() {
        return new PatriciaTrie<>();
    }

    // final로 선언한 PasswordEncoder에서 이 Bean을 사용할 수 있음
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
