package example.oauth2_sample.member.controller;

import example.oauth2_sample.common.auth.JwtTokenProvider;
import example.oauth2_sample.member.domain.Member;
import example.oauth2_sample.member.domain.SocialType;
import example.oauth2_sample.member.dto.*;
import example.oauth2_sample.member.service.GoogleService;
import example.oauth2_sample.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
  private final MemberService memberService;
  private final JwtTokenProvider jwtTokenProvider;
  private final GoogleService googleService;

  @PostMapping("/signup")
  public ResponseEntity<?> memberCreate(@RequestBody MemberCreateDto memberCreateDto) {
    Member member = memberService.create(memberCreateDto);
    return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto memberLoginDto) {
    // email, password 일치 검증
    Member member = memberService.login(memberLoginDto);

    // 검증 완료 후 jwt access token 생성
    String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());

    Map<String, Object> loginInfo = new HashMap<>();
    loginInfo.put("id", member.getId());
    loginInfo.put("token", jwtToken);
    return new ResponseEntity<>(loginInfo, HttpStatus.OK);
  }

  @PostMapping("/google/doLogin")
  public ResponseEntity<?> googleLogin(@RequestBody RedirectDto redirectDto) {
//    accesstoken 발급
    AccessTokenDto accessTokenDto = googleService.getAcessToken(redirectDto.getCode());

//    사용자정보 획득
    GoogleProfileDto googleProfileDto = googleService.getGoogleProfile(accessTokenDto.getAccessToken());

//    회원가입 되어 있지 않을 경우, 회원 가입
    Member originalMember = memberService.getMemberBySocialId(googleProfileDto.getSub()); // socialid = oauthid
    if(originalMember == null) {
      // 멤버 객체가 필요하기 때문에, 회원가입 시켜주면서 토큰을 함께 발급시켜준다.
      originalMember = memberService.createOauth(googleProfileDto.getSub(), googleProfileDto.getEmail(), SocialType.GOOGLE);
    }

//    회원 가입이 돼어있는 회원이면 토큰 발급
    String jwtToken = jwtTokenProvider.createToken((originalMember.getEmail()), originalMember.getRole().toString());
    Map<String, Object> loginInfo = new HashMap<>();
    loginInfo.put("id", originalMember.getId());
    loginInfo.put("token", jwtToken);
    return new ResponseEntity<>(loginInfo, HttpStatus.OK);
  }
}
