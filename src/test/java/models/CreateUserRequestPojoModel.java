package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequestPojoModel {

	private String name;
	private String job;
}
