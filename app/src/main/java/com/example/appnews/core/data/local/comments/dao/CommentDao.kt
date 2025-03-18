package com.example.appnews.core.data.local.comments.dao

import androidx.room.*
import com.example.appnews.core.data.local.comments.entities.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComments(comments: List<CommentEntity>)

    @Query("SELECT * FROM comments WHERE news_id = :newsId")
    fun getCommentsByNewsId(newsId: String): Flow<List<CommentEntity>>

    @Query("DELETE FROM comments WHERE news_id = :newsId")
    suspend fun deleteCommentsByNewsId(newsId: String)

    @Query("DELETE FROM comments WHERE _id = :commentId")
    suspend fun deleteCommentById(commentId: String)
}