package adesc062.uottawa.ca.tournamentdesigner.domain;

import android.content.Context;

import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class MatchTeamScore {

    int team_id;
    int match_id;
    int tournament_id;

	public MatchTeamScore(Context context, String team1, int match_id, int tournament_id) {

        // Get the team id
        team_id = DBAdapter.getTeamId(context, team1, tournament_id);

        // Insert the match team score into the database
        DBAdapter.insertMatchTeamScore(context, team_id, match_id, tournament_id);

        // Set the match id
        this.match_id = match_id;

        // Set the tournament id
        this.tournament_id = tournament_id;
	}

    public void makeBye(Context context) {

        // Make the match team score a win in the database because the match is a bye
        DBAdapter.updateMatchTeamScore(context, team_id, match_id, 0, 1);

    }
}
