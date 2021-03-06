package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.AppConstants;
import com.sam_chordas.android.stockhawk.service.HistoryStockBean;
import com.sam_chordas.android.stockhawk.service.MyConnection;
import com.sam_chordas.android.stockhawk.service.Parser;
import com.sam_chordas.android.stockhawk.service.VolleyRequest;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by PriyamSaikia on 21-05-2016.
 */
public class DetailActivity extends AppCompatActivity implements MyConnection.IMyConnection {
    private static final String TAG = DetailActivity.class.getSimpleName();
    @Bind(R.id.linechart)
    LineChart lineChart;
    @Bind(R.id.helloworld)
    TextView helloworld;
    @Bind(R.id.progressbar_detail)
    ProgressBar progressBar;
    private String mSymbol;
    private String mStartDate;
    private String mEndDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lineChart.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Calendar now = Calendar.getInstance();
        String month = String.valueOf(now.get(Calendar.MONTH));
        if (month.length() > 1) {
        } else {
            month = "0" + month;
        }
        mStartDate = now.get(Calendar.YEAR) + "-" + month + "-" + 01;
        mEndDate = now.get(Calendar.YEAR) + "-" + month + "-" + 28;

        helloworld.setText(getIntent().getStringExtra(AppConstants.BUNDLE_STOCK));
        mSymbol = getIntent().getStringExtra(AppConstants.BUNDLE_STOCK);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDetailedGraph();
    }

    private void getDetailedGraph() {
        String url = String.format(AppConstants.REQUEST_STOCK_HISTORY, mSymbol, mStartDate, mEndDate);
        VolleyRequest.sendRequest(this, AppConstants.BASE_URL_HISTORY +
                        URLEncoder.encode(url)
                        + AppConstants.URL_FINALISER_HISTORY
                , this, 0);
    }

    @OnClick(R.id.tv_btn_switch)
    public void onSwitchClick(View view) {
        getDetailedGraph();
    }

    @Override
    public void onSuccess(String response, int requestId) {
        Parser parser = new Parser();
        ArrayList<HistoryStockBean> arrayList = parser.getHistoryData(response);
        lineChart.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        setUpGraph(arrayList);
    }

    private void setUpGraph(ArrayList<HistoryStockBean> arrayList) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = arrayList.size() - 1; i >= 0; i--)
            entries.add(new Entry(Float.valueOf(arrayList.get(i).getAdjusted_close()), i));

        LineDataSet dataset = new LineDataSet(entries, this.getText(R.string.history) + "");

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = arrayList.size() - 1; i >= 0; i--)
            labels.add(arrayList.get(i).getDate());

        LineData data = new LineData(labels, dataset);
        lineChart.setData(data); // set the data and list of lables into chart
        lineChart.invalidate();
    }

    @Override
    public void onFailure(String error, int requestId) {
        Toast.makeText(this, getText(R.string.problem), Toast.LENGTH_SHORT).show();
        lineChart.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
