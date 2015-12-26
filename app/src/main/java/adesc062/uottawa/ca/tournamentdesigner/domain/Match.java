package adesc062.uottawa.ca.tournamentdesigner.domain;

import android.content.Context;

import adesc062.uottawa.ca.tournamentdesigner.database.DB;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class Match {

	private boolean complete;
    private int match_id;

	public Match (Context context, int currentRound, int tournament_id,
                  String team1, String team2) {

        // Insert the match
        DBAdapter.insertMatch(context, currentRound, tournament_id);

        // Get the match id
        match_id = DBAdapter.getLatestMatchId(context, tournament_id);

        // Get the number of teams
        int numTeams = DBAdapter.getNumTeamsForTournament(context, tournament_id);

        // Get the number of circuits for the tournament
        int numCircuits = DBAdapter.getTournamentNumCircuits(context, tournament_id);

        // Find the total number of Round Robin rounds that have to be played
        int numRoundRobinRounds = ((numTeams - 1) + (numTeams%2)) * numCircuits;

        // Determine whether the current match is in a Round Robin or a Knockout portion
        int formatType;
        // If the current round is bigger than the number of Round Robin rounds,
        // then the format is Knockout
        if (currentRound > numRoundRobinRounds)
            formatType = 2;
        // Otherwise, the format is Round Robin
        else
            formatType = 1;

        // Create the match team scores
        MatchTeamScore matchTeamScore1 = new MatchTeamScore(context, team1, match_id, tournament_id, formatType);

        // If the match is a bye, make the team win against itself
        if(team1.equals(team2)) {

            // Update the association class to represent a win
             matchTeamScore1.makeBye(context);

            // Set the match to completed
            updateMatch(context, match_id, team1, 0, team1, 1, tournament_id, formatType);
        }
        else{

            MatchTeamScore matchTeamScore2 = new MatchTeamScore(context, team2, match_id, tournament_id, formatType);
        }
	}

	public boolean getComplete( ){

		return complete;
	}

    public static void updateMatch(Context context, int match_id, String team1, int score1, String team2,
                              int score2, int tournament_id, int formatType) {

        // Redundant  because Byes are handled by updating on creation
        // Check if the match is a bye
        if (team1.equals(team2)) {

            DBAdapter.updateMatch(context, match_id);
        }
        // If the match is not a bye
        else {

            // Get the team id
            int team_id1 = DBAdapter.getTeamId(context, team1, tournament_id);
            int team_id2 = DBAdapter.getTeamId(context, team2, tournament_id);

            // Check if the match is a tie
            if(score1 == score2) {

                // Set the match team scores
                DBAdapter.updateMatchTeamScore(context, team_id1, match_id, score1, 0, formatType);
                DBAdapter.updateMatchTeamScore(context, team_id2, match_id, score2, 0, formatType);

                // Give both teams a tie, i.e. one point
                DBAdapter.giveTeamTie(context, team_id1);
                DBAdapter.giveTeamTie(context, team_id2);
            }
            // If team1 won
            else if(score1 > score2) {

                // Set the match team scores
                DBAdapter.updateMatchTeamScore(context, team_id1, match_id, score1, 1, formatType);
                DBAdapter.updateMatchTeamScore(context, team_id2, match_id, score2, 0, formatType);

                // Give team1 a win, i.e. three points
                DBAdapter.giveTeamWin(context, team_id1);
            }
            // If team2 won
            else {

                // Set the match team scores
                DBAdapter.updateMatchTeamScore(context, team_id1, match_id, score1, 0, formatType);
                DBAdapter.updateMatchTeamScore(context, team_id2, match_id, score2, 1, formatType);

                // Give team2 a win, i.e. three points
                DBAdapter.giveTeamWin(context, team_id2);
            }
        }

        // Set the match to updated
        DBAdapter.updateMatch(context, match_id);
    }
}
