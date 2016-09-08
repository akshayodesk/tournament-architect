package descalexis.gmail.com.tournamentarchitect.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import descalexis.gmail.com.tournamentarchitect.R;

public class DropboxSyncActivity extends Activity {

    private ToggleButton activationToggleButton;
    private SharedPreferences sharedPreferences;
    private boolean syncIsActivated;
    private Button logInButton;
    private Button logOutButton;
    private Button uploadButton;
    private Button downloadButton;
    private TextView loggedInAsTextView;
    private String accessToken;
    private String username;
    private DbxClientV2 sDbxClient;
    private boolean loggedOut;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_sync);
        activationToggleButton = (ToggleButton) findViewById(R.id.activationToggleButton);
        logInButton = (Button) findViewById(R.id.logInButton);
        logOutButton = (Button) findViewById(R.id.logOutButton);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        downloadButton = (Button) findViewById(R.id.downloadButton);
        loggedInAsTextView = (TextView) findViewById(R.id.loggedInAsTextView);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        syncIsActivated = sharedPreferences.getBoolean("syncIsActivated", false);
        accessToken = sharedPreferences.getString("accessToken", null);
        activationToggleButton.setChecked(syncIsActivated);
        setButtonsStatus();
        setUsername();
    }

    public void activationToggleButtonOnClick(View view) {
        if (activationToggleButton.isChecked()) {
            syncIsActivated = true;
            sharedPreferences.edit().putBoolean("syncIsActivated", true).commit();
            setButtonsStatus();
        }
        else {
            syncIsActivated = false;
            sharedPreferences.edit().putBoolean("syncIsActivated", false).commit();
            setButtonsStatus();
        }
    }

    public void logInOnClick(View view) {
        Auth.startOAuth2Authentication(DropboxSyncActivity.this, getString(R.string.app_key));
        sharedPreferences.edit().putBoolean("loggedOut", false).commit();
    }

    public void logOutOnClick(View view) {
        accessToken = null;
        username = null;
        sharedPreferences.edit().remove("accessToken").commit();
        sharedPreferences.edit().remove("username").commit();
        sharedPreferences.edit().putBoolean("loggedOut", true).commit();
        setUsername();
        setButtonsStatus();
    }

    public void uploadOnClick(View view) {
        final Dialog alertConfirmUpload = new Dialog(DropboxSyncActivity.this);
        alertConfirmUpload.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertConfirmUpload.setContentView(R.layout.custom_alert_yes_not);
        TextView alertText = (TextView) alertConfirmUpload.findViewById(R.id.confirmMessageTextView);
        alertText.setText("Are you sure you want to upload the local database and overwrite the existing one?");
        Button yesUploadButton = (Button) alertConfirmUpload.findViewById(R.id.yesDeleteButon);
        Button noUploadButton = (Button) alertConfirmUpload.findViewById(R.id.noDeleteButon);
        yesUploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new UploadDatabaseTask().execute();
                alertConfirmUpload.dismiss();
                Log.d("LOGIN", "Went through here");
            }
        });
        noUploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertConfirmUpload.dismiss();
            }
        });
        alertConfirmUpload.show();
    }

    public void downloadOnClick(View view) {
        final Dialog alertConfirmDownload = new Dialog(DropboxSyncActivity.this);
        alertConfirmDownload.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertConfirmDownload.setContentView(R.layout.custom_alert_yes_not);
        TextView alertText = (TextView) alertConfirmDownload.findViewById(R.id.confirmMessageTextView);
        alertText.setText("Are you sure you want to overwrite the local database with the one from your account?");
        Button yesDownloadButton = (Button) alertConfirmDownload.findViewById(R.id.yesDeleteButon);
        Button noDownloadButton = (Button) alertConfirmDownload.findViewById(R.id.noDeleteButon);
        yesDownloadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DownloadDatabaseTask().execute();
                alertConfirmDownload.dismiss();
            }
        });
        noDownloadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertConfirmDownload.dismiss();
            }
        });
        alertConfirmDownload.show();
    }

    protected void onResume() {
        super.onResume();
        loggedOut = sharedPreferences.getBoolean("loggedOut", true);
        if (!loggedOut) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                sharedPreferences.edit().putString("accessToken", accessToken).apply();
            }
        }
        new GetUsernameTask().execute();
    }

    private void setButtonsStatus() {
        if (syncIsActivated) {
            if (accessToken != null) {
                logInButton.setClickable(false);
                logInButton.getBackground().setAlpha(100);
                logOutButton.setClickable(true);
                logOutButton.getBackground().setAlpha(255);
                uploadButton.setClickable(true);
                uploadButton.getBackground().setAlpha(255);
                downloadButton.setClickable(true);
                downloadButton.getBackground().setAlpha(255);
            } else {
                logInButton.setClickable(true);
                logInButton.getBackground().setAlpha(255);
                logOutButton.setClickable(false);
                logOutButton.getBackground().setAlpha(100);
                uploadButton.setClickable(false);
                uploadButton.getBackground().setAlpha(100);
                downloadButton.setClickable(false);
                downloadButton.getBackground().setAlpha(100);
            }
        }
        else {
            logInButton.setClickable(false);
            logInButton.getBackground().setAlpha(100);
            logOutButton.setClickable(false);
            logOutButton.getBackground().setAlpha(100);
            uploadButton.setClickable(false);
            uploadButton.getBackground().setAlpha(100);
            downloadButton.setClickable(false);
            downloadButton.getBackground().setAlpha(100);
        }
    }

    private void setUsername() {
        accessToken = sharedPreferences.getString("accessToken", null);
        if (accessToken != null) {
            username = sharedPreferences.getString("username", "Unknown");
            loggedInAsTextView.setText("Logged in as " + username);
        } else {
            loggedInAsTextView.setText("");
        }
    }

    private void createToast(String textToDisplay, int length) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        TextView toastTextView = (TextView) layout.findViewById(R.id.text);
        toastTextView.setText(textToDisplay);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();
    }

    public class GetUsernameTask extends AsyncTask<Void, Boolean, Boolean> {
        protected Boolean doInBackground(Void... params) {
            Boolean logInSuccessful = false;
            accessToken = sharedPreferences.getString("accessToken", null);
            if (syncIsActivated && accessToken != null) {
                try {
                    DbxRequestConfig requestConfig = new DbxRequestConfig("TournamentArchitect/1.1");
                    sDbxClient = new DbxClientV2(requestConfig, accessToken);
                    username = sDbxClient.users().getCurrentAccount().getName().getDisplayName();
                    sharedPreferences.edit().putString("username", username).apply();
                    logInSuccessful = true;
                } catch (Exception e) {
                    Log.i("GetUsernameError", "Error getting username", e);
                }
            }
            return logInSuccessful;
        }
        protected void onPostExecute(Boolean logInSuccessful) {
            if (syncIsActivated && accessToken != null) {
                setUsername();
                setButtonsStatus();
            }
        }
    }

    public class UploadDatabaseTask extends AsyncTask<Void, Boolean, Boolean> {
        protected void onPreExecute() {
            if (syncIsActivated && accessToken != null) {
                createToast("Uploading...", Toast.LENGTH_LONG);
            }
        }
        protected Boolean doInBackground(Void... params) {
            Boolean uploadSuccessful = false;
            accessToken = sharedPreferences.getString("accessToken", null);
            if (syncIsActivated && accessToken != null) {
                try {
                    DbxRequestConfig requestConfig = new DbxRequestConfig("TournamentArchitect/1.1");
                    sDbxClient = new DbxClientV2(requestConfig, accessToken);
                    final String localFilePath = "/data/data/adesc062.uottawa.ca.tournamentArchitect/databases/TournamentArchitectDatabase.s3db";
                    File localFile = new File(localFilePath);
                    InputStream inputStream = new FileInputStream(localFile);
                    sDbxClient.files().delete("/TournamentArchitectDatabase.s3db");
                    sDbxClient.files().uploadBuilder("/TournamentArchitectDatabase.s3db").uploadAndFinish(inputStream);
                    uploadSuccessful = true;
                } catch (Exception e) {
                    Log.i("ErrorSync", "Error syncing data", e);
                }
            }
            return uploadSuccessful;
        }
        protected void onPostExecute(Boolean uploadSuccessful) {
            if (syncIsActivated && accessToken != null) {
                if (uploadSuccessful)
                    createToast("Upload successful", Toast.LENGTH_SHORT);
                else
                    createToast("Upload failed", Toast.LENGTH_SHORT);
                setUsername();
                }
        }
    }

    public class DownloadDatabaseTask extends AsyncTask<Void, Boolean, Boolean> {
        protected void onPreExecute() {
            if (syncIsActivated && accessToken != null) {
                createToast("Downloading...", Toast.LENGTH_LONG);
            }
        }
        protected Boolean doInBackground(Void... params) {
            Boolean downloadSuccessful = false;
            accessToken = sharedPreferences.getString("accessToken", null);
            if (syncIsActivated && accessToken != null) {
                try {
                    DbxRequestConfig requestConfig = new DbxRequestConfig("TournamentArchitect/1.1");
                    sDbxClient = new DbxClientV2(requestConfig, accessToken);
                    DbxDownloader<FileMetadata> dbxDownloader = sDbxClient.files().download("/TournamentArchitectDatabase.s3db");
                    InputStream inputStream = dbxDownloader.getInputStream();
                    OutputStream outputStream = new FileOutputStream("/data/data/adesc062.uottawa.ca.tournamentArchitect/databases/TournamentArchitectDatabase.s3db");
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer) )> 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                    downloadSuccessful = true;
                } catch (Exception e) {
                    Log.i("ErrorSync", "Error syncing data", e);
                }
            }
            return downloadSuccessful;
        }
        protected void onPostExecute(Boolean uploadSuccessful) {
            if (syncIsActivated && accessToken != null) {
                if (uploadSuccessful)
                    createToast("Download successful", Toast.LENGTH_SHORT);
                else
                    createToast("Download failed; are you sure there is a file to download?", Toast.LENGTH_SHORT);
                setUsername();
            }
        }
    }
}
