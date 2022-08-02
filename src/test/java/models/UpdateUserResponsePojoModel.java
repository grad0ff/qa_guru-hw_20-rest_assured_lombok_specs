package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class UpdateUserResponsePojoModel {

	private String name;
	private String job;
	private String updatedAt;
}
