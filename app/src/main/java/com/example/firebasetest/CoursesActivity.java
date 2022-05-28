package com.example.firebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.Map;

public class CoursesActivity extends AppCompatActivity {

    Button createCourses;
    LinearLayout layout;
    StorageReference storageReference;
    FirebaseFirestore fs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        fs = FirebaseFirestore.getInstance();
        createCourses = findViewById(R.id.createCourses);
        storageReference =  FirebaseStorage.getInstance().getReference();


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
                    tryout(name,description,imageName,i);
                    i++;
                }
            }
        });

        createCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),NewCourse.class));
            }
        });


    }










    private void tryout(String name,String desc,String imagename,int index) {
        TextView textNam = new TextView(this);
        TextView textDesc = new TextView(this);
        ImageView image = new ImageView(this);
        Button but = new Button(this);

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

                    }
                });





        }




}