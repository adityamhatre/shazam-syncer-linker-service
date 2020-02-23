package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SongDTO {
	private String shazamSongId;
	private String songName;
	private boolean linkFound;
	private String link;
	private UserDTO shazamedBy;
	private long timestamp;


}
