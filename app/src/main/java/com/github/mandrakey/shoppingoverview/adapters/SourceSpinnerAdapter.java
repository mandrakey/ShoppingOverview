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
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mandrakey.shoppingoverview.R;
import com.github.mandrakey.shoppingoverview.database.Database;
import com.github.mandrakey.shoppingoverview.model.Source;
import com.github.mandrakey.shoppingoverview.viewholders.SourceSpinnerDropdownViewholder;

public class SourceSpinnerAdapter extends ArrayAdapter<Source> {

    public SourceSpinnerAdapter(Context c) {
        super(c, R.layout.source_spinner_dropdown_item);
        refresh();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Source s = getItem(position);
        SourceSpinnerDropdownViewholder viewholder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.source_spinner_dropdown_item, null);

            viewholder = new SourceSpinnerDropdownViewholder();
            viewholder.icon = (ImageView)convertView.findViewById(android.R.id.icon);
            viewholder.text1 = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewholder);
        } else {
            viewholder = (SourceSpinnerDropdownViewholder)convertView.getTag();
        }

        if (s != null) {
            viewholder.text1.setText(s.name);
            viewholder.icon.setImageBitmap(s.image);
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Source s = getItem(position);
        SourceSpinnerDropdownViewholder viewholder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.source_spinner_dropdown_item, null);

            viewholder = new SourceSpinnerDropdownViewholder();
            viewholder.icon = (ImageView)convertView.findViewById(android.R.id.icon);
            viewholder.text1 = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewholder);
        } else {
            viewholder = (SourceSpinnerDropdownViewholder)convertView.getTag();
        }

        if (s != null) {
            viewholder.text1.setText(s.name);
            viewholder.icon.setImageBitmap(s.image);
        }
        return convertView;
    }

    @Override
    public int getPosition(Source item) {
        for (int i = 0; i < getCount(); ++i) {
            Source s = getItem(i);
            if (s != null && s.getId() == item.getId()) return i;
        }
        return -1;
    }

    public void refresh() {
        clear();
        Database db = new Database(getContext());
        addAll(db.getSources());
        db.close();
    }
}
