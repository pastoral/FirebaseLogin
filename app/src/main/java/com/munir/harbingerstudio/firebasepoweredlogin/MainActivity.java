package com.munir.harbingerstudio.firebasepoweredlogin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import static com.munir.harbingerstudio.firebasepoweredlogin.Constants.permisionList;
import static com.munir.harbingerstudio.firebasepoweredlogin.Constants.permsRequestCode;

public class MainActivity extends BaseActivity  {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private static final int RC_SIGN_IN = 123;
    private RelativeLayout relativeLayout;
    private AuthUI.IdpConfig googleIdp;
    private AuthUI.IdpConfig facebookIdp;
    ArrayList<Object> userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // MainActivity.super.requestAppPermissions(permisionList, R.string.runtime_permissions_txt, permsRequestCode);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        firebaseAuth = FirebaseAuth.getInstance();
        relativeLayout = (RelativeLayout)findViewById(R.id.activity_main_rel);
        // listLoginProvider = (ListView)findViewById(R.id.login_list_view);

        //listLoginProvider.setAdapter(adapter);
        //listLoginProvider.setOnItemClickListener(this);
        googleIdp = new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                .setPermissions(Arrays.asList(Scopes.PROFILE)).build();
        facebookIdp = new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                .setPermissions(Arrays.asList("user_friends")).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        userData = new ArrayList<>();
        if(firebaseUser != null){
            //Signed in, launch the Sign In Activity
            // Name, email address, and profile photo Url
            userData.add(firebaseUser.getDisplayName());
            userData.add(firebaseUser.getEmail());
            userData.add(firebaseUser.getPhotoUrl());
            //pl = (Parcelable)userData;
            Intent intent = new Intent(this, ProfileActivity.class);
            //intent.putParcelableArrayListExtra("userdata", pl);
            // startActivity(new Intent(this, ProfileActivity.class));
            startActivity(intent);
            finish();
            return;
        }
        else{
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(
                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                    googleIdp,
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                            ))
                    .build(),RC_SIGN_IN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if(resultCode== ResultCodes.OK){
                IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
                startActivity(new Intent(this,ProfileActivity.class)
                        .putExtra("my_token", idpResponse.getIdpToken()));
                finish();
                return;
            }
            else{
                // Sign in failed
                if(response==null){
                    // AppUser pressed back button
                    Snackbar.make(relativeLayout,"Signin Cancelled", Snackbar.LENGTH_SHORT);
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(relativeLayout,"No Internet Connection", Snackbar.LENGTH_SHORT);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Snackbar.make(relativeLayout,"Unknown Error", Snackbar.LENGTH_SHORT);
                    return;
                }
            }
        }
    }



   /* public static class MyArrayAdapter extends ArrayAdapter<Class> {
        private Context mContext;
        private Class[] mClasses;
        private int[] mDescriptionIds;
        public MyArrayAdapter(Context context, int resource, Class[] objects) {
            super(context, resource, objects);
            mContext = context;
            mClasses = objects;
        }
        public void setmDescriptionIds(int[] mDescriptionIds) {
            this.mDescriptionIds = mDescriptionIds;
        }
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_expandable_list_item_2,null);
            }
            ((TextView) view.findViewById(android.R.id.text1)).setText(mClasses[position].getSimpleName());
            ((TextView) view.findViewById(android.R.id.text2)).setText(mDescriptionIds[position]);
            return view;
        }
    } */
}