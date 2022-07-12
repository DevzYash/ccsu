package com.yash.ccsu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    WebView webView;

    ArrayList<String> newsTitleList, newsDatesList, newsLinksList,examTitleList,examDateList,examLinkList;
    ArrayList<DataAdapter> data = new ArrayList<>();

    int notificationListSize,examsListSize;
    RecyclerView recyclerView;
    Button button;
    MaterialCardView notificationCard, examCard, revisedExamCard;
    LinearLayout homeLayout,recyclerLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findingViews();
        design();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new webclient());
        myUtils.progressDialog(MainActivity.this, R.layout.progressdialog, false);

        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, String.valueOf(data.size()), Toast.LENGTH_SHORT).show();
                if(data.size()>0){
                    data.clear();
                }
                homeLayout.setVisibility(View.GONE);
                recyclerLayout.setVisibility(View.VISIBLE);
                webView.loadUrl("https://www.ccsuniversity.ac.in/ccsum/search-news.php");
                myUtils.showProgressDialog();


            }
        });

        examCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, String.valueOf(data.size()), Toast.LENGTH_SHORT).show();
                if(data.size()>0){
                    data.clear();
                }
                Log.e("yashyash", String.valueOf(examLinkList));
                homeLayout.setVisibility(View.GONE);
                recyclerLayout.setVisibility(View.VISIBLE);
                webView.loadUrl("https://www.ccsuniversity.ac.in/ccsum/Category.php?Id=c9f0f895fb98ab9159f51fd0297e236d");
                myUtils.showProgressDialog();
            }
        });


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
        examDateList = new ArrayList<>();
        examLinkList = new ArrayList<>();
        examTitleList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        Adapter adapter = new Adapter(data, getApplicationContext());
        recyclerView.setAdapter(adapter);

        if (!myUtils.isConnected(MainActivity.this)) {
            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setMessage("Turn On Your Internet")
                    .setPositiveButton("Okay", null).show();
        }



    }

    private void findingViews() {
        recyclerView = findViewById(R.id.recyclerView);
        button = findViewById(R.id.button);
        webView = findViewById(R.id.webview);
        notificationCard = findViewById(R.id.notificationCard);
        examCard = findViewById(R.id.examCard);
        revisedExamCard = findViewById(R.id.revisedExamCard);
        homeLayout = findViewById(R.id.homeLayout);
        recyclerLayout = findViewById(R.id.recyclerViewParent);

    }

    private void design() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private class webclient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.equals("https://www.ccsuniversity.ac.in/ccsum/search-news.php")) {
                webView.evaluateJavascript("document.getElementsByClassName(\"table table-bordered table-striped\")[0].childNodes[3].childElementCount", s -> {
                    // getting total rows
                    notificationListSize = Integer.parseInt(s);
                    for (int i = 0; i < notificationListSize; i++) {
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
                            if (s13.contains("news.php")) {
                                String newText = s13.substring(8, s13.indexOf("target"));
                                newsLinksList.add("https://www.ccsuniversity.ac.in/ccsum/" + newText);
                            } else {
                                String newText = s13.substring(8, s13.indexOf(">"));
                                newsLinksList.add(newText);
                            }
                            DataAdapter dataAdapter = new DataAdapter(newsDatesList.get(finalI), newsTitleList.get(finalI), (finalI == 0 || finalI == 1) ? (R.drawable.newitem) : (R.drawable.newspaper), newsLinksList.get(finalI));
                            data.add(dataAdapter);
                            Adapter adapter = new Adapter(data, getApplicationContext());
                            recyclerView.setAdapter(adapter);

                        });
                        //loop end
                    }
                });
            }
            else if(url.equals("https://www.ccsuniversity.ac.in/ccsum/Category.php?Id=c9f0f895fb98ab9159f51fd0297e236d")){
                webView.evaluateJavascript("document.getElementsByClassName(\"col-md-6\")[0].getElementsByClassName(\"table table-bordered table-striped\")[0].childNodes[3].childElementCount", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        examsListSize = Integer.parseInt(s);
                        for (int i = 0; i < examsListSize; i++) {
                            int finalI = i;
                            // getting every single title
                            webView.evaluateJavascript("document.getElementsByClassName(\"col-md-6\")[0].getElementsByClassName(\"table table-bordered table-striped\")[0].childNodes[3].children[" + i + "].getElementsByTagName(\"td\")[0].innerText", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    s = JSONUtil.unescape(s).replace("\"","");
                                    examTitleList.add(s);
                                }
                            });
                            // getting every single date
                            webView.evaluateJavascript("document.getElementsByClassName(\"col-md-6\")[0].getElementsByClassName(\"table table-bordered table-striped\")[0].childNodes[3].children[" + i + "].getElementsByTagName(\"td\")[1].innerText", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    s = JSONUtil.unescape(s).replace("\"","");
                                    examDateList.add(s);
                                }
                            });
                            // getting every single link
                            webView.evaluateJavascript("document.getElementsByClassName(\"col-md-6\")[0].getElementsByClassName(\"table table-bordered table-striped\")[0].childNodes[3].children[" + i + "].getElementsByTagName(\"td\")[2].children[0].innerHTML", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    s = JSONUtil.unescape(s).replace("\"", "");
                                    if (s.contains("news.php")) {
                                       String newText = s.substring(s.indexOf("href")+5,s.indexOf("target"));
                                        examLinkList.add("https://www.ccsuniversity.ac.in/ccsum/" + newText);
                                    } else {
                                        String newText = s.substring(8, s.indexOf(">"));
                                        examLinkList.add(newText);
                                    }
                                    DataAdapter dataAdapter = new DataAdapter(examDateList.get(finalI), examTitleList.get(finalI), (finalI == 0 || finalI == 1) ? (R.drawable.newitem) : (R.drawable.newspaper), examLinkList.get(finalI));
                                    data.add(dataAdapter);
                                    Adapter adapter = new Adapter(data, getApplicationContext());
                                    recyclerView.setAdapter(adapter);
                                }
                            });



                        }
                    }
                });

            }
            // page loaded
            myUtils.dismissProgressDialog();
        }
    }

    @Override
    public void onBackPressed() {
        if (recyclerLayout.getVisibility()==View.VISIBLE){
            homeLayout.setVisibility(View.VISIBLE);
            recyclerLayout.setVisibility(View.GONE);
        }
        else{
            super.onBackPressed();
        }

    }
}
