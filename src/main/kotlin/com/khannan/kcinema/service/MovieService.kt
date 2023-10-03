package com.khannan.kcinema.service

import com.khannan.kcinema.model.*

interface MovieService {
    fun movieById(id: Int): Movie
    fun searchMovieByTitle(title: String): List<Movie>
    fun movieFullInfoById(id: Int): DetailedMovieInfo
    fun movieCastById(id: Int): List<Actor>
    fun addUserMovie(userId: Int, movieId: Int)
    fun deleteUserMovie(userId: Int, movieId: Int)
    fun movieByUser(id: Int): List<Movie>
    fun allMovies(): List<Movie>
    fun createMovie(movie: Movie, movieMedia: MovieMedia)
    fun updateMovie(id: Int, movie: Movie, movieMedia: MovieMedia)
    fun deleteMovie(id: Int)
    fun movieMedia(id: Int): MovieMedia
}