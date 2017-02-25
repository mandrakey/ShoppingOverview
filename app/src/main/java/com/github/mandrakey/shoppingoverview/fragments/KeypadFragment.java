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
import android.widget.ImageButton;

import com.github.mandrakey.shoppingoverview.R;

public class KeypadFragment extends Fragment implements View.OnClickListener {

    public interface KeypadListener {
        void keypadClicked(int keypadId);
    }

    protected View view;

    protected Button keypad0;
    protected Button keypad1;
    protected Button keypad2;
    protected Button keypad3;
    protected Button keypad4;
    protected Button keypad5;
    protected Button keypad6;
    protected Button keypad7;
    protected Button keypad8;
    protected Button keypad9;
    protected Button keypadComma;
    protected ImageButton keypadDelete;
    protected Button keypadClear;
    protected Button keypadOk;

    protected KeypadListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_keypad, container, false);

        keypad0 = (Button)view.findViewById(R.id.keypad0);
        keypad1 = (Button)view.findViewById(R.id.keypad1);
        keypad2 = (Button)view.findViewById(R.id.keypad2);
        keypad3 = (Button)view.findViewById(R.id.keypad3);
        keypad4 = (Button)view.findViewById(R.id.keypad4);
        keypad5 = (Button)view.findViewById(R.id.keypad5);
        keypad6 = (Button)view.findViewById(R.id.keypad6);
        keypad7 = (Button)view.findViewById(R.id.keypad7);
        keypad8 = (Button)view.findViewById(R.id.keypad8);
        keypad9 = (Button)view.findViewById(R.id.keypad9);
        keypadComma = (Button)view.findViewById(R.id.keypadComma);
        keypadDelete = (ImageButton)view.findViewById(R.id.keypadDelete);
        keypadClear = (Button)view.findViewById(R.id.keypadClear);
        keypadOk = (Button)view.findViewById(R.id.keypadOk);

        keypad0.setOnClickListener(this);
        keypad1.setOnClickListener(this);
        keypad2.setOnClickListener(this);
        keypad3.setOnClickListener(this);
        keypad4.setOnClickListener(this);
        keypad5.setOnClickListener(this);
        keypad6.setOnClickListener(this);
        keypad7.setOnClickListener(this);
        keypad8.setOnClickListener(this);
        keypad9.setOnClickListener(this);
        keypadComma.setOnClickListener(this);
        keypadDelete.setOnClickListener(this);
        keypadClear.setOnClickListener(this);
        keypadOk.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.keypadClicked(view.getId());
        }
    }

    public void setKeypadListener(KeypadListener l) {
        listener = l;
    }

    public KeypadListener getKeypadListener() {
        return listener;
    }
}
