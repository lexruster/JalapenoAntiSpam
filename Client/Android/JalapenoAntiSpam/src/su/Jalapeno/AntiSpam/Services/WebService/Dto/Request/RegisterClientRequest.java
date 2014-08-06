package su.Jalapeno.AntiSpam.Services.WebService.Dto.Request;

import java.util.UUID;

public class RegisterClientRequest extends BaseClientRequest {
	public RegisterClientRequest(UUID clientId) {
		super(clientId);
	}

	public String Token;
}