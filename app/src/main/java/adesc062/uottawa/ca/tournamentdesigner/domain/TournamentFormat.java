package adesc062.uottawa.ca.tournamentdesigner.domain;

import android.content.Context;

import java.lang.reflect.Array;
import java.util.*;

import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public abstract class TournamentFormat {

    protected int size;
	protected int[] rounds;
    protected int tournament_id;
	private boolean isComplete;
	protected ArrayList<String> formatTeams;
    protected ArrayList<String> orderedTeams;
	protected int currentRound;

	public TournamentFormat(int tournament_id) {

		formatTeams = new ArrayList<String>();

        this.tournament_id = tournament_id;
		//if(rounds == null)
			//rounds = new LinkedList<TournamentRound>();

		//isComplete = false;
		currentRound = 0; // Used for offsetting match-ups and for preventing null pointers on empty round list
	}

    public void setCurrentRound(Context context) {

        currentRound = DBAdapter.getCurrentRound(context, tournament_id);
    }

    public void setUpFormat(Context context, int tournament_id, int totalCircuits, ArrayList<String> teams) {

        int size = teams.size();

        // Set up the format in the database
        DBAdapter.setUpFormat(context, tournament_id, totalCircuits, size);

        // Randomize the teams list
        for(int i = 0; i < size; i++) {

            // Remove a random team and save it
            int rand = (int) (Math.random() * (teams.size() - 1));
            String removedTeam = teams.remove(rand);

            // Add it back to the end of the list
            teams.add(i, removedTeam);
        }

        // Set up the format positions for each team
        for(int j = 1; j < size; j++) {

            DBAdapter.setTeamFormatPosition(context, tournament_id, teams.get(j), j);
        }
    }

    public void createNextRound(Context context, int tournament_id) {

        // Initialize variables
        int format_id = DBAdapter.getFormatId(context, tournament_id);
        currentRound = DBAdapter.getCurrentRound(context, tournament_id);

        // Create the round
        DBAdapter.insertRound(context, tournament_id, size, format_id);

        /* Add matches to the round */
        int numOfMatches = orderedTeams.size()/2 + orderedTeams.size()%2;

        // Set up every round
        for(int  c = 0; c < numOfMatches; c++) {

            // Get the name of the first team of the match
            String team1 = orderedTeams.get(c);

            // The first team plays last team (not based on standings but on random order)
            // Every round, the place considered last is offset by 1

            int team2Pointer = (orderedTeams.size() - 1) - c - currentRound;

            // If one team is matched up against itself, the number of teams is not a power of two.
            // Therefore, it receives a bye
			String team2 = orderedTeams.get(team2Pointer);
			Match matchTemp = new Match(context, DBAdapter.getCurrentRoundID(context, currentRound, tournament_id), tournament_id, team1, team2);
        }

        //DBAdapter.insertMatch(context, 1, 1);

		// Increment the current round
        currentRound = currentRound + 1;
        DBAdapter.incrementCurrentRound(context, format_id, currentRound);
    }

    public ArrayList<String> getFormatOrderedTeams(Context context, int tournament_id) {

        return DBAdapter.getFormatOrderedTeams(context, tournament_id);
    }

    public boolean checkIsRoundComplete(Context context) {

        // Get the list of updated values for the latest round
        ArrayList<Integer> matchesUpdatedValues = DBAdapter.getMatchesUpdatedValues(context, DBAdapter.getCurrentRoundID(context, currentRound - 1, tournament_id));

        // Go through the list and check if a match has not yet been updated
        for(int i = 0; i < matchesUpdatedValues.size(); i++) {

            if(matchesUpdatedValues.get(i) == 0)
                return false;
        }

        return true;
    }
}
