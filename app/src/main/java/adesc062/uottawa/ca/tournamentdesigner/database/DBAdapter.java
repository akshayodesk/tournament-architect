package adesc062.uottawa.ca.tournamentdesigner.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import adesc062.uottawa.ca.tournamentdesigner.database.DB;

public class DBAdapter {

    /**
     * Used to create a new empty tournament;
     *
     * @param context is the current application context.
     */
    public static void newTournament(Context context){

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the tournament
        db.execSQL("INSERT INTO tournaments (type, numCircuits, status) VALUES (1, 1, 1)");
        try {
            changeTournamentName(context, getMostRecentTournamentId(context), null);
        } catch (Exception e) {

        }

        // Close the database
        db.close();
    }

    /**
     * Used  to change the name of a tournament of given tournament_id.
     *
     * @param context is the current applicaton context.
     * @param tournament_id is the id of tournament to have its name changed.
     * @param name is the name to give to the tournament.
     * @throws IllegalArgumentException when the name of tournament is already in us.
     * @throws NullPointerException when the name given is empty.
     */
    public static void changeTournamentName(Context context, int tournament_id, String name) throws IllegalArgumentException, NullPointerException {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Used to generate names automatically without user input
        if(name == null){

            name = "New Tournament " + getMostRecentTournamentId(context);
            db.execSQL("UPDATE tournaments SET name = '" + name +"' WHERE tournament_id = " + tournament_id);

        // If the name is empty
        }else if(name.equals("")){

            throw new NullPointerException();
        }

        // If the name is already used
        else {
            try {

                ContentValues dataToInsert = new ContentValues();
                dataToInsert.put("name", name);

                String[] whereArgs = new String[] {String.valueOf(tournament_id)};

                if(db.updateWithOnConflict("tournaments", dataToInsert, "tournament_id=?", whereArgs, SQLiteDatabase.CONFLICT_IGNORE) == 0)
                    throw new IllegalArgumentException();

            }catch (IllegalArgumentException e) {

                throw new IllegalArgumentException();
            }
        }

        // Close the database
        db.close();
    }

    /**
     * Returns the id of a tournament of given name.
     *
     * @param context is the current application context.
     * @param name is the name of the tournament to get the id of.
     * @return the id of the tournament.
     */
    public static int getTournamentId(Context context, String name) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int tournament_id = -1;

        // If the given name is null, then return the id of the most recent tournament created
        if(name == null){

            tournament_id = getMostRecentTournamentId(context);
        }else
            try {

                Cursor c = db.rawQuery("SELECT * FROM tournaments WHERE name = '" + name + "'", null);
                c.moveToFirst();

                try {

                    tournament_id = c.getInt(c.getColumnIndex("tournament_id"));
                } catch (Exception e) {

                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {

                // Do nothing
            }

        // Close the database
        db.close();

        // Return the tournament id
        return tournament_id;
    }

    /**
     * Return the id of the last tournament created.
     *
     * @param context is the current application context.
     * @return the id of the last tournament created.
     */
    public static int getMostRecentTournamentId(Context context) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get the information
        int tournament_id = -1;

        try {

            final SQLiteStatement stmt = db.compileStatement("SELECT MAX(tournament_id) FROM tournaments");

            tournament_id = (int) stmt.simpleQueryForLong();
        } catch (SQLiteException se ) {

            // Do nothing
        }

        // If a tournament was not found, throw an exception
        if(tournament_id == -1)
            throw new IllegalArgumentException();

        // Close the database
        db.close();

        // Return the tournament id
        return tournament_id;
    }

    /**
     * Used to insert a new tournament into the database.
     *
     * @param context the current application context.
     * @param name the name of the tournament.
     * @param type the format of the tournament: 1: Round-Robin, 2: Bracket, 3: Combination.
     * @param numCircuits the number of rounds of the Round-Robin part of the tournament.
     */
    public static void insertTournament(Context context, String name, int type, int numCircuits) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the tournament
        db.execSQL("INSERT INTO tournaments (name, type, numCircuits, status) VALUES "
                + "('" + name + "', " + type + ", " + numCircuits + ", 1)");

