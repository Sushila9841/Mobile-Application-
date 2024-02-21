package com.collegeproj.journeyjournal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class AddJorurnalActivity extends AppCompatActivity {

    EditText title, description, location;
    TextView date;

    ImageView image1;
    Bitmap bitmap1;
    Uri outputFileUri;
    int SELECT_PHOTO1 = 132;

    DbHelper dbHelper;

    int from;
    JournalInfo info;
    String id;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jorurnal);
        sharedPreferences = getSharedPreferences("userinfo", 0);

        from = getIntent().getIntExtra("from", 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHelper = new DbHelper(this);

        date = findViewById(R.id.date);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        location = findViewById(R.id.location);
        image1 = findViewById(R.id.image1);
        if (from == 1) {
            id = getIntent().getStringExtra("id");
            info = dbHelper.getJournalInfo(id);
            title.setText(info.title);
            description.setText(info.description);
            date.setText(info.date);
            location.setText(info.location);
            if (info.image != null)
                image1.setImageBitmap(Utility.getBitmap(info.image));
            ((Button) findViewById(R.id.add)).setText("Update");
        }

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.isEmptyFieldValid(AddJorurnalActivity.this, title, "Enter title value")
                        && Utility.isEmptyFieldValid(AddJorurnalActivity.this, description, "Enter description value")
                        && Utility.isEmptyFieldValid(AddJorurnalActivity.this, date, "Choose Date")
                        && Utility.isEmptyFieldValid(AddJorurnalActivity.this, location, "Enter location value")
                ) {
                    saveJournal();
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatepickerDialog();
            }
        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseImageDialog(AddJorurnalActivity.this);
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

    public void showDatepickerDialog() {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.date_picker_dialog, null);
        DatePicker datePicker = view.findViewById(R.id.datepicker);
        datePicker.setMaxDate(System.currentTimeMillis());
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateValue = datePicker.getYear() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth();
                date.setText(dateValue);
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setTitle("Choose Date");
        dialog.show();

    }

    public void saveJournal() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title.getText().toString());
        contentValues.put("date", date.getText().toString());
        contentValues.put("description", description.getText().toString());
        contentValues.put("location", location.getText().toString());
        contentValues.put("userid", sharedPreferences.getString("userid", ""));
        if (bitmap1 != null)
            contentValues.put("image", Utility.getBlob(bitmap1));
        if (from == 0) {
            dbHelper.insertJournal(contentValues);
            Toast.makeText(this, "Journal added.", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updateJournal(info.id, contentValues);
            Toast.makeText(this, "Journal Updated.", Toast.LENGTH_SHORT).show();

        }
        finish();
    }

    public void showChooseImageDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.camera_gallery_dialog, null);
        view.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(AddJorurnalActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 101);
                    return;
                }
                capturePhoto();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage(SELECT_PHOTO1);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();

    }

    private void capturePhoto() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/JourneyJournal/";
        File newdir = new File(dir);
        newdir.mkdirs();
        String file = dir + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";


        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        }

        outputFileUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(cameraIntent, 101);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        capturePhoto();
    }

    public void getImage(int i) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, i);
    }

    private Bitmap decodeUri(Uri selectedImage) {

        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage),
                    null, o);

// The new size we want to scale to
            final int REQUIRED_SIZE = 400;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            bitmap1 = decodeUri(outputFileUri);
            try {
                bitmap1 = Utility.rotateImageIfRequire(this, bitmap1, outputFileUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            image1.setImageBitmap(bitmap1);


        } else if (requestCode == SELECT_PHOTO1 && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            bitmap1 = decodeUri(selectedImage);
            image1.setImageBitmap(bitmap1);
        }

    }
}