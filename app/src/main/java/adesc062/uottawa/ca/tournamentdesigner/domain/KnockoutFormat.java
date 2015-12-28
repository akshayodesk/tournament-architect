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

        // If this is not the current round, create new matches according
        // to the winners from the last round
        if (currentRound != 0){

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

                teamWins.add(i, DBAdapter.getTeamNumWins(context, competingTeams.get(i), tournament_id));
            }

            // Determine the winners of the previous rounds that will advance
            ArrayList<String> winnerTeams = new ArrayList<String>();
            for (int v = 0; v < competingTeams.size(); v = v + 2) {

                // If the first team won, add it
                if (teamWins.get(v) > teamWins.get(v + 1))
                    winnerTeams.add(competingTeams.get(v));
                    // Otherwise, add the second team
                else
                    winnerTeams.add(competingTeams.get(v + 1));
            }

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
        for (int c = 0; c < orderedTeams.size(); c = c + 2) {

            String leftTeam = orderedTeams.get(c);
            String rightTeam = orderedTeams.get(c + 1);

            new Match(context, DBAdapter.getRoundID(context, currentRound, tournament_id), tournament_id, leftTeam, rightTeam);
        }


        // Increment the current round
        currentRound = currentRound + 1;
        DBAdapter.setCurrentRound(context, format_id, currentRound);
    }
}
