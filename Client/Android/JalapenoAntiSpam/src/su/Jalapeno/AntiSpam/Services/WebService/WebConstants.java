package su.Jalapeno.AntiSpam.Services.WebService;

public class WebConstants {
	public final static String IS_SPAMMER_URL = "IsSpammer";
	public final static String REGISTER_CLIENT_URL = "RegisterClient";
	public final static String NOTIFY_ABOUT_PAYMENT_URL = "NotifyAboutPayment";
	public final static String REGISTER_TEST_CLIENT_URL = "RegisterTestClient";
	public final static String COMPLAIN_URL = "Complain";

	public final static String DOMAIN_URL_PRIMARY = "https://jalapenoapi.jalapeno.su/AntispamService.svc/";
	public final static String DOMAIN_URL_SECONDARY = "https://jalapenoapi.tekhna.ru/AntispamService.svc/";
	
	//timeout in milliseconds 
	public final static int CONNECTION_TIMEOUT = 30000;
	public static final int SOCKET_TIMEOUT = 35000;
}
