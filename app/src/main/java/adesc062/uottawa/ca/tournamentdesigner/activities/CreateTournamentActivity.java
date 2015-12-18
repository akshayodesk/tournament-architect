package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import adesc062.uottawa.ca.tournamentdesigner.R;
import adesc062.uottawa.ca.tournamentdesigner.adapters.CustomTeamsListViewAdapter;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

/**
 * This activity is used by the user to create, edit and start tournaments.
 */
public class CreateTournamentActivity extends Activity {

    int tournament_id;
    String tournamentName;
    String[] teamNames;
    Integer[] teamLogos; // These integer correspond to the resource IDs of the drawables
    ListView teamsList;
    boolean deletingTeams = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        // Get the tournament id from the intent
        tournament_id = getIntent().getIntExtra("tournament_id", -1);

        // Set up the list of teams
        setUpTeamsList(false);

        /* If the tournament was already created and is now being edited */
        if(getIntent().hasExtra("tournamentName")){ // If the intent passed a name, then the tournament is being edited

            // Set the tournament name
            EditText tournamentNameTextView = (EditText) findViewById(R.id.tournamentNameEditText);
            tournamentName = getIntent().getStringExtra("tournamentName");
            tournamentNameTextView.setText(tournamentName);

            // Set the format type in the radio group
            int formatType = DBAdapter.getTournamentFormatType(getApplicationContext(), tournament_id);
            RadioGroup formatTypeRadioGroup = (RadioGroup) findViewById(R.id.formatTypeRadioGroup);
            if(formatType == 1) {
                formatTypeRadioGroup.check(R.id.roundRobinRadioButton);
            }
            else if(formatType == 2) {
                formatTypeRadioGroup.check(R.id.knockoutRadioButton);
                EditText numRoundRobinEditText = (EditText) findViewById(R.id.numRoundsEditText);
                numRoundRobinEditText.setEnabled(false);
            }
            else {
                formatTypeRadioGroup.check(R.id.combinationRadioButton);
            }

            // Set the number of Round Robin rounds
            EditText numRoundRoundsEditText = (EditText) findViewById(R.id.numRoundsEditText);
            numRoundRoundsEditText.setText(String.valueOf(DBAdapter.getTournamentNumRounds(getApplicationContext(), tournament_id)));
        }
    }

    /**
     * Called whenever the user clicks the Add Team button.
     * Brings up the team editing page.
     * The Edit Team page allows user to change a team's logo or name.
     *
     * @param view the button that was clicked.
     */
    public void addTeamTournamentOnClick(View view) {

        Intent intent = new Intent(this, EditTeamActivity.class);
        intent.putExtra("tournament_id", tournament_id);
        startActivityForResult(intent, 0);
    }

    public void deleteAndDoneTeamOnClick(View view) {

        /* If we are setting up the teams list for deletion */
        if (deletingTeams == false) {

            // Set the deleting teams flag to true
            deletingTeams = true;

            // Set up the list of teams
            setUpTeamsList(true);

            // Set up on the onClick for the teams list for deleting
            teamsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {

                    // Get the team name
                    TextView teamNameTextView = (TextView) view.findViewById(R.id.txt);
                    String teamName = teamNameTextView.getText().toString();

                    // Delete the team from the database
                    DBAdapter.deleteTeam(getApplicationContext(), teamName, tournament_id);

                    // Set the deleting teams flag to true
                    deletingTeams = false;

                    // Reset the teams list
                    Button deleteAndDoneTeamButton = (Button) findViewById(R.id.deleteAndDoneTeamButton);
                    deleteAndDoneTeamButton.performClick();
                }
            });

            // Rename the button
            Button deleteAndDoneTeamButton = (Button) findViewById(R.id.deleteAndDoneTeamButton);
            deleteAndDoneTeamButton.setText("Done");
        }
        /* If we are setting up the teams list back to normal */
        else {

            // Set the deleting teams flag to true
            deletingTeams = false;

            // Set up the list of teams
            setUpTeamsList(false);

            // Rename the button
            Button deleteAndDoneTeamButton = (Button) findViewById(R.id.deleteAndDoneTeamButton);
            deleteAndDoneTeamButton.setText("Delete");
        }
    }

    public void saveOnClick(View view) {

        // Save all the user entered data
        if (saveData()) {

            // If the data was saved successfully
            // Display a toast informing the user the data has been saved
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast,
                    (ViewGroup) findViewById(R.id.custom_toast_layout_id));

            // Set the message
            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Tournament Saved");

            // Set up the toast
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

    /**
     * Save the tournament name, format type and
     * number of rounds in the database.
     *
     * @return boolean representing whether the data was saved successfully
     */
    private boolean saveData() {

        // Get the tournament name from the EditText
        EditText tournamentNameEditText = (EditText) findViewById(R.id.tournamentNameEditText);
        String tournamentName = tournamentNameEditText.getText().toString();

        try{

            // Save the tournament name in the database
            // Save the name
            DBAdapter.changeTournamentName(getApplicationContext(), tournament_id, tournamentName);

            // Save the format type
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.formatTypeRadioGroup);
            int radioButtonID = radioGroup.getCheckedRadioButtonId();
            View radioButton = radioGroup.findViewById(radioButtonID);
            int formatType = radioGroup.indexOfChild(radioButton);
            formatType++; // 1: RoundRobin, 2: Knockout, 3: Combination
            DBAdapter.saveTournamentFormatType(getApplicationContext(), formatType, tournament_id);

            // Save the number of round robin rounds
            EditText numRoundRobinEditText = (EditText) findViewById(R.id.numRoundsEditText);
            int numRoundRobins = Integer.parseInt(numRoundRobinEditText.getText().toString());
            DBAdapter.saveTournamentNumRounds(getApplicationContext(), numRoundRobins, tournament_id);

            // If the data was saved sucessfully, return true
            return true;

            // If the tournament name is already in use
        }catch (IllegalArgumentException e) {

            // Pop up a dialog
            final Dialog alertTournamentNameAlreadyInUse = new Dialog(CreateTournamentActivity.this);
            alertTournamentNameAlreadyInUse.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertTournamentNameAlreadyInUse.setContentView(R.layout.custom_alert_ok);

            // Set the message
            TextView messageTournamentNameAlreadyInUse = (TextView) alertTournamentNameAlreadyInUse.findViewById(R.id.messageOkTextView);
            messageTournamentNameAlreadyInUse.setText("Tournament name already in use.");

            Button okButton = (Button) alertTournamentNameAlreadyInUse.findViewById(R.id.okButton);

            okButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    alertTournamentNameAlreadyInUse.dismiss();
                }
            });
            alertTournamentNameAlreadyInUse.show();

            // Return false because the data was not saved
            return false;

            // If the tournament name is empty
        }catch (NullPointerException e) {

            // Pop up a dialog
            final Dialog alertTournamentNameEmpty = new Dialog(CreateTournamentActivity.this);
            alertTournamentNameEmpty.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertTournamentNameEmpty.setContentView(R.layout.custom_alert_ok);

            // Set the message
            TextView messageTournamentNameAlreadyInUse = (TextView) alertTournamentNameEmpty.findViewById(R.id.messageOkTextView);
            messageTournamentNameAlreadyInUse.setText("Please enter a tournament name.");

            Button okButton = (Button) alertTournamentNameEmpty.findViewById(R.id.okButton);

            okButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    alertTournamentNameEmpty.dismiss();
                }
            });
            alertTournamentNameEmpty.show();

            return false;
        }
    }

    /**
     * Called whenever the user clicks the Delete Tournament button.
     * Deletes the tournament and all associated data
     * from the database.
     *
     * @param view the button that was clicked.
     */
    public void deleteOnClick(View view) {

        // Ask the user for confirmation on deleting the tournament
        // Pop up a dialog
        final Dialog alertConfirmDeletion = new Dialog(CreateTournamentActivity.this);
        alertConfirmDeletion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertConfirmDeletion.setContentView(R.layout.custom_alert_yes_not);

        Button yesDeleteButton = (Button) alertConfirmDeletion.findViewById(R.id.yesDeleteButon);
        Button noDeleteButton = (Button) alertConfirmDeletion.findViewById(R.id.noDeleteButon);

        /* If the user clicks Yes, then delete the tournament */
        yesDeleteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // If we came from the Load Tournament Activity
                if(getIntent().hasExtra("tournamentName")){

                    // Go back to the Load Tournament activity
                    Intent intent = new Intent(getApplicationContext(), LoadTournamentActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear current activities
                    finish();
                    startActivity(intent);

                    // If we came from the Home Activity
                } else {

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    alertConfirmDeletion.dismiss();
                    finish();
                    startActivity(intent);
                }

                // Delete the tournament from the database
                DBAdapter.deleteTournament(getApplicationContext(), tournament_id);
            }
        });
        /* If the user clicks No, then do not delete the tournament */
        noDeleteButton.setOnClickListener(new View.OnClickListener() {

            // Dismiss the alert
            public void onClick(View v) {

                alertConfirmDeletion.dismiss();
            }
        });
        alertConfirmDeletion.show();
    }

    /**
     * Called whenever the user clicks the Start button.
     * Starts the tournament.
     * A started tournament cannot be edited, but its matches can be played.
     *
     * @param view the View that was clicked.
     */
    public void startOnClick(View view) {

        /* Check if the tournament has less than three teams.
           If it does, do not start and inform the user */
        int numTeams = DBAdapter.getNumTeamsForTournament(getApplicationContext(), tournament_id);

        // If the tournament has less than three teams
        if(numTeams < 3){

            // Pop up a dialog to inform the user
            final Dialog alertNotEnoughTeams = new Dialog(CreateTournamentActivity.this);
            alertNotEnoughTeams.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertNotEnoughTeams.setContentView(R.layout.custom_alert_ok);

            Button okButton = (Button) alertNotEnoughTeams.findViewById(R.id.okButton);

            okButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v)
                {
                    alertNotEnoughTeams.dismiss();
                }
            });
            alertNotEnoughTeams.show();

        /* If the tournament has at least three teams,
            then proceed with starting it. */
        }else {

            // Ask the user for confirmation on starting the tournament
            // Pop up a dialog
            final Dialog alertConfirmStart = new Dialog(CreateTournamentActivity.this);
            alertConfirmStart.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertConfirmStart.setContentView(R.layout.custom_alert_yes_not);

            // Set the correct message
            TextView confirmationMessage = (TextView) alertConfirmStart.findViewById(R.id.confirmMessageTextView);
            confirmationMessage.setText("Are you sure you want to start the tournament now?");

            // Initialize the buttons
            Button yesDeleteButton = (Button) alertConfirmStart.findViewById(R.id.yesDeleteButon);
            Button noDeleteButton = (Button) alertConfirmStart.findViewById(R.id.noDeleteButon);

            /* If the user clicks Yes to start */
            yesDeleteButton.setOnClickListener(new View.OnClickListener() {

                // Start the tournament
                public void onClick(View v) {

                    alertConfirmStart.dismiss();

                    /* Before starting, save the tournament data */
                    // Get the tournament name from the EditText
                    EditText tournamentNameEditText = (EditText) findViewById(R.id.tournamentNameEditText);
                    String tournamentName = tournamentNameEditText.getText().toString();

                    try{

                        // Save the tournament name
                        DBAdapter.changeTournamentName(getApplicationContext(), tournament_id, tournamentName);

                        // Save the format type
                        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.formatTypeRadioGroup);
                        int radioButtonID = radioGroup.getCheckedRadioButtonId();
                        View radioButton = radioGroup.findViewById(radioButtonID);
                        int formatType = radioGroup.indexOfChild(radioButton);
                        formatType++; // 1: RoundRobin, 2: Knockout, 3: Combination
                        DBAdapter.saveTournamentFormatType(getApplicationContext(), formatType, tournament_id);

                        // Save the number of round robin rounds
                        EditText numRoundRobinEditText = (EditText) findViewById(R.id.numRoundsEditText);
                        int numRoundRobins = Integer.parseInt(numRoundRobinEditText.getText().toString());
                        DBAdapter.saveTournamentNumRounds(getApplicationContext(), numRoundRobins, tournament_id);

                        Intent intent = new Intent(getApplicationContext(), StandingsActivity.class);
                        intent.putExtra("tournament_id", tournament_id);
                        finish();
                        startActivity(intent);

                        // Change tournament status to started, (2)
                        //DBAdapter.changeTournamentStatus(getApplicationContext(), tournament_id);

                    // If the tournament name is already in use - cancel start
                    }catch (IllegalArgumentException e) {

                        // Pop up a dialog to inform the user
                        final Dialog alertTournamentNameAlreadyInUse = new Dialog(CreateTournamentActivity.this);
                        alertTournamentNameAlreadyInUse.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertTournamentNameAlreadyInUse.setContentView(R.layout.custom_alert_ok);

                        // Set the message
                        TextView messageTournamentNameAlreadyInUse = (TextView) alertTournamentNameAlreadyInUse.findViewById(R.id.messageOkTextView);
                        messageTournamentNameAlreadyInUse.setText("Tournament name already in use.");

                        Button okButton = (Button) alertTournamentNameAlreadyInUse.findViewById(R.id.okButton);

                        okButton.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                alertTournamentNameAlreadyInUse.dismiss();
                            }
                        });
                        alertTournamentNameAlreadyInUse.show();

                    // If the tournament name is empty - cancel start
                    }catch (NullPointerException e) {

                        // Pop up a dialog to inform the user
                        final Dialog alertTournamentNameEmpty = new Dialog(CreateTournamentActivity.this);
                        alertTournamentNameEmpty.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertTournamentNameEmpty.setContentView(R.layout.custom_alert_ok);

                        // Set the message
                        TextView messageTournamentNameAlreadyInUse = (TextView) alertTournamentNameEmpty.findViewById(R.id.messageOkTextView);
                        messageTournamentNameAlreadyInUse.setText("Please enter a tournament name.");

                        Button okButton = (Button) alertTournamentNameEmpty.findViewById(R.id.okButton);

                        okButton.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                alertTournamentNameEmpty.dismiss();
                            }
                        });
                        alertTournamentNameEmpty.show();
                    }
                }
            });

            /* If if the user clicks No, then do not start */
            noDeleteButton.setOnClickListener(new View.OnClickListener() {

                // Dismiss the alert
                public void onClick(View v) {

                    alertConfirmStart.dismiss();
                }
            });
            alertConfirmStart.show();
        }
    }

    /**
     * Used whenever a team is saved or deleted and the application
     * comes back to the tournament creation page.
     * It updates the teams list/
     *
     * @param requestCode the code that was requested when the intent was started.
     * @param resultCode the code passed back from the edit team activity.
     * @param data the intent that started this activity again.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        setUpTeamsList(deletingTeams);

        if (deletingTeams) {

            // Reset the teams list
            Button deleteAndDoneTeamOnClick = (Button) findViewById(R.id.deleteAndDoneTeamButton);
            deleteAndDoneTeamOnClick.performClick();
        }
    }

    /**
     * Called whenever clicks on the Round Robin radio button.
     * Sets up the tournament as Round Robin format, (1).
     *
     * @param view the view of the radio button pressed.
     */
    public void roundRobinOnClick(View view) {

        EditText numRoundRobinEditText = (EditText) findViewById(R.id.numRoundsEditText);
        numRoundRobinEditText.setEnabled(true); // Enables the user to change the number of rounds
    }

    /**
     * Called whenever clicks on the Knockout radio button.
     * Sets up the tournament as Knockout format, (2).
     *
     * @param view the view of the radio button pressed.
     */
    public void knockoutRobinOnClick(View view) {

        EditText numRoundRobinEditText = (EditText) findViewById(R.id.numRoundsEditText);
        numRoundRobinEditText.setEnabled(false); // Disables the user from changing the number of rounds
    }

    /**
     * Called whenever clicks on the Combination radio button.
     * Sets up the tournament as Combination format, (3).
     * A combination format implies a Round Robin format first and
     * then a Knockout format.
     *
     * @param view the view of the radio button pressed.
     */
    public void combinationRobinOnClick(View view) {

        EditText numRoundRobinEditText = (EditText) findViewById(R.id.numRoundsEditText);
        numRoundRobinEditText.setEnabled(true); // Enables the user to change the number of rounds
    }

    /**
     * Called by the application to update the list of teams.
     * The list of teams is saved in the database
     * whenever the user saves or deletes a team and not
     * when the user clicks the Save buton.
     */
    private void setUpTeamsList(boolean deleting) {

        /* Set up the list of teams */
        // Get the information from the database
        ArrayList<String> teamNamesArray = DBAdapter.getTeamNames(this.getApplicationContext(), tournament_id);
        ArrayList<String> teamLogosArray = DBAdapter.getTeamLogos(this.getApplicationContext(), tournament_id);

        // Convert the team names to a String array
        teamNames = new String[teamNamesArray.size()];
        teamNames = teamNamesArray.toArray(teamNames);

        // Convert the team logos to an integer array of the resource ids
        teamLogos = new Integer[teamLogosArray.size()];
        for(int i = 0; i < teamLogos.length; i++){
            teamLogos[i] = this.getResources().getIdentifier(teamLogosArray.get(i), "drawable", this.getPackageName());
        }
        // Create the teams list adapter and set it
        CustomTeamsListViewAdapter adapter = new CustomTeamsListViewAdapter(CreateTournamentActivity.this, teamNames, teamLogos, deleting);
        teamsList = (ListView) findViewById(R.id.teamsListView);
        teamsList.setAdapter(adapter);

        // Set up on the onClick for the teams list
        teamsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {

                // Get the team name
                TextView  teamNameTextView = (TextView) view.findViewById(R.id.txt);
                String teamName = teamNameTextView.getText().toString();

                // Put the information in the intent
                Intent intent = new Intent(getApplicationContext(), EditTeamActivity.class);
                intent.putExtra("teamName", teamName);
                intent.putExtra("teamLogo", DBAdapter.getTeamLogo(getApplicationContext(), teamName, tournament_id));
                intent.putExtra("tournament_id", tournament_id);

                // Start the edit team activity
                startActivityForResult(intent, 0);
            }
        });
    }

    public void onBackPressed() {

        // Save all the data
        saveData();

        // Finish activity and return
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}