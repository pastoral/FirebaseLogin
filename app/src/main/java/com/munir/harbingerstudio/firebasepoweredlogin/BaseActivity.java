package com.munir.harbingerstudio.firebasepoweredlogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by munirul.hoque on 8/8/2017.
 */

public class BaseActivity extends AppCompatActivity {
    public ProgressDialog mProgressDialog;

    public void showProgressDialog(String message, Context context){
        if(mProgressDialog==null){
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog(){
        if(mProgressDialog != null  && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    public void showSnack(ViewGroup viewGroup, String message){
        Snackbar snackbar = Snackbar
                .make(viewGroup, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
