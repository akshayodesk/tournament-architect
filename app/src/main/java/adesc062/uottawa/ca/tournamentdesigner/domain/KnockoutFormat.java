package adesc062.uottawa.ca.tournamentdesigner.domain;

import android.content.Context;

import java.util.*;

import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class KnockoutFormat extends TournamentFormat {

	public KnockoutFormat(Context context, int tournament_id){

		super(context, tournament_id); //ignore the circuit pass, kept it for polymorphism
	}

	public boolean isTournamentComplete(Context context){

        int numTeams = DBAdapter.getNumTeamsForTournament(context, tournament_id);

        // Get the number of rounds to play
        int currentRound = DBAdapter.getCurrentRound(context, tournament_id);
        double logOfTeams = Math.log10(numTeams)/ Math.log10(2);
        int wholeOfLogOfTeam = (int) logOfTeams;
        if(logOfTeams - wholeOfLogOfTeam != 0) {
            wholeOfLogOfTeam++;
        }

        return (currentRound >= wholeOfLogOfTeam) && checkIsRoundComplete(context);
	}

    public void createNextRound(Context context, int tournament_id) {

        // Get the list of ordered teams
        orderedTeams = DBAdapter.getFormatOrderedTeams(context, tournament_id);

        // Set the size variable from the parent abstract class, TournamentFormat
        size = orderedTeams.size();

        // Get the current round number
        currentRound = DBAdapter.getCurrentRound(context, tournament_id);
        double logOfTeams = Math.log10(size)/ Math.log10(2);
        int wholeOfLogOfTeam = (int) logOfTeams;
        if(logOfTeams - wholeOfLogOfTeam != 0) {
            wholeOfLogOfTeam++;
        }

        // Handling Combination Format
        /*
        if(currentRound > orderedTeams.size() - 1) {

            int numCircuits = DBAdapter.getTournamentNumRounds(context, tournament_id);
            currentRound = currentRound - (orderedTeams.size() - 2) * numCircuits;
        } */

        // If we need to create the current round
        if(currentRound > 0 ) {

            // Calculate the number of teams to remove
            int numberOfTeamsToRemove = 0;
            int tempSize;

            for(int i = 0; i < currentRound; i++) {

                tempSize = size/ 2;
                size = size - tempSize;
                numberOfTeamsToRemove += tempSize;
            }

            ArrayList<String> teamNamesArray = DBAdapter.getTeamNames(context, tournament_id);

            ArrayList<Integer> teamWins = new ArrayList<Integer>();
            for(int i = 0; i < teamNamesArray.size(); i++) {

                teamWins.add(i, DBAdapter.getTeamNumWin(context, teamNamesArray.get(i), tournament_id));
            }

            // Sort the arrays based on decreasing number of wins for each team
            ArrayList<Integer> orderedIndexes = new ArrayList<>(); // Used to store the ordered indexes
            for(int j = 0; j < teamNamesArray.size(); j++) {

                int highestValue = Collections.max(teamWins);

                // Iterate through the list of the index of first team with the highest number of wins
                int k = 0;
                while(k < teamWins.size() - 1 && teamWins.get(k) != highestValue) {

                    k++;
                }

                orderedIndexes.add(k);
                teamWins.set(k, -1); // Change it to -1 to avoid getting the same value twice
            }

            // Re-order the team names and the team logos
            ArrayList<String> sortedNamesArray = new ArrayList<>();

            for(int l = 0; l < teamNamesArray.size(); l++) {

                String currentHighestTeamName = teamNamesArray.get(orderedIndexes.get(0));
                orderedIndexes.remove(0);
                sortedNamesArray.add(currentHighestTeamName);
            }
            orderedTeams = new ArrayList<>(sortedNamesArray);

            // Remove the teams
            for(int c = 0; c < numberOfTeamsToRemove; c++){

                orderedTeams.remove(orderedTeams.size()-1);
            }
        }

        // Call the create next round method from the superclass
        super.createNextRound(context, tournament_id);
    }
}
