package com.qamp.app.messaging;

import android.util.SparseBooleanArray;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class SelectableAdapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {
    private static final String TAG = SelectableAdapter.class.getSimpleName();
    private SparseBooleanArray selectedItems;

    public SelectableAdapter() {
        this.selectedItems = null;
        this.selectedItems = new SparseBooleanArray();
    }

    public boolean isSelected(int position) {
        return getSelectedItems().contains(Integer.valueOf(position));
    }

    public void toggleSelection(int position) {
        if (this.selectedItems.get(position, false)) {
            this.selectedItems.delete(position);
        } else {
            this.selectedItems.put(position, true);
        }
    }

    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        this.selectedItems.clear();
        for (Integer i : selection) {
            notifyItemChanged(i.intValue());
        }
    }

    public List ListOfSelelction() {
        List<Integer> list = getSelectedItems();
        this.selectedItems.clear();
        return list;
    }

    public void clearSelectedItems() {
        this.selectedItems.clear();
    }

    public int getSelectedItemCount() {
        return this.selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(this.selectedItems.size());
        for (int i = 0; i < this.selectedItems.size(); i++) {
            items.add(Integer.valueOf(this.selectedItems.keyAt(i)));
        }
        return items;
    }
}
