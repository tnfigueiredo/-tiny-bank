package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.model.User
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class UserRepository {

    private companion object{
        val userRepo: MutableMap<UUID, User> = ConcurrentHashMap()
    }

    fun saveOrUpdate(user: User): Result<User?> = kotlin.runCatching {
        when{
            user.id == null -> {
                val userToBeSaved = user.copy(id = UUID.randomUUID())
                userRepo[userToBeSaved.id!!] = userToBeSaved
                userToBeSaved
            }
            userRepo[user.id] == null -> throw DataNotFoundException("User Not Found to be updated.")
            else -> {
                userRepo[user.id]?.let { userToBeUpdated ->
                    val userToBeSaved = userToBeUpdated.copy(name = user.name, surname = user.surname, docType = user.docType, docCountry = user.docCountry)
                    userRepo[user.id] = userToBeSaved
                    userToBeSaved
                }
            }
        }
    }

}