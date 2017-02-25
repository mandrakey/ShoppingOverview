package com.github.mandrakey.shoppingoverview.dialogues;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mandrakey.shoppingoverview.R;
import com.github.mandrakey.shoppingoverview.activities.MainActivity;
import com.github.mandrakey.shoppingoverview.database.Database;
import com.github.mandrakey.shoppingoverview.model.Purchase;
import com.github.mandrakey.shoppingoverview.viewholders.PurchaseListItemViewholder;

public class DeletePurchaseDialogue {

    private Context context;
    private Purchase purchase;

    private View view;
    private PurchaseListItemViewholder vh;

    public DeletePurchaseDialogue(Context c, Purchase p) {
        context = c;
        purchase = p;
    }

    public void show() {
        view = LayoutInflater.from(context)
                .inflate(R.layout.dialogue_purchase_delete, null);

        vh = new PurchaseListItemViewholder();
        vh.tvSum = (TextView)view.findViewById(R.id.tvSum);
        vh.icon = (ImageView)view.findViewById(android.R.id.icon);
        vh.tvSourceName = (TextView)view.findViewById(R.id.tvSourceName);
        vh.tvCategory = (TextView)view.findViewById(R.id.tvCategory);
        vh.tvDateTime = (TextView)view.findViewById(R.id.tvDateTime);

        vh.tvSum.setText(context.getResources().getString(R.string.price_format, purchase.sum));
        vh.icon.setImageBitmap(purchase.getSource().image);
        vh.tvSourceName.setText(purchase.getSource().name);
        vh.tvCategory.setText(purchase.getCategory().name);
        vh.tvDateTime.setText(purchase.datetime.toString());

        view.findViewById(R.id.tvSwipeHint).setVisibility(View.GONE);

        new AlertDialog.Builder(context)
                .setTitle("Delete purchase?")
                .setView(view)
                .setNegativeButton("Abort", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Database(context).deletePurchase(purchase);
                        LocalBroadcastManager.getInstance(context)
                                .sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_PURCHASES));
                        dialogInterface.dismiss();
                    }
                }).show();
    }

}
