package com.example.firebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class StudentCoursesActivity extends AppCompatActivity {

    LinearLayout layout;
    StorageReference storageReference;
    FirebaseFirestore fs;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_courses);
        fs = FirebaseFirestore.getInstance();
        storageReference =  FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        Query q = fs.collection("courses");
        q.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int i = 0;
                for(QueryDocumentSnapshot qs : queryDocumentSnapshots){
                    Map<String, Object> map = qs.getData();
                    String description = map.get("CourseDescription").toString();
                    String imageName = "";
                    String name = map.get("CourseName").toString();
                    if(map.containsKey("ImageName")){
                        imageName = map.get("ImageName").toString();
                    }
                    if(map.containsKey("students")){
                        ArrayList<String> students = (ArrayList<String>) map.get("students");
                        if(!students.contains(fAuth.getCurrentUser().getUid())){
                            continue;
                        }
                    }else{
                        continue;
                    }
                    tryout(name,description,imageName,i,qs.getId());
                    i++;
                }
            }
        });

    }

    private void tryout(String name,String desc,String imagename,int index,String id) {
        TextView textNam = new TextView(this);
        TextView textDesc = new TextView(this);
        ImageView image = new ImageView(this);
        Button but = new Button(this);
        but.setText("Open Course");
        textNam.setTextColor(Color.parseColor("#000000"));
        textNam.setTextSize(50);
        textDesc.setTextColor(Color.parseColor("#000000"));

        layout = (LinearLayout) findViewById(R.id.rootlayout);


        textNam.setId(100 + index);
        textDesc.setId(200 + index);
        image.setId(300+index);
        but.setId(400+index);
        textDesc.setText(desc);
        textNam.setText(name);

        if(!imagename.equals("")) {
            StorageReference ref = storageReference.child("images/" + imagename);
            ref.getBytes(1024 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setImageBitmap(bitmap);
                }
            });
        }

        layout.addView(textNam);
        layout.addView(textDesc);
        layout.addView(image);
        layout.addView(but);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), StudentSelectedCourseActivity.class);
                i.putExtra("id",id);
                startActivity(i);



            }
        });





    }
}