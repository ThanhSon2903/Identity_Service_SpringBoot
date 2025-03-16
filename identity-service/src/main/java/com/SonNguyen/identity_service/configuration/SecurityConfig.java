package com.SonNguyen.identity_service.configuration;


import com.SonNguyen.identity_service.enums.Role;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private String PUBLIC_ENDPOINT[] = {"/users","/auth/token","/auth/introspect"};
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;


    @Bean
    //Cấu hình này cho phép truy cập ko giới hạn tới các endpoint, sử dụng jwt để xác thực và tắt bảo vê csrf
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //Cấu hình cho phép tất cả yêu cầu POST tới các ENDPOINT được truy cập mà không cần xác thực
        httpSecurity.authorizeHttpRequests(request -> request
                .requestMatchers(HttpMethod.POST,PUBLIC_ENDPOINT).permitAll()
                .anyRequest()
                .authenticated());

        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(jwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        httpSecurity.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    System.out.println("🚨 Lỗi authentication: " + authException.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    System.out.println("🚫 Lỗi phân quyền: " + accessDeniedException.getMessage());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                })
        );
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(Customizer.withDefaults());

        return httpSecurity.build();
    }
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        return new JwtAuthenticationConverter() {
            {
                setJwtGrantedAuthoritiesConverter(jwt -> {
                    Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
                    System.out.println("✅ Authorities sau khi convert: " + authorities);
                    return authorities != null ? new ArrayList<>(authorities) : List.of();
                });
            }
        };
    }


    @Bean
        //Cấu hình việc xác thực và giải mã JWT
    JwtDecoder jwtDecoder(){
        //Tạo đối tượng secretKeySpec từ 1 chuỗi SIGNER_KEY và được chuyển thành 1 mảng BYTE.
        //SIGNER_KEY dùng thuật toan HS512
        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(),"HS512");

        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
        //BeanInstantiation: Spring sẽ khởi tạo các đối tượng Bean giống như khởi tạo các đối tượng
        // java thông thường và đưa vào applicationContext
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}