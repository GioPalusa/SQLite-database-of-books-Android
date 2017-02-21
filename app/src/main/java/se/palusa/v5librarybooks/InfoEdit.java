package se.palusa.v5librarybooks;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InfoEdit extends AppCompatActivity {

    long bookId;
    String title, author;
    Button deleteBook, updateBook;
    EditText updateTitle, updateAuthor;
    Database dataSource = MainActivity.dataSource;


    // Find all items and set onClickListeners to buttons
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);

        deleteBook = (Button) findViewById(R.id.buttonDelete);
        updateBook = (Button) findViewById(R.id.buttonUpdate);
        updateTitle = (EditText) findViewById(R.id.updateTitle);
        updateAuthor = (EditText) findViewById(R.id.updateAuthor);
        dataSource.open();

        // bookId need to get a long (since the database requires a long)
        // the key is named "findBook"
        bookId = getIntent().getExtras().getLong("findBook");

        // Get book as an object of type Books
        final Books thisBook = dataSource.getBook(bookId);

        // Update EditText fields with information from database
        updateTitle.setText(thisBook.getName());
        updateAuthor.setText(thisBook.getAuthor());

        // send delete-request to database and send in a Books object
        deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSource.deleteBook(thisBook);
                finish();
            }
        });

        updateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If both fields are empty - make a Toast
                if (updateTitle.getText().toString().isEmpty() && updateAuthor.getText().toString().isEmpty())
                    Toast.makeText(InfoEdit.this, R.string.nothingToEdit, Toast.LENGTH_SHORT).show();
                else {
                // if both fields have information - send to Database.java to process
                    dataSource.updateBook(bookId, updateTitle.getText().toString(), updateAuthor.getText().toString());
                    finish();
                }
            }
        });

    }

    // Open database on start
    @Override
    protected void onStart() {
        super.onStart();
        dataSource.open();
    }

    // close database on pause
    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    // close database on destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }
}
