package su.Jalapeno.AntiSpam.Services.WebService.Dto;

import java.util.UUID;

public class IsSpammerRequest {
	public UUID ClientId;
	public String SenderId;
	public String Hash;
}