package com.khannan.kcinema.repository

import com.khannan.kcinema.model.*
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

@Repository
class MovieRepositoryImpl(db: DataSource) : MovieRepository {
    private val connection: Connection = db.connection

    companion object {
        private const val CREATE_TABLE_MOVIES =
            "CREATE TABLE IF NOT EXISTS Movie (movId INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY, movTitle VARCHAR(255) NOT NULL, movYear INTEGER NOT NULL, movTime INTEGER NOT NULL, movLang VARCHAR(255) NOT NULL, movRelCountry VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_ACTOR =
            "CREATE TABLE IF NOT EXISTS Actor (actId INTEGER PRIMARY KEY generated always as identity, actFirstName VARCHAR(255) NOT NULL, actLastName VARCHAR(255) NOT NULL, actGender VARCHAR(10) NOT NULL)"
        private const val CREATE_TABLE_DIRECTOR =
            "CREATE TABLE IF NOT EXISTS Director (dirId INTEGER PRIMARY KEY, dirFirstName VARCHAR(255) NOT NULL, dirLastName VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_GENRE =
            "CREATE TABLE IF NOT EXISTS Genre (genId INTEGER PRIMARY KEY, genTitle VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_MOVIE_CAST =
            "CREATE TABLE IF NOT EXISTS MovieCast (actId INTEGER NOT NULL, movId INTEGER NOT NULL, role VARCHAR(255) NOT NULL, PRIMARY KEY (actId, movId), FOREIGN KEY (actId) REFERENCES Actor(actId), FOREIGN KEY (movId) REFERENCES Movie(movId))"
        private const val CREATE_TABLE_MOVIE_DIRECTION =
            "CREATE TABLE IF NOT EXISTS MovieDirection (dirId INTEGER NOT NULL, movId INTEGER NOT NULL, PRIMARY KEY (dirId, movId), FOREIGN KEY (dirId) REFERENCES Director(dirId), FOREIGN KEY (movId) REFERENCES Movie(movId))"
        private const val CREATE_TABLE_MOVIE_GENRE =
            "CREATE TABLE IF NOT EXISTS MovieGenre (movId INTEGER NOT NULL, genId INTEGER NOT NULL, PRIMARY KEY (movId, genId), FOREIGN KEY (movId) REFERENCES Movie(movId), FOREIGN KEY (genId) REFERENCES Genre(genId))"
        private const val CREATE_TABLE_REVIEWER =
            "CREATE TABLE IF NOT EXISTS Reviewer (revId INTEGER PRIMARY KEY, revName VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_REVIEW =
            "CREATE TABLE IF NOT EXISTS Review (movId INTEGER NOT NULL, revId INTEGER NOT NULL, revStars INTEGER NOT NULL, PRIMARY KEY (movId, revId), FOREIGN KEY (movId) REFERENCES Movie(movId), FOREIGN KEY (revId) REFERENCES Reviewer(revId))"
        private const val CREATE_TABLE_MOVIE_FILE =
            "CREATE TABLE IF NOT EXISTS MovieFile (movId INTEGER NOT NULL, movPath VARCHAR(255) NOT NULL, movPreviewPath VARCHAR(255) NOT NULL, CONSTRAINT id_unique UNIQUE (movId), FOREIGN KEY (movId) REFERENCES Movie (movId))"
        private const val CREATE_TABLE_USER_MOVIE =
            "CREATE TABLE IF NOT EXISTS userMovie (userid INTEGER, movId INTEGER, FOREIGN KEY (userid) REFERENCES cinemauser(userid), FOREIGN KEY (movid) REFERENCES movie(movid), PRIMARY KEY (userid, movid))"
        private const val SELECT_MOVIE_BY_ID =
            "SELECT movtitle, movyear, movtime, movlang, movrelcountry FROM movie WHERE movid = ?"
        private const val SELECT_MOVIE_FILE_BY_ID =
            "SELECT movpath, movpreviewpath FROM moviefile WHERE movid = ?"
        private const val SELECT_ALL_MOVIE_FILE =
            "SELECT movid, movpath, movpreviewpath FROM moviefile ORDER BY movid"
        private const val SELECT_ALL_MOVIES =
            "SELECT movid, movtitle, movyear, movtime, movlang, movrelcountry FROM movie ORDER BY movid"
        private const val SELECT_MOVIES_BY_TITLE =
            "SELECT movId, movTitle, movYear, movTime, movLang, movRelCountry FROM movie WHERE LOWER(movTitle) LIKE ?"
        private const val SELECT_MOVIE_GENRE_BY_ID =
            "SELECT genre.genid, genre.gentitle FROM moviegenre " +
                    "JOIN genre ON moviegenre.genid = genre.genid " +
                    "WHERE moviegenre.movid = ?"
        private const val SELECT_MOVIE_CAST_BY_ID =
            "SELECT actor.actid, actfirstname, actlastname, role, actgender\n" +
                    "FROM moviecast\n" +
                    "JOIN actor ON moviecast.actid = actor.actid\n" +
                    "WHERE moviecast.movid = ?"
        private const val SELECT_MOVIE_DIRECTORS_BY_ID =
            "SELECT director.dirid, director.dirfirstname, director.dirlastname " +
                    "FROM moviedirection " +
                    "JOIN director ON moviedirection.dirid = director.dirid " +
                    "WHERE moviedirection.movid = ?"
        private const val SELECT_USER_MOVIES =
            "SELECT movie.movid, movie.movtitle, movie.movyear, movie.movtime, movie.movlang, movie.movrelcountry FROM movie INNER JOIN usermovie ON usermovie.movid = movie.movid WHERE usermovie.userid = ?"
        private const val SELECT_MOVIE_REVIEWS_BY_ID =
            "SELECT rv.revid, r.revname, rv.revstars FROM reviewer r JOIN review rv ON r.revid = rv.revid WHERE rv.movid = ?"
        private const val SELECT_MOVIE_RATING_BY_ID =
            "SELECT AVG(revstars) AS rating FROM review WHERE movid = ?"
        private const val INSERT_MOVIE =
            "INSERT INTO movie (movtitle, movyear, movtime, movlang, movrelcountry) VALUES (?, ?, ?, ?, ?)"
        private const val INSERT_MOVIE_FILE =
            "INSERT INTO moviefile (movid, movpath, movpreviewpath) VALUES (?, ?, ?)"
        private const val INSERT_USER_MOVIE =
            "INSERT INTO usermovie (userid, movid) VALUES (?, ?)"
        private const val DELETE_USER_MOVIE =
            "DELETE FROM usermovie WHERE userid = ? AND movid = ?"
        private const val UPDATE_MOVIE =
            "UPDATE movie SET movtitle = ?, movyear = ?, movtime = ?, movlang = ?, movrelcountry = ? WHERE movid = ?"
        private const val UPDATE_MOVIE_FILE =
            "UPDATE moviefile SET movpath = ?, movpreviewpath = ? WHERE movid = ?"
        private const val DELETE_MOVIE_FILE = "DELETE FROM moviefile WHERE movid = ?"
        private const val DELETE_MOVIE = "DELETE FROM movie WHERE movid = ?"
    }

