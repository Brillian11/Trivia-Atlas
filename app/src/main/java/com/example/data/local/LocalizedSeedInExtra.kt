package com.example.data.local

object LocalizedSeedInExtra {
    val questions: Map<String, LocalizedQuestionCopy> = mapOf(
        // Pengetahuan Umum (5..15)
        "seed_gk_5" to LocalizedQuestionCopy(
            questionText = "Berapa banyak sisi yang dimiliki bangun segi enam beraturan (heksagon)?",
            correctAnswer = "6",
            incorrectAnswers = listOf("5", "7", "8"),
            explanation = "Heksagon adalah poligon bersisi enam dengan jumlah seluruh sudut dalamnya sebesar 720 derajat."
        ),
        "seed_gk_6" to LocalizedQuestionCopy(
            questionText = "Batu mulia manakah yang merupakan mineral alami paling keras pada skala Mohs?",
            correctAnswer = "Berlian",
            incorrectAnswers = listOf("Rubi", "Kuarsa", "Topas"),
            explanation = "Berlian berada di tingkat 10—puncak skala Mohs—berkat ikatan karbon tetrahedralnya yang sangat kuat."
        ),
        "seed_gk_7" to LocalizedQuestionCopy(
            questionText = "Apakah warna khas batu permata zamrud (emerald)?",
            correctAnswer = "Hijau",
            incorrectAnswers = listOf("Biru", "Merah", "Kuning sawo"),
            explanation = "Zamrud adalah varietas hijau dari mineral beril yang memperoleh warnanya dari unsur kromium atau vanadium."
        ),
        "seed_gk_8" to LocalizedQuestionCopy(
            questionText = "Dalam angka Romawi, berapakah nilai yang dilambangkan oleh huruf 'L'?",
            correctAnswer = "50",
            incorrectAnswers = listOf("100", "500", "40"),
            explanation = "Dalam sistem Romawi: I=1, V=5, X=10, L=50, C=100, D=500, dan M=1000."
        ),
        "seed_gk_9" to LocalizedQuestionCopy(
            questionText = "Apa nama mata uang resmi negara Jepang?",
            correctAnswer = "Yen",
            incorrectAnswers = listOf("Won", "Yuan", "Baht"),
            explanation = "Diperkenalkan pada 1871 di era Meiji, yen Jepang dilambangkan dengan simbol ¥."
        ),
        "seed_gk_10" to LocalizedQuestionCopy(
            questionText = "Istilah catur apakah yang terjadi saat raja tidak sedang diskak namun tidak punya langkah sah?",
            correctAnswer = "Pat (Stalemate)",
            incorrectAnswers = listOf("Skakmat", "Rokade", "En passant"),
            explanation = "Menurut aturan catur resmi, kondisi pat langsung mengakhiri permainan dengan hasil seri (remis)."
        ),
        "seed_gk_11" to LocalizedQuestionCopy(
            questionText = "Apa satuan SI untuk hambatan listrik?",
            correctAnswer = "Ohm",
            incorrectAnswers = listOf("Watt", "Joule", "Tesla"),
            explanation = "Dinamai menurut fisikawan Jerman Georg Simon Ohm, hambatan dilambangkan huruf Yunani omega (Ω)."
        ),
        "seed_gk_12" to LocalizedQuestionCopy(
            questionText = "Rumpun bahasa manakah yang mencakup bahasa Hungaria, Finlandia, dan Estonia?",
            correctAnswer = "Uralik",
            incorrectAnswers = listOf("Slavia", "Keltik", "Semit"),
            explanation = "Berbeda dari kebanyakan bahasa Eropa yang termasuk Indo-Eropa, Finlandia, Estonia, dan Hungaria berumpun Uralik."
        ),
        "seed_gk_13" to LocalizedQuestionCopy(
            questionText = "Disebut apakah seni tradisional Jepang menanam pohon mini di dalam pot dangkal?",
            correctAnswer = "Bonsai",
            incorrectAnswers = listOf("Ikebana", "Kintsugi", "Suiseki"),
            explanation = "Bermakna 'ditanam di pot dangkal', bonsai membentuk pohon selama puluhan tahun lewat pemangkasan dan pengawatan."
        ),
        "seed_gk_14" to LocalizedQuestionCopy(
            questionText = "Unsur logam apakah yang memberi warna merah menyala pada batu rubi?",
            correctAnswer = "Kromium",
            incorrectAnswers = listOf("Kobalt", "Tembaga", "Mangan"),
            explanation = "Rubi dan safir sama-sama berasal dari mineral korundum; jejak kromium menghasilkan warna merah rubi."
        ),
        "seed_gk_15" to LocalizedQuestionCopy(
            questionText = "Berapa jumlah hari dalam satu tahun kabisat?",
            correctAnswer = "366",
            incorrectAnswers = listOf("364", "365", "367"),
            explanation = "Tahun kabisat menambahkan tanggal 29 Februari setiap empat tahun agar kalender tetap selaras dengan orbit Bumi."
        ),

        // Sains (5..15)
        "seed_sci_5" to LocalizedQuestionCopy(
            questionText = "Planet manakah di Tata Surya kita yang letaknya paling dekat dengan Matahari?",
            correctAnswer = "Merkurius",
            incorrectAnswers = listOf("Venus", "Mars", "Bumi"),
            explanation = "Merkurius mengorbit pada jarak rata-rata 58 juta km dari Matahari dan menyelesaikan satu tahun dalam 88 hari Bumi."
        ),
        "seed_sci_6" to LocalizedQuestionCopy(
            questionText = "Gas apakah yang menyusun sekitar 78% atmosfer kering Bumi?",
            correctAnswer = "Nitrogen",
            incorrectAnswers = listOf("Oksigen", "Karbon dioksida", "Argon"),
            explanation = "Udara kering terdiri atas sekitar 78% nitrogen, 21% oksigen, serta 1% argon dan gas lainnya."
        ),
        "seed_sci_7" to LocalizedQuestionCopy(
            questionText = "Gaya apakah yang menjaga Bulan tetap mengorbit mengelilingi Bumi?",
            correctAnswer = "Gravitasi",
            incorrectAnswers = listOf("Magnetisme", "Gesekan", "Gaya nuklir"),
            explanation = "Gravitasi menghasilkan tarikan sentripetal yang menyeimbangkan kecepatan orbit Bulan."
        ),
        "seed_sci_8" to LocalizedQuestionCopy(
            questionText = "Berapakah nilai pH air murni yang netral pada suhu 25 °C?",
            correctAnswer = "7",
            incorrectAnswers = listOf("0", "5", "14"),
            explanation = "Skala pH berkisar dari 0 hingga 14; di bawah 7 bersifat asam, 7 netral, dan di atas 7 bersifat basa."
        ),
        "seed_sci_9" to LocalizedQuestionCopy(
            questionText = "Gas mulia manakah yang bernomor atom 2 dan biasa dipakai untuk menerbangkan balon cuaca?",
            correctAnswer = "Helium",
            incorrectAnswers = listOf("Neon", "Hidrogen", "Xenon"),
            explanation = "Helium lebih ringan daripada udara dan bersifat inert secara kimia, sehingga aman bagi balon dan pendingin MRI."
        ),
        "seed_sci_10" to LocalizedQuestionCopy(
            questionText = "Ilmuwan siapakah yang merumuskan tiga hukum gerak dan hukum gravitasi universal?",
            correctAnswer = "Isaac Newton",
            incorrectAnswers = listOf("Galileo Galilei", "Johannes Kepler", "Niels Bohr"),
            explanation = "Newton menerbitkan hukum gerak dan gravitasinya dalam karya 'Principia Mathematica' pada 1687."
        ),
        "seed_sci_11" to LocalizedQuestionCopy(
            questionText = "Apakah organ dalam terbesar pada tubuh manusia?",
            correctAnswer = "Hati",
            incorrectAnswers = listOf("Limpa", "Pankreas", "Ginjal"),
            explanation = "Hati orang dewasa menjalankan ratusan fungsi metabolik, termasuk detoksifikasi dan produksi empedu."
        ),
        "seed_sci_12" to LocalizedQuestionCopy(
            questionText = "Partikel subatomik manakah yang membawa muatan listrik negatif?",
            correctAnswer = "Elektron",
            incorrectAnswers = listOf("Proton", "Neutron", "Positron"),
            explanation = "Elektron bermuatan negatif (-1), sedangkan proton bermuatan +1 dan neutron tidak bermuatan."
        ),
        "seed_sci_13" to LocalizedQuestionCopy(
            questionText = "Apa nama proses ketika tumbuhan mengubah cahaya matahari, air, dan CO₂ menjadi glukosa?",
            correctAnswer = "Fotosintesis",
            incorrectAnswers = listOf("Fermentasi", "Transpirasi", "Kemosintesis"),
            explanation = "Kloroplas menangkap energi cahaya menggunakan klorofil untuk membentuk gula dan melepaskan oksigen."
        ),
        "seed_sci_14" to LocalizedQuestionCopy(
            questionText = "Unsur logam manakah yang memiliki konduktivitas listrik tertinggi pada suhu kamar?",
            correctAnswer = "Perak",
            incorrectAnswers = listOf("Emas", "Tembaga", "Aluminium"),
            explanation = "Perak menghantarkan listrik sedikit lebih baik dari tembaga, meski tembaga lebih luas dipakai karena lebih ekonomis."
        ),
        "seed_sci_15" to LocalizedQuestionCopy(
            questionText = "Pada tekanan standar permukaan laut, pada suhu berapa derajat Celsius air murni mendidih?",
            correctAnswer = "100",
            incorrectAnswers = listOf("80", "90", "112"),
            explanation = "Pada skala Celsius, air murni membeku di 0 °C dan mendidih di 100 °C pada tekanan 1 atmosfer."
        ),

        // Sejarah (5..15)
        "seed_his_5" to LocalizedQuestionCopy(
            questionText = "Siapakah Presiden pertama Amerika Serikat?",
            correctAnswer = "George Washington",
            incorrectAnswers = listOf("Thomas Jefferson", "John Adams", "Benjamin Franklin"),
            explanation = "George Washington dilantik sebagai Presiden pertama Amerika Serikat pada 30 April 1789."
        ),
        "seed_his_6" to LocalizedQuestionCopy(
            questionText = "Peradaban kuno manakah yang membangun Machu Picchu di Pegunungan Andes?",
            correctAnswer = "Inca",
            incorrectAnswers = listOf("Maya", "Aztec", "Olmec"),
            explanation = "Dibangun pada abad ke-15 di bawah Kaisar Pachacuti, Machu Picchu berada di ketinggian 2.430 meter di Peru."
        ),
        "seed_his_7" to LocalizedQuestionCopy(
            questionText = "Pada tahun berapakah Tembok Berlin runtuh, menandai berakhirnya Perang Dingin?",
            correctAnswer = "1989",
            incorrectAnswers = listOf("1985", "1991", "1993"),
            explanation = "Warga merobohkan Tembok Berlin pada 9 November 1989 setelah perlintasan perbatasan Jerman Timur dibuka."
        ),
        "seed_his_8" to LocalizedQuestionCopy(
            questionText = "Kapal pesiar manakah yang tenggelam dalam pelayaran perdananya di Atlantik Utara pada April 1912?",
            correctAnswer = "Titanic",
            incorrectAnswers = listOf("Lusitania", "Britannic", "Carpathia"),
            explanation = "RMS Titanic menabrak gunung es pada 14–15 April 1912, memicu pembaruan besar keselamatan maritim."
        ),
        "seed_his_9" to LocalizedQuestionCopy(
            questionText = "Kota Renaisans Italia manakah yang dipimpin turun-temurun oleh keluarga bankir Medici?",
            correctAnswer = "Firenze (Florence)",
            incorrectAnswers = listOf("Venesia", "Milan", "Napoli"),
            explanation = "Dukungan seni keluarga Medici menjadikan Firenze pusat lahirnya era Renaisans Italia."
        ),
        "seed_his_10" to LocalizedQuestionCopy(
            questionText = "Siapakah wanita pertama yang meraih Hadiah Nobel lewat bidang Fisika pada 1903?",
            correctAnswer = "Marie Curie",
            incorrectAnswers = listOf("Rosalind Franklin", "Ada Lovelace", "Lise Meitner"),
            explanation = "Marie Curie memenangkan Nobel Fisika pada 1903 dan Nobel Kimia pada 1911."
        ),
        "seed_his_11" to LocalizedQuestionCopy(
            questionText = "Dokumen bersejarah apakah yang disahkan Raja John dari Inggris di Runnymede pada 1215?",
            correctAnswer = "Magna Carta",
            incorrectAnswers = listOf("Domesday Book", "Perjanjian Verdun", "Bill of Rights"),
            explanation = "Magna Carta menegaskan prinsip penting bahwa raja sekalipun tunduk pada aturan hukum."
        ),
        "seed_his_12" to LocalizedQuestionCopy(
            questionText = "Jaringan perdagangan kuno apakah yang menghubungkan Chang'an di Tiongkok dengan dunia Mediterania?",
            correctAnswer = "Jalur Sutra",
            incorrectAnswers = listOf("Jalur Ambar", "Rute Dupa", "Jalan Raya Persia"),
            explanation = "Aktif sejak Dinasti Han, Jalur Sutra membawa sutra, rempah, kertas, seni, dan gagasan lintas Eurasia."
        ),
        "seed_his_13" to LocalizedQuestionCopy(
            questionText = "Pada tahun berapakah Indonesia memproklamasikan kemerdekaannya?",
            correctAnswer = "1945",
            incorrectAnswers = listOf("1942", "1949", "1950"),
            explanation = "Sukarno dan Mohammad Hatta membacakan Proklamasi Kemerdekaan Indonesia pada 17 Agustus 1945."
        ),
        "seed_his_14" to LocalizedQuestionCopy(
            questionText = "Kaisar Bizantium manakah yang memprakarsai pembangunan Hagia Sophia di Konstantinopel?",
            correctAnswer = "Yustinianus I",
            incorrectAnswers = listOf("Konstantinus I", "Heraklius", "Basil II"),
            explanation = "Rampung pada 537 M di masa Yustinianus I, kubah raksasa Hagia Sophia merupakan mahakarya teknik Bizantium."
        ),
        "seed_his_15" to LocalizedQuestionCopy(
            questionText = "Siapakah pemimpin ekspedisi tahun 1911 yang pertama kali mencapai Kutub Selatan?",
            correctAnswer = "Roald Amundsen",
            incorrectAnswers = listOf("Robert Falcon Scott", "Ernest Shackleton", "Richard Byrd"),
            explanation = "Penjelajah Norwegia Roald Amundsen tiba di Kutub Selatan geografis pada 14 Desember 1911."
        ),

        // Geografi (5..15)
        "seed_geo_5" to LocalizedQuestionCopy(
            questionText = "Apakah nama samudra terbesar di Bumi?",
            correctAnswer = "Samudra Pasifik",
            incorrectAnswers = listOf("Samudra Atlantik", "Samudra Hindia", "Samudra Arktik"),
            explanation = "Dengan luas lebih dari 165 juta km², Samudra Pasifik mencakup hampir sepertiga permukaan Bumi."
        ),
        "seed_geo_6" to LocalizedQuestionCopy(
            questionText = "Di benua manakah Gurun Sahara berada?",
            correctAnswer = "Afrika",
            incorrectAnswers = listOf("Asia", "Australia", "Amerika Selatan"),
            explanation = "Sahara membentang melintasi 11 negara di Afrika Utara dan menjadi gurun panas terluas di dunia."
        ),
        "seed_geo_7" to LocalizedQuestionCopy(
            questionText = "Negara manakah yang memiliki garis pantai terpanjang di dunia?",
            correctAnswer = "Kanada",
            incorrectAnswers = listOf("Rusia", "Indonesia", "Australia"),
            explanation = "Dengan ribuan pulau Arktik dan pesisirnya, total garis pantai Kanada melampaui 202.000 kilometer."
        ),
        "seed_geo_8" to LocalizedQuestionCopy(
            questionText = "Apa nama ibu kota negara Islandia?",
            correctAnswer = "Reykjavik",
            incorrectAnswers = listOf("Oslo", "Helsinki", "Tórshavn"),
            explanation = "Reykjavik adalah ibu kota negara berdaulat yang terletak paling utara di dunia."
        ),
        "seed_geo_9" to LocalizedQuestionCopy(
            questionText = "Pegunungan di Amerika Selatan manakah yang merupakan rangkaian pegunungan daratan terpanjang di Bumi?",
            correctAnswer = "Pegunungan Andes",
            incorrectAnswers = listOf("Pegunungan Rocky", "Pegunungan Alpen", "Pegunungan Ural"),
            explanation = "Membentang sekitar 7.000 km di pesisir barat Amerika Selatan, Andes melintasi tujuh negara."
        ),
        "seed_geo_10" to LocalizedQuestionCopy(
            questionText = "Ibu kota negara Eropa manakah yang dibelah Sungai Donau menjadi Buda dan Pest?",
            correctAnswer = "Budapest",
            incorrectAnswers = listOf("Wina", "Praha", "Beograd"),
            explanation = "Buda, Pest, dan Óbuda bersatu pada tahun 1873 membentuk ibu kota Hungaria, Budapest."
        ),
        "seed_geo_11" to LocalizedQuestionCopy(
            questionText = "Danau Baikal, danau air tawar terdalam di dunia, terletak di negara mana?",
            correctAnswer = "Rusia",
            incorrectAnswers = listOf("Kazakhstan", "Mongolia", "Kanada"),
            explanation = "Berada di Siberia selatan, Danau Baikal mencapai kedalaman 1.642 m dan menyimpan ~20% air tawar cair dunia."
        ),
        "seed_geo_12" to LocalizedQuestionCopy(
            questionText = "Dua samudra apakah yang dihubungkan oleh Terusan Panama?",
            correctAnswer = "Atlantik dan Pasifik",
            incorrectAnswers = listOf("Hindia dan Pasifik", "Atlantik dan Arktik", "Hindia dan Atlantik"),
            explanation = "Dibuka pada 1914, terusan sepanjang 82 km ini menghubungkan Laut Karibia (Atlantik) ke Teluk Panama (Pasifik)."
        ),
        "seed_geo_13" to LocalizedQuestionCopy(
            questionText = "Disebut apakah bagian wilayah Indonesia di Pulau Borneo?",
            correctAnswer = "Kalimantan",
            incorrectAnswers = listOf("Sumatra", "Sulawesi", "Jawa"),
            explanation = "Kalimantan mencakup provinsi-provinsi Indonesia di Borneo, sedangkan Sumatra adalah pulau terbesar penuh milik Indonesia."
        ),
        "seed_geo_14" to LocalizedQuestionCopy(
            questionText = "Addis Ababa adalah ibu kota dari negara Afrika yang mana?",
            correctAnswer = "Etiopia",
            incorrectAnswers = listOf("Kenya", "Tanzania", "Ghana"),
            explanation = "Berada pada ketinggian sekitar 2.355 meter, Addis Ababa menjadi markas besar Uni Afrika."
        ),
        "seed_geo_15" to LocalizedQuestionCopy(
            questionText = "Garis khayal apakah yang membagi Bumi menjadi Belahan Bumi Utara dan Selatan?",
            correctAnswer = "Khatulistiwa",
            incorrectAnswers = listOf("Meridian Utama", "Garis Balik Utara", "Garis Tanggal Internasional"),
            explanation = "Pada 0 derajat lintang, garis khatulistiwa (ekuator) mengelilingi Bumi tepat di tengah kedua kutub."
        ),

        // Seni & Sastra (5..15)
        "seed_art_5" to LocalizedQuestionCopy(
            questionText = "Siapakah pengarang naskah tragedi terkenal 'Romeo and Juliet'?",
            correctAnswer = "William Shakespeare",
            incorrectAnswers = listOf("Christopher Marlowe", "Charles Dickens", "Jane Austen"),
            explanation = "Ditulis di awal karier Shakespeare sekitar 1595, kisah ini berlatar di kota Verona."
        ),
        "seed_art_6" to LocalizedQuestionCopy(
            questionText = "Lukisan potret karya Leonardo da Vinci manakah yang dipajang di Museum Louvre, Paris?",
            correctAnswer = "Mona Lisa",
            incorrectAnswers = listOf("Perjamuan Terakhir", "Lady with an Ermine", "Salvator Mundi"),
            explanation = "Dilukis di atas panel kayu poplar pada awal abad ke-16, Mona Lisa tersohor akan senyum teknik sfumato-nya."
        ),
        "seed_art_7" to LocalizedQuestionCopy(
            questionText = "Dalam novel 'The Hobbit' karya J.R.R. Tolkien, siapakah nama tokoh utama hobbit tersebut?",
            correctAnswer = "Bilbo Baggins",
            incorrectAnswers = listOf("Frodo Baggins", "Samwise Gamgee", "Peregrin Took"),
            explanation = "Bilbo Baggins meninggalkan Shire bersama Gandalf dan tiga belas kurcaci menuju Erebor."
        ),
        "seed_art_8" to LocalizedQuestionCopy(
            questionText = "Pengarang Spanyol manakah yang menulis novel klasik 'Don Quixote'?",
            correctAnswer = "Miguel de Cervantes",
            incorrectAnswers = listOf("Lope de Vega", "Gabriel García Márquez", "Federico García Lorca"),
            explanation = "Terbit dalam dua bagian pada 1605 dan 1615, karya ini mengisahkan petualangan kesatria Alonso Quixano."
        ),
        "seed_art_9" to LocalizedQuestionCopy(
            questionText = "Pelukis ekspresionis Norwegia manakah yang menciptakan lukisan 'The Scream' pada 1893?",
            correctAnswer = "Edvard Munch",
            incorrectAnswers = listOf("Gustav Klimt", "Egon Schiele", "Wassily Kandinsky"),
            explanation = "Munch melukis beberapa versi 'The Scream' terinspirasi langit senja merah menyala di atas fyord Oslo."
        ),
        "seed_art_10" to LocalizedQuestionCopy(
            questionText = "Siapakah penulis novel terkenal 'Pride and Prejudice'?",
            correctAnswer = "Jane Austen",
            incorrectAnswers = listOf("Charlotte Brontë", "Mary Shelley", "George Eliot"),
            explanation = "Terbit tahun 1813, 'Pride and Prejudice' berfokus pada kisah Elizabeth Bennet dan Fitzwilliam Darcy."
        ),
        "seed_art_11" to LocalizedQuestionCopy(
            questionText = "Pematung Prancis manakah yang menciptakan patung perunggu terkenal 'Sang Pemikir' (The Thinker)?",
            correctAnswer = "Auguste Rodin",
            incorrectAnswers = listOf("Constantin Brâncuși", "Alberto Giacometti", "Edgar Degas"),
            explanation = "Awalnya dirancang untuk bagian atas 'Gerbang Neraka', patung ini dicetak dalam ukuran besar pada 1904."
        ),
        "seed_art_12" to LocalizedQuestionCopy(
            questionText = "Peraih Nobel sastra asal Kolombia manakah yang menulis 'Seratus Tahun Kesunyian'?",
            correctAnswer = "Gabriel García Márquez",
            incorrectAnswers = listOf("Mario Vargas Llosa", "Jorge Luis Borges", "Julio Cortázar"),
            explanation = "Tonggak realisme magis (1967) ini menuturkan kisah tujuh generasi keluarga Buendía di Macondo."
        ),
        "seed_art_13" to LocalizedQuestionCopy(
            questionText = "Bangsawati Jepang abad ke-11 manakah yang menulis karya klasik 'Hikayat Genji'?",
            correctAnswer = "Murasaki Shikibu",
            incorrectAnswers = listOf("Sei Shōnagon", "Ono no Komachi", "Izumi Shikibu"),
            explanation = "Sering disebut novel pertama di dunia, 'Hikayat Genji' menggambarkan kehidupan istana era Heian secara mendalam."
        ),
        "seed_art_14" to LocalizedQuestionCopy(
            questionText = "Aliran seni rupa yang dipelopori Claude Monet dan menekankan tangkapan cahaya alami sesaat adalah?",
            correctAnswer = "Impresionisme",
            incorrectAnswers = listOf("Kubisme", "Barok", "Surealisme"),
            explanation = "Dinamai dari lukisan Monet 'Impression, Sunrise' (1872), Impresionisme mengubah wajah seni lukis modern."
        ),
        "seed_art_15" to LocalizedQuestionCopy(
            questionText = "Dalam novel 'Moby-Dick' karya Herman Melville, hewan apakah Moby Dick itu?",
            correctAnswer = "Paus sperma",
            incorrectAnswers = listOf("Hiu putih besar", "Cumi-cumi raksasa", "Paus pembunuh"),
            explanation = "Kapten Ahab memburu paus sperma putih raksasa tersebut melintasi samudra dunia."
        ),

        // Film & TV (5..15)
        "seed_film_5" to LocalizedQuestionCopy(
            questionText = "Dalam film animasi 'The Lion King' (1994), siapakah nama ayah Simba?",
            correctAnswer = "Mufasa",
            incorrectAnswers = listOf("Scar", "Rafiki", "Zazu"),
            explanation = "Raja Mufasa memerintah Pride Lands sebelum dikhianati oleh adiknya sendiri, Scar."
        ),
        "seed_film_6" to LocalizedQuestionCopy(
            questionText = "Film dinosaurus fenomenal tahun 1993 manakah yang disutradarai Steven Spielberg?",
            correctAnswer = "Jurassic Park",
            incorrectAnswers = listOf("King Kong", "Godzilla", "Avatar"),
            explanation = "Diadaptasi dari novel Michael Crichton, 'Jurassic Park' memelopori efek CGI dan animatronik realistis."
        ),
        "seed_film_7" to LocalizedQuestionCopy(
            questionText = "Apa nama negara fiktif di Afrika dalam film Marvel 'Black Panther'?",
            correctAnswer = "Wakanda",
            incorrectAnswers = listOf("Genosha", "Sokovia", "Latveria"),
            explanation = "Wakanda menyembunyikan teknologi mutakhirnya yang ditenagai logam langka vibranium dari dunia luar."
        ),
        "seed_film_8" to LocalizedQuestionCopy(
            questionText = "Film Pixar tahun 1995 manakah yang menjadi film panjang pertama buatan animasi komputer sepenuhnya?",
            correctAnswer = "Toy Story",
            incorrectAnswers = listOf("A Bug's Life", "Monsters, Inc.", "Shrek"),
            explanation = "Disutradarai John Lasseter, 'Toy Story' memperkenalkan karakter ikonik Woody dan Buzz Lightyear."
        ),
        "seed_film_9" to LocalizedQuestionCopy(
            questionText = "Serial fiksi ilmiah Inggris manakah yang menampilkan penjelajah waktu dalam boks telepon polisi biru?",
            correctAnswer = "Doctor Who",
            incorrectAnswers = listOf("Torchwood", "Black Mirror", "Red Dwarf"),
            explanation = "Pertama tayang di BBC tahun 1963, sang Doctor menjelajahi ruang dan waktu menggunakan TARDIS."
        ),
        "seed_film_10" to LocalizedQuestionCopy(
            questionText = "Siapakah komposer musik tema legendaris untuk 'Star Wars', 'Indiana Jones', dan 'Jaws'?",
            correctAnswer = "John Williams",
            incorrectAnswers = listOf("Hans Zimmer", "Ennio Morricone", "Howard Shore"),
            explanation = "John Williams meraih lebih dari 50 nominasi Piala Oscar sepanjang enam dekade kariernya."
        ),
        "seed_film_11" to LocalizedQuestionCopy(
            questionText = "Siapakah sutradara trilogi film 'The Lord of the Rings'?",
            correctAnswer = "Peter Jackson",
            incorrectAnswers = listOf("Ridley Scott", "James Cameron", "Guillermo del Toro"),
            explanation = "Diproduksi di Selandia Baru, trilogi arahan Peter Jackson memenangi total 17 Piala Oscar."
        ),
        "seed_film_12" to LocalizedQuestionCopy(
            questionText = "Film epik Akira Kurosawa tahun 1954 manakah yang mengisahkan warga desa menyewa pendekar membela panen?",
            correctAnswer = "Seven Samurai",
            incorrectAnswers = listOf("Rashomon", "Ran", "Yojimbo"),
            explanation = "'Seven Samurai' menginspirasi film koboi 'The Magnificent Seven' dan penceritaan laga modern."
        ),
        "seed_film_13" to LocalizedQuestionCopy(
            questionText = "Drama tahun 1941 garapan Orson Welles manakah yang dibuka dengan kata terakhir misterius 'Rosebud'?",
            correctAnswer = "Citizen Kane",
            incorrectAnswers = listOf("Casablanca", "Sunset Boulevard", "Vertigo"),
            explanation = "Welles ikut menulis, menyutradarai, dan memerankan taipan media Charles Foster Kane di usia 25 tahun."
        ),
        "seed_film_14" to LocalizedQuestionCopy(
            questionText = "Dalam film 'Inception' karya Christopher Nolan, benda pribadi apakah yang dipakai untuk menguji mimpi atau nyata?",
            correctAnswer = "Totem",
            incorrectAnswers = listOf("Kronometer", "Pendulum", "Kompas"),
            explanation = "Gasing kecil totem milik Cobb membantunya memastikan apakah ia sedang berada di dunia nyata atau mimpi."
        ),
        "seed_film_15" to LocalizedQuestionCopy(
            questionText = "Karakter spons kuning manakah yang tinggal di dalam buah nanas di dasar laut Bikini Bottom?",
            correctAnswer = "SpongeBob SquarePants",
            incorrectAnswers = listOf("Patrick Star", "Squidward", "Plankton"),
            explanation = "Diciptakan oleh pendidik ilmu kelautan Stephen Hillenburg, serial ini pertama tayang tahun 1999."
        ),

        // Musik (5..15)
        "seed_mus_5" to LocalizedQuestionCopy(
            questionText = "Grup musik rock asal Liverpool manakah yang beranggotakan John Lennon, Paul McCartney, George Harrison, dan Ringo Starr?",
            correctAnswer = "The Beatles",
            incorrectAnswers = listOf("The Rolling Stones", "The Who", "Queen"),
            explanation = "Dibentuk di Liverpool pada 1960, The Beatles menjadi grup musik terlaris sepanjang sejarah."
        ),
        "seed_mus_6" to LocalizedQuestionCopy(
            questionText = "Alat musik tiup berlidah tunggal manakah yang diciptakan oleh Adolphe Sax pada 1840-an?",
            correctAnswer = "Saksofon",
            incorrectAnswers = listOf("Klarinet", "Oboe", "Fagot"),
            explanation = "Dipatenkan di Paris pada 1846, saksofon memadukan lantangnya alat musik tiup logam dengan kelincahan tiup kayu."
        ),
        "seed_mus_7" to LocalizedQuestionCopy(
            questionText = "Dalam dinamika musik, apa arti istilah Italia 'forte' bagi pemain musik?",
            correctAnswer = "Keras / lantang",
            incorrectAnswers = listOf("Lembut", "Cepat", "Lambat"),
            explanation = "Ditandai huruf 'f', forte berarti dimainkan keras atau kuat, sedangkan 'p' (piano) berarti lembut."
        ),
        "seed_mus_8" to LocalizedQuestionCopy(
            questionText = "Komponis Jerman yang mengalami ketulian manakah yang menggubah Simfoni No. 9 dengan 'Ode to Joy'?",
            correctAnswer = "Ludwig van Beethoven",
            incorrectAnswers = listOf("Franz Schubert", "Johannes Brahms", "Joseph Haydn"),
            explanation = "Rampung pada 1824 saat Beethoven sudah tuli berat, simfoni ini memelopori paduan suara dalam karya simfoni."
        ),
        "seed_mus_9" to LocalizedQuestionCopy(
            questionText = "Aliran musik asal Jamaika akhir 1960-an manakah yang dipopulerkan ke seluruh dunia oleh Bob Marley?",
            correctAnswer = "Reggae",
            incorrectAnswers = listOf("Bossa nova", "Flamenco", "Afrobeat"),
            explanation = "Berciri petikan ritmis off-beat dan bas yang dalam, reggae diakui sebagai warisan budaya UNESCO."
        ),
        "seed_mus_10" to LocalizedQuestionCopy(
            questionText = "Ansambel musik perkusi tradisional Indonesia manakah yang terdiri atas bilah logam perunggu, gong, dan kendang?",
            correctAnswer = "Gamelan",
            incorrectAnswers = listOf("Sitar", "Taiko", "Mariachi"),
            explanation = "Berkembang kuat di Jawa dan Bali, gamelán mengiringi tarian, pagelaran wayang, serta upacara adat."
        ),
        "seed_mus_11" to LocalizedQuestionCopy(
            questionText = "Apa nama jenis suara pria paling rendah dalam musik vokal klasik?",
            correctAnswer = "Bas",
            incorrectAnswers = listOf("Tenor", "Bariton", "Alto"),
            explanation = "Urutan suara klasik dari terendah hingga tertinggi meliputi Bas, Bariton, Tenor, Alto, Mezosopran, dan Sopran."
        ),
        "seed_mus_12" to LocalizedQuestionCopy(
            questionText = "Komponis jenius Austria abad ke-18 manakah yang menggubah 'Seruling Ajaib' (The Magic Flute)?",
            correctAnswer = "Wolfgang Amadeus Mozart",
            incorrectAnswers = listOf("Franz Liszt", "Frédéric Chopin", "Claude Debussy"),
            explanation = "Mozart menggubah lebih dari 600 karya simfoni, konserto, musik kamar, dan opera sebelum berusia 36 tahun."
        ),
        "seed_mus_13" to LocalizedQuestionCopy(
            questionText = "Aliran musik Brasil perpaduan samba dan jazz sejuk manakah yang melahirkan lagu 'The Girl from Ipanema'?",
            correctAnswer = "Bossa nova",
            incorrectAnswers = listOf("Tango", "Cumbia", "Merengue"),
            explanation = "Dipopulerkan di Rio de Janeiro pada akhir 1950-an oleh João Gilberto dan Antônio Carlos Jobim."
        ),
        "seed_mus_14" to LocalizedQuestionCopy(
            questionText = "Dalam notasi balok standar birama 4/4, berapa ketukan nilai not setengah bertitik?",
            correctAnswer = "3",
            incorrectAnswers = listOf("2", "4", "1,5"),
            explanation = "Not setengah bernilai 2 ketukan dan tanda titik menambah separuhnya (1 ketukan), sehingga total 3 ketukan."
        ),
        "seed_mus_15" to LocalizedQuestionCopy(
            questionText = "Band rock Inggris manakah yang merilis lagu opera-rock legendaris tahun 1975 'Bohemian Rhapsody'?",
            correctAnswer = "Queen",
            incorrectAnswers = listOf("Pink Floyd", "Led Zeppelin", "Deep Purple"),
            explanation = "Ditulis oleh Freddie Mercury, 'Bohemian Rhapsody' memadukan harmoni paduan suara megah dengan hard rock."
        ),

        // Olahraga & Rekreasi (5..15)
        "seed_spt_5" to LocalizedQuestionCopy(
            questionText = "Dalam bola basket, berapa poin yang diperoleh dari tembakan di luar garis lengkung tiga angka?",
            correctAnswer = "3",
            incorrectAnswers = listOf("1", "2", "4"),
            explanation = "Tembakan dari dalam lengkungan bernilai 2 poin, lemparan bebas 1 poin, dan dari luar garis bernilai 3 poin."
        ),
        "seed_spt_6" to LocalizedQuestionCopy(
            questionText = "Olahraga raket manakah yang menggunakan kok (shuttlecock) berbulu atau sintetis?",
            correctAnswer = "Bulu tangkis",
            incorrectAnswers = listOf("Squash", "Tenis meja", "Padel"),
            explanation = "Sebagai cabang Olimpiade, pukulan smes bulu tangkis dapat melesat lebih dari 400 km/jam."
        ),
        "seed_spt_7" to LocalizedQuestionCopy(
            questionText = "Berapa banyak cincin saling bertaut yang terdapat pada bendera resmi Olimpiade?",
            correctAnswer = "5",
            incorrectAnswers = listOf("4", "6", "7"),
            explanation = "Kelima cincin biru, kuning, hitam, hijau, dan merah melambangkan persatuan lima benua berpenghuni."
        ),
        "seed_spt_8" to LocalizedQuestionCopy(
            questionText = "Berapakah skor maksimal yang bisa diraih dalam satu permainan boling sepuluh pin standar?",
            correctAnswer = "300",
            incorrectAnswers = listOf("200", "250", "350"),
            explanation = "Mencetak dua belas kali strike berturut-turut di seluruh sepuluh frame menghasilkan skor sempurna 300."
        ),
        "seed_spt_9" to LocalizedQuestionCopy(
            questionText = "Balap sepeda tahunan tiga minggu manakah yang garis finisnya berada di Champs-Élysées, Paris?",
            correctAnswer = "Tour de France",
            incorrectAnswers = listOf("Giro d'Italia", "Vuelta a España", "Paris–Roubaix"),
            explanation = "Pertama digelar tahun 1903, Tour de France menganugerahkan kaus kuning (maillot jaune) bagi pemimpin klasemen."
        ),
        "seed_spt_10" to LocalizedQuestionCopy(
            questionText = "Dalam golf, istilah apakah yang menyebut penyelesaian satu lubang dengan satu pukulan di bawah par?",
            correctAnswer = "Birdie",
            incorrectAnswers = listOf("Eagle", "Bogey", "Albatross"),
            explanation = "Satu di bawah par disebut birdie, dua di bawah par disebut eagle, dan satu di atas par disebut bogey."
        ),
        "seed_spt_11" to LocalizedQuestionCopy(
            questionText = "Seni bela diri Jepang yang berarti 'jalan kelembutan' dan berfokus pada bantingan serta kuncian adalah?",
            correctAnswer = "Judo",
            incorrectAnswers = listOf("Karate", "Kendo", "Taekwondo"),
            explanation = "Didirikan oleh Jigoro Kano pada 1882, judo resmi menjadi cabang Olimpiade sejak Tokyo 1964."
        ),
        "seed_spt_12" to LocalizedQuestionCopy(
            questionText = "Berapa kilometer jarak standar resmi lomba lari maraton Olimpiade?",
            correctAnswer = "42,195",
            incorrectAnswers = listOf("38,5", "40,0", "45,2"),
            explanation = "Dibakukan pada 1921 sejauh 42,195 km, mengikuti panjang lintasan Olimpiade London 1908."
        ),
        "seed_spt_13" to LocalizedQuestionCopy(
            questionText = "Dalam Formula 1, sirkuit jalan raya bersejarah manakah di Monte Carlo yang memiliki tikungan tajam Fairmont?",
            correctAnswer = "Sirkuit Monako",
            incorrectAnswers = listOf("Silverstone", "Monza", "Spa-Francorchamps"),
            explanation = "Pertama kali digelar 1929 di jalanan pelabuhan Monte Carlo, Monako termasuk Triple Crown balap dunia."
        ),
        "seed_spt_14" to LocalizedQuestionCopy(
            questionText = "Dalam olahraga rugbi union, berapa poin yang diperoleh dari satu 'try'?",
            correctAnswer = "5",
            incorrectAnswers = listOf("3", "4", "6"),
            explanation = "Mencetak try memberi 5 poin, ditambah 2 poin tambahan jika tendangan konversi berhasil masuk."
        ),
        "seed_spt_15" to LocalizedQuestionCopy(
            questionText = "Negara manakah yang menjuarai Piala Dunia FIFA pertama pada tahun 1930?",
            correctAnswer = "Uruguay",
            incorrectAnswers = listOf("Argentina", "Brasil", "Italia"),
            explanation = "Tuan rumah Uruguay mengalahkan Argentina 4–2 pada partai final di Montevideo tahun 1930."
        ),

        // Kuliner & Minuman (5..15)
        "seed_food_5" to LocalizedQuestionCopy(
            questionText = "Hidangan sawi putih fermentasi manakah yang menjadi pelengkap utama masakan Korea?",
            correctAnswer = "Kimchi",
            incorrectAnswers = listOf("Sauerkraut", "Miso", "Tempe"),
            explanation = "Dibumbui cabai, bawang putih, dan jahe, kimchi memiliki ratusan ragam resep daerah."
        ),
        "seed_food_6" to LocalizedQuestionCopy(
            questionText = "Saus guacamole terutama dibuat dari buah apa yang dilumatkan?",
            correctAnswer = "Alpukat",
            incorrectAnswers = listOf("Mangga", "Pepaya", "Zukini"),
            explanation = "Berasal dari peradaban Aztec, guacamole memadukan alpukat matang dengan jeruk nipis, garam, dan ketumbar."
        ),
        "seed_food_7" to LocalizedQuestionCopy(
            questionText = "Kacang apakah yang menjadi bahan utama saus pesto tradisional Genova bersama daun basil dan minyak zaitun?",
            correctAnswer = "Kacang pinus",
            incorrectAnswers = listOf("Kenari", "Kacang mete", "Kacang tanah"),
            explanation = "Pesto Genova klasik memadukan daun basil, bawang putih, kacang pinus, keju Parmesan, dan minyak zaitun."
        ),
        "seed_food_8" to LocalizedQuestionCopy(
            questionText = "Masakan daging sapi berbumbu asal Sumatra Barat yang dimasak lama dengan santan hingga kering dan termasyhur di dunia adalah?",
            correctAnswer = "Rendang",
            incorrectAnswers = listOf("Gado-gado", "Laksa", "Bakso"),
            explanation = "Dimasak berjam-jam dengan santan, serai, lengkuas, dan rempah, rendang Minangkabau meresap hingga empuk dan tahan lama."
        ),
        "seed_food_9" to LocalizedQuestionCopy(
            questionText = "Minuman kopi berbasis espresso manakah yang diberi susu panas dan lapisan busa susu tebal di atasnya?",
            correctAnswer = "Cappuccino",
            incorrectAnswers = listOf("Americano", "Ristretto", "Cold brew"),
            explanation = "Cappuccino klasik memadukan takaran seimbang antara espresso, susu kukus, dan busa susu halus."
        ),
        "seed_food_10" to LocalizedQuestionCopy(
            questionText = "Tahu dan tempe adalah sumber protein nabati tradisional yang dibuat dari kacang apa?",
            correctAnswer = "Kedelai",
            incorrectAnswers = listOf("Kacang arab", "Lentil", "Kacang hitam"),
            explanation = "Berasal dari Jawa, tempe dibuat dengan memfermentasi kedelai rebus menggunakan ragi kapang Rhizopus."
        ),
        "seed_food_11" to LocalizedQuestionCopy(
            questionText = "Hidangan nasi khas Valencia, Spanyol yang dimasak dalam wajan lebar dangkal dengan kunyit safron adalah?",
            correctAnswer = "Paella",
            incorrectAnswers = listOf("Risotto", "Biryani", "Bibimbap"),
            explanation = "Paella mengambil namanya dari wajan lebar bertelinga dua yang digunakan untuk memasaknya di atas api."
        ),
        "seed_food_12" to LocalizedQuestionCopy(
            questionText = "Rempah berbentuk polong manakah yang menjadi rempah termahal kedua di dunia setelah saffron?",
            correctAnswer = "Vanila",
            incorrectAnswers = listOf("Kapulaga", "Pala", "Cengkih"),
            explanation = "Asli dari Mesoamerika, polong vanila berasal dari anggrek Vanilla planifolia dan memerlukan penyerbukan tangan."
        ),
        "seed_food_13" to LocalizedQuestionCopy(
            questionText = "Istilah kuliner Prancis apakah yang berarti memasak bahan dalam kemasan kedap udara di air bersuhu terkontrol?",
            correctAnswer = "Sous vide",
            incorrectAnswers = listOf("Flambé", "Julienne", "Bain-marie"),
            explanation = "Secara harfiah berarti 'di bawah vakum', teknik sous vide menghasilkan tingkat kematangan merata sempurna."
        ),
        "seed_food_14" to LocalizedQuestionCopy(
            questionText = "Minuman teh fermentasi manakah yang dibuat menggunakan kultur simbiotik bakteri dan ragi (SCOBY)?",
            correctAnswer = "Kombucha",
            incorrectAnswers = listOf("Kefir", "Mate", "Horchata"),
            explanation = "Teh manis yang difermentasi bersama SCOBY menghasilkan minuman berkarbonasi alami dengan rasa segar asam."
        ),
        "seed_food_15" to LocalizedQuestionCopy(
            questionText = "Cokelat dibuat dari biji yang difermentasi dan dipanggang dari pohon tropis apa?",
            correctAnswer = "Kakao",
            incorrectAnswers = listOf("Kopi", "Kola", "Karob"),
            explanation = "Buah Theobroma cacao tumbuh langsung dari batang dan cabang pohon kakao di daerah beriklim tropis."
        ),

        // Masyarakat & Budaya (5..15)
        "seed_soc_5" to LocalizedQuestionCopy(
            questionText = "Kain tradisional Indonesia bermotif rintangan lilin malam yang diakui UNESCO sebagai Warisan Budaya Takbenda adalah?",
            correctAnswer = "Batik",
            incorrectAnswers = listOf("Kente", "Tartan", "Tweed"),
            explanation = "Perajin menorehkan malam panas memakai canting sebelum pencelupan warna untuk membentuk motif penuh makna."
        ),
        "seed_soc_6" to LocalizedQuestionCopy(
            questionText = "Perayaan tahunan apakah yang menandai awal tahun baru pada kalender lunisolar tradisional Tionghoa?",
            correctAnswer = "Tahun Baru Imlek",
            incorrectAnswers = listOf("Festival Pertengahan Musim Gugur", "Songkran", "Nowruz"),
            explanation = "Dikenal juga sebagai Festival Musim Semi, setiap Tahun Baru Imlek bertepatan dengan salah satu dari dua belas shio."
        ),
        "seed_soc_7" to LocalizedQuestionCopy(
            questionText = "Apakah bahasa resmi negara Brasil?",
            correctAnswer = "Bahasa Portugis",
            incorrectAnswers = listOf("Bahasa Spanyol", "Bahasa Prancis", "Bahasa Italia"),
            explanation = "Brasil adalah negara berbahasa Portugis terbesar di dunia sekaligus satu-satunya di benua Amerika."
        ),
        "seed_soc_8" to LocalizedQuestionCopy(
            questionText = "Aliran filsafat Tiongkok kuno manakah yang menekankan hidup selaras dengan aliran alami alam semesta?",
            correctAnswer = "Taoisme",
            incorrectAnswers = listOf("Stoikisme", "Eksistensialisme", "Utilitarianisme"),
            explanation = "Dikaitkan dengan kitab 'Tao Te Ching' karya Laozi, Taoisme mengutamakan kesederhanaan dan tindakan alami."
        ),
        "seed_soc_9" to LocalizedQuestionCopy(
            questionText = "Sistem aksara 24 huruf manakah yang diciptakan di Korea pada abad ke-15 di bawah Raja Sejong?",
            correctAnswer = "Hangul",
            incorrectAnswers = listOf("Kanji", "Kiril", "Devanagari"),
            explanation = "Diresmikan pada 1446, Hangul dirancang secara logis agar seluruh rakyat dapat belajar membaca dengan cepat."
        ),
        "seed_soc_10" to LocalizedQuestionCopy(
            questionText = "Festival Romawi kuno pada pertengahan Desember manakah yang dirayakan dengan pesta dan saling memberi hadiah?",
            correctAnswer = "Saturnalia",
            incorrectAnswers = listOf("Lupercalia", "Beltane", "Samhain"),
            explanation = "Dipersembahkan bagi dewa Saturnus, Saturnalia melonggarkan batas status sosial sementara lewat jamuan meriah."
        ),
        "seed_soc_11" to LocalizedQuestionCopy(
            questionText = "Seni teater bayangan tradisional Indonesia yang diakui UNESCO dan mementaskan epos Ramayana serta Mahabharata adalah?",
            correctAnswer = "Wayang Kulit",
            incorrectAnswers = listOf("Kabuki", "Kathakali", "Bunraku"),
            explanation = "Dimainkan oleh seorang dalang di balik kelir bercahaya, Wayang Kulit memadukan kisah filsafat dan iringan gamelan."
        ),
        "seed_soc_12" to LocalizedQuestionCopy(
            questionText = "Cabang filsafat manakah yang mempelajari asal-usul, hakikat, dan pembenaran pengetahuan?",
            correctAnswer = "Epistemologi",
            incorrectAnswers = listOf("Estetika", "Metafisika", "Ontologi"),
            explanation = "Epistemologi meneliti bagaimana manusia mengetahui sesuatu serta membedakan keyakinan berdasar dari sekadar opini."
        ),
        "seed_soc_13" to LocalizedQuestionCopy(
            questionText = "Kitab hukum Mesopotamia kuno di atas prasasti batu basal yang memuat asas 'mata ganti mata' adalah?",
            correctAnswer = "Kode Hammurabi",
            incorrectAnswers = listOf("Dua Belas Prasasti", "Dekrit Asoka", "Kode Yustinianus"),
            explanation = "Disusun sekitar 1750 SM di Babilonia, Kode Hammurabi memahat 282 pasal hukum dalam aksara paku Akkadia."
        ),
        "seed_soc_14" to LocalizedQuestionCopy(
            questionText = "Seni navigasi tradisional Polinesia yang melintasi Samudra Pasifik berpandukan bintang dan ombak tanpa kompas disebut?",
            correctAnswer = "Wayfinding",
            incorrectAnswers = listOf("Dead reckoning", "Triangulasi laser", "Pemeruman gema"),
            explanation = "Pelaut Polinesia menyeberangi ribuan kilometer lautan lepas dengan kano berlambung ganda tanpa alat magnetik."
        ),
        "seed_soc_15" to LocalizedQuestionCopy(
            questionText = "Dalam budaya Jepang, apakah yang dimaksud dengan 'kintsugi'?",
            correctAnswer = "Memperbaiki keramik pecah dengan pernis emas",
            incorrectAnswers = listOf("Merangkai bunga musiman", "Mengukir stempel kayu", "Menganyam keranjang bambu"),
            explanation = "Kintsugi memandang retakan dan perbaikan sebagai bagian terhormat dari sejarah sebuah benda, bukan cacat yang disembunyikan."
        )
    )
}
