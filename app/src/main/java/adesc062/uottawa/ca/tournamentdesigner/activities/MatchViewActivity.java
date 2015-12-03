package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.*;

import adesc062.uottawa.ca.tournamentdesigner.R;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;
import adesc062.uottawa.ca.tournamentdesigner.domain.Match;
import adesc062.uottawa.ca.tournamentdesigner.domain.MatchTeamScore;

public class MatchViewActivity extends Activity {

    int match_id;
    String team1Name;
    String team2Name;
    int team1Logo;
    int team2Logo;
    EditText team1ScoreEditText;
    EditText team2ScoreEditText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        // Initialize the intent
        Intent intent = getIntent();

        // Initialize the views
        TextView teamName1TextView = (TextView) findViewById(R.id.teamName1TextView);
        TextView teamName2TextView = (TextView) findViewById(R.id.teamName2TextView);
        ImageView team1ImageView = (ImageView) findViewById(R.id.team1ImageView);
        ImageView team2ImageView = (ImageView) findViewById(R.id.team2ImageView);
        EditText team1ScoreEditText = (EditText) findViewById(R.id.team1ScoreEditText);
        EditText team2ScoreEditText = (EditText) findViewById(R.id.team2ScoreEditTExt);

        // Get the information
        team1Name = intent.getStringExtra("team1Name");
        team2Name = intent.getStringExtra("team2Name");
        team1Logo = intent.getIntExtra("team1Logo", 0);
        team2Logo = intent.getIntExtra("team2Logo", 0);

        // Set the team logos, names and the match id
        match_id = intent.getIntExtra("match_id", 0);
        teamName1TextView.setText(team1Name);
        teamName2TextView.setText(team2Name);
        team1ImageView.setImageResource(team1Logo);
        team2ImageView.setImageResource(team2Logo);
    }

    public void saveOnClickInMatchView(View view) {

        // Initialize the edit texts
        team1ScoreEditText = (EditText) findViewById(R.id.team1ScoreEditText);
        team2ScoreEditText = (EditText) findViewById(R.id.team2ScoreEditTExt);

        // Get the strings from the edit text
        String team1Score = team1ScoreEditText.getText().toString();
        String team2Score = team2ScoreEditText.getText().toString();

        // If the user entered scores for both teams, save them
        if(!team1Score.equals("") && !team2Score.equals("")) {

            // If the match is a tie and the format is Knockout


            // Get the scores from the edit texts
            int team1ScoreInt = Integer.parseInt(team1Score);
            int team2ScoreInt = Integer.parseInt(team2Score);

            TextView

            // Get the tournament id
            int tournament_id = DBAdapter.getTournamentId(getApplicationContext(), match_id);

            // Update the match
            Match.updateMatch(getApplicationContext(), match_id, team1Name, team1ScoreInt,
                    team2Name, team2ScoreInt, tournament_id);

            //finish();
        }
        // If the user did not enter scores for both teams, pop up a warning
        else {

            // Pop up a dialog to inform the user
            final Dialog alertTournamentNameAlreadyInUse = new Dialog(MatchViewActivity.this);
            alertTournamentNameAlreadyInUse.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertTournamentNameAlreadyInUse.setContentView(R.layout.custom_alert_ok);

            // Set the message
            TextView messageTournamentNameAlreadyInUse = (TextView) alertTournamentNameAlreadyInUse.findViewById(R.id.messageOkTextView);
            messageTournamentNameAlreadyInUse.setText("You must enter a score for both teams to save.");

            Button okButton = (Button) alertTournamentNameAlreadyInUse.findViewById(R.id.okButton);

            okButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    alertTournamentNameAlreadyInUse.dismiss();
                }
            });
            alertTournamentNameAlreadyInUse.show();
        }

    }
}
