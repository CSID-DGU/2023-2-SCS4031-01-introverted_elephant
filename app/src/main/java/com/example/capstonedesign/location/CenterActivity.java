package com.example.capstonedesign.location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.capstonedesign.R;

import java.util.ArrayList;
import java.util.List;

public class CenterActivity extends AppCompatActivity implements SimpleTextAdapter.OnStartDragViewHolderListener {
    private ItemTouchHelper itemTouchHelper;
    private List<String> centerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);

        Intent intent = getIntent();
        List<Institution> receivedAgencyList = (List<Institution>) intent.getSerializableExtra("centers");
        List<Hospital> receivedHospitalList = (List<Hospital>) intent.getSerializableExtra("hospitals");
        if (receivedAgencyList != null) {
            for (Institution institution : receivedAgencyList) {
                // Institution 객체의 각 속성에 접근하는 예제
                StringBuilder sb = new StringBuilder();
                sb.append("기관명 :"+institution.name+"\n");
                sb.append("전화번호 :"+institution.phonenumber+"\n");
                sb.append("주소 :"+institution.address);
                centerList.add(sb.toString());
            }
            for (Hospital Hospital : receivedHospitalList) {
                // Institution 객체의 각 속성에 접근하는 예제
                StringBuilder sb = new StringBuilder();
                sb.append("기관명 :"+ Hospital.placeName+"\n");
                sb.append("전화번호 :"+Hospital.phone+"\n");
                sb.append("주소 :"+Hospital.roadAddressName);
                centerList.add(sb.toString());
            }
        }

        RecyclerView centerListView = findViewById(R.id.center_list);
        centerListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        centerListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        SimpleTextAdapter adapter = new SimpleTextAdapter( centerList, this);
        centerListView.setAdapter(adapter);

        itemTouchHelper = new ItemTouchHelper(new SimpleTextItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(centerListView);

    }

    @Override
    public void onStartDragViewHolder(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }
}