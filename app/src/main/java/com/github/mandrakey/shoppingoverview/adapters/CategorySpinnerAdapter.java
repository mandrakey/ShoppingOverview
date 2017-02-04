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

package com.github.mandrakey.shoppingoverview.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.mandrakey.shoppingoverview.R;
import com.github.mandrakey.shoppingoverview.database.Database;
import com.github.mandrakey.shoppingoverview.model.Category;

import java.security.InvalidParameterException;

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    private boolean allowAll = false;

    public CategorySpinnerAdapter(Context c, boolean vAllowAll) {
        super(c, R.layout.category_spinner_dropdown_item);

        allowAll = vAllowAll;
        refresh();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category c = getItem(position);
        TextView text1;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_spinner_dropdown_item, null);
            text1 = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(text1);
        } else {
            text1 = (TextView)convertView.getTag();
        }

        text1.setText(c != null ? c.name : "");
        return convertView;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Category c = getItem(position);
        TextView text1;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_spinner_dropdown_item, null);
            text1 = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(text1);
        } else {
            text1 = (TextView)convertView.getTag();
        }

        text1.setText(c != null ? c.name : "");
        return convertView;
    }

    @Override
    public int getPosition(Category cat) {
        for (int i = 0; i < getCount(); ++ i) {
            Category c = getItem(i);
            if (c != null && c.getId() == cat.getId()) return i;
        }

        return -1;
    }

    public void refresh() {
        clear();

        if (allowAll) {
            Category allCat = new Category();
            allCat.name = "All";
            add(allCat);
        }

        Database db = new Database(getContext());
        addAll(db.getCategories());
        db.close();
    }
}
