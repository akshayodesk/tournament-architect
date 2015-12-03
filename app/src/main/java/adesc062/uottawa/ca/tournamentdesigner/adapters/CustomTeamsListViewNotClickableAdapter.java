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
 * Used for the teams list view in the Standings activity.
 *
 */
public class CustomTeamsListViewNotClickableAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] teamNames;
    private final Integer[] logos; // These integer correspond to the resource IDs of the drawables

    public CustomTeamsListViewNotClickableAdapter(Activity context, String[] teamNames, Integer[] logos) {

        super(context, R.layout.custom_teams_list_view, teamNames);

        this.context = context;
        this.teamNames = teamNames;
        this.logos = logos;
    }

    public View getView(int position, View view, ViewGroup parent) {

        // Set up the layout of the list
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_teams_list_view_not_clickable, null, true);

        // Set up the names of the teams
        TextView txtTitle = (TextView) rowView.findViewById(R.id.standingsTeamName);

        // Set up the logos of the teams
        ImageView imageView = (ImageView) rowView.findViewById(R.id.standingsTeamImage);
        txtTitle.setText(teamNames[position]);
        imageView.setImageResource(logos[position]);

        // Return the view of the row
        return rowView;
    }
}