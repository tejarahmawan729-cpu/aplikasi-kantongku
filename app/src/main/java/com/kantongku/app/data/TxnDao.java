package com.kantongku.app.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TxnDao {
    @Insert
    void insert(Txn txn);

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC, id DESC")
    LiveData<List<Txn>> observeAll();
}
