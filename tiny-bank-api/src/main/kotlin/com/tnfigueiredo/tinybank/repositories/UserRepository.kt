package com.tnfigueiredo.tinybank.repositories

import com.tnfigueiredo.tinybank.exceptions.DataDuplicatedException
import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.model.DocType
import com.tnfigueiredo.tinybank.model.User
import java.util.*
import java.util.concurrent.ConcurrentHashMap

interface UserRepository{

    fun saveOrUpdate(user: User): Result<User?>

    fun deleteAll()

}

class UserRepositoryImpl:UserRepository {

    private companion object{
        val userRepo: MutableMap<UUID, User> = ConcurrentHashMap()
    }

    override fun saveOrUpdate(user: User): Result<User?> = kotlin.runCatching {
        when{
            user.id == null -> {
                findUserByDocumentInfo(user.docType, user.document, user.docCountry)?.let { throw DataDuplicatedException("Duplicated user document: $user.") }
                val userToBeSaved = user.copy(id = UUID.randomUUID())
                userRepo[userToBeSaved.id!!] = userToBeSaved
                userToBeSaved
            }
            userRepo[user.id] == null -> throw DataNotFoundException("User Not Found to be updated.")
            else -> {
                userRepo[user.id]?.let { userToBeUpdated ->
                    findUserByDocumentInfo(user.docType, user.document, user.docCountry)
                        ?.let { userWithDocument -> if(userWithDocument != userToBeUpdated) throw DataDuplicatedException("Duplicated user document: $user.")}
                    val userToBeSaved = userToBeUpdated.copy(name = user.name, surname = user.surname, docType = user.docType, docCountry = user.docCountry)
                    userRepo[user.id] = userToBeSaved
                    userToBeSaved
                }
            }
        }
    }

    fun findUserByDocumentInfo(docType: DocType, document: String, docCountry: String): User? =
        userRepo.values.firstOrNull{ user -> user.isUserDocument(docType, document, docCountry) }

    override fun deleteAll():Unit = userRepo.clear()

}