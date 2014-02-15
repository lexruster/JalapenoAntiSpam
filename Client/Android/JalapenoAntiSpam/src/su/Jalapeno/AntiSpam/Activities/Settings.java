package su.Jalapeno.AntiSpam.Activities;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ToggleButton;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import su.Jalapeno.AntiSpam.Util.UI.UiControlsUtil;

import java.util.ResourceBundle;

import com.google.inject.Inject;
//import com.google.android.gms.*;


//public class Settings extends Activity {
public class Settings extends JalapenoActivity {
	
	  // system service
	  @Inject
	  LocationManager loc;
	  
	  
    //protected UiControlsUtil UiUtils;
    ToggleButton toogleButton;
    SettingsService settingsService;
    private Config config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);

        SetEvent();
        Init();

        // UiUtils = new UiControlsUtil(this);
    }

    private void SetEvent() {
        UiUtils.SetTapForButton(R.id.buttonSpammerList, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewSpamerList();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonDebug, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UiUtils.NavigateTo(Debug.class);
                    }
                });

        toogleButton = (ToggleButton) findViewById(R.id.toggleEnabled);
    }

    @Override
    protected void onPause() {
        super.onPause();
        settingsService.SaveSettings(config);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Init();
    }

    private void Init() {
        Context context = this.getApplicationContext();
        settingsService = new SettingsService(context);
        config = settingsService.LoadSettings();

        toogleButton.setChecked(config.Enabled);
    }

    private void ViewSpamerList() {
        UiUtils.NavigateTo(SpamerList.class);
    }

    public void toggleClick(View view) {
        config.Enabled = toogleButton.isChecked();
        settingsService.SaveSettings(config);
    }
}
