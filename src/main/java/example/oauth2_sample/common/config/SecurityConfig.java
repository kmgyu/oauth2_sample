package example.oauth2_sample.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain myfilter(HttpSecurity http) throws Exception {
    return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // .csrf(csrf -> csrf.disable())
            .csrf(AbstractHttpConfigurer::disable) // 이렇게도 가능함.
            // Basic Authentication은 username과 password를 base64로 인코딩해 인증값으로 활용
            // 토큰 인증은 서명의 시그니처 부분이 암호화되어 기본 인증과 차별화된다.
            .httpBasic(AbstractHttpConfigurer::disable) // Basic Authentication disable
            // 세션 기반 인증 비활성화
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a.requestMatchers("/member/create", "/member/login").permitAll()
                    .anyRequest().authenticated())
            .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
    configuration.setAllowedMethods(Arrays.asList("*")); // all http method allow
    configuration.setAllowedHeaders(Arrays.asList("*")); // all header allow
    configuration.setAllowCredentials(true); // credential(자격증명) allow

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // cors allow in all url pattern
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
