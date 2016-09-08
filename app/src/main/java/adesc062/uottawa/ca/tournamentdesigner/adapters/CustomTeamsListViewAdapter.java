package adesc062.uottawa.ca.tournamentdesigner.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import adesc062.uottawa.ca.tournamentdesigner.R;

/**
 * Used for the teams list view in the CreateTournament activity.
 * The list includes a small arrow in indicate  that
 * the rows are clickable.
 */
public class CustomTeamsListViewAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] teamNames;
    private final Integer[] logos; // These integer correspond to the resource IDs of the drawables
    private boolean deleting;

    public CustomTeamsListViewAdapter(Activity context, String[] teamNames, Integer[] logos, boolean deleting) {
        super(context, R.layout.custom_teams_list_view, teamNames);
        this.context = context;
        this.teamNames = teamNames;
        this.logos = logos;
        this.deleting = deleting;
    }

    public View getView(int position, View view, ViewGroup parent) {
        // Set up the layout of the list
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_teams_list_view, null, true);
        // Set up the names of the teams
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        txtTitle.setText(teamNames[position]);
        // Set up the logos of the teams
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        imageView.setImageResource(logos[position]);
        // If we are in deleting mode, change the indicator
        if (deleting) {
            ImageView indicator = (ImageView) rowView.findViewById(R.id.indicatorImageView);
            indicator.setImageResource(R.drawable.delete_x);
        }
        // Return the view of the row
        return rowView;
    }
}