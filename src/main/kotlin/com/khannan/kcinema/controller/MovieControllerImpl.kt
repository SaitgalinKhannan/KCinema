package com.khannan.kcinema.controller

import com.khannan.kcinema.model.*
import com.khannan.kcinema.service.MovieService
import org.springframework.core.io.FileUrlResource
import org.springframework.core.io.UrlResource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import kotlin.math.min


@RestController
@RequestMapping("/movies")
class MovieControllerImpl(val service: MovieService) : MovieController {

    @GetMapping("/{movieId}")
    override fun movieById(@PathVariable movieId: Int): Movie {
        return service.movieById(movieId)
    }

    @GetMapping("/search/{title}")
    override fun searchMovieByTitle(@PathVariable title: String): List<Movie> {
        TODO("Not yet implemented")
    }

    @GetMapping("/info/{movieId}")
    override fun movieFullInfoById(@PathVariable movieId: Int): DetailedMovieInfo {
        return service.movieFullInfoById(movieId)
    }

    @GetMapping("/cast/{movieId}")
    override fun movieCastById(@PathVariable movieId: Int): List<Actor> {
        return service.movieCastById(movieId)
    }

    @PostMapping("/users/movie")
    override fun addUserMovie(@RequestBody pair: Pair<Int, Int>) {
        return service.addUserMovie(userId = pair.first, movieId = pair.second)
    }

    override fun deleteUserMovie(userId: Int, movieId: Int) {
        return service.deleteUserMovie(userId = userId, movieId = movieId)
    }

    @GetMapping("/user/movies")
    override fun movieByUser(userId: Int): List<Movie> {
        TODO("Not yet implemented")
    }

    override fun allMovies(): List<Movie> {
        TODO("Not yet implemented")
    }

    override fun createMovie(movie: Movie, movieFile: MovieMedia) {
        TODO("Not yet implemented")
    }

    override fun updateMovie(movieId: Int, movie: Movie, movieFile: MovieMedia) {
        TODO("Not yet implemented")
    }

    override fun deleteMovie(movieId: Int) {
        TODO("Not yet implemented")
    }

    @GetMapping("/media/{id}")
    override fun movieMedia(
        @PathVariable id: Int,
        @RequestHeader headers: HttpHeaders
    ): ResponseEntity<ResourceRegion> {
        val path = service.movieMedia(id)
        val video = FileUrlResource(path.filePath)
        val region = resourceRegion(video, headers)
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .contentType(
                MediaTypeFactory
                    .getMediaType(video)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM)
            )
            .body(region)
    }

    private fun resourceRegion(video: UrlResource, headers: HttpHeaders): ResourceRegion {
        val contentLength = video.contentLength()
        val range = headers.range.firstOrNull()
        return if (range != null) {
            val start = range.getRangeStart(contentLength)
            val end = range.getRangeEnd(contentLength)
            val rangeLength = min(1 * 1024 * 1024, end - start + 1)
            ResourceRegion(video, start, rangeLength)
        } else {
            val rangeLength = min(1 * 1024 * 1024, contentLength)
            ResourceRegion(video, 0, rangeLength)
        }
    }
}