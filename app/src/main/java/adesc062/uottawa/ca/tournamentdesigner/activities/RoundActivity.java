package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import adesc062.uottawa.ca.tournamentdesigner.R;
import adesc062.uottawa.ca.tournamentdesigner.adapters.CustomRoundsListViewAdapter;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class RoundActivity extends Activity {

    private int tournament_id;
    private int editedRoundNum;
    private int tournamentStatus;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);

        // Get the tournament info
        Intent intent = getIntent();
        tournament_id = intent.getIntExtra("tournament_id", 0);
        editedRoundNum = intent.getIntExtra("editedRoundNum", -1);
        tournamentStatus = DBAdapter.getTournamentStatus(getApplicationContext(), tournament_id);

        // Set up the list of rounds
        updateRounds();
    }

    private void updateRounds() {

        // Get the number of rounds currently available
        int numCurrentRounds = DBAdapter.getTournamentNumCurrentRounds(getApplicationContext(), tournament_id);

        // Create the String array to represent the round names
        String[] roundNames = new String[numCurrentRounds];
        for(int i = 1; i <= numCurrentRounds; i++) {

            roundNames[i - 1] = "Round " + i;
        }

        // Create the rounds list adapter and set it
        CustomRoundsListViewAdapter adapter = new CustomRoundsListViewAdapter(RoundActivity.this,
                roundNames, tournamentStatus);
        ListView roundsListView = (ListView) findViewById(R.id.roundsListView);
        roundsListView.setAdapter(adapter);

        // Set up on the onClick for the teams list
        roundsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {

                // Get the round id
                int round_id = DBAdapter.getRoundID(getApplicationContext(), pos, tournament_id);

                // Put the information in the intent
                Intent intent = new Intent(getApplicationContext(), MatchesActivity.class);
                intent.putExtra("tournament_id", tournament_id);
                intent.putExtra("round_id", round_id);
                intent.putExtra("roundNum", pos);

                // Start the edit team activity
                startActivity(intent);
            }
        });

        // If we we just saved a match and the round was not complete, go to the matches page
        if(editedRoundNum != -1) {

            // Get the round id
            int round_id = DBAdapter.getRoundID(getApplicationContext(), editedRoundNum, tournament_id);

            // Put the information in the intent
            Intent newIntent = new Intent(getApplicationContext(), MatchesActivity.class);
            newIntent.putExtra("tournament_id", tournament_id);
            newIntent.putExtra("round_id", round_id);
            newIntent.putExtra("roundNum", editedRoundNum);

            // Start the edit team activity
            startActivity(newIntent);
        }
    }

}
