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
import com.makebit.filterss.models.Feed;
import com.makebit.filterss.models.Multifeed;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapterMultifeeds extends BaseExpandableListAdapter {
    private Context context;
    private List<Multifeed> multifeeds;

    public ExpandableListAdapterMultifeeds(Context context, List<Multifeed> multifeeds) {
        this.context = context;
        this.multifeeds = multifeeds;
    }

    @Override
    public Feed getChild(int groupPosition, int childPosition) {
        List<Feed> feeds = this.multifeeds.get(groupPosition).getFeeds();
        Feed feed = feeds.get(childPosition);
        return feed;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Feed feed = getChild(groupPosition, childPosition);
        final String childText = feed.getTitle();
        final String iconLink = feed.getIconURL();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_view_item, null);
        }

        //Text view containing the Name of the Child (feed)
        TextView textView = convertView.findViewById(R.id.exp_menu_group_item);
        textView.setText(childText);

        //Child's Icon
        ImageView feedIcon = convertView.findViewById(R.id.exp_menu_group_item_icon);
        if (iconLink != null && !iconLink.isEmpty())
            Picasso.get()
                    .load(iconLink)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.ic_rss_feed_black_24dp)
                    .error(R.drawable.ic_rss_feed_black_24dp)
                    .noFade()
                    .into(feedIcon);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Feed> feeds = this.multifeeds.get(groupPosition).getFeeds();
        return feeds.size();
    }

    @Override
    public Multifeed getGroup(int groupPosition) {
        return multifeeds.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.multifeeds.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
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