    init {
        try {
            connection.createStatement().use { statement ->
                statement.executeUpdate(CREATE_TABLE_MOVIES)
                statement.executeUpdate(CREATE_TABLE_ACTOR)
                statement.executeUpdate(CREATE_TABLE_DIRECTOR)
                statement.executeUpdate(CREATE_TABLE_GENRE)
                statement.executeUpdate(CREATE_TABLE_MOVIE_CAST)
                statement.executeUpdate(CREATE_TABLE_MOVIE_DIRECTION)
                statement.executeUpdate(CREATE_TABLE_MOVIE_GENRE)
                statement.executeUpdate(CREATE_TABLE_REVIEWER)
                statement.executeUpdate(CREATE_TABLE_REVIEW)
                statement.executeUpdate(CREATE_TABLE_MOVIE_FILE)
                statement.executeUpdate(CREATE_TABLE_USER_MOVIE)
            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    override fun movieById(id: Int): Movie {
        connection.prepareStatement(SELECT_MOVIE_BY_ID).use { statement ->
            statement.setInt(1, id)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                val movTitle = resultSet.getString("movTitle")
                val movYear = resultSet.getInt("movYear")
                val movTime = resultSet.getInt("movTime")
                val movLang = resultSet.getString("movLang")
                val movRelCountry = resultSet.getString("movRelCountry")

                return Movie(
                    id,
                    movTitle,
                    movYear,
                    movTime,
                    movLang,
                    movRelCountry
                )
            } else {
                throw Exception("Record not found")
            }
        }
    }

    override fun searchMovieByTitle(title: String): List<Movie> {
        connection.prepareStatement(SELECT_MOVIES_BY_TITLE).use { statement ->
            statement.setString(1, title)
            val resultSet = statement.executeQuery()
            val movieList = mutableListOf<Movie>()

            while (resultSet.next()) {
                val movieId = resultSet.getInt("movId")
                val movTitle = resultSet.getString("movTitle")
                val movYear = resultSet.getInt("movYear")
                val movTime = resultSet.getInt("movTime")
                val movLang = resultSet.getString("movLang")
                val movRelCountry = resultSet.getString("movRelCountry")

                movieList.add(
                    Movie(
                        movieId,
                        movTitle,
                        movYear,
                        movTime,
                        movLang,
                        movRelCountry
                    )
                )
            }

            return movieList
        }
    }

    override fun movieFullInfo(id: Int): DetailedMovieInfo {
        try {
            val movie = movieById(id)
            val movieFile = movieMedia(id)
            val movieGenre = movieGenre(id)
            val movieCast = movieCast(id)
            val movieDirectors = movieDirectors(id)
            val movieReviews = movieReviews(id)
            val movieRating: Int = movieRating(id)

            return DetailedMovieInfo(
                movie,
                movieFile,
                movieGenre,
                movieCast,
                movieDirectors,
                movieReviews,
                movieRating
            )
        } catch (e: Exception) {
            println(e.message)
            throw Exception("Record not found: movieFullInfo")
        }
    }

    override fun insertUserMovie(userId: Int, movieId: Int) {
        connection.prepareStatement(INSERT_USER_MOVIE).use { statement ->
            statement.setInt(1, userId)
            statement.setInt(2, movieId)
            statement.executeUpdate()
        }
    }

    override fun deleteUserMovie(userId: Int, movieId: Int) {
        connection.prepareStatement(DELETE_USER_MOVIE).use { statement ->
            statement.setInt(1, userId)
            statement.setInt(2, movieId)
            statement.executeUpdate()
        }
    }

    override fun movieByUser(id: Int): List<Movie> {
        connection.prepareStatement(SELECT_USER_MOVIES).use { statement ->
            statement.setInt(1, id)
            val userMoviesResultSet = statement.executeQuery()
            val movieList = mutableListOf<Movie>()

            while (userMoviesResultSet.next()) {
                val movieId = userMoviesResultSet.getInt("movId")
                val movTitle = userMoviesResultSet.getString("movTitle")
                val movYear = userMoviesResultSet.getInt("movYear")
                val movTime = userMoviesResultSet.getInt("movTime")
                val movLang = userMoviesResultSet.getString("movLang")
                val movRelCountry = userMoviesResultSet.getString("movRelCountry")

                movieList.add(
                    Movie(
                        movieId,
                        movTitle,
                        movYear,
                        movTime,
                        movLang,
                        movRelCountry
                    )
                )
            }

            return movieList
        }
    }

    override fun movieRating(id: Int): Int {
        connection.prepareStatement(SELECT_MOVIE_RATING_BY_ID).use { statement ->
            statement.setInt(1, id)
            val movieRatingResultSet = statement.executeQuery()

            return if (movieRatingResultSet.next()) {
                movieRatingResultSet.getInt("rating")
            } else {
                0
            }
        }
    }

    override fun movieReviews(id: Int): List<Review> {
        connection.prepareStatement(SELECT_MOVIE_REVIEWS_BY_ID).use { statement ->
            statement.setInt(1, id)
            val movieReviewsResultSet = statement.executeQuery()
            val movieReviews: MutableList<Review> = mutableListOf()

            while (movieReviewsResultSet.next()) {
                val reviewId = movieReviewsResultSet.getInt("revId")
                val reviewerName = movieReviewsResultSet.getString("revName")
                val reviewStars = movieReviewsResultSet.getInt("revStars")
                val review = Review(reviewId, reviewerName, reviewStars)
                movieReviews.add(review)
            }

            return movieReviews
        }
    }

    override fun movieDirectors(id: Int): List<Director> {
        connection.prepareStatement(SELECT_MOVIE_DIRECTORS_BY_ID).use { statement ->

            statement.setInt(1, id)
            val movieDirectorsResultSet = statement.executeQuery()
            val movieDirectors: MutableList<Director> = mutableListOf()

            while (movieDirectorsResultSet.next()) {
                val directorId = movieDirectorsResultSet.getInt("dirId")
                val firstName = movieDirectorsResultSet.getString("dirFirstName")
                val lastName = movieDirectorsResultSet.getString("dirLastName")
                val director = Director(directorId, firstName, lastName)
                movieDirectors.add(director)
            }

            return movieDirectors
        }
    }

    override fun movieCast(id: Int): List<Actor> {
        connection.prepareStatement(SELECT_MOVIE_CAST_BY_ID).use { statement ->

            statement.setInt(1, id)
            val movieCastResultSet = statement.executeQuery()
            var movieCast = mutableListOf<Actor>()

            if (movieCastResultSet.next()) {
                val cast = mutableListOf<Actor>()
                while (movieCastResultSet.next()) {
                    val actorId = movieCastResultSet.getInt("actId")
                    val firstName = movieCastResultSet.getString("actFirstName")
                    val lastName = movieCastResultSet.getString("actLastName")
                    val role = movieCastResultSet.getString("role")
                    val gender = movieCastResultSet.getString("actGender")
                    val actor = Actor(actorId, firstName, lastName, role, gender)
                    cast.add(actor)
                }
                movieCast = cast
            }

            return movieCast
        }
    }

    override fun movieGenre(id: Int): List<Genre> {
        connection.prepareStatement(SELECT_MOVIE_GENRE_BY_ID).use { statement ->

            statement.setInt(1, id)
            val movieGenreResultSet = statement.executeQuery()
            val movieGenres = mutableListOf<Genre>()

            while (movieGenreResultSet.next()) {
                val genreId = movieGenreResultSet.getInt("genId")
                val genreTitle = movieGenreResultSet.getString("genTitle")
                val genre = Genre(genreId, genreTitle)
                movieGenres.add(genre)
            }

            return movieGenres
        }
    }

    override fun create(movie: Movie, movieMedia: MovieMedia): Int {
        connection.prepareStatement(INSERT_MOVIE, Statement.RETURN_GENERATED_KEYS).use { statement ->
            statement.setString(1, movie.title)
            statement.setInt(2, movie.releaseYear)
            statement.setInt(3, movie.runtimeMinutes)
            statement.setString(4, movie.language)
            statement.setString(5, movie.releaseCountry)
            statement.executeUpdate()

            val movieFileStatement = connection.prepareStatement(INSERT_MOVIE_FILE)
            movieFileStatement.setInt(1, movieMedia.id)
            movieFileStatement.setString(2, movieMedia.filePath)
            movieFileStatement.setString(3, movieMedia.previewFilePath)
            movieFileStatement.executeUpdate()
            movieFileStatement.close()

            val generatedKeys = statement.generatedKeys
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1)
            } else {
                throw Exception("Unable to retrieve the id of the newly inserted movie")
            }
        }
    }

