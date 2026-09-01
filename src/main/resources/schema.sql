-- =========================================================================
-- 1. TABLE DEFINITIONS
-- =========================================================================

CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    rating DOUBLE PRECISION DEFAULT 0.0,
    total_sales INT DEFAULT 0,
    last_seen TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS universities (
                                            id BIGSERIAL PRIMARY KEY,
                                            name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS faculties (
                                         id BIGSERIAL PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
    university_id BIGINT NOT NULL REFERENCES universities(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS majors (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL,
    faculty_id BIGINT NOT NULL REFERENCES faculties(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS books (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    description TEXT DEFAULT '',
    price DECIMAL(10, 2),
    listing_type VARCHAR(50) DEFAULT 'for_sale',
    exchange_for TEXT,
    condition VARCHAR(50) DEFAULT 'good',
    category VARCHAR(50) NOT NULL,
    type VARCHAR(50),
    sub_type VARCHAR(50),
    edition VARCHAR(100),
    cover_image VARCHAR(500),
    images_url TEXT[],
    views_count INT DEFAULT 0,
    saves_count INT DEFAULT 0,
    publisher_id BIGINT NOT NULL,
    university_id BIGINT,
    faculty_id BIGINT,
    major_id BIGINT,
    published_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_books_university FOREIGN KEY (university_id) REFERENCES universities(id) ON DELETE SET NULL,
    CONSTRAINT fk_books_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
    CONSTRAINT fk_books_major FOREIGN KEY (major_id) REFERENCES majors(id) ON DELETE SET NULL
    );

-- =========================================================================
-- 2. UNIVERSITIES SEED DATA (All 24 Universities)
-- =========================================================================

INSERT INTO universities (id, name) VALUES
                                        (1, 'University of Jordan (UJ) - الجامعة الأردنية'),
                                        (2, 'Jordan University of Science and Technology (JUST) - جامعة العلوم والتكنولوجيا'),
                                        (3, 'Yarmouk University (YU) - جامعة اليرموك'),
                                        (4, 'Hashemite University (HU) - الجامعة الهاشمية'),
                                        (5, 'Al-Balqa Applied University (BAU) - جامعة البلقاء التطبيقية'),
                                        (6, 'Princess Sumaya University for Technology (PSUT) - جامعة الأميرة سمية للتكنولوجيا'),
                                        (7, 'German Jordanian University (GJU) - الجامعة الألمانية الأردنية'),
                                        (8, 'Mutah University - جامعة مؤتة'),
                                        (9, 'Al al-Bayt University (AABU) - جامعة آل البيت'),
                                        (10, 'Tafila Technical University (TTU) - جامعة الطفيلة التقنية'),
                                        (11, 'Al-Hussein Bin Talal University (AHU) - جامعة الحسين بن طلال'),
                                        (12, 'Applied Science Private University (ASU) - جامعة العلوم التطبيقية الخاصة'),
                                        (13, 'Al-Ahliyya Amman University (AAU) - جامعة عمان الأهلية'),
                                        (14, 'University of Petra (UOP) - جامعة البترا'),
                                        (15, 'Al-Zaytoonah University of Jordan - جامعة الزيتونة الأردنية'),
                                        (16, 'Philadelphia University - جامعة فيلادلفيا'),
                                        (17, 'Jerash University - جامعة جرش'),
                                        (18, 'Zarqa University (ZU) - جامعة الزرقاء'),
                                        (19, 'Middle East University (MEU) - جامعة الشرق الأوسط'),
                                        (20, 'Isra University - جامعة الإسراء'),
                                        (21, 'Jadara University - جامعة جدارا'),
                                        (22, 'Amman Arab University (AAU) - جامعة عمان العربية'),
                                        (23, 'American University of Madaba (AUM) - الجامعة الأمريكية في مادبا'),
                                        (24, 'World Islamic Sciences and Education University (WISE) - جامعة العلوم الإسلامية العالمية')
    ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

-- =========================================================================
-- 3. FACULTIES SEED DATA
-- =========================================================================

INSERT INTO faculties (id, university_id, name) VALUES
-- 1. UJ
(101, 1, 'King Abdullah II School of Information Technology'),
(102, 1, 'Faculty of Engineering & Technology'),
(103, 1, 'School of Medicine'),
(104, 1, 'School of Pharmacy'),
(105, 1, 'School of Business'),
(106, 1, 'School of Science'),
(107, 1, 'School of Law'),
(108, 1, 'School of Foreign Languages'),
(109, 1, 'School of Arts'),
(110, 1, 'School of Nursing'),

-- 2. JUST
(201, 2, 'Faculty of Computer & Information Technology'),
(202, 2, 'Faculty of Engineering'),
(203, 2, 'Faculty of Medicine'),
(204, 2, 'Faculty of Pharmacy'),
(205, 2, 'Faculty of Dentistry'),
(206, 2, 'Faculty of Applied Medical Sciences'),
(207, 2, 'Faculty of Science & Arts'),
(208, 2, 'Faculty of Nursing'),

-- 3. Yarmouk University
(301, 3, 'Faculty of Information Technology & Computer Sciences'),
(302, 3, 'Hijjawi Faculty for Engineering Technology'),
(303, 3, 'Faculty of Business'),
(304, 3, 'Faculty of Medicine'),
(305, 3, 'Faculty of Pharmacy'),
(306, 3, 'Faculty of Science'),
(307, 3, 'Faculty of Arts'),
(308, 3, 'Faculty of Law'),
(309, 3, 'Faculty of Mass Communication'),

-- 4. Hashemite University
(401, 4, 'Faculty of Prince Hussein Bin Abdullah II for IT'),
(402, 4, 'Faculty of Engineering'),
(403, 4, 'Faculty of Business Administration'),
(404, 4, 'Faculty of Medicine'),
(405, 4, 'Faculty of Science'),
(406, 4, 'Faculty of Allied Health Sciences'),
(407, 4, 'Faculty of Educational Sciences'),

-- 5. Al-Balqa Applied University
(501, 5, 'Faculty of Artificial Intelligence & Information Technology'),
(502, 5, 'Faculty of Engineering'),
(503, 5, 'Faculty of Business'),
(504, 5, 'Faculty of Medicine'),
(505, 5, 'Faculty of Science'),
(506, 5, 'Faculty of Agriculture'),

-- 6. PSUT
(601, 6, 'King Hussein School of Computing Sciences'),
(602, 6, 'King Abdullah II School of Engineering'),
(603, 6, 'King Talal School of Business Technology'),

-- 7. German Jordanian University
(701, 7, 'School of Electrical Engineering & Information Technology'),
(702, 7, 'School of Applied Technical Sciences'),
(703, 7, 'School of Management & Logistic Sciences'),
(704, 7, 'School of Architecture & Built Environment'),

-- 8. Mutah University
(801, 8, 'Faculty of Information Technology'),
(802, 8, 'Faculty of Engineering'),
(803, 8, 'Faculty of Medicine'),
(804, 8, 'Faculty of Pharmacy'),
(805, 8, 'Faculty of Business'),
(806, 8, 'Faculty of Science'),
(807, 8, 'Faculty of Law'),

-- 9. Al al-Bayt University
(901, 9, 'Faculty of Information Technology'),
(902, 9, 'Faculty of Engineering'),
(903, 9, 'Faculty of Business'),
(904, 9, 'Faculty of Science'),
(905, 9, 'Faculty of Arts & Humanities'),
(906, 9, 'Faculty of Law'),

-- 10. Tafila Technical University
(1001, 10, 'Faculty of Information & Communication Technology'),
(1002, 10, 'Faculty of Engineering'),
(1003, 10, 'Faculty of Business'),
(1004, 10, 'Faculty of Science'),

-- 11. Al-Hussein Bin Talal University
(1101, 11, 'Faculty of Information Technology'),
(1102, 11, 'Faculty of Engineering'),
(1103, 11, 'Faculty of Business Administration & Economics'),
(1104, 11, 'Faculty of Science'),
(1105, 11, 'Faculty of Nursing'),

-- 12. Applied Science Private University (ASU)
(1201, 12, 'Faculty of Information Technology'),
(1202, 12, 'Faculty of Engineering & Technology'),
(1203, 12, 'Faculty of Pharmacy'),
(1204, 12, 'Faculty of Business'),
(1205, 12, 'Faculty of Art & Design'),
(1206, 12, 'Faculty of Law'),
(1207, 12, 'Faculty of Nursing'),

-- 13. Al-Ahliyya Amman University (AAU)
(1301, 13, 'Faculty of Information Technology'),
(1302, 13, 'Faculty of Engineering'),
(1303, 13, 'Faculty of Pharmacy'),
(1304, 13, 'Faculty of Business'),
(1305, 13, 'Faculty of Architecture & Design'),
(1306, 13, 'Faculty of Allied Medical Sciences'),
(1307, 13, 'Faculty of Law'),

-- 14. University of Petra (UOP)
(1401, 14, 'Faculty of Information Technology'),
(1402, 14, 'Faculty of Pharmacy & Medical Sciences'),
(1403, 14, 'Faculty of Architecture & Design'),
(1404, 14, 'Faculty of Administrative & Financial Sciences'),
(1405, 14, 'Faculty of Arts & Sciences'),
(1406, 14, 'Faculty of Mass Communication'),
(1407, 14, 'Faculty of Law'),

-- 15. Al-Zaytoonah University
(1501, 15, 'Faculty of Science & Information Technology'),
(1502, 15, 'Faculty of Engineering & Technology'),
(1503, 15, 'Faculty of Pharmacy'),
(1504, 15, 'Faculty of Business'),
(1505, 15, 'Faculty of Nursing'),
(1506, 15, 'Faculty of Arts'),
(1507, 15, 'Faculty of Law'),

-- 16. Philadelphia University
(1601, 16, 'Faculty of Information Technology'),
(1602, 16, 'Faculty of Engineering & Technology'),
(1603, 16, 'Faculty of Pharmacy'),
(1604, 16, 'Faculty of Business'),
(1605, 16, 'Faculty of Allied Medical Sciences'),
(1606, 16, 'Faculty of Arts & Educational Sciences'),
(1607, 16, 'Faculty of Law'),

-- 17. Jerash University
(1701, 17, 'Faculty of Computer Science & Information Technology'),
(1702, 17, 'Faculty of Engineering'),
(1703, 17, 'Faculty of Pharmacy'),
(1704, 17, 'Faculty of Business'),
(1705, 17, 'Faculty of Agriculture'),
(1706, 17, 'Faculty of Arts'),
(1707, 17, 'Faculty of Law'),
(1708, 17, 'Faculty of Educational Sciences'),

-- 18. Zarqa University
(1801, 18, 'Faculty of Information Technology'),
(1802, 18, 'Faculty of Engineering Technology'),
(1803, 18, 'Faculty of Pharmacy'),
(1804, 18, 'Faculty of Economics & Administrative Sciences'),
(1805, 18, 'Faculty of Allied Medical Sciences'),
(1806, 18, 'Faculty of Arts'),
(1807, 18, 'Faculty of Law'),
(1808, 18, 'Faculty of Nursing'),

-- 19. Middle East University (MEU)
(1901, 19, 'Faculty of Information Technology'),
(1902, 19, 'Faculty of Engineering'),
(1903, 19, 'Faculty of Business'),
(1904, 19, 'Faculty of Pharmacy'),
(1905, 19, 'Faculty of Media'),
(1906, 19, 'Faculty of Architecture & Design'),
(1907, 19, 'Faculty of Arts & Educational Sciences'),
(1908, 19, 'Faculty of Law'),

-- 20. Isra University
(2001, 20, 'Faculty of Information Technology'),
(2002, 20, 'Faculty of Engineering'),
(2003, 20, 'Faculty of Pharmacy'),
(2004, 20, 'Faculty of Business'),
(2005, 20, 'Faculty of Allied Medical Sciences'),
(2006, 20, 'Faculty of Arts'),
(2007, 20, 'Faculty of Law'),
(2008, 20, 'Faculty of Nursing'),

-- 21. Jadara University
(2101, 21, 'Faculty of Information Technology'),
(2102, 21, 'Faculty of Engineering'),
(2103, 21, 'Faculty of Pharmacy'),
(2104, 21, 'Faculty of Business'),
(2105, 21, 'Faculty of Allied Medical Sciences'),
(2106, 21, 'Faculty of Arts & Languages'),
(2107, 21, 'Faculty of Law'),

-- 22. Amman Arab University
(2201, 22, 'Faculty of Computer Sciences & Informatics'),
(2202, 22, 'Faculty of Aviation Sciences'),
(2203, 22, 'Faculty of Business'),
(2204, 22, 'Faculty of Arts & Sciences'),
(2205, 22, 'Faculty of Law'),
(2206, 22, 'Faculty of Educational Sciences'),

-- 23. American University of Madaba (AUM)
(2301, 23, 'Faculty of Information Technology'),
(2302, 23, 'Faculty of Engineering'),
(2303, 23, 'Faculty of Health Sciences'),
(2304, 23, 'Faculty of Business & Finance'),
(2305, 23, 'Faculty of Architecture & Design'),
(2306, 23, 'Faculty of Languages & Communication'),

-- 24. World Islamic Sciences and Education University (WISE)
(2401, 24, 'Faculty of Information Technology'),
(2402, 24, 'Faculty of Islamic Banking & Financial Sciences'),
(2403, 24, 'Faculty of Business & Management'),
(2404, 24, 'Faculty of Islamic Sciences & Sharia'),
(2405, 24, 'Faculty of Arts & Educational Sciences'),
(2406, 24, 'Faculty of Law')
    ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, university_id = EXCLUDED.university_id;

-- =========================================================================
-- 4. MAJORS SEED DATA
-- =========================================================================

INSERT INTO majors (faculty_id, name) VALUES
-- 1. UJ (101 - 110)
(101, 'Computer Science (CS)'), (101, 'Computer Information Systems (CIS)'), (101, 'Business Information Technology (BIT)'), (101, 'Artificial Intelligence (AI)'), (101, 'Cybersecurity'), (101, 'Data Science'), (101, 'Software Engineering'),
(102, 'Civil Engineering'), (102, 'Mechanical Engineering'), (102, 'Electrical Engineering'), (102, 'Computer Engineering'), (102, 'Chemical Engineering'), (102, 'Mechatronics Engineering'), (102, 'Industrial Engineering'), (102, 'Architectural Engineering'),
(103, 'Doctor of Medicine (MD)'),
(104, 'Pharmacy'), (104, 'Doctor of Pharmacy (PharmD)'),
(105, 'Accounting'), (105, 'Business Administration'), (105, 'Finance & Banking'), (105, 'Marketing'), (105, 'Management Information Systems (MIS)'), (105, 'Public Administration'), (105, 'Business Economics'),
(106, 'Mathematics'), (106, 'Physics'), (106, 'Chemistry'), (106, 'Biological Sciences'), (106, 'Geology'),
(107, 'Law'), (107, 'Private Law'), (107, 'Public Law'),
(108, 'English Language & Literature'), (108, 'French Language & Literature'), (108, 'Spanish Language & Literature'), (108, 'German Language & Literature'), (108, 'Korean Language'),
(109, 'Arabic Language & Literature'), (109, 'History'), (109, 'Sociology'), (109, 'Geography'), (109, 'Philosophy'),
(110, 'General Nursing'), (110, 'Clinical Nursing'),

-- 2. JUST (201 - 208)
(201, 'Computer Science'), (201, 'Software Engineering'), (201, 'Cybersecurity'), (201, 'Network Engineering & Security'), (201, 'Artificial Intelligence & Data Science'), (201, 'Computer Information Systems'),
(202, 'Biomedical Engineering'), (202, 'Computer Engineering'), (202, 'Electrical Engineering'), (202, 'Mechanical Engineering'), (202, 'Civil Engineering'), (202, 'Aeronautical Engineering'), (202, 'Chemical Engineering'), (202, 'Industrial Engineering'), (202, 'Nuclear Engineering'),
(203, 'Doctor of Medicine (MD)'),
(204, 'Pharmacy'), (204, 'Doctor of Pharmacy (PharmD)'),
(205, 'Dental Surgery & Medicine (BDS)'),
(206, 'Medical Laboratory Sciences'), (206, 'Radiologic Technology'), (206, 'Physical Therapy'), (206, 'Optometry'), (206, 'Paramedic'),
(207, 'Mathematics'), (207, 'Applied Physics'), (207, 'Applied Chemistry'), (207, 'Biotechnology & Genetic Engineering'),
(208, 'General Nursing'), (208, 'Midwifery'),

-- 3. Yarmouk University (301 - 309)
(301, 'Computer Science'), (301, 'Software Engineering'), (301, 'Information Systems'), (301, 'Cybersecurity'), (301, 'Artificial Intelligence'),
(302, 'Computer Engineering'), (302, 'Electronics Engineering'), (302, 'Telecommunications Engineering'), (302, 'Civil Engineering'), (302, 'Industrial Engineering'), (302, 'Biomedical Engineering'), (302, 'Architectural Engineering'),
(303, 'Accounting'), (303, 'Banking & Finance'), (303, 'Business Administration'), (303, 'Marketing'), (303, 'Public Administration'), (303, 'Economics'),
(304, 'Doctor of Medicine (MD)'),
(305, 'Pharmacy'),
(306, 'Mathematics'), (306, 'Physics'), (306, 'Chemistry'), (306, 'Biological Sciences'), (306, 'Statistics'), (306, 'Earth & Environmental Sciences'),
(307, 'English Language & Literature'), (307, 'Translation'), (307, 'Arabic Language & Literature'), (307, 'History'), (307, 'Political Science'),
(308, 'Law'),
(309, 'Journalism'), (309, 'Radio & Television'), (309, 'Public Relations & Advertising'),

-- 4. Hashemite University (401 - 407)
(401, 'Computer Science & Applications'), (401, 'Software Engineering'), (401, 'Cybersecurity'), (401, 'Business Information Technology (BIT)'), (401, 'Data Science & Artificial Intelligence'),
(402, 'Mechatronics Engineering'), (402, 'Industrial Engineering'), (402, 'Biomedical Engineering'), (402, 'Mechanical Engineering'), (402, 'Civil Engineering'), (402, 'Computer Engineering'), (402, 'Electrical Engineering'), (402, 'Architectural Engineering'),
(403, 'Accounting & Commercial Law'), (403, 'Banking & Financial Sciences'), (403, 'Business Administration'), (403, 'Economics'), (403, 'Risk Management & Insurance'), (403, 'Management Information Systems (MIS)'), (403, 'Financial Technology (FinTech)'),
(404, 'Doctor of Medicine (MD)'),
(405, 'Mathematics'), (405, 'Physics'), (405, 'Chemistry'), (405, 'Biotechnology'), (405, 'Geology & Environment'),
(406, 'Medical Laboratory Sciences'), (406, 'Physical & Occupational Therapy'), (406, 'Clinical Nutrition & Dietetics'), (406, 'Medical Imaging'),
(407, 'Special Education'), (407, 'Classroom Teacher'), (407, 'Educational Technology'),

-- 5. Al-Balqa Applied University (501 - 506)
(501, 'Computer Science'), (501, 'Software Engineering'), (501, 'Cybersecurity'), (501, 'Artificial Intelligence & Robotics'), (501, 'Data Science'), (501, 'Virtual & Augmented Reality'),
(502, 'Civil Engineering'), (502, 'Electrical Power Engineering'), (502, 'Mechanical Engineering'), (502, 'Mechatronics Engineering'), (502, 'Materials Engineering'), (502, 'Chemical Engineering'),
(503, 'Accounting'), (503, 'Business Administration'), (503, 'Finance'), (503, 'Management Information Systems (MIS)'), (503, 'Logistics & Supply Chain'),
(504, 'Doctor of Medicine (MD)'),
(505, 'Applied Mathematics'), (505, 'Applied Physics'), (505, 'Chemistry'), (505, 'Medical Analysis'),
(506, 'Plant Production & Protection'), (506, 'Animal Production'), (506, 'Water & Environmental Management'),

-- 6. PSUT (601 - 603)
(601, 'Computer Science'), (601, 'Software Engineering'), (601, 'Cybersecurity'), (601, 'Data Science & Artificial Intelligence'),
(602, 'Computer Engineering'), (602, 'Communications Engineering'), (602, 'Electronics Engineering'), (602, 'Power & Energy Engineering'), (602, 'Networks & Information Security Engineering'),
(603, 'E-Business & Digital Marketing'), (603, 'Business Information Technology (BIT)'), (603, 'Business Analytics'), (603, 'Accounting'),

-- 7. German Jordanian University (701 - 704)
(701, 'Computer Science'), (701, 'Computer Engineering'), (701, 'Cybersecurity & Cloud Computing'), (701, 'Data Science & AI'), (701, 'Electrical & Communication Engineering'),
(702, 'Mechatronics Engineering'), (702, 'Industrial Engineering'), (702, 'Biomedical Engineering'), (702, 'Mechanical & Maintenance Engineering'), (702, 'Water & Environmental Engineering'),
(703, 'Logistics Sciences'), (703, 'International Accounting'), (703, 'Management'), (703, 'Digital Marketing'), (703, 'International Business'),
(704, 'Architecture'), (704, 'Design & Visual Communication'), (704, 'Interior Architecture'),

-- 8. Mutah University (801 - 807)
(801, 'Computer Science'), (801, 'Software Engineering'), (801, 'Cybersecurity'), (801, 'Computer Information Systems'),
(802, 'Civil Engineering'), (802, 'Electrical Engineering'), (802, 'Mechanical Engineering'), (802, 'Chemical Engineering'), (802, 'Industrial Engineering'), (802, 'Mining Engineering'),
(803, 'Doctor of Medicine (MD)'),
(804, 'Pharmacy'),
(805, 'Accounting'), (805, 'Finance & Banking'), (805, 'Business Administration'), (805, 'Marketing'), (805, 'Public Administration'),
(806, 'Mathematics & Statistics'), (806, 'Physics'), (806, 'Chemistry'), (806, 'Biological Sciences'),
(807, 'Law'),

-- 9. Al al-Bayt University (901 - 906)
(901, 'Computer Science'), (901, 'Software Engineering'), (901, 'Cybersecurity'), (901, 'Computer Information Systems'), (901, 'AI & Data Science'),
(902, 'Civil Engineering'), (902, 'Renewable Energy Engineering'), (902, 'Architectural Engineering'), (902, 'Electrical Engineering'),
(903, 'Accounting'), (903, 'Banking & Finance'), (903, 'Business Administration'), (903, 'Management Information Systems (MIS)'), (903, 'Economics'),
(904, 'Mathematics'), (904, 'Physics'), (904, 'Chemistry'), (904, 'Biological Sciences'), (904, 'Earth & Environmental Sciences'),
(905, 'Arabic Language & Literature'), (905, 'English Language & Literature'), (905, 'History'), (905, 'Political Science'),
(906, 'Law'),

-- 10. Tafila Technical University (1001 - 1004)
(1001, 'Computer Science'), (1001, 'Cybersecurity'), (1001, 'Software Engineering'), (1001, 'Artificial Intelligence'),
(1002, 'Mechanical Engineering'), (1002, 'Electrical Power Engineering'), (1002, 'Civil Engineering'), (1002, 'Mechatronics Engineering'), (1002, 'Mining & Minerals Engineering'), (1002, 'Chemical Engineering'),
(1003, 'Accounting'), (1003, 'Business Administration'), (1003, 'Financial & Banking Sciences'), (1003, 'Management Information Systems (MIS)'),
(1004, 'Applied Mathematics'), (1004, 'Applied Physics'), (1004, 'Applied Chemistry'),

-- 11. Al-Hussein Bin Talal University (1101 - 1105)
(1101, 'Computer Science'), (1101, 'Software Engineering'), (1101, 'Cybersecurity'), (1101, 'Information Systems'),
(1102, 'Mining Engineering'), (1102, 'Civil Engineering'), (1102, 'Electrical Engineering'), (1102, 'Mechanical Engineering'), (1102, 'Communications & Computer Engineering'), (1102, 'Renewable Energy Engineering'),
(1103, 'Business Administration'), (1103, 'Accounting'), (1103, 'Economics'), (1103, 'Finance & Banking'), (1103, 'Hotel & Tourism Management'),
(1104, 'Mathematics'), (1104, 'Physics'), (1104, 'Chemistry'), (1104, 'Biological Sciences'),
(1105, 'Nursing'),

-- 12. Applied Science Private University (1201 - 1207)
(1201, 'Computer Science'), (1201, 'Software Engineering'), (1201, 'Cybersecurity'), (1201, 'AI & Data Science'), (1201, 'Computer Networks & Systems'),
(1202, 'Civil Engineering'), (1202, 'Mechanical Engineering'), (1202, 'Electrical & Communication Engineering'), (1202, 'Computer Engineering'), (1202, 'Architectural Engineering'), (1202, 'Mechatronics Engineering'),
(1203, 'Pharmacy'), (1203, 'Clinical Pharmacy'),
(1204, 'Accounting'), (1204, 'Finance & Banking'), (1204, 'Business Administration'), (1204, 'Digital Marketing'), (1204, 'MIS'), (1204, 'FinTech'),
(1205, 'Graphic Design'), (1205, 'Interior Design'), (1205, 'Digital Media'),
(1206, 'Law'),
(1207, 'Nursing'),

-- 13. Al-Ahliyya Amman University (1301 - 1307)
(1301, 'Computer Science'), (1301, 'Software Engineering'), (1301, 'Cybersecurity'), (1301, 'Artificial Intelligence'), (1301, 'Data Science'),
(1302, 'Computer Engineering'), (1302, 'Civil Engineering'), (1302, 'Electronics & Communications Engineering'), (1302, 'Renewable Energy Engineering'),
(1303, 'Pharmacy'), (1303, 'Cosmetic Sciences'),
(1304, 'Business Administration'), (1304, 'Accounting'), (1304, 'Financial Technology (FinTech)'), (1304, 'E-Commerce & Digital Marketing'), (1304, 'Financial & Banking Sciences'),
(1305, 'Architecture'), (1305, 'Interior Design'), (1305, 'Graphic Design'),
(1306, 'Medical Analysis'), (1306, 'Optometry'), (1306, 'Physical Therapy'), (1306, 'Speech & Hearing Sciences'),
(1307, 'Law'),

-- 14. University of Petra (1401 - 1407)
(1401, 'Computer Science'), (1401, 'Software Engineering'), (1401, 'Cybersecurity'), (1401, 'Data Science & AI'), (1401, 'Animation & Multimedia'), (1401, 'Information Security'),
(1402, 'Pharmacy'), (1402, 'Clinical Nutrition & Dietetics'),
(1403, 'Architecture'), (1403, 'Interior Design'), (1403, 'Graphic Design'),
(1404, 'Business Administration'), (1404, 'Accounting'), (1404, 'Banking & Finance'), (1404, 'Digital Marketing'), (1404, 'Financial Technology (FinTech)'),
(1405, 'English Language & Translation'), (1405, 'Chemistry'), (1405, 'Mathematics'), (1405, 'Physical Education'),
(1406, 'Digital Journalism'), (1406, 'Radio & Television'), (1406, 'Public Relations'),
(1407, 'Law'),

-- 15. Al-Zaytoonah University (1501 - 1507)
(1501, 'Computer Science'), (1501, 'Software Engineering'), (1501, 'Cybersecurity'), (1501, 'Artificial Intelligence'), (1501, 'Multimedia Systems'),
(1502, 'Civil & Infrastructure Engineering'), (1502, 'Mechanical Engineering'), (1502, 'Electrical Engineering'), (1502, 'Computer Engineering'), (1502, 'Architectural Engineering'),
(1503, 'Pharmacy'),
(1504, 'Business Administration'), (1504, 'Accounting'), (1504, 'Finance & Banking'), (1504, 'Marketing'), (1504, 'Management Information Systems (MIS)'),
(1505, 'Nursing'),
(1506, 'English Language & Literature'), (1506, 'Translation'), (1506, 'Arabic Language'),
(1507, 'Law'),

-- 16. Philadelphia University (1601 - 1607)
(1601, 'Computer Science'), (1601, 'Software Engineering'), (1601, 'Cybersecurity'), (1601, 'AI & Robotics'), (1601, 'Data Science'),
(1602, 'Electrical Engineering'), (1602, 'Mechanical Engineering'), (1602, 'Civil Engineering'), (1602, 'Mechatronics Engineering'), (1602, 'Renewable Energy Engineering'), (1602, 'Computer Engineering'),
(1603, 'Pharmacy'),
(1604, 'Business Administration'), (1604, 'Accounting'), (1604, 'Financial & Banking Sciences'), (1604, 'Marketing'), (1604, 'E-Business'),
(1605, 'Clinical Nutrition'), (1605, 'Physical Therapy'), (1605, 'Medical Analysis'),
(1606, 'English Language & Literature'), (1606, 'Translation'), (1606, 'Arabic Language'),
(1607, 'Law'),

-- 17. Jerash University (1701 - 1708)
(1701, 'Computer Science'), (1701, 'Cybersecurity'), (1701, 'Data Science & AI'), (1701, 'Software Engineering'),
(1702, 'Civil Engineering'), (1702, 'Architectural Engineering'), (1702, 'Communications & Electronics Engineering'),
(1703, 'Pharmacy'),
(1704, 'Accounting'), (1704, 'Business Administration'), (1704, 'Financial & Banking Sciences'), (1704, 'Marketing'),
(1705, 'Plant Production & Protection'), (1705, 'Animal Production'),
(1706, 'English Language & Literature'), (1706, 'Arabic Language & Literature'), (1706, 'Translation'),
(1707, 'Law'),
(1708, 'Classroom Teacher'), (1708, 'Special Education'),

-- 18. Zarqa University (1801 - 1808)
(1801, 'Computer Science'), (1801, 'Software Engineering'), (1801, 'Cybersecurity'), (1801, 'CIS'), (1801, 'Artificial Intelligence'), (1801, 'Internet of Things (IoT)'),
(1802, 'Civil Engineering'), (1802, 'Electrical Engineering'), (1802, 'Architectural Engineering'), (1802, 'Mechanical Engineering'), (1802, 'Renewable Energy Engineering'),
(1803, 'Pharmacy'),
(1804, 'Accounting'), (1804, 'Islamic Banking'), (1804, 'Business Administration'), (1804, 'Marketing'), (1804, 'Finance & Investment'), (1804, 'MIS'),
(1805, 'Medical Laboratory Sciences'), (1805, 'Clinical Nutrition & Dietetics'),
(1806, 'English Language & Translation'), (1806, 'Arabic Language & Literature'), (1806, 'Journalism & Media'),
(1807, 'Law'),
(1808, 'Nursing'),

-- 19. Middle East University (1901 - 1908)
(1901, 'Computer Science'), (1901, 'Software Engineering'), (1901, 'Cybersecurity'), (1901, 'AI & Data Science'),
(1902, 'Civil Engineering'), (1902, 'Renewable Energy Engineering'), (1902, 'Computer Engineering'),
(1903, 'Business Administration'), (1903, 'Accounting'), (1903, 'FinTech'), (1903, 'Digital Marketing'), (1903, 'E-Business'),
(1904, 'Pharmacy'),
(1905, 'Journalism & Digital Media'), (1905, 'Radio & Television'), (1905, 'Public Relations'),
(1906, 'Architecture'), (1906, 'Graphic Design'), (1906, 'Interior Design'),
(1907, 'English Language & Literature'), (1907, 'Translation'),
(1908, 'Law'),

-- 20. Isra University (2001 - 2008)
(2001, 'Computer Science'), (2001, 'Software Engineering'), (2001, 'Cybersecurity'), (2001, 'AI & Data Science'),
(2002, 'Civil Engineering'), (2002, 'Electrical Engineering'), (2002, 'Mechanical Engineering'), (2002, 'Renewable Energy Engineering'),
(2003, 'Pharmacy'),
(2004, 'Accounting'), (2004, 'Business Administration'), (2004, 'Finance & Banking'), (2004, 'Marketing'), (2004, 'Management Information Systems (MIS)'),
(2005, 'Medical Laboratory Sciences'), (2005, 'Physical Therapy'),
(2006, 'English Language & Literature'), (2006, 'Translation'),
(2007, 'Law'),
(2008, 'Nursing'),

-- 21. Jadara University (2101 - 2107)
(2101, 'Computer Science'), (2101, 'Software Engineering'), (2101, 'Cybersecurity'), (2101, 'AI & Data Science'), (2101, 'Mobile Computing'),
(2102, 'Civil Engineering'), (2102, 'Communications & Computer Engineering'), (2102, 'Renewable Energy Engineering'),
(2103, 'Pharmacy'),
(2104, 'Business Administration'), (2104, 'Accounting'), (2104, 'Finance & Banking'), (2104, 'Digital Marketing'), (2104, 'MIS'), (2104, 'Logistics & Supply Chain'),
(2105, 'Medical Analysis'), (2105, 'Radiologic Technology'),
(2106, 'English Language & Literature'), (2106, 'Translation'), (2106, 'Arabic Language'),
(2107, 'Law'),

-- 22. Amman Arab University (2201 - 2206)
(2201, 'Computer Science'), (2201, 'Software Engineering'), (2201, 'Cybersecurity'), (2201, 'AI & Data Science'),
(2202, 'Aircraft Maintenance Engineering'), (2202, 'Aviation Management'),
(2203, 'Business Administration'), (2203, 'Accounting'), (2203, 'Finance & Banking'), (2203, 'Marketing'), (2203, 'Digital Marketing'),
(2204, 'English Language & Translation'), (2204, 'Mathematics'),
(2205, 'Law'),
(2206, 'Special Education'), (2206, 'Educational Administration'),

-- 23. American University of Madaba (2301 - 2306)
(2301, 'Computer Science'), (2301, 'Data Science & AI'), (2301, 'Cybersecurity'),
(2302, 'Civil Engineering'), (2302, 'Electrical Engineering'), (2302, 'Mechanical Engineering'),
(2303, 'Pharmacy'), (2303, 'Medical Laboratories'), (2303, 'Nutrition & Dietetics'),
(2304, 'Business Administration'), (2304, 'Accounting'), (2304, 'Marketing'), (2304, 'Human Resource Management'), (2304, 'Banking & Finance'),
(2305, 'Architecture'), (2305, 'Interior Design'), (2305, 'Graphic Design'),
(2306, 'English Language & Literature'), (2306, 'Translation'),

-- 24. WISE University (2401 - 2406)
(2401, 'Computer Science'), (2401, 'Software Engineering'), (2401, 'Cybersecurity'), (2401, 'Networks & Information Security'),
(2402, 'Islamic Banking'), (2402, 'Islamic Economics'), (2402, 'Financial Technology (FinTech)'),
(2403, 'Business Administration'), (2403, 'Accounting'), (2403, 'Marketing'), (2403, 'Management Information Systems (MIS)'),
(2404, 'Islamic Jurisprudence (Fiqh)'), (2404, 'Usul al-Fiqh'), (2404, 'Quranic Studies'), (2404, 'Hadith & Its Sciences'),
(2405, 'Arabic Language & Literature'), (2405, 'English Language & Literature'), (2405, 'Islamic History'),
(2406, 'Sharia & Law'), (2406, 'Public Law');

-- =========================================================================
-- 5. RESET SEQUENCES
-- =========================================================================

SELECT setval('universities_id_seq', (SELECT COALESCE(MAX(id), 1) FROM universities));
SELECT setval('faculties_id_seq', (SELECT COALESCE(MAX(id), 1) FROM faculties));
SELECT setval('majors_id_seq', (SELECT COALESCE(MAX(id), 1) FROM majors));