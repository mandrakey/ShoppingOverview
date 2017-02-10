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

package com.github.mandrakey.shoppingoverview.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class PurchaseTextView extends TextView {

    protected char content[] = new char[20];
    protected int curIndex = 0;
    protected int decimalsStart = -1;

    public PurchaseTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PurchaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public PurchaseTextView(Context context) {
        super(context);
    }

    public void add(char c) {
        if (curIndex >= content.length) {
            throw new IndexOutOfBoundsException("Cannot add any more text.");
        }

        if (c == ',' || c == '.') {
            if (decimalsStart > -1) {
                throw new RuntimeException("Cannot add more than 1 decimal point.");
            }
            c = '.';
            decimalsStart = curIndex;
        }
        content[curIndex++] = c;
        setText(new String(content));
    }

    public void remove() {
        if (curIndex == 0) return;

        if (decimalsStart == curIndex) {
            decimalsStart = -1;
        }
        content[--curIndex] = '\0';
        setText(new String(content));
    }

    public void empty() {
        for (int i = 0; i < content.length; ++i) content[i] = '\0';
        curIndex = 0;
        decimalsStart = -1;
        setText(new String(content));
    }

    @Override
    public CharSequence getText() {
        return super.getText().toString().replace("\0", "").replace(",", ".");
    }
}
