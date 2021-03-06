package su.Jalapeno.AntiSpam.Services.WebService.Commands;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.WebClient;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.BaseRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.BaseResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.PublicKeyResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.WebErrorEnum;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.PublicKeyInfo;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

public abstract class BaseCommand<TReq extends BaseRequest, TResp extends BaseResponse> {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "BaseCommand";
	public TReq Request;
	public TResp Response;
	protected JalapenoHttpService _httpService;
	protected SettingsService _settingsService;
	protected String _domain;
	private Gson _gson;

	private Class<TResp> _respClazz;
	private EncoderService _encoderService;
	private Config _config;

	@Inject
	public BaseCommand(JalapenoHttpService httpService, SettingsService settingsService, EncoderService encoderService,
			Class<TResp> respClazz) {
		_httpService = httpService;
		_settingsService = settingsService;
		_encoderService = encoderService;
		_respClazz = respClazz;
		Init();
	}

	private void Init() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd.MM.yy hh:mm:ss");
		_gson = gsonBuilder.serializeNulls().create();
		_config = _settingsService.LoadSettings();
		_domain = _config.GetDomain();
	}

	protected abstract String GetAction();

	protected abstract TResp OnServiceNotAvailable();

	private void Setup(TReq request) {
		Request = request;

		try {
			Response = _respClazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TResp Execute(TReq request) {
		Setup(request);

		if (!_httpService.ServiceIsAvailable()) {
			Logger.Debug(LOG_TAG, "ServiceIs not Available");
			Response = OnServiceNotAvailable();
			return Response;
		}

		String json = _gson.toJson(Request);
		Logger.Debug(LOG_TAG, _respClazz + ": " + json);

		ProceedRequest(json);

		if (Response.ErrorMessage == WebErrorEnum.NoConnection) {
			Logger.Debug(LOG_TAG, "NoConnection 1");
			ChangeDomain();
			ProceedRequest(json);
		}

		if (Response.ErrorMessage == WebErrorEnum.NoConnection) {
			Logger.Debug(LOG_TAG, "NoConnection 2 OnServiceNotAvailable");
			Response = OnServiceNotAvailable();
			return Response;
		}

		if (NeedResend(Response)) {
			ProceedRequest(json);
		}

		return Response;
	}

	private void ProceedRequest(String json) {
		Logger.Debug(LOG_TAG, "ProceedRequest");
		String requestString = LoadResponse(json);
		ParseResponse(requestString);
	}

	protected String LoadResponse(String json) {
		String postData = PrepareJsonRequest(json);
		String requestString = WebClient.Post(GetUrl(), postData);
		return requestString;
	}

	private void ParseResponse(String requestString) {
		if (TextUtils.isEmpty(requestString)) {
			FillNoConnection(Response);
		} else {
			Response = _gson.fromJson(requestString, _respClazz);
			Logger.Debug(LOG_TAG, "ParseResponse response Success: " + Response.WasSuccessful + " error: " + Response.ErrorMessage);
		}
	}

	protected String GetUrl() {
		return _domain + GetAction();
	}

	private void ChangeDomain() {
		Logger.Debug(LOG_TAG, "ChangeDomain");
		_config.DomainUrlPrimary = !_config.DomainUrlPrimary;
		_settingsService.SaveSettings(_config);
		_config = _settingsService.LoadSettings();
		_domain = _config.GetDomain();
	}

	private boolean NeedResend(BaseResponse response) {
		if (response.WasSuccessful) {
			return false;
		}
		Logger.Debug(LOG_TAG, "ValidateAndNeedResend " + response.ErrorMessage);

		if (response.ErrorMessage == WebErrorEnum.InvalidToken || response.ErrorMessage == WebErrorEnum.TooManyComplaintsFromUser) {
			return false;
		}

		if (response.ErrorMessage == WebErrorEnum.InvalidPublicKey) {
			PublicKeyResponse pbk = GetPublicKey();
			if (pbk.WasSuccessful) {
				PublicKeyInfo publicKeyInfo = CryptoService.GetPublicKeyInfo(pbk.PublicKey);
				_settingsService.UpdatePublicKey(publicKeyInfo);
				Logger.Debug(LOG_TAG, "UpdatePublicKey success " + response.ErrorMessage);
				return true;
			}

			return false;
		}

		if (response.ErrorMessage == WebErrorEnum.InvalidToken || response.ErrorMessage == WebErrorEnum.UserBanned
				|| response.ErrorMessage == WebErrorEnum.NotAuthorizedRequest) {
			Logger.Debug(LOG_TAG, "Disable registration by error " + response.ErrorMessage);

			_config = _settingsService.LoadSettings();
			_config.ClientRegistered = false;
			_config.Enabled = false;
			_settingsService.SaveSettings(_config);

			return false;
		}

		return false;
	}

	private PublicKeyResponse GetPublicKey() {
		PublicKeyResponse response = null;
		if (_httpService.ServiceIsAvailable()) {
			response = _httpService.GetPublicKey(_domain);
		}

		return response;
	}

	protected void FillNoConnection(BaseResponse response) {
		response.WasSuccessful = false;
		response.ErrorMessage = WebErrorEnum.NoConnection;
	}

	private String PrepareJsonRequest(String request) {
		String postData = _encoderService.Encode(request);
		String jsonPostData = _gson.toJson(postData);

		return jsonPostData;
	}
}
