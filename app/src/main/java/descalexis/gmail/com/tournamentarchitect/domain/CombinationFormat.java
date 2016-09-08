package descalexis.gmail.com.tournamentarchitect.domain;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;

import descalexis.gmail.com.tournamentarchitect.database.DBAdapter;

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

                justSwitched = true;
                this.createNextRound(context, tournament_id);
                currentFormat.setJustSwitched(true);
            }
        }
	}

	public boolean isTournamentComplete(Context context){

        ArrayList<String> teams = DBAdapter.getTeamNames(context, tournament_id);
        int numTeams = teams.size();

        // Determine the number of Round Robin rounds
        // Find the number of rounds per circuit
        int numRoundsPerCircuit = numTeams - 1 + numTeams%2;
        // Get the number of circuits for the tournament
        int numCircuits = DBAdapter.getTournamentNumCircuits(context, tournament_id);
        // Find the number of Round Robin rounds that will be played
        int numTotalRoundRobinRounds = numRoundsPerCircuit * numCircuits;

        // Determine the number of Knockout rounds
        int currentRound = DBAdapter.getCurrentRound(context, tournament_id);
        double logOfTeams = Math.log10(numTeams)/ Math.log10(2);
        int numTotalKnockoutRounds = (int) logOfTeams;
        if(logOfTeams - numTotalKnockoutRounds != 0) {
            numTotalKnockoutRounds++;
        }

        boolean knockoutCompleted = currentRound >= (numTotalRoundRobinRounds + numTotalKnockoutRounds)
                && checkIsRoundComplete(context);

        return isKnockout && knockoutCompleted;

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

            // If this is the first Knockout round to be created
            if (justSwitched) {

                /* We must order the teams based by score from the
                   Round Robin, and then match up the best teams
                   against the worst teams
                 */

                // Get the score for each team
                ArrayList<Integer> teamWins = new ArrayList<>();
                for (int i = 0; i < orderedTeams.size(); i++) {

                    int teamID = DBAdapter.getTeamId(context, orderedTeams.get(i), tournament_id);
                    teamWins.add(i, DBAdapter.getTeamScore(context, teamID));
                }

                // Sort the list based on decreasing score for each team
                ArrayList<Integer> orderedIndexes = new ArrayList<>(); // Used to store the ordered indexes
                for (int j = 0; j < orderedTeams.size(); j++) {

                    int highestValue = Collections.max(teamWins);

                    // Iterate through the list of the index of first team with the highest number of wins
                    int k = 0;
                    while (k < teamWins.size() - 1 && teamWins.get(k) != highestValue) {

                        k++;
                    }

                    orderedIndexes.add(k);
                    teamWins.set(k, -1); // Change it to -1 to avoid getting the same value twice
                }

                // Re-order the team names
                ArrayList<String> sortedNamesArray = new ArrayList<>();
                for (int l = 0; l < orderedTeams.size(); l++) {

                    String currentHighestTeamName = orderedTeams.get(orderedIndexes.get(0));
                    orderedIndexes.remove(0);
                    sortedNamesArray.add(currentHighestTeamName);
                }
                orderedTeams = new ArrayList<>(sortedNamesArray);


                // If the number of teams is odd, give a bye to the best team
                if (orderedTeams.size()%2 == 1) {

                    orderedTeams.add(orderedTeams.get(0));
                }

                // Now set pairs of the best team against the worst team, etc
                for (int i = 0; i < orderedTeams.size(); i = i + 2) {

                    // Save the 2nd best team
                    String tempTeam = orderedTeams.get(i + 1);

                    // Put the worst team right after the best team
                    orderedTeams.set(i + 1, orderedTeams.get(orderedTeams.size() - 1));

                    // Put the 2nd best team back in
                    orderedTeams.add(i + 2, tempTeam);

                    // Remove the extra worst team
                    orderedTeams.remove(orderedTeams.size() - 1);
                }

                // Now order the pairs so that the best team never meets
                // the 2nd best team before the finals

                // Split the teams in upper and lower brackets
                ArrayList<String> upperBracket = new ArrayList<>();
                ArrayList<String> lowerBracket = new ArrayList<>();
                for(int i = 0; i < orderedTeams.size() / 2; i++) {

                    // Put two teams in the upper bracket
                    upperBracket.add(orderedTeams.get(0));
                    upperBracket.add(orderedTeams.get(1));

                    // Remove those two teams
                    orderedTeams.remove(0);
                    orderedTeams.remove(0);

                    // Put two teams in the lower bracket if size allows it
                    if(!orderedTeams.isEmpty()) {

                        lowerBracket.add(orderedTeams.get(0));
                        lowerBracket.add(orderedTeams.get(1));

                        // Remove those two teams
                        orderedTeams.remove(0);
                        orderedTeams.remove(0);
                    }
                }

                // Join the two brackets
                for(int i = 0; i < upperBracket.size(); i++)
                    orderedTeams.add(upperBracket.get(i));
                for (int i = 0; i < lowerBracket.size(); i++)
                    orderedTeams.add(lowerBracket.get(i));

                // Set the new format positions in the database
                for(int i = 0; i < orderedTeams.size(); i++) {

                    DBAdapter.setTeamFormatPosition(context, tournament_id, orderedTeams.get(i), i + 1);
                }

            }
            // If this is not the first Knockout round to be created
            else {

                // Remove teams that have been eliminated
                ArrayList<String> competingTeams = new ArrayList<>(orderedTeams);
                for (int i = competingTeams.size() - 1; i >= 0; i--) {

                    // Get the format position for the team
                    int formatPosition = DBAdapter.getTeamFormatPosition(context, competingTeams.get(i), tournament_id);

                    // If the format position is -1, the team has been eliminated, so remove it
                    if (formatPosition == -1)
                        competingTeams.remove(i);
                }

                // Get the number of wins for each team
                ArrayList<Integer> teamWins = new ArrayList<Integer>();
                for (int i = 0; i < competingTeams.size(); i++) {

                    teamWins.add(i, DBAdapter.getTeamNumKnockoutWins(context, competingTeams.get(i), tournament_id));
                }

                // Determine the winners of the previous rounds that will advance
                ArrayList<String> winnerTeams = new ArrayList<>();
                boolean gotBye = false; // Boolean used to check that we had a bye
                for (int v = 0; v < competingTeams.size() - 1; v = v + 2) {

                    // If two succeeding teams have the same the number of wins,
                    // then one had a bye, so add it
                    if (teamWins.get(v) == teamWins.get(v + 1)) {

                        winnerTeams.add(competingTeams.get(v));
                        v--; // Because we only covered one team
                        gotBye = true;
                    }
                    // If the first team won, add it
                    else if (teamWins.get(v) > teamWins.get(v + 1))
                        winnerTeams.add(competingTeams.get(v));
                        // Otherwise, add the second team
                    else
                        winnerTeams.add(competingTeams.get(v + 1));
                }

                // If the number of teams is odd and we did not see a bye,
                // then the last team must have had a bye, so add it
                if (!gotBye && competingTeams.size()%2 == 1)
                    winnerTeams.add(competingTeams.get(competingTeams.size() - 1));

                // Remove the old format positions
                DBAdapter.removeFormatPositions(context, tournament_id);

                // Set the new format positions for the winning teams
                for(int i = 0; i < winnerTeams.size(); i++) {

                    DBAdapter.setTeamFormatPosition(context, tournament_id, winnerTeams.get(i), i + 1);
                }

                // Set the ordered teams to be the determined winners
                orderedTeams = winnerTeams;
            }

            // Get the format id
            int format_id = DBAdapter.getFormatId(context, tournament_id);

            // Create the round
            DBAdapter.insertRound(context, tournament_id, size, format_id);

            // Set up every match
            for (int c = 0; c < orderedTeams.size() - 1; c = c + 2) {

                String leftTeam = orderedTeams.get(c);
                String rightTeam = orderedTeams.get(c + 1);

                new Match(context, DBAdapter.getRoundID(context, currentRound, tournament_id), tournament_id, leftTeam, rightTeam);
            }
            // If needed, set up the bye
            if (orderedTeams.size()%2 == 1) {

                String team = orderedTeams.get(orderedTeams.size() - 1);

                new Match(context, DBAdapter.getRoundID(context, currentRound, tournament_id), tournament_id, team, team);
            }

            // Increment the current round
            currentRound = currentRound + 1;
            DBAdapter.setCurrentRound(context, format_id, currentRound);
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


