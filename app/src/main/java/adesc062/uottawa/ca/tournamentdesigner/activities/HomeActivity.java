package adesc062.uottawa.ca.tournamentdesigner.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import adesc062.uottawa.ca.tournamentdesigner.R;
import adesc062.uottawa.ca.tournamentdesigner.database.DB;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

public class HomeActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* Create the database on first application use
            - Will only be created once unless the
            - user deletes it
         */

        DB dbHelper = new DB(this.getApplicationContext());
    }

    /**
     * Called whenever the user clicks the New Tournament button.
     * Brings up the tournament creation page.
     * The Tournament Creation Page allows users to create a custom tournament.
     *
     * @param view the View that was clicked.
     */
    public void newTournamentOnClick(View view) {

        // Create a new tournament in the database.
        DBAdapter.newTournament(getApplicationContext());
        int tournament_id = DBAdapter.getMostRecentTournamentId(getApplicationContext());

        Intent intent = new Intent(this, CreateTournamentActivity.class);
        intent.putExtra("tournament_id", tournament_id);
        startActivity(intent);
    }

    /**
     * Called whenever the user clicks the Load Tournament button.
     * Brings up the tournament loading page.
     * The Tournament Creation Page allows users to load previous tournaments.
     *
     * @param view the View that was clicked.
     */
    public void loadTournamentOnClick(View view) {

        Intent intent = new Intent(this, LoadTournamentActivity.class);
        startActivity(intent);
    }

    /**
     * Called whenever the user clicks the Help button.
     * Brings up the Help page.
     * The Help page has instructions on how to use the application.
     *
     * @param view the View that was clicked.
     */
    public void helpButtonOnClick(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
}
