package com.korail.motrex.webapp.dialog;

import android.app.Dialog;
import android.content.Context;


public class CustomDialog extends Dialog {

    public static final int DIALOG_TYPE_EDIT = 1;
    public static final int DIALOG_TYPE_TWO_BUTTON = 2;
    public static final int DIALOG_TYPE_ONE_BUTTON = 3;

    protected int mDialogType;


    public CustomDialog(Context context) {
        super(context);
    }



    public int getDialogType(){
        return mDialogType;
    }


}
