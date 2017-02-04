/*
Copyright 2017 Maurice Bleuel <mandrakey@bleuelmedia.com>

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.github.mandrakey.shoppingoverview.dialogues;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mandrakey.shoppingoverview.R;
import com.github.mandrakey.shoppingoverview.SourceSettingsFragment;
import com.github.mandrakey.shoppingoverview.activities.SettingsActivity;
import com.github.mandrakey.shoppingoverview.database.Database;
import com.github.mandrakey.shoppingoverview.model.Source;
import com.github.mandrakey.shoppingoverview.widgets.ActivityResultDelegate;

import java.io.IOException;
import java.io.InputStream;

public class SourceEditDialogue implements ActivityResultDelegate {

    public static final int REQUEST_IMAGE = 1;

    private SettingsActivity parent;
    private Source source;

    private View dialogueView;
    private EditText text1;
    private ImageView icon;

    public SourceEditDialogue(SettingsActivity p, Source s) {
        parent = p;
        source = s;
    }

    public void show() {
        dialogueView = LayoutInflater.from(parent)
                .inflate(R.layout.dialogue_source_edit, null);
        text1 = (EditText)dialogueView.findViewById(android.R.id.text1);
        icon = (ImageView)dialogueView.findViewById(android.R.id.icon);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                parent.startActivityForResult(i, REQUEST_IMAGE);
            }
        });

        if (source != null) {
            text1.setText(source.name);
            icon.setImageBitmap(source.image);
        } else {
            source = new Source();
        }

        parent.registerActivityResultDelegate(this);

        AlertDialog dlg = new AlertDialog.Builder(parent)
                .setTitle(source == null ? R.string.add_source : R.string.edit_source)
                .setView(dialogueView)
                .setNegativeButton("Abort", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if ("".equals(text1.getText())) {
                            Toast.makeText(parent, R.string.please_enter_a_source_name,
                                    Toast.LENGTH_LONG).show();
                            dialogInterface.dismiss();
                            return;
                        }
                        source.name = text1.getText().toString();

                        Database db = new Database(parent);
                        if (source.getId() == -1) {
                            db.insertSource(source);
                        } else {
                            db.updateSource(source);
                        }
                        db.close();

                        dialogInterface.dismiss();
                        LocalBroadcastManager.getInstance(parent)
                                .sendBroadcast(new Intent(SourceSettingsFragment.ACTION_REFRESH_SOURCES));
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        parent.unregisterActivityResultDelegate(SourceEditDialogue.this);
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_IMAGE) return;

        Uri uri = data.getData();
        try {
            InputStream is = parent.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            icon.setImageBitmap(bitmap);
            source.image = bitmap;
        } catch (IOException ex) {
            Log.e("SourceEditDialogue", "Failed to load image.");
        }
    }
}
