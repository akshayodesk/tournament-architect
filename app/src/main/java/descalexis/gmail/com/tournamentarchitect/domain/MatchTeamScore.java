package descalexis.gmail.com.tournamentarchitect.domain;

import android.content.Context;

import descalexis.gmail.com.tournamentarchitect.database.DBAdapter;

public class MatchTeamScore {

    int team_id;
    int match_id;
    int tournament_id;
    int formatType;

	public MatchTeamScore(Context context, String team1, int match_id, int tournament_id, int formatType) {

        // Get the team id
        team_id = DBAdapter.getTeamId(context, team1, tournament_id);

        // Insert the match team score into the database
        DBAdapter.insertMatchTeamScore(context, team_id, match_id, tournament_id);

        // Set the match id
        this.match_id = match_id;

        // Set the tournament id
        this.tournament_id = tournament_id;

        // Set the formatType
        this.formatType = formatType;
	}

    public void makeBye(Context context) {

        // Make the match team score a win in the database because the match is a bye
        DBAdapter.updateMatchTeamScore(context, team_id, match_id, 0, 1, formatType);

    }
}
