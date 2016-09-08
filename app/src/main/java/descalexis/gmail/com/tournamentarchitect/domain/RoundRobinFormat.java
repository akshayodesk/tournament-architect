package descalexis.gmail.com.tournamentarchitect.domain;

import android.content.Context;

import descalexis.gmail.com.tournamentarchitect.database.DBAdapter;

public class RoundRobinFormat extends TournamentFormat {

	private int numberOfCircuits; //RoundRobinRound
	
	public RoundRobinFormat(Context context, int tournament_id){

        super(context, tournament_id);
        isRR = true;
	}
	
	public boolean isTournamentComplete(Context context){

        int numTeams = DBAdapter.getNumTeamsForTournament(context, tournament_id);
        int currentRound = DBAdapter.getCurrentRound(context, tournament_id);
        int numRoundsInCircuits = numTeams - 1 + numTeams%2;
        int maxRounds = numRoundsInCircuits * DBAdapter.getTournamentNumCircuits(context, tournament_id);

		return (currentRound >= maxRounds) && checkIsRoundComplete(context);
	}

    public void createNextRound(Context context, int tournament_id) {

        // Get the list of ordered teams
        orderedTeams = DBAdapter.getFormatOrderedTeams(context, tournament_id);

        // Set the size variable from the parent abstract class, TournamentFormat
        size = orderedTeams.size();

        // Call the create next round method from the superclass
        super.createNextRound(context, tournament_id);
    }
}
