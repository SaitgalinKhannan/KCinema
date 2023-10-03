package com.khannan.kcinema.controller

import com.khannan.kcinema.model.*
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

interface MovieController {
    fun searchMovieByTitle(title: String): List<Movie>
    fun movieFullInfoById(movieId: Int): DetailedMovieInfo
    fun movieCastById(movieId: Int): List<Actor>
    fun addUserMovie(pair: Pair<Int, Int>)
    fun deleteUserMovie(userId: Int, movieId: Int)
    fun movieByUser(userId: Int): List<Movie>
    fun movieById(movieId: Int): Movie
    fun allMovies(): List<Movie>
    fun createMovie(movie: Movie, movieFile: MovieMedia)
    fun updateMovie(movieId: Int, movie: Movie, movieFile: MovieMedia)
    fun deleteMovie(movieId: Int)
    fun movieMedia(id: Int, headers: HttpHeaders, response: HttpServletResponse): ResponseEntity<Resource>
}