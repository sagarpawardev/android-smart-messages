package dev.sagar.smsblocker.ux.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.ux.utils.ThemeUtil;

/**
 * Created by sagarpawar on 06/05/18.
 */

public class TranslateDialog {
    private Context context;
    public TranslateDialog(Context context){
        this.context = context;
    }



    public void show(String msg){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_chat__translate, null);
        TextView tvBody = view.findViewById(R.id.tv_body);
        tvBody.setText(msg);
        ThemeUtil theme = new ThemeUtil(this.context);
        Typeface typeface = theme.getTypeface();
        tvBody.setTypeface(typeface);

        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        String txtOk = context.getString(R.string.txt_ok);
        ad.setView(view);

        ad.setPositiveButton(txtOk,
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                }
        );

        ad.show();

    }
}
