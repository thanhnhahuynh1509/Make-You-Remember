package com.huynhthanhnha.makeyouremember.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.huynhthanhnha.makeyouremember.R;
import com.huynhthanhnha.makeyouremember.database.NotesDatabase;
import com.huynhthanhnha.makeyouremember.models.Note;
import com.huynhthanhnha.makeyouremember.utils.LanguageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotesActivity extends AppCompat {

    private String[] colors = {"#FCA484", "#92F8B0", "#A6D2F9", "#ffd947", "#FFFFFF"};

    private Executor executor = Executors.newSingleThreadExecutor();

    private ImageView btnBack;
    private ImageView btnSaved;
    private ImageView imageNote;
    private TextView textDateTime;
    private TextView textKey;
    private TextView textValue;
    private TextView textDescription;
    private LinearLayout featureLayout;
    private View caret;
    private LinearLayout btnDelete;
    private ImageView btnDeleteImage;

    private AlertDialog deleteAlertDialog;

    private LanguageUtils languageUtils;

    private DateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    private ImageView currentImageColor;
    private int currentPickColor = 5;
    private String currentImagePath;
    private boolean isUpdated = false;
    private Note noteUpdate;

    private final ActivityResultLauncher<Intent> openGalleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult() , (result) -> {
                if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectImageUri = result.getData().getData();

                    try(InputStream is = getContentResolver().openInputStream(selectImageUri)) {
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        currentImagePath = getPathFromUri(selectImageUri);
                    } catch(IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        languageUtils = new LanguageUtils(this);
        btnBack = findViewById(R.id.btnBack);
        btnSaved = findViewById(R.id.btnSaved);
        textDateTime = findViewById(R.id.textDateTime);
        featureLayout = findViewById(R.id.featureLayout);
        imageNote = findViewById(R.id.imageNote);
        caret = findViewById(R.id.caret);
        textKey = findViewById(R.id.textKey);
        textValue = findViewById(R.id.textValue);
        textDescription = findViewById(R.id.textDescription);
        btnDelete = findViewById(R.id.btnDelete);
        btnDeleteImage = findViewById(R.id.btnDeleteImage);

        textDateTime.setText(dateFormat.format(new Date()));

        initFeatureLayout();

        // Btn back
        btnBack.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });


        isUpdated = getIntent().getBooleanExtra("isUpdated", false);
        noteUpdate = (Note) getIntent().getSerializableExtra("note");

        // Btn save
        btnSaved.setOnClickListener(v -> {
            String key = textKey.getText().toString().trim();
            String value = textValue.getText().toString().trim();
            String description = textDescription.getText().toString();
            String dateTime = textDateTime.getText().toString().trim();

            if(key.isEmpty()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.key_not_empty), Toast.LENGTH_LONG).show();
                return;
            }
            if(value.isEmpty()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.value_not_empty), Toast.LENGTH_LONG).show();
                return;
            }
            Note note = null;

            if(isUpdated) {
                note = noteUpdate;
            } else {
                note = new Note();
                note.setDelete(false);
                note.setFavourite(false);
                note.setCorrectTimes(0);
            }
            note.setColor(colors[currentPickColor - 1]);
            note.setDate(dateTime);
            note.setKey(key);
            note.setValue(value);
            note.setDescription(description);
            note.setImage(currentImagePath);
            saveNote(note);
        });

        if(isUpdated) {
            loadNoteToUpdate(noteUpdate);
        }
    }


    private void initFeatureLayout() {
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior =BottomSheetBehavior.from(featureLayout);
        featureLayout.findViewById(R.id.textFeatures).setOnClickListener(v -> {
            if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            else
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        setCaretColor();

        final ImageView imageColor1 = featureLayout.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = featureLayout.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = featureLayout.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = featureLayout.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = featureLayout.findViewById(R.id.imageColor5);

        currentImageColor = imageColor5;

        // Image
        featureLayout.findViewById(R.id.layoutAddImage).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NotesActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                openGallery();
            }
        });

        imageColor1.setOnClickListener(v -> {
            setCheckImageColor(imageColor1, 1);
        });

        imageColor2.setOnClickListener(v -> {
            setCheckImageColor(imageColor2, 2);
        });

        imageColor3.setOnClickListener(v -> {
            setCheckImageColor(imageColor3, 3);
        });

        imageColor4.setOnClickListener(v -> {
            setCheckImageColor(imageColor4, 4);
        });

        imageColor5.setOnClickListener(v -> {
            setCheckImageColor(imageColor5, 5);
        });
    }

    // Handle pick image
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            openGallery();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGalleryLauncher.launch(intent);
    }

    //

    private void setCaretColor() {
        GradientDrawable caretGD = (GradientDrawable) caret.getBackground();
        caretGD.setColor(Color.parseColor(colors[currentPickColor - 1]));
    }

    private void setCheckImageColor(ImageView image, int position) {
        if(currentImageColor != null) {
            currentImageColor.setImageResource(0);
        }
        currentImageColor = image;
        currentPickColor = position;
        image.setImageResource(R.drawable.ic_check);
        setCaretColor();
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if(cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void saveNote(Note note) {
        executor.execute(() -> {
            NotesDatabase.getDatabase(getApplicationContext()).getNoteDao().insert(note);
            finish();
        });
    }

    // load updated note

    private void loadNoteToUpdate(Note note) {
        btnDelete.setVisibility(View.VISIBLE);
        textKey.setText(note.getKey());
        textValue.setText(note.getValue());
        textDescription.setText(note.getDescription());

        if(note.getImage() != null && !note.getImage().isEmpty()) {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImage()));
            imageNote.setVisibility(View.VISIBLE);
            btnDeleteImage.setVisibility(View.VISIBLE);
            currentImagePath = note.getImage();

            btnDeleteImage.setOnClickListener(v -> {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                btnDeleteImage.setVisibility(View.GONE);
                currentImagePath = null;
            });
        }



        for(int i = 0; i < colors.length; i++) {
            if(note.getColor().equals(colors[i])) {
                setCheckImageColor(getImageColorByCurrentPosition(i + 1), i + 1);
                break;
            }
        }

        btnDelete.setOnClickListener(v -> {
            if(deleteAlertDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.delete_alert_layout,
                        (LinearLayout) findViewById(R.id.layoutDeleteNoteContainer), false);
                builder.setView(view);
                deleteAlertDialog = builder.create();
                deleteAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                view.findViewById(R.id.btnTextCancel).setOnClickListener(ve -> {
                    deleteAlertDialog.dismiss();
                });

                view.findViewById(R.id.btnTextDelete).setOnClickListener(ve -> {
                    deleteAlertDialog.dismiss();
                    setStatusDeleteNote(note);
                });
            }
            deleteAlertDialog.show();
        });
    }

    private ImageView getImageColorByCurrentPosition(int position) {
        switch (position) {
            case 1: return featureLayout.findViewById(R.id.imageColor1);
            case 2: return featureLayout.findViewById(R.id.imageColor2);
            case 3: return featureLayout.findViewById(R.id.imageColor3);
            case 4: return featureLayout.findViewById(R.id.imageColor4);
            default: return featureLayout.findViewById(R.id.imageColor5);
        }
    }

    private void setStatusDeleteNote(Note note) {
        executor.execute(() -> {
            NotesDatabase.getDatabase(getApplicationContext()).getNoteDao().toggleDelete(note.getId());
            finish();
        });
    }

}