package su.Jalapeno.AntiSpam.Util.UI;

import su.Jalapeno.AntiSpam.Filter.R;
import android.app.ProgressDialog;
import android.content.Context;

public class Spiner {
	private ProgressDialog _progressDialog;
	private Context _context;

	public Spiner(Context context) {
		_context = context;
		_progressDialog = new ProgressDialog(_context);
	}

	public void Hide() {
		if (_progressDialog.isShowing()) {
			_progressDialog.dismiss();
		}
	}

	public void Show() {
		String message = _context.getResources().getString(
				R.string.WaitMessage);
		_progressDialog.setMessage(message);
		_progressDialog.show();
	}
}
