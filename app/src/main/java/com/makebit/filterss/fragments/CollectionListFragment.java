package com.makebit.filterss.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.makebit.filterss.ArticleActivity;
import com.makebit.filterss.R;
import com.makebit.filterss.adapters.CollectionListAdapter;
import com.makebit.filterss.models.Collection;

import java.util.ArrayList;
import java.util.List;

import top.defaults.colorpicker.ColorPickerPopup;

public class CollectionListFragment extends Fragment {
    private final String TAG = getClass().getName();

    private ArrayList<Collection> collections;
    private CollectionListAdapter adapter;

    private CollectionEditInterface collectionEditInterface;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static CollectionListFragment newInstance(ArrayList<Collection> collections) {
        CollectionListFragment fragment = new CollectionListFragment();
        Bundle args = new Bundle();
        args.putSerializable("collections", collections);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_list, container, false);

        collections = (ArrayList<Collection>) getArguments().getSerializable("collections");
        adapter = new CollectionListAdapter(getContext(), collections);

        final ListView listview = view.findViewById(R.id.listViewCollectionList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Collection collection = (Collection) parent.getItemAtPosition(position);
                Log.v(ArticleActivity.logTag, "Collection " + id + " clicked" + collection.toString());

                onCollectionSelected(position);
            }

        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
                final Collection collection = (Collection) parent.getItemAtPosition(position);
                Log.v(ArticleActivity.logTag, "Collection " + id + " long clicked " + collection.toString());

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_remove_collection_title)
                        .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag, "Removing collection " + position + " collection: " + parent.getItemAtPosition(position));

                                collectionEditInterface.onDeleteCollection(collection, collections, position, adapter);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag, "Dialog closed");
                            }
                        })
                        .show();

                return true;
            }
        });

        return view;
    }


    public void onCollectionSelected(final int position) {
        final Collection collection = collections.get(position);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_collection_edit, null);
        builder.setView(dialogView);
        final AlertDialog editCollectionDialog = builder.create();

        final TextView collectionTitle = dialogView.findViewById(R.id.editTextCollectionEditTitle);
        final View collectionColor = dialogView.findViewById(R.id.viewCollectionEditColor);

        collectionTitle.setText(collection.getTitle());
        GradientDrawable background = (GradientDrawable) collectionColor.getBackground();
        background.setColor(collection.getColor());

        collectionColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                editCollectionDialog.dismiss();
                new ColorPickerPopup.Builder(getContext())
                        .initialColor(collection.getColor())
                        .enableBrightness(false)
                        .enableAlpha(false)
                        .okTitle(getString(R.string.choose))
                        .cancelTitle(getString(R.string.cancel))
                        .showIndicator(false)
                        .showValue(false)
                        .build()
                        .show(new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                GradientDrawable background = (GradientDrawable) v.getBackground();
                                background.setColor(color);
                                collection.setColor(color);
                                onCollectionSelected(position);
                            }

                            @Override
                            public void onColor(int color, boolean fromUser) {

                            }
                        });
            }
        });

        editCollectionDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        String name = collectionTitle.getText().toString();
                        collection.setTitle(name);

                        collectionEditInterface.onUpdateCollection(collection, adapter);

                    }
                }
        );

        /*builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = collectionName.getText().toString();
                collection.setName(name);
                Log.v(ArticleActivity.logTag, "Saving collection: " + collection.toString());
                adapter.notifyDataSetChanged();
            }
        });*/

        editCollectionDialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CollectionEditInterface) {
            collectionEditInterface = (CollectionEditInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CollectionEditInterface");
        }
    }


    /**
     * onUpdateCollection: called when the collection is updated
     * onDeleteCollection: called when a collection is deleted
     */
    public interface CollectionEditInterface {
        void onUpdateCollection(Collection collection, CollectionListAdapter adapter);
        void onDeleteCollection(Collection collection, List<Collection> collections, int position, CollectionListAdapter adapter);
    }

}
