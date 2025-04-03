package com.example.ojt_aada_mockproject1_trint28.data.mapper;

import com.example.ojt_aada_mockproject1_trint28.data.local.entity.MovieEntity;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;

public class MovieMapper {

    public static MovieEntity toEntity(Movie movie, int userId) {
        return new MovieEntity(
                movie.getId(),
                userId,
                movie.getTitle(),
                movie.getOverview(),
                movie.getReleaseDate(),
                movie.getVoteAverage(),
                movie.isAdult(),
                movie.getPosterUrl(),
                movie.isLiked()
        );
    }

    public static Movie toDomain(MovieEntity entity) {
        return new Movie(
                entity.getId(),
                entity.getTitle(),
                entity.getOverview(),
                entity.getReleaseDate(),
                entity.getVoteAverage(),
                entity.isAdult(),
                entity.getPosterUrl(),
                entity.isLiked()
        );
    }
}