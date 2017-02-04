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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mandrakey.shoppingoverview.CategorySettingsFragment;
import com.github.mandrakey.shoppingoverview.R;
import com.github.mandrakey.shoppingoverview.database.Database;
import com.github.mandrakey.shoppingoverview.model.Category;

public class CategoryEditDialogue {

    private Context context;
    private Category category;

    private View dialogueView;
    private EditText text1;

    public CategoryEditDialogue(Context c, Category cat) {
        context = c;
        category = cat;
    }

    public void show() {
        dialogueView = LayoutInflater.from(context)
                .inflate(R.layout.dialogue_category_edit, null);
        text1 = (EditText)dialogueView.findViewById(android.R.id.text1);

        if (category != null) {
            text1.setText(category.name);
        } else {
            category = new Category();
        }

        new AlertDialog.Builder(context)
                .setTitle(category.getId() == -1 ? R.string.add_category : R.string.edit_category)
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
                        if (text1.getText().toString().isEmpty()) {
                            Toast.makeText(context, R.string.please_enter_a_category,
                                    Toast.LENGTH_LONG).show();
                            dialogInterface.dismiss();
                            return;
                        }

                        category.name = text1.getText().toString();
                        Database db = new Database(context);
                        if (category.getId() == -1) {
                            db.insertCategory(category);
                        } else {
                            db.updateCategory(category);
                        }
                        db.close();

                        LocalBroadcastManager.getInstance(context)
                                .sendBroadcast(new Intent(CategorySettingsFragment.ACTION_REFRESH_CATEGORIES));
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

}
