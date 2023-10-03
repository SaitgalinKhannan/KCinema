package com.khannan.kcinema.repository

import com.khannan.kcinema.model.*

interface MovieRepository {
    fun searchMovieByTitle(title: String): List<Movie>
    fun movieFullInfo(id: Int): DetailedMovieInfo
    fun insertUserMovie(userId: Int, movieId: Int)
    fun deleteUserMovie(userId: Int, movieId: Int)
    fun movieByUser(id: Int): List<Movie>
    fun movieRating(id: Int): Int
    fun movieReviews(id: Int): List<Review>
    fun movieDirectors(id: Int): List<Director>
    fun movieCast(id: Int): List<Actor>
    fun movieGenre(id: Int): List<Genre>
    fun create(movie: Movie, movieMedia: MovieMedia): Int
    fun movieById(id: Int): Movie
    fun deleteMovieMedia(id: Int): Int
    fun movieMedia(id: Int): MovieMedia
    fun allMovies(): List<Movie>
    fun allMoviesFiles(): List<MovieMedia>
    fun updateMovie(id: Int, movie: Movie, movieMedia: MovieMedia)
    fun deleteMovie(id: Int)
}