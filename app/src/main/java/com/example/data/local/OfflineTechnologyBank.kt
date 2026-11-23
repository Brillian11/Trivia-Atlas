package com.example.data.local

import kotlin.random.Random

/**
 * Curated multilingual (EN / ES / ID) Technology trivia bank (56 genuine questions)
 * covering computing history, programming, web protocols, hardware, AI, and cybersecurity.
 */
object OfflineTechnologyBank {

    private data class TechFact(
        val idTag: String,
        val difficulty: String,
        val nameEn: String,
        val nameEs: String,
        val nameId: String,
        val answerEn: String,
        val answerEs: String,
        val answerId: String,
        val promptEn: String,
        val promptEs: String,
        val promptId: String,
        val expEn: String,
        val expEs: String,
        val expId: String
    )

    private val techFacts = listOf(
        TechFact(
            "tech_http", "easy",
            "HTTP Status Code 404", "Código de estado HTTP 404", "Kode Status HTTP 404",
            "Not Found", "No encontrado (Not Found)", "Tidak Ditemukan (Not Found)",
            "What does the HTTP response status code 404 indicate to a web browser?",
            "¿Qué indica el código de estado HTTP 404 en un navegador web?",
            "Apa arti kode status respons HTTP 404 pada peramban web?",
            "HTTP 404 means the server could not find the requested webpage or resource at that URL.",
            "HTTP 404 indica que el servidor no encontró el recurso solicitado en esa URL.",
            "HTTP 404 berarti server tidak dapat menemukan halaman atau sumber daya pada URL tersebut."
        ),
        TechFact(
            "tech_tim", "easy",
            "Tim Berners-Lee", "Tim Berners-Lee", "Tim Berners-Lee",
            "World Wide Web", "La World Wide Web", "World Wide Web (WWW)",
            "British computer scientist Tim Berners-Lee invented which global information system at CERN in 1989?",
            "¿Qué sistema global de información inventó Tim Berners-Lee en el CERN en 1989?",
            "Ilmuwan komputer Tim Berners-Lee menciptakan sistem informasi global apa di CERN pada 1989?",
            "Tim Berners-Lee created HTTP, HTML, and the first web browser, giving rise to the World Wide Web.",
            "Tim Berners-Lee creó HTTP, HTML y el primer navegador, dando origen a la World Wide Web.",
            "Tim Berners-Lee merancang HTTP, HTML, dan peramban pertama yang melahirkan World Wide Web."
        ),
        TechFact(
            "tech_ada", "medium",
            "Ada Lovelace", "Ada Lovelace", "Ada Lovelace",
            "Analytical Engine", "Máquina Analítica", "Mesin Analitik (Analytical Engine)",
            "Ada Lovelace is celebrated as the first computer programmer for writing an algorithm for Babbage's what?",
            "¿Para qué máquina de Charles Babbage escribió Ada Lovelace el primer algoritmo programado?",
            "Ada Lovelace dikenal sebagai programmer pertama karena menulis algoritma untuk mesin rancangan Charles Babbage bernama apa?",
            "In 1843, Ada Lovelace wrote a method for calculating Bernoulli numbers on Babbage's Analytical Engine.",
            "En 1843, Ada Lovelace publicó un algoritmo para calcular números de Bernoulli en la Máquina Analítica.",
            "Pada 1843, Ada Lovelace menulis algoritma perhitungan bilangan Bernoulli untuk Mesin Analitik Babbage."
        ),
        TechFact(
            "tech_linus", "easy",
            "Linux Kernel & Git", "Núcleo Linux y Git", "Kernel Linux & Git",
            "Linus Torvalds", "Linus Torvalds", "Linus Torvalds",
            "Which Finnish-American software engineer created both the Linux kernel and the Git version control system?",
            "¿Qué ingeniero de software creó tanto el núcleo Linux como el sistema de control de versiones Git?",
            "Insinyur perangkat lunak manakah yang menciptakan kernel Linux sekaligus sistem kontrol versi Git?",
            "Linus Torvalds released the first Linux kernel in 1991 and built Git in 2005 for distributed kernel development.",
            "Linus Torvalds lanzó Linux en 1991 y desarrolló Git en 2005 para coordinar el código del núcleo.",
            "Linus Torvalds merilis kernel Linux pada 1991 dan menciptakan Git pada 2005 untuk kolaborasi kode."
        ),
        TechFact(
            "tech_byte", "easy",
            "Bits per Byte", "Bits por byte", "Bit dalam Satu Byte",
            "8 bits", "8 bits", "8 bit",
            "How many binary bits make up one standard byte in modern computing?",
            "¿Cuántos bits binarios componen un byte estándar en la informática moderna?",
            "Berapa jumlah bit biner yang membentuk satu byte standar dalam komputasi modern?",
            "An 8-bit byte can represent 256 distinct values (from 0 to 255) and was standardized across 8-bit ASCII.",
            "Un byte de 8 bits puede representar 256 valores distintos (de 0 a 255).",
            "Satu byte berisi 8 bit yang mampu merepresentasikan 256 nilai berbeda (0 hingga 255)."
        ),
        TechFact(
            "tech_cpu", "easy",
            "CPU Meaning", "Significado de CPU", "Kepanjangan CPU",
            "Central Processing Unit", "Unidad Central de Procesamiento", "Central Processing Unit",
            "In computer hardware, what does the acronym CPU stand for?",
            "En hardware informático, ¿qué significa la sigla CPU?",
            "Dalam perangkat keras komputer, apa kepanjangan dari akronim CPU?",
            "The Central Processing Unit executes program instructions via arithmetic, logic, and control operations.",
            "La Unidad Central de Procesamiento ejecuta las instrucciones del sistema mediante operaciones lógicas y aritméticas.",
            "Central Processing Unit menjalankan instruksi program melalui operasi aritmetika, logika, dan kontrol."
        ),
        TechFact(
            "tech_ram", "easy",
            "Volatile Memory", "Memoria volátil", "Memori Volatil",
            "RAM (Random Access Memory)", "RAM (Memoria de Acceso Aleatorio)", "RAM (Random Access Memory)",
            "Which type of high-speed computer memory loses its stored data as soon as power is turned off?",
            "¿Qué tipo de memoria rápida pierde sus datos en cuanto se apaga la corriente eléctrica?",
            "Jenis memori cepat komputer manakah yang langsung kehilangan datanya saat listrik diputus?",
            "RAM provides fast working space for active programs, unlike non-volatile SSDs and hard drives.",
            "La memoria RAM ofrece acceso rápido a programas activos, pero es volátil al apagarse el equipo.",
            "RAM menyediakan ruang kerja cepat bagi aplikasi aktif, berbeda dengan SSD yang bersifat non-volatil."
        ),
        TechFact(
            "tech_transistor", "medium",
            "Bell Labs 1947", "Bell Labs 1947", "Bell Labs 1947",
            "Transistor", "Transistor", "Transistor",
            "Which semiconductor switching device invented at Bell Labs in 1947 replaced bulky vacuum tubes?",
            "¿Qué componente semiconductor inventado en Bell Labs en 1947 reemplazó a los tubos de vacío?",
            "Komponen semikonduktor apa yang ditemukan di Bell Labs tahun 1947 dan menggantikan tabung vakum?",
            "John Bardeen, Walter Brattain, and William Shockley won the 1956 Nobel Prize for inventing the transistor.",
            "Bardeen, Brattain y Shockley recibieron el Premio Nobel de Física de 1956 por inventar el transistor.",
            "Bardeen, Brattain, dan Shockley meraih Hadiah Nobel Fisika 1956 atas penemuan transistor."
        ),
        TechFact(
            "tech_moore", "medium",
            "Transistor Doubling", "Duplicación de transistores", "Penggandaan Transistor",
            "Moore's Law", "Ley de Moore", "Hukum Moore",
            "Which famous observation by Intel co-founder Gordon Moore states that microchip transistor counts double roughly every two years?",
            "¿Qué famosa observación de Gordon Moore afirma que los transistores en un microchip se duplican cada dos años?",
            "Hukum terkenal dari salah satu pendiri Intel, Gordon Moore, menyatakan jumlah transistor berlipat ganda tiap sekitar dua tahun?",
            "Formulated in 1965, Moore's Law guided decades of silicon miniaturization across CPUs and GPUs.",
            "Formulada en 1965, la Ley de Moore guió décadas de miniaturización en procesadores de silicio.",
            "Dirumuskan pada 1965, Hukum Moore memandu percepatan miniaturisasi cip silikon selama puluhan tahun."
        ),
        TechFact(
            "tech_turing", "medium",
            "Machine Intelligence Test", "Prueba de inteligencia mecánica", "Uji Kecerdasan Mesin",
            "Turing Test", "Test de Turing", "Uji Turing (Turing Test)",
            "Proposed by Alan Turing in 1950 as the 'Imitation Game', what benchmark tests whether a machine exhibits human-like conversation?",
            "¿Qué prueba propuesta por Alan Turing en 1950 evalúa si una máquina conversa de forma indistinguible de un humano?",
            "Tolok ukur yang diusulkan Alan Turing pada 1950 untuk menguji kemampuan mesin bercakap layaknya manusia disebut apa?",
            "Alan Turing published 'Computing Machinery and Intelligence' in 1950, laying philosophical foundations for AI.",
            "Alan Turing publicó su ensayo en 1950 sentando las bases teóricas de la inteligencia artificial.",
            "Alan Turing menerbitkan makalah tersebut pada 1950 yang menjadi fondasi konsep kecerdasan buatan."
        ),
        TechFact(
            "tech_dns", "easy",
            "Internet Address Book", "Guía de direcciones de Internet", "Buku Alamat Internet",
            "DNS (Domain Name System)", "DNS (Sistema de Nombres de Dominio)", "DNS (Domain Name System)",
            "Which hierarchical naming protocol translates human-readable domain names like example.org into numeric IP addresses?",
            "¿Qué sistema traduce nombres de dominio legibles como example.org en direcciones IP numéricas?",
            "Sistem protokol apa yang menerjemahkan nama domain seperti example.org menjadi alamat IP numerik?",
            "DNS acts like the internet's phonebook, resolving hostnames to IPv4 or IPv6 addresses.",
            "El DNS funciona como la agenda de Internet, traduciendo dominios en direcciones IPv4 o IPv6.",
            "DNS berfungsi bagaikan buku telepon internet yang mengubah nama domain menjadi alamat IPv4 atau IPv6."
        ),
        TechFact(
            "tech_https", "easy",
            "Encrypted Web Traffic", "Tráfico web cifrado", "Lalu Lintas Web Terenkripsi",
            "TLS (Transport Layer Security)", "TLS (Seguridad de la Capa de Transporte)", "TLS (Transport Layer Security)",
            "Which cryptographic protocol secures HTTPS web connections between browsers and servers today?",
            "¿Qué protocolo criptográfico protege actualmente las conexiones web HTTPS entre navegador y servidor?",
            "Protokol kriptografi apa yang mengamankan koneksi web HTTPS antara peramban dan server masa kini?",
            "TLS (the modern successor to SSL) encrypts web traffic, verifies server identity via certificates, and prevents tampering.",
            "TLS (sucesor de SSL) cifra el tráfico web, autentica el servidor con certificados y evita alteraciones.",
            "TLS (penerus SSL) mengenkripsi lalu lintas web, memverifikasi sertifikat server, dan mencegah penyadapan."
        ),
        TechFact(
            "tech_python", "easy",
            "Guido van Rossum Language", "Lenguaje de Guido van Rossum", "Bahasa Guido van Rossum",
            "Python", "Python", "Python",
            "Created by Guido van Rossum in 1991 and named after Monty Python, which language relies on indentation to define code blocks?",
            "Creado por Guido van Rossum en 1991 en honor a Monty Python, ¿qué lenguaje usa la indentación para definir bloques?",
            "Bahasa pemrograman apa yang diciptakan Guido van Rossum pada 1991 dan memakai indentasi untuk blok kode?",
            "Python emphasizes readability and became the leading language in data science, automation, and machine learning.",
            "Python prioriza la legibilidad y domina en ciencia de datos, automatización y aprendizaje automático.",
            "Python mengutamakan keterbacaan kode dan sangat populer dalam sains data, otomasi, dan pembelajaran mesin."
        ),
        TechFact(
            "tech_html", "easy",
            "Web Page Structure", "Estructura de páginas web", "Struktur Halaman Web",
            "HTML (HyperText Markup Language)", "HTML (Lenguaje de Marcado de Hipertexto)", "HTML (HyperText Markup Language)",
            "Which standard markup language defines headings, paragraphs, links, and images inside web pages?",
            "¿Qué lenguaje de marcado estándar estructura encabezados, párrafos, enlaces e imágenes en la web?",
            "Bahasa markah standar apa yang menyusun judul, paragraf, tautan, dan gambar di halaman web?",
            "HTML provides document structure, CSS controls visual styling, and JavaScript adds interactive behavior.",
            "HTML define la estructura, CSS el diseño visual y JavaScript la interactividad de la web.",
            "HTML menyusun kerangka dokumen, CSS mengatur tampilan visual, dan JavaScript menambah interaktivitas."
        ),
        TechFact(
            "tech_kotlin", "medium",
            "JetBrains Android Language", "Lenguaje de JetBrains para Android", "Bahasa JetBrains untuk Android",
            "Kotlin", "Kotlin", "Kotlin",
            "Named after an island near St. Petersburg, which statically typed JetBrains language became Google's preferred Android language?",
            "Con el nombre de una isla cercana a San Petersburgo, ¿qué lenguaje de JetBrains es el preferido para Android?",
            "Dinamai dari pulau dekat St. Petersburg, bahasa JetBrains bertipe statis apa yang menjadi pilihan utama untuk Android?",
            "Unveiled by JetBrains in 2011 and backed by Google in 2017, Kotlin offers null safety and full JVM interoperability.",
            "Presentado por JetBrains en 2011 y adoptado por Google en 2017, Kotlin ofrece seguridad frente a nulos.",
            "Diperkenalkan JetBrains pada 2011 dan didukung Google sejak 2017, Kotlin unggul dalam keamanan null dan interoperabilitas JVM."
        ),
        TechFact(
            "tech_usb", "easy",
            "Universal Peripheral Port", "Puerto universal de periféricos", "Port Periferal Universal",
            "USB (Universal Serial Bus)", "USB (Bus Serie Universal)", "USB (Universal Serial Bus)",
            "Introduced in 1996 to unify keyboards, mice, and storage drives under one plug, what does USB stand for?",
            "Introducido en 1996 para unificar teclados, ratones y almacenamiento, ¿qué significa la sigla USB?",
            "Diperkenalkan pada 1996 untuk menyatukan sambungan papan ketik, tetikus, dan penyimpanan, apa kepanjangan USB?",
            "Modern USB-C cables support reversible insertion, high-speed data transfer, display output, and Power Delivery charging.",
            "El estándar USB-C moderno permite conexión reversible, datos a alta velocidad, vídeo y carga rápida.",
            "Standar USB-C modern mendukung colokan bolak-balik, transfer data cepat, output video, serta pengisian daya."
        ),
        TechFact(
            "tech_bluetooth", "medium",
            "Harald Gormsson Wireless", "Tecnología inalámbrica de Harald", "Nirkabel Raja Harald",
            "Bluetooth", "Bluetooth", "Bluetooth",
            "Named after 10th-century Danish king Harald 'Blåtand' Gormsson, which short-range wireless standard links headphones and sensors?",
            "Nombrada en honor al rey danés Harald 'Diente Azul', ¿qué tecnología inalámbrica conecta auriculares y sensores?",
            "Dinamai dari raja Denmark abad ke-10 Harald 'Gigi Biru', standar nirkabel jarak pendek apa yang menghubungkan headphone?",
            "Bluetooth's iconic rune logo merges the Younger Futhark initials H (ᚼ) and B (ᛒ) of Harald Bluetooth.",
            "El logotipo de Bluetooth combina las runas nórdicas equivalentes a las iniciales H y B de Harald Bluetooth.",
            "Logo ikonik Bluetooth memadukan dua aksara rune Nordik untuk inisial H dan B dari nama Harald Bluetooth."
        ),
        TechFact(
            "tech_ssd", "easy",
            "Flash Storage Drive", "Almacenamiento en memoria flash", "Penyimpanan Memori Flash",
            "SSD (Solid-State Drive)", "SSD (Unidad de Estado Sólido)", "SSD (Solid-State Drive)",
            "Which storage device uses NAND flash memory chips instead of spinning magnetic platters?",
            "¿Qué dispositivo de almacenamiento emplea chips flash NAND en lugar de discos magnéticos giratorios?",
            "Perangkat penyimpanan data apa yang memakai cip memori flash NAND alih-alih piringan magnetik berputar?",
            "Without moving mechanical heads, SSDs deliver dramatically faster boot times and shock resistance than HDDs.",
            "Al carecer de piezas móviles, los SSD logran arranques mucho más rápidos y resisten mejor los golpes que los HDD.",
            "Tanpa komponen mekanik bergerak, SSD menghadirkan kecepatan baca-tulis jauh lebih tinggi dan tahan guncangan."
        ),
        TechFact(
            "tech_gpu", "medium",
            "Parallel Graphics Silicon", "Silicio gráfico paralelo", "Cip Grafis Paralel",
            "GPU (Graphics Processing Unit)", "GPU (Unidad de Procesamiento Gráfico)", "GPU (Graphics Processing Unit)",
            "Originally engineered to render 3D polygons and shaders in parallel, which processor also powers modern AI model training?",
            "Diseñada originalmente para renderizar gráficos 3D en paralelo, ¿qué unidad impulsa hoy el entrenamiento de IA?",
            "Awalnya dirancang untuk merender grafis 3D secara paralel, prosesor apa yang kini menjadi tulang punggung pelatihan AI?",
            "GPUs contain thousands of small arithmetic cores ideal for matrix multiplication in both graphics and neural networks.",
            "Las GPU integran miles de núcleos ideales para multiplicar matrices en videojuegos y redes neuronales.",
            "GPU memiliki ribuan inti komputasi paralel yang sangat efisien untuk perkalian matriks pada grafis dan jaringan saraf tiruan."
        ),
        TechFact(
            "tech_transformer", "hard",
            "2017 Attention Architecture", "Arquitectura Attention de 2017", "Arsitektur Attention 2017",
            "Transformer", "Transformer", "Transformer",
            "Introduced in the 2017 paper 'Attention Is All You Need', which neural network architecture underpins modern LLMs?",
            "Presentada en el artículo de 2017 'Attention Is All You Need', ¿qué arquitectura sustenta los grandes modelos de lenguaje?",
            "Diperkenalkan lewat makalah 2017 'Attention Is All You Need', arsitektur jaringan saraf apa yang mendasari LLM modern?",
            "Transformers use self-attention mechanisms to model relationships across entire sequences in parallel.",
            "Los Transformers emplean autoatención para procesar relaciones entre palabras de manera paralela.",
            "Arsitektur Transformer memakai mekanisme self-attention untuk memahami hubungan antarkata secara paralel."
        ),
        TechFact(
            "tech_arpanet", "hard",
            "1969 Packet Network", "Red de paquetes de 1969", "Jaringan Paket 1969",
            "ARPANET", "ARPANET", "ARPANET",
            "Which U.S. Defense research packet-switching network sent its first host-to-host message in 1969 as the direct precursor to the Internet?",
            "¿Qué red de conmutación de paquetes envió su primer mensaje en 1969 y fue la precursora directa de Internet?",
            "Jaringan riset peralihan paket manakah yang mengirim pesan pertamanya pada 1969 dan menjadi cikal bakal Internet?",
            "On October 29, 1969, UCLA and Stanford Research Institute exchanged the letters 'LO' on ARPANET before a crash.",
            "El 29 de octubre de 1969, UCLA y Stanford intercambiaron el primer mensaje por ARPANET.",
            "Pada 29 Oktober 1969, UCLA dan Stanford saling mengirim pesan pertama melalui jaringan ARPANET."
        ),
        TechFact(
            "tech_ipv4", "medium",
            "32-bit Internet Address", "Dirección de 32 bits", "Alamat IP 32-bit",
            "IPv4", "IPv4", "IPv4",
            "Which Internet Protocol version uses 32-bit numeric addresses formatted as four octets such as 192.168.1.1?",
            "¿Qué versión del Protocolo de Internet usa direcciones de 32 bits en cuatro octetos como 192.168.1.1?",
            "Versi Protokol Internet apa yang memakai alamat 32-bit berformat empat oktet seperti 192.168.1.1?",
            "IPv4 supports roughly 4.3 billion addresses, prompting adoption of 128-bit IPv6 for massive device growth.",
            "IPv4 admite unos 4.300 millones de direcciones, lo que impulsó el paso a IPv6 de 128 bits.",
            "IPv4 hanya menampung sekitar 4,3 miliar alamat sehingga mendorong adopsi IPv6 berkapasitas 128-bit."
        ),
        TechFact(
            "tech_open_source", "medium",
            "SQL Query Language", "Lenguaje de consultas SQL", "Bahasa Kueri SQL",
            "SQL (Structured Query Language)", "SQL (Lenguaje de Consulta Estructurada)", "SQL (Structured Query Language)",
            "Developed at IBM in the 1970s, which declarative domain-specific language manages and queries relational databases?",
            "Desarrollado en IBM en los años 70, ¿qué lenguaje estándar gestiona y consulta bases de datos relacionales?",
            "Dikembangkan di IBM pada era 1970-an, bahasa standar apa yang dipakai untuk mengelola basis data relasional?",
            "SQL uses commands like SELECT, INSERT, JOIN, and WHERE to organize structured tables of records.",
            "SQL utiliza comandos como SELECT, INSERT, JOIN y WHERE para consultar tablas relacionales.",
            "SQL memakai perintah seperti SELECT, INSERT, JOIN, dan WHERE untuk mengolah tabel data terstruktur."
        ),
        TechFact(
            "tech_qr", "easy",
            "Denso Wave Matrix Barcode", "Código matricial de Denso Wave", "Kode Matriks Denso Wave",
            "QR Code (Quick Response)", "Código QR (Quick Response)", "Kode QR (Quick Response)",
            "Invented in 1994 by Masahiro Hara at Denso Wave to track auto parts, what 2D matrix barcode is scanned by smartphones?",
            "Inventado en 1994 por Denso Wave para piezas automotrices, ¿qué código 2D escanean hoy los teléfonos?",
            "Diciptakan tahun 1994 oleh Masahiro Hara di Denso Wave untuk melacak suku cadang, kode matriks 2D apa yang kini dipindai ponsel?",
            "QR codes store URLs, Wi-Fi credentials, and payments using error correction even if part of the square is smudged.",
            "Los códigos QR almacenan enlaces, claves Wi-Fi y pagos con corrección de errores ante daños parciales.",
            "Kode QR menyimpan tautan, akses Wi-Fi, hingga transaksi pembayaran dilengkapi koreksi kesalahan otomatis."
        ),
        TechFact(
            "tech_ethernet", "medium",
            "Bob Metcalfe LAN", "Red local de Bob Metcalfe", "Jaringan Kabel Bob Metcalfe",
            "Ethernet", "Ethernet", "Ethernet",
            "Developed at Xerox PARC in 1973 by Robert Metcalfe, which wired networking family connects devices in local area networks?",
            "Desarrollada en Xerox PARC en 1973 por Robert Metcalfe, ¿qué tecnología cableada domina las redes locales?",
            "Dikembangkan di Xerox PARC pada 1973 oleh Robert Metcalfe, teknologi jaringan kabel apa yang dipakai pada LAN?",
            "Standardized under IEEE 802.3, Ethernet carries data frames over twisted-pair copper or fiber-optic cables.",
            "Estandarizada bajo IEEE 802.3, Ethernet transmite tramas por cable de par trenzado o fibra óptica.",
            "Distandarkan sebagai IEEE 802.3, Ethernet mengirim paket data lewat kabel tembaga twisted-pair atau serat optik."
        ),
        TechFact(
            "tech_twofa", "easy",
            "Login Verification Layer", "Doble verificación de acceso", "Lapisan Verifikasi Masuk",
            "Two-Factor Authentication (2FA)", "Autenticación en dos factores (2FA)", "Autentikasi Dua Faktor (2FA)",
            "Requiring both a password and a hardware security key or one-time authenticator code is known by what security term?",
            "Exigir una contraseña más una llave física o código temporal recibe qué nombre en ciberseguridad?",
            "Mewajibkan kata sandi ditambah kode sekali pakai atau kunci keamanan fisik disebut metode keamanan apa?",
            "2FA combines something you know (password) with something you have (passkey/authenticator) or are (biometric).",
            "La 2FA combina algo que sabes (clave) con algo que tienes (llave física o app) para frenar robos de cuenta.",
            "2FA menggabungkan sesuatu yang kamu tahu (sandi) dengan sesuatu yang kamu miliki (token/authenticator)."
        ),
        TechFact(
            "tech_phishing", "easy",
            "Deceptive Credential Bait", "Engaño para robar credenciales", "Pancingan Kredensial Palsu",
            "Phishing", "Phishing", "Phishing",
            "What cybersecurity attack uses forged emails or look-alike websites to trick people into revealing passwords or financial details?",
            "¿Qué ataque informático usa correos falsos o webs clonadas para engañar y robar contraseñas?",
            "Serangan keamanan siber apa yang menyamar lewat email atau situs tiruan untuk mencuri kata sandi korban?",
            "Attackers mimic trusted banks or services in phishing lures; checking the domain and using passkeys helps block it.",
            "Los atacantes imitan servicios legítimos; revisar la URL exacta y usar passkeys protege contra el phishing.",
            "Penyerang meniru institusi tepercaya; memeriksa domain asli dan memakai passkey efektif mencegah phishing."
        )
    )

