# Waraq — Complete Codebase Audit & Refactoring Assessment (V2)
# ورق — تدقيق شامل للكود وتقييم إعادة الهيكلة (الإصدار الثاني)

---

## 1. Executive Summary | الملخص التنفيذي

| Dimension / البُعد | Rating / التقييم |
|---|---|
| **Overall Codebase Quality / جودة الكود** | Healthy / صحي وممتاز |
| **Architecture Quality / جودة الهيكلية** | Good / جيدة جداً |
| **Biggest Strengths / أبرز نقاط القوة** | بنية قوية جداً بعد الإصلاحات، لا يوجد N+1 queries، أمان ممتاز للـ Endpoints وحماية ملكية البيانات، استخدام متقدم لـ Spring Data JDBC و Kotlin. |
| **Biggest Problems / أبرز المشاكل** | القائمة السوداء للتوكنات (Token Blacklist) لا تزال في الذاكرة (Memory Leak risk)، وبعض الأكواد غير المستخدمة (Dead Code). |
| **Overall Technical Debt / الدين التقني** | Low / منخفض |
| **Overall Refactoring Necessity / ضرورة إعادة الهيكلة** | Low / منخفضة — يقتصر على التنظيف البسيط |

**Overall Assessment: Healthy**
**التقييم العام: صحي وممتاز**

**EN:** After recent refactoring phases (Phases 1-5), the Waraq backend has significantly improved. Critical issues like N+1 queries, unauthenticated file uploads, and duplicated repository logic have been fully resolved. The architecture is solid and secure. The remaining issues are mostly related to technical debt (in-memory token blacklist, unused classes) and environment configuration (hardcoded DB credentials).

**AR:** بعد مراحل إعادة الهيكلة الأخيرة (1 إلى 5)، تحسنت الواجهة الخلفية لمنصة "ورق" بشكل كبير. تم حل المشاكل الحرجة بالكامل مثل استعلامات N+1، ورفع الملفات بدون مصادقة، والمنطق المكرر. الهيكلية الآن صلبة وآمنة. المشاكل المتبقية تقتصر على الدين التقني (القائمة السوداء للتوكنات بالذاكرة، وأكواد غير مستخدمة) وبعض إعدادات البيئة.

---

## 2. Project Overview | نظرة عامة على المشروع

**EN:** **Waraq (ورق)** is a REST API backend for a book marketplace where the general public and university students in Jordan can buy, sell, and exchange books. 
- **Main Modules:** Authentication (JWT), User Profiles, Listings Management (Active/Sold), Book Catalog, Academic Hierarchy lookup, File Uploads.
- **Main Technologies:** Kotlin 2.3.21, Spring Boot 4.1.0, Spring Data JDBC, Spring Security, PostgreSQL 15, JJWT.

**AR:** **ورق (Waraq)** هو واجهة برمجة تطبيقات (REST API) لسوق كتب حيث يمكن للعامة وطلاب الجامعات في الأردن بيع وشراء وتبادل الكتب.
- **الوحدات الرئيسية:** المصادقة، الملفات الشخصية، إدارة الإعلانات، كتالوج الكتب، البحث الأكاديمي، رفع الملفات.
- **التقنيات الرئيسية:** Kotlin, Spring Boot, Spring Data JDBC, Spring Security, PostgreSQL.

---

## 3. Architecture Overview | نظرة عامة على الهيكلية

**EN:** The project follows a clean 3-layer architecture (Controllers → Services → Repositories).
- **Controllers:** Handle HTTP requests, parsing, and formatting responses.
- **Services:** Contain business logic, validation, and enforce data ownership checks (e.g., ensuring a user only edits their own listings or profile).
- **Repositories:** Handle database persistence. Uses `NamedParameterJdbcTemplate` for complex queries with manual `RowMapper` logic to avoid ORM overhead.
- **Security:** Stateless JWT authentication.

**AR:** المشروع يتبع هيكلية قياسية بـ 3 طبقات.
الخدمات (Services) أصبحت مركزية وممتازة في التحقق من الصلاحيات والملكية. طبقة البيانات (Repositories) تستخدم JDBC مباشرة مما يوفر أداءً عالياً.

---

## 4. Complete File Inventory | جرد الملفات الكامل

*Note: Showing only key files for brevity in this summary.*

| File | Type | Purpose | Issues | Refactoring Needed | Priority |
|------|------|---------|--------|--------------------|----------|
| `application.yaml` | Config | DB connection | DB credentials in VCS | Move to env vars | **P0** |
| `JwtUtils.kt` | Security | JWT logic | Default secret is hardcoded | Use strict env var | P1 |
| `TokenBlacklistService.kt` | Security | Logout tracking | In-memory `ConcurrentHashMap` | Move to DB/Redis | **P1** |
| `BookService.kt` | Service | Book logic | Unused | Delete it | P2 |
| `ListingService.kt` | Service | Listing logic | No issues. Perfectly handles logic | None | - |
| `UserProfileService.kt`| Service | Profile logic | No issues. Robust email/phone validation | None | - |
| `ListingRepository.kt` | Repo | Listing DB | No issues. Unified and performant | None | - |

