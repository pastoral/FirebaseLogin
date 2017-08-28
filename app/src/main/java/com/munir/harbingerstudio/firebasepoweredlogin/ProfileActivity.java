package com.munir.harbingerstudio.firebasepoweredlogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.munir.harbingerstudio.firebasepoweredlogin.model.AppUser;

import java.util.Iterator;
import java.util.regex.Pattern;


public class ProfileActivity extends BaseActivity {
    private String name, email, uid,editedEmail;
    private Uri photoUrl;
    private AppUser appUser;
    //private String userNames, userEmails, userPhotos
    private TextView userName, userEmail, changeEmail;
    private ImageView userPhoto;
    private StringBuilder providers;
    private ProfileAlertBuilder profileAlertBuilder;
    private LinearLayout row3;
    private CoordinatorLayout coordinate_profile;
    private Button btnChangePic;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        showProgressDialog();
        userName = (TextView)findViewById(R.id.userName);
        userEmail = (TextView)findViewById(R.id.userEmail);
        userPhoto = (ImageView) findViewById(R.id.userPhoto);
        changeEmail = (TextView) findViewById(R.id.strChangeEmail);
        btnChangePic = (Button)findViewById(R.id.btnChangePic);
        row3 = (LinearLayout)findViewById(R.id.row3);
        coordinate_profile = (CoordinatorLayout)findViewById(R.id.coordinate_profile);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user != null){
            loadUserData(user);
        }
        else{
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(user);

    }

    public void logout(View v){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                });
    }

    private AppUser loadUserData(FirebaseUser user){
        // The user's ID, unique to the Firebase project. Do NOT use this value to
        // authenticate with your backend server, if you have one. Use
        // FirebaseUser.getToken() instead.
         uid = user.getUid();
        // Name, email address, and profile photo Url
         name = user.getDisplayName();
         email = user.getEmail();
         photoUrl = user.getPhotoUrl();

        //Collecting Login provider's data
        providers = new StringBuilder(100);
        //providers.append("Providers used: ");
        if(user.getProviders()==null || user.getProviders().isEmpty()){
            providers.append("None");
        }
        else{
            Iterator<String> providerIter = user.getProviders().iterator();
            while(providerIter.hasNext()){
                String provider = providerIter.next();
                if (GoogleAuthProvider.PROVIDER_ID.equals(provider)) {
                    providers.append("Google");
                } else if (FacebookAuthProvider.PROVIDER_ID.equals(provider)) {
                    providers.append("Facebook");
                } else if (EmailAuthProvider.PROVIDER_ID.equals(provider)) {
                    providers.append("Email");
                }else if(PhoneAuthProvider.PROVIDER_ID.equals(provider)){
                    providers.append("Phone");
                } else {
                    providers.append(provider);
                }

                if (providerIter.hasNext()) {
                    providers.append(", ");
                }
            }
        }

        Toast.makeText(this,providers.toString(),Toast.LENGTH_LONG).show();
         appUser = new AppUser(uid,name,email,photoUrl);
         return appUser;
    }

    public void updateUI(FirebaseUser user){
        hideProgressDialog();
        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());
        //userPhoto.setText(String.valueOf(appUser.getPhotoUrl()));
        if(appUser.getPhotoUrl() != null){
            Glide.with(this).load(user.getPhotoUrl()).fitCenter().into(userPhoto);
        }
        if(providers.toString().contains("Phone")){
            changeEmail.setVisibility(View.VISIBLE);
        }
        if(providers.toString().contains("Email")){
            row3.setVisibility(View.VISIBLE);
        }

    }


    public void updateDisplayName(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
        final EditText edittext = new EditText(ProfileActivity.this);
        profileAlertBuilder = new ProfileAlertBuilder(ProfileActivity.this);
        alert = profileAlertBuilder.alertForEditText(user.getDisplayName().toString(),
                                                    "MySymphony Profile",
                                                    "Change your display name",
                                                    edittext);

        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //Editable YouEditTextValue = edittext.getText();
                //OR
                //String YouEditTextValue = edittext.getText().toString();
                if(edittext.getText()==null || edittext.getText().length()<= 0){
                    showSnack(coordinate_profile,"Name can not be empty");
                    return;
                }
                String editedName = edittext.getText().toString();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(editedName)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Changed User Name: ", "User profile updated.");
                                    recreate();
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

        alert.show();
    }


    public void updateDisplayEmail(View v){

        AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
        final EditText edittext = new EditText(ProfileActivity.this);
        edittext.setHint(getString(R.string.email_hint));
        profileAlertBuilder = new ProfileAlertBuilder(ProfileActivity.this);
        String currentEmail = user.getEmail();
        if(currentEmail == null || currentEmail.isEmpty()){
            currentEmail = "";
        }
        alert = profileAlertBuilder.alertForEditText(currentEmail,
                "MySymphony Profile",
                "Change your Email",
                edittext);



        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //Editable YouEditTextValue = edittext.getText();
                //OR
                //String YouEditTextValue = edittext.getText().toString();
                editedEmail = edittext.getText().toString();
                if(!validEmail(editedEmail)){
                    showSnack(coordinate_profile,"Please insert a correct Email");
                    return;
                }
                user.updateEmail(editedEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("Changed User Email: ", "User email address updated.");
                            recreate();
                        }
                        else{
                            Log.d("Changed User Email: ", "Unsuccessfull");
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
        if(!validEmail(editedEmail)){
            d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
            d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);


    }

    public void updatePassword(View v){

        AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
        final EditText edittext = new EditText(ProfileActivity.this);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        profileAlertBuilder = new ProfileAlertBuilder(ProfileActivity.this);
        //String currentPassword = ;

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
                if(edittext.getText()==null||edittext.getText().length()==0){
                    showSnack(coordinate_profile,"Password could not be empty");
                    return;
                }
                String editedPassword = edittext.getText().toString();
                user.updatePassword(editedPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("User_Password_Change:", "User password updated.");
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
        if(edittext.getText().toString()==null || edittext.getText().toString().isEmpty()){
            d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);


    }

    public void deleteAccount(View view){
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
        if(email==null||email.isEmpty()){
            return false;
        }
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }



}
