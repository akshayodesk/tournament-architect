package descalexis.gmail.com.tournamentarchitect.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import descalexis.gmail.com.tournamentarchitect.R;
import descalexis.gmail.com.tournamentarchitect.database.DBAdapter;

/**
 * Used for the teams list view in the Standings activity.
 *
 */
public class CustomTeamsListViewNotClickableWithScoreAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final int formatType;
    private final int tournament_id;
    private final String[] teamNames;
    private final Integer[] logos; // These integer correspond to the resource IDs of the drawables
    private final Integer[] teamWins;

    public CustomTeamsListViewNotClickableWithScoreAdapter(Activity context, int formatType, String[] teamNames,
                                                           Integer[] logos, Integer[] teamWins, int tournament_id) {
        super(context, R.layout.custom_teams_list_view, teamNames);
        this.context = context;
        this.formatType = formatType;
        this.tournament_id = tournament_id;
        this.teamNames = teamNames;
        this.logos = logos;
        this.teamWins = teamWins;
    }

    public View getView(int position, View view, ViewGroup parent) {
        // Set up the layout of the list
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_teams_list_view_with_scores, null, true);
        // Set up the names of the teams
        TextView teamNameForStandingsTextView = (TextView) rowView.findViewById(R.id.teamNameForStandingsTextView);
        teamNameForStandingsTextView.setText(teamNames[position]);
        // Set up the logos of the teams
        ImageView teamLogoForStandingsImageView = (ImageView) rowView.findViewById(R.id.teamLogoForStandingsImageView);
        teamLogoForStandingsImageView.setImageResource(logos[position]);
        // Set up the number of wins for each team
        TextView numberOfWinsTextView = (TextView) rowView.findViewById(R.id.numberOfWinsTextView);
        numberOfWinsTextView.setText(String.valueOf(teamWins[position]));
        // If the format is Knockout, change "Score:" to "Wins:"
        if (formatType == 2) {
            TextView scoreIndicatorTextView = (TextView) rowView.findViewById(R.id.scoreIndicatorTextView);
            scoreIndicatorTextView.setText("Wins:");
        }
        // If a team has been eliminated, reduce its opacity
        if (DBAdapter.getTeamFormatPosition(context, teamNames[position], tournament_id) == - 1) {
            teamNameForStandingsTextView.setAlpha(0.5f);
            teamLogoForStandingsImageView.setAlpha(0.5f);
            numberOfWinsTextView.setAlpha(0.5f);
            TextView scoreIndicatorTextView = (TextView) rowView.findViewById(R.id.scoreIndicatorTextView);
            scoreIndicatorTextView.setAlpha(0.5f);

        }
        // Return the view of the row
        return rowView;
    }
}