package com.ibm.iot4i.models;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class ListResponse {
    private int offset;
    private int limit;
    private int totalItems;
    private ArrayList<JsonObject> items;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public ArrayList<JsonObject> getItems() {
        return items;
    }

    public void setItems(ArrayList<JsonObject> items) {
        this.items = items;
    }

	@Override
	public String toString() {
		return "ListResponse [offset=" + offset + ", limit=" + limit + ", totalItems=" + totalItems + ", items=" + items
				+ "]";
	}
    
    
}
