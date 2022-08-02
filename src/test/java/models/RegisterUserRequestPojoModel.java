package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterUserRequestPojoModel {

	private String email;
	private String password;
}
