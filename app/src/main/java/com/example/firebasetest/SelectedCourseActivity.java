package com.example.firebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SelectedCourseActivity extends AppCompatActivity {

    String id;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    TextView tnam,tdesc;
    ImageView cimg;
    Button adstud;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_course);

        tnam = findViewById(R.id.textName);
        tdesc = findViewById(R.id.textDescription);
        cimg = findViewById(R.id.coursePic);
        adstud = findViewById(R.id.addstudentsButton);
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            //The key argument here must match that used in the other activity
        }

        fStore = FirebaseFirestore.getInstance();
        documentReference = fStore.collection("courses").document(id);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String description = documentSnapshot.get("CourseDescription").toString();
                String imageName = "";
                String name = documentSnapshot.get("CourseName").toString();
                if(documentSnapshot.contains("ImageName")){
                    imageName = documentSnapshot.get("ImageName").toString();
                    StorageReference ref = storageReference.child("images/" + imageName);
                    ref.getBytes(1024*1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            cimg.setImageBitmap(bitmap);
                        }
                    });
                }
                tnam.setText(name);
                tdesc.setText(description);

            }
        });

        adstud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), StudentAddActivity.class);
                i.putExtra("id",id);
                startActivity(i);
            }
        });
    }
}