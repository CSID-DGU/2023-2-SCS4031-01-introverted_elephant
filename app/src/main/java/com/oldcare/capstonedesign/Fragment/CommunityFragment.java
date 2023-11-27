package com.oldcare.capstonedesign.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;
import com.oldcare.capstonedesign.R;
import com.oldcare.capstonedesign.RecordActivity;
import com.oldcare.capstonedesign.StepCounterActivity;
import com.oldcare.capstonedesign.location.LocationActivity;
import com.oldcare.capstonedesign.location.TermsofUseofLocationbasedServicesnActivity;

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
        mapButton.setOnClickListener(view1 -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            SharedPreferences preferences = requireContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
            String uid = preferences.getString("uid", "");
            db.collection("Users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // documentSnapshot이 존재할 때 (사용자 데이터가 존재할 때)

                            // "agree" 필드의 값을 가져옴
                            int agreeValue = documentSnapshot.getLong("agree") != null ?
                                    documentSnapshot.getLong("agree").intValue() : 0;

                            // "agree" 필드의 값이 1인지 확인
                            if (agreeValue == 1) {
                                // 동의한 경우의 처리
                                // 여기에 동의한 경우의 로직 추가
                                Intent intent = new Intent(getActivity(), LocationActivity.class);
                                startActivity(intent);
                            } else {
                                // 동의하지 않은 경우의 처리
                                // 여기에 동의하지 않은 경우의 로직 추가
                                Intent intent = new Intent(getActivity(), TermsofUseofLocationbasedServicesnActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

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