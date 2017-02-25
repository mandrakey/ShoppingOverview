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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.github.mandrakey.shoppingoverview.R;
import com.github.mandrakey.shoppingoverview.activities.MainActivity;
import com.github.mandrakey.shoppingoverview.adapters.EditPurchaseTabsAdapter;
import com.github.mandrakey.shoppingoverview.database.Database;
import com.github.mandrakey.shoppingoverview.model.Purchase;

public class EditPurchaseDialogue extends DialogFragment {

    private static final String TAG = "EditPurchaseDialog";

    private View view;

    private ViewPager viewPager;
    private PagerSlidingTabStrip pstsTabs;

    private Purchase purchase;
    private Purchase oldPurchase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialogue_purchase_edit, container, false);

        getDialog().setTitle("Edit purchase");

        viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new EditPurchaseTabsAdapter(getChildFragmentManager(), purchase));

        pstsTabs = (PagerSlidingTabStrip)view.findViewById(R.id.pstsTabs);
        pstsTabs.setViewPager(viewPager);

        view.findViewById(R.id.btnAbort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        view.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Old sum: " + oldPurchase.sum);
                Log.d(TAG, "New sum: " + purchase.sum);

                Database db = new Database(getContext());
                db.updatePurchase(
                        oldPurchase,
                        purchase.getCategoryId(),
                        purchase.getSourceId(),
                        purchase.sum,
                        purchase.datetime);
                db.close();

                LocalBroadcastManager.getInstance(getContext())
                        .sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_PURCHASES));

                dismiss();
            }
        });

        return view;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (purchase == null) {
            throw new IllegalStateException("Need a purchase to display dialogue.");
        }

        super.show(manager, tag);
    }

    public void setPurchase(Purchase p) {
        try {
            purchase = p.clone();
            oldPurchase = p;
        } catch (CloneNotSupportedException ex) {
            Log.e(TAG, "Failed to store old purchase values: " + ex.getMessage());
            dismiss();
        }
    }
}
