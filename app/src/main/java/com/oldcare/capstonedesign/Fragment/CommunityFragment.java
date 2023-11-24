package com.oldcare.capstonedesign.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.oldcare.capstonedesign.R;
import com.oldcare.capstonedesign.RecordActivity;
import com.oldcare.capstonedesign.StepCounterActivity;
import com.oldcare.capstonedesign.location.LocationActivity;

public class CommunityFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);


        Button memoButton = view.findViewById(R.id.memoButton);
        memoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), RecordActivity.class);
                startActivity(intent);
            }
        });

        Button mapButton = view.findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                startActivity(intent);
            }
        });

        Button stepButton = view.findViewById(R.id.stepButton);
        stepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StepCounterActivity.class);
                startActivity(intent);
            }
        });

        return view;
        //여기까지 oncreateview
    }
}