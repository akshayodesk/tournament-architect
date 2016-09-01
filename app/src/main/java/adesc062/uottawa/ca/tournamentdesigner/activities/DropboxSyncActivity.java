package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;

import adesc062.uottawa.ca.tournamentdesigner.R;


public class DropboxSyncActivity extends Activity {
    final static private String APP_KEY = "drnhvv07gtk5pvu";
    final static private String APP_SECRET = "rxtzet77l6yhpsd";
    private DropboxAPI<AndroidAuthSession> mDBApi;

    private String[] tournamentsNames;
    private String[] tournamentsStatus;
    private boolean deletingTournaments = false;
    private ListView tournamentsListView;
    private ToggleButton activationToggleButton;
    private SharedPreferences sharedPreferences;
    private boolean activationToggleButtonIsActivated;
    private Button logInButton;
    private Button logOutButton;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_sync);

        activationToggleButton = (ToggleButton) findViewById(R.id.activationToggleButton);
        logInButton = (Button) findViewById(R.id.logInButton);
        logOutButton = (Button) findViewById(R.id.logOutButton);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        activationToggleButtonIsActivated = sharedPreferences.getBoolean("activationToggleButtonIsActivated", false);

        if (activationToggleButtonIsActivated)
        {
            setLogInButtonStatus(true);
        }
        else
        {
            setLogInButtonStatus(false);
        }


    }

    public void activationToggleButtonOnClick(View view)
    {
        if (activationToggleButton.isChecked())
        {
            sharedPreferences.edit().putBoolean("activationToggleButtonIsActivated", true).commit();
            setLogInButtonStatus(true);

        }
        else
        {
            sharedPreferences.edit().putBoolean("activationToggleButtonIsActivated", false).commit();
            setLogInButtonStatus(false);
        }
    }

    public void logInOnClick(View view) {
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(DropboxSyncActivity.this);
    }

    public void logOutOnClick(View view) {
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(DropboxSyncActivity.this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    protected void onResume() {
        super.onResume();

        // Get the access token
        if (mDBApi != null && mDBApi.getSession().authenticationSuccessful()) {
            try {
                mDBApi.getSession().finishAuthentication();
                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
        // Upload the database file through a background thread
        new uploadDatabaseFileTask().execute();
    }

    public void onBackPressed() {

        // Go back to the Home page
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void disableButton() {

        Button deleteAndDoneTournamentButton = (Button) findViewById(R.id.deleteAndDoneTournamentButton);
        deleteAndDoneTournamentButton.setEnabled(false);
        deleteAndDoneTournamentButton.setAlpha(0.5f);
        deleteAndDoneTournamentButton.setText("Delete");
    }

    private void setLogInButtonStatus(boolean value)
    {
        if (value)
        {
            logInButton.setClickable(true);
            logInButton.getBackground().setAlpha(255);
        }
            else
        {
            logInButton.setClickable(false);
            logInButton.getBackground().setAlpha(100);
        }
    }

    private class uploadDatabaseFileTask extends AsyncTask<Void, Boolean, Boolean> {

        protected void onPreExecute()
        {
            // Display a toast informing the user the upload is beginning
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast,
                    (ViewGroup) findViewById(R.id.custom_toast_layout_id));
            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Syncing.");
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected Boolean doInBackground(Void... params) {
            Boolean uploadSuccessful = false;
            final String inFileName = "/data/data/adesc062.uottawa.ca.tournamentdesigner/databases/tournamentDesignerDB.s3db";
            File dbFile = new File(inFileName);
            DropboxAPI.Entry response = null;
            try {
                FileInputStream inputStream = new FileInputStream(dbFile);
                response = mDBApi.putFileOverwrite("/tournamentDesignerDB.s3db", inputStream,
                        dbFile.length(), null);
            } catch (Exception e) {
                Log.i("PutFileError", "Error uploading file", e);
            }
            if (response != null) {
                Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
                uploadSuccessful = true;
            }
            return uploadSuccessful;
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(Boolean uploadSuccessful) {
            // Display a toast informing the user the upload is beginning
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast,
                    (ViewGroup) findViewById(R.id.custom_toast_layout_id));
            TextView textResult = (TextView) layout.findViewById(R.id.text);
            if (uploadSuccessful)
                textResult.setText("Done");
            else
                textResult.setText("Sync failed");
            Toast toastResult = new Toast(getApplicationContext());
            toastResult.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toastResult.setDuration(Toast.LENGTH_LONG);
            toastResult.setView(layout);
            toastResult.show();
        }
    }
}
