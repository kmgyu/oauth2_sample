package example.oauth2_sample.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 없는 필드 자동 무시
public class AccessTokenDto {
  private String accessToken;
  private String expires_in;
  private String scope;
  private String id_token;
}
