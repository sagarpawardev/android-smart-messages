package dev.sagar.smsblocker.ux.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Created by sagarpawar on 02/04/18.
 */

public class IntroDialog extends Dialog {

    Context context;
    public IntroDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public IntroDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    
}
