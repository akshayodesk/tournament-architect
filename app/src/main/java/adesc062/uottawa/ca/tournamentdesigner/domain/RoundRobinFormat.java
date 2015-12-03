package adesc062.uottawa.ca.tournamentdesigner.domain;

import android.content.Context;

import java.util.*;

import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class RoundRobinFormat extends TournamentFormat {
	private int numberOfCircuits; //RoundRobinRound
	
	public RoundRobinFormat(int tournament_id){

        super(tournament_id);
	}
	
	public boolean checkIsTournamentComplete(){

        return true;
		//return (lastRoundNumber>=numberOfCircuits) && checkIsRoundComplete();
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
