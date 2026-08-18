package com.waraqa.backend.repository
import com.waraqa.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>

    fun existsByEmail(email: String): Boolean

    fun existsByPhoneNumber(phoneNumber: String): Boolean
}