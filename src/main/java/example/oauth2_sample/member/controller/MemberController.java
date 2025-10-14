package example.oauth2_sample.member.controller;

import example.oauth2_sample.common.auth.JwtTokenProvider;
import example.oauth2_sample.member.domain.Member;
import example.oauth2_sample.member.dto.MemberCreateDto;
import example.oauth2_sample.member.dto.MemberLoginDto;
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
}
