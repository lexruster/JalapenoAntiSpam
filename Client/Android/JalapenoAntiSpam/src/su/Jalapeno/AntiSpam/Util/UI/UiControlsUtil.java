package su.Jalapeno.AntiSpam.Util.UI;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

/**
 * Created by Alexander on 23.12.13.
 */
public class UiControlsUtil {
	private Activity _activity;

	public UiControlsUtil(Activity activity) {
		_activity = activity;
	}

	public void SetTapForButton(Button button, int id, View.OnClickListener onClickListener) {
		button = (Button) _activity.findViewById(id);
		View.OnClickListener tapListener = onClickListener;
		button.setOnClickListener(tapListener);
	}

	public void SetTapForButton(int id, View.OnClickListener onClickListener) {
		Button button = (Button) _activity.findViewById(id);
		View.OnClickListener tapListener = onClickListener;
		button.setOnClickListener(tapListener);
	}

	public void NavigateTo(Class<?> cls) {
		Intent intentFeed = new Intent(_activity, cls);
		_activity.startActivity(intentFeed);
	}

	public void NavigateAndClearHistory(Class<?> cls) {
		Intent intent = new Intent(_activity, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		_activity.startActivity(intent);
		_activity.finish();
	}
}
