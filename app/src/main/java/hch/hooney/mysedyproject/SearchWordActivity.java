package hch.hooney.mysedyproject;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import hch.hooney.mysedyproject.AdapterPack.SearchResultAdapter;
import hch.hooney.mysedyproject.Application.MySedyApplication;
import hch.hooney.mysedyproject.DO.WordDO;

public class SearchWordActivity extends AppCompatActivity {
    private final String TAG = SearchWordActivity.class.getSimpleName();

    private ImageButton back_btn, search_btn;
    private EditText wordEdit;
    private RecyclerView recyclerView;

    private ArrayList<WordDO> list;
    private boolean isCommit;

    private Thread searchThread;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 101:
                    Toast.makeText(getApplicationContext(), "단어가 원형인지 또는 네트워크가 연결되어있느지 확인해주세요.", Toast.LENGTH_SHORT).show();
                    isCommit = false;
                    searchThread = null;
                    break;
                case 102:
                    //이후
                    recyclerView.getAdapter().notifyDataSetChanged();
                    isCommit = false;
                    searchThread = null;
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_word);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(getIntent().getStringExtra("word") != null){
            wordEdit.setText(getIntent().getStringExtra("word"));
        }
    }

    @Override
    protected void onDestroy() {
        if(searchThread!=null){
            if(searchThread.isAlive()){
                searchThread.interrupt();
            }
            searchThread = null;
        }

        super.onDestroy();
    }

    private void init(){
        isCommit = false;
        list = new ArrayList<>();

        back_btn = (ImageButton) findViewById(R.id.search_back_btn);
        search_btn = (ImageButton) findViewById(R.id.search_commit);
        wordEdit = (EditText) findViewById(R.id.search_main_edit);
        recyclerView = (RecyclerView) findViewById(R.id.search_result_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new SearchResultAdapter(getApplicationContext(), list));

        setEvents();
    }

    private Thread initSearchThread(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                String result = getRequest(buildURL(wordEdit.getText().toString()));
                if(!parseDictionary(result)){
                    msg.what = 101;
                }else{
                    msg.what = 102;
                    msg.obj = result;
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    private void setEvents(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCommit){
                    isCommit = true;
                    commit();
                }
            }
        });

        wordEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    try{
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }catch (Exception e){
                        Log.d(TAG, "soft keyboard state is hidden.");
                    }


                    isCommit = true;
                    commit();
                    return true;
                }else{
                    return false;
                }
            }
        });
    }

    private void commit(){
        list.clear();
        searchThread = initSearchThread();
        searchThread.start();
    }
    private boolean parseDictionary(String res){
        try{
            JSONArray root = new JSONObject(res).getJSONArray("results");
            for(int index = 0 ; index < root.length() ; index ++){
                JSONObject child_node_1 = root.getJSONObject(index);
                JSONArray lexicalEntries = child_node_1.getJSONArray("lexicalEntries");
                for(int index2 = 0 ; index2 < lexicalEntries.length();index2++){
                    JSONObject child_node_2 = lexicalEntries.getJSONObject(index2);
                    Log.d(TAG, child_node_2.toString());
                    String s_atr = child_node_2.getString("lexicalCategory");
                    String s_word = child_node_2.getString("text");
                    JSONArray entries = child_node_2.getJSONArray("entries");
                    for(int index3 = 0 ; index3 < entries.length() ; index3++){
                        JSONObject child_node_3 = entries.getJSONObject(index3);
                        JSONArray senses = child_node_3.getJSONArray("senses");
                        for(int index4 = 0 ; index4 < senses.length() ; index4++){
                            JSONObject child_node_4 = senses.getJSONObject(index4);
                            parseWordDO(child_node_4, s_atr, s_word);
                            //SubSenses;
                            if(!child_node_4.isNull("subsenses")){
                                JSONArray subsenses = child_node_4.getJSONArray("subsenses");
                                for(int no = 0 ; no < subsenses.length() ; no++){
                                    parseWordDO(subsenses.getJSONObject(no), s_atr, s_word);
                                }
                            }
                        }
                    }
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void parseWordDO(JSONObject json, String atr, String word) throws JSONException{
        WordDO item = new WordDO();
        String s_def = "";
        if(!json.isNull("definitions")){
            JSONArray def = json.getJSONArray("definitions");

            for(int no = 0 ; no < def.length() ; no++){
                s_def+=def.getString(no)+"\n";
            }
        }else{
            JSONArray def = json.getJSONArray("crossReferenceMarkers");

            for(int no = 0 ; no < def.length() ; no++){
                s_def+=def.getString(no)+"\n";
            }
        }

        item.setDefinition(s_def);
        String s_exam = "";
        if(!json.isNull("examples")){
            JSONArray exam = json.getJSONArray("examples");

            for(int no = 0 ; no < exam.length() ; no++){
                JSONObject node = exam.getJSONObject(no);
                s_exam+="- "+node.getString("text")+"\n";
            }
        }else{
            s_exam = "No example sentences.";
        }

        item.setExample_sentence(s_exam);
        item.setWord(word);
        item.setAtr(atr);
        list.add(item);
    }

    private String buildURL(String word){
        final String language="en";
        return  "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word.toLowerCase();
    }

    private String getRequest(String link){
        try{
            URL url = new URL(link);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application");
            urlConnection.setRequestProperty("app_id", getOxfordID());
            urlConnection.setRequestProperty("app_key", getOxfordKey());

            // read the output from the server
            BufferedReader reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder=new StringBuilder();

            String line = null;
            while ((line=reader.readLine()) !=null){
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public native String getOxfordKey();
    public native String getOxfordID();
}
