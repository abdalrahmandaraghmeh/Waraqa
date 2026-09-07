package com.waraqa.backend

import tools.jackson.databind.ObjectMapper
import com.waraqa.backend.dto.LoginRequest
import com.waraqa.backend.dto.RegisterRequest
import com.waraqa.backend.repository.UserRepository
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

import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder

@SpringBootTest
@Transactional
class SecurityTest @Autowired constructor(
    private val wac: WebApplicationContext,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper
) {

    private lateinit var mockMvc: MockMvc
    private lateinit var token: String

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()

        val registerRequest = RegisterRequest(
            name = "Security Test User",
            email = "testsecurity@example.com",
            password = "SecurePassword123",
            phoneNumber = "+199999999"
        )
        
        // Register user via API
        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )

        // Login to get token
        val loginRequest = LoginRequest(
            email = "testsecurity@example.com",
            password = "SecurePassword123"
        )
        val loginResult = mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        ).andExpect(status().isOk).andReturn()

        val responseString = loginResult.response.contentAsString
        val responseMap = objectMapper.readValue(responseString, Map::class.java)
        token = responseMap["token"] as String
        assertNotNull(token)
    }

    @Test
    fun `should access public endpoints without authentication`() {
        // GET /listings should return 200
        mockMvc.perform(get("/api/v1/listings"))
            .andExpect(status().isOk)

        // GET /api/universities should return 200
        mockMvc.perform(get("/api/v1/universities"))
            .andExpect(status().isOk)

        // GET /api/faculties should return 200
        mockMvc.perform(get("/api/v1/faculties"))
            .andExpect(status().isOk)

        // GET /api/majors should return 200
        mockMvc.perform(get("/api/v1/majors"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should protect profile endpoint and deny access after logout`() {
        // 1. Access profile without token -> should be Forbidden (403)
        mockMvc.perform(get("/api/v1/users/profile"))
            .andExpect(status().isForbidden)

        // 2. Access profile with valid token -> should be OK (200)
        mockMvc.perform(
            get("/api/v1/users/profile")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)

        // 3. Perform logout
        mockMvc.perform(
            post("/api/v1/auth/logout")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)

        // 4. Access profile again with now blacklisted token -> should be Forbidden (403)
        mockMvc.perform(
            get("/api/v1/users/profile")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `should protect upload endpoint from unauthenticated users`() {
        mockMvc.perform(post("/api/v1/upload"))
            .andExpect(status().isForbidden)
    }
}
