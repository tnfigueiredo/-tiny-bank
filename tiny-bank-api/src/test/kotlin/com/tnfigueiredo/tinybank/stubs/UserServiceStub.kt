package com.tnfigueiredo.tinybank.stubs

import com.tnfigueiredo.tinybank.exceptions.DataNotFoundException
import com.tnfigueiredo.tinybank.model.DocType.PASSPORT
import com.tnfigueiredo.tinybank.model.User
import com.tnfigueiredo.tinybank.services.UserService
import java.util.*

class UserServiceStub: UserService {

    private companion object{
        val A_RANDOM_ID: UUID = UUID.fromString("eae467d9-deb2-49b3-aaf5-f1e146e567e1")
    }

    override fun createOrUpdateUser(user: User): Result<User> =
        when{
            user.id == null -> Result.success(User(A_RANDOM_ID, "A_NAME", "A_SURNAME", PASSPORT, "A_PASSPORT", "A_COUNTRY"))
            else -> Result.failure(DataNotFoundException("Data not found"))
        }


}