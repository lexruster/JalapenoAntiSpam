package su.Jalapeno.AntiSpam.Services.WebService;

public class WebConstants {
	public final static String PUBLIC_KEY_URL = "/AntispamService.svc/PublicKey";
	public final static String IS_SPAMMER_URL = "/AntispamService.svc/IsSpammer";
	public final static String REGISTER_CLIENT_URL = "/AntispamService.svc/RegisterClient";
	public final static String COMPLAIN_URL = "/AntispamService.svc/Complain";

	public final static String DOMAIN_URL_PRINARY = "http://jalapenoapi.jalapeno.su";
	public final static String DOMAIN_URL_SECONDARY = "http://10.0.2.2:33500";
	
	//timeout in milliseconds 
	public final static int CONNECTION_TIMEOUT = 4000;
	public static final int SOCKET_TIMEOUT = 4500;
}
