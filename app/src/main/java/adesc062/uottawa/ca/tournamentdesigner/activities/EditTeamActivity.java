package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import adesc062.uottawa.ca.tournamentdesigner.R;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class EditTeamActivity extends Activity {
    ImageView teamLogo;
    EditText teamNameEditText;
    String logo;
    boolean changedLogo = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);
        // If we are editing a team
        if(getIntent().hasExtra("teamName")){
            // Get the information from the intent
            String teamName = getIntent().getStringExtra("teamName");
            logo = getIntent().getStringExtra("teamLogo");
            // Set up the team logo
            int logoID = getResources().getIdentifier(logo, "drawable", getPackageName());
            teamLogo = (ImageView) findViewById(R.id.teamLogoImageView);
            teamLogo.setImageResource(logoID);
            //logo = getResources().getResourceEntryName(logoID);
            logo = "canada";
            // Set up the team name
            teamNameEditText = (EditText) findViewById(R.id.teamNameEditText);
            teamNameEditText.setText(teamName);
        // If we are editing a team
        } else
            logo = "tournament_designer_logo_lowres";
    }

    /**
     * Used to save a team and add it to the tournament.
     *
     * @param view the view of the save button.
     */
    public void saveOnClick(View view) {
        // If a team is being modified
        if(getIntent().hasExtra("teamName")){
            // Get the original team name
            String originalTeamName = getIntent().getStringExtra("teamName");
            // Get the new team name
            teamNameEditText = (EditText) findViewById(R.id.teamNameEditText);
            String newTeamName = teamNameEditText.getText().toString();
            // Get the tournament id
            int tournament_id = getIntent().getIntExtra("tournament_id", 1);
            // If the logo was not changed
            if(changedLogo == false) {
                logo = DBAdapter.getTeamLogo(getApplicationContext(), originalTeamName, tournament_id);
                try {
                    DBAdapter.modifyTeam(getApplicationContext(), originalTeamName, newTeamName, logo,
                            DBAdapter.getMostRecentTournamentId(getApplicationContext()));
                    // Finish activity and return
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }catch (IllegalArgumentException e) {
                    // If the team name is already in use
                    // Pop up a dialog
                    final Dialog alertTeamNameAlreadyInUse = new Dialog(EditTeamActivity.this);
                    alertTeamNameAlreadyInUse.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertTeamNameAlreadyInUse.setContentView(R.layout.custom_alert_ok);
                    // Set the message
                    TextView messageTournamentNameAlreadyInUse = (TextView) alertTeamNameAlreadyInUse.findViewById(R.id.messageOkTextView);
                    messageTournamentNameAlreadyInUse.setText("Team name already in use.");
                    Button okButton = (Button) alertTeamNameAlreadyInUse.findViewById(R.id.okButton);
                    okButton.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            alertTeamNameAlreadyInUse.dismiss();
                        }
                    });
                    alertTeamNameAlreadyInUse.show();
                }catch (NullPointerException e) {
                    // If the team name is empty
                    // Pop up a dialog
                    final Dialog alertTeamNameEmpty = new Dialog(EditTeamActivity.this);
                    alertTeamNameEmpty.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertTeamNameEmpty.setContentView(R.layout.custom_alert_ok);
                    // Set the message
                    TextView messageTournamentNameAlreadyInUse = (TextView) alertTeamNameEmpty.findViewById(R.id.messageOkTextView);
                    messageTournamentNameAlreadyInUse.setText("Please enter a team name.");
                    Button okButton = (Button) alertTeamNameEmpty.findViewById(R.id.okButton);
                    okButton.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            alertTeamNameEmpty.dismiss();
                        }
                    });
                    alertTeamNameEmpty.show();
                }
            }
            // If the logo was changed
            else {
                try {
                    DBAdapter.modifyTeamName(getApplicationContext(), originalTeamName, newTeamName,
                            DBAdapter.getMostRecentTournamentId(getApplicationContext()));
                    // Finish activity and return
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }catch (IllegalArgumentException e) {
                    // If the team name is already in use
                    // Pop up a dialog
                    final Dialog alertTeamNameAlreadyInUse = new Dialog(EditTeamActivity.this);
                    alertTeamNameAlreadyInUse.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertTeamNameAlreadyInUse.setContentView(R.layout.custom_alert_ok);
                    // Set the message
                    TextView messageTournamentNameAlreadyInUse = (TextView) alertTeamNameAlreadyInUse.findViewById(R.id.messageOkTextView);
                    messageTournamentNameAlreadyInUse.setText("Team name already in use.");
                    Button okButton = (Button) alertTeamNameAlreadyInUse.findViewById(R.id.okButton);
                    okButton.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            alertTeamNameAlreadyInUse.dismiss();
                        }
                    });
                    alertTeamNameAlreadyInUse.show();
                }catch (NullPointerException e) {
                    // If the team name is empty
                    // Pop up a dialog
                    final Dialog alertTeamNameEmpty = new Dialog(EditTeamActivity.this);
                    alertTeamNameEmpty.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertTeamNameEmpty.setContentView(R.layout.custom_alert_ok);
                    // Set the message
                    TextView messageTournamentNameAlreadyInUse = (TextView) alertTeamNameEmpty.findViewById(R.id.messageOkTextView);
                    messageTournamentNameAlreadyInUse.setText("Please enter a team name.");
                    Button okButton = (Button) alertTeamNameEmpty.findViewById(R.id.okButton);
                    okButton.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            alertTeamNameEmpty.dismiss();
                        }
                    });
                    alertTeamNameEmpty.show();
                }
            }
        // If a team is being inserted
        }else {
            // Get the new team name
            teamNameEditText = (EditText) findViewById(R.id.teamNameEditText);
            String newTeamName = teamNameEditText.getText().toString();
            try {
                // Insert the team
                DBAdapter.insertTeam(getApplicationContext(), newTeamName, logo,
                        DBAdapter.getMostRecentTournamentId(getApplicationContext()));
                // Finish activity and return
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }catch (IllegalArgumentException e) {
                // If the team name is already in use
                // Pop up a dialog
                final Dialog alertTeamNameAlreadyInUse = new Dialog(EditTeamActivity.this);
                alertTeamNameAlreadyInUse.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertTeamNameAlreadyInUse.setContentView(R.layout.custom_alert_ok);
                // Set the message
                TextView messageTournamentNameAlreadyInUse = (TextView) alertTeamNameAlreadyInUse.findViewById(R.id.messageOkTextView);
                messageTournamentNameAlreadyInUse.setText("Team name already in use.");
                Button okButton = (Button) alertTeamNameAlreadyInUse.findViewById(R.id.okButton);
                okButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        alertTeamNameAlreadyInUse.dismiss();
                    }
                });
                alertTeamNameAlreadyInUse.show();
            }catch (NullPointerException e) {
                // If the team name is empty
                // Pop up a dialog
                final Dialog alertTeamNameEmpty = new Dialog(EditTeamActivity.this);
                alertTeamNameEmpty.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertTeamNameEmpty.setContentView(R.layout.custom_alert_ok);
                // Set the message
                TextView messageTournamentNameAlreadyInUse = (TextView) alertTeamNameEmpty.findViewById(R.id.messageOkTextView);
                messageTournamentNameAlreadyInUse.setText("Please enter a team name.");
                Button okButton = (Button) alertTeamNameEmpty.findViewById(R.id.okButton);
                okButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        alertTeamNameEmpty.dismiss();
                    }
                });
                alertTeamNameEmpty.show();
            }
        }
    }

    /**
     *
     * @param view
     */
    public void deleteOnClick(View view) {
        // If a team is being edited, then delete it
        // Otherwise, simply return
        if(getIntent().hasExtra("teamName")) {
            String teamName = getIntent().getStringExtra("teamName");
            DBAdapter.deleteTeam(getApplicationContext(), teamName, DBAdapter.getMostRecentTournamentId(getApplicationContext()));
        }
        // Finish activity and return
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Called whenever the user clicks the Logo image.
     * Brings up the Flag Selection page.
     * The Flag Selection page allows the user to select a flag.
     *
     * @param view the View that was clicked.
     */
    public void logoOnClick(View view) {
        Intent intent = new Intent(this.getApplicationContext(), FlagSelectionActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Get the image
        teamLogo = (ImageView) findViewById(R.id.teamLogoImageView);
        // If an image was passed, edit the current one
        if (resultCode == RESULT_OK) {
            // Figure out the correct image
            int id = data.getIntExtra("imageID", R.id.teamLogoImageView);
            String idStr = getResources().getResourceName(id);
            String drawableName = idStr.substring(39);
            drawableName = drawableName.replaceAll("ImageView", "");
            drawableName = drawableName.replaceAll("adesc062.uottawa.ca.tournamentdesigner/", "");
            drawableName = drawableName.replaceAll("id/", "");
            if (drawableName.equals("tournamentdesigner"))
                drawableName = "tournament_designer_logo_lowres";
            int resID = getResources().getIdentifier(drawableName, "drawable", getPackageName());
            teamLogo.setImageResource(resID);
            // Get the team name
            teamNameEditText = (EditText) findViewById(R.id.teamNameEditText);
            String teamName = teamNameEditText.getText().toString();
            // Change the logo in the database
            DBAdapter.modifyTeamLogo(getApplicationContext(), getIntent().getStringExtra("teamName"),
                    drawableName, DBAdapter.getMostRecentTournamentId(getApplicationContext()));
            // Update the logo variables
            logo = drawableName;
            changedLogo = true;
        }
    }
}
