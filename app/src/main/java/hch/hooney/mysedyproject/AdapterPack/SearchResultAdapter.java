package hch.hooney.mysedyproject.AdapterPack;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import hch.hooney.mysedyproject.DO.WordDO;
import hch.hooney.mysedyproject.R;

public class SearchResultAdapter extends RecyclerView.Adapter {
    private final String TAG = SearchResultAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<WordDO> list;

    private TextToSpeech tts;
    private int lastPosition = -1;

    public SearchResultAdapter(Context mContext, ArrayList<WordDO> list) {
        this.mContext = mContext;
        this.list = list;
        this.tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    Log.d(TAG, "TTS onInit");
                    tts.setPitch(1.0f);
                    tts.setSpeechRate(1.0f);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        ResultAdapterHolder holder = new ResultAdapterHolder(view);
        return holder;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ResultAdapterHolder hold = (ResultAdapterHolder) holder;
        final WordDO item = list.get(position);
        if(item != null){
            hold.title.setText(item.getWord());
            hold.atr.setText("   [ "+item.getAtr()+" ]");
            hold.def.setText(item.getDefinition());
            hold.example.setText(item.getExample_sentence());

            hold.tts_uk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speakWord(Locale.UK, item.getWord());
                }
            });

            hold.tts_un.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speakWord(Locale.US, item.getWord());
                }
            });

            if(position == list.size()-1){
                hold.itemView.setPadding(0, 2, 2, 140);
            }

            setAnimation(hold.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // 새로 보여지는 뷰라면 애니메이션을 해줍니다
        if (position > lastPosition) {
            Animation animation = AnimationUtils
                    .loadAnimation(mContext, R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void speakWord(Locale locale, String word){
        tts.setLanguage(locale);
        tts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
    }

    private class ResultAdapterHolder extends RecyclerView.ViewHolder {
        public TextView title, atr, def, example;
        public Button tts_un, tts_uk;
        public ImageButton addMyCard;

        public ResultAdapterHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.item_search_result_word);
            atr = (TextView) itemView.findViewById(R.id.item_search_result_atr);
            def = (TextView) itemView.findViewById(R.id.item_search_result_definition);
            example = (TextView) itemView.findViewById(R.id.item_search_result_exam_sentence);
            tts_un = (Button) itemView.findViewById(R.id.item_search_result_tts_un);
            tts_uk = (Button) itemView.findViewById(R.id.item_search_result_tts_uk);
            addMyCard = (ImageButton) itemView.findViewById(R.id.item_search_result_add);
        }
    }
}
