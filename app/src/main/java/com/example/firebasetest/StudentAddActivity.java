package com.example.firebasetest;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentAddActivity extends AppCompatActivity {
    LinearLayout layout;
    StorageReference storageReference;
    FirebaseFirestore fs;
    Button button;
    DocumentReference documentReference;
    String id;
    ArrayList<String> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_add);
        storageReference = FirebaseStorage.getInstance().getReference();
        fs=FirebaseFirestore.getInstance();
        button = findViewById(R.id.addStudent);
        students = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            //The key argument here must match that used in the other activity
        }
        documentReference = fs.collection("courses").document(id);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> course = new HashMap<>();
                course.put("students",students);
                documentReference.update(course).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"Updated course Students: "+ id);
                        finish();
                    }
                });

            }
        });

        documentReference = fs.collection("courses").document(id);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.contains("students")){
                    students =(ArrayList<String>) (documentSnapshot.get("students"));
                }



            }
        });

        Query q = fs.collection("users");
        q.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int i = 0;
                for(QueryDocumentSnapshot qs : queryDocumentSnapshots){
                    Map<String, Object> map = qs.getData();
                    String name = map.get("fName").toString();
                    String imageName = "";

                    if(map.containsKey("profilePic")){
                        imageName = map.get("profilePic").toString();
                    }

                    String accountType = map.get("accountType").toString();

                    if(accountType.equals("Student")) {
                        tryout(name, imageName, i, qs.getId());
                        i++;
                    }
                }
            }
        });

    }




    private void tryout(String name,String imagename,int index,String id) {
        TextView textNam = new TextView(this);

        ImageView image = new ImageView(this);
        CheckBox check = new CheckBox(this);

        textNam.setTextColor(Color.parseColor("#000000"));
        textNam.setTextSize(30);


        layout = (LinearLayout) findViewById(R.id.rootlayout);
        if(students.contains(id)){
            check.setChecked(true);
        }

        textNam.setId(100 + index);
        check.setId(200+index);
        image.setId(300+index);
        textNam.setText(name);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check.isChecked()){
                    students.add(id);
                }else{
                    students.remove(id);
                }
            }
        });


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
        layout.addView(check);
        layout.addView(image);
        layout.addView(textNam);

    }

}