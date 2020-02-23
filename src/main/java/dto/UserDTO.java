package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
	private String objectId;
	private String inid;
	private String codever;
	private String username;
	private String deviceFcmToken;
	private Integer frequency;
	private boolean bootStrapped;
}
