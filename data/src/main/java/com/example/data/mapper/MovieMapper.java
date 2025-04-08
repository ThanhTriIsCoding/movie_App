package com.example.data.mapper;


import com.example.domain.model.Movie;
import com.example.data.local.entity.MovieEntity;

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