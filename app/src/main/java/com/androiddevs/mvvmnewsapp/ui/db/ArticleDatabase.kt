package com.androiddevs.mvvmnewsapp.ui.db

import android.content.Context
import androidx.room.*
import com.androiddevs.mvvmnewsapp.ui.model.Article
import java.util.concurrent.locks.Lock


@Database(
    entities = [Article::class],
    version = 1
)

@TypeConverters(Converts::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object {
        @Volatile
        private var instance: ArticleDatabase? = null
        private val Lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(Lock) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()

    }
}