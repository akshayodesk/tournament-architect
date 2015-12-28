package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import adesc062.uottawa.ca.tournamentdesigner.R;
import adesc062.uottawa.ca.tournamentdesigner.adapters.CustomRoundsListViewAdapter;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class RoundActivity extends Activity {

    private int tournament_id;
    private int editedRoundNum;
    private int tournamentStatus;
    private int tournamentFormat;
    private ArrayList<String> roundNames = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);

        // Get the tournament info
        Intent intent = getIntent();
        tournament_id = intent.getIntExtra("tournament_id", 0);
        editedRoundNum = intent.getIntExtra("editedRoundNum", -1);
        tournamentStatus = DBAdapter.getTournamentStatus(getApplicationContext(), tournament_id);
        tournamentFormat = DBAdapter.getTournamentFormatType(getApplicationContext(), tournament_id);

        // Set up the list of rounds
        updateRounds();
    }

    private void updateRounds() {

        // Get the number of rounds currently available
        int numCurrentRounds = DBAdapter.getTournamentNumCurrentRound(getApplicationContext(), tournament_id);

        // Get the number of teams
        int numTeams = DBAdapter.getNumTeamsForTournament(getApplicationContext(), tournament_id);

        /* Create the String array to represent the round names */
        // If the format is Round Robin
        if (tournamentFormat == 1) {

            // Find the number of rounds per circuit
            int numRoundsPerCircuit = numTeams - 1 + numTeams%2;

            int currentCircuit = 1; // The current circuit in the algorithm
            int numRoundsLeft = numCurrentRounds; // The number of rounds left to name

            //Get the number of circuits
            int numCircuits = DBAdapter.getTournamentNumCircuits(getApplicationContext(), tournament_id);

            // If there is only one circuit, do not include circuit number
            if (numCircuits == 1) {

                for (int round = 1; round <= numRoundsLeft; round++) {

                    roundNames.add("Round " + round);
                }
            }
            // If there is more than one circuit, include the circuit number
            else {
                // Name the rounds
                while (currentCircuit - 1 * numRoundsPerCircuit <= numCurrentRounds) {

                    for (int round = 1; round <= numRoundsPerCircuit; round++) {

                        if (numRoundsLeft > 0) {

                            roundNames.add("Round " + round + " - Circuit " + currentCircuit);
                            numRoundsLeft--;
                        }
                    }

                    currentCircuit++;
                }
            }
        }
        // If the format is Knockout
        else if (tournamentFormat == 2) {

            // Get the number of rounds that will be played
            double logOfTeams = Math.log10(numTeams)/ Math.log10(2);
            int wholeOfLogOfTeam = (int) logOfTeams;
            if(logOfTeams - wholeOfLogOfTeam != 0) {
                wholeOfLogOfTeam++;
            }

            // Variables used to name the rounds
            int numberOfRoundsToPlay = wholeOfLogOfTeam - 1; // The number of rounds that will be played
            int nameNumber = (int) Math.pow(2, numberOfRoundsToPlay); // The number used to name each round
            String roundName;

            // Name the rounds
             for (int i = 1; i <= numCurrentRounds; i++) {

                 // If the round is the final
                 if (nameNumber == 1) {

                     roundName = "Final";
                 }
                 // If the round is the semi-finals
                 else if (nameNumber == 2) {

                     roundName = "Semi-finals";
                 }
                 // If the round is the quarter-finals
                 else if (nameNumber == 4) {

                     roundName = "Quarter-finals";
                 }
                 // If the round is the eighth-finals
                 else if (nameNumber == 8) {

                     roundName = "Eighth-finals";
                 }
                // If the round number ends with a 1
                 else if (nameNumber%10 == 1) {

                     roundName = nameNumber + "st-finals";
                 }
                // If the round number ends with a 2
                 else if (nameNumber%10 == 2) {

                     roundName = nameNumber + "nd-finals";
                 }
                 // If the round number ends with a 3
                 else if (nameNumber%10 == 3) {

                     roundName = nameNumber + "rd-finals";
                 }
                 // If the round name ends with something else
                 else {

                     roundName = nameNumber + "th-finals";
                 }

                 roundNames.add(roundNames.size(), roundName);

                 nameNumber = nameNumber / 2;
             }
        }
        // If the format is combination
        else {

            /* Name the Round Robin rounds */
            // Find the number of rounds per circuit
            int numRoundsPerCircuit = numTeams - 1 + numTeams%2;

            // Get the number of circuits for the tournament
            int numCircuits = DBAdapter.getTournamentNumCircuits(getApplicationContext(), tournament_id);

            // Find the number of Round Robin rounds that will be played
            int numTotalRoundRobinRounds = numRoundsPerCircuit * numCircuits;

            int currentCircuit = 1; // The current circuit in the algorithm
            int numRoundsLeft = numCurrentRounds; // The number of rounds left to name

            // If there is only one circuit, do not include circuit number
            if (numCircuits == 1) {

                for (int round = 1; round <= numTotalRoundRobinRounds && round <= numRoundsLeft; round++) {

                    roundNames.add("Round " + round);
                }
            }
            // If there is more than one circuit, include the circuit number
            else {
                while (currentCircuit - 1 * numRoundsPerCircuit <= numCurrentRounds &&
                        currentCircuit * numRoundsPerCircuit <= numTotalRoundRobinRounds) {

                    for (int round = 1; round <= numRoundsPerCircuit; round++) {

                        if (numRoundsLeft > 0) {

                            roundNames.add("Round " + round + " - Circuit " + currentCircuit);
                            numRoundsLeft--;
                        }
                    }

                    currentCircuit++;
                }
            }

            // Calculate the number of Knockout rounds that will be played
            double logOfTeams = Math.log10(numTeams)/ Math.log10(2);
            int numKnockoutRounds = (int) logOfTeams;
            if(logOfTeams - numKnockoutRounds != 0) {
                numKnockoutRounds++;
            }

            // Variables used to name the rounds
            int nameNumber = (int) Math.pow(2, numKnockoutRounds - 1); // The number used to name each round
            String roundName;

            // Name the rounds
            for (int i = numTotalRoundRobinRounds + 1; i <= numCurrentRounds; i++) {

                // If the round is the final
                if (nameNumber == 1) {

                    roundName = "Final";
                }
                // If the round is the semi-finals
                else if (nameNumber == 2) {

                    roundName = "Semi-finals";
                }
                // If the round is the quarter-finals
                else if (nameNumber == 4) {

                    roundName = "Quarter-finals";
                }
                // If the round is the eighth-finals
                else if (nameNumber == 8) {

                    roundName = "Eighth-finals";
                }
                // If the round number ends with a 1
                else if (nameNumber%10 == 1) {

                    roundName = nameNumber + "st-finals";
                }
                // If the round number ends with a 2
                else if (nameNumber%10 == 2) {

                    roundName = nameNumber + "nd-finals";
                }
                // If the round number ends with a 3
                else if (nameNumber%10 == 3) {

                    roundName = nameNumber + "rd-finals";
                }
                // If the round name ends with something else
                else {

                    roundName = nameNumber + "th-finals";
                }

                roundNames.add(roundNames.size(), roundName);

                nameNumber = nameNumber / 2;
            }
        }

        // Convert the teams names ArrayList to a String Array
        String[] roundNamesStringArray = new String[roundNames.size()];
        for(int i = 0; i < roundNames.size(); i++) {

            roundNamesStringArray[i] = roundNames.get(i);
        }

        // Create the rounds list adapter and set it
        CustomRoundsListViewAdapter adapter = new CustomRoundsListViewAdapter(RoundActivity.this,
                roundNamesStringArray, tournamentStatus);
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
