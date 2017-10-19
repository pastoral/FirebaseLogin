package com.munir.harbingerstudio.firebasepoweredlogin;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
import com.onesignal.OneSignal;
//import com.nguyenhoanglam.imagepicker.model.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static com.munir.harbingerstudio.firebasepoweredlogin.Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.munir.harbingerstudio.firebasepoweredlogin.Constants.REQUEST_CHECK_SETTINGS;
import static com.munir.harbingerstudio.firebasepoweredlogin.Constants.UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.munir.harbingerstudio.firebasepoweredlogin.Constants.permisionList;
import static com.munir.harbingerstudio.firebasepoweredlogin.Constants.permsRequestCode;


public class ProfileActivity extends BaseActivity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, LocationListener {
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
    public AppUser appUser;


    private boolean mRequestingLocationUpdates;
    protected LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;
    public LocationSettingsRequest mLocationSettingsRequest;
    public PlaceAutocompleteFragment autocompleteFragment;
    //private ResponeReceiver receiver;
    private IntentFilter filter;

    private Location mLastLocation;




    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GoogleApiClient mGoogleApiClient;
    private String placeName,vicinity = null;
    private static boolean isActivityActive = false;
    public  ArrayList<String> existingImeiList = new ArrayList<>();
    public ArrayList<String> existingModelList = new ArrayList<>();
    public String userLocality = "";
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

        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityActive = true;
        mLastLocation = new Location("");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        //receiver = new ResponeReceiver();
        if(!mRequestingLocationUpdates){
            mRequestingLocationUpdates = true;
        }

