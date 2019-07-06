package com.makebit.filterss.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.makebit.filterss.R;
import com.makebit.filterss.models.Multifeed;

import java.util.List;


public class MultifeedListAdapter extends ArrayAdapter<Multifeed> {
    private class ViewHolderMultifeed {

        View multifeedViewColor;
        TextView multifeedName;
        TextView multifeedCount;
    }

    private final Context context;

    private final List<Multifeed> multifeeds;
    public MultifeedListAdapter(Context context, List<Multifeed> multifeeds) {
        super(context, -1, multifeeds);
        this.context = context;
        this.multifeeds = multifeeds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderMultifeed viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.multifeed_item, parent, false);

            viewHolder = new ViewHolderMultifeed();
            viewHolder.multifeedViewColor = convertView.findViewById(R.id.viewMultifeedColor);
            viewHolder.multifeedName = convertView.findViewById(R.id.textViewMultifeedName);
            viewHolder.multifeedCount= convertView.findViewById(R.id.textViewMultifeedCount);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderMultifeed) convertView.getTag();
        }

        Multifeed multifeed = multifeeds.get(position);
        if (multifeed != null) {
            viewHolder.multifeedName.setText(multifeed.getTitle());
            viewHolder.multifeedCount.setText(String.valueOf(multifeed.getFeeds().size()));
            // set the multifeed's color
            GradientDrawable background = (GradientDrawable) viewHolder.multifeedViewColor.getBackground();
            background.setColor(multifeed.getColor());
        }
        return convertView;

    }

    public List<Multifeed> getItems() {
        return multifeeds;
    }
}