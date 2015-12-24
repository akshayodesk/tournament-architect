package adesc062.uottawa.ca.tournamentdesigner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Used for setting up the database
 * for the Tournament Designer activity.
 * The application stores tournaments, teams, formats,
 * rounds, matches and the scores of teams for a match.
 */
public class DB extends SQLiteOpenHelper {

    final static int DB_VERSION = 1;
    final static String DB_NAME = "tournamentDesignerDB.s3db";
    Context context;

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        this.context = context;
    }

    /**
     * Used to create the tables for the database.
     * This method is only called once unless the user has
     * never created the database before or has completely
     * wiped the database
     *
     * @param db the database to write on.
     */
    public void onCreate(SQLiteDatabase db) {

        /* Create the tables for the database */
        // Create the tournaments table
        db.execSQL("CREATE TABLE tournaments (tournament_id INTEGER PRIMARY KEY, "
                + "name TEXT UNIQUE, "
                + "type INTEGER NOT NULL, "
                + "numCircuits INTEGER, "
                + "status INTEGER NOT NULL)");

        // Create the teams table
        db.execSQL("CREATE TABLE teams (team_id INTEGER PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "logo TEXT NOT NULL, "
                + "format_position INTEGER, "
                + "team_tournament_id INTEGER NOT NULL, "
                + "FOREIGN KEY(team_tournament_id) REFERENCES tournaments(tournament_id))");

        // Create the formats table
        db.execSQL("CREATE  TABLE formats (format_id INTEGER PRIMARY KEY, "
                + "currentRound INTEGER, "
                + "totalCircuits INTEGER, "
                + "size INTEGER NOT NULL, "
                + "format_tournament_id INTEGER NOT NULL, "
                + "FOREIGN KEY(format_tournament_id) REFERENCES tournaments(tournament_id))");

        // Create the rounds table
        db.execSQL("CREATE TABLE rounds (round_id INTEGER PRIMARY KEY, "
                + "size INTEGER, "
                + "round_format_id INTEGER, "
                + "round_tournament_id INTEGER, "
                + "FOREIGN KEY(round_format_id) REFERENCES formats(format_id), "
                + "FOREIGN KEY(round_tournament_id) REFERENCES tournaments(tournament_id))");

        // Create the matches table
        db.execSQL("CREATE TABLE matches (match_id INTEGER PRIMARY KEY, "
                + "updated INTEGER NOT NULL, "
                + "match_round_id INTEGER NOT NULL, "
                + "match_tournament_id INTEGER NOT NULL, "
                + "FOREIGN KEY (match_round_id) REFERENCES rounds(round_id), "
                + "FOREIGN KEY (match_tournament_id) REFERENCES tournaments(tournament_id))");

        // Create the match team scores table
        db.execSQL("CREATE TABLE match_team_scores (match_team_score_id INTEGER PRIMARY KEY, "
                + "score INTEGER, "
                + "isWinner INTEGER, "
                + "match_team_score_team_id INTEGER NOT NULL, "
                + "match_team_score_match_id INTEGER NOT NULL, "
                + "match_team_score_tournament_id INTEGER NOT NULL, "
                + "FOREIGN KEY (match_team_score_team_id) REFERENCES teams(team_id), "
                + "FOREIGN KEY (match_team_score_match_id) REFERENCES matches(match_id), "
                + "FOREIGN KEY (match_team_score_tournament_id) REFERENCES tournaments(tournament_id))");

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // do stuff
            }
        }
}
