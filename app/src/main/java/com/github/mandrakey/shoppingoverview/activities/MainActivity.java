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

package com.github.mandrakey.shoppingoverview.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mandrakey.shoppingoverview.R;
import com.github.mandrakey.shoppingoverview.adapters.CategorySpinnerAdapter;
import com.github.mandrakey.shoppingoverview.adapters.PurchaseListAdapter;
import com.github.mandrakey.shoppingoverview.adapters.SourceSpinnerAdapter;
import com.github.mandrakey.shoppingoverview.database.Database;
import com.github.mandrakey.shoppingoverview.model.Category;
import com.github.mandrakey.shoppingoverview.model.Purchase;
import com.github.mandrakey.shoppingoverview.model.Source;
import com.github.mandrakey.shoppingoverview.widgets.PurchaseTextView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Spinner spDisplayCategory;
    private Button btnPreviousDate;
    private TextView tvCurrentMonth;
    private TextView tvCurrentYear;
    private Button btnNextDate;
    private ListView lvDisplayItems;
    private Spinner spAddCategory;
    private Spinner spSource;
    private PurchaseTextView tvAddPrice;
    private TextView tvTotalStats;
    private TextView tvPrevMonthStats;
    private TextView tvMonthStats;

    private Pair<Integer, View> selectedDisplayItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        spDisplayCategory = (Spinner)findViewById(R.id.spDisplayCategory);
        btnPreviousDate = (Button)findViewById(R.id.btnPreviousDate);
        tvCurrentMonth = (TextView)findViewById(R.id.tvCurrentMonth);
        tvCurrentYear = (TextView)findViewById(R.id.tvCurrentYear);
        btnNextDate = (Button)findViewById(R.id.btnNextDate);
        lvDisplayItems = (ListView)findViewById(R.id.lvDisplayItems);
        spAddCategory = (Spinner)findViewById(R.id.spAddCategory);
        spSource = (Spinner)findViewById(R.id.spSource);
        tvAddPrice = (PurchaseTextView)findViewById(R.id.tvAddPrice);
        tvTotalStats = (TextView)findViewById(R.id.tvTotalStats);
        tvPrevMonthStats = (TextView)findViewById(R.id.tvPrevMonthStats);
        tvMonthStats = (TextView)findViewById(R.id.tvMonthStats);

        spDisplayCategory.setAdapter(new CategorySpinnerAdapter(this, true));
        spAddCategory.setAdapter(new CategorySpinnerAdapter(this, false));
        spSource.setAdapter(new SourceSpinnerAdapter(this));

        PurchaseListAdapter pla = new PurchaseListAdapter(this,
                (Category)spDisplayCategory.getSelectedItem());
        lvDisplayItems.setAdapter(pla);

        tvAddPrice.setTextEx("");

        String[] months = getResources().getStringArray(R.array.months);
        tvCurrentMonth.setText(months[pla.getMonth()]);
        tvCurrentYear.setText(Integer.toString(pla.getYear()));

        //----------------------------------------------------------------------
        // Init listeners

        spDisplayCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeDisplayCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                changeDisplayCategory();
            }
        });

        /* lvDisplayItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectedDisplayItem != null) {
                    selectedDisplayItem.second.setBackgroundColor(Color.TRANSPARENT);
                }

                Purchase p = (Purchase)lvDisplayItems.getAdapter().getItem(i);
                selectedDisplayItem = new Pair<>(i, view);
                view.setBackgroundColor(Color.LTGRAY);

                tvAddPrice.setTextEx(String.format("%.2f", p.sum));

                CategorySpinnerAdapter csa = (CategorySpinnerAdapter)spAddCategory
                        .getAdapter();
                spAddCategory.setSelection(csa.getPosition(p.getCategory()));

                SourceSpinnerAdapter ssa = (SourceSpinnerAdapter)spSource
                        .getAdapter();
                spSource.setSelection(ssa.getPosition(p.getSource()));

                return false;
            }
        }); */

        btnPreviousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PurchaseListAdapter pla = (PurchaseListAdapter)lvDisplayItems.getAdapter();
                int m = pla.getMonth() - 1;
                if (m < 0) {
                    m = 11;
                    pla.setYear(pla.getYear() - 1);
                    tvCurrentYear.setText(Integer.toString(pla.getYear()));
                }

                pla.setMonth(m);
                pla.refresh();
                refreshStats();

                String[] months = getResources().getStringArray(R.array.months);
                tvCurrentMonth.setText(months[m]);
            }
        });

        btnNextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PurchaseListAdapter pla = (PurchaseListAdapter)lvDisplayItems.getAdapter();
                int m = pla.getMonth() + 1;
                if (m > 11) {
                    m = 0;
                    pla.setYear(pla.getYear() + 1);
                    tvCurrentYear.setText(Integer.toString(pla.getYear()));
                }

                pla.setMonth(m);
                pla.refresh();
                refreshStats();

                String[] months = getResources().getStringArray(R.array.months);
                tvCurrentMonth.setText(months[m]);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ((SourceSpinnerAdapter)spSource.getAdapter()).refresh();
        ((CategorySpinnerAdapter)spAddCategory.getAdapter()).refresh();
        ((CategorySpinnerAdapter)spDisplayCategory.getAdapter()).refresh();
        ((PurchaseListAdapter)lvDisplayItems.getAdapter()).refresh();
        refreshStats();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.mnuAbout:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            default:
                return false;
        }

        return true;
    }

    public void calculatorButtonPressed(View source) {
        String tag = (String)source.getTag();

        if ("C".equals(tag)) {
            tvAddPrice.empty();
        } else if ("OK".equals(tag)) {
            if (tvAddPrice.getText().toString().isEmpty()) {
                return;
            }

            double sum = Double.parseDouble(tvAddPrice.getText().toString());
            Category cat = (Category)spAddCategory.getSelectedItem();
            Source s = (Source)spSource.getSelectedItem();
            Database db = new Database(this);

            if (selectedDisplayItem == null) {
                db.addPurchase(cat.getId(), s.getId(), sum);
            } else {
                Purchase p = (Purchase)lvDisplayItems.getAdapter()
                        .getItem(selectedDisplayItem.first);
                db.updatePurchase(p, cat.getId(), s.getId(), sum);
                selectedDisplayItem.second.setBackgroundColor(Color.TRANSPARENT);
                selectedDisplayItem = null;
            }

            CategorySpinnerAdapter csa = (CategorySpinnerAdapter)spDisplayCategory.getAdapter();
            spDisplayCategory.setSelection(csa.getPosition(cat));

            db.close();
            ((PurchaseListAdapter)lvDisplayItems.getAdapter()).refresh();
            refreshStats();
            tvAddPrice.empty();
        } else if ("delete".equals(tag)) {
            try {
                tvAddPrice.remove();
            } catch (Exception ex) {
                Toast.makeText(this,
                        getResources().getString(R.string.digit_could_not_be_removed, ex.getMessage()),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                tvAddPrice.add(tag.charAt(0));
            } catch (Exception ex) {
                Toast.makeText(this,
                        getResources().getString(R.string.digit_could_not_be_added, ex.getMessage()),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void changeDisplayCategory() {
        PurchaseListAdapter pla = (PurchaseListAdapter)lvDisplayItems.getAdapter();
        Category cat = (Category)spDisplayCategory.getSelectedItem();

        pla.setCategory(cat.getId() == -1 ? null : cat);
        pla.refresh();
        refreshStats();

        if (selectedDisplayItem != null) {
            selectedDisplayItem.second.setBackgroundColor(Color.TRANSPARENT);
            selectedDisplayItem = null;
            tvAddPrice.empty();
        }
    }

    private void refreshStats() {
        Database db = new Database(this);
        PurchaseListAdapter pla = (PurchaseListAdapter)lvDisplayItems.getAdapter();

        Category currentCategory = pla.getCategory();
        Integer categoryId = currentCategory != null
                ? currentCategory.getId()
                : null;

        Map<String, Double> allTime = db.getPurchaseDataTotal(categoryId);
        Map<String, Double> current = db.getPurchaseDataForMonth(pla.getMonth(),
                pla.getYear(), categoryId);

        int lastmonth = pla.getMonth() - 1;
        int lastyear = pla.getYear();
        if (lastmonth < 0) {
            lastmonth = 11;
            --lastyear;
        }
        Map<String, Double> previous = db.getPurchaseDataForMonth(lastmonth,
                lastyear, categoryId);

        tvTotalStats.setText(getString(R.string.stats_format,
                allTime.get("sum"), allTime.get("avg")));
        tvPrevMonthStats.setText(getString(R.string.stats_format,
                previous.get("sum"), previous.get("avg")));
        tvMonthStats.setText(getString(R.string.stats_format,
                current.get("sum"), current.get("avg")));
    }
}
