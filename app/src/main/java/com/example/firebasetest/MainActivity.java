package com.example.firebasetest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.grpc.Context;

public class MainActivity extends AppCompatActivity {

    TextView fullName,email,phone,verifyMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button resendCode,courses;
    ImageButton settings;
    ImageView profilepic;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    String userID;
    DocumentReference documentReference,buttonReference;
    DocumentSnapshot checkbutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone = findViewById(R.id.profilePhone);
        email = findViewById(R.id.profileEmail);
        fullName = findViewById(R.id.profileName);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        settings = (ImageButton) findViewById(R.id.imageButton2);
        userID = fAuth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userID);
        buttonReference = fStore.collection("users").document(userID);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        courses = findViewById(R.id.courses);

        profilepic = findViewById(R.id.imageView3);
        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.verifyMsg);

        userId = fAuth.getCurrentUser().getUid();
        FirebaseUser user = fAuth.getCurrentUser();
        resendCode.setVisibility(View.GONE);
        verifyMsg.setVisibility(View.GONE);


          documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                 String picturename = (String) documentSnapshot.get("profilePic");
                 StorageReference ref = storageReference.child("images/" + picturename);
                 ref.getBytes(1024*1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                     @Override
                     public void onSuccess(byte[] bytes) {
                         Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                         profilepic.setImageBitmap(bitmap);
                     }
                 });
             }
         });

        buttonReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String accountType = (String) documentSnapshot.get("accountType");
                System.out.println(accountType);
                if(accountType.equals("Teacher")){
                    courses.setVisibility(View.VISIBLE);
                }
            }
        });


        courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CoursesActivity.class));
            }
        });


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });


        if(user.isEmailVerified()){
            settings.setVisibility(View.VISIBLE);
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,SettingsActivityActivity.class);
                    startActivity(intent);
                }
            });
        }



        if(!user.isEmailVerified()){
            resendCode.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);
            settings.setVisibility(View.INVISIBLE);



            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(),"Verification Email Has been Sent.",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,"onFailure: Email not sent " + e.getMessage());


                        }
                    });
                }
            });
        }

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                phone.setText(documentSnapshot.getString("phone"));
                fullName.setText(documentSnapshot.getString("fName"));
                email.setText(documentSnapshot.getString("email"));
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
            profilepic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        String randomKey = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("images/" + randomKey);

        Map<String,Object> user = new HashMap<>();
        user.put("profilePic",randomKey);
        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"User Profile Picture Updated For: "+ userID);
            }
        });
        ref.putFile(imageUri);
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),login_activity.class));
        finish();
    }
}