package com.makebit.filterss.models;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * RSSFeed object which is written to by the DOMParser, containing
 * different RSSItem objects and the ability to add another item.
 * Also allows for finding the total number of items in the feed
 * if unknown already.
 */
public class RSSFeed implements Serializable {
	// Create a new item count
	private int itemCount = 0;
	// Create a new item list
	private List<Article> itemList;
	// Serializable ID
	private static final long serialVersionUID = 1L;

	public RSSFeed() {
		// Initialize the item list
		itemList = new Vector<Article>(0);
	}

	public void addItem(Article item) {
		// Add an item to the Vector
		itemList.add(item);
		// Increment the item count
		itemCount++;
	}

	public Article getItem(int position) {
		// Return the item at the chosen position
		return itemList.get(position);
	}

	public int getItemCount() {
		// Return the number of items in the feed
		return itemCount;
	}

	public List<Article> getItemList() {
		return itemList;
	}

	public void setItemList(List<Article> itemList) {
		this.itemList = itemList;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "RSSFeed{" +
				"itemCount=" + itemCount +
				", itemList=" + itemList +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RSSFeed rssFeed = (RSSFeed) o;

		if (itemCount != rssFeed.itemCount) return false;
		return itemList != null ? itemList.equals(rssFeed.itemList) : rssFeed.itemList == null;
	}

	@Override
	public int hashCode() {
		int result = itemCount;
		result = 31 * result + (itemList != null ? itemList.hashCode() : 0);
		return result;
	}
}
