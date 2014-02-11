package su.Jalapeno.AntiSpam.Activities;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import com.google.android.gms.common.Scopes;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Services.*;
import su.Jalapeno.AntiSpam.Services.SmsReceive.SmsReceiver;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Services.PhoneNumberNormalizer;
import su.Jalapeno.AntiSpam.Util.DebugMessage;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;

import java.io.IOException;
import java.util.Random;

public class Debug extends JalapenoActivity {
    private static final String TAG = Debug.class.getSimpleName();
    PhoneNumberNormalizer _phoneNumberNormalizer;
    JalapenoHttpService jalapenoHttpService;
    SmsService _smsService;
    SettingsService _settingsService;
    SmsReceiver _smsReceiver;
    Context _context;
    private String spamPhone = "+79999";
    private String normalPhone = "+78888";
    private int ACCOUNT_CODE = 16307;
    //private String SCOPE = "user";
    //private String SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    //private String SCOPE = "https://www.googleapis.com/auth/userinfo.profile/";
    private String SCOPE_BASE = "audience:server:client_id:";
    private static final int AUTH_REQUEST_CODE = 1;
    //private static final String SCOPE = "audience:server:client_id:" + CLIENT_ID;
    private String SCOPE;
    //private String CLIENT_ID="140853970719-gufi39a6b16iqm2h5qdlq5reti7f095e.apps.googleusercontent.com";
    //private String CLIENT_ID="140853970719.apps.googleusercontent.com";
    private String CLIENT_ID="140853970719-l2mlr63jvg2h0lrosvnl7sj2t6fi4df0.apps.googleusercontent.com";
    private Debug mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.debug);

        Init();
        SetEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Init();
    }

    private void Init() {
        Log.i(TAG, "Start debug");
        SCOPE=SCOPE_BASE+CLIENT_ID;
        _context = getApplicationContext();
        _phoneNumberNormalizer = new PhoneNumberNormalizer();
        _settingsService = new SettingsService(_context);
        jalapenoHttpService = new JalapenoHttpService(_context);
        _smsService = new SmsService(new ContactsService(), jalapenoHttpService, new UserValidateService(),
                new LocalSpamBaseService(RepositoryFactory.getRepository()), new RequestQueue(jalapenoHttpService), _settingsService);
        _smsReceiver = new SmsReceiver(_settingsService, _smsService);
    }

    private void SetEvent() {
        UiUtils.SetTapForButton(R.id.buttonAuthorization, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Authorization();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonInfo, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Info();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonNewSms, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NewSms();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonSmsSpam, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Spam();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonClearSpam, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClearSpam();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonFillSpam, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FillSpam();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonTestInBaseSpam, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TestInSpam();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonRecieve9999, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Receive9999();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonRecieve8888, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Receive8888();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonLocalhostRequest, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocalhostRequest();
                    }
                });
        UiUtils.SetTapForButton(R.id.buttonSendTokenToEmail, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SentTokenEmail();
                    }
                });

        UiUtils.SetTapForButton(R.id.buttonSettingsTest, new
                View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingsTest();
                    }
                });
    }

    private void SentTokenEmail() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, ACCOUNT_CODE);
    }

    private void SentTokenEmail2(final String accountName) {
        /*AccountManager accountManager = AccountManager.get(getApplicationContext());
        Account[] allAccounts = accountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        Account myAccount = null;
        for (Account account : allAccounts) {
            if (account.name.equals(accountName)) {
                myAccount = account;
            }
        }*/
        mActivity =this;
        final String[] error = new String[1];
        error[0]="";
        DebugMessage.Debug(mActivity, accountName);

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token = "";
                /*
                try {
                    Log.i(TAG, "Begin get token");
                    token = GoogleAuthUtil.getToken(Debug.this, accountName, SCOPE);
                } catch (IOException transientEx) {
                    // Network or server error, try later
                    Log.e(TAG, transientEx.toString());
                } catch (UserRecoverableAuthException e) {
                    // Recover (with e.getIntent())
                    Log.e(TAG, e.toString());
                    Intent recover = e.getIntent();
                    startActivityForResult(recover, ACCOUNT_CODE);
                } catch (GoogleAuthException authEx) {
                    // The call is not ever expected to succeed
                    // assuming you have already verified that
                    // Google Play services is installed.
                    Log.e(TAG, authEx.toString());
                }
                */
                //String token = null;
                try {

                    Log.i(TAG, "Scope: " + SCOPE);
                    Log.i(TAG, "Email: " + accountName);
                    token = GoogleAuthUtil.getToken(mActivity, accountName, SCOPE);
                } catch (GooglePlayServicesAvailabilityException playEx) {
                    error[0] =playEx.getMessage();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                            playEx.getConnectionStatusCode(),
                            Debug.this,
                            1);
                    // Use the dialog to present to the user.
                } catch (UserRecoverableAuthException recoverableException) {
                    error[0]= recoverableException.getMessage();
                    Intent recoveryIntent = recoverableException.getIntent();
                    // Use the intent in a custom dialog or just startActivityForResult.
                    Debug.this.startActivityForResult(recoveryIntent, 2);
                } catch (GoogleAuthException authEx) {
                    // This is likely unrecoverable.
                    error[0]= "Unrecoverable authentication exception: " + authEx.getMessage();
                    Log.e(TAG, "Unrecoverable authentication exception: " + authEx.getMessage(), authEx);

                    return "";
                } catch (IOException ioEx) {
                    error[0]= "transient error encountered: " + ioEx.getMessage();
                    Log.i(TAG, "transient error encountered: " + ioEx.getMessage());

                    //doExponentialBackoff();
                    return "";
                }



                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                Log.i(TAG, "Access token retrieved:" + token);
                if(error[0]!="")
                {
                    DebugMessage.Debug(mActivity, error[0]);
                }
                if(token!="")
                {                DebugMessage.Debug(mActivity, token);
                getAndUseAuthTokenBlocking(token);
                }
            }

        };

        task.execute();