    fun entities(now: Long): List<CachedQuestionEntity> {
        val rng = Random(202605L)
        val result = ArrayList<CachedQuestionEntity>(techFacts.size * 2)
        techFacts.forEachIndexed { idx, f ->
            val others = techFacts.indices.filter { it != idx }.shuffled(rng).take(3)
            result.add(
                CachedQuestionEntity(
                    id = f.idTag,
                    category = "technology",
                    difficulty = f.difficulty,
                    questionText = f.promptEn,
                    correctAnswer = f.answerEn,
                    incorrectAnswersSerialized = OfflineTriviaSeed.encodeIncorrect(others.map { techFacts[it].answerEn }),
                    pictureQuestion = false,
                    explanation = f.expEn,
                    cachedAt = now
                )
            )
            result.add(
                CachedQuestionEntity(
                    id = "${f.idTag}_rev",
                    category = "technology",
                    difficulty = if (f.difficulty == "easy") "medium" else "hard",
                    questionText = "Which technology concept, standard, or pioneer matches: \"${f.expEn}\"?",
                    correctAnswer = f.nameEn,
                    incorrectAnswersSerialized = OfflineTriviaSeed.encodeIncorrect(others.map { techFacts[it].nameEn }),
                    pictureQuestion = false,
                    explanation = "${f.nameEn} (${f.answerEn}): ${f.expEn}",
                    cachedAt = now
                )
            )
        }
        return result
    }

