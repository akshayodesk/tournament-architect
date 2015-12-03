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

    int tournament_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);

        // Get the tournament id
        tournament_id = getIntent().getIntExtra("tournament_id", 0);

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

        TextView text = (TextView) findViewById(R.id.roundsTextView);
        text.setText(String.valueOf(numCurrentRounds));

        // Create the rounds list adapter and set it
        CustomRoundsListViewAdapter adapter = new CustomRoundsListViewAdapter(RoundActivity.this, roundNames);
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

                // Start the edit team activity
                startActivity(intent);
            }
        });
    }

}
