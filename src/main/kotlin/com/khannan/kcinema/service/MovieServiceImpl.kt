package com.khannan.kcinema.service

import com.khannan.kcinema.model.*
import com.khannan.kcinema.repository.MovieRepository
import org.springframework.stereotype.Service

@Service
class MovieServiceImpl(private val repository: MovieRepository) : MovieService {
    override fun movieById(id: Int): Movie {
        return repository.movieById(id)
    }

    override fun searchMovieByTitle(title: String): List<Movie> {
        return repository.searchMovieByTitle(title)
    }

    override fun movieFullInfoById(id: Int): DetailedMovieInfo {
        return repository.movieFullInfo(id)
    }

    override fun movieCastById(id: Int): List<Actor> {
        return repository.movieCast(id)
    }

    override fun addUserMovie(userId: Int, movieId: Int) {
        repository.insertUserMovie(userId, movieId)
    }

    override fun deleteUserMovie(userId: Int, movieId: Int) {
        repository.deleteUserMovie(userId, movieId)
    }

    override fun movieByUser(id: Int): List<Movie> {
        return repository.movieByUser(id)
    }

    override fun allMovies(): List<Movie> {
        return repository.allMovies()
    }

    override fun createMovie(movie: Movie, movieMedia: MovieMedia) {
        repository.create(movie, movieMedia)
    }

    override fun updateMovie(id: Int, movie: Movie, movieMedia: MovieMedia) {
        repository.updateMovie(id, movie, movieMedia)
    }

    override fun deleteMovie(id: Int) {
        repository.deleteMovie(id)
    }

    override fun movieMedia(id: Int): MovieMedia {
        return repository.movieMedia(id)
    }
}