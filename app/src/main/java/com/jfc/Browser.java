package com.jfc;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class Browser extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        WebView browserWebView = (WebView) findViewById(R.id.browserWebView);
        browserWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                final String finalUrl = url;
                if(url.equals("https://jfc.itslearning.com/index.aspx") || url.equals("https://jfc.magister.net/#/inloggen")){
                    new Thread(new Runnable() {
                        public void run() {
                            SharedPreferences sp = getApplication().getSharedPreferences("MainSharedPreferences", Context.MODE_PRIVATE);
                            Instrumentation inst = new Instrumentation();
                            if(finalUrl.equals("https://jfc.itslearning.com/index.aspx")) {
                                inst.sendCharacterSync(KeyEvent.KEYCODE_TAB);
                                inst.sendCharacterSync(KeyEvent.KEYCODE_TAB);
                            }
                            //inst.sendStringSync(sp.getString("Username",""));
                            //inst.sendCharacterSync(KeyEvent.KEYCODE_TAB);
                            //inst.sendStringSync(sp.getString("Password",""));
                            //inst.sendCharacterSync(KeyEvent.KEYCODE_ENTER);
                        }
                    }).start();
                }
            }
        });
        WebSettings webSettings = browserWebView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        browserWebView.loadUrl(getIntent().getStringExtra("url"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browser, menu);
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
}
