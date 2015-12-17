package adesc062.uottawa.ca.tournamentdesigner.domain;

import android.content.Context;

import java.lang.reflect.Array;
import java.nio.channels.IllegalBlockingModeException;
import java.util.*;

import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class CombinationFormat extends TournamentFormat {

	public	TournamentFormat currentFormat;
    private boolean isKnockout;

	public CombinationFormat(Context context, int tournament_id){

        super(context, tournament_id);

        // Create the Round Robin format
        currentFormat = new RoundRobinFormat(context, tournament_id);

        // If the Round Robin is complete, create the Knockout format
        if(currentFormat.isTournamentComplete(context)) {

            isKnockout = true;
            currentFormat = new KnockoutFormat(context, tournament_id);

            // If the tournament is switching to Knockout
            ArrayList<String> teams = DBAdapter.getTeamNames(context, tournament_id);
            int numTeams = teams.size();
            int circuits = DBAdapter.getTournamentNumRounds(context, tournament_id);
            int numRoundRobinRounds = ((numTeams - 1) + (numTeams%2)) * circuits;
            if(currentRound == numRoundRobinRounds) {

                this.createNextRound(context, tournament_id);
                currentFormat.setJustSwitched(true);
            }
        }
	}

	public boolean isTournamentComplete(Context context){

        ArrayList<String> teams = DBAdapter.getTeamNames(context, tournament_id);
        int numTeams = teams.size();

        // Get the number of rounds to play
        int currentRound = DBAdapter.getCurrentRound(context, tournament_id);
        double logOfTeams = Math.log10(numTeams)/ Math.log10(2);
        int wholeOfLogOfTeam = (int) logOfTeams;
        if(logOfTeams - wholeOfLogOfTeam != 0) {
            wholeOfLogOfTeam++;
        }

        boolean knockoutCompleted = (currentRound >= ((wholeOfLogOfTeam - (numTeams%2))  + ((numTeams - 1) + (numTeams%2))* DBAdapter.getTournamentNumRounds(context, tournament_id))) && checkIsRoundComplete(context);

        if(isKnockout && knockoutCompleted) {

            return true;
        }

        return false;
	}

    public void createNextRound(Context context, int tournament_id) {

        /*
        currentRound = DBAdapter.getCurrentRound(context, tournament_id);
        ArrayList<String> teams = DBAdapter.getTeamNames(context, tournament_id);

        if(currentRound >= teams.size()) { */

        // If the format is Knockout
        if(isKnockout) {

            // Get the list of ordered teams
            orderedTeams = DBAdapter.getFormatOrderedTeams(context, tournament_id);

            // Set the size variable from the parent abstract class, TournamentFormat
            size = orderedTeams.size();

            // Get the current round number
            currentRound = DBAdapter.getCurrentRound(context, tournament_id);
            double logOfTeams = Math.log10(size) / Math.log10(2);
            int wholeOfLogOfTeam = (int) logOfTeams;
            if (logOfTeams - wholeOfLogOfTeam != 0) {
                wholeOfLogOfTeam++;
            }

            // Handling Combination Format
             if(currentRound > orderedTeams.size() - 1) {

                 int numCircuits = DBAdapter.getTournamentNumRounds(context, tournament_id);
                 currentRound = currentRound - (orderedTeams.size() - 1) * numCircuits;
             }

            // If we need to create the current round
            // Calculate the number of teams to remove
            int numberOfTeamsToRemove = 0;
            int tempSize;

            for (int i = 0; i < currentRound; i++) {

                tempSize = size / 2;
                size = size - tempSize;
                numberOfTeamsToRemove += tempSize;
            }

            ArrayList<String> teamNamesArray = DBAdapter.getTeamNames(context, tournament_id);

            ArrayList<Integer> teamWins = new ArrayList<Integer>();
            for (int i = 0; i < teamNamesArray.size(); i++) {

                teamWins.add(i, DBAdapter.getTeamNumWin(context, teamNamesArray.get(i), tournament_id));
            }

            // Sort the arrays based on decreasing number of wins for each team
            ArrayList<Integer> orderedIndexes = new ArrayList<>(); // Used to store the ordered indexes
            for (int j = 0; j < teamNamesArray.size(); j++) {

                int highestValue = Collections.max(teamWins);

                // Iterate through the list of the index of first team with the highest number of wins
                int k = 0;
                while (k < teamWins.size() - 1 && teamWins.get(k) != highestValue) {

                    k++;
                }

                orderedIndexes.add(k);
                teamWins.set(k, -1); // Change it to -1 to avoid getting the same value twice
            }

            // Re-order the team names and the team logos
            ArrayList<String> sortedNamesArray = new ArrayList<>();

            for (int l = 0; l < teamNamesArray.size(); l++) {

                String currentHighestTeamName = teamNamesArray.get(orderedIndexes.get(0));
                orderedIndexes.remove(0);
                sortedNamesArray.add(currentHighestTeamName);
            }
            orderedTeams = new ArrayList<>(sortedNamesArray);

            // Remove the teams
            for (int c = 0; c < numberOfTeamsToRemove; c++) {

                orderedTeams.remove(orderedTeams.size() - 1);
            }

            // Call the create next round method from the superclass
            super.createNextRound(context, tournament_id);
        }
        // If the format is Round Robin
        else {

            currentFormat.createNextRound(context, tournament_id);
        }
        //DBAdapter.incrementCurrentRound(context, DBAdapter.getFormatId(context, tournament_id), currentRound);
    }

    public boolean checkIsRoundComplete(Context context) {

        // Get the list of updated values for the latest round
        currentRound = DBAdapter.getCurrentRound(context, tournament_id) ;
        ArrayList<Integer> matchesUpdatedValues;

        // Get the number of round robin rounds
        int numTeams = DBAdapter.getNumTeamsForTournament(context, tournament_id);
        int numRoundRobinRounds = numTeams * DBAdapter.getTournamentNumRounds(context, tournament_id);

        if(isKnockout) {

            matchesUpdatedValues = DBAdapter.getMatchesUpdatedValues(context, currentRound);
        }
        else {
            matchesUpdatedValues = DBAdapter.getMatchesUpdatedValues(context, currentRound);
        }

        // Go through the list and check if a match has not yet been updated
        for(int i = 0; i < matchesUpdatedValues.size(); i++) {

            if(matchesUpdatedValues.get(i) == 0)
                return false;
        }

        return true;
    }

    public boolean isKnockout() {

        return isKnockout;
    }
}


