package com.khannan.kcinema.controller

import com.khannan.kcinema.model.Actor
import com.khannan.kcinema.model.DetailedMovieInfo
import com.khannan.kcinema.model.Movie
import com.khannan.kcinema.model.MovieMedia
import com.khannan.kcinema.service.MovieService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.FileUrlResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.IOException
import java.nio.file.Files


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

    @GetMapping("/download/{id}")
    override fun movieMedia(
        @PathVariable id: Int,
        @RequestHeader headers: HttpHeaders,
        response: HttpServletResponse
    ): ResponseEntity<Resource> {

        val path = service.movieMedia(id)
        val video = FileUrlResource(path.filePath)

        if (!video.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }


        if (headers.range.size != 0) {
            val totalLength = video.file.length()
            val byteRange = headers.range.first()
            val start = byteRange.getRangeStart(0L)
            val end = byteRange.getRangeEnd(headers.contentLength)

            if (start >= totalLength) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build()
            }

            val contentLength = end - start + 1
            val httpHeaders = HttpHeaders()

            httpHeaders.add("Content-Range", "bytes $start-$end/$totalLength")
            httpHeaders.add("Accept-Ranges", "bytes")
            httpHeaders.add("Content-Length", contentLength.toString())
            httpHeaders.contentType = MediaType.APPLICATION_OCTET_STREAM

            try {
                val inputStream = Files.newInputStream(video.file.toPath())
                inputStream.use { input ->
                    val outputStream = response.outputStream
                    outputStream.use { output ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        var bytesToRead = contentLength

                        while (bytesToRead > 0) {
                            bytesRead = input.read(buffer)
                            if (bytesRead == -1) {
                                break
                            }

                            if (bytesToRead >= bytesRead) {
                                output.write(buffer, 0, bytesRead)
                                bytesToRead -= bytesRead
                            } else {
                                output.write(buffer, 0, bytesToRead.toInt())
                                bytesToRead = 0
                            }
                        }
                    }
                }

                return ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .headers(httpHeaders).body(video)
            } catch (e: IOException) {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build()
            }
        } else {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_OCTET_STREAM
            httpHeaders.contentLength = video.file.length()
            httpHeaders.setContentDispositionFormData("attachment", video.filename)

            try {
                val inputStream = Files.newInputStream(video.file.toPath())
                inputStream.use { input ->
                    val outputStream = response.outputStream
                    outputStream.use { output ->
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

                return ResponseEntity.ok().headers(httpHeaders).body(video)
            } catch (e: IOException) {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build()
            }
        }
    }
}