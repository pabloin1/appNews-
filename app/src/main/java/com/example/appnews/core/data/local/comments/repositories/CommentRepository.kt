package com.example.appnews.core.data.local.comments.repositories

import com.example.appnews.data.local.comments.dao.CommentDao
import com.example.appnews.data.local.comments.entities.CommentEntity
import kotlinx.coroutines.flow.Flow

class CommentRepository(private val commentDao: CommentDao) {
    fun getCommentsByNewsId(newsId: String): Flow<List<CommentEntity>> {
        return commentDao.getCommentsByNewsId(newsId)
    }

    suspend fun insertComment(comment: CommentEntity) {
        commentDao.insertComment(comment)
    }

    suspend fun insertAllComments(comments: List<CommentEntity>) {
        commentDao.insertAllComments(comments)
    }

    suspend fun deleteCommentById(commentId: String) {
        commentDao.deleteCommentById(commentId)
    }

    suspend fun deleteCommentsByNewsId(newsId: String) {
        commentDao.deleteCommentsByNewsId(newsId)
    }
}