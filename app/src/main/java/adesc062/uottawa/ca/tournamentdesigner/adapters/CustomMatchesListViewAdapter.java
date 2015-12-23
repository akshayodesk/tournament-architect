package adesc062.uottawa.ca.tournamentdesigner.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import adesc062.uottawa.ca.tournamentdesigner.R;
import adesc062.uottawa.ca.tournamentdesigner.database.DBAdapter;

/**
 * Used for the teams list view in the CreateTournament activity.
 * The list includes a small arrow in indicate  that
 * the rows are clickable.
 */
public class CustomMatchesListViewAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<Integer> matchIDs;
    private final String[] team1Names;
    private final String[] team2Names;
    private final Integer[] team1Logos; // These integer correspond to the resource IDs of the drawables
    private final Integer[] team2Logos; // These integer correspond to the resource IDs of the drawables

    public CustomMatchesListViewAdapter(Activity context, ArrayList<Integer> matchIDs, String[] team1Names, Integer[] team1Logos,
                                        String[] team2Names, Integer[] team2Logos ) {

        super(context, R.layout.custom_teams_list_view, team1Names);

        this.context = context;
        this.matchIDs = matchIDs;
        this.team1Names = team1Names;
        this.team2Names = team2Names;
        this.team1Logos = team1Logos;
        this.team2Logos = team2Logos;
    }

    public View getView(int position, View view, ViewGroup parent) {

        // Set up the layout of the list
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_matches_list_view, null, true);

        // If the match is a bye
        if(team1Names[position].equals(team2Names[position])) {

            // Set up the name of the team
            TextView teamName1TextView = (TextView) rowView.findViewById(R.id.teamName1TextView);
            teamName1TextView.setText(team1Names[position]);
            teamName1TextView.setTextColor(Color.parseColor("#80000000"));

            // Remove the name of second team
            TextView teamName2TextView = (TextView) rowView.findViewById(R.id.teamName2TextView);
            teamName2TextView.setText("");

            // Set up the text saying the match is a bye
            TextView vsTextView = (TextView) rowView.findViewById(R.id.vsTextView);
            vsTextView.setText("Bye");
            vsTextView.setTextColor(Color.parseColor("#80000000"));

            // Set up the logo of the team
            ImageView team1LogoImageView = (ImageView) rowView.findViewById(R.id.team1LogoImageView);
            team1LogoImageView.setImageResource(team1Logos[position]);
            team1LogoImageView.setAlpha(0.5f);

            // Remove the logo of the second team
            ImageView team2LogoImageView = (ImageView) rowView.findViewById(R.id.team2LogoImageView);
            team2LogoImageView.setImageResource(-1);

            // Remove the arrow
            ImageView arrow = (ImageView) rowView.findViewById(R.id.arrowImageView);
            arrow.setImageResource(-1);

        }
        // If the match is not a bye
        else {

            // Set up the names of the first teams
            TextView teamName1TextView = (TextView) rowView.findViewById(R.id.teamName1TextView);
            teamName1TextView.setText(team1Names[position]);

            // Set up the names of the second teams
            TextView teamName2TextView = (TextView) rowView.findViewById(R.id.teamName2TextView);
            teamName2TextView.setText(team2Names[position]);

            // Set up the logos of the first teams
            ImageView team1LogoImageView = (ImageView) rowView.findViewById(R.id.team1LogoImageView);
            team1LogoImageView.setImageResource(team1Logos[position]);

            // Set up the logos of the second teams
            ImageView team2LogoImageView = (ImageView) rowView.findViewById(R.id.team2LogoImageView);
            team2LogoImageView.setImageResource(team2Logos[position]);

            // If the match had already been updated, reduce opacity
            if (DBAdapter.getMatchUpdated(context, matchIDs.get(position)) == 1) {

                teamName1TextView.setTextColor(Color.parseColor("#80000000"));
                teamName2TextView.setTextColor(Color.parseColor("#80000000"));
                team1LogoImageView.setAlpha(0.5f);
                team2LogoImageView.setAlpha(0.5f);

                TextView vsTextView = (TextView) rowView.findViewById(R.id.vsTextView);
                vsTextView.setTextColor(Color.parseColor("#80000000"));

                ImageView arrow = (ImageView) rowView.findViewById(R.id.arrowImageView);
                arrow.setAlpha(0.5f);
            }
        }

        // Return the view of the row
        return rowView;
    }
}