package com.example.firebasetest;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectedCourseActivity extends AppCompatActivity {

    String id;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    TextView tnam,tdesc,tNumberofStud;
    ImageView cimg;
    Button adstud,buttonAttendance;
    StorageReference storageReference;
    Boolean attendaceStarted;
    LinearLayout layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_course);
        attendaceStarted = false;
        tNumberofStud = findViewById(R.id.textNumberOfStudents);
        tnam = findViewById(R.id.textName);
        tdesc = findViewById(R.id.textDescription);
        cimg = findViewById(R.id.coursePic);
        adstud = findViewById(R.id.addstudentsButton);
        storageReference = FirebaseStorage.getInstance().getReference();
        buttonAttendance = findViewById(R.id.buttonAttendance);
        layout = findViewById(R.id.attendance);

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
                Integer numberofstudents = 0;
                if(documentSnapshot.contains("students")){
                   ArrayList<String> students =(ArrayList<String>) (documentSnapshot.get("students"));
                   numberofstudents=students.size();

                }
                if(documentSnapshot.contains("attendanceStarted")){
                    attendaceStarted = (Boolean) documentSnapshot.get("attendanceStarted");
                    if(attendaceStarted){
                        buttonAttendance.setText("Stop Attendance");
                    }else{
                        buttonAttendance.setText("Start Attendance");
                    }
                }
                tNumberofStud.setText("Current number of students: " + numberofstudents.toString());
                tnam.setText(name);
                tdesc.setText(description);
                if(attendaceStarted && documentSnapshot.contains("attendanceList")){
                    ArrayList<String> attendanceList =(ArrayList<String>) (documentSnapshot.get("attendanceList"));
                    int i = 0;

                    for(String userId : attendanceList){
                        final int x = i;
                        fStore.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String name = (String) documentSnapshot.get("fName");
                                TextView fName = new TextView(SelectedCourseActivity.this);
                                fName.setText(name);
                                fName.setId(20+x);
                                layout.addView(fName);

                            }
                        });
                        i++;

                    }
                }

            }

        });



        buttonAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentReference = FirebaseFirestore.getInstance().collection("courses").document(id);
                attendaceStarted=!attendaceStarted;
                if(attendaceStarted){
                    buttonAttendance.setText("Stop Attendance");
                }else{
                    buttonAttendance.setText("Start Attendance");
                }
                Map<String,Object> course = new HashMap<>();
                course.put("attendanceStarted",attendaceStarted);
                course.put("attendanceList",new ArrayList<String>());
                documentReference.update(course);
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