/*
        AsyncTask task1 = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                getAndUseAuthTokenBlocking(accountName);
                return null;
            }
        };

        task.execute(null);*/


    }

    void getAndUseAuthTokenBlocking(String token) {
        EmailSender emailSender = new EmailSender(this);
        emailSender.SendEmail("lexruster@gmail.com;timur.khodzhaev@gmail.com;", "Token", "Token:" + token);
    }

    private void SettingsTest() {
        SettingsService ss = new SettingsService(_context);
        Config set = ss.LoadSettings();
        String old = set.PublicKey;

        set.PublicKey = set.PublicKey + "1";
        set.Enabled = !set.Enabled;
        set.UnknownSound = set.UnknownSound + "2";

        ss.SaveSettings(set);

        Config set2 = ss.LoadSettings();
        Toast.makeText(this, String.format("Old key %s, new key %s", old.toString(), set2.PublicKey.toString()), Toast.LENGTH_LONG).show();
    }

    private void LocalhostRequest() {
        boolean avail = jalapenoHttpService.ServiceIsAvailable();
        Toast.makeText(this, String.format("Available: %s", avail), Toast.LENGTH_LONG).show();

        String resp = jalapenoHttpService.SendLocalTestRequest();

        Toast.makeText(this, String.format("Responce: %s", resp.toString()), Toast.LENGTH_LONG).show();
    }

    private void Receive(String phone, String body) {
        _smsReceiver.Receive(phone, body, _context);
    }

    private void Receive8888() {
        Receive(normalPhone, "test no spam body");
    }

    private void Receive9999() {
        Receive(spamPhone, "test spam body!");
    }

    private void TestInSpam() {
        LocalSpamBaseService spamBase = new LocalSpamBaseService(RepositoryFactory.getRepository());
        Boolean testResult = spamBase.PhoneInLocalSpamBase(spamPhone);
        Toast.makeText(this, String.format("Test: %s", testResult.toString()), Toast.LENGTH_LONG).show();
    }

    private void FillSpam() {
        LocalSpamBaseService spamBase = new LocalSpamBaseService(RepositoryFactory.getRepository());
        Random rnd = new Random();
        for (int i = 0; i < 20; ++i) {
            int random = rnd.nextInt();
            if (random < 0) {
                random *= -1;
            }
            String phone = String.format("+7%d", random);
            String normalPhone = phone;
            spamBase.AddAddressToLocalSpamBase(normalPhone);
        }
        spamBase.AddAddressToLocalSpamBase(spamPhone);
    }

    private void ClearSpam() {
        LocalSpamBaseService spamBase = new LocalSpamBaseService(RepositoryFactory.getRepository());
        spamBase.Clear();
    }

    private void Info() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String number = tm.getLine1Number();

        String imei = tm.getDeviceId();
        Toast.makeText(this, String.format("%s - %s", number, imei), Toast.LENGTH_LONG).show();
    }

    private void Authorization() {
        Intent intent = com.google.android.gms.common.AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, 13);
    }

    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        if (requestCode == 13 && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Toast.makeText(this, accountName, Toast.LENGTH_LONG).show();
        }

        if (requestCode == ACCOUNT_CODE && resultCode == RESULT_OK) {
            String accountName = data
                    .getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            SentTokenEmail2(accountName);
        }
    }

    private void Spam() {
        Toast.makeText(this, "Spam", Toast.LENGTH_LONG).show();
        MediaPlayer mp = MediaPlayer.create(Debug.this, R.raw.cartoon003);
        mp.start();
    }

        /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
*/

    private void NewSms() {
        Toast.makeText(this, "SMS", Toast.LENGTH_LONG).show();
        MediaPlayer mp = MediaPlayer.create(Debug.this, R.raw.cartoon010);
        mp.start();
    }
}
