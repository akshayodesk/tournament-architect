package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import adesc062.uottawa.ca.tournamentdesigner.R;

public class FlagSelectionActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_selection);
    }

    public void onFlagClick(View view){

        // Taken from Lab Android App Development (Advanced concepts)
        //Creating a Return intent to pass to the Edit Team Activity
        Intent returnIntent = new Intent();

        //Figuring out which image was clicked
        ImageView selectedImage = (ImageView) view;

        //Adding stuff to the return intent
        returnIntent.putExtra("imageID", selectedImage.getId());

        setResult(RESULT_OK, returnIntent);

        //Finishing Activity and return
        finish();

    }
}
