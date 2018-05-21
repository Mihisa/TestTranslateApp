package com.mihisa.testtranslateapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mihisa.testtranslateapp.adapters.HistoryAdapter;
import com.mihisa.testtranslateapp.utils.ConstResources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    List<History> dataset = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initRecyclerView(view);
        setHasOptionsMenu(true);
        return view;
    }

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rv_history);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        dataset = loadCacheData();
        adapter = new HistoryAdapter(dataset, new OnFavoriteClickListener() {
            @Override
            public void onFavClick(int position) {
                markAsFav(position);
            }
        });
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeItemFromHistory(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void removeItemFromHistory(int position) {
        History item = dataset.get(position);
        dataset.remove(position);
        String name = item.getTextFrom() + item.getTextTo() + item.getLang();

        SharedPreferences prefs = getActivity().getSharedPreferences(ConstResources.PREFS_CACHE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(name);
        editor.apply();
    }


    private List<History> loadCacheData() {
        Map<String, String> allEntries = (Map<String, String>) getContext().getSharedPreferences(ConstResources.PREFS_CACHE_NAME, Context.MODE_PRIVATE).getAll();
        List<History> list = new ArrayList<>();
        Gson gson = new Gson();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = entry.getValue().toString();
            History historyItem = gson.fromJson(json, History.class);
            list.add(new History(historyItem.getLang(), historyItem.getTextTo(), historyItem.getTextFrom(), historyItem.getDate(), historyItem.isMarkedFav()));
        }
        Collections.sort(list);
        return list;
    }

    private void markAsFav(int position) {
        History item = dataset.get(position);
        item.setMarkedFav(!item.isMarkedFav());

        String name = item.getTextFrom() + item.getTextTo() + item.getLang();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(item);

        SharedPreferences prefs = getActivity().getSharedPreferences(ConstResources.PREFS_CACHE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(name, json);
        editor.apply();

    }
}
