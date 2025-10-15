package example.oauth2_sample.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtTokenFilter extends GenericFilter {

  @Value("${jwt.secret}")
  private String secretKey;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    String token = request.getHeader("Authorization");

    try {
      if (token != null) {
//      if(!token.subString(0, 7).equals("Bearer ")) -> startsWith로 바꿀 수 있음.
        if(!token.startsWith("Bearer ")) {
          throw new AuthenticationException("Bearer Token is required");
        }

        // bearer 토큰 검증 및 Claims 추출. Claims는 payload라고 보면 됨.
        String jwtToken = token.substring(7);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        // Authentication 객체 생성 참고용
        // 만드는 룰은 아래처럼 지키면 된다. claims의 subject를 통해 이메일도 꺼낼 수 있고, 토큰, 권한등도 꺼낼 수 있음.
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role"))); // ROLE_이 관례. 이거 없으면 에러나는 부분도 존재
        UserDetails userDetails = new User(claims.getSubject(), "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, jwtToken, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityContextHolder.getContext().getAuthentication().getAuthorities();
      }

      // filter chain으로 복귀
      filterChain.doFilter(servletRequest, servletResponse);
    } catch (Exception e) {
      // 예외 터지면 사용자에게 UNAUTHORIZED 에러 반환
      e.printStackTrace();
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json");
      response.getWriter().write("invalid token");
    }
  }

}
