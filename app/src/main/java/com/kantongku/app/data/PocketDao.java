package com.kantongku.app.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PocketDao {
    @Query("SELECT * FROM pockets ORDER BY id")
    LiveData<List<Pocket>> observeAll();

    @Query("SELECT * FROM pockets ORDER BY id")
    List<Pocket> getAll();

    @Query("SELECT * FROM pockets WHERE id = :id")
    Pocket getById(long id);

    @Insert
    long insert(Pocket pocket);

    @Query("UPDATE pockets SET name = :name, percent = :percent WHERE id = :id")
    void updateInfo(long id, String name, int percent);

    @Query("UPDATE pockets SET balance = balance + :delta WHERE id = :id")
    void addBalance(long id, long delta);

    @Query("DELETE FROM pockets WHERE id = :id")
    void deleteById(long id);
}
