package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import adesc062.uottawa.ca.tournamentdesigner.R;
import adesc062.uottawa.ca.tournamentdesigner.adapters.CustomTournamentsListViewAdapter;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class LoadTournamentActivity extends Activity {

    String[] tournamentsNames;
    String[] tournamentsStatus;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_tournament);

        // Get the information from the database
        ArrayList<String> tournamentsNamesArray = DBAdapter.getTournamentNames(this.getApplicationContext());
        ArrayList<String> tournamentsStatusArray = DBAdapter.getTournamentStatuses(this.getApplicationContext());

        // Convert the tournament names to a String array
        tournamentsNames = new String[tournamentsNamesArray.size()];
        tournamentsNames = tournamentsNamesArray.toArray(tournamentsNames);

        // Convert the tournament statuses to a String array
        tournamentsStatus = new String[tournamentsStatusArray.size()];
        tournamentsStatus = tournamentsStatusArray.toArray(tournamentsStatus);

        // Convert the statuses to their respective String
        String[] tournamentsStatusConverted = new String[tournamentsStatus.length];
        for(int i = 0; i < tournamentsStatusConverted.length; i++) {

            if(tournamentsStatus[i].equals("" + 1))
                tournamentsStatusConverted[i] = "Under Creation";
            else if(tournamentsStatus[i].equals("" + 2))
                tournamentsStatusConverted[i] = "Started";
            else
                tournamentsStatusConverted[i] = "Finished";
        }

        // Create the tournaments list adapter and set it
        CustomTournamentsListViewAdapter adapter = new CustomTournamentsListViewAdapter(LoadTournamentActivity.this, tournamentsNames, tournamentsStatusConverted);
        ListView tournamentsListView = (ListView) findViewById(R.id.tournamentsListView);
        tournamentsListView.setAdapter(adapter);

        // Set up on the onClick for the tournaments list
        tournamentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {

                // Get the tournament name
                TextView tournamentNameTextView = (TextView) view.findViewById(R.id.tournamentNameTextView);
                String tournamentName = tournamentNameTextView.getText().toString();

                // Get the tournament id
                int tournament_id = DBAdapter.getTournamentId(getApplicationContext(), tournamentName);

                // Get the tournament status
                int tournamentStatus = DBAdapter.getTournamentStatus(getApplicationContext(), tournament_id);

                // If the tournament is under creation
                if (tournamentStatus == 1) {

                    // Put the information in the intent
                    Intent intent = new Intent(getApplicationContext(), CreateTournamentActivity.class);
                    intent.putExtra("tournamentName", tournamentName);
                    intent.putExtra("tournament_id", tournament_id);

                    // Start the create tournament activity
                    startActivity(intent);
                    finish();

                // If the tournament is started
                }else {

                    // Put the information in the intent
                    Intent intent = new Intent(getApplicationContext(), StandingsActivity.class);
                    intent.putExtra("tournament_id", tournament_id);
                    intent.putExtra("cameFromLoadActivity", true);

                    // Start the standings activity
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) return;

        Intent intent = new Intent(this, LoadTournamentActivity.class);
        startActivity(intent);
    }

    public void onBackPressed() {

        // Go back to the home page and finish this activity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
