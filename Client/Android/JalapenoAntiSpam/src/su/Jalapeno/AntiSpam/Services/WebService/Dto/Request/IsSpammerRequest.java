package su.Jalapeno.AntiSpam.Services.WebService.Dto.Request;

import java.util.UUID;

public class IsSpammerRequest extends BaseClientRequest {

	public IsSpammerRequest(UUID clientId) {
		super(clientId);
	}

	public String SenderId;
	public String Hash;
}