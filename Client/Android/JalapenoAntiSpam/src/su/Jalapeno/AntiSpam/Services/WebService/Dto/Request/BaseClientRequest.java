package su.Jalapeno.AntiSpam.Services.WebService.Dto.Request;

import java.util.UUID;

public class BaseClientRequest extends BaseRequest{
	public BaseClientRequest(UUID clientId)
	{
		ClientId=clientId;
	}
	public UUID ClientId;
}
