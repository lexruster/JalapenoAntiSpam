package su.Jalapeno.AntiSpam.Services.WebService.Dto.Request;

import java.util.UUID;

public class NotifyAboutPaymentRequest extends BaseClientRequest {
	public NotifyAboutPaymentRequest(String info, UUID clientId) {
		super(clientId);
		PaymentInfo = info;
	}

	public String PaymentInfo;
}