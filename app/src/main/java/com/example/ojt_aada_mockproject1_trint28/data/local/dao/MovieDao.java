package com.example.ojt_aada_mockproject1_trint28.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ojt_aada_mockproject1_trint28.data.local.entity.MovieEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies WHERE userId = :userId")
    Flowable<List<MovieEntity>> getFavoriteMovies(int userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFavoriteMovie(MovieEntity movie);

    @Query("DELETE FROM movies WHERE id = :movieId AND userId = :userId")
    Completable deleteFavoriteMovie(int movieId, int userId);

    @Query("SELECT COUNT(*) FROM movies WHERE id = :movieId AND userId = :userId")
    Single<Integer> isMovieLiked(int movieId, int userId);
}