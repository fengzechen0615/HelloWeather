package com.example.francisfeng.helloweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.francisfeng.helloweather.Databases.City;
import com.example.francisfeng.helloweather.Databases.County;
import com.example.francisfeng.helloweather.Databases.Province;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaFragment extends Fragment {

    public static final int PROVINCE_LEVEL = 0;
    public static final int CITY_LEVEL = 1;
    public static final int COUNTY_LEVEL = 2;

    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    // 省列表
    private List<Province> provinceList;
    // 市列表
    private List<City> cityList;
    // 县列表
    private List<County> countyList;
    // 选中的省份
    private Province selectedProvince;
    // 选中的城市
    private City selectedCity;
    // 当前选中的级别
    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == PROVINCE_LEVEL) {
                    selectedProvince = provinceList.get(position);
                    selectedCities();
                } else if (currentLevel == CITY_LEVEL) {
                    selectedCity = cityList.get(position);
                    selectedCounties();
                } else if (currentLevel == COUNTY_LEVEL) {
                    String countyId = countyList.get(position).getCountyId();
                    // 如果是在主界面，传id
                    if (getActivity() instanceof Welcome) {
                        Intent intent = new Intent(getActivity(), WeatherInformation.class);
                        intent.putExtra("countyId", countyId);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    // 在现实天气界面，关闭滑动菜单，请求天气，更新为新的天气
                    else if (getActivity() instanceof WeatherInformation) {
                        WeatherInformation weatherInformation = (WeatherInformation) getActivity();
                        weatherInformation.drawerLayout.closeDrawers();
                        weatherInformation.requestWeather(countyId);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == COUNTY_LEVEL) {
                    selectedCities();
                } else if (currentLevel == CITY_LEVEL) {
                    selectedProvinces();
                }
            }
        });
        selectedProvinces();
    }

    // 从数据库中查询全国所有的省
    private void selectedProvinces() {
        titleText.setText("CHINA");
        // 第一个界面，没有返回按钮
                backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        dataList.clear();
        for (Province province : provinceList) {
            dataList.add(province.getProvinceName());
        }
        // 更新list
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        currentLevel = PROVINCE_LEVEL;

    }

    // 从数据库中查询选中省内所有的市
    private void selectedCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        dataList.clear();
        for (City city : cityList) {
            dataList.add(city.getCityName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        currentLevel = CITY_LEVEL;

    }

    // 从数据库中查询选中市内所有的县
    private void selectedCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        dataList.clear();
        for (County county : countyList) {
            dataList.add(county.getCountyName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        currentLevel = COUNTY_LEVEL;

    }
}
