package su.Jalapeno.AntiSpam;

import javax.annotation.Nonnull;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.SystemService.NotifyType;
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
		ReportField.ANDROID_VERSION, ReportField.APP_VERSION_NAME,
		ReportField.APP_VERSION_CODE, ReportField.APPLICATION_LOG,
		ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA,
		ReportField.STACK_TRACE, ReportField.LOGCAT }, logcatArguments = {
		"-t", "200", "-v", "long", "test:I", "*:D", "*:S" })
public class MyApplication extends Application {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "MyApplication";
	@Nonnull
	static final String MAIL = "lexruster@gmail.com";

	public String getPublicKey() {
		CryptoService cr = new CryptoService();
		String base64EncodedPublicKey = cr
				.Decrypt(BillingConstants.ENCYPTED_LICENCE_KEY);

		return base64EncodedPublicKey;
	}

	@Nonnull
	private static MyApplication instance;

	public MyApplication() {
		instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.Debug(LOG_TAG, "onCreate ");
		ACRA.init(this);
		startService(new Intent(this, AppService.class).putExtra(
				NotifyType.ExtraConstant, NotifyType.RefreshSmsNotify));
		RepositoryFactory.initRepository(getApplicationContext());
	}

	@Nonnull
	public static MyApplication get() {
		return instance;
	}

	@Override
	public void onTerminate() {
		Logger.Debug(LOG_TAG, "onTerminate ");
		RepositoryFactory.releaseRepository();
		super.onTerminate();
	}
}