    override fun deleteMovieMedia(id: Int): Int {
        connection.prepareStatement(DELETE_MOVIE_FILE).use { statement ->
            statement.setInt(1, id)
            return statement.executeUpdate()
        }
    }

    override fun movieMedia(id: Int): MovieMedia {
        connection.prepareStatement(SELECT_MOVIE_FILE_BY_ID).use { statement ->
            statement.setInt(1, id)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val filePath = resultSet.getString("movPath")
                val previewFilePath = resultSet.getString("movPreviewPath")

                return MovieMedia(
                    id,
                    filePath,
                    previewFilePath
                )
            } else {
                throw Exception("Record not found")
            }
        }
    }

    override fun allMovies(): List<Movie> {
        connection.prepareStatement(SELECT_ALL_MOVIES).use { statement ->
            val resultSet = statement.executeQuery()
            val movieList = mutableListOf<Movie>()

            while (resultSet.next()) {
                val movieId = resultSet.getInt("movId")
                val movTitle = resultSet.getString("movTitle")
                val movYear = resultSet.getInt("movYear")
                val movTime = resultSet.getInt("movTime")
                val movLang = resultSet.getString("movLang")
                val movRelCountry = resultSet.getString("movRelCountry")

                movieList.add(
                    Movie(
                        movieId,
                        movTitle,
                        movYear,
                        movTime,
                        movLang,
                        movRelCountry
                    )
                )
            }

            return movieList
        }
    }

    override fun allMoviesFiles(): List<MovieMedia> {
        connection.prepareStatement(SELECT_ALL_MOVIE_FILE).use { statement ->

            val resultSet = statement.executeQuery()
            val movieList = mutableListOf<MovieMedia>()

            while (resultSet.next()) {
                val movieId = resultSet.getInt("movId")
                val movPath = resultSet.getString("movPath")
                val movPreviewPath = resultSet.getString("movPreviewPath")

                movieList.add(
                    MovieMedia(
                        movieId,
                        movPath,
                        movPreviewPath
                    )
                )
            }

            return movieList
        }
    }

    override fun updateMovie(id: Int, movie: Movie, movieMedia: MovieMedia) {
        connection.prepareStatement(UPDATE_MOVIE).use { statement ->

            statement.setString(1, movie.title)
            statement.setInt(2, movie.releaseYear)
            statement.setInt(3, movie.runtimeMinutes)
            statement.setString(4, movie.language)
            statement.setString(5, movie.releaseCountry)
            statement.setInt(6, id)
            statement.executeUpdate()

            val statementMovieFile = connection.prepareStatement(UPDATE_MOVIE_FILE)
            statementMovieFile.setString(1, movieMedia.filePath)
            statementMovieFile.setString(2, movieMedia.previewFilePath)
            statementMovieFile.setInt(3, id)
            statementMovieFile.executeUpdate()
        }
    }

    override fun deleteMovie(id: Int) {
        connection.prepareStatement(DELETE_MOVIE).use { statement ->
            statement.setInt(1, id)
            statement.executeUpdate()
        }
    }
}