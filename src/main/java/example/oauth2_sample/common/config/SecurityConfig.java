package example.oauth2_sample.common.config;

import example.oauth2_sample.common.auth.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  // 강의에서는 생성자 주입
  private final JwtTokenFilter jwtTokenFilter;

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
            .authorizeHttpRequests(a->a.requestMatchers("/member/create",
                            "/member/login",
                            "/member/google/doLogin",
                            "/member/kakao/doLogin").permitAll()
                    .anyRequest().authenticated())
            // UsernamePasswordAuthenticationFilter는 이 클래스에서 폼 로그인 인증을 처리
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class) // 특정 url 패턴 제외 검증하는데, 여기서 검증한다는 것. UsernamePasswordAuthenticationFilter 동작 전에 이것을 사용하겠다.
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
