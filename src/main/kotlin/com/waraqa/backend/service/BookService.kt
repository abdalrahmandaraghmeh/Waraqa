package com.waraqa.backend.service

import com.waraqa.backend.dto.BookResponseDto
import com.waraqa.backend.exception.NotFoundException
import com.waraqa.backend.model.Book
import com.waraqa.backend.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository
) {

    // 1. جلب كل الكتب (لتصفح الكتالوج العام)
    fun getAllBooks(): List<Book> {
        return bookRepository.findAll()
    }

    // 2. البحث عن الكتب (لتشغيل شريط البحث والفلاتر)
    // ملاحظة: إذا كان البحث سيتضمن فلاتر السعر والجامعة، فالأفضل استخدام findListings
    // الموجودة في ListingRepository لأنها تدمج الجدولين. أما هذه الدالة فهي للبحث في أسماء الكتب فقط.
    fun searchBooksInCatalog(title: String, author: String): Book? {
        return bookRepository.findByTitleAndAuthor(title, author).orElse(null)
    }

    // 3. جلب تفاصيل كتاب معين من الكتالوج
    fun getBookById(bookId: Long): Book {
        return bookRepository.findById(bookId)
            .orElseThrow { NotFoundException("الكتاب غير موجود في الكتالوج") }
    }


}