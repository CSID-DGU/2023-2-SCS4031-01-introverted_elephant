package com.oldcare.capstonedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WarningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        TextView link = findViewById(R.id.textView19);
        link.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("https://sable-walrus-280.notion.site/8b9bb038a2ef48558f728c83a589d35e?pvs=4");
            intent.setData(uri);startActivity(intent);
        });
    }
}