package com.khannan.kcinema.controller

import com.khannan.kcinema.model.Actor
import com.khannan.kcinema.model.DetailedMovieInfo
import com.khannan.kcinema.model.Movie
import com.khannan.kcinema.model.MovieMedia
import com.khannan.kcinema.service.MovieService
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.FileUrlResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
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
        return service.searchMovieByTitle(title)
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
        return service.movieByUser(userId)
    }

    override fun allMovies(): List<Movie> {
        return service.allMovies()
    }

    override fun createMovie(movie: Movie, movieFile: MovieMedia) {
        service.createMovie(movie, movieFile)
    }

    override fun updateMovie(movieId: Int, movie: Movie, movieFile: MovieMedia) {
        service.updateMovie(movieId, movie, movieFile)
    }

    override fun deleteMovie(movieId: Int) {
        service.deleteMovie(movieId)
    }

    @GetMapping("/download/{id}")
    override fun downloadMovieMedia(
        @PathVariable id: Int, @RequestHeader headers: HttpHeaders
    ): ResponseEntity<StreamingResponseBody> {
        val path = service.movieMedia(id)
        val video = FileUrlResource(path.filePath)

        if (!video.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        try {
            val streamingResponseBody = StreamingResponseBody { outputStream1: OutputStream ->
                val inputStream = Files.newInputStream(video.file.toPath())
                inputStream.use { input ->
                    outputStream1.use { output ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int

                        while (true) {
                            bytesRead = input.read(buffer)
                            if (bytesRead == -1) {
                                break
                            }
                            output.write(buffer, 0, bytesRead)
                        }
                    }
                }
            }

            val filename = video.file.name
            val httpHeaders = HttpHeaders()
            httpHeaders.contentDisposition =
                ContentDisposition.builder("attachment").filename(filename, StandardCharsets.UTF_8).build()
            httpHeaders.contentType = MediaType.APPLICATION_OCTET_STREAM
            httpHeaders.contentLength = video.file.length()

            return ResponseEntity.ok().headers(httpHeaders).body(streamingResponseBody)
        } catch (e: IOException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/media/{id}")
    override fun movieMedia(@RequestHeader headers: HttpHeaders, @PathVariable id: Int): ResponseEntity<ResourceRegion> {
        val path = service.movieMedia(id)
        val media: Resource = FileSystemResource(path.filePath)
        val region = resourceRegion(media, headers)

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).contentType(
            MediaTypeFactory.getMediaType(media).orElse(MediaType.APPLICATION_OCTET_STREAM)
        ).body(region)
    }

    private fun resourceRegion(media: Resource, headers: HttpHeaders): ResourceRegion {
        var contentLength = 0L

        try {
            contentLength = media.contentLength()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val range = headers.getRange()
        return if (range.isNotEmpty()) {
            val start = range.first().getRangeStart(contentLength)
            val end = if (range.first().getRangeEnd(contentLength) > 1) range.first()
                .getRangeEnd(contentLength) else contentLength - 1
            val rangeLength = min((end - start + 1).toDouble(), contentLength.toDouble()).toLong()
            ResourceRegion(media, start, rangeLength)
        } else {
            val rangeLength = min((1024 * 1024).toDouble(), contentLength.toDouble()).toLong()
            ResourceRegion(media, 0, rangeLength)
        }
    }
}