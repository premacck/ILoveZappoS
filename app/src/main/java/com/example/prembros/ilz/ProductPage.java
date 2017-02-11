package com.example.prembros.ilz;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

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

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class ProductPage extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener{

    boolean hidden = true;
    boolean revealed = false;
    String fixedStr;
    Product p;
    Bind_product binding;
    ActionBar ab;
    String review;
    SupportAnimator animator;
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

        final LinearLayout mRevealView = (LinearLayout) findViewById(R.id.item_added_notification);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_to_cart);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!revealed) {
                    animationForward(fab, mRevealView);
                } else {
                    animationReversed(fab, mRevealView);
                }
            }
        });

        Button back = (Button) findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidden = true;
                revealed = false;
                animationReversed(fab, mRevealView);
            }
        });
        Button viewCart = (Button) findViewById(R.id.button_view_cart);
        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidden = true;
                revealed = false;
            }
        });

        fixedStr = getIntent().getExtras().getString("fixedStr");
                if (isConnected()) {
                    new HttpAsyncTask().execute(fixedStr);
                }
    }

    public void animationForward(FloatingActionButton fab, LinearLayout mRevealView){
        revealed = true;
//        fab.setBackgroundResource(R.drawable.rounded_cancel_button);
        fab.setImageResource(R.drawable.ic_close);

        float pixelDensity = getResources().getDisplayMetrics().density;
        int centerX = mRevealView.getRight();
        centerX -= ((28 * pixelDensity) + (16 * pixelDensity));
        int centerY = mRevealView.getBottom();
        int startRadius = 0;
        int endRadius = (int) Math.hypot(mRevealView.getWidth(), mRevealView.getHeight());
        animator = ViewAnimationUtils.createCircularReveal(mRevealView, centerX, centerY, startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);
//                    animator.start();
        if (hidden){
            mRevealView.setVisibility(View.VISIBLE);
            animator.start();
            hidden = false;
        }
    }

    public void animationReversed(FloatingActionButton fab, final LinearLayout mRevealView){
        if (animator != null && !animator.isRunning()){

//            fab.setBackgroundColor(Color.rgb(0, 188, 212));
            fab.setImageResource(R.drawable.ic_add_shopping_cart);

            animator = animator.reverse();
            animator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    mRevealView.setVisibility(View.GONE);
                    hidden = true;
                    revealed = false;
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
            animator.start();
        }
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
//                binding.slider.setCustomAnimation(new DescriptionAnimation());
                binding.slider.setDuration(5000);
                binding.slider.addOnPageChangeListener(ProductPage.this);

                review = "http://api.zappos.com/Review?productId="+p.getProductId()+"&page=1&key=b743e26728e16b81da139182bb2094357c31d331";

                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        if (isConnected()) {
                            String result = GET(review);
                            //noinspection MismatchedQueryAndUpdateOfCollection
                            JSONParser jp = new JSONParser();
                            JSONObject jsonObject;
                            Review r;
                            try {
                                jsonObject = new JSONObject(result);
                                r = jp.parseRating(jsonObject);
                                binding.setReview(r);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
                thread.start();
            }
        }
    }
}