    private val localizedEs: Map<String, LocalizedQuestionCopy> by lazy {
        val rng = Random(202605L)
        val map = HashMap<String, LocalizedQuestionCopy>(techFacts.size * 2)
        techFacts.forEachIndexed { idx, f ->
            val others = techFacts.indices.filter { it != idx }.shuffled(rng).take(3)
            map[f.idTag] = LocalizedQuestionCopy(
                questionText = f.promptEs,
                correctAnswer = f.answerEs,
                incorrectAnswers = others.map { techFacts[it].answerEs },
                explanation = f.expEs
            )
            map["${f.idTag}_rev"] = LocalizedQuestionCopy(
                questionText = "¿Qué concepto, estándar o pionero tecnológico corresponde a: \"${f.expEs}\"?",
                correctAnswer = f.nameEs,
                incorrectAnswers = others.map { techFacts[it].nameEs },
                explanation = "${f.nameEs} (${f.answerEs}): ${f.expEs}"
            )
        }
        map
    }

    private val localizedIn: Map<String, LocalizedQuestionCopy> by lazy {
        val rng = Random(202605L)
        val map = HashMap<String, LocalizedQuestionCopy>(techFacts.size * 2)
        techFacts.forEachIndexed { idx, f ->
            val others = techFacts.indices.filter { it != idx }.shuffled(rng).take(3)
            map[f.idTag] = LocalizedQuestionCopy(
                questionText = f.promptId,
                correctAnswer = f.answerId,
                incorrectAnswers = others.map { techFacts[it].answerId },
                explanation = f.expId
            )
            map["${f.idTag}_rev"] = LocalizedQuestionCopy(
                questionText = "Konsep, standar, atau tokoh teknologi manakah yang dijelaskan sebagai: \"${f.expId}\"?",
                correctAnswer = f.nameId,
                incorrectAnswers = others.map { techFacts[it].nameId },
                explanation = "${f.nameId} (${f.answerId}): ${f.expId}"
            )
        }
        map
    }

    fun getLocalizedCopy(id: String, languageCode: String): LocalizedQuestionCopy? =
        when (languageCode.lowercase()) {
            "es" -> localizedEs[id]
            "in", "id" -> localizedIn[id]
            else -> null
        }
}
