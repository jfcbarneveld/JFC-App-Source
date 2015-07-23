package com.jfc;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.DimenRes;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    public String homeUrl = "http://jfcbarneveld.github.io/";

    //Define image buttons
    public ImageView button1;
    public ImageView button2;
    public ImageView button3;
    public ImageView button4;
    public ImageView button5;
    public ImageView button6;

    public String[] buttonLinks;

    TextView more;
    TextView settings;
    LinearLayout linearLayoutHorizontal;

    String screenOrientation;

    int width = 0;
    int height = 0;

    LinearLayout llVer1;
    LinearLayout llVer2;
    LinearLayout llVer3;
    LinearLayout llHor;

    RelativeLayout rootlayout;

    boolean rerendered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkForLogin();

        //Create wrapcontent layoutparams for linearlayouts
        LinearLayout.LayoutParams llParamsWrapContent = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //Create all imageviews
        button1 = new ImageView(this);
        button2 = new ImageView(this);
        button3 = new ImageView(this);
        button4 = new ImageView(this);
        button5 = new ImageView(this);
        button6 = new ImageView(this);

        //Get display size
        getNewDims();

        //Create layouts
        llVer1 = new LinearLayout(this);
        llVer1.setOrientation(LinearLayout.VERTICAL);
        llVer2 = new LinearLayout(this);
        llVer2.setOrientation(LinearLayout.VERTICAL);
        llVer3 = new LinearLayout(this);
        llVer3.setOrientation(LinearLayout.VERTICAL);

        llVer1.setLayoutParams(llParamsWrapContent);
        llVer2.setLayoutParams(llParamsWrapContent);
        llVer3.setLayoutParams(llParamsWrapContent);

        //Set buttons padding
        button1.setPadding(10, 10, 10, 10);
        button2.setPadding(10, 10, 10, 10);
        button3.setPadding(10, 10, 10, 10);
        button4.setPadding(10, 10, 10, 10);
        button5.setPadding(10, 10, 10, 10);
        button6.setPadding(10, 10, 10, 10);



        //Get images
        getImages();
        setImageListeners();


        //Main layout
        llHor = new LinearLayout(this);
        llHor.setOrientation(LinearLayout.HORIZONTAL);
        llHor.setLayoutParams(llParamsWrapContent);
        llHor.setGravity(Gravity.CENTER_HORIZONTAL);
        llHor.setBackgroundColor(Color.parseColor("#0085ad"));

        if (screenOrientation.equals("landscape")){
            llVer1.addView(button1);
            llVer1.addView(button2);
            llVer2.addView(button3);
            llVer2.addView(button4);
            llVer3.addView(button5);
            llVer3.addView(button6);
            llHor.addView(llVer3);
        }else{ //Portrait
            llVer1.addView(button1);
            llVer1.addView(button2);
            llVer1.addView(button3);
            llVer2.addView(button4);
            llVer2.addView(button5);
            llVer2.addView(button6);
        }
        llHor.addView(llVer1);
        llHor.addView(llVer2);

        LinearLayout linearLayoutVertical = new LinearLayout(this);
        linearLayoutVertical.setOrientation(LinearLayout.VERTICAL);
        linearLayoutHorizontal = new LinearLayout(this);
        linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutHorizontal.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //Create buttons
        settings = new TextView(this);
        more = new TextView(this);
        settings.setGravity(Gravity.CENTER);
        more.setGravity(Gravity.CENTER);
        settings.setTextColor(Color.WHITE);
        more.setTextColor(Color.WHITE);
        settings.setTextSize(20);
        more.setTextSize(20);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(width/2, ViewGroup.LayoutParams.WRAP_CONTENT);
        more.setLayoutParams(buttonParams);
        settings.setLayoutParams(buttonParams);
        more.setText(getResources().getString(R.string.more));
        settings.setText(getResources().getString(R.string.settings));
        linearLayoutHorizontal.addView(settings);
        linearLayoutHorizontal.addView(more);

        linearLayoutVertical.addView(llHor);
        linearLayoutVertical.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayoutVertical.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayoutHorizontal.setId(R.id.linearLayoutHorizontal);
        LinearLayout divider = new LinearLayout(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));
        divider.setBackgroundColor(Color.WHITE);
        linearLayoutVertical.addView(divider);
        linearLayoutVertical.setId(R.id.linearLayoutVertical);
        linearLayoutVertical.addView(linearLayoutHorizontal);

        rootlayout = new RelativeLayout(this);
        rootlayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rootlayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        rootlayout.setBackgroundColor(Color.parseColor("#0085ad"));
        rootlayout.addView(linearLayoutVertical);
        setContentView(rootlayout);
    }

    private void setButtonSizes(){
        //Set button size
        int size;
        //height = rootlayout.getHeight() - dpToPx(48);
        System.out.println("Root height: "+rootlayout.getHeight());
        System.out.println("more.getHeight() = "+more.getHeight());
        height = rootlayout.getHeight() - more.getHeight();
        try {
            if ((float)width / (float)height > 1) { //Landscape
                if ((width / 3) < (height / 1.81)) {
                    size = (width / 3) - 20;
                    System.out.println("Size 1");
                } else {
                    size = (int) ((height / 1.81) - 20);
                    System.out.println("Size 2");
                }
            } else { //Portrait
                if ((width / 2) > (height / 3.3)) {
                    size = (width / 2) - 20;
                    System.out.println("Size 3");
                } else {
                    size = (int) ((height / 3.3) - 20);
                    System.out.println("Size 4");
                }
            }

            //Set buttons to correct size
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(size,size);
            button1.setLayoutParams(buttonLayoutParams);
            button2.setLayoutParams(buttonLayoutParams);
            button3.setLayoutParams(buttonLayoutParams);
            button4.setLayoutParams(buttonLayoutParams);
            button5.setLayoutParams(buttonLayoutParams);
            button6.setLayoutParams(buttonLayoutParams);
        }catch (ArithmeticException e){
            e.printStackTrace();
        }

        int linearLayoutHorizontalHeight = linearLayoutHorizontal.getLayoutParams().height;
        more.getLayoutParams().height = linearLayoutHorizontalHeight;
        settings.getLayoutParams().height = linearLayoutHorizontalHeight;
    }

    private void checkForLogin(){
        SharedPreferences sp = getApplication().getSharedPreferences("MainSharedPreferences", Context.MODE_PRIVATE);
        if(!sp.getBoolean("AppLoggedIn",false)){
            final Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getNewDims();
        setButtonSizes();
        setNewLayout();
        System.out.println("Config changed");
        System.out.println("Orientation = " + screenOrientation);
    }

    private void setNewLayout() {
        System.out.println("Setting new layout with orientation " + screenOrientation);
        llVer1.removeAllViews();
        llVer2.removeAllViews();
        llVer3.removeAllViews();
        llHor.removeView(llVer3);
        if (screenOrientation.equals("landscape")){
            System.out.println("Setting landscape rows");
            llVer1.addView(button1);
            llVer1.addView(button2);
            llVer2.addView(button3);
            llVer2.addView(button4);
            llVer3.addView(button5);
            llVer3.addView(button6);
            llHor.addView(llVer3);
        }else{ //Portrait
            System.out.println("Setting portrait rows");
            llVer1.addView(button1);
            llVer1.addView(button2);
            llVer1.addView(button3);
            llVer2.addView(button4);
            llVer2.addView(button5);
            llVer2.addView(button6);
        }
    }

    private void getNewDims(){
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        width = point.x;
        height = point.y;
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);

        //Landscape or portrait?
        if ((float)width / (float)height > 1) { //Landscape
            screenOrientation = "landscape";
        } else { //Portrait
            screenOrientation = "portrait";
        }
        System.out.println("Orientation is now " + screenOrientation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    public void getImages(){
        try {
            File image6 = new File(getFilesDir().getAbsolutePath()+"/"+"startImage6.png");
            if(image6.exists()){
                setImages();
            }
            Document doc = Jsoup.connect(homeUrl+"index.html").get();
            Elements buttons = doc.select(".buttons");
            Elements links = doc.select(".buttons_link");
            String[] buttonUrls = new String[10];
            buttonLinks = new String[10];
            int count = 0;
            for (Element button : buttons) {
                buttonUrls[count]=button.attr("src");
                count=count+1;
            }
            count = 0;
            System.out.println("links length: "+links.size());
            for (Element link : links) {
                System.out.println("link: "+link.attr("href"));
                buttonLinks[count]=link.attr("href");
                count=count+1;
            }
            System.out.print("Urls: ");
            for(String url : buttonUrls){
                System.out.print(url + ",");
            }

            System.out.print("Links: ");
            for(String link : buttonLinks){
                System.out.print(link + ",");
            }
            SharedPreferences sp = getApplication().getSharedPreferences("MainSharedPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor spe = sp.edit();
            spe.putString("startImageLink1",buttonLinks[0]);
            spe.putString("startImageLink2", buttonLinks[1]);
            spe.putString("startImageLink3",buttonLinks[2]);
            spe.putString("startImageLink4",buttonLinks[3]);
            spe.putString("startImageLink5",buttonLinks[4]);
            spe.putString("startImageLink6",buttonLinks[5]);
            spe.apply();
            downloadFile("startImage1", homeUrl + buttonUrls[0], button1);
            downloadFile("startImage2",homeUrl+buttonUrls[1],button2);
            downloadFile("startImage3",homeUrl+buttonUrls[2],button3);
            downloadFile("startImage4",homeUrl+buttonUrls[3],button4);
            downloadFile("startImage5",homeUrl+buttonUrls[4],button5);
            downloadFile("startImage6",homeUrl+buttonUrls[5],button6);
        }catch (IOException e){
            e.printStackTrace();
            AlertDialog.Builder popup = new AlertDialog.Builder(this);
            popup.setTitle("Er is iets misgegaan...");
            popup.setMessage("Heb je wifi of gegevensverbinding?");
            popup.setNegativeButton("Sluiten", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            popup.setNeutralButton("Probeer opnieuw", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getImages();
                }
            });
        }
    }

    private void setImageListeners(){
        final SharedPreferences sp = getApplication().getSharedPreferences("MainSharedPreferences", Context.MODE_PRIVATE);
        System.out.println("Setting onclicklisteners");
                button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("sp.getString geeft: "+sp.getString("startImageLink1", ""));
                try {
                    if (!sp.getString("startImageLink1", "").equals("")) {
                        navigateToUrl(sp.getString("startImageLink1", ""));
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("sp.getString geeft: "+sp.getString("startImageLink2", ""));
                try {
                    if (!sp.getString("startImageLink2", "").equals("")) {
                        navigateToUrl(sp.getString("startImageLink2", ""));
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("sp.getString geeft: "+sp.getString("startImageLink3", ""));
                try {
                    if (!sp.getString("startImageLink3", "").equals("")) {
                        navigateToUrl(sp.getString("startImageLink3", ""));
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("sp.getString geeft: "+sp.getString("startImageLink4", ""));
                try {
                    if (!sp.getString("startImageLink4", "").equals("")) {
                        navigateToUrl(sp.getString("startImageLink4", ""));
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("sp.getString geeft: "+sp.getString("startImageLink5", ""));
                try {
                    if (!sp.getString("startImageLink5", "").equals("")) {
                        navigateToUrl(sp.getString("startImageLink5", ""));
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("sp.getString geeft: "+sp.getString("startImageLink6", ""));
                try {
                    if (!sp.getString("startImageLink6", "").equals("")) {
                        navigateToUrl(sp.getString("startImageLink6", ""));
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void navigateToUrl(String url){
        Intent intent = new Intent(this, Browser.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void setImages(){
        BitmapDrawable bitmapDrawable1 = new BitmapDrawable(getResources(), getFilesDir().getAbsolutePath()+"/"+"startImage1.png");
        button1.setImageDrawable(bitmapDrawable1);
        BitmapDrawable bitmapDrawable2 = new BitmapDrawable(getResources(), getFilesDir().getAbsolutePath()+"/"+"startImage2.png");
        button1.setImageDrawable(bitmapDrawable2);
        BitmapDrawable bitmapDrawable3 = new BitmapDrawable(getResources(), getFilesDir().getAbsolutePath()+"/"+"startImage3.png");
        button1.setImageDrawable(bitmapDrawable3);
        BitmapDrawable bitmapDrawable4 = new BitmapDrawable(getResources(), getFilesDir().getAbsolutePath()+"/"+"startImage4.png");
        button1.setImageDrawable(bitmapDrawable4);
        BitmapDrawable bitmapDrawable5 = new BitmapDrawable(getResources(), getFilesDir().getAbsolutePath()+"/"+"startImage5.png");
        button1.setImageDrawable(bitmapDrawable5);
        BitmapDrawable bitmapDrawable6 = new BitmapDrawable(getResources(), getFilesDir().getAbsolutePath()+"/"+"startImage6.png");
        button1.setImageDrawable(bitmapDrawable6);
    }

    private void downloadFile(String filename, String urlString, ImageView imageView){
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(is);
            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);

            ByteArrayOutputStream outstream = new ByteArrayOutputStream();

            bm.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            byte[] byteArray = outstream.toByteArray();

            fos.write(byteArray);
            fos.close();
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), getFilesDir().getAbsolutePath()+"/"+filename);
            imageView.setImageDrawable(bitmapDrawable);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            System.out.println("Getting image from "+urldisplay);
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
