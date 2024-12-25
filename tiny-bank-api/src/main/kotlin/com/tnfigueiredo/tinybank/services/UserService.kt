package com.tnfigueiredo.tinybank.services

import com.tnfigueiredo.tinybank.exceptions.BusinessRuleValidationException
import com.tnfigueiredo.tinybank.model.DocType
import com.tnfigueiredo.tinybank.model.User
import com.tnfigueiredo.tinybank.repositories.UserRepository
import java.util.UUID

interface UserService{

    fun createOrUpdateUser(user: User): Result<User>

    fun findUserByDocument(docType: DocType, document: String, docCountry: String): Result<User?>

    fun deactivateUser(userId: UUID): Result<User>

    fun activateUser(user: User): Result<User>

}

class UserServiceImpl(private val userRepository: UserRepository) : UserService {

    override fun createOrUpdateUser(user: User): Result<User>  = kotlin.runCatching {
        if(user.name.isBlank() || user.surname.isBlank() || user.document.isBlank() || user.docCountry.isBlank()){
            throw BusinessRuleValidationException("User mandatory fields are empty: $user")
        }
        val result = userRepository.saveOrUpdate(user)
        result.onFailure { throw it }
        result.getOrNull()!!
    }

    override fun findUserByDocument(docType: DocType, document: String, docCountry: String): Result<User?> = kotlin.runCatching {
        userRepository.findUserByDocumentInfo(docType, document, docCountry)
    }

    override fun deactivateUser(userId: UUID): Result<User> = kotlin.runCatching {
        userRepository.findUserById(userId)
            .onFailure { throw it }
            .getOrNull()!!.let {userToBeDeactivated ->
                if(userToBeDeactivated.isUserActive().not())
                    throw BusinessRuleValidationException("The user is already deactivated.")
                userRepository.deactivateUser(userId).getOrNull()!!
            }
    }

    override fun activateUser(user: User): Result<User> = kotlin.runCatching {
        userRepository.findUserById(user.id!!)
            .onFailure { throw it }
            .getOrNull()!!.let {userToBeActivated ->
                if(userToBeActivated.isUserActive())
                    throw BusinessRuleValidationException("The user is already active.")
                userRepository.activateUser(userToBeActivated.copy(name = user.name, surname = user.surname)).getOrNull()!!
            }
    }

}