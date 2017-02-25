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

package com.github.mandrakey.shoppingoverview.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Pair;

import com.github.mandrakey.shoppingoverview.model.Category;
import com.github.mandrakey.shoppingoverview.model.Purchase;
import com.github.mandrakey.shoppingoverview.model.Source;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Database extends SQLiteOpenHelper implements Closeable {

    public static final int DB_VERSION = 1;

    private static final String DB_NAME = "so.db3";
    private static final String TAG = "Database";

    // TABLES
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_SOURCES = "sources";
    public static final String TABLE_PURCHASES = "purchases";

    private Context context;

    public Database(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
        context = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "CONSTRAINT uq_categories_name UNIQUE(name) " +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_SOURCES + " (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "image BLOB DEFAULT (null), " +
                "CONSTRAINT uq_sources_name UNIQUE(name) " +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_PURCHASES + " (" +
                "category_id INTEGER NOT NULL, " +
                "source_id INTEGER NOT NULL, " +
                "datetime INTEGER NOT NULL, " +
                "month INTEGER NOT NULL, " +
                "year INTEGER NOT NULL, " +
                "sum REAL NOT NULL, " +
                "CONSTRAINT pk_purchases PRIMARY KEY (category_id, source_id, datetime), " +
                "CONSTRAINT fk_purchases_categories FOREIGN KEY (category_id) REFERENCES " + TABLE_CATEGORIES + "(id)" +
                "CONSTRAINT fk_purchases_categories FOREIGN KEY (source_id) REFERENCES " + TABLE_SOURCES + "(id) " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // pass for now
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    //==========================================================================

    public List<Category> getCategories() {
        Cursor c = getReadableDatabase().query(TABLE_CATEGORIES,
                new String[]{"*"}, null, null, null, null, null);

        List<Category> res = new ArrayList<>();
        while (c.moveToNext()) {
            res.add(Category.fromCursor(c));
        }

        c.close();
        return res;
    }

    public Category getCategory(int id) {
        Cursor c = getReadableDatabase().query(TABLE_CATEGORIES,
                new String[]{"*"}, "id=?", new String[]{Integer.toString(id)},
                null, null, null);

        if (!c.moveToNext()) {
            return null;
        }

        Category res = Category.fromCursor(c);
        c.close();
        return res;
    }

    public void insertCategory(Category c) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", c.name);

        db.insert(TABLE_CATEGORIES, null, cv);
        db.close();
    }

    public void updateCategory(Category c) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", c.name);

        db.update(TABLE_CATEGORIES, cv, "id=?", new String[]{Integer.toString(c.getId())});
        db.close();
    }

    public void deleteCategory(Category c) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CATEGORIES, "id=?", new String[]{Integer.toString(c.getId())});
        db.close();
    }

    public List<Source> getSources() {
        Cursor c = getReadableDatabase().query(TABLE_SOURCES,
                new String[]{"*"}, null, null, null, null, null);

        List<Source> res = new ArrayList<>();
        while (c.moveToNext()) {
            res.add(Source.fromCursor(c));
        }

        c.close();
        return res;
    }

    public Source getSource(int id) {
        Cursor c = getReadableDatabase().query(TABLE_SOURCES,
                new String[]{"*"}, "id=?", new String[]{Integer.toString(id)},
                null, null, null);

        if (!c.moveToNext()) {
            return null;
        }

        Source res = Source.fromCursor(c);
        c.close();
        return res;
    }

    public void insertSource(Source s) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", s.name);

        if (s.image != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            s.image.compress(Bitmap.CompressFormat.PNG, 100, bos);
            cv.put("image", bos.toByteArray());
        } else {
            cv.putNull("image");
        }

        db.insert(TABLE_SOURCES, null, cv);
        db.close();
    }

    public void updateSource(Source s) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", s.name);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        s.image.compress(Bitmap.CompressFormat.PNG, 100, bos);
        cv.put("image", bos.toByteArray());

        db.update(TABLE_SOURCES, cv,
                "id=?", new String[]{Integer.toString(s.getId())});
        db.close();
    }

    public void deleteSource(Source s) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SOURCES, "id=?", new String[]{Integer.toString(s.getId())});
        db.close();
    }

    public List<Purchase> getPurchases() {
        return getPurchases("datetime DESC");
    }

    public List<Purchase> getPurchases(String orderBy) {
        Cursor c = getReadableDatabase().query(TABLE_PURCHASES,
                new String[]{"*"}, null, null,
                null, null, orderBy);

        List<Purchase> res = new ArrayList<>();
        while (c.moveToNext()) {
            res.add(Purchase.fromCursor(context, c));
        }

        c.close();
        return res;
    }

    public List<Purchase> getPurchasesForCategory(Category cat) {
        return getPurchasesForCategory(cat, "datetime DESC");
    }

    public List<Purchase> getPurchasesForCategory(Category cat, String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PURCHASES,
                new String[]{"*"},
                "category_id=?", new String[]{Integer.toString(cat.getId())},
                null, null, orderBy);

        List<Purchase> res = new ArrayList<>();
        while (c.moveToNext()) {
            res.add(Purchase.fromCursor(context, c));
        }

        c.close();
        return res;
    }

    public List<Purchase> searchPurchases(Map<String, String> params,
                                          Map<String, String> order) {
        StringBuilder where = new StringBuilder();
        for (String k : params.keySet()) {
            where.append(k).append(" = ? AND ");
        }
        String whereString = where.toString();

        StringBuilder orderBy = new StringBuilder();
        for (String k : order.keySet()) {
            String value = order.get(k);
            if (!Arrays.asList("ASC", "DESC").contains(value)) {
                throw new IllegalArgumentException("Illegal sort order '" +
                    value + "'");
            }
            orderBy.append(k).append(" ").append(value).append(" ");
        }
        String orderByString = orderBy.toString();

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_PURCHASES, new String[]{"*"},
                whereString.substring(0, whereString.length() - 5),
                params.values().toArray(new String[0]),
                null, null,
                orderByString.substring(0, orderByString.length() - 1));

        List<Purchase> res = new ArrayList<>();
        while (c.moveToNext()) {
            res.add(Purchase.fromCursor(context, c));
        }

        c.close();
        return res;
    }

    public void addPurchase(int categoryId, int sourceId, double sum) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        ContentValues cv = new ContentValues();
        cv.put("category_id", categoryId);
        cv.put("source_id", sourceId);
        cv.put("datetime", cal.getTimeInMillis());
        cv.put("month", cal.get(Calendar.MONTH));
        cv.put("year", cal.get(Calendar.YEAR));
        cv.put("sum", sum);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PURCHASES, null, cv);
        db.close();
    }

    public void updatePurchase(Purchase p, int categoryId, int sourceId,
                               double sum) {

        ContentValues cv = new ContentValues();
        cv.put("category_id", categoryId);
        cv.put("source_id", sourceId);
        cv.put("sum", sum);

        SQLiteDatabase db = getWritableDatabase();

        String[] where = new String[]{
                Integer.toString(p.getCategory().getId()),
                Integer.toString(p.getSource().getId()),
                Long.toString(p.datetime.getTime())
        };
        db.update(TABLE_PURCHASES, cv,
                "category_id=? AND source_id=? AND datetime=?", where);
        db.close();
    }

    public void deletePurchase(Purchase p) {
        SQLiteDatabase db = getWritableDatabase();

        String[] where = new String[]{
                Integer.toString(p.getCategory().getId()),
                Integer.toString(p.getSource().getId()),
                Long.toString(p.datetime.getTime())
        };
        db.delete(TABLE_PURCHASES,
                "category_id=? AND source_id=? AND datetime=?", where);
        db.close();
    }

    public Map<String, Double> getPurchaseDataTotal() {
        return getPurchaseDataTotal(null);
    }

    public Map<String, Double> getPurchaseDataTotal(Integer categoryId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PURCHASES,
                new String[]{"sum(sum), avg(sum)"},
                ((categoryId != null) ? "category_id=?" : null),
                ((categoryId != null) ? new String[]{Integer.toString(categoryId)} : null),
                null, null, null);

        double sum = -1, avg = -1;
        if (c.moveToFirst()) {
            sum = c.getDouble(0);
            avg = c.getDouble(1);
        }

        Map<String, Double> res = new HashMap<>(2);
        res.put("sum", sum);
        res.put("avg", avg);

        c.close();
        db.close();
        return res;
    }

    public Map<String, Double> getPurchaseDataForMonth(int month, int year) {
        return getPurchaseDataForMonth(month, year, null);
    }

    public Map<String, Double> getPurchaseDataForMonth(int month, int year,
                                                       Integer categoryId) {
        SQLiteDatabase db = getReadableDatabase();

        String where = "month = ? AND year = ?" +
                ((categoryId != null) ? " AND category_id = ?" : "");

        List<String> whereArgs = new ArrayList<>(Arrays.asList(Integer.toString(month),
                Integer.toString(year)));
        if (categoryId != null) {
            whereArgs.add(Integer.toString(categoryId));
        }

        Cursor c = db.query(TABLE_PURCHASES,
                new String[]{"sum(sum), avg(sum)"},
                where, whereArgs.toArray(new String[0]),
                null, null, null);

        double sum = -1, avg = -1;
        if (c.moveToFirst()) {
            sum = c.getDouble(0);
            avg = c.getDouble(1);
        }

        Map<String, Double> res = new HashMap<>(2);
        res.put("sum", sum);
        res.put("avg", avg);

        c.close();
        db.close();
        return res;
    }
}
