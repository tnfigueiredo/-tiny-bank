package com.tnfigueiredo.tinybank.services

import com.tnfigueiredo.tinybank.exceptions.BusinessRuleValidationException
import com.tnfigueiredo.tinybank.model.User
import com.tnfigueiredo.tinybank.repositories.UserRepository

interface UserService{

    fun createOrUpdateUser(user: User): Result<User>

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


}