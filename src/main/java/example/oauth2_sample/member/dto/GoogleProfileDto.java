package example.oauth2_sample.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleProfileDto {
//  id 값은 주된 값을 의미하는 subject를 줄인 sub로 들어옴.
  private String sub;
  private String email;
}
