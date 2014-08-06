package su.Jalapeno.AntiSpam.Services.WebService.Dto.Request;

import java.util.UUID;


public class ComplainRequest extends BaseClientRequest {
	public ComplainRequest(UUID clientId) {
		super(clientId);
	}
	public String SenderId;
	public String Hash;
}