        // Close the database
        db.close();
    }

    /**
     * Used to delete a tournament of given tournament_id.
     * This method is called when the user clicks the Delete Tournament button.
     *
     * @param context is the current application context.
     * @param tournament_id is the id of the tournament to be deleted.
     */
    public static void deleteTournament(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete the tournament
        db.execSQL("DELETE FROM tournaments WHERE tournament_id = " + tournament_id);

        // Delete the teams associated with that tournament
        db.execSQL("DELETE FROM teams WHERE team_tournament_id = " + tournament_id);

        // Delete the formats associated with that tournament
        db.execSQL("DELETE FROM formats WHERE format_tournament_id = " + tournament_id);

        // Delete the rounds associated with that tournament
        db.execSQL("DELETE FROM rounds WHERE round_tournament_id = " + tournament_id);

        // Delete the matches associated with that tournament
        db.execSQL("DELETE FROM matches WHERE match_tournament_id = " + tournament_id);

        // Delete the match_team_scores associated with that tournament
        db.execSQL("DELETE FROM match_team_scores WHERE match_team_score_tournament_id = " + tournament_id);
        // Close the database
        db.close();
    }

    /**
     * Used to save the number of rounds for a tournament of given tournament_id.
     *
     * @param context is the current application context.
     * @param numCircuits is the number of rounds to be saved
     * @param tournament_id is the id of the tournament to change the number of rounds of.
     */
    public static void saveTournamentNumCircuits(Context context, int numCircuits, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // If the number of circuits is

        // Save the number of rounds
        db.execSQL("UPDATE tournaments SET numCircuits = " + numCircuits + " WHERE tournament_id = " + tournament_id);

        // Close the database
        db.close();
    }

    /**
     * Used to change the status of a tournament.
     * 1: Under Creation, 2: Started, 3: Finished.
     *
     * @param context is the current application context.
     * @param tournament_id is the id of the tournament to change the status of.
     */
    public static void updateTournamentStatus(Context context, int tournament_id, int status) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Save the number of rounds
        db.execSQL("UPDATE tournaments SET status = " + status + " WHERE tournament_id = " + tournament_id);

        // Close the database
        db.close();
    }

    /**
     * Returns the name for the tournament of given tournament_id.
     *
     * @param context is the current application context.
     * @param tournament_id is the is of the tournament to get the name of
     * @return the name for the tournament of given tournament_id.
     */
    public static String getTournamentName(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        String name = "";

        try {

            Cursor c = db.rawQuery("SELECT * FROM tournaments WHERE tournament_id = " + tournament_id, null);

            c.moveToFirst();
            {
                try {
                    name = c.getString(c.getColumnIndex("name"));
                }
                catch (Exception e) {
                    throw new IllegalArgumentException();
                }

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the name
        return name;
    }

    /**
     * Returns the number of rounds of a tournament of given tournament_id.
     *
     * @param context is the current application context.
     * @param tournament_id is the id of the tournament to get the number of rounds of.
     * @return the number of rounds for the tournament.
     */
    public static int getTournamentNumCircuits(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get the information
        int numCircuits = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM tournaments WHERE tournament_id = " + tournament_id, null);

            c.moveToFirst();
            {
                try {
                    numCircuits = c.getInt(c.getColumnIndex("numCircuits"));
                }
                catch (Exception e) {
                    throw new IllegalArgumentException();
                }

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the number of rounds
        return numCircuits;
    }

    /**
     * Returns the int representing the format type of a tournament of given tournament_id.
     * 1: Round Robin, 2: Knockout, 3: Combination.
     *
     * @param context is the current application context.
     * @param tournament_id is the id of the tournament to get the format type of.
     * @return the int representing the format type of the tournament.
     */
    public static int getTournamentFormatType(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int formatType = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM tournaments WHERE tournament_id = " + tournament_id, null);

            c.moveToFirst();
            {
                try {

                    formatType = c.getInt(c.getColumnIndex("type"));
                }
                catch (Exception e) {

                    throw new Exception();
                }

            }
        } catch (Exception e ) {

            // If an error is encountered, set the default format to Round Robin
            formatType = 1;
        }

        // Close the database
        db.close();

        // Return the format type
        return formatType;
    }

    /**
     * Used to save the format type of a tournament of given tournament_id.
     * 1: Round Robin, 2: Knockout, 3: Combination.
     *
     * @param context is the current application context.
     * @param formatType is the format type to give to the tournament.
     * @param tournament_id is the id of the tournament to change the format type of.
     */
    public static void saveTournamentFormatType(Context context, int formatType, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the tournament format type
        ContentValues dataToPutIn = new ContentValues();
        dataToPutIn.put("type", formatType);
        String[] whereArgs = new String[]{String.valueOf(tournament_id)};
        db.update("tournaments", dataToPutIn, "tournament_id = ?", whereArgs);

        // Close the database
        db.close();
    }

    /**
     * Used to insert a team into the database.
     *
     * @param context the current application context.
     * @param name the name of the team.
     * @param logo the source location of the image.
     * @param team_tournament_id the tournament id attached to this team.
     */
    public static void insertTeam(Context context, String name, String logo, int team_tournament_id) throws IllegalArgumentException, NullPointerException {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // If the name is empty
         if(name.equals("")){

            throw new NullPointerException();
         }

        // Check if the team name already exists
        try {

            Cursor c = db.rawQuery("SELECT * FROM teams WHERE name = '" + name + "' AND team_tournament_id = " + team_tournament_id, null);

            if (c.moveToFirst() == true)
                throw new IllegalArgumentException();

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException();
        }

        // Insert the team
        ContentValues dataToInsert = new ContentValues();
        dataToInsert.put("name", name);
        dataToInsert.put("logo", logo);
        dataToInsert.put("format_position", 0);
        dataToInsert.put("score", 0);
        dataToInsert.put("team_tournament_id", team_tournament_id);
        String[] whereArgs = new String[]{String.valueOf(team_tournament_id)};

        db.insert("teams", null, dataToInsert);

        // Close the database
        db.close();
    }

    /**
     * Used to modify the attributes of a team of given team_tournament_id.
     *
     * @param context is the current application context.
     * @param originalTeamName is the original name of the team.
     * @param name is the new name to give to the name.
     * @param logo is the new logo to give to the team.
     * @param team_tournament_id is the id of the tournament the team is associated with.
     */
    public static void modifyTeam(Context context, String originalTeamName, String name, String logo, int team_tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // If the name is empty
        if(name.equals("")){

            throw new NullPointerException();
        }
        try {

            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put("name", name);
            dataToInsert.put("logo", logo);

            String[] whereArgs = new String[] {originalTeamName, String.valueOf(team_tournament_id)};

            if(db.updateWithOnConflict("teams", dataToInsert, "name =? AND team_tournament_id=?", whereArgs, SQLiteDatabase.CONFLICT_IGNORE) != 1)
                throw new IllegalArgumentException();

            // If the team name is already in use
        }catch (IllegalArgumentException e) {

            throw new IllegalArgumentException();
        }

        // Close the database
        db.close();
    }

    /**
     * Used to modify only the name for a team of given team_tournament_id.
     *
     * @param context is the current application context.
     * @param originalTeamName is the original name of the team.
     * @param name is the new name to give to the name.
     * @param team_tournament_id is the id of the tournament the team is associated with.
     */
    public static void modifyTeamName(Context context, String originalTeamName, String name,  int team_tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // If the name is empty
        if(name.equals("")){

            throw new NullPointerException();
        }
        try {

            ContentValues dataToInsert = new ContentValues();
            dataToInsert.put("name", name);

            String[] whereArgs = new String[] {originalTeamName, String.valueOf(team_tournament_id)};

            if(db.updateWithOnConflict("teams", dataToInsert, "name =? AND team_tournament_id=?", whereArgs, SQLiteDatabase.CONFLICT_IGNORE) == 0)
                throw new IllegalArgumentException();

            // If the team name is already in use
        }catch (IllegalArgumentException e) {

            throw new IllegalArgumentException();
        }

        // Close the database
        db.close();
    }

    /**
     * Used to modify only the logo for a team of given team_tournament_id.
     *
     * @param context is the current application context.
     * @param name is the original name of the team.
     * @param logo is the new logo to give to the team.
     * @param team_tournament_id is the id of the tournament the team is associated with.
     */
    public static void modifyTeamLogo(Context context, String name, String logo, int team_tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Update the team logo
        db.execSQL("UPDATE teams SET logo = '" + logo +"' WHERE name = '" + name
                + "' AND team_tournament_id = " + team_tournament_id);

        // Close the database
        db.close();
    }

    /**
     * Used to delete a team of given team_tournament_id.
     *
     * @param context is the current application context.
     * @param name is the name of team to delete.
     * @param team_tournament_id is the id of the tournament the team is associated with.
     */
    public static void deleteTeam(Context context, String name, int team_tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete the team
        db.execSQL("DELETE FROM teams WHERE name = '" + name + "' AND team_tournament_id = " + team_tournament_id);

        // Close the database
        db.close();
    }

    /**
     * Returns the number of teams currently in a tournament of given tournament_id.
     *
     * @param context is the current application context.
     * @param tournament_id is the of the tournament to get the number of teams from.
     * @return the number of teams, int, currently in the tournament.
     */
    public static int getNumTeamsForTournament(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int numTeams = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM teams WHERE team_tournament_id = " + tournament_id, null);

            c.moveToFirst();
            {
                numTeams = c.getCount();
            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the number of teams
        return numTeams;
    }
    /**
     *  Returns an ArrayList of all the tournaments in the database.
     *
     * @param context the current context.
     * @return the ArrayList of all the tournaments in the database.
     */
    public static ArrayList<String> getTournaments(Context context) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<String> tournaments = new ArrayList<String>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM tournaments", null);

            while (c.moveToNext())
            {
                String name = c.getString(c.getColumnIndex("name"));

                try
                {
                    tournaments.add(name);
                }
                catch (Exception e) {
                    throw new NullPointerException();
                }

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of tournaments
        return tournaments;
    }

    public static String getTeamName(Context context, int team_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        String teamName = "";

        try {

            Cursor c = db.rawQuery("SELECT * FROM teams WHERE team_id = " + team_id, null);

            c.moveToFirst();

            try {
                teamName = c.getString(c.getColumnIndex("name"));
            }
            catch (Exception e) {

                throw new NullPointerException();
            }
        } catch (Exception e) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the logo
        return teamName;
    }

    public static String getTeamLogo(Context context, int team_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        String teamLogo = "";

        try {

            Cursor c = db.rawQuery("SELECT * FROM teams WHERE team_id = " + team_id, null);

            c.moveToFirst();

            try {
                teamLogo = c.getString(c.getColumnIndex("logo"));
            }
            catch (Exception e) {

                throw new NullPointerException();
            }
        } catch (Exception e) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the logo
        return teamLogo;
    }


    /**
     *  Returns an ArrayList of the team names for a tournament of given tournament_id.
     *
     * @param context the current context.
     * @param tournament_id the id of the tournament to get the team names of.
     * @return the ArrayList of the team names of the tournament.
     */
    public static ArrayList<String> getTeamNames(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<String> teamNames = new ArrayList<String>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM teams WHERE team_tournament_id = " + tournament_id, null);

            while (c.moveToNext())
            {
                String name = c.getString(c.getColumnIndex("name"));

                try
                {
                    teamNames.add(name);
                }
                catch (Exception e) {

                    throw new NullPointerException();
                }

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of team names
        return teamNames;
    }

    /**
     *  Returns an ArrayList of the team logos for a tournament of given tournament_id.
     *
     * @param context the current context.
     * @param tournament_id the id of the tournament to get the team logos of.
     * @return the ArrayList of the team logos of the tournament
     */
    public static ArrayList<String> getTeamLogos(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<String> teamLogos = new ArrayList<String>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM teams WHERE team_tournament_id = " + tournament_id, null);

            while (c.moveToNext())
            {
                String logo = c.getString(c.getColumnIndex("logo"));

                try
                {
                    teamLogos.add(logo);
                }
                catch (Exception e) {
                    throw new NullPointerException();
                }

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of team logos
        return teamLogos;
    }


    /**
     * Returns the logo of the team of given name and team_tournament_id.
     *
     * @param context is the current application context.
     * @param teamName is the name of team to get logo of.
     * @param team_tournament_id is id of the tournament the team is associated with.
     * @return the logo for the team.
     */
    public static String getTeamLogo(Context context, String teamName, int team_tournament_id){

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        String logo = teamName;

        try {

            Cursor c = db.rawQuery("SELECT * FROM teams WHERE name = '" + teamName +"' AND team_tournament_id = " + team_tournament_id, null);
            c.moveToFirst();

            try {
                logo = c.getString(c.getColumnIndex("logo"));
            }
            catch (Exception e) {

                throw new NullPointerException();
            }
        } catch (Exception e) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the logo
        return logo;
    }

    /**
     * Returns a list of the name of every tournament in the database.
     *
     * @param context is the current application context.
     * @return an list of the name of every tournament.
     */
    public static ArrayList<String> getTournamentNames(Context context) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<String> tournamentNames = new ArrayList<String>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM tournaments", null);

            while (c.moveToNext())
            {
                String name = c.getString(c.getColumnIndex("name"));

                try
                {
                    tournamentNames.add(name);
                }
                catch (Exception e) {

                    throw new NullPointerException();
                }

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the name of the tournaments
        return tournamentNames;
    }

    /**
     * Returns the status for a tournament of given id.
     * 1: Under Creation, 2: Started.
     *
     * @param context the current application context.
     * @param tournament_id the id of the tournament to get the status of.
     * @return the status for a tournament of given id.
     */
    public static int getTournamentStatus(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int status = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM tournaments WHERE tournament_id = " + tournament_id, null);
            c.moveToFirst();

            try {
                status = c.getInt(c.getColumnIndex("status"));
            }
            catch (Exception e) {

                throw new NullPointerException();
            }
        } catch (Exception e) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the logo
        return status;
    }
    /**
     * Returns a list of the status of every tournament in the database.
     *
     * @param context is the current application context.
     * @return an list of the status of every tournament.
     */
    public static ArrayList<String> getTournamentStatuses(Context context) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<String> tournamentStatus = new ArrayList<String>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM tournaments", null);

            while (c.moveToNext())
            {
                String name = c.getString(c.getColumnIndex("status"));

                try
                {
                    tournamentStatus.add(name);
                }
                catch (Exception e) {
                    throw new NullPointerException();
                }

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of the status
        return tournamentStatus;
    }

    public static void setUpFormat(Context context, int tournament_id, int totalCircuits, int size) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the team
        db.execSQL("INSERT INTO formats (currentRound, totalCircuits, size, format_tournament_id) VALUES "
                + "(0, " + totalCircuits + ", " + size + ", " + tournament_id + ")");

        // Close the database
        db.close();
    }

    public static void setTeamFormatPosition(Context context, int team_tournament_id, String teamName, int formatPosition ) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the team
        db.execSQL("UPDATE teams SET format_position = " + formatPosition + " WHERE "
                + " team_tournament_id = " + team_tournament_id + " AND name = '" + teamName + "'");

        // Close the database
        db.close();
    }

    public static int getFormatId(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
       int format_id = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM formats WHERE format_tournament_id = " + tournament_id, null);

            c.moveToFirst();
                format_id = c.getInt(c.getColumnIndex("format_id"));

        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of the status
        return format_id;
    }

    public static ArrayList<String> getFormatOrderedTeams(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<String> orderedTeams = new ArrayList<String>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM teams WHERE team_tournament_id = " + tournament_id + " ORDER BY format_position", null);

                while (c.moveToNext())
                {
                    String name = c.getString(c.getColumnIndex("name"));

                    try
                    {
                        orderedTeams.add(name);
                    }
                    catch (Exception e) {
                        throw new NullPointerException();
                    }

                }
            } catch (Exception e ) {

                // Do nothing
            }

        // Close the database
        db.close();

        // Return the list of the status
        return orderedTeams;
    }

    public static int getCurrentRound(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int currentRound = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM formats WHERE format_tournament_id = " + tournament_id, null);

            while (c.moveToNext())
            {
                currentRound = c.getInt(c.getColumnIndex("currentRound"));

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of the status
        return currentRound;
    }

    public static void insertRound(Context context, int tournament_id, int size, int round_format_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the team
        db.execSQL("INSERT INTO rounds (size, round_format_id, round_tournament_id) VALUES "
                + "(" + size + ", " + round_format_id + ", " + tournament_id + ")");

        // Close the database
        db.close();
    }

    public static void insertMatch(Context context, int round_id, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the team
        db.execSQL("INSERT INTO matches (updated, match_round_id, match_tournament_id) VALUES "
                + "(0, " + round_id + ", " + tournament_id + ")");

        // Close the database
        db.close();
    }

    public static int getLatestMatchId(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int match_id = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM matches WHERE match_tournament_id = " + tournament_id, null);

            while(c.moveToNext())
            {
                match_id = c.getInt(c.getColumnIndex("match_id"));

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of the status
        return match_id;
    }

    public static int getTeamId(Context context, String teamName, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int team_id = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM teams WHERE team_tournament_id = " + tournament_id + " AND name = '" + teamName + "'", null);

            c.moveToFirst();
            {
                team_id = c.getInt(c.getColumnIndex("team_id"));

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of the status
        return team_id;
    }

    public static void insertMatchTeamScore(Context context, int team_id, int match_id, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the team
        db.execSQL("INSERT INTO match_team_scores (score, match_team_score_team_id, "
                + "match_team_score_match_id, match_team_score_tournament_id) VALUES "
                + "(0, " + team_id + ", " + match_id + ", " + tournament_id + ")");

        // Close the database
        db.close();
    }

    public static void updateMatchTeamScore(Context context, int team_id, int match_id, int score, int isWinner) {


        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Insert the team
            db.execSQL("UPDATE match_team_scores SET score = " + score + ", isWinner = " + isWinner
                    + " WHERE match_team_score_team_id = " + team_id + " AND match_team_score_match_id = " + match_id);
        }catch(Exception e) {
            throw new IllegalArgumentException();
        }
        // Close the database
        db.close();
    }

    public static void updateMatch(Context context, int match_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the team
        db.execSQL("UPDATE matches SET updated = 1 WHERE match_id = " + match_id);

        // Close the database
        db.close();
    }

    public static void incrementCurrentRound(Context context, int format_id, int currentRound) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert the team
        db.execSQL("UPDATE formats SET currentRound = " + currentRound + " WHERE format_id = " + format_id);

        // Close the database
        db.close();
    }

    public static int getTeamNumWin(Context context, String teamName, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        /* Get the information */
        // Get the list of team ids
        int team_id = getTeamId(context, teamName, tournament_id);
        int teamWins = 0;

        try {

            Cursor c = db.rawQuery("SELECT sum(isWinner) FROM match_team_scores WHERE match_team_score_team_id = " + team_id, null);

            c.moveToFirst();
            {
                teamWins = c.getInt(0);

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of the status
        return teamWins;
    }

    public static int getTournamentNumCurrentRound(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int numCurrentRound = -1;

        try {

            Cursor c = db.rawQuery("SELECT count(*) FROM rounds WHERE round_tournament_id = " + tournament_id, null);

            c.moveToFirst();
            {
                numCurrentRound = c.getInt(0);
            }
        } catch (Exception e ) {

            numCurrentRound = 0;
        }

        // Close the database
        db.close();

        // Return the list of the status
        return numCurrentRound;
    }

    public static ArrayList<Integer> getMatchesUpdatedValues(Context context, int currentRound, int match_tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<Integer> matchesUpdatedValues = new ArrayList<>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM matches WHERE match_round_id = " + currentRound
                    + " AND match_tournament_id = " + match_tournament_id, null);

            while (c.moveToNext())
            {
                matchesUpdatedValues.add(c.getInt(c.getColumnIndex("updated")));

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of the status
        return matchesUpdatedValues;
    }

    public static ArrayList<Integer> getRoundMatchIDs(Context context, int round_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<Integer> matchIDs = new ArrayList<>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM matches WHERE match_round_id = " + round_id, null);

            while (c.moveToNext())
            {
                matchIDs.add(c.getInt(c.getColumnIndex("match_id")));

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of the status
        return matchIDs;
    }

    public static ArrayList<Integer> getMatchTeamIDs(Context context, int match_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<Integer> teamIDs = new ArrayList<>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM match_team_scores WHERE match_team_score_match_id = " + match_id, null);

            while (c.moveToNext())
            {

                teamIDs.add(c.getInt(c.getColumnIndex("match_team_score_team_id")));
            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the list of the status
        return teamIDs;
    }

    public static int getRoundID(Context context, int roundNum, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<Integer> roundIDs = new ArrayList<>();
        int currentRoundID = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM rounds WHERE round_tournament_id = " + tournament_id, null);

            while (c.moveToNext())
            {
                roundIDs.add(c.getInt(c.getColumnIndex("round_id")));

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Get the current round id
        currentRoundID = roundIDs.get(roundNum);

        // Close the database
        db.close();

        // Return the list of the status
        return currentRoundID;
    }

    public static int getCurrentRoundID(Context context, int tournament_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        ArrayList<Integer> roundIDs = new ArrayList<>();
        int currentRoundID = -1;

        try {

            Cursor c = db.rawQuery("SELECT * FROM rounds WHERE round_tournament_id = " + tournament_id, null);

            while (c.moveToNext())
            {
                roundIDs.add(c.getInt(c.getColumnIndex("round_id")));

            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Get the current round id
        currentRoundID = roundIDs.get(roundIDs.size() - 1);

        // Close the database
        db.close();

        // Return the list of the status
        return currentRoundID;
    }

    public static int getNumMatchTeamScores(Context context) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int numMatchTeamScores = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM match_team_scores", null);

            c.moveToFirst();
            {
                numMatchTeamScores = c.getCount();
            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the number of teams
        return numMatchTeamScores;
    }

    public static int getTournamentId(Context context, int match_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int tournament_id = 0;

        try {

            Cursor c = db.rawQuery("SELECT * FROM matches WHERE match_id = " + match_id, null);

            c.moveToFirst();
            {
                tournament_id = c.getInt(c.getColumnIndex("match_tournament_id"));
            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the number of teams
        return tournament_id;
    }

    public static int getMatchUpdated(Context context, int match_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int updated = -1;

        try {

            Cursor c = db.rawQuery("SELECT * FROM matches WHERE match_id = " + match_id, null);

            c.moveToFirst();
            {
                updated = c.getInt(c.getColumnIndex("updated"));
            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the number of teams
        return updated;
    }

    public static int getMatchTeamScore(Context context, int team_id, int match_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int score = -1;

        try {

            Cursor c = db.rawQuery("SELECT * FROM match_team_scores WHERE "
                    + "match_team_score_team_id = " + team_id + " AND match_team_score_match_id = " + match_id, null);

            c.moveToFirst();
            {
                score = c.getInt(c.getColumnIndex("score"));
            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the number of teams
        return score;
    }

    public static void giveTeamWin(Context context, int team_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Give the team +3 to score
        db.execSQL("UPDATE teams SET score = score + 3 WHERE team_id = " + team_id);

        // Close the database
        db.close();
    }

    public static void giveTeamTie(Context context, int team_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Give the team +3 to score
        db.execSQL("UPDATE teams SET score = score + 1 WHERE team_id = " + team_id);

        // Close the database
        db.close();
    }

    public static int getTeamScore(Context context, int team_id) {

        // Open the database
        DB dbHelper = new DB(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the information
        int score = -1;

        try {

            Cursor c = db.rawQuery("SELECT * FROM teams WHERE " + "team_id = " + team_id, null);

            c.moveToFirst();
            {
                score = c.getInt(c.getColumnIndex("score"));
            }
        } catch (Exception e ) {

            // Do nothing
        }

        // Close the database
        db.close();

        // Return the score
        return score;
    }
}
