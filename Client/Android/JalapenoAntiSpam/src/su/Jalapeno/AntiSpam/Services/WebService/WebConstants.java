package su.Jalapeno.AntiSpam.Services.WebService;

public class WebConstants {
	public final static String IS_SPAMMER_URL = "/AntispamService.svc/IsSpammer";
	public final static String REGISTER_CLIENT_URL = "/AntispamService.svc/RegisterClient";
	public final static String NOTIFY_ABOUT_PAYMENT_URL = "/AntispamService.svc/NotifyAboutPayment";
	public final static String REGISTER_TEST_CLIENT_URL = "/AntispamService.svc/RegisterTestClient";
	public final static String COMPLAIN_URL = "/AntispamService.svc/Complain";

	public final static String DOMAIN_URL_PRIMARY = "https://jalapenoapi.jalapeno.su";
	public final static String DOMAIN_URL_SECONDARY = "http://10.0.2.2/jalapenoAdmin";
	
	//timeout in milliseconds 
	public final static int CONNECTION_TIMEOUT = 30000;
	public static final int SOCKET_TIMEOUT = 35000;
}
