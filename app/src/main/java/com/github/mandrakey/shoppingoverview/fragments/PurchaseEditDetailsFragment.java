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

package com.github.mandrakey.shoppingoverview.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.github.mandrakey.shoppingoverview.R;
import com.github.mandrakey.shoppingoverview.adapters.CategorySpinnerAdapter;
import com.github.mandrakey.shoppingoverview.adapters.SourceSpinnerAdapter;
import com.github.mandrakey.shoppingoverview.model.Purchase;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class PurchaseEditDetailsFragment extends Fragment {

    private View view;

    private Purchase purchase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_purchase_edit_details, container, false);

        final Spinner spCategory = (Spinner)view.findViewById(R.id.spCategory);
        final CategorySpinnerAdapter csa = new CategorySpinnerAdapter(getContext(), false);
        spCategory.setAdapter(csa);
        spCategory.setSelection(csa.getPosition(purchase.getCategory()));
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                purchase.setCategoryId(csa.getItem(i).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spCategory.setSelection(csa.getPosition(purchase.getCategory()));
            }
        });

        final Spinner spSource = (Spinner)view.findViewById(R.id.spSource);
        final SourceSpinnerAdapter ssa = new SourceSpinnerAdapter(getContext());
        spSource.setAdapter(ssa);
        spSource.setSelection(ssa.getPosition(purchase.getSource()));
        spSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                purchase.setSourceId(ssa.getItem(i).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spSource.setSelection(ssa.getPosition(purchase.getSource()));
            }
        });

        Calendar cal = new GregorianCalendar();
        cal.setTime(purchase.datetime);

        DatePicker dpDate = (DatePicker)view.findViewById(R.id.dpDate);
        dpDate.init(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
                        Calendar cal = new GregorianCalendar();
                        cal.setTime(purchase.datetime);
                        cal.set(Calendar.YEAR, y);
                        cal.set(Calendar.MONTH, m);
                        cal.set(Calendar.DAY_OF_MONTH, d);
                        purchase.datetime = cal.getTime();
                    }
                }
        );

        TimePicker tpTime = (TimePicker)view.findViewById(R.id.tpTime);
        if (Build.VERSION.SDK_INT < 23) {
            tpTime.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            tpTime.setCurrentMinute(cal.get(Calendar.MINUTE));
        } else {
            tpTime.setHour(cal.get(Calendar.HOUR_OF_DAY));
            tpTime.setMinute(cal.get(Calendar.MINUTE));
        }
        tpTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int h, int m) {
                Calendar cal = new GregorianCalendar();
                cal.setTime(purchase.datetime);
                cal.set(Calendar.HOUR, h);
                cal.set(Calendar.MINUTE, m);
                purchase.datetime = cal.getTime();
            }
        });

        return view;
    }

    public void setPurchase(Purchase p) {
        purchase = p;
    }
}
