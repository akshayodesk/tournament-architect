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
public class CustomNumCircuitsSpinnerAdapter extends ArrayAdapter<Integer> {

    private final Activity context;
    private Integer[] circuitNums;

    public CustomNumCircuitsSpinnerAdapter(Activity context, int textViewResID, Integer[] circuitNums) {

        super(context, textViewResID, circuitNums);

        this.context = context;
        this.circuitNums = circuitNums;
    }

    public View getDropDownView(int position, View convertView,ViewGroup parent) {

        return getCustomView(position, convertView, parent);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View view, ViewGroup parent) {

        // Set up the layout of the list
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_num_circuits_spinner, null, true);

        // Set the number
        TextView spinnerNumberTextView = (TextView) rowView.findViewById(R.id.spinnerNumberTextView);
        spinnerNumberTextView.setText(String.valueOf(circuitNums[position]));

        // Return the view of the row
        return rowView;
    }
}