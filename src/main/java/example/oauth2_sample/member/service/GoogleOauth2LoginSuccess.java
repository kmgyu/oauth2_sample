package example.oauth2_sample.member.service;

import example.oauth2_sample.common.auth.JwtTokenProvider;
import example.oauth2_sample.member.domain.Member;
import example.oauth2_sample.member.domain.SocialType;
import example.oauth2_sample.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GoogleOauth2LoginSuccess extends SimpleUrlAuthenticationSuccessHandler {
  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;


  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

    // oauth profile 추출
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String openId = oAuth2User.getAttribute("sub");
    String email = oAuth2User.getAttribute("email");

    // 회원가입 여부 확인
    Member member = memberRepository.findBySocialId(openId).orElse(null);
    if (member == null) {
      member = Member.builder()
              .socialId(openId)
              .email(email)
              .socialType(SocialType.GOOGLE)
              .build();
      memberRepository.save(member);
    }

    // JWT 토큰 생성
    String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());

    // 클라이언트 리디렉션 방식으로 토큰 전달
//    response.sendRedirect("http://localhost:3000?token="+jwtToken);

    // 쿠키 전달 방식
    Cookie jwtCookie = new Cookie("token", jwtToken);
    jwtCookie.setPath("/");
    response.addCookie(jwtCookie);
    response.sendRedirect("http://localhost:3000");

  }
}
