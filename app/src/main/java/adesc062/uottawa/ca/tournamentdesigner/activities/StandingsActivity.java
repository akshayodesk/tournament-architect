package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import adesc062.uottawa.ca.tournamentdesigner.R;
import adesc062.uottawa.ca.tournamentdesigner.adapters.CustomTeamsListViewNotClickableWithScoreAdapter;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;
import adesc062.uottawa.ca.tournamentdesigner.domain.CombinationFormat;
import adesc062.uottawa.ca.tournamentdesigner.domain.KnockoutFormat;
import adesc062.uottawa.ca.tournamentdesigner.domain.RoundRobinFormat;
import adesc062.uottawa.ca.tournamentdesigner.domain.TournamentFormat;

public class StandingsActivity extends Activity {
    int tournament_id;
    int status;
    int formatType;
    TournamentFormat format;
    ArrayList<String> unsortedTeams;
    String[] teamNames;
    Integer[] teamLogos;
    Integer[] teamScoresForDisplay;
    ListView standings;
    boolean roundWasComplete = false;
    boolean tournamentWasComplete = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);
        // Get the tournament id
        tournament_id = getIntent().getIntExtra("tournament_id", 0);
        // Get the tournament status
        status = DBAdapter.getTournamentStatus(getApplicationContext(), tournament_id);
        // Get the list of teams
        unsortedTeams = DBAdapter.getTeamNames(getApplicationContext(), tournament_id);
        // Get the tournament format type
        formatType = DBAdapter.getTournamentFormatType(getApplicationContext(), tournament_id);
        // Get the number of rounds
        int numRounds = DBAdapter.getTournamentNumCircuits(getApplicationContext(), tournament_id);
        /* Set up the format type */
        // If the format type is Round Robin
        if(formatType == 1) {
            format = new RoundRobinFormat(getApplicationContext(), tournament_id);
        }
        // If the format type is Knockout
        else if(formatType == 2) {
            format = new KnockoutFormat(getApplicationContext(), tournament_id);
        }
        // If the format type is Combination
        else {
            format = new CombinationFormat(getApplicationContext(), tournament_id);
            formatType = 3;
        }
        // If the tournament has just been started
        if(status == 1) {
            format.setUpFormat(getApplicationContext(), tournament_id, numRounds, unsortedTeams);
            format.createNextRound(getApplicationContext(), tournament_id);
            // Change the status of the tournament to started
            DBAdapter.updateTournamentStatus(getApplicationContext(), tournament_id, 2);
        }
        // If the tournament had already been started
        else {
            format.setCurrentRound(getApplicationContext());
        }
        // Update the standings
        updateStandings();
        // Check if the tournament is complete
        if(format.isTournamentComplete(getApplicationContext())) {
            // Get the list of winners and display them
            String[] winners = getWinners();
            displayWinners(winners);
            if(format.getJustSwitched() == false) {
                DBAdapter.updateTournamentStatus(getApplicationContext(), tournament_id, 3);
                tournamentWasComplete = true;
            }
        }
        // Determine if a new round must be generated
        else if(format.checkIsRoundComplete(getApplicationContext())) {
            // Create the next round
            format.createNextRound(getApplicationContext(), tournament_id);
            roundWasComplete = true;
        }
        // If we we just saved a match and the tournament is not complete, go to the rounds page
        if(!tournamentWasComplete && getIntent().hasExtra("editedRoundNum")) {
            int editedRoundNum = getIntent().getIntExtra("editedRoundNum", -2);
            // Open the rounds page
            Intent newIntent = new Intent(this, RoundActivity.class);
            newIntent.putExtra("tournament_id", tournament_id);
            boolean skip = false;
            if(formatType == 3) {

                skip = ((CombinationFormat) format).currentFormat.getJustSwitched();
            }
            // If the round was not complete, specify the round to travel to
            if(!roundWasComplete && !skip)
                newIntent.putExtra("editedRoundNum", editedRoundNum);

            startActivity(newIntent);
        }
    }

    public void viewRoundsOnClick(View view) {
        Intent intent = new Intent(this, RoundActivity.class);
        intent.putExtra("tournament_id", tournament_id);
        startActivity(intent);
    }

    public void deleteTournamentOnClick(View view) {
        // Ask the user for confirmation on deleting the tournament
        // Pop up a dialog
        final Dialog alertConfirmDeletion = new Dialog(StandingsActivity.this);
        alertConfirmDeletion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertConfirmDeletion.setContentView(R.layout.custom_alert_yes_not);
        Button yesDeleteButton = (Button) alertConfirmDeletion.findViewById(R.id.yesDeleteButon);
        Button noDeleteButton = (Button) alertConfirmDeletion.findViewById(R.id.noDeleteButon);
        yesDeleteButton.setOnClickListener(new View.OnClickListener() {

            // Delete the tournament and go
            public void onClick(View v) {
                // If we came from the Load Tournament Activity
                if(getIntent().hasExtra("cameFromLoadActivity")){
                    alertConfirmDeletion.dismiss();
                    Intent intent = new Intent(getApplicationContext(), LoadTournamentActivity.class);
                    finish();
                    startActivity(intent);
                    // If we came from the Home Activity
                } else {
                    alertConfirmDeletion.dismiss();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    finish();
                    startActivity(intent);
                }
                DBAdapter.deleteTournament(getApplicationContext(), tournament_id);
            }
        });
        noDeleteButton.setOnClickListener(new View.OnClickListener() {

            // Dismiss the alert
            public void onClick(View v) {

                alertConfirmDeletion.dismiss();
            }
        });
        alertConfirmDeletion.show();
    }

    private void updateStandings() {
        // Get the team names, the team logos and number of wins for each team from the database
        ArrayList<String> teamNamesArray = DBAdapter.getTeamNames(this.getApplicationContext(), tournament_id);
        ArrayList<String> teamLogosArray = DBAdapter.getTeamLogos(this.getApplicationContext(), tournament_id);
        /* Get the corresponding score to order teams */
        ArrayList<Integer> teamScores = new ArrayList<Integer>();
        // If the format is Round Robin, order the teams by score
        if (formatType == 1) {
            // Get the score for each team
            for(int i = 0; i < teamNamesArray.size(); i++) {

                int teamID = DBAdapter.getTeamId(getApplicationContext(), teamNamesArray.get(i), tournament_id);
                teamScores.add(i, DBAdapter.getTeamScore(getApplicationContext(), teamID));
            }
        }
        // If the format is Knockout, order the teams by wins
        else if (formatType == 2) {
            // Get the number of wins for each team
            for (int i = 0; i < teamNamesArray.size(); i++) {
                teamScores.add(i, DBAdapter.getTeamNumWins(getApplicationContext(), teamNamesArray.get(i), tournament_id));
            }
        }
        // If the format is Combination, order the teams by score in the Round Robin portion
        // and by number of wins in the Knockout portion
        else {
            // If the tournament is currently in the Round Robin, order the teams by score
            if(!((CombinationFormat) format).getIsKnockout()) {
                // Get the score for each team
                for(int i = 0; i < teamNamesArray.size(); i++) {
                    int teamID = DBAdapter.getTeamId(getApplicationContext(), teamNamesArray.get(i), tournament_id);
                    teamScores.add(i, DBAdapter.getTeamScore(getApplicationContext(), teamID));
                }
            }
            // If the tournament is currently in the Knockout, order the teams by wins in the Knockout
            else {
                // Get the number of wins for each team
                for (int i = 0; i < teamNamesArray.size(); i++) {
                    teamScores.add(DBAdapter.getTeamNumKnockoutWins(getApplicationContext(), teamNamesArray.get(i), tournament_id));
                }
            }
        }
        // Sort the arrays based on decreasing score for each team
        ArrayList<Integer> orderedIndexes = new ArrayList<>(); // Used to store the ordered indexes
        for(int j = 0; j < teamNamesArray.size(); j++) {
            int highestValue = Collections.max(teamScores);
            // Iterate through the list of the index of first team with the highest number of wins
            int k = 0;
            while(k < teamScores.size() - 1 && teamScores.get(k) != highestValue) {
                k++;
            }
            orderedIndexes.add(k);
            teamScores.set(k, -1); // Change it to -1 to avoid getting the same value twice
        }
        // Re-order the team names and the team logos
        ArrayList<String> sortedNamesArray = new ArrayList<>();
        ArrayList<String> sortedLogosArray = new ArrayList<>();
        for(int l = 0; l < teamNamesArray.size(); l++) {
            String currentHighestTeamName = teamNamesArray.get(orderedIndexes.get(0));
            String currentHighestTeamLogo = teamLogosArray.get(orderedIndexes.get(0));
            orderedIndexes.remove(0);
            sortedNamesArray.add(currentHighestTeamName);
            sortedLogosArray.add(currentHighestTeamLogo);
        }
        /* Get the score for each team */
        ArrayList<Integer> sortedScores = new ArrayList<Integer>();
        // If the format is Round Robin, order the teams by score
        if (formatType == 1) {
            // Get the score for each team
            for(int i = 0; i < sortedNamesArray.size(); i++) {
                int teamID = DBAdapter.getTeamId(getApplicationContext(), sortedNamesArray.get(i), tournament_id);
                sortedScores.add(i, DBAdapter.getTeamScore(getApplicationContext(), teamID));
            }
        }
        // If the format is Knockout, order the teams by wins
        else if (formatType == 2) {
            // Get the number of wins for each team
            for (int i = 0; i < sortedNamesArray.size(); i++) {
                sortedScores.add(i, DBAdapter.getTeamNumWins(getApplicationContext(), sortedNamesArray.get(i), tournament_id));
            }
        }
        // If the format is Combination, order the teams by score in the Round Robin portion
        // and by number of wins in the Knockout portion
        else {
            // If the tournament is currently in the Round Robin, order the teams by score
            if(!((CombinationFormat) format).getIsKnockout()) {
                // Get the score for each team
                for(int i = 0; i < sortedNamesArray.size(); i++) {
                    int teamID = DBAdapter.getTeamId(getApplicationContext(), sortedNamesArray.get(i), tournament_id);
                    sortedScores.add(i, DBAdapter.getTeamScore(getApplicationContext(), teamID));
                }
            }
            // If the tournament is currently in the Knockout, order the teams by wins in the Knockout
            else {
                // Get the number of wins for each team
                for (int i = 0; i < sortedNamesArray.size(); i++) {
                    sortedScores.add(DBAdapter.getTeamNumKnockoutWins(getApplicationContext(), sortedNamesArray.get(i), tournament_id));
                }
            }
        }
        // Convert the team names to a String array
        teamNames = new String[sortedNamesArray.size()];
        teamNames = sortedNamesArray.toArray(teamNames);
        // Convert the team logos to an integer array of the resource ids
        teamLogos = new Integer[sortedLogosArray.size()];
        for(int n = 0; n < teamLogos.length; n++){
            teamLogos[n] = this.getResources().getIdentifier(sortedLogosArray.get(n), "drawable", this.getPackageName());
        }
        // Convert the score for each team to an integer array
        teamScoresForDisplay = new Integer[sortedScores.size()];
        for(int m = 0; m < teamScoresForDisplay.length; m++) {
            teamScoresForDisplay[m] = sortedScores.get(m);
        }
        // If the format is Combination, get the actual current format type
        int actualFormatType = formatType;
        if (formatType == 3 ) {
            // Get the current round
            int currentRound = DBAdapter.getCurrentRound(getApplicationContext(), tournament_id);
            // Determine the number of rounds
            ArrayList<String> teams = DBAdapter.getTeamNames(getApplicationContext(), tournament_id);
            int numTeams = teams.size();
            // Determine the number of Round Robin rounds
            // Find the number of rounds per circuit
            int numRoundsPerCircuit = numTeams - 1 + numTeams%2;
            // Get the number of circuits for the tournament
            int numCircuits = DBAdapter.getTournamentNumCircuits(getApplicationContext(), tournament_id);
            // Find the number of Round Robin rounds that will be played
            int numTotalRoundRobinRounds = numRoundsPerCircuit * numCircuits;
            if(currentRound >= numTotalRoundRobinRounds)
                actualFormatType = 2;
            else
                actualFormatType = 1;
        }
        // Create the teams list adapter and set it
        CustomTeamsListViewNotClickableWithScoreAdapter adapter = new CustomTeamsListViewNotClickableWithScoreAdapter(StandingsActivity.this, actualFormatType,
                teamNames, teamLogos, teamScoresForDisplay, tournament_id);
        standings = (ListView) findViewById(R.id.standingsListView);
        standings.setAdapter(adapter);
    }

    public String[] getWinners() {
        // Get the team names
        ArrayList<String> teamNamesArray = DBAdapter.getTeamNames(this.getApplicationContext(), tournament_id);
        // Initialize the list that will hold the scores of all the teams
        ArrayList<Integer> teamScores = new ArrayList<Integer>();
        // If the format is Round Robin, order the teams by score
        if (formatType == 1) {
            // Get the score for each team
            for(int i = 0; i < teamNamesArray.size(); i++) {
                int teamID = DBAdapter.getTeamId(getApplicationContext(), teamNamesArray.get(i), tournament_id);
                teamScores.add(i, DBAdapter.getTeamScore(getApplicationContext(), teamID));
            }
        }
        // If the format is Knockout, order the teams by wins
        else if (formatType == 2) {
            // Get the number of wins for each team
            for (int i = 0; i < teamNamesArray.size(); i++) {
                teamScores.add(DBAdapter.getTeamNumWins(getApplicationContext(), teamNamesArray.get(i), tournament_id));
            }
        }
        // If the format is Combination, order the teams by wins in Knockout
        else {
            // Get the number of wins for each team
            for (int i = 0; i < teamNamesArray.size(); i++) {
                teamScores.add(DBAdapter.getTeamNumKnockoutWins(getApplicationContext(), teamNamesArray.get(i), tournament_id));
            }
        }
        // Only keep the highest score teams
        int maxWins = Collections.max(teamScores);
        ArrayList<String> sortedNamesArray = new ArrayList<>();
        for (int g = 0; g < teamScores.size(); g++) {
            if(teamScores.get(g) == maxWins) {
                String currentHighestTeamName = teamNamesArray.get(g);
                teamScores.remove(0);
                sortedNamesArray.add(currentHighestTeamName);
            }
        }
        // Convert the team names to a String array
        teamNames = new String[sortedNamesArray.size()];
        teamNames = sortedNamesArray.toArray(teamNames);
        // Return the array of winners
        return teamNames;
    }

    private void displayWinners(String[] winners) {
        // Pop up a dialog
        final Dialog alertWinners = new Dialog(StandingsActivity.this);
        alertWinners.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertWinners.setContentView(R.layout.custom_alert_ok);
        /* Create the message */
        String message = "Congratulations!" + "\n" + "\n";
        // If only one team won
        if(winners.length == 1) {
            message = message + "The winner is " + winners[0] + "!";
        }
        // If multiple teams won
        else {
            message = message + "The winners of the tournament are ";
            for (int i = 0; i < winners.length - 1; i++) {
                message = message + winners[i] + ", ";
            }
            message = message + "and " + winners[winners.length - 1] + "!";
        }
        // Set the message
        TextView winnersTextView = (TextView) alertWinners.findViewById(R.id.messageOkTextView);
        winnersTextView.setText(message);
        Button okButton = (Button) alertWinners.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertWinners.dismiss();
            }
        });
        alertWinners.show();
    }

    public void onBackPressed() {
        // Go back to the home page and finish this activity
        Intent intent = new Intent(this, LoadTournamentActivity.class);
        startActivity(intent);
        finish();
    }
}
