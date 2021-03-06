package com.gz.jey.mynews.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gz.jey.mynews.R;

import java.util.List;

public class CheckBoxsAdapter extends RecyclerView.Adapter<CheckBoxsViewHolder>{


    public interface Listener {
    }

    // FOR COMMUNICATION
    private final CheckBoxsAdapter.Listener callback;

    // FOR DATA
    private List<String> categorys;
    private List<Boolean> check;

    /**
     * @param categs List<String>
     * @param checked List<boolean>
     * @param callback CheckBoxAdapter.Listener
     *  CONSTRUCTOR
     */
    public CheckBoxsAdapter(List<String> categs, List<Boolean> checked, CheckBoxsAdapter.Listener callback) {
        this.categorys = categs;
        this.check = checked;
        this.callback = callback;
    }

    /**
     * @param parent ViewGroup
     * @param viewType int
     * @return CheckBoxsViewHolder(View)
     */
    @NonNull
    @Override
    public CheckBoxsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.checkbox_item, parent, false);
        return new CheckBoxsViewHolder(view);
    }

    /**
     * @param viewHolder CheckBoxsViewHolder
     * @param position int
     * UPDATE VIEW HOLDER WITH CHECKBOXS
     */
    @Override
    public void onBindViewHolder(@NonNull CheckBoxsViewHolder viewHolder, int position) {
        viewHolder.updateCheckBoxs(this.categorys.get(position),this.callback,this.check.get(position));
    }

    /**
     * @return THE TOTAL COUNT OF ITEMS IN THE LIST
     */
    @Override
    public int getItemCount() {
        return this.categorys.size();
    }

}

