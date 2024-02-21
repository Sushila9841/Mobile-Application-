package com.collegeproj.journeyjournal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class JournalDetailActivity extends AppCompatActivity {

    JournalInfo info;
    TextView title, date, description;
    ImageView edit, delete, imageView;
    DbHelper dbHelper;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("id");
        dbHelper = new DbHelper(this);
        date = findViewById(R.id.date);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        delete = findViewById(R.id.delete);
        edit = findViewById(R.id.edit);
        imageView = findViewById(R.id.image);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JournalDetailActivity.this, AddJorurnalActivity.class);
                intent.putExtra("from", 1);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        info = dbHelper.getJournalInfo(id);
        title.setText(info.title);
        description.setText(info.description);
        date.setText(info.date + "|" + info.location);
        if (info.image != null) {
            imageView.setImageBitmap(Utility.getBitmap(info.image));
        }
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Journal");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHelper.deleteJournal(info.id);
                Toast.makeText(JournalDetailActivity.this, "Journal Deleted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}