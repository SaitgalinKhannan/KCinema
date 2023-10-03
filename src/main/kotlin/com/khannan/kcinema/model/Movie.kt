package com.khannan.kcinema.model

data class Movie(
    val id: Int,
    val title: String,
    val releaseYear: Int,
    val runtimeMinutes: Int,
    val language: String,
    val releaseCountry: String,
)

data class DetailedMovieInfo(
    val movie: Movie,
    val movieMedia: MovieMedia,
    val genres: List<Genre>,
    val cast: List<Actor>,
    val directors: List<Director>,
    val reviews: List<Review>,
    val rating: Int
)

data class Actor(
    val actorId: Int,
    val firstName: String,
    val lastName: String,
    val role: String,
    val gender: String
)

data class Director(
    val directorId: Int,
    val firstName: String,
    val lastName: String
)

data class Genre(
    val genreId: Int,
    val title: String
)

@Suppress("Unused")
data class FullMovie(
    val movie: Movie,
    val movieMedia: MovieMedia
)

data class MovieMedia(
    val id: Int,
    val filePath: String,
    val previewFilePath: String
)

data class Review(
    val reviewId: Int,
    val reviewerName: String,
    val rating: Int
)

@Suppress("Unused")
data class Reviewer(
    val reviewerId: Int,
    val name: String
)
