package su.Jalapeno.AntiSpam;

import static java.util.Arrays.asList;
import static org.solovyev.android.checkout.ProductTypes.IN_APP;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Cache;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Products;

import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.BillingConstants;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.app.Application;
import android.content.Intent;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */
@ReportsCrashes(formKey = "", mailTo = MyApplication.MAIL, mode = ReportingInteractionMode.SILENT, customReportContent = {
		ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }, logcatArguments = {
		"-t", "200", "-v", "long", "test:I", "*:D", "*:S" })
public class MyApplication extends Application {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "MyApplication";
	@Nonnull
	static final String MAIL = "lexruster@gmail.com";

	@Nonnull
	private static final Products products = Products.create().add(IN_APP, asList(BillingConstants.ANTISPAM_ACCESS));

	@Nonnull
	private final Billing billing = new Billing(this, new Billing.Configuration() {
		@Nonnull
		@Override
		public String getPublicKey() {

			CryptoService cr = new CryptoService();
			String base64EncodedPublicKey = cr.Decrypt(BillingConstants.ENCYPTED_LICENCE_KEY);

			return base64EncodedPublicKey;
		}

		@Nullable
		@Override
		public Cache getCache() {
			return Billing.newCache();
		}
	});

	@Nonnull
	private final Checkout checkout = Checkout.forApplication(billing, products);

	@Nonnull
	private static MyApplication instance;

	public MyApplication() {
		instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.Debug(LOG_TAG, "onCreate ");
		billing.connect();
		ACRA.init(this);
		startService(new Intent(this, AppService.class));
		RepositoryFactory.initRepository(getApplicationContext());
	}

	@Nonnull
	public static MyApplication get() {
		return instance;
	}

	@Nonnull
	public Checkout getCheckout() {
		return checkout;
	}

	@Override
	public void onTerminate() {
		Logger.Debug(LOG_TAG, "onTerminate ");
		RepositoryFactory.releaseRepository();
		super.onTerminate();
	}
}