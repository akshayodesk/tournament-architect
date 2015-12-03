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
public class CustomRoundsListViewAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] roundNames;

    public CustomRoundsListViewAdapter(Activity context, String[] roundNames) {

        super(context, R.layout.custom_teams_list_view, roundNames);

        this.context = context;
        this.roundNames = roundNames;
    }

    public View getView(int position, View view, ViewGroup parent) {

        // Set up the layout of the list
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_rounds_list_view, null, true);

        // Set up the names of the rounds
        TextView roundNameIndividualTextView = (TextView) rowView.findViewById(R.id.roundNameIndividualTextView);
        roundNameIndividualTextView.setText(roundNames[position]);

        // Return the view of the row
        return rowView;
    }
}