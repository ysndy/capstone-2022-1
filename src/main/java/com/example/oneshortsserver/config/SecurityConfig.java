package com.example.oneshortsserver.config;

import com.example.oneshortsserver.user.UserRole;
import com.example.oneshortsserver.user.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserSecurityService userSecurityService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/").permitAll()
                    .antMatchers("/fileupload").hasRole(String.valueOf(UserRole.USER))
                    .antMatchers("/api/video/list").hasRole(String.valueOf(UserRole.USER))
                        .antMatchers("/api/regist").hasRole(String.valueOf(UserRole.USER))
                .antMatchers("/api/video/upload").hasRole(String.valueOf(UserRole.USER));

        http.formLogin().loginPage("/api/login");
        http.csrf().disable();
        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/api/logout")).logoutSuccessUrl("/").invalidateHttpSession(true);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
