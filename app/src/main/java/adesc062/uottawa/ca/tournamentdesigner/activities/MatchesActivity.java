package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import adesc062.uottawa.ca.tournamentdesigner.R;
import adesc062.uottawa.ca.tournamentdesigner.adapters.CustomMatchesListViewAdapter;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class MatchesActivity extends Activity {

    int round_id;
    int roundNum;
    ArrayList<Integer> matchIDs;
    String[] team1Names;
    String[] team2Names;
    Integer[] team1Logos;
    Integer[] team2Logos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        roundNum = getIntent().getIntExtra("roundNum", -1);

        updateMatches();
    }

    private void updateMatches() {

        // Get the information from the intent
        round_id = getIntent().getIntExtra("round_id", 0);

        // Get the list of matches for that round
        matchIDs = DBAdapter.getRoundMatchIDs(getApplicationContext(), round_id);

        // Initialize the lists for names and logos
        ArrayList<String> team1NamesArray = new ArrayList<>();
        ArrayList<String> team2NamesArray = new ArrayList<>();
        ArrayList<Integer> team1LogosArray = new ArrayList<>();
        ArrayList<Integer> team2LogosArray = new ArrayList<>();

        // Get the information
        for(int i = 0; i < matchIDs.size(); i++) {

            // Get the team ids for a match
            ArrayList<Integer> matchTeamIDs = DBAdapter.getMatchTeamIDs(getApplicationContext(), matchIDs.get(i));

            //For Bye
            if(matchTeamIDs.size()==1){

                team1NamesArray.add(DBAdapter.getTeamName(getApplicationContext(), matchTeamIDs.get(0)));
                String logo1 = DBAdapter.getTeamLogo(getApplicationContext(), matchTeamIDs.get(0));
                team1LogosArray.add(getResources().getIdentifier(logo1, "drawable", getPackageName()));


                team2NamesArray.add(DBAdapter.getTeamName(getApplicationContext(), matchTeamIDs.get(0)));
                String logo2 = DBAdapter.getTeamLogo(getApplicationContext(), matchTeamIDs.get(0));
                team2LogosArray.add(getResources().getIdentifier(logo2, "drawable", getPackageName()));

            }
            else {
                for (int j = 0; j < 2; j++) {


                    // Get the name and logo for the first team
                    if (j == 0) {

                        team1NamesArray.add(DBAdapter.getTeamName(getApplicationContext(), matchTeamIDs.get(j)));
                        String logo = DBAdapter.getTeamLogo(getApplicationContext(), matchTeamIDs.get(j));
                        team1LogosArray.add(getResources().getIdentifier(logo, "drawable", getPackageName()));
                    }
                    // Get the name and logo for the second team
                    else {

                        team2NamesArray.add(DBAdapter.getTeamName(getApplicationContext(), matchTeamIDs.get(j)));
                        String logo = DBAdapter.getTeamLogo(getApplicationContext(), matchTeamIDs.get(j));
                        team2LogosArray.add(getResources().getIdentifier(logo, "drawable", getPackageName()));
                    }
                }
            }
        }

        // Convert the names of the first teams to a string array
        team1Names = new String[team1NamesArray.size()];
        team1Names = team1NamesArray.toArray(team1Names);

        // Convert the names of the second teams to a string array
        team2Names = new String[team2NamesArray.size()];
        team2Names = team2NamesArray.toArray(team2Names);

        // Convert the team logos of the first teams to an integer array of the resource ids
        team1Logos = new Integer[team1LogosArray.size()];
        team1Logos = team1LogosArray.toArray(team1Logos);

        // Convert the team logos of the second teams to an integer array of the resource ids
        team2Logos = new Integer[team2LogosArray.size()];
        team2Logos = team2LogosArray.toArray(team2Logos);


        // Create the rounds list adapter and set it
        CustomMatchesListViewAdapter adapter = new CustomMatchesListViewAdapter(MatchesActivity.this, team1Names,
                team1Logos, team2Names, team2Logos);
        ListView matchesListView = (ListView) findViewById(R.id.matchesListView);
        matchesListView.setAdapter(adapter);

        // Set up on the onClick for the matches list
        matchesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {

                // Put the information in the intent
                Intent intent = new Intent(getApplicationContext(), MatchViewActivity.class);
                intent.putExtra("match_id", matchIDs.get(pos));
                intent.putExtra("roundNum", roundNum);
                intent.putExtra("team1Name", team1Names[pos]);
                intent.putExtra("team2Name", team2Names[pos]);
                intent.putExtra("team1Logo", team1Logos[pos]);
                intent.putExtra("team2Logo", team2Logos[pos]);

                // Start the edit team activity if the match is not a bye
                if(!team1Names[pos].equals(team2Names[pos]))
                    startActivity(intent);
            }
        });
    }
}
