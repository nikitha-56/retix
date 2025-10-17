// package com.example.retix.Configuration;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeHttpRequests(authorize -> 
//                 authorize
//                     .requestMatchers("/public/**").permitAll()
//                     .anyRequest().authenticated()
//             )
//             .formLogin(formLogin -> 
//                 formLogin
//                     .loginPage("/login")
//                     .permitAll()
//             )
//             .logout(logout -> 
//                 logout
//                     .permitAll()
//             );

//         return http.build();
//     }
// }
