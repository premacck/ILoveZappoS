package com.example.prembros.ilz;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.prembros.ilz.databinding.Bind_product;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ProductPage extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    String fixedStr;
    Product p;
    Bind_product binding;
    ActionBar ab;
    HashMap<String,String> url_maps = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_product_page);
        //Intent intent = getIntent();
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_product_page);
        setSupportActionBar(binding.toolbarProductPage);
        ab = getSupportActionBar();
        if (ab!=null){
            ab.setDisplayHomeAsUpEnabled(true);
        }

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        fixedStr = getIntent().getExtras().getString("fixedStr");
        Thread thread = new Thread(new Runnable() {
            public void run() {
                if (isConnected()) {
                    new HttpAsyncTask().execute(fixedStr);
                }
            }
        });
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_search:
                return true;
            case R.id.action_cart:
                return true;
            default:
                return false;
        }
    }

    private boolean isConnected() {
        ConnectivityManager conman = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conman.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


    public String GET(String URLString) {
        InputStream inputStream = null;
        String result = null;
        try {
            URL url = new URL(URLString);
//           create HttpClient
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            receive response as inputStream
            try {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (Exception uhe) {
                uhe.printStackTrace();
            }
            if (inputStream != null) {
                result = getStringFromInputStream(inputStream);
            } else {
                result = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getStringFromInputStream(InputStream inputStream) {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        binding.slider.stopAutoCycle();
        super.onStop();
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, Product> {

        @Override
        protected Product doInBackground(String... params) {
            String result = GET(params[0]);
            //noinspection MismatchedQueryAndUpdateOfCollection
            JSONParser jp = new JSONParser();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                p = jp.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return p;
        }

        @Override
        protected void onPostExecute(Product p) {
            if (p != null) {
                binding.setProduct(p);
                if (ab!=null) {
                    ab.setTitle(p.getBrandName());
                }
                String url = p.getThumbnailImageUrl().replace("-THUMBNAIL","-MULTIVIEW");
                url_maps.put("i1", url.replace("-t-","-p-"));
                url_maps.put("i2", url.replace("-t-","-1-"));
                url_maps.put("i3", url.replace("-t-","-2-"));
                url_maps.put("i4", url.replace("-t-","-3-"));
                for(String name : url_maps.keySet()){
                    TextSliderView textSliderView = new TextSliderView(ProductPage.this);
                    // initialize a SliderLayout
                    textSliderView
                            .image(url_maps.get(name))
                            .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                            .setOnSliderClickListener(ProductPage.this);

//                    .description(p.getProductName())
                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra",name);

                    binding.slider.addSlider(textSliderView);
                }
                binding.slider.setPresetTransformer(SliderLayout.Transformer.Default);
                binding.slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                binding.slider.setCustomAnimation(new DescriptionAnimation());
                binding.slider.setDuration(5000);
                binding.slider.addOnPageChangeListener(ProductPage.this);
            }
        }
    }
}
