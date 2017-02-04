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

package com.github.mandrakey.shoppingoverview.model;

import android.content.Context;
import android.database.Cursor;

import com.github.mandrakey.shoppingoverview.database.Database;

import java.util.Date;

public class Purchase {

    protected int categoryId;
    protected int sourceId;
    protected Category category;
    protected Source source;
    protected Context context;

    public Date datetime;
    public int month;
    public int year;
    public double sum;

    public static Purchase fromCursor(Context ctx, Cursor c) {
        Purchase p = new Purchase(ctx);

        p.categoryId = c.getInt(c.getColumnIndex("category_id"));
        p.sourceId = c.getInt(c.getColumnIndex("source_id"));
        p.datetime = new Date(c.getLong(c.getColumnIndex("datetime")));
        p.month = c.getInt(c.getColumnIndex("month"));
        p.year = c.getInt(c.getColumnIndex("year"));
        p.sum = c.getDouble(c.getColumnIndex("sum"));

        return p;
    }

    public Purchase(Context ctx) {
        context = ctx;
    }

    public Category getCategory()
    {
        if (category == null) {
            Database db = new Database(context);
            category = db.getCategory(categoryId);
            db.close();
        }
        return category;
    }

    public Source getSource() {
        if (source == null) {
            Database db = new Database(context);
            source = db.getSource(sourceId);
            db.close();
        }
        return source;
    }

}
