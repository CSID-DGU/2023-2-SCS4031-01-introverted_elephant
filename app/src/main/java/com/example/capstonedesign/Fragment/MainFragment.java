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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("user_preferences", MODE_PRIVATE);

        FloatingActionButton medicineButton = view.findViewById(R.id.medicineButton);
        medicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "투약알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), MedicineActivity.class);
                startActivity(intent);

            }
        });

        FloatingActionButton foodButton = view.findViewById(R.id.foodButton);
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "식사알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), MedicineActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton hospitalButton = view.findViewById(R.id.hospitalButton);
        hospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences에 값들 저장
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("title", "병원알림");
                editor.apply();
                Intent intent = new Intent(getActivity(), MedicineActivity.class);
                startActivity(intent);
            }
        });


        return view;
        //여기까지 oncreateview
    }
}