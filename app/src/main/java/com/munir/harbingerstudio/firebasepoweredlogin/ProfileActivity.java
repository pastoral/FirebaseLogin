package com.munir.harbingerstudio.firebasepoweredlogin;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.munir.harbingerstudio.firebasepoweredlogin.model.AppUser;
//import com.nguyenhoanglam.imagepicker.model.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import static android.R.id.progress;


public class ProfileActivity extends BaseActivity {
    private String name, email, uid, editedEmail, userProvider;

    private TextView userName, userEmail, changeEmail, userLocation, phoneNumber;
    private ImageView userPhoto;
    private ProfileAlertBuilder profileAlertBuilder;
    private CoordinatorLayout coordinate_profile;
    private Button btnChangePic, button_change_password;
    private DatabaseReference databaseReference, dbUserRef;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public Map<String, Object> userDataMap = new HashMap<String, Object>();
    public final int REQUEST_CODE_PICKER = 123;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference, profilepicReference;
    //uri to store file
    private Uri filePath;
    public AppUser appUser;
    public static final int permsRequestCode = 20;
    public static String[] permisionList = { "android.permission.ACCESS_FINE_LOCATION" , "android.permission.ACCESS_COARSE_LOCATION",
                                            "android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE",
                                            "android.permission.WRITE_EXTERNAL_STORAGE" , "android.permission.READ_PHONE_STATE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ProfileActivity.super.requestAppPermissions(permisionList, R.string.runtime_permissions_txt, permsRequestCode);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbUserRef = databaseReference.child("users");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getInstance().getReference();

        //showProgressDialog();
        userName = (TextView) findViewById(R.id.userName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        userPhoto = (ImageView) findViewById(R.id.userPhoto);

        userLocation = (TextView) findViewById(R.id.userLocation);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);

        coordinate_profile = (CoordinatorLayout) findViewById(R.id.coordinate_profile);
        button_change_password = (Button) findViewById(R.id.button_change_password);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {

        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbUserRef.orderByKey().equalTo(user.getUid()).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //appUser = postSnapshot.getValue(AppUser.class);
                    showProgressDialog("Loading user information", ProfileActivity.this);
                    userDataMap = (HashMap<String, Object>) postSnapshot.getValue();
                    appUser = postSnapshot.getValue(AppUser.class);
                    updateUI();
                    getExistingImei();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void logout(View v) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
    }


    public void updateUI() {
        if (userDataMap.size() > 0) {
            hideProgressDialog();
            userName.setText(userDataMap.get("name").toString());
            userEmail.setText(userDataMap.get("email").toString());
            if (userDataMap.get("photoURL").toString() != null) {
                Glide.with(this).load(userDataMap.get("photoURL")).fitCenter().into(userPhoto);
            }
            userLocation.setText(userDataMap.get("location").toString());
            phoneNumber.setText(userDataMap.get("phoneNumber").toString());
            userProvider = userDataMap.get("providerId").toString();
            if (userProvider.equals("password")) {
                button_change_password.setVisibility(View.VISIBLE);
            }
        }
    }


    public void updatePassword(View v) {

        AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
        final EditText edittext = new EditText(ProfileActivity.this);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        profileAlertBuilder = new ProfileAlertBuilder(ProfileActivity.this);

        alert = profileAlertBuilder.alertForEditText("",
                "MySymphony Profile",
                "Change your Password",
                edittext);


        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //Editable YouEditTextValue = edittext.getText();
                //OR
                //String YouEditTextValue = edittext.getText().toString();
                if (edittext.getText() == null || edittext.getText().length() == 0) {
                    showSnack(coordinate_profile, "Password could not be empty");
                    return;
                }
                if (edittext.getText().length() < 6) {
                    showSnack(coordinate_profile, "Minimum password length is 6 character");
                    return;
                }
                String editedPassword = edittext.getText().toString();
                user.updatePassword(editedPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("User_Password_Change:", "User password updated.");
                            showSnack(coordinate_profile, "Password changed successfully");
                        }
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        AlertDialog d = alert.show();
        if (edittext.getText().toString() == null || edittext.getText().toString().isEmpty()) {
            d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);


    }

    public void deleteAccount(View view) {
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("User_Account_Deletion", "User account deleted.");
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                });

    }

    private boolean validEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }



    public void editProfile(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
        Context context = ProfileActivity.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText displayNameEditText = new EditText(context);
        displayNameEditText.setHint(R.string.name_hint);
        if (userDataMap.get("name") == null) {
            displayNameEditText.setHint(R.string.name_hint);
        } else {
            displayNameEditText.setText(userDataMap.get("name").toString());
        }


        final EditText phoneEditText = new EditText(context);
        phoneEditText.setHint(R.string.hint_phone_number);
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        if (userDataMap.get("phoneNumber") == null) {
            phoneEditText.setHint(R.string.hint_phone_number);
        } else {
            phoneEditText.setText(userDataMap.get("phoneNumber").toString());
        }

        final EditText emailEditText = new EditText(context);
        emailEditText.setHint(R.string.email_hint);
        if (userDataMap.get("email") == null) {
            emailEditText.setHint(R.string.email_hint);
        } else {

            emailEditText.setText(userDataMap.get("email").toString());
        }

        final EditText locationEditText = new EditText(context);
        locationEditText.setHint(R.string.location_hint);
        if (userDataMap.get("location") != null) {
            locationEditText.setText(userDataMap.get("location").toString());
        }

        layout.addView(displayNameEditText);
        if (userProvider.equals("google.com") || userProvider.equals("password") || userProvider.equals("facebook.com")) {
            layout.addView(phoneEditText);
        }
        if (userProvider.equals("phone")) {
            layout.addView(emailEditText);
        }
        layout.addView(locationEditText);

        alert.setIcon(R.drawable.dialog_icon);
        alert.setTitle("My Symphony");
        alert.setMessage("Update your information");
        alert.setView(layout);

        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //Editable YouEditTextValue = edittext.getText();
                //OR
                //String YouEditTextValue = edittext.getText().toString();
                if (displayNameEditText.getText() == null || displayNameEditText.getText().length() <= 0) {
                    showSnack(coordinate_profile, "Name can not be empty");
                    return;
                }


                String editedName = (displayNameEditText.getText().toString().length() > 0) ? displayNameEditText.getText().toString() : userDataMap.get("name").toString();
                String editedPhone = (phoneEditText.getText().toString().length() > 0) ? phoneEditText.getText().toString() : userDataMap.get("phoneNumber").toString();
                String editedEmail = emailEditText.getText().toString();
                String editedLocation = (locationEditText.getText().toString().length() > 0) ? locationEditText.getText().toString() : userDataMap.get("location").toString();

                try {
                    dbUserRef.child(user.getUid()).child("name").setValue(editedName);
                    dbUserRef.child(user.getUid()).child("phoneNumber").setValue(editedPhone);
                    if (!editedEmail.isEmpty() && !validEmail(editedEmail)) {
                        showSnack(coordinate_profile, "Please insert a correct Email");
                        return;
                    } else {
                        dbUserRef.child(user.getUid()).child("email").setValue(editedEmail);
                    }
                    dbUserRef.child(user.getUid()).child("location").setValue(editedLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public void changeProfilePic(View view){
        ImagePicker.create(this)
                .returnAfterFirst(true) // set whether pick or camera action should return immediate result or not. For pick image only work on single mode
                .folderMode(true) // folder mode (false by default)
                .folderTitle("Folder") // folder selection title
                .imageTitle("Tap to select") // image selection title
                .single() // single mode
                .limit(1) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Pictures") // directory name for captured image  ("Camera" folder by default)
                .start(REQUEST_CODE_PICKER); // start image picker activity with request code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_PICKER && resultCode== RESULT_OK && data!= null){
            ArrayList<Image> images = (ArrayList<Image>) ImagePicker.getImages(data);
           // ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            String name = images.get(0).getName();
            String path = images.get(0).getPath();

            File renmedFile = renameFile(path);
            Uri file = Uri.fromFile(renmedFile);

            //profilepicReference = storageReference.child("profilepic" + file.getLastPathSegment());
            profilepicReference = storageReference.child("profilepic/" + file.getLastPathSegment());

           // showProgressDialog("Uploading...." , ProfileActivity.this);
            UploadTask uploadTask = profilepicReference.putFile(file);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                   // hideProgressDialog();
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    showProgressDialog("Uploading...." +(int)progress + " %" , ProfileActivity.this);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    try{
                        //dbUserRef.child(user.getUid()).child("photoURL").setValue(downloadUrl.toString());
                        String temp = " ";
                        updateUI();
                        //hideProgressDialog();
                        showSnack(coordinate_profile,"Profile picture changed");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    // hideProgressDialog();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    showSnack(coordinate_profile,"Fail to change the profile picture");
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private File renameFile(String path){
        File dir = Environment.getExternalStorageDirectory();
        File from = null;
        File to = null;
        File temp = null;
        if(dir.exists()){
            from = new File(path);
            //to = new File(dir, path);
            temp = from;

            String splited [] = path.split("/") ;
            String lastVal = splited[splited.length-1];
            String splitedExt  = lastVal.substring(lastVal.length()-4);
            //String extension = splitedExt[1];
            String dirName = path.substring(0,path.length()-lastVal.length());
            to = new File(dirName+user.getUid()+((int)(Math.random()*9000)+1000)+splitedExt);

            if(temp.exists())
                temp.renameTo(to);
        }
        //String rename = to.getName();
        //String temp = " ";
        return to;
    }

    public void getExistingImei(){
        ArrayList<String> existingImeiList = new ArrayList<>();
        ArrayList<String> existingModelList = new ArrayList<>();
        ModelInfo modelInfo = new ModelInfo();

        for(int i=0; i<appUser.getImei().size(); i++){
            existingImeiList.add(appUser.imei.get(i).toString());
        }
        for(int i=0; i<appUser.getModel().size(); i++){
            existingModelList.add(appUser.model.get(i).toString());
        }
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String modelName = "";
        if(modelInfo.getSystemProperty("ro.product.device").length()>0){
            modelName = modelInfo.getSystemProperty("ro.product.manufacturer")+ " "+modelInfo.getSystemProperty("ro.product.device");
        }
        else{
            modelName = modelInfo.getSystemProperty("ro.product.manufacturer")+ " "+modelInfo.getSystemProperty("ro.build.product");
        }
        if(!existingImeiList.contains(modelInfo.getDeviceImei(mTelephonyManager))){
            existingImeiList.add(modelInfo.getDeviceImei(mTelephonyManager));
            dbUserRef.child(user.getUid()).child("imei").setValue(existingImeiList);
            existingModelList.add(modelName);
            dbUserRef.child(user.getUid()).child("model").setValue(existingModelList);
        }
       /* if(!userDataMap.get("providerId").toString().equals("phone")){
            if(modelInfo.isSimSupport(mTelephonyManager,getApplicationContext())) {
                String m = modelInfo.getPhoneNumber(getApplicationContext());
                dbUserRef.child(user.getUid()).child("phoneNumber").setValue(m);
            }
        }*/
    }


}
