package adesc062.uottawa.ca.tournamentdesigner.adapters;

import android.app.Activity;
import android.graphics.Color;
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
public class CustomRoundsListViewAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] roundNames;
    private int tournamentFinished;

    public CustomRoundsListViewAdapter(Activity context, String[] roundNames, int tournamentFinished) {
        super(context, R.layout.custom_teams_list_view, roundNames);
        this.context = context;
        this.roundNames = roundNames;
        this.tournamentFinished = tournamentFinished;
    }

    public View getView(int position, View view, ViewGroup parent) {
        // Set up the layout of the list
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_rounds_list_view, null, true);
        // Set up the names of the rounds
        TextView roundNameIndividualTextView = (TextView) rowView.findViewById(R.id.roundNameIndividualTextView);
        roundNameIndividualTextView.setText(roundNames[position]);
        // If we are setting up the last round, give it the "In Progress" status
        if (position == roundNames.length - 1 && tournamentFinished != 3) {
            TextView roundStatusTextView = (TextView) rowView.findViewById(R.id.roundStatusTextView);
            roundStatusTextView.setText("In Progress");
            roundNameIndividualTextView.setTextColor(Color.parseColor("#FF000000"));
            roundStatusTextView.setTextColor(Color.parseColor("#FF000000"));
            ImageView tournamentsListViewRightArrow = (ImageView) rowView.findViewById(R.id.tournamentsListViewRightArrow);
            tournamentsListViewRightArrow.setAlpha(1f);
        }
        // Return the view of the row
        return rowView;
    }
}