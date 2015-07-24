package com.jfc;

import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class Browser extends ActionBarActivity {

    private WebView browserWebView;
    private RelativeLayout layout;
    private ProgressBar spinner;
    private TextView loadingTextView;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        layout = (RelativeLayout) findViewById(R.id.relativeBrowserLayout);
        browserWebView = (WebView) findViewById(R.id.browserWebView);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        loadingTextView = (TextView) findViewById(R.id.loginTextView);
        loadingTextView.setTextColor(Color.WHITE);

        String url = getIntent().getStringExtra("url");
        System.out.println("url to load is: " + url + " , testUrl(url) is: " + testUrl(url));
        if(!testUrl(url)){
            System.out.println("Showing browser...");
            showBrowser();
        }

        layout.setBackgroundColor(Color.parseColor("#0085ad"));

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        browserWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                final String finalUrl = url;
                System.out.println("Url is: " + url + " , spinner.getVisibility() = " + spinner.getVisibility() + " , View.GONE = " + View.GONE);
                if (testUrl(url)) {
                    //Hide browser if visible to login
                    if (spinner.getVisibility() == View.GONE) {
                        hideBrowser();
                    }
                    new Thread(new Runnable() {
                        public void run() {
                            SharedPreferences sp = getApplication().getSharedPreferences("MainSharedPreferences", Context.MODE_PRIVATE);
                            Instrumentation inst = new Instrumentation();

                            //Wait for site to load
                            if (finalUrl.equals("https://jfc.magister.net/#/inloggen")) {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            //Hide keyboard
                            try {
                                if (Browser.this.getCurrentFocus() != null)
                                    inputMethodManager.hideSoftInputFromWindow(Browser.this.getCurrentFocus().getWindowToken(), 0);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            //Enter username
                            inst.sendStringSync(sp.getString("Username", ""));
                            inst.sendCharacterSync(KeyEvent.KEYCODE_TAB);

                            //Hide keyboard again
                            try {
                                if (Browser.this.getCurrentFocus() != null)
                                    inputMethodManager.hideSoftInputFromWindow(Browser.this.getCurrentFocus().getWindowToken(), 0);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            //Enter password
                            inst.sendStringSync(sp.getString("Password", ""));
                            inst.sendCharacterSync(KeyEvent.KEYCODE_ENTER);
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else if (spinner.getVisibility() != View.GONE) {
                    //Show browser again
                    showBrowser();
                }
            }
        });


        WebSettings webSettings = browserWebView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        browserWebView.loadUrl(url);
    }

    private boolean testUrl(String url){
        if(url.equals("https://jfc.itslearning.com/index.aspx") || url.equals("https://jfc.magister.net/#/inloggen")){
            return true;
        }else return false;
    }

    private void showBrowser(){
        System.out.println("Setting browser width");
        browserWebView.getLayoutParams().width = layout.getLayoutParams().width;
        browserWebView.getLayoutParams().height = layout.getLayoutParams().height;
        spinner.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.GONE);
        //browserWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    private void hideBrowser(){
        System.out.println("Hiding browser");
        browserWebView.getLayoutParams().width = 0;
        browserWebView.getLayoutParams().height = 0;
        spinner.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.VISIBLE);
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

        return super.onOptionsItemSelected(item);
    }
}
