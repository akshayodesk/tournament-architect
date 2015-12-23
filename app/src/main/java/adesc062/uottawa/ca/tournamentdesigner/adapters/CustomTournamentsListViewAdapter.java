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
 * Used for the tournaments list view in the Load Tournament activity.
 */
public class CustomTournamentsListViewAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] tournamentNames;
    private final String[] tournamentStatus; // Under Creation (1) or Started (2)
    private boolean deleting;

    public CustomTournamentsListViewAdapter(Activity context, String[] tournamentNames,
                                            String[] tournamentStatus, boolean deleting) {

        super(context, R.layout.custom_teams_list_view, tournamentNames);

        this.context = context;
        this.tournamentNames = tournamentNames;
        this.tournamentStatus = tournamentStatus;
        this.deleting = deleting;
    }

    public View getView(int position, View view, ViewGroup parent) {

        // Set up the layout of the list
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_tournaments_list_view, null, true);

        // Set up the names of the tournaments
        TextView tournamentNameTextView = (TextView) rowView.findViewById(R.id.tournamentNameTextView);
        tournamentNameTextView.setText(tournamentNames[position]);

        // Set up the statuses of the tournaments
        TextView tournamentStatusTextView = (TextView) rowView.findViewById(R.id.statusTextView);
        tournamentStatusTextView.setText(tournamentStatus[position]);

        // If we are in deleting mode, change the indicator
        if (deleting) {

            ImageView indicator = (ImageView) rowView.findViewById(R.id.tournamentsModeIndicator);
            indicator.setImageResource(R.drawable.delete_x);
        }

        // If the tournament is finished, reduce opacity
        if (tournamentStatus[position].equals("Finished")) {

            tournamentNameTextView.setAlpha(0.5f);
            tournamentStatusTextView.setAlpha(0.5f);

            ImageView tournamentsModeIndicator = (ImageView) rowView.findViewById(R.id.tournamentsModeIndicator);
            tournamentsModeIndicator.setAlpha(0.5f);
        }

        // Return the view of the row
        return rowView;
    }
}