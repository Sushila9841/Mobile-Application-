package com.collegeproj.journeyjournal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class JournalAdapter extends ArrayAdapter<JournalInfo> {

    Context context;

    public JournalAdapter(@NonNull Context context, ArrayList<JournalInfo> list) {
        super(context, 0, list);
        this.context = context;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_item_layout, null);
        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.description);
        TextView date = view.findViewById(R.id.date);
        ImageView imageView = view.findViewById(R.id.image);

        JournalInfo info = getItem(position);

        title.setText(info.title);
        description.setText(info.description);
        date.setText(info.date + " | " + info.location);
        if (info.image != null)
            imageView.setImageBitmap(Utility.getBitmap(info.image));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,JournalDetailActivity.class);
                intent.putExtra("id",info.id);
                context.startActivity(intent);
            }
        });
        return view;

    }
}
