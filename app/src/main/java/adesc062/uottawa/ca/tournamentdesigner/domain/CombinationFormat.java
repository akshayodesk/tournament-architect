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
            int circuits = DBAdapter.getTournamentNumCircuits(context, tournament_id);
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

        boolean knockoutCompleted = currentRound >= (wholeOfLogOfTeam - (numTeams%2)) && checkIsRoundComplete(context);

        if(isKnockout && knockoutCompleted) {

            return true;
        }

        return false;
	}

    public void createNextRound(Context context, int tournament_id) {

        // If the format is Knockout
        if (isKnockout) {

            // Get the list of ordered teams
            orderedTeams = DBAdapter.getFormatOrderedTeams(context, tournament_id);

            // Set the size variable from the parent abstract class, TournamentFormat
            size = orderedTeams.size();

            // Get the current round number
            currentRound = DBAdapter.getCurrentRound(context, tournament_id);

            // Handling switch from Round Robin to Knockout
             if(currentRound >= (orderedTeams.size() - 1) * DBAdapter.getTournamentNumCircuits(context, tournament_id)) {

                 currentRound = 1;
             }

            // Calculate the number of teams to remove
            int numberOfTeamsToRemove = 0;
            int tempSize;
            for (int i = 0; i < currentRound; i++) {

                tempSize = size / 2;
                size = size - tempSize;
                numberOfTeamsToRemove += tempSize;
            }

            // Get the list of team names
            ArrayList<String> teamNamesArray = DBAdapter.getTeamNames(context, tournament_id);

            // Get the number of wins for each team
            ArrayList<Integer> teamWins = new ArrayList<Integer>();
            for (int i = 0; i < teamNamesArray.size(); i++) {

                teamWins.add(i, DBAdapter.getTeamNumWins(context, teamNamesArray.get(i), tournament_id));
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

            // Cut the teams that were lower-ranked
            for (int c = 0; c < numberOfTeamsToRemove; c++) {

                orderedTeams.remove(orderedTeams.size() - 1);
            }

            // Now, create the matches
            // Initialize variables
            int format_id = DBAdapter.getFormatId(context, tournament_id);
            currentRound = DBAdapter.getCurrentRound(context, tournament_id);

            // Create the round
            DBAdapter.insertRound(context, tournament_id, size, format_id);

            // See algorithm: http://assets.usta.com/assets/650/USTA_Import/Northern/dps/doc_37_1812.pdf

            // Matches consist of left vs right
            ArrayList<String> leftTeams= new ArrayList<String>();
            ArrayList<String> rightTeams= new ArrayList<String>();

            // If Number of teams is ODD, add a flag for the bye match
            if(orderedTeams.size()%2 == 1) {

                orderedTeams.add("BYE");
            }

            // Remove first teamName
            leftTeams.add(orderedTeams.remove(0));

            // Shift remaining teams counter-clockwise according to round (i.e.: first round 0 times, second 1 time...)
            // Removes last element and adds to front
            int last = orderedTeams.size() - 1;
            for(int c = 0; c < currentRound; c++ ){

                String temp = orderedTeams.remove(last);
                orderedTeams.add(0,temp);
            }

            // Split teams into left and right (except for last team as teams will be oddNUmber at this point)
            int counter = (orderedTeams.size()/2);
            for(int c = 0; c < (counter);c++){

                // Remove first
                String tempRightTeam= orderedTeams.remove(0);

                // Remove first
                String tempLeftTeam = orderedTeams.remove(0);

                // Adjusting for BYE (i.e.: in a BYE, team wins against self (cannot use bye as team name since it would require a full team object in DB)
                if (tempLeftTeam.equals("BYE")) {

                    tempLeftTeam = orderedTeams.get(0);
                }
                else if (tempRightTeam.equals("BYE")) {

                    tempRightTeam = leftTeams.get(leftTeams.size() - 1);
                }

                rightTeams.add(tempRightTeam);
                leftTeams.add(tempLeftTeam);
            }

            String tempRightTeam = orderedTeams.remove(0);

            //Add the remaining to to rightTeams
            //BYE
            if (tempRightTeam.equals("BYE")) {

                rightTeams.add(leftTeams.get(leftTeams.size()-1));
            }
            else {
                rightTeams.add(tempRightTeam);
            }

            // Teams are now split, now must create the match between them
            int numOfMatches = leftTeams.size();

            // Set up every round
            for(int c = 0; c < leftTeams.size(); c++) {

                String leftTeam = leftTeams.get(c);
                String rightTeam = rightTeams.get(c);

                Match matchTemp = new Match(context, DBAdapter.getRoundID(context, currentRound, tournament_id), tournament_id, leftTeam, rightTeam);
            }

            // Increment the current round
            currentRound = currentRound + 1;
            DBAdapter.setCurrentRound(context, format_id, currentRound);

            // Call the create next round method from the superclass
            super.createNextRound(context, tournament_id);
        }
        // If the format is Round Robin
        else {

            currentFormat.createNextRound(context, tournament_id);
        }
    }

    public boolean checkIsRoundComplete(Context context) {

        // Get the list of updated values for the latest round
        int currentRoundID = DBAdapter.getCurrentRoundID(context, tournament_id) ;
        ArrayList<Integer> matchesUpdatedValues;

        // Get the number of round robin rounds
        int numTeams = DBAdapter.getNumTeamsForTournament(context, tournament_id);

        matchesUpdatedValues = DBAdapter.getMatchesUpdatedValues(context, currentRoundID, tournament_id);

        // Go through the list and check if a match has not yet been updated
        for(int i = 0; i < matchesUpdatedValues.size(); i++) {

            if(matchesUpdatedValues.get(i) == 0)
                return false;
        }

        return true;
    }

    public boolean getIsKnockout() {

        return isKnockout;
    }
}


