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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mandrakey.shoppingoverview.R;
import com.github.mandrakey.shoppingoverview.model.Purchase;
import com.github.mandrakey.shoppingoverview.widgets.PurchaseTextView;

public class PurchaseEditSumFragment extends Fragment {

    private View view;

    private PurchaseTextView tvSum;
    private KeypadFragment keypad;

    private Purchase purchase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_purchase_edit_sum, container, false);

        tvSum = (PurchaseTextView)view.findViewById(R.id.tvSum);
        tvSum.setTextEx(Double.toString(purchase.sum));
        keypad = (KeypadFragment)getChildFragmentManager().findFragmentById(R.id.keypad);

        view.findViewById(R.id.keypadOk).setVisibility(View.GONE);

        keypad.setKeypadListener(new KeypadFragment.KeypadListener() {
            @Override
            public void keypadClicked(int keypadId) {
                switch (keypadId) {
                    case R.id.keypadDelete:
                        tvSum.remove();
                        break;
                    case R.id.keypadClear:
                        tvSum.empty();
                        break;
                    case R.id.keypadOk:
                        break;
                    case R.id.keypadComma:
                        tvSum.add('.');
                        break;
                    default:
                        char c = ((Button)view.findViewById(keypadId)).getText().charAt(0);
                        tvSum.add(c);
                }

                purchase.sum = (tvSum.getText().length() > 0)
                        ? Double.parseDouble(tvSum.getText().toString())
                        : 0;
            }
        });

        return view;
    }

    public void setPurchase(Purchase p) {
        purchase = p;
    }
}
