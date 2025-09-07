package com.xichen.matelink.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xichen.matelink.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * 用户DAO
 */
@Dao
interface UserDao {

@Insert(onConflict = OnConflictStrategy.REPLACE)
fun insertUser(user: UserEntity)

@Insert(onConflict = OnConflictStrategy.REPLACE)
fun insertUsers(users: List<UserEntity>)

@Update
fun updateUser(user: UserEntity)

@Query("DELETE FROM users WHERE id = :userId")
fun deleteUser(userId: String): Int

@Query("DELETE FROM users")
fun deleteAllUsers(): Int

@Query("SELECT * FROM users WHERE id = :userId")
fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    fun getUsersCount(): Int
}
