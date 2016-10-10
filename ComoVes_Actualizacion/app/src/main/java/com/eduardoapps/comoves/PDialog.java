package com.eduardoapps.comoves;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Eduardo on 23/09/2016.
 */
public class PDialog {

    ProgressDialog mProgressDialog;
    String msg;
    Context c;

    public PDialog(Context c, String msg){
        this.msg = msg;
        this.c = c;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(c);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
