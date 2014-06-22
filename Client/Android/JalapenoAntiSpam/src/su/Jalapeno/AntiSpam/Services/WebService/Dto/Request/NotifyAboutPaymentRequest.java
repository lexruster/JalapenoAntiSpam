package su.Jalapeno.AntiSpam.Services.WebService.Dto.Request;

public class NotifyAboutPaymentRequest extends BaseClientRequest {
	public NotifyAboutPaymentRequest(String info) {
		PaymentInfo = info;
	}

	public String PaymentInfo;
}