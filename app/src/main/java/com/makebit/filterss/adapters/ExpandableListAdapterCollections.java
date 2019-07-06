package com.makebit.filterss.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makebit.filterss.R;
import com.makebit.filterss.models.Collection;
import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.Multifeed;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapterCollections extends BaseExpandableListAdapter {
    private Context context;
    private List<Collection> collections;

    public ExpandableListAdapterCollections(Context context, List<Collection> collections) {
        this.context = context;
        this.collections = collections;
    }


    @Override
    public Collection getGroup(int groupPosition) {
        return collections.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return this.collections.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).getTitle();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_view_group, null);
        }

        //Set the Title for each group
        TextView textView = convertView.findViewById(R.id.exp_menu_group_name);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(headerTitle);
        textView.setTextColor(getGroup(groupPosition).getColor());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    /**
     * Sets the number(TextView) of articles for each feed, data mapped in a HashMap passed as argument
     * @param feedArticlesNumberMap HashMap of the feed,number of articles association
     */
    /*public void updateFeedArticlesNumbers(Map<String,Integer> feedArticlesNumberMap) {
        feedArticlesNumber = (HashMap<String, Integer>) feedArticlesNumberMap;
    }*/
}
