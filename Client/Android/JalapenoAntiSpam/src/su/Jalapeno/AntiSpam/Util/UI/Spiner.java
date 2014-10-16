package su.Jalapeno.AntiSpam.Util.UI;

import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.app.ProgressDialog;
import android.content.Context;

public class Spiner {
	private ProgressDialog _progressDialog;
	private Context _context;
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "Spiner";

	public Spiner(Context context) {
		_context = context;
		CreateSpiner();
		Logger.Debug(LOG_TAG, "Spiner construct");
	}

	public void Hide() {
		try {
			if ((_progressDialog != null) && _progressDialog.isShowing()) {
				_progressDialog.dismiss();
			}
		} catch (final IllegalArgumentException ex) {
			Logger.Error(LOG_TAG, "Hide spinner error", ex);
		} catch (final Exception ex) {
			Logger.Error(LOG_TAG, "Hide spinner error", ex);
		} finally {
			_progressDialog = null;
		}
		
		Logger.Debug(LOG_TAG, "Spiner hide");
	}

	public void Show() {
		String message = _context.getResources()
				.getString(R.string.WaitMessage);
		if (_progressDialog == null) {
			CreateSpiner();
		}
		_progressDialog.setMessage(message);
		_progressDialog.show();
		Logger.Debug(LOG_TAG, "Spiner show");
	}

	private void CreateSpiner() {
		_progressDialog = new ProgressDialog(_context);
		_progressDialog.setCanceledOnTouchOutside(false);
		Logger.Debug(LOG_TAG, "Spiner create");
	}
}
