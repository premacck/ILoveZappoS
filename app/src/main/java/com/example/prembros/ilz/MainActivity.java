package com.example.prembros.ilz;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    private GoogleApiClient googleApiClient;
    String searchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        final MaterialEditText searchText = (MaterialEditText) findViewById(R.id.search_text);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(MainActivity.this);
                searchString = searchText.getText().toString();
                Intent intent = new Intent(MainActivity.this, ProductPage.class);
                intent.putExtra("searchString", searchString);
                String fixedString = "https://api.zappos.com/Search?term=" + searchString + "&key=b743e26728e16b81da139182bb2094357c31d331";
                intent.putExtra("fixedStr", fixedString);
                startActivity(intent);
            }
        });

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        AppIndex.AppIndexApi.start(googleApiClient, getIndexApiAction());
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(googleApiClient, getIndexApiAction());
        googleApiClient.disconnect();
    }
}
