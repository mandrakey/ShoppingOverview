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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.mandrakey.shoppingoverview.fragments.PurchaseEditDetailsFragment;
import com.github.mandrakey.shoppingoverview.fragments.PurchaseEditSumFragment;
import com.github.mandrakey.shoppingoverview.model.Purchase;

public class EditPurchaseTabsAdapter extends FragmentPagerAdapter {

    private PurchaseEditSumFragment sumFragment;
    private PurchaseEditDetailsFragment detailsFragment;

    private Purchase purchase;

    public EditPurchaseTabsAdapter(FragmentManager fm, Purchase p) {
        super(fm);
        purchase = p;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (sumFragment == null) {
                    sumFragment = new PurchaseEditSumFragment();
                    sumFragment.setPurchase(purchase);

                }
                return sumFragment;
            case 1:
                if (detailsFragment == null) {
                    detailsFragment = new PurchaseEditDetailsFragment();
                    detailsFragment.setPurchase(purchase);
                }
                return detailsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Sum";
            case 1:
                return "Details";
            default:
                throw new IllegalArgumentException("No such tab");
        }
    }
}
