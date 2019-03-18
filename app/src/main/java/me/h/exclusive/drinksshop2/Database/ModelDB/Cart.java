package me.h.exclusive.drinksshop2.Database.ModelDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Cart")
public class Cart {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "link")
    public String link;

    @ColumnInfo(name = "amount")
    public int amount;

    @ColumnInfo(name = "price")
    public int price;

    @ColumnInfo(name = "suger")
    public int suger;

    @ColumnInfo(name = "ice")
    public int ice;

    @ColumnInfo(name = "size")
    public int size;

    @ColumnInfo(name = "toopingExtras")
    public String toopingExtras;


}
