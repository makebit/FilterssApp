package com.makebit.filterss.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.makebit.filterss.R;
import com.makebit.filterss.models.Collection;

import java.util.List;


public class CollectionListAdapter extends ArrayAdapter<Collection> {

    private class ViewHolderCollection {
        View collectionViewColor;
        TextView collectionName;
    }

    private final Context context;
    private final List<Collection> collections;

    public CollectionListAdapter(Context context, List<Collection> collections) {
        super(context, -1, collections);
        this.context = context;
        this.collections = collections;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderCollection viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.collection_item, parent, false);

            viewHolder = new ViewHolderCollection();
            viewHolder.collectionViewColor = convertView.findViewById(R.id.viewCollectionColor);
            viewHolder.collectionName = convertView.findViewById(R.id.textViewCollectionName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderCollection) convertView.getTag();
        }

        Collection collection = collections.get(position);
        if (collection != null) {
            viewHolder.collectionName.setText(collection.getTitle());
            // set the collection's color
            GradientDrawable background = (GradientDrawable) viewHolder.collectionViewColor.getBackground();
            background.setColor(collection.getColor());
        }
        return convertView;

    }

    public List<Collection> getItems() {
        return collections;
    }
}