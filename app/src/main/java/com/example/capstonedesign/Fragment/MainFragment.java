package com.example.capstonedesign.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.capstonedesign.MainActivity;
import com.example.capstonedesign.MedicineActivity;
import com.example.capstonedesign.R;
import com.example.capstonedesign.login.LoginActivity;
import com.example.capstonedesign.old_man.OldMainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainFragment extends Fragment {

    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        FloatingActionButton medicineButton = view.findViewById(R.id.medicineButton);
        medicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MedicineActivity.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });

        FloatingActionButton foodButton = view.findViewById(R.id.foodButton);
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버튼이 클릭되었을 때 수행할 작업을 여기에 추가
            }
        });

        FloatingActionButton hospitalButton = view.findViewById(R.id.hospitalButton);
        hospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버튼이 클릭되었을 때 수행할 작업을 여기에 추가
            }
        });


        return view;
        //여기까지 oncreateview
    }
}