package com.example.capstonedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class RecordActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance();


        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(new RecyclerViewAdapter());

        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String oldMan = preferences.getString("oldMan", "");

        private ArrayList<Person> telephoneBook = new ArrayList<>();

        public RecyclerViewAdapter() {
            firestore.collection("Users").document(oldMan).collection("message").addSnapshotListener((querySnapshot, firebaseFirestoreException) -> {
                // ArrayList 비워줌
                telephoneBook.clear();

                for (QueryDocumentSnapshot snapshot : querySnapshot) {
                    Person item = snapshot.toObject(Person.class);
                    telephoneBook.add(item);
                }
                notifyDataSetChanged();
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new ViewHolder(view);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View view) {
                super(view);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // ViewHolder 내부에서 이미 뷰를 찾았으므로 다시 찾을 필요 없음
            TextView titleView = holder.itemView.findViewById(R.id.titleTextView);
            TextView contentView = holder.itemView.findViewById(R.id.contentTextView);
            TextView timeView = holder.itemView.findViewById(R.id.timeTextView);


            // 데이터를 설정
            titleView.setText(telephoneBook.get(position).getTitle());
            contentView.setText(telephoneBook.get(position).getContent());

            String timeValue = telephoneBook.get(position).getTime();
            if (timeValue != null && !timeValue.isEmpty()) {
                timeView.setText(timeValue);
            } else {
                timeView.setText("미수행");
            }

        }


        @Override
        public int getItemCount() {
            return telephoneBook.size();
        }
    }
}
