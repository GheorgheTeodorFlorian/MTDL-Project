package com.example.firebasetest;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewCourse extends AppCompatActivity {

    FirebaseFirestore fStore;
    Button createCourse;
    EditText courseName,courseDescription;
    Uri imageUri;
    ImageView coursePic;
    StorageReference storageReference;
    String randomKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);
        fStore=FirebaseFirestore.getInstance();
        createCourse=findViewById(R.id.createCourse);
        courseName=findViewById(R.id.courseName);
        courseDescription=findViewById(R.id.courseDescription);
        coursePic = findViewById(R.id.coursePicture);
        storageReference = FirebaseStorage.getInstance().getReference();



        coursePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });


        createCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseNam = courseName.getText().toString().trim();
                String courseDesc = courseDescription.getText().toString().trim();
                if(TextUtils.isEmpty(courseNam)){
                    courseName.setError("Course Name is Required.");
                    return;
                }
                if(TextUtils.isEmpty(courseDesc)){
                    courseDescription.setError("Course Description is required.");
                    return;
                }


                CollectionReference documentReference = fStore.collection("courses");
                Map<String,Object> course = new HashMap<>();
                course.put("CourseName",courseNam);
                course.put("CourseDescription",courseDesc);
                if(randomKey != null){
                    course.put("ImageName",randomKey);
                }

                documentReference.add(course);
                finish();

            }
        });

    }


    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            coursePic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        randomKey = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("images/" + randomKey);


        ref.putFile(imageUri);
    }
}