---

## 5. Main Application Flows | تدفقات التطبيق الرئيسية

### 1. View My Listings (Active/Sold)
`Client` → `UserController.getUserListings(status)` → `ListingService.getOwnerListings()` → `ListingRepository.findOwnerListingsByStatus()`
*Note: Validates JWT, extracts User ID, fetches exact tab.*

### 2. Edit Profile
`Client` → `UserController.updateUserProfile()` → `UserProfileService.updateProfile()` → `UserProfileRepository.updateProfile()`
*Note: Validates uniqueness of Email and Phone across the database before updating.*

---

## 6. Findings by Category | النتائج حسب الفئة

### Architecture & Security
- **Finding:** Token Blacklist uses `ConcurrentHashMap`. This causes memory leaks and loses blacklisted tokens on restart. (Priority: High)
- **Finding:** Hardcoded DB credentials in `application.yaml` (Priority: Critical).

### Business Logic & API
- **Finding:** The API is highly consistent now (`/api/v1/...`). 
- **Finding:** Ownership checks are perfectly placed in the Service layer (`verifyOwnership()`).

### Database
- **Finding:** `schema.sql` properly adds columns using `ALTER TABLE IF NOT EXISTS`, making tests robust. 
- **Finding:** Complex JOINs in `ListingRepository` successfully eradicated the N+1 problem.

---

## 7. What Is Already Good (DO NOT TOUCH) | ما هو جيد ولا يحتاج تغيير

1. **`ListingRepository.kt`**: The custom RowMapper and unified SQL JOINs are extremely well-written and performant. Do not try to replace this with Spring Data JPA.
2. **Security Config (`SecurityConfig.kt`)**: The endpoint protection matrix is perfect.
3. **`ListingService` & `UserProfileService` Ownership Checks**: The logic to ensure users only modify their own data is bulletproof.

---

## 8. Refactoring Candidates | مرشحات إعادة الهيكلة

| Priority | File | Proposed Change | Expected Benefit |
|----------|------|-----------------|------------------|
| **P0** | `application.yaml` | Use `${DB_PASSWORD}` env variables for DB | Prevents credential leaks |
| **P1** | `TokenBlacklistService` | Save blacklisted tokens to Postgres or Redis | Fixes memory leak & persists logout state |
| **P1** | `JwtUtils` | Remove hardcoded fallback secret | Improves production security |
| **P2** | `BookService`, `BookDetailDto` | Delete unused files | Reduces noise and technical debt |

---

## 9. Quick Wins | إصلاحات سريعة

- Delete `BookService.kt`, `CreateBookRequest.kt`, `UpdateBookRequest.kt` (Dead code).
- Update `application.yaml` to use environment variables for `spring.datasource.password`.

---

## 10. Major Refactorings | إعادة هيكلة كبرى

- **Implement Persistent Token Blacklist**: Requires creating a new table `blacklisted_tokens` in `schema.sql`, and updating `TokenBlacklistService` to query the database instead of a memory map.

---

## 11. Technical Debt | الدين التقني

- **Acceptable Debt:** Using `schema.sql` instead of Liquibase/Flyway is acceptable for this early stage.
- **Important Debt:** In-memory Token Blacklist. Must be fixed before scaling.

---

## 12. Dead / Unused Code | أكواد غير مستخدمة

- **CONFIRMED UNUSED:** 
  - `com.waraqa.backend.service.BookService`
  - `com.waraqa.backend.dto.CreateBookRequest`
  - `com.waraqa.backend.dto.UpdateBookRequest`
  - `com.waraqa.backend.dto.BookDetailDto`
  - `com.waraqa.backend.dto.BookResponseDto`

---

## 13. Missing Tests | الاختبارات الناقصة

- **`ListingServiceTest`**: Missing unit tests to verify ownership exceptions (403 Forbidden).
- **`UserProfileServiceTest`**: Missing unit tests for email and phone number uniqueness logic.

---

## 14. Refactoring Roadmap | خارطة طريق إعادة الهيكلة

1. **Step 1:** Delete all confirmed unused code (Quick Win).
2. **Step 2:** Secure configurations (update `application.yaml` to read from environment).
3. **Step 3:** Implement Persistent Token Blacklist (Database-backed) to solve the Logout memory leak.
4. **Step 4:** Add missing unit tests for Services.

---

## 15. Final Verdict | الحكم النهائي

1. **Is the current architecture acceptable?** Yes, highly acceptable and performant.
2. **What are the 5 biggest problems?** In-memory blacklist, DB credentials in config, hardcoded JWT fallback, dead code, missing service-level unit tests.
3. **What must be fixed immediately?** Hardcoded DB credentials.
4. **What should be refactored soon?** The Token Blacklist.
5. **What can wait?** Adding Liquibase/Flyway for DB migrations.
6. **What should NOT be touched?** The custom JDBC Repositories and Security filter chain.
7. **What is the estimated overall refactoring complexity?** Very Low. The heavy lifting was already completed in previous phases.