         if(user == null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        checkLocationSettings();

        if(mRequestingLocationUpdates && mGoogleApiClient.isConnected()){
            startLocationUpdate();
        }

        if (user != null) {
            if(!isActivityActive) {
                showProgressDialog("Loading user data....", ProfileActivity.this);
            }
            dbUserRef.orderByKey().equalTo(user.getUid()).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //appUser = postSnapshot.getValue(AppUser.class);

                        userDataMap = (HashMap<String, Object>) postSnapshot.getValue();
                        appUser = postSnapshot.getValue(AppUser.class);
                        getExistingImei();
                        updateUI();


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityActive = false;
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
        String imeiList = "";
        String modelList ="";
        if (userDataMap.size() > 0) {
            hideProgressDialog();
            userName.setText(userDataMap.get("name").toString());
            userEmail.setText(userDataMap.get("email").toString());
            if (userDataMap.get("photoURL").toString() != null) {
                Glide.with(getApplicationContext()).load(userDataMap.get("photoURL")).fitCenter().into(userPhoto);
            }
            userLocation.setText(userDataMap.get("location").toString());
            phoneNumber.setText(userDataMap.get("phoneNumber").toString());
            userProvider = userDataMap.get("providerId").toString();
            if (userProvider.equals("password")) {
                button_change_password.setVisibility(View.VISIBLE);
            }

           updateOneSignal(appUser);
            //OneSignal.sendTag();
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

                    if(!ProfileActivity.this.isFinishing()) {
                        if(isActivityActive) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            showProgressDialog("Uploading...." + (int) progress + " %", ProfileActivity.this);
                        }
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    try{
                        //dbUserRef.child(user.getUid()).child("photoURL").setValue(downloadUrl.toString());
                        String temp = " ";
                        dbUserRef.child(user.getUid()).child("photoURL").setValue(downloadUrl);
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
        if(requestCode==REQUEST_CHECK_SETTINGS){
            if(resultCode == RESULT_OK){
                mRequestingLocationUpdates = true;
            }

            if(resultCode == RESULT_CANCELED){
                Log.i("ThreeFragment", "User chose not to make required location settings changes.");
            }
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

        String modelName = "";
        String imei = "";
        ModelInfo modelInfo = new ModelInfo();

        for(int i=0; i<appUser.getImei().size(); i++){
            existingImeiList.add(appUser.imei.get(i).toString());
        }
        for(int i=0; i<appUser.getModel().size(); i++){
            existingModelList.add(appUser.model.get(i).toString());
        }
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

        if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if(modelInfo.getSystemProperty("ro.product.device").length()>0){
                modelName = modelInfo.getSystemProperty("ro.product.manufacturer")+ " "+modelInfo.getSystemProperty("ro.product.device");
            }
            else{
                modelName = modelInfo.getSystemProperty("ro.product.manufacturer")+ " "+modelInfo.getSystemProperty("ro.build.product");
            }
        }
        else{
            return;
        }

        if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei = modelInfo.getDeviceImei(mTelephonyManager);
        }
        else{
            imei = "UnAccessable";
        }

        if(!existingImeiList.contains(modelInfo.getDeviceImei(mTelephonyManager))){
            existingImeiList.add(imei);
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

    private void startLocationUpdate(){
        Log.d("STARTLOC update", "startLocationUpdate fired");
        if(ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
       /* Intent intent = new Intent(this, BackgroundLocationService.class);
        intent.putExtra("requestId", 101);
        startService(intent);*/

    }

    private void stopLocationUpdate(){
        /*Intent intent = new Intent(this, BackgroundLocationService.class);
        intent.putExtra("requestId", 101);
        stopService(intent);*/

        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if(mRequestingLocationUpdates){
            startLocationUpdate();
        }
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d("Profile Activity: ", "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
        Log.d("Profile Activity: " , "Play services connection suspended");
    }


    private void getLocationData() {
        if (mLastLocation == null) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
            }
        }
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    public void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,builder.build());
        result.setResultCallback(this);

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch(status.getStatusCode()){
            case LocationSettingsStatusCodes.SUCCESS :
                Log.i("Profile Activity : ", "All location settings are satisfied.");
                startLocationUpdate();
                //stopLocationUpdate();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED :
                Log.i("Profile Activity : ", "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                try{
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                }
                catch(IntentSender.SendIntentException e){
                    Log.i("Profile Activity : ", "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE :
                Log.i("Profile Activity : ", "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    protected LocationRequest createLocationRequest() {
        Log.i("Profile Activity : ", "createLocationRequest()");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10);
        return mLocationRequest;
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public void onLocationChanged(Location location) {
        String address = "";
        mLastLocation = location;
        if(mLastLocation != null) {
            double lat = mLastLocation.getLatitude();
            double lan = mLastLocation.getLongitude();
            address = getAddress(lat,lan);
        }

        try{
            if(address.length()>3){
                dbUserRef.child(user.getUid()).child("location").setValue(address);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        // Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                //Address address = addresses.get(0);
                // result.append(address.getLocality()).append("\n");
                // result.append(address.getCountryName());
                result.append(addresses.get(0).getAddressLine(0)+"#");
                result.append(addresses.get(0).getLocality() + "#");
                result.append(addresses.get(0).getCountryName());
                userLocality = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    public void loadnews(View v){
        Intent i = new Intent(this, NewsListActivity.class);
        startActivity(i);
    }

    public void updateOneSignal(AppUser registeredUser){
        String modelName = "";
        String imei ="";
        String swVersion = "";
        ModelInfo modelInfo = new ModelInfo();
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei = modelInfo.getDeviceImei(mTelephonyManager);
        }
        else{
            imei = "UnAccessable";
        }
        if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if(modelInfo.getSystemProperty("ro.product.device").length()>0){
                modelName = modelInfo.getSystemProperty("ro.product.device");
            }
            else{
                modelName = modelInfo.getSystemProperty("ro.build.product");
            }
        }
        else{
            return;
        }
        swVersion = modelInfo.getSystemProperty("ro.build.display.id");
        OneSignal.sendTag("Imei",imei);
        OneSignal.sendTag("Model",modelName);
        OneSignal.sendTag("UserType" , registeredUser.getUserCategoryText());
        OneSignal.sendTag("Region" , userLocality);
        OneSignal.sendTag("SW_Version" , swVersion);

    }

}