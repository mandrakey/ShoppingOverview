package com.github.mandrakey.shoppingoverview.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mandrakey.shoppingoverview.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitle(R.string.about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvLicenseText = (TextView)findViewById(R.id.tvLicenseText);

        BufferedReader br = new BufferedReader(new InputStreamReader(getResources()
            .openRawResource(R.raw.license)));
        StringBuilder html = new StringBuilder();

        String line;
        try {
            while ((line = br.readLine()) != null) {
                html.append(line);
            }
        } catch (IOException ex) {
            Log.w("AboutActivity", "Failed to read license text: " + ex.getMessage());
        }

        if (Build.VERSION.SDK_INT < 24) {
            tvLicenseText.setText(Html.fromHtml(html.toString()));
        } else {
            tvLicenseText.setText(Html.fromHtml(html.toString(), Html.FROM_HTML_MODE_COMPACT));
        }
        tvLicenseText.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }
}
