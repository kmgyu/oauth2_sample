package example.oauth2_sample.member.service;

import example.oauth2_sample.member.dto.AccessTokenDto;
import example.oauth2_sample.member.dto.GoogleProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class GoogleService {

  @Value("${oauth.google.client-od}")
  private String googleClientId;
  @Value("${oauth.google.client-secret}")
  private String googleClientSecret;
  @Value("${oauth.google.redirect-uri}")
  private String googleRedirectUri;


  public AccessTokenDto getAcessToken(String code) {
    // 인가코드, clientId, client_secret, redirect_uri, grant_type.
    // 위 값들은 바뀌기 쉽다. 따라서 properties에서 설정해준다.

    // Spring 6부터 RestTemplate Deprecated. 따라서 RestClient 사용이 권장됨.
    RestClient restClient = RestClient.create();

    // MultiValueMap을 통해 form-data 형식 body 간편하게 조립
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", code);
    params.add("client_id", googleClientId);
    params.add("client_secret", googleClientSecret);
    params.add("redirect_uri", googleRedirectUri);
    params.add("grant_type", "authorization_code"); // 강의에서는 여긴 단순 문자열이라 설정은 따로 안해주었다고 함.
    // https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.3
    // 스펙을 찾아보니, "반드시" authorization_code여야 한다고 한다. 타입 자체는 4개가 있는 데 프론트에서 처리하는 것으로 보인다...?

    ResponseEntity<AccessTokenDto> response = restClient.post()
            .uri("https://oauth2.googleapis.com/token")
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

  public GoogleProfileDto getGoogleProfile(String token) {
    RestClient restClient = RestClient.create();

    ResponseEntity<GoogleProfileDto> response = restClient.get()
            .uri("https://openidconnect.googleapis.com/v1/userinfo")
            .header("Authorization", "Bearer " + token) // Bearer token이라는 명시 필요
            .retrieve()
            .toEntity(GoogleProfileDto.class);

    log.info("Profile JSON {}", response.getBody());

    return response.getBody();
  }


}
