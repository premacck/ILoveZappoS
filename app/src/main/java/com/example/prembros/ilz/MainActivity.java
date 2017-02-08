package com.example.prembros.ilz;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnFragmentInteractionListener {

    private GoogleApiClient googleApiClient;
    private MaterialAutoCompleteTextView searchText;
    String searchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchText = (MaterialAutoCompleteTextView) this.findViewById(R.id.search_text);

        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        mainActivityFragment.setArguments(getIntent().getExtras());
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, R.anim.slide_out_left,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.add(R.id.main_fragment, mainActivityFragment).commit();

        googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

//        FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fab);
//        fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onFragmentInteractionInMain() {
        hideSoftKeyboard(MainActivity.this);
        searchString = searchText.getText().toString();
        String fixedStr = "https://api.zappos.com/Search?term=" + searchString + "&key=b743e26728e16b81da139182bb2094357c31d331";
        Intent intent = new Intent(MainActivity.this, ProductPage.class);
        intent.putExtra("fixedStr", fixedStr);
        startActivity(intent);
    }
}
