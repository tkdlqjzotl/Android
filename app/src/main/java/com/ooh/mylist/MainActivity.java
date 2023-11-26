package com.ooh.mylist;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    StockListAdapter listAdapter;
    private static final String DELIMITER = "\\|";
    public static ArrayList<ItemCode_Stock> m_arrStockList = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_arrStockList = new ArrayList<>();
        setContentView(R.layout.layout_main_activity);

        listAdapter = new StockListAdapter();


        try {
            InputStream in = getResources().openRawResource(R.raw.m_stock);
            InputStreamReader inr = new InputStreamReader(in, "euc-kr");
            BufferedReader reader = new BufferedReader(inr);
            setItemInfo_Stock(reader);

            String str;
            while ((str = reader.readLine()) != null) {
//                            buf.append(str + "\n");
                System.out.println(str);
            }
            reader.close();


            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0; i<m_arrStockList.size(); i++) {
            listAdapter.addItem(new StockItem(m_arrStockList.get(i).m_sKorName, m_arrStockList.get(i).m_sCode,
                    m_arrStockList.get(i).sLargeDiv,  m_arrStockList.get(i).sSmallDiv));
        }
        listView = findViewById(R.id.my_list);

        listView.setAdapter(listAdapter);
    }

    public static class StockItem {

        public String name;
        public String price;
        public String rate;
        public String change;

        public StockItem(String name, String price, String rate, String change) {
            this.name = name;
            this.price = price;
            this.rate = rate;
            this.change = change;
        }
    }



    public static class StockItemView extends FrameLayout {

        TextView tvName;
        TextView tvPrice;
        TextView tvRate;
        TextView tvChange;


        public StockItemView(Context context) {
            super(context);
            init(context);
        }

        public StockItemView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public void init(Context context) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.list_item_stock, this, true);

            tvName = (TextView) findViewById(R.id.tv_name);
            tvPrice = (TextView) findViewById(R.id.tv_price);
            tvRate = (TextView) findViewById(R.id.tv_crate);
            tvChange = (TextView) findViewById(R.id.tv_change);

        }

        public void setName(String name) {
            tvName.setText(name);
        }

        public void setPrice(String price) {
            tvPrice.setText(price);
        }

        public void setRate(String rate) {
            tvRate.setText(rate);
        }

        public void setChange(String change) {
            tvChange.setText(change);
        }
    }



    public class StockListAdapter extends BaseAdapter {


        ArrayList<StockItem> items = new ArrayList<>();


        public void addItem(StockItem item) {
            items.add(item);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 뷰 객체 재사용
            StockItemView view = null;
            if (convertView == null) {
                view = new StockItemView(getApplicationContext());
            } else {
                view = (StockItemView) convertView;
            }

            StockItem item = items.get(position);
            view.setName(item.name);
            view.setPrice(item.price);
            view.setRate(item.rate);
            view.setChange(item.change);

            return view;
        }
    }

    public static void setItemInfo_Stock(BufferedReader br)
    {
        String thisLine;
        ArrayList<String> tempArraylist;
        try {
            while ((thisLine = br.readLine()) != null) {
                String[] sRead = thisLine.split(DELIMITER);
                ItemCode_Stock item = new ItemCode_Stock();

                item.m_sCode = sRead[0].trim();
                item.m_sKorName = sRead[1].trim();
                String sMarket = sRead[2].trim();
                char cMarket = sMarket.length() <= 0 ? ' ' : sMarket.charAt(0);
                item.m_sFullCode = sRead[3].trim();
                item.sSymbolCode = sRead[4].trim();  /*  & : 기준가발생(권리락, 배당락, 감자, 액면분할, 기준일)
                                                            X : 거래정지
                                                            % : 관리종목
                                                            ! : 투자주의, 경고, 위험, 위험예고, 단기과열(구 이상등급)
                                                            / : 액변병합
                                                            - : 불성실공지
                                                         */
                item.sLargeDiv = sRead[5].trim();
                item.sMidDiv = sRead[6].trim();
                item.sSmallDiv = sRead[7].trim();

                if (cMarket == '1') {
                    // 코스피
                    //item.m_cMarketType = MK_KOSPI_STOCK;
                    //m_mapStock.put(item.m_sCode, item.m_sKorName,item.sLargeDiv);
                    m_arrStockList.add(item);
                } else if (cMarket == '4') {
                    // 코스닥
                    //item.m_cMarketType = MK_KOSDAQ_STOCK;
                    //m_mapStock.put(item.m_sCode, item.m_sKorName);
                    m_arrStockList.add(item);
                }
                // m_mapMapping.put(item.m_sFullCode, item.m_sCode);

                // 주식 전체 추가
                // m_arrAllStock.add(item);
                // m_mapAllStock.put(item.m_sCode, item);
            }
        } catch (IOException e) {
            Log.e("Exception", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        // 코스피/코스닥/ETN만 정렬
        //Collections.sort(m_arrStockList);
        //tempArraylist= new ArrayList<String>();
        // tempArraylist.addAll(m_arrStockList);
    }
    public static class ItemCode_Stock {
        String m_sCode;
        String m_sKorName;
        String m_sFullCode;
        String sSymbolCode;  /*  & : 기준가발생(권리락, 배당락, 감자, 액면분할, 기준일)
                                                            X : 거래정지
                                                            % : 관리종목
                                                            ! : 투자주의, 경고, 위험, 위험예고, 단기과열(구 이상등급)
                                                            / : 액변병합
                                                            - : 불성실공지
                                                         */
        String sLargeDiv;
        String sMidDiv;
        String sSmallDiv;


        public void ItemCode_Stock()
        {
            this.m_sCode = m_sCode;
            this.m_sKorName = m_sKorName;
            this.m_sFullCode = m_sFullCode;
            this.sSymbolCode = sSymbolCode;
            this.sLargeDiv = sLargeDiv;
            this.sMidDiv = sMidDiv;
            this.sSmallDiv = sSmallDiv;

        }
        public String getCode() {
            return this.m_sCode;
        }

        public String getKorName() {
            return this.m_sKorName;
        }

        public String getFullCode() {
            return this.m_sFullCode;
        }
    }
}
