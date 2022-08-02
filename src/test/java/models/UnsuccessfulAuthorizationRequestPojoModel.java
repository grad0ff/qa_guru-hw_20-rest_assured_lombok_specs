package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnsuccessfulAuthorizationRequestPojoModel {

	private String email;
}
