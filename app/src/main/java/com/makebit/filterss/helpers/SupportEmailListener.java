package com.makebit.filterss.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.makebit.filterss.BuildConfig;
import com.makebit.filterss.R;

public class SupportEmailListener implements View.OnClickListener {

    Context context;

    public SupportEmailListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        String os = android.os.Build.VERSION.RELEASE;
        String sdk = android.os.Build.VERSION.SDK;
        String model = android.os.Build.MODEL;

        String mailto = "mailto:filterssapp@gmail.com" +
                "?subject=" + Uri.encode(context.getString(R.string.support)) +
                "&body=" + Uri.encode(
                context.getString(R.string.describe_problem) + '\n' + '\n' +
                        "-----------------------------" + '\n' +
                        "app: " + versionCode + " : " + versionName + '\n' +
                        "os: " + os + '\n' +
                        "sdk: " + sdk + '\n' +
                        "model: " + model + '\n'
        );

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.send_email)));
    }
}