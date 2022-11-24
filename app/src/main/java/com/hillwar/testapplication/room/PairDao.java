package com.hillwar.testapplication.room;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PairDao {

    @Query("SELECT * FROM pairqa")
    List<PairQA> getAll();

    @Query("SELECT * FROM pairqa WHERE id = :id")
    default PairQA getById(long id) {
        return null;
    }

    @Insert
    void insert(PairQA employee);

    @Update
    void update(PairQA employee);

    @Delete
    void delete(PairQA employee);

}
