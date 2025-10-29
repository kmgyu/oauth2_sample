package example.oauth2_sample.member.service;

import example.oauth2_sample.member.dto.AccessTokenDto;
import example.oauth2_sample.member.dto.GoogleProfileDto;
import example.oauth2_sample.member.dto.KakaoProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class KakaoService {

  @Value("${oauth.kakao.client-id}")
  private String kakaoClientId;

  @Value("${oauth.kakao.redirect-uri}")
  private String kakaoRedirectUri;


  public AccessTokenDto getAcessToken(String code) {
    // 인가코드, clientId, client_secret, redirect_uri, grant_type.
    // 위 값들은 바뀌기 쉽다. 따라서 properties에서 설정해준다.

    // Spring 6부터 RestTemplate Deprecated. 따라서 RestClient 사용이 권장됨.
    RestClient restClient = RestClient.create();

    // MultiValueMap을 통해 form-data 형식 body 간편하게 조립
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", code);
    params.add("client_id", kakaoClientId);
    params.add("redirect_uri", kakaoRedirectUri);
    params.add("grant_type", "authorization_code");


    ResponseEntity<AccessTokenDto> response = restClient.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
            // ?code=XXX%client_id=yyyy&... 방식으로 직접 설정도 가능하지만 multi value map으로 더 간편하게 만들어줄 수 있다.
            .body(params)
//                    retrieve: 응답 body값만을 추출
            .retrieve()
            .toEntity(AccessTokenDto.class);

    // ObjectMapper로 직접 매핑해줄 수도 있지만 여기선 자동 파싱 활용
    log.info("응답 json: {}", response.getBody());
    return response.getBody();
  }

  public KakaoProfileDto getKakaoProfile(String token) {
    RestClient restClient = RestClient.create();

    ResponseEntity<KakaoProfileDto> response = restClient.get()
            .uri("\thttps://kapi.kakao.com/v2/user/me")
            .header("Authorization", "Bearer " + token) // Bearer token이라는 명시 필요
            .retrieve()
            .toEntity(KakaoProfileDto.class);

    log.info("Profile JSON {}", response.getBody());

    return response.getBody();
  }


}
