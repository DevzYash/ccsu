package com.yash.ccsu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    TextView tv;
    ArrayList<String> newsTitleList, newsDatesList, newsLinksList,mylust;
    ArrayList<DataAdapter> data = new ArrayList<>();
    int listSize;
    RecyclerView recyclerView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.Tv);
        recyclerView = findViewById(R.id.recyclerView);
        Button button = findViewById(R.id.button);
        webView = findViewById(R.id.webview);
        button.setOnClickListener(view -> {
            Log.d("yashyash", newsDatesList.toString());
            Log.d("yashyash", newsTitleList.toString());
            Log.d("yashyash", newsLinksList.toString());
            Log.d("yashyash", String.valueOf(newsLinksList.size()));
            Toast.makeText(this, "Total News are " + newsLinksList.size(), Toast.LENGTH_SHORT).show();

        });
        newsDatesList = new ArrayList<>();
        newsLinksList = new ArrayList<>();
        newsTitleList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        Adapter adapter = new Adapter(data, getApplicationContext());
        recyclerView.setAdapter(adapter);

        if (myUtils.isConnected(MainActivity.this)) {webView.loadUrl("https://www.ccsuniversity.ac.in/ccsum/search-news.php");}
        else {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setMessage("Turn On Your Internet")
                    .setPositiveButton("Okay", null).show();
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new webclient());


    }

    private class webclient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            webView.evaluateJavascript("document.getElementsByClassName(\"table table-bordered table-striped\")[0].childNodes[3].childElementCount", s -> {
                // getting total rows
                listSize = Integer.parseInt(s);
                Log.e("yashyash", String.valueOf(listSize));
                for (int i = 0; i < listSize; i++) {
                    int finalI = i;
                    // getting every single date
                    webView.evaluateJavascript("document.getElementsByClassName(\"table table-bordered table-striped\")[0].childNodes[3].children[" + i + "].getElementsByTagName(\"td\")[0].innerText", s12 -> {
                        s12 = JSONUtil.unescape(s12).replace("\"", "");
                        newsDatesList.add(s12);
                    });
                    // getting every single title
                    webView.evaluateJavascript("document.getElementsByClassName(\"table table-bordered table-striped\")[0].childNodes[3].children[" + i + "].getElementsByTagName(\"td\")[1].innerText", s1 -> {
                        s1 = JSONUtil.unescape(s1).replace("\"", "");
                      newsTitleList.add(s1);
                    });
                    // getting every single link
                    webView.evaluateJavascript("document.getElementsByClassName(\"table table-bordered table-striped\")[0].childNodes[3].children[" + i + "]" + ".getElementsByTagName(\"td\")[1].innerHTML", s13 -> {
                       s13 = JSONUtil.unescape(s13).replace("\"", "");
                       if(s13.contains("news.php")){
                           String newText = s13.substring(8, s13.indexOf("target"));
                           newsLinksList.add("https://www.ccsuniversity.ac.in/ccsum/"+newText);
                       }
                       else{
                           String newText = s13.substring(8, s13.indexOf(">"));
                           newsLinksList.add(newText);
                       }
                        DataAdapter dataAdapter = new DataAdapter(newsDatesList.get(finalI), newsTitleList.get(finalI), (finalI == 0 || finalI == 1) ? (R.drawable.newitem) : (R.drawable.newspaper), newsLinksList.get(finalI));
                        data.add(dataAdapter);
                        Adapter adapter = new Adapter(data, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        Log.e("yashyash","01");
                    });
                    //loop end
                }
            });
            // page loaded
            Toast.makeText(MainActivity.this, "Data Fetch Successfully..", Toast.LENGTH_SHORT).show();
        }
    }


}
