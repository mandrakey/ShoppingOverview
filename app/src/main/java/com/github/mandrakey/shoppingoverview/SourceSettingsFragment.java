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

package com.github.mandrakey.shoppingoverview;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.github.mandrakey.shoppingoverview.activities.SettingsActivity;
import com.github.mandrakey.shoppingoverview.adapters.SourceListAdapter;
import com.github.mandrakey.shoppingoverview.database.Database;
import com.github.mandrakey.shoppingoverview.dialogues.SourceEditDialogue;
import com.github.mandrakey.shoppingoverview.model.Source;

public class SourceSettingsFragment extends Fragment implements View.OnClickListener {

    public static final String ACTION_REFRESH_SOURCES = "refresh_sources";

    private View view;
    private ImageButton btnAdd;
    private ImageButton btnEdit;
    private ImageButton btnDelete;
    private ListView list;

    private Pair<Integer, View> currentSelection;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            broadcastReceived(context, intent);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_sources_settings, container, false);

        IntentFilter ifl = new IntentFilter(ACTION_REFRESH_SOURCES);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, ifl);

        btnAdd = (ImageButton)view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        btnEdit = (ImageButton)view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
        btnDelete = (ImageButton)view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        list = (ListView)view.findViewById(android.R.id.list);
        list.setAdapter(new SourceListAdapter(getActivity()));

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentSelection != null) {
                    currentSelection.second.setBackgroundColor(Color.TRANSPARENT);
                }

                Source s = (Source)list.getAdapter().getItem(i);
                currentSelection = new Pair<>(i, view);
                view.setBackgroundColor(Color.LTGRAY);
                showHideButtons();

                return true;
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd: {
                SourceEditDialogue sed = new SourceEditDialogue((SettingsActivity)getActivity(), null);
                sed.show();
                break;
            }
            case R.id.btnEdit: {
                if (currentSelection == null) return;

                Source s = ((SourceListAdapter) list.getAdapter()).getItem(currentSelection.first);
                SourceEditDialogue sed = new SourceEditDialogue((SettingsActivity)getActivity(), s);
                sed.show();
                break;
            }
            case R.id.btnDelete:
                if (currentSelection == null) return;

                deleteSource();
                break;
            default:
        }
    }

    private void showHideButtons() {
        btnDelete.setVisibility(currentSelection == null ? View.GONE : View.VISIBLE);
        btnEdit.setVisibility(currentSelection == null ? View.GONE : View.VISIBLE);
    }

    private void deleteSource() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete source #" + currentSelection.first + "?")
                .setMessage("Do you really want to delete this source?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SourceListAdapter a = (SourceListAdapter)list.getAdapter();
                        Source s = a.getItem(currentSelection.first);

                        Database db = new Database(getActivity());
                        db.deleteSource(s);
                        db.close();

                        ((SourceListAdapter)list.getAdapter()).refresh();
                        showHideButtons();
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void broadcastReceived(Context context, Intent intent) {
        if (ACTION_REFRESH_SOURCES.equals(intent.getAction())) {
            ((SourceListAdapter)list.getAdapter()).refresh();
        }
    }
}
