package com.makebit.filterss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Category implements Serializable {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("lang")
    @Expose
    private String lang;

    @SerializedName("description")
    @Expose
    private String description;

    public Category() {
    }

    public Category(String name, String lang, String description) {
        this.name = name;
        this.lang = lang;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", lang='" + lang + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (!name.equals(category.name)) return false;
        if (!lang.equals(category.lang)) return false;
        return description != null ? description.equals(category.description) : category.description == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + lang.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
