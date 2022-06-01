package com.example.firebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentSelectedCourseActivity extends AppCompatActivity {


    String id;

    DocumentReference documentReference;
    FirebaseFirestore fStore;
    TextView tnam,tdesc,tNumberofStud;
    ImageView cimg;
    Button mark;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_selected_course);
        mark = findViewById(R.id.buttonMark);
        fAuth = FirebaseAuth.getInstance();
        tNumberofStud = findViewById(R.id.textNumberOfStudents);
        tnam = findViewById(R.id.textName);
        tdesc = findViewById(R.id.textDescription);
        cimg = findViewById(R.id.coursePic);
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            //The key argument here must match that used in the other activity
        }


        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentReference = fStore.collection("courses").document(id);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.contains("attendanceStarted")){
                            Boolean check = (Boolean) documentSnapshot.get("attendanceStarted");
                            if(check){
                                mark.setVisibility(View.VISIBLE);
                            }else{
                                mark.setVisibility(View.GONE);
                                return;
                            }
                           ArrayList<String> attendanceList =(ArrayList<String>) (documentSnapshot.get("attendanceList"));
                            if(!attendanceList.contains(fAuth.getCurrentUser().getUid())){
                             attendanceList.add(fAuth.getCurrentUser().getUid());
                                Map<String,Object> course = new HashMap<>();
                                course.put("attendanceList",attendanceList);
                                documentReference.update(course);
                            }
                        }
                    }
                });

            }
        });

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
                Integer numberofstudents = 0;
                if(documentSnapshot.contains("students")){
                    ArrayList<String> students =(ArrayList<String>) (documentSnapshot.get("students"));
                    numberofstudents=students.size();
                }

                tNumberofStud.setText("Current number of students: " + numberofstudents.toString());
                tnam.setText(name);
                tdesc.setText(description);




                if(documentSnapshot.contains("attendanceStarted")){
                    Boolean check = (Boolean) documentSnapshot.get("attendanceStarted");
                    if(check){
                        mark.setVisibility(View.VISIBLE);
                    }else{
                        mark.setVisibility(View.GONE);
                    }
                }

            }
        });
    }
}