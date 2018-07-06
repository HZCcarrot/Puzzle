package com.example.puzzle.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.puzzle.R;
import com.example.puzzle.model.HistoryBean;
import com.example.puzzle.model.RankBean;
import com.example.puzzle.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class History extends AppCompatActivity {
    private Spinner difficulty;
    private ListView historyList;
    private List historyItems;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        difficulty = (Spinner) findViewById(R.id.history_difficulty);
        historyList = (ListView) findViewById(R.id.history_list);
        historyItems = new ArrayList<HistoryItem>();

        Retrofit retrofit = HttpUtils.getRetrofit();
        HttpUtils.Myapi api = retrofit.create(HttpUtils.Myapi.class);
        api.getAllHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HistoryBean>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(HistoryBean historyBean) {
                        List<HistoryBean.ContentBean> contentBeans = historyBean.getContent();
                        int index = 0;
                        for (HistoryBean.ContentBean contentBean: contentBeans){
                            index ++;
                            String mode = contentBean.getMode() + "×" +  contentBean.getMode();
                            String date = contentBean.getTime();
                            String score = ScoreToTime(contentBean.getScore());
                            historyItems.add(new HistoryItem(mode, date, score));
                        }
                        historyAdapter = new HistoryAdapter(History.this, historyItems);
                        historyList.setAdapter(historyAdapter);
                    }

                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onComplete() { }
                });

    }

    private String ScoreToTime(String score){
        Integer scoreInt = Integer.parseInt(score);
        String second = scoreInt % 60 < 10 ? "0" + String.valueOf(scoreInt % 60):String.valueOf(scoreInt % 60);
        String minute = scoreInt / 60 < 10 ? "0" + String.valueOf(scoreInt / 60):String.valueOf(scoreInt / 60);
        return minute + ":" + second;
    }
}

class HistoryItem{
    String type;
    String date;
    String score;

    public HistoryItem(String type, String date, String score){
        this.type = type;
        this.date = date;
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getScore() {
        return score;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setScore(String score) {
        this.score = score;
    }
}

class HisrotyViewHolder{
    TextView his_type, his_date, his_score;
}

class HistoryAdapter extends BaseAdapter {
    List<HistoryItem> list;
    LayoutInflater inflater;

    public HistoryAdapter(Context context, List<HistoryItem> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    public void update(int index, ListView listview){
        //得到第一个可见item项的位置
        int visiblePosition = listview.getFirstVisiblePosition();
        //得到指定位置的视图，对listview的缓存机制不清楚的可以去了解下
        View view = listview.getChildAt(index - visiblePosition);
        HisrotyViewHolder holder = (HisrotyViewHolder) view.getTag();
        setData(holder,index);
    }
    private void setData(HisrotyViewHolder holder,int index){
        HistoryItem historyItem = list.get(index);
        holder.his_type.setText(historyItem.getType());
        holder.his_date.setText(historyItem.getDate());
        holder.his_score.setText(historyItem.getScore());

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        HisrotyViewHolder viewHolder;
        if(convertView==null){
            if (position <= 2){
                convertView = inflater.inflate(R.layout.rank_item_orange, null);
            }else{
                convertView = inflater.inflate(R.layout.rank_item_white, null);
            }
            viewHolder = new HisrotyViewHolder();
            viewHolder.his_type = (TextView) convertView.findViewById(R.id.his_type);
            viewHolder.his_date = (TextView) convertView.findViewById(R.id.his_date);
            viewHolder.his_score = (TextView) convertView.findViewById(R.id.his_score);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (HisrotyViewHolder) convertView.getTag();
        }
        viewHolder.his_type.setText(list.get(position).getType());
        viewHolder.his_date.setText(list.get(position).getDate());
        viewHolder.his_score.setText(list.get(position).getScore());
        return convertView;
    }

}
