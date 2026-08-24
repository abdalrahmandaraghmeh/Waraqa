package com.waraqa.backend

import tools.jackson.databind.ObjectMapper
import com.waraqa.backend.dto.LoginRequest
import com.waraqa.backend.dto.RegisterRequest
import com.waraqa.backend.dto.CreateBookRequest
import com.waraqa.backend.model.Book
import com.waraqa.backend.model.User
import com.waraqa.backend.repository.BookRepository
import com.waraqa.backend.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@SpringBootTest
@Transactional
class BookControllerTest @Autowired constructor(
    private val wac: WebApplicationContext,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper
) {

    private lateinit var mockMvc: MockMvc
    private lateinit var token: String
    private var testUserId: Long = 0L

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build()

        // Clean user if exists, or register
        val userEmail = "booktester@example.com"
        val existingUser = userRepository.findByEmail(userEmail)
        
        testUserId = if (existingUser.isPresent) {
            existingUser.get().userId!!
        } else {
            val user = User(
                name = "Ahmed",
                email = userEmail,
                password = "SecurePassword123",
                phoneNumber = "+177777777"
            )
            val savedUser = userRepository.save(user)
            savedUser.userId!!
        }

        // Login to get token
        val loginRequest = LoginRequest(
            email = userEmail,
            password = "SecurePassword123"
        )
        val loginResult = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        ).andExpect(status().isOk).andReturn()

        val responseString = loginResult.response.contentAsString
        val responseMap = objectMapper.readValue(responseString, Map::class.java)
        token = responseMap["token"] as String
        assertNotNull(token)

        // Seed 10 books with different rating, category, and academic ids
        for (i in 1..10) {
            val category = if (i % 2 == 0) "academic" else "general"
            val type = if (i % 3 == 0) "novel" else "book"
            val rating = 3.0 + (i * 0.2) // ratings will be 3.2, 3.4, ..., 5.0
            
            val book = Book(
                title = "Calculus Book $i",
                author = "Author $i",
                price = BigDecimal("10.00").add(BigDecimal(i)),
                publishedAt = LocalDateTime.now(),
                coverImage = "https://example.com/cover$i.jpg",
                category = category,
                type = type,
                rating = rating,
                publisherId = testUserId,
                universityId = if (category == "academic") 1L else null,
                facultyId = if (category == "academic") 1L else null,
                majorId = if (category == "academic") (i % 2 + 1).toLong() else null
            )
            bookRepository.save(book)
        }
    }

    @Test
    fun `should return default feed of 8 books sorted by rating descending`() {
        val result = mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
            .andReturn()

        val responseString = result.response.contentAsString
        val list = objectMapper.readValue(responseString, List::class.java)
        
        // Assert limit defaults to 8
        assertEquals(8, list.size)

        // Assert consistent DTO response fields
        val firstBook = list[0] as Map<*, *>
        assertNotNull(firstBook["id"])
        assertNotNull(firstBook["title"])
        assertNotNull(firstBook["cover_image"])
        assertEquals("Ahmed", firstBook["publisher_name"])
        assertNotNull(firstBook["price"])
    }

    @Test
    fun `should support keyword search`() {
        // All our seeded books have "Calculus Book" in the title
        val result = mockMvc.perform(get("/api/books?search=Book 2"))
            .andExpect(status().isOk)
            .andReturn()

        val responseString = result.response.contentAsString
        val list = objectMapper.readValue(responseString, List::class.java)
        
        assertEquals(1, list.size)
        val book = list[0] as Map<*, *>
        assertEquals("Calculus Book 2", book["title"])
    }

    @Test
    fun `should support category filters`() {
        // 5 academic and 5 general books are seeded
        val resultAcademic = mockMvc.perform(get("/api/books?category=academic&limit=10"))
            .andExpect(status().isOk)
            .andReturn()
        
        val listAcademic = objectMapper.readValue(resultAcademic.response.contentAsString, List::class.java)
        assertEquals(5, listAcademic.size)

        val resultGeneral = mockMvc.perform(get("/api/books?category=general&limit=10"))
            .andExpect(status().isOk)
            .andReturn()

        val listGeneral = objectMapper.readValue(resultGeneral.response.contentAsString, List::class.java)
        assertEquals(5, listGeneral.size)
    }

    @Test
    fun `should support academic cascading filters`() {
        // Filters: university_id=1, faculty_id=1, major_id=1
        val result = mockMvc.perform(get("/api/books?category=academic&university_id=1&faculty_id=1&major_id=1&limit=10"))
            .andExpect(status().isOk)
            .andReturn()

        val list = objectMapper.readValue(result.response.contentAsString, List::class.java)
        // Of the academic books (even indices i=2,4,6,8,10), majorId is (i % 2 + 1), so:
        // i=2: majorId = 2 + 1 = 1 (match)
        // i=4: majorId = 4 + 1 = 1 (match)
        // i=6: majorId = 6 + 1 = 1 (match)
        // i=8: majorId = 8 + 1 = 1 (match)
        // i=10: majorId = 10 + 1 = 1 (match)
        // Actually, in Kotlin: i % 2 + 1 for even i is always 1: (2%2)+1 = 1, (4%2)+1 = 1, etc.
        // So all 5 academic books match majorId = 1.
        assertEquals(5, list.size)
    }

    @Test
    fun `should support pagination and View All`() {
        // Load with limit=all should return all 10 books
        val resultAll = mockMvc.perform(get("/api/books?limit=all"))
            .andExpect(status().isOk)
            .andReturn()

        val listAll = objectMapper.readValue(resultAll.response.contentAsString, List::class.java)
        assertEquals(10, listAll.size)

        // Load page 1 with limit 4 (should return next 4 books)
        val resultPage1 = mockMvc.perform(get("/api/books?page=1&limit=4"))
            .andExpect(status().isOk)
            .andReturn()

        val listPage1 = objectMapper.readValue(resultPage1.response.contentAsString, List::class.java)
        assertEquals(4, listPage1.size)
    }

    @Test
    fun `should publish a book when authenticated`() {
        val request = CreateBookRequest(
            title = "New Published Book",
            author = "New Author",
            price = BigDecimal("24.99"),
            publishedAt = LocalDateTime.now(),
            coverImage = "https://example.com/newcover.jpg",
            category = "academic",
            type = "book",
            universityId = 1L,
            facultyId = 1L,
            majorId = 1L
        )

        mockMvc.perform(
            post("/api/books")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isCreated)
    }

    @Test
    fun `should deny publishing when unauthenticated`() {
        val request = CreateBookRequest(
            title = "New Published Book",
            author = "New Author",
            price = BigDecimal("24.99"),
            publishedAt = LocalDateTime.now(),
            category = "general"
        )

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isForbidden)
    }
}
