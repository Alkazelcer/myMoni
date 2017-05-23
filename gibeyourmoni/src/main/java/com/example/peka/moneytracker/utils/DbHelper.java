package com.example.peka.moneytracker.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.peka.moneytracker.models.Expense;
import com.example.peka.moneytracker.models.Tag;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peka on 21.03.17.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + "expenses " + " (" +
                    " id " + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " name " + " TEXT," +
                    " price" + " DOUBLE," +
                    " date " + " INTEGER)";
    private static final String SQL_CREATE_TAGS = "CREATE TABLE IF NOT EXISTS tag (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name TEXT)";

    private static final String SQL_CREATE_EXPENSE_TO_TAG = "CREATE TABLE IF NOT EXISTS expense_to_tag (" +
            " expense_id INTEGER, " +
            " tag_id INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + "expenses";

    private static final String SQL_DELETE_TAG = "DROP TABLE IF EXISTS " + "tag";
    private static final String SQL_DELETE_EXPENSE_TO_TAG = "DROP TABLE IF EXISTS " + "expense_to_tag";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MoneyTracker.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_TAGS);
        db.execSQL(SQL_CREATE_EXPENSE_TO_TAG);
    }

    public void droptagg(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_EXPENSE_TO_TAG);
    }

    public void dropAll(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_TAG);
        db.execSQL(SQL_DELETE_EXPENSE_TO_TAG);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
       // db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }

    public List<Expense> selectInDates(SQLiteDatabase db, DateTime startDate, DateTime endDate) {
        List<Expense> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from expenses where date>? " +
                " AND date<? order by date ASC", new String[] { ""+ startDate.getMillis(), "" + endDate.getMillis()});


        while(cursor.moveToNext()) {
            Expense expense = new Expense();
            expense.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            expense.setDate(new DateTime(cursor.getLong(cursor.getColumnIndexOrThrow("date"))));
            expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            expense.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
            list.add(expense);
        }
        cursor.close();


        return list;
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deleteExpense(SQLiteDatabase db, Expense expense) {
        deleteExpense(db, expense.getId());
    }

    public void deleteExpense(SQLiteDatabase db, int id) {
        db.execSQL("DELETE FROM "+" expenses "+" WHERE "+"id"+"="+id);
    }

    public long addExpense(SQLiteDatabase db, Expense expense, List<Tag> tags) {
//        db.execSQL("Insert into expenses (name, date) values(" + name + ", " + date + ")");

        // Gets the data repository in write mode

// Create a new map of values, where column names are the keys
        ContentValues expenseValues = new ContentValues();
        expenseValues.put("name", expense.getName());
        expenseValues.put("date", expense.getDate().getMillis());
        expenseValues.put("price", expense.getPrice());

// Insert the new row, returning the primary key value of the new row
        long expenseId = db.insert("expenses", null, expenseValues);

        for (Tag tag : tags) {
            ContentValues tagValues = new ContentValues();
            tagValues.put("expense_id", expenseId);
            tagValues.put("tag_id", tag.getId());

            db.insert("expense_to_tag", null, tagValues);

        }
        return expenseId;
    }

    public List<Expense> getExpenses(SQLiteDatabase db) {
        List<Expense> list = new ArrayList<>();

        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
               "id", "name", "date", "price"
        };

// Filter results WHERE "title" = 'My Title'
        String selection = " = ?";
        String[] selectionArgs = { "My Title" };

// How you want the results sorted in the resulting Cursor
        String sortOrder = "date" + " DESC";

        Cursor cursor = db.query(
                "expenses",                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(cursor.moveToNext()) {
            Expense expense = new Expense();
            expense.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            expense.setDate(new DateTime(cursor.getLong(cursor.getColumnIndexOrThrow("date"))));
            expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            expense.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
            list.add(expense);
        }
        cursor.close();


        return list;
    }

    public List<Tag> getTags(SQLiteDatabase db) {
        List<Tag> list = new ArrayList<>();

        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                "id", "name"
        };

        String sortOrder = "name" + " ASC";

        Cursor cursor = db.query(
                "tag",                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(cursor.moveToNext()) {
            Tag tag = new Tag();
            tag.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            tag.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            list.add(tag);
        }
        cursor.close();


        return list;
    }

    public long addTag(SQLiteDatabase db, Tag tag) {
//        db.execSQL("Insert into expenses (name, date) values(" + name + ", " + date + ")");

        // Gets the data repository in write mode

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("name", tag.getName());
        long id = db.insert("tag", null, values);
        tag.setId(id);
// Insert the new row, returning the primary key value of the new row
        return id;
    }

    public List<Tag> selectTagsByExpense(SQLiteDatabase db, Expense expense) {
        List<Tag> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("select tag.id as id, tag.name as name from expense_to_tag join tag on " +
                "tag.id = expense_to_tag.tag_id where expense_to_tag.expense_id = ?",
                new String[] { "" + expense.getId()});


        while(cursor.moveToNext()) {
            Tag tag = new Tag();
            tag.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            tag.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            list.add(tag);
        }
        cursor.close();


        return list;
    }


}