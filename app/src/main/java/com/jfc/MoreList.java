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
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Visibility;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.jsoup.Connection;
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
import java.net.SocketTimeoutException;
import java.net.URL;


public class MoreList extends ActionBarActivity {

    private String homeUrl = MainActivity.HOME_URL;

    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_list);

        System.out.println("Starting MoreList");

        layout = (RelativeLayout) findViewById(R.id.moreListLayout);
        layout.setBackgroundColor(Color.parseColor(MainActivity.PRIMARY_COLOR));

        getListItems();
    }

    private void getListItems(){
        System.out.println("Getting list items");
        try {
            File image6 = new File(getFilesDir().getAbsolutePath()+"/"+"listImage6.png");
            if(image6.exists()){
                setList();
            }
            System.out.println("Getting doc");

            Document doc = Jsoup.connect(homeUrl + "/html/list.html").timeout(10000).get();


            Elements images = doc.select(".list_button");
            Elements links = doc.select(".list_link");

            String[] listUrls = new String[100];
            String[] imageLinks = new String[100];
            String[] listText = new String[100];

            System.out.println("Looping images...");
            int count = 0;
            for (Element image : images) {
                imageLinks[count]=image.attr("src");
                count=count+1;
            }

            System.out.println("Looping links...");
            count = 0;
            for (Element link : links) {
                listUrls[count]=link.attr("href");
                listText[count]=link.ownText();
                System.out.println("ownText: "+link.ownText());
                count=count+1;
            }

            //System.out.print("Image Urls: ");
            //for(String url : imageLinks){
            //    System.out.print(url + ",");
            //}

            //System.out.print("Links: ");
            //for(String link : listUrls){
            //    System.out.print(link + ",");
            //}

            //System.out.print("Texts: ");
            //for(String text : listText){
            //    System.out.print(text + ",");
            //}

            final String[] finalListUrls = listUrls;
            final String[] finalImageLinks = imageLinks;
            final String[] finalListText = listText;

            System.out.println("Starting thread");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Getting shared preferences");
                    SharedPreferences sp = getApplication().getSharedPreferences("MainSharedPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor spe = sp.edit();


                    System.out.println("finalImageLinks: ");
                    for(int i=0;i<100;i++){
                        System.out.print(finalImageLinks[i] + ",");
                    }

                    int counter = 0;
                    while(finalImageLinks[counter]!= null && !finalImageLinks[counter].equals("")) {
                        spe.putString("listLink" + counter, finalListUrls[counter]);
                        spe.putString("listText" + counter, finalListText[counter]);
                        downloadFile("listImage" + counter+".png", homeUrl + "html/" + finalImageLinks[counter]);
                        counter = counter+1;
                    }
                    System.out.println("Downloading done, setting list...");
                    spe.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setList();
                        }
                    });

                }
            }).start();
        }catch (IOException e) {
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
                    getListItems();
                }
            });
            popup.show();
        }
    }

    private void setList(){
        ListView listView = new ListView(this);
        listView.setLayoutParams(new ActionBar.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.MATCH_PARENT));

        String[] items = new String[100];
        String[] urls = new String[100];
        final SharedPreferences sp = getApplication().getSharedPreferences("MainSharedPreferences", Context.MODE_PRIVATE);
        int count = 0;
        try {
            while (!sp.getString("listText" + count, "").equals("")) {
                items[count] = sp.getString("listText"+count,"");
                urls[count] = sp.getString("listLink"+count,"");
                count = count+1;
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        ListAdapter listAdapter = new MoreListAdapter(this,new String[count]);
        listView.setAdapter(listAdapter);

        final String[] finalUrls = urls;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = finalUrls[position];
                if (!url.contains("http")) {
                    url = homeUrl + "more/" + url;
                }
                System.out.println("Navigating to: "+url);
                navigateToUrl(url);
            }
        });

        layout.addView(listView);
        ProgressBar loading = (ProgressBar) findViewById(R.id.moreListProgressBar);
        loading.setVisibility(View.GONE);
    }

    public void navigateToUrl(String url){
        Intent intent = new Intent(this, Browser.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    public void downloadFile(String filename, String urlString){
        try {
            System.out.println("Downloading "+urlString+" as "+filename);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(is);
            FileOutputStream fos = MoreList.this.openFileOutput(filename, Context.MODE_PRIVATE);

            ByteArrayOutputStream outstream = new ByteArrayOutputStream();

            bm.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            byte[] byteArray = outstream.toByteArray();

            fos.write(byteArray);
            fos.close();
            //runOnUiThread(new Runnable() {
            //    @Override
            //    public void run() {
            //        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), getFilesDir().getAbsolutePath() + "/" + filename);
            //        imageView.setImageDrawable(bitmapDrawable);
            //    }
            //});
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
