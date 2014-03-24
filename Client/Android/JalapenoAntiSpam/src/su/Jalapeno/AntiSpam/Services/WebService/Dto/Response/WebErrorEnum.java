package su.Jalapeno.AntiSpam.Services.WebService.Dto.Response;

public enum WebErrorEnum {
	InvalidRequest,
	InvalidToken,
	InvalidPublicKey,
	UserBanned,
	NotAuthorizedRequest,
	TooManyComplaintsFromUser,
	NoConnection
}
