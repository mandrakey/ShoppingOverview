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
import com.github.mandrakey.shoppingoverview.model.Category;
import com.github.mandrakey.shoppingoverview.model.Purchase;
import com.github.mandrakey.shoppingoverview.viewholders.PurchaseListItemViewholder;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class PurchaseListAdapter extends ArrayAdapter<Purchase> {

    private Category category;
    private int month;
    private int year;

    public PurchaseListAdapter(Context context, Category initialCategory) {
        super(context, R.layout.listitem_purchases);

        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        refresh();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PurchaseListItemViewholder viewholder;
        Purchase p = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_purchases, null);
            viewholder = new PurchaseListItemViewholder();

            viewholder.icon = (ImageView)convertView.findViewById(android.R.id.icon);
            viewholder.tvSourceName = (TextView)convertView.findViewById(R.id.tvSourceName);
            viewholder.tvSum = (TextView)convertView.findViewById(R.id.tvSum);
            viewholder.tvDateTime = (TextView)convertView.findViewById(R.id.tvDateTime);
            viewholder.tvCategory = (TextView)convertView.findViewById(R.id.tvCategory);
            convertView.setTag(viewholder);
        } else {
            viewholder = (PurchaseListItemViewholder)convertView.getTag();
        }

        if (p != null) {
            viewholder.icon.setImageBitmap(p.getSource().image);
            viewholder.tvSourceName.setText(p.getSource().name);
            viewholder.tvSum.setText(getContext().getResources().getString(R.string.price_format, p.sum));
            viewholder.tvDateTime.setText(p.datetime.toString());
            viewholder.tvCategory.setText(p.getCategory().name);
        }

        return convertView;
    }

    public void refresh() {
        clear();
        Database db = new Database(getContext());

        Map<String, String> params = new HashMap<>();
        params.put("month", Integer.toString(month));
        params.put("year", Integer.toString(year));
        if (category != null) {
            params.put("category_id", Integer.toString(category.getId()));
        }

        Map<String, String> order = new HashMap<>();
        order.put("datetime", "DESC");

        addAll(db.searchPurchases(params, order));
        db.close();
    }

    public void setCategory(Category cat) {
        category = cat;
    }

    public Category getCategory() {
        return category;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
