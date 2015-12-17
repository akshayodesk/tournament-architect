package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import adesc062.uottawa.ca.tournamentdesigner.R;

public class HelpActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Set the text
        //int id = this.getResources().getIdentifier("help", "raw", "TournamentDesigner.app.src.main.res.raw");
        Context context = getApplicationContext();
        int id = this.getResources().getIdentifier("help", "raw", this.getPackageName());
        String helpText = readRawTextFile(getApplication(), id);
        TextView helpTextView = (TextView) findViewById(R.id.helpTextTextView);
        helpTextView.setText(helpText);
        helpTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    public static String readRawTextFile(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}
