package example.oauth2_sample.member.service;

import example.oauth2_sample.member.domain.Member;
import example.oauth2_sample.member.domain.SocialType;
import example.oauth2_sample.member.dto.MemberCreateDto;
import example.oauth2_sample.member.dto.MemberLoginDto;
import example.oauth2_sample.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member create(MemberCreateDto dto) {
    Member member = Member.builder()
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .build();
    memberRepository.save(member);
    return member;
  }

  public Member login(MemberLoginDto memberLoginDto) {
    Optional<Member> optMember = memberRepository.findByEmail(memberLoginDto.getEmail());
    if(!optMember.isPresent()) {
      throw new IllegalArgumentException("email not exists");
    }

    Member member = optMember.get();
    if(!passwordEncoder.matches(member.getPassword(), memberLoginDto.getPassword())) {
      throw new IllegalArgumentException("password not match");
    }
    return member;
  }

  public Member getMemberBySocialId(String socialId) {
    // controller 레이어에서 null 체킹을 하기 때문에 서비스 레이어에서 따로 검증하기 않음.
    Member member = memberRepository.findBySocialId(socialId).orElse(null);
    return member;
  }

  public Member createOauth(String sociald, String email, SocialType socialType) {
    Member member = Member.builder()
            .email(email)
            .socialType(socialType)
            .socialId(sociald)
            .build();
    memberRepository.save(member);
    return member;
  }
}
