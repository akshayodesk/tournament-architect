package adesc062.uottawa.ca.tournamentdesigner.domain;

import android.content.Context;

import java.util.*;

import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class CombinationFormat extends TournamentFormat {

	private	TournamentFormat currentFormat;

	public CombinationFormat(int tournament_id){
		super(tournament_id);
		//currentFormat=new RoundRobinFormat(numberOfCircuits, aTeams);
	}

	protected boolean generateNextRound(){
        return true;
		//return currentFormat.generateNextRound();
	}

	public boolean checkIsTournamentComplete(){

        return true;
        /*
		if((formatTeams.size()<3)&&checkIsRoundComplete()){
			if(state==1){
				currentFormat = new KnockoutFormat(formatTeams);
				currentFormat.generateNextRound();
				return false;
			}
			currentFormat.generateNextRound();
			return true;
		}
		return false; */
	}

    public void createNextRound(Context context, int tournament_id) {

        // Get the list of ordered teams
        orderedTeams = DBAdapter.getFormatOrderedTeams(context, tournament_id);

        // Set the size variable from the parent abstract class, TournamentFormat
        size = orderedTeams.size();

        // Get the current round number
        currentRound = DBAdapter.getCurrentRound(context, tournament_id);

        // If we need to create the current round
        if(currentRound < size - 1) {

            // Only half of the teams move on to Knockout from the Round Robin
            size = size/2;

            /* Calculate the number of teams to remove */
            int numberOfTeamsToRemove = size;

            for(int i = 0; i < currentRound; i++) {

                numberOfTeamsToRemove = numberOfTeamsToRemove/2;
            }

            size = size - numberOfTeamsToRemove;
        }

        // Call the create next round method from the superclass
        super.createNextRound(context, tournament_id);
    }
}
