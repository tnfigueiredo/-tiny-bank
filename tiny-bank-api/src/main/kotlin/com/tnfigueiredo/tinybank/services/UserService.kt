package com.tnfigueiredo.tinybank.services

import com.tnfigueiredo.tinybank.exceptions.BusinessRuleValidationException
import com.tnfigueiredo.tinybank.model.User
import com.tnfigueiredo.tinybank.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    fun createOrUpdateUser(user: User): Result<User>  = kotlin.runCatching {
        if(user.name.isBlank() || user.surname.isBlank() || user.document.isBlank() || user.docCountry.isBlank()){
            throw BusinessRuleValidationException("User mandatory fields are empty: $user")
        }
        val result = userRepository.saveOrUpdate(user)
        result.onFailure { throw it }
        result.getOrNull()!!
    }


}