package com.github.mandrakey.shoppingoverview.widgets;

import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mandrakey.shoppingoverview.viewholders.PurchaseListItemViewholder;

public class PurchaseListItemTouchListener implements View.OnTouchListener {

    public static final int MARGIN_HIDDEN = -130;
    public static final int MARGIN_VISIBLE = 0;

    private Pair<Float, Float> swipeStart;
    private ViewGroup.MarginLayoutParams lp;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        PurchaseListItemViewholder vh = (PurchaseListItemViewholder)view.getTag();
        lp = (ViewGroup.MarginLayoutParams)vh.llEditButtons.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                swipeStart = new Pair<>(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                swipeButtons(vh, swipeStart.first - event.getX());
                break;
            default:
                if (lp.getMarginEnd() < MARGIN_VISIBLE) {
                    //noinspection ResourceType
                    lp.setMarginEnd(MARGIN_HIDDEN);
                    vh.llEditButtons.setLayoutParams(lp);
                    vh.tvSwipeHint.setVisibility(View.VISIBLE);
                } else {
                    vh.tvSwipeHint.setVisibility(View.GONE);
                }
                return false;
        }
        return true;
    }

    private void swipeButtons(PurchaseListItemViewholder vh, float deltaX) {
        int newPos = lp.getMarginEnd() + (int)(deltaX / 20);
        if (newPos < MARGIN_HIDDEN) newPos = MARGIN_HIDDEN;
        if (newPos > MARGIN_VISIBLE) newPos = MARGIN_VISIBLE;

        //noinspection ResourceType
        lp.setMarginEnd(newPos);
        vh.llEditButtons.setLayoutParams(lp);
    }
}
