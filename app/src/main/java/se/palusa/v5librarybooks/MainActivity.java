package se.palusa.v5librarybooks;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends ListActivity {

    EditText titleInput, authorInput;
    String inputtitle, inputAuthor;
    Button addBook;
    protected static Database dataSource;
    ListView listView;
    Boolean firstStart = true;
    protected static ArrayAdapter<Books> adapter;
    List<Books> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find EditTexts and buttons
        authorInput = (EditText) findViewById(R.id.authorInput);
        titleInput = (EditText) findViewById(R.id.titleInput);
        addBook = (Button) findViewById(R.id.addBook);

        // Create a sharedPreferences to be able to save state of first start
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Open up a database so data can be read and written to it
        dataSource = new Database(this);
        dataSource.open();
        listView = getListView();

        // Get all books to show in the list. The list is an array list of type Books
        values = dataSource.getAllBooks();
        adapter = new ArrayAdapter<Books>(this, android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);


        // a click listener to take the text from EditText fields and send them to the database
        // to create a new Books object. If one of the fields is empty, a toast is shown
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputtitle = titleInput.getText().toString();
                inputAuthor = authorInput.getText().toString();

                if (!inputAuthor.equals("") && !inputtitle.equals("")) {
                    createBooks(inputtitle, inputAuthor);
                } else {
                    Toast.makeText(MainActivity.this, R.string.needToEnter, Toast.LENGTH_LONG).show();
                }

            }
        });

        // The listview (showing all the books) have a listener, to open the activity info_edit
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, InfoEdit.class);

                // Create a new book getting the position clicked
                Books book = values.get(position);
                // create a long, fetching the id from the books object
                long thisBookId = book.getId();
                // send the id in to the new activity using the key "findBook"
                intent.putExtra("findBook", thisBookId);
                parent.getItemAtPosition(position);
                startActivity(intent);

            }
        });


        // Hard coded start-up titles and authors for first start
        // Then set first start to false, so they won't register again
        if (firstStart) {
            createBooks("Tapeter och solsken", "Janne Andersson");
            createBooks("Böcker jag läst", "Jag");
            createBooks("Statistik över något", "Nisse");
            createBooks("Snö eller regn", "Författare");
            createBooks("Halt eller springa", "Nils Dacke");
            createBooks("Gurkor eller tomater", "Astrid Lindgren");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstStart", false).apply();
        }


    }

        // Method to create books
    void createBooks (String title, String author) {

        // Create a new object of type Books
    Books book = dataSource.createBooks(title, author);
        // Use the adapter to update the listView
    adapter.add(book);
    adapter.notifyDataSetChanged();
        // Clear the textfields to notify user that it has been added
    authorInput.setText("");
    titleInput.setText("");

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

    }

    @Override
    protected void onStart() {
        super.onStart();
        dataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    // After infoEdit finishes, MainActivity resumes
    // Then the database needs to be fetched again to update and show current results
    @Override
    protected void onResume() {
        super.onResume();
        values = dataSource.getAllBooks();
        adapter = new ArrayAdapter<Books>(this, android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



}

