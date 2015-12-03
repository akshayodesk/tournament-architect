package adesc062.uottawa.ca.tournamentdesigner.domain;

import android.content.Context;

import java.util.*;

import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class KnockoutFormat extends TournamentFormat {

	public KnockoutFormat(int tournament_id){

		super(tournament_id);
		//ignore the circuit pass, kept it for polymorphism
	}

    /*
	//override
	protected boolean generateNextRound(){
		//Removes half the teams if not first round (eliminate losers)
		Tournament.sortByWins(formatTeams);
		if(!rounds.isEmpty()){
			int numberOfTeamsToRemove= formatTeams.size()/2-formatTeams.size()%2;
			
			//when size is 3, 3->1-1->0 removed, therefore it will never become less than 3
			if(formatTeams.size()==3){
				
			}
			for(int c=0;c<numberOfTeamsToRemove;c++)
				formatTeams.removeLast();
		}	
		return super.generateNextRound();
	}

	*/
	
	public boolean checkIsTournamentComplete(){

        return true;
		//return (formatTeams.size()<3) && checkIsRoundComplete();
	}

    public void createNextRound(Context context, int tournament_id) {

        // Get the list of ordered teams
        orderedTeams = DBAdapter.getFormatOrderedTeams(context, tournament_id);

        // Set the size variable from the parent abstract class, TournamentFormat
        size = orderedTeams.size();

        // Get the current round number
        currentRound = DBAdapter.getCurrentRound(context, tournament_id);

        // If we need to create the current round
        if(currentRound > 0 ) {


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
