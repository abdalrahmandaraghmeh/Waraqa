package com.waraqa.backend.service

import com.waraqa.backend.dto.BookDetailDto
import com.waraqa.backend.dto.BookResponseDto
import com.waraqa.backend.dto.CreateBookRequest
import com.waraqa.backend.dto.ListingResponseDto
import com.waraqa.backend.dto.SellerDto
import com.waraqa.backend.dto.UpdateBookRequest
import com.waraqa.backend.dto.toDto
import com.waraqa.backend.exception.ForbiddenException
import com.waraqa.backend.exception.NotFoundException
import com.waraqa.backend.model.Book
import com.waraqa.backend.repository.AcademicRepository
import com.waraqa.backend.repository.BookRepository
import com.waraqa.backend.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val academicRepository: AcademicRepository
) {

    fun getBooks(
        search: String?,
        category: String?,
        universityId: Long?,
        facultyId: Long?,
        majorId: Long?,
        subType: String?,
        sort: String?,
        page: Int,
        limit: Int
    ): List<BookResponseDto> {
        return bookRepository.findBooks(
            search = search,
            category = category,
            universityId = universityId,
            facultyId = facultyId,
            majorId = majorId,
            subType = subType,
            sort = sort,
            page = page,
            limit = limit
        ).map { it.toDto() }
    }

    fun getAllBooks(): List<BookResponseDto> {
        return bookRepository.findAll().map { it.toDto() }
    }

    fun getMyBooks(userEmail: String): List<BookResponseDto> {
        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { NotFoundException("User not found") }

        val userId = user.userId ?: throw RuntimeException("User ID is missing")

        return bookRepository.findAllByUserId(userId).map { it.toDto() }
    }

    fun createBook(request: CreateBookRequest): Book {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw ForbiddenException("Unauthenticated user")
        val userEmail = authentication.name

        val user = userRepository.findByEmail(userEmail)
            .orElseThrow { NotFoundException("User not found") }

        val userId = user.userId
            ?: throw RuntimeException("User ID is missing")

        val book = Book(
            title = request.title,
            price = request.price,
            coverImage = request.imagesUrl.firstOrNull(),
            publisherId = userId,
            universityId = request.universityId,
            facultyId = request.facultyId,
            majorId = request.majorId,
            category = request.category ?: "academic",
            type = request.type ?: "Used"
        )
        return bookRepository.save(book)
    }

    fun getBookById(bookId: Long): BookDetailDto {
        bookRepository.incrementViewsCount(bookId)

        val book = bookRepository.findById(bookId)
            .orElseThrow { NotFoundException("Book not found") }

        val seller = userRepository.findById(book.publisherId)
            .orElseThrow {NotFoundException("Seller not found")}

        val isActiveToday = seller.lastSeen?.isAfter(LocalDateTime.now().minusHours(24)) ?: false

        val universityName = book.universityId?.let { academicRepository.findUniversityById(it)?.name }
        val facultyName = book.facultyId?.let { academicRepository.findFacultyById(it)?.name }
        val majorName = book.majorId?.let { academicRepository.findMajorById(it)?.name }

        val sellerDto = SellerDto(
            userId = seller.userId ?: book.publisherId,
            name = seller.name,
            avatarUrl = seller.avatarUrl,
            rating = seller.rating,
            totalSales = seller.totalSales,
            isActiveToday = isActiveToday
        )

        return BookDetailDto(
            id = book.id ?: bookId,
            title = book.title,
            author = book.author,
            description = book.description,
            price = book.price,
            listingType = book.listingType,
            exchangeFor = book.exchangeFor,
            condition = book.condition,
            category = book.category,
            type = book.type,
            subType = book.subType,
            edition = book.edition,
            coverImage = book.coverImage,
            imagesUrl = book.imagesUrl,
            viewsCount = book.viewsCount,
            savesCount = book.savesCount,
            publishedAt = book.publishedAt,
            universityName = universityName,
            facultyName = facultyName,
            majorName = majorName,
            seller = sellerDto
        )
    }

    fun updateBook(bookId: Long, request: UpdateBookRequest, currentUserId: Long): ListingResponseDto {
        val book = bookRepository.findById(bookId)
            .orElseThrow { NotFoundException("Book not found") }

        if (book.publisherId != currentUserId) {
            throw ForbiddenException("Not authorized to edit this book.")
        }

        val updated = book.copy(
            title = request.title ?: book.title,
            price = request.price ?: book.price,
            category = request.category ?: book.category,
            universityId = request.universityId ?: book.universityId,
            facultyId = request.facultyId ?: book.facultyId,
            majorId = request.majorId ?: book.majorId
        )

        bookRepository.save(updated)

        return ListingResponseDto(
            id = updated.id ?: bookId,
            title = updated.title,
            description = request.description ?: "",
            price = updated.price,
            category = request.category ?: book.category,
            publisherId = updated.publisherId,
            universityId = updated.universityId ?: 1L,
            facultyId = updated.facultyId ?: 1L,
            majorId = updated.majorId ?: 1L,
            createdAt = updated.publishedAt
        )
    }
}