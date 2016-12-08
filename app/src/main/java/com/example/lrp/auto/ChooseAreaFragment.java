package com.example.lrp.auto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lrp.auto.db.City;
import com.example.lrp.auto.db.County;
import com.example.lrp.auto.db.Province;
import com.example.lrp.auto.util.HttpUtil;
import com.example.lrp.auto.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/7.
 */

public class ChooseAreaFragment extends Fragment {
    private static final String TAG = "ChooseAreaFragment";
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_County = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button back_button;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

    private Province selectProvince;

    private City selectCity;

    private int currentLevel;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area, container, false);

        titleText = (TextView) view.findViewById(R.id.title_text);

        back_button = (Button) view.findViewById(R.id.back_button);

        listView = (ListView) view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);


        listView.setAdapter(adapter);

        Log.i(TAG, "is running");

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long id) {

                if (currentLevel == LEVEL_PROVINCE) {

                    selectProvince = provinceList.get(postion);

                    qureyCities();


                } else if (currentLevel == LEVEL_CITY) {

                    selectCity = cityList.get(postion);
                    qureyCounties();

                } else if (currentLevel == LEVEL_County) {


                    String weatherId = countyList.get(postion).getWeatherId();


                    if (getActivity() instanceof MainActivity) {

                        Intent intent = new Intent(getActivity(), WeatherActivity.class);

                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();


                    } else if (getActivity() instanceof WeatherActivity) {


                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }

                }

            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentLevel == LEVEL_County) {

                    qureyCities();
                } else if (currentLevel == LEVEL_CITY) {

                    qureyProvinces();

                }


            }
        });
        qureyProvinces();

    }


    private void qureyProvinces() {
        Log.d(TAG, "qureyProvinces:  is run");

        titleText.setText("中国");
        back_button.setVisibility(View.GONE);

        provinceList = DataSupport.findAll(Province.class);

        if (provinceList.size() > 0) {


            dataList.clear();
            for (Province province : provinceList) {


                dataList.add(province.getProvinceName());


            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;

        } else {

            String adress = "http://guolin.tech/api/china";
            qureyFromServer(adress, "province");

        }

    }

    private void qureyCities() {
        Log.d(TAG, "qureyCities:  is run");

        titleText.setText(selectProvince.getProvinceName());

        back_button.setVisibility(View.VISIBLE);

        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectProvince.getId())).find(City.class);


        if (cityList.size() > 0) {

            dataList.clear();
            for (City city : cityList) {


                dataList.add(city.getCityName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;


        } else {

            int provinceCode = selectProvince.getProvinceCode();

            String adress = "http://guolin.tech/api/china/" + provinceCode;

            qureyFromServer(adress, "city");


        }

    }


    private void qureyCounties() {
        Log.d(TAG, "qureyCounties:  is run");
        titleText.setText(selectCity.getCityName());

        back_button.setVisibility(View.VISIBLE);


        countyList = DataSupport.where("cityid = ?", String.valueOf(selectCity.getId())).find(County.class);

        if (countyList.size() > 0) {

            dataList.clear();

            for (County county : countyList) {


                dataList.add(county.getcountyName());


            }
            adapter.notifyDataSetChanged();

            listView.setSelection(0);

            currentLevel = LEVEL_County;


        } else {


            int provinceCode = selectProvince.getProvinceCode();


            int cityCode = selectCity.getCityCode();


            String adress = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;

            qureyFromServer(adress, "county");

        }
    }

    private void qureyFromServer(String adress, final String type) {

        Log.d(TAG, "qureyFromServer:  is run");
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(adress, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {


                Log.d(TAG, "onResponse:  is run");
                String responseText = response.body().string();
                boolean result = false;

                if ("province".equals(type)) {

                    result = Utility.handleProvinceResponse(responseText);


                } else if ("city".equals(type)) {

                    result = Utility.handleCityResponse(responseText, selectProvince.getId());


                } else if ("county".equals(type)) {

                    result = Utility.handleCountyResponse(responseText, selectCity.getId());


                }

                if (result) {


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            closeProgressDialog();
                            if ("province".equals(type)) {

                                qureyProvinces();

                            } else if ("city".equals(type)) {
                                qureyCities();

                            } else if ("conty".equals(type)) {

                                qureyCounties();
                            }


                        }
                    });

                }


            }

            @Override
            public void onFailure(Call call, IOException e) {


                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {

                        closeProgressDialog();

                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();


                    }
                });


            }
        });


    }

    private void showProgressDialog() {
        if (progressDialog == null) {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载 。。。。");
            progressDialog.setCanceledOnTouchOutside(false);

        }
        progressDialog.show();


    }

    private void closeProgressDialog() {
        if (progressDialog != null) {

            progressDialog.dismiss();


        }

    }
}
