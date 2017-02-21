package se.palusa.v5librarybooks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giovanni on 2017-02-14.
 * © Giovanni Palusa 2017
 */

class Database extends SQLiteOpenHelper {

    private SQLiteDatabase database;
    private final static String DB_NAME = "BooksDatabase";
    private final static String TABLE_NAME = "LibraryOfBooks";
    private final static String COLUMN_BOOK = "Book";
    private final static String COLUMN_AUTHOR = "Author";
    private final static String  _ID = "ID";

    // set a name for the database using the SQL line
    // "BooksDatabase.db" , here it's cut up so it can be changed easily
    Database(Context context) {
        super(context, "\"" + DB_NAME + ".db", null, 1);
    }

    @Override
    // Create the database using the SQL line
    // CREATE TABLE LibraryOfBooks (ID integer primary key autoincrement, Author varchar not null, Book varchar not null);
    // Then execute the SQL line
    public void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " integer primary key autoincrement, "
                + COLUMN_AUTHOR +  " varchar not null, " + COLUMN_BOOK + " varchar not null);";
        db.execSQL(DATABASE_CREATE);

    }

    // Create a new book in the database. bookName and authorName as Strings are being sent in
    // ContentValues is created to access the database in writeable mode
    // Then set the same values to an object of type Books and return the object
    Books createBooks (String bookName, String authorName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_BOOK, bookName);
        contentValues.put(COLUMN_AUTHOR, authorName);
        long insertID = database.insert(TABLE_NAME, null, contentValues);

        Books book = new Books();
        book.setId(insertID);
        book.setName(bookName);
        book.setAuthor(authorName);
        return book;
    }

    // Send in a books object and use database.delete to remove the object
    // first, we need to fetch the id from the item, so we know what to remove
    // then we can run the command
    void deleteBook (Books books) {
        long id = books.getId();
        database.delete(Database.TABLE_NAME, Database._ID + " = " + id, null);
    }

    // An arrayList of type Books to get all books from the database
    // No data is being sent in
    // A string array named allBooks is being created, filled with "id" and "books"

    List<Books> getAllBooks() {
        List<Books> books = new ArrayList<Books>();
        String[] allBooks = {_ID, COLUMN_BOOK};

        // The cursor points to a specific row in the database
        // It's the cursor that makes the actual query
        // cursor.moveToFirst moves to the top of the database
        Cursor cursor = database.query(Database.TABLE_NAME,
                allBooks, null, null, null, null, null);
        cursor.moveToFirst();

        // Loop through all the rows found in the database
        // add objects containing the info from the database to the arrayList of Books
        // and then return the arrayList
        while (!cursor.isAfterLast()) {
            Books booksShow = booksCursor(cursor);
            books.add(booksShow);
            cursor.moveToNext();
        }
        cursor.close();
        return books;
    }

    // Search for a specific book when id is sent in
    // first use the data columns "id" "author" and "books"
    // The ? sign is switched to the id sent in
    Books getBook (long id) {
        Books searchedBook = new Books();
        String[] data = {_ID, COLUMN_AUTHOR, COLUMN_BOOK };
        String whereClause = _ID + " =  ?";
        String[] whereArgs = { "" + id };

        // The cursor here makes the query
        // The query asks for the table name, looking in the columns named id, author and books
        // then looks for the id the was sent in
        Cursor cursor = database.query(TABLE_NAME,data,whereClause,whereArgs, null,null, null);
        // Move to the first post found
        cursor.moveToFirst();
        // pick the id, author and title and set it to the object of type Books
        // then return the object
        searchedBook.setId(cursor.getLong(0));
        searchedBook.setAuthor(cursor.getString(1));
        searchedBook.setName(cursor.getString(2));

        cursor.close();
        return searchedBook;
    }

    // The update method needs both the existing id, the new title and the new author
    // Some fields might be empty, hence the if:s
    // Create ContentValues to search the database
    // search for the id sent in then use the id to set new data in the database and update
    void updateBook (long id, String newTitle, String newAuthor) {
        ContentValues contentValues = new ContentValues();
        String select = _ID + " = " + id;

        if (newTitle.isEmpty()) {
            contentValues.put(COLUMN_AUTHOR, newAuthor);
        }
        else if (newAuthor.isEmpty()) {
            contentValues.put(COLUMN_BOOK, newTitle);
        }
        else {
            contentValues.put(COLUMN_BOOK, newTitle);
            contentValues.put(COLUMN_AUTHOR, newAuthor);
        }
        database.update(TABLE_NAME, contentValues, select, null);

    }

    // The booksCursor can be used multiple times and sets id and name, and then returns the title
    // This is used in the while loop to present all book titles
    private Books booksCursor(Cursor cursor) {
        Books book = new Books();
        book.setId(cursor.getLong(0));
        book.setName(cursor.getString(1));
        return book;
    }

    // Open up the database
    public void open() throws SQLException {
        database = getWritableDatabase();
    }

    // If the database is updated - drop the old one and create a new (blank) one
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

}
