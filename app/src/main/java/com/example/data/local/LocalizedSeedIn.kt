package com.example.data.local

object LocalizedSeedIn {
    private val baseQuestions: Map<String, LocalizedQuestionCopy> = mapOf(
        "pic_taj_mahal" to LocalizedQuestionCopy(
            questionText = "Mausoleum marmer putih gading ikonik manakah yang digambarkan dalam ilustrasi ini?",
            correctAnswer = "Taj Mahal",
            incorrectAnswers = listOf("Hagia Sophia", "Petra Treasury", "Istana Alhambra"),
            explanation = "Dibangun mulai tahun 1632 di Agra, India, Taj Mahal berdiri megah di tepi Sungai Yamuna."
        ),
        "pic_saturn" to LocalizedQuestionCopy(
            questionText = "Planet raksasa gas manakah di Tata Surya kita yang terkenal dengan sistem cincin mencolok seperti ini?",
            correctAnswer = "Saturnus",
            incorrectAnswers = listOf("Neptunus", "Uranus", "Jupiter"),
            explanation = "Cincin Saturnus membentang hingga 282.000 km lebarnya, tetapi ketebalannya sering hanya puluhan meter."
        ),
        "pic_monarch" to LocalizedQuestionCopy(
            questionText = "Spesies kupu-kupu migrasi manakah yang diilustrasikan hinggap di bunga coneflower?",
            correctAnswer = "Kupu-kupu Raja",
            incorrectAnswers = listOf("Blue Morpho", "Swallowtail", "Painted Lady"),
            explanation = "Kupu-kupu Raja (Monarch) bermigrasi ribuan kilometer melintasi Amerika Utara setiap musim gugur."
        ),
        "pic_dna_helix" to LocalizedQuestionCopy(
            questionText = "Molekul biologis manakah yang membentuk struktur tangga heliks ganda ini?",
            correctAnswer = "DNA",
            incorrectAnswers = listOf("Transfer RNA", "ATP Sintase", "Hemoglobin"),
            explanation = "Asam deoksiribonukleat (DNA) menyimpan instruksi genetik dalam pasangan basa nukleotida."
        ),
        "pic_pyramids_giza" to LocalizedQuestionCopy(
            questionText = "Siluet gurun ini menggambarkan Keajaiban Dunia Kuno yang mana?",
            correctAnswer = "Piramida Agung Giza",
            incorrectAnswers = listOf("Taman Gantung Babilonia", "Kuil Artemis", "Ziggurat Ur"),
            explanation = "Dibangun untuk Firaun Khufu sekitar 2560 SM, ini adalah keajaiban kuno tertua yang masih bertahan."
        ),
        "pic_mount_fuji" to LocalizedQuestionCopy(
            questionText = "Gunung berapi kerucut bersalju — puncak tertinggi di Jepang — manakah yang tampak pada ilustrasi ini?",
            correctAnswer = "Gunung Fuji",
            incorrectAnswers = listOf("Gunung Etna", "Gunung Kilimanjaro", "Gunung Rainier"),
            explanation = "Menjulang setinggi 3.776 meter di Pulau Honshu, Gunung Fuji adalah ikon budaya karya klasik Hokusai."
        ),
        "pic_eiffel_tower" to LocalizedQuestionCopy(
            questionText = "Dibangun untuk Pameran Dunia 1889, menara kisi besi tempa ikonik manakah yang ditampilkan?",
            correctAnswer = "Menara Eiffel",
            incorrectAnswers = listOf("Menara Blackpool", "Tokyo Tower", "Space Needle"),
            explanation = "Menara setinggi 330 meter rancangan Gustave Eiffel di Champ de Mars awalnya hanya direncanakan berdiri 20 tahun."
        ),
        "pic_solar_eclipse" to LocalizedQuestionCopy(
            questionText = "Peristiwa langit apakah yang menghasilkan cincin korona matahari bercahaya seperti ilustrasi ini?",
            correctAnswer = "Gerhana Matahari Total",
            incorrectAnswers = listOf("Gerhana Bulan", "Aurora Borealis", "Transit Planet"),
            explanation = "Saat totalitas, Bulan menutupi fotosfer Matahari sepenuhnya sehingga korona luar terlihat jelas."
        ),
        "pic_parthenon" to LocalizedQuestionCopy(
            questionText = "Kuil bergaya Doria di puncak Akropolis Athena manakah yang digambarkan dalam pelat arsitektur ini?",
            correctAnswer = "Parthenon",
            incorrectAnswers = listOf("Pantheon", "Kuil Karnak", "Sagrada Familia"),
            explanation = "Didedikasikan untuk dewi Athena pada 438 SM, Parthenon menjadi puncak arsitektur Doria Yunani klasik."
        ),
        "pic_prism_spectrum" to LocalizedQuestionCopy(
            questionText = "Fenomena optik apakah yang terjadi ketika cahaya putih terurai melalui prisma kaca segitiga?",
            correctAnswer = "Dispersi",
            incorrectAnswers = listOf("Polarisasi", "Fluoresensi", "Pemantulan Sempurna"),
            explanation = "Panjang gelombang pendek seperti ungu membelok lebih tajam dibanding merah, membentuk spektrum pelangi."
        ),
        "pic_compass_rose" to LocalizedQuestionCopy(
            questionText = "Pada peta pelayaran tradisional, disebut apakah lambang penunjuk 8 arah mata angin ini?",
            correctAnswer = "Mawar Kompas",
            incorrectAnswers = listOf("Astrolab", "Sekstan", "Busur Meridian"),
            explanation = "Mawar kompas (compass rose) telah memandu pelaut dan kartografer sejak abad ke-14."
        ),
        "pic_trebel_clef" to LocalizedQuestionCopy(
            questionText = "Simbol notasi musik manakah yang melingkari garis nada G4 pada paranada standar?",
            correctAnswer = "Kunci G",
            incorrectAnswers = listOf("Kunci F", "Kunci C", "Fermata"),
            explanation = "Disebut juga treble clef, Kunci G menandai letak nada G di atas C tengah pada garis kedua paranada."
        ),
        "pic_colosseum" to LocalizedQuestionCopy(
            questionText = "Amfiteater Romawi kuno di jantung kota Roma manakah yang digambarkan dalam ilustrasi ini?",
            correctAnswer = "Koloseum",
            incorrectAnswers = listOf("Circus Maximus", "Arena Verona", "Pantheon"),
            explanation = "Selesai dibangun pada 80 M di bawah Kaisar Titus, Amfiteater Flavian mampu menampung sekitar 50.000 penonton."
        ),
        "pic_golden_gate" to LocalizedQuestionCopy(
            questionText = "Jembatan gantung berwarna oranye ikonik yang membentang di Teluk San Francisco manakah ini?",
            correctAnswer = "Jembatan Golden Gate",
            incorrectAnswers = listOf("Jembatan Brooklyn", "Tower Bridge", "Jembatan Akashi Kaikyo"),
            explanation = "Dibuka pada 1937, Jembatan Golden Gate membentang di selat yang menghubungkan Teluk San Francisco ke Samudra Pasifik."
        ),
        "pic_volcano" to LocalizedQuestionCopy(
            questionText = "Bentang alam geologis apakah yang menyemburkan magma cair, abu vulkanik, dan gas dari kerak Bumi?",
            correctAnswer = "Gunung Berapi",
            incorrectAnswers = listOf("Geiser", "Atol", "Gletser"),
            explanation = "Begitu magma mencapai permukaan Bumi melalui kawah gunung berapi, para geolog menyebutnya lava."
        ),
        "pic_crescent_moon" to LocalizedQuestionCopy(
            questionText = "Ketika hanya sebagian tipis melengkung dari Bulan yang tampak bercahaya di langit malam, fase apakah ini?",
            correctAnswer = "Bulan Sabit",
            incorrectAnswers = listOf("Bulan Purnama", "Bulan Cembung", "Kuartal Pertama"),
            explanation = "Fase bulan sabit terjadi sesaat setelah Bulan Baru atau menjelang Bulan Baru berikutnya."
        ),
        "pic_torii_gate" to LocalizedQuestionCopy(
            questionText = "Gerbang tradisional Jepang berwarna merah terang manakah yang menandai pintu masuk kuil Shinto?",
            correctAnswer = "Gerbang Torii",
            incorrectAnswers = listOf("Pagoda", "Karesansui", "Partisi Shoji"),
            explanation = "Torii secara simbolis menandai peralihan dari dunia sehari-hari menuju kawasan suci kuil Shinto."
        ),
        "pic_sundial" to LocalizedQuestionCopy(
            questionText = "Alat penunjuk waktu kuno manakah yang memakai gnomon pembuat bayangan untuk melacak posisi Matahari?",
            correctAnswer = "Jam Matahari",
            incorrectAnswers = listOf("Astrolab", "Kronometer", "Orrery"),
            explanation = "Digunakan di Mesir dan Babilonia kuno, jam matahari membaca waktu surya dari bayangan pada pelat berpenunjuk."
        ),
        "pic_lighthouse" to LocalizedQuestionCopy(
            questionText = "Menara suar pesisir manakah yang memperingatkan kapal dari karang dan memandu pelaut ke pelabuhan?",
            correctAnswer = "Mercusuar",
            incorrectAnswers = listOf("Menara Semafor", "Pemecah Gelombang", "Pelampung"),
            explanation = "Terkenal sejak Mercusuar Iskandariyah, mercusuar memakai lensa Fresnel untuk memancarkan cahaya sejauh puluhan kilometer."
        ),
        "pic_windmill" to LocalizedQuestionCopy(
            questionText = "Bangunan berbaling-baling manakah yang menjadi simbol ikonik pengeringan air dan penggilingan gandum di Belanda?",
            correctAnswer = "Kincir Angin",
            incorrectAnswers = listOf("Kincir Air", "Silo", "Akuaduk"),
            explanation = "Di Belanda, kincir angin bersejarah memompa air keluar dari kawasan polder rendah untuk menciptakan lahan subur."
        ),
        "pic_hourglass" to LocalizedQuestionCopy(
            questionText = "Alat pengukur waktu kaca berisi pasir manakah yang bekerja lewat gravitasi melalui leher sempit?",
            correctAnswer = "Jam Pasir",
            incorrectAnswers = listOf("Barometer", "Metronom", "Kalorimeter"),
            explanation = "Jam pasir laut populer di kapal sejak abad ke-14 karena guncangan ombak tidak mengganggu aliran pasirnya."
        ),
        "pic_olympic_torch" to LocalizedQuestionCopy(
            questionText = "Pembawa api seremonial apakah yang diarak estafet dari Olympia menuju kota tuan rumah Olimpiade?",
            correctAnswer = "Obor Olimpiade",
            incorrectAnswers = listOf("Mahkota Daun Salam", "Piala Stanley", "Tongkat Estafet"),
            explanation = "Diperkenalkan pada Olimpiade 1936, estafet obor melambangkan perdamaian dan kesinambungan dengan Yunani kuno."
        ),
        "pic_chess_knight" to LocalizedQuestionCopy(
            questionText = "Bidak catur berbentuk kuda manakah yang bergerak membentuk huruf L dan dapat melompati bidak lain?",
            correctAnswer = "Kuda",
            incorrectAnswers = listOf("Gajah", "Benteng", "Pion"),
            explanation = "Bidak kuda selalu mendarat pada petak dengan warna yang berlawanan dari petak awalnya."
        ),
        "pic_folding_fan" to LocalizedQuestionCopy(
            questionText = "Dikembangkan di Jepang pada era Heian, aksesori genggam berlipat apakah yang disebut sensu?",
            correctAnswer = "Kipas Lipat",
            incorrectAnswers = listOf("Payung Kertas", "Sabuk Obi", "Bangau Origami"),
            explanation = "Kipas lipat Jepang (sensu) menyebar ke Asia Timur dan Eropa untuk seni, tarian, serta tata krama istana."
        ),
        "seed_gk_1" to LocalizedQuestionCopy(
            questionText = "Logam manakah yang berwujud cair pada suhu dan tekanan ruang standar?",
            correctAnswer = "Raksa",
            incorrectAnswers = listOf("Galium", "Timbal", "Tungsten"),
            explanation = "Raksa (merkuri) meleleh pada -38,8 °C, menjadikannya satu-satunya unsur logam cair pada suhu kamar."
        ),
        "seed_gk_2" to LocalizedQuestionCopy(
            questionText = "Berapa jumlah tuts pada grand piano modern standar?",
            correctAnswer = "88",
            incorrectAnswers = listOf("76", "84", "92"),
            explanation = "Piano standar memiliki 52 tuts putih dan 36 tuts hitam, mencakup lebih dari tujuh oktaf dari A0 hingga C8."
        ),
        "seed_gk_3" to LocalizedQuestionCopy(
            questionText = "Apa kepanjangan dari singkatan 'HTTP' di World Wide Web?",
            correctAnswer = "Hypertext Transfer Protocol",
            incorrectAnswers = listOf("High Transmission Text Process", "Hyperlink Transport Terminal Protocol", "Host Transfer Type Protocol"),
            explanation = "Tim Berners-Lee merancang HTTP di CERN pada 1989–1991 sebagai fondasi pertukaran dokumen web."
        ),
        "seed_gk_4" to LocalizedQuestionCopy(
            questionText = "Negara Skandinavia manakah yang menggunakan bendera Dannebrog, bendera nasional tertua yang masih terus dipakai?",
            correctAnswer = "Denmark",
            incorrectAnswers = listOf("Norwegia", "Swedia", "Finlandia"),
            explanation = "Salib Nordik merah-putih Denmark telah digunakan secara berkelanjutan setidaknya sejak abad ke-14."
        ),
        "seed_sci_1" to LocalizedQuestionCopy(
            questionText = "Apa simbol kimia untuk unsur kalium (potassium)?",
            correctAnswer = "K",
            incorrectAnswers = listOf("P", "Pt", "Po"),
            explanation = "Simbol K berasal dari bahasa Latin Baru 'kalium', yang berakar dari kata Arab 'al-qalyah' (abu tumbuhan)."
        ),
        "seed_sci_2" to LocalizedQuestionCopy(
            questionText = "Bagian sel manusia manakah yang terutama bertugas menghasilkan energi ATP?",
            correctAnswer = "Mitokondria",
            incorrectAnswers = listOf("Badan Golgi", "Ribosom", "Lisosom"),
            explanation = "Mitokondria mengubah nutrisi menjadi adenosina trifosfat (ATP) melalui respirasi seluler."
        ),
        "seed_sci_3" to LocalizedQuestionCopy(
            questionText = "Kira-kira berapa lama cahaya Matahari menempuh perjalanan ke Bumi?",
            correctAnswer = "8 menit",
            incorrectAnswers = listOf("8 detik", "42 menit", "2 jam"),
            explanation = "Dengan kecepatan 299.792 km/dtk melintasi ~150 juta km, cahaya Matahari tiba dalam sekitar 8 menit 20 detik."
        ),
        "seed_sci_4" to LocalizedQuestionCopy(
            questionText = "Apa nama batas teoretis di sekitar lubang hitam yang membuat apa pun tak dapat lolos?",
            correctAnswer = "Horizon peristiwa",
            incorrectAnswers = listOf("Batas Roche", "Batas Chandrasekhar", "Batas Oort"),
            explanation = "Begitu materi atau cahaya melewati horizon peristiwa, kecepatan lepasnya melampaui kecepatan cahaya."
        ),
        "seed_his_1" to LocalizedQuestionCopy(
            questionText = "Pada tahun berapakah misi Apollo 11 pertama kali mendaratkan manusia di Bulan?",
            correctAnswer = "1969",
            incorrectAnswers = listOf("1965", "1971", "1973"),
            explanation = "Neil Armstrong dan Buzz Aldrin mendarat di Tranquility Base pada 20 Juli 1969."
        ),
        "seed_his_2" to LocalizedQuestionCopy(
            questionText = "Batu Rosetta membantu para ahli menguraikan sistem aksara kuno yang mana?",
            correctAnswer = "Hieroglif Mesir",
            incorrectAnswers = listOf("Paku (Cuneiform)", "Linear B", "Sansekerta"),
            explanation = "Dipahat pada 196 SM dalam tiga aksara, Batu Rosetta memberi kunci kepada Jean-François Champollion pada 1822."
        ),
        "seed_his_3" to LocalizedQuestionCopy(
            questionText = "Penemu Jerman abad ke-15 manakah yang memperkenalkan mesin cetak huruf bergerak ke Eropa?",
            correctAnswer = "Johannes Gutenberg",
            incorrectAnswers = listOf("Martin Luther", "Albrecht Dürer", "Nicolaus Copernicus"),
            explanation = "Mesin cetak mekanis Gutenberg di Mainz (~1450) menurunkan biaya buku dan mempercepat era Renaisans."
        ),
        "seed_his_4" to LocalizedQuestionCopy(
            questionText = "Kekaisaran Anatolia kuno manakah yang bertempur melawan Mesir di Pertempuran Kadesh pada 1274 SM?",
            correctAnswer = "Bangsa Het",
            incorrectAnswers = listOf("Asiria", "Minoa", "Fenisia"),
            explanation = "Mesir di bawah Ramses II dan Kekaisaran Het kelak menandatangani salah satu perjanjian damai tertua."
        ),
        "seed_geo_1" to LocalizedQuestionCopy(
            questionText = "Sungai manakah yang mengalir melewati Kairo, ibu kota Mesir?",
            correctAnswer = "Sungai Nil",
            incorrectAnswers = listOf("Sungai Tigris", "Sungai Kongo", "Sungai Donau"),
            explanation = "Membentang sekitar 6.650 km ke Laut Tengah, Sungai Nil telah menopang peradaban Mesir selama ribuan tahun."
        ),
        "seed_geo_2" to LocalizedQuestionCopy(
            questionText = "Negara berdaulat manakah yang wilayahnya sepenuhnya dikelilingi oleh Afrika Selatan?",
            correctAnswer = "Lesotho",
            incorrectAnswers = listOf("Eswatini", "Botswana", "Malawi"),
            explanation = "Dijuluki Kerajaan di Langit, Lesotho adalah negara enklave yang seluruh perbatasannya dikelilingi Afrika Selatan."
        ),
        "seed_geo_3" to LocalizedQuestionCopy(
            questionText = "Apa nama ibu kota negara Australia?",
            correctAnswer = "Canberra",
            incorrectAnswers = listOf("Sydney", "Melbourne", "Brisbane"),
            explanation = "Dipilih pada 1908 sebagai jalan tengah antara Sydney dan Melbourne, Canberra dibangun sebagai ibu kota terencana."
        ),
        "seed_geo_4" to LocalizedQuestionCopy(
            questionText = "Selat manakah yang memisahkan Pulau Utara dan Pulau Selatan Selandia Baru?",
            correctAnswer = "Selat Cook",
            incorrectAnswers = listOf("Selat Bass", "Selat Torres", "Selat Magellan"),
            explanation = "Dinamai menurut Kapten James Cook, Selat Cook menghubungkan Laut Tasman dengan Samudra Pasifik Selatan."
        ),
        "seed_art_1" to LocalizedQuestionCopy(
            questionText = "Siapakah yang melukis 'The Starry Night' pada tahun 1889?",
            correctAnswer = "Vincent van Gogh",
            incorrectAnswers = listOf("Claude Monet", "Paul Cézanne", "Pablo Picasso"),
            explanation = "Van Gogh melukis 'The Starry Night' dari jendela kamar perawatannya di Saint-Rémy-de-Provence."
        ),
        "seed_art_2" to LocalizedQuestionCopy(
            questionText = "Novel distopia karya George Orwell manakah yang dibuka dengan jam berdentang tiga belas kali?",
            correctAnswer = "1984",
            incorrectAnswers = listOf("Brave New World", "Fahrenheit 451", "Animal Farm"),
            explanation = "Terbit pada 1949, '1984' dimulai pada hari April yang cerah namun dingin saat Winston Smith memasuki apartemennya."
        ),
        "seed_art_3" to LocalizedQuestionCopy(
            questionText = "Dalam tradisi Yunani, siapakah penyair yang diyakini menggubah epos 'Iliad' dan 'Odyssey'?",
            correctAnswer = "Homeros",
            incorrectAnswers = listOf("Sofokles", "Vergilius", "Euripides"),
            explanation = "Digubah sekitar abad ke-8 SM, kedua epos karya Homeros membentuk fondasi sastra klasik Barat."
        ),
        "seed_art_4" to LocalizedQuestionCopy(
            questionText = "Maestro Belanda manakah yang melukis 'Gadis Beranting Mutiara' sekitar tahun 1665?",
            correctAnswer = "Johannes Vermeer",
            incorrectAnswers = listOf("Rembrandt van Rijn", "Frans Hals", "Jan Steen"),
            explanation = "Karya terkenal Vermeer di Mauritshuis ini termasyhur berkat sorban biru ultramarin dan kilau mutiaranya."
        ),
        "seed_film_1" to LocalizedQuestionCopy(
            questionText = "Film animasi Studio Ghibli manakah yang mengisahkan Chihiro terjebak di dunia arwah?",
            correctAnswer = "Spirited Away",
            incorrectAnswers = listOf("Howl's Moving Castle", "My Neighbor Totoro", "Princess Mononoke"),
            explanation = "Disutradarai Hayao Miyazaki (2001), 'Spirited Away' memenangkan Piala Oscar untuk Film Animasi Terbaik."
        ),
        "seed_film_2" to LocalizedQuestionCopy(
            questionText = "Siapakah sutradara film thriller Korea Selatan pemenang Oscar 2019 'Parasite'?",
            correctAnswer = "Bong Joon-ho",
            incorrectAnswers = listOf("Park Chan-wook", "Lee Chang-dong", "Hirokazu Kore-eda"),
            explanation = "'Parasite' garapan Bong Joon-ho menjadi film berbahasa non-Inggris pertama yang meraih Best Picture Oscar."
        ),
        "seed_film_3" to LocalizedQuestionCopy(
            questionText = "Dalam serial televisi 'Breaking Bad', apa nama samaran Walter White?",
            correctAnswer = "Heisenberg",
            incorrectAnswers = listOf("Schrödinger", "Planck", "Oppenheimer"),
            explanation = "Walter memilih nama belakang fisikawan kuantum Werner Heisenberg yang terkenal dengan prinsip ketidakpastian."
        ),
        "seed_film_4" to LocalizedQuestionCopy(
            questionText = "Film fiksi ilmiah bisu tahun 1927 manakah yang disutradarai oleh Fritz Lang?",
            correctAnswer = "Metropolis",
            incorrectAnswers = listOf("Nosferatu", "The Cabinet of Dr. Caligari", "Sunrise"),
            explanation = "'Metropolis' karya Fritz Lang memelopori tata kota miniatur dan memengaruhi sinema fiksi ilmiah sepanjang abad."
        ),
        "seed_mus_1" to LocalizedQuestionCopy(
            questionText = "Berapa banyak senar yang dimiliki biola konser standar?",
            correctAnswer = "4",
            incorrectAnswers = listOf("5", "6", "8"),
            explanation = "Keempat senar biola standar disetem dalam interval kuin murni: G3, D4, A4, dan E5."
        ),
        "seed_mus_2" to LocalizedQuestionCopy(
            questionText = "Komponis klasik manakah yang menggubah konserto biola 'Empat Musim' (The Four Seasons)?",
            correctAnswer = "Antonio Vivaldi",
            incorrectAnswers = listOf("Johann Sebastian Bach", "Wolfgang Amadeus Mozart", "George Frideric Handel"),
            explanation = "Diterbitkan pada 1725, empat konserto Vivaldi melukiskan kicau musim semi, badai musim panas, panen, dan es musim dingin."
        ),
        "seed_mus_3" to LocalizedQuestionCopy(
            questionText = "Pemain trompet jazz manakah yang merilis album legendaris tahun 1959 'Kind of Blue'?",
            correctAnswer = "Miles Davis",
            incorrectAnswers = listOf("John Coltrane", "Dizzy Gillespie", "Chet Baker"),
            explanation = "Direkam bersama John Coltrane dan Bill Evans, 'Kind of Blue' membentuk gaya modal jazz modern."
        ),
        "seed_mus_4" to LocalizedQuestionCopy(
            questionText = "Dalam teori musik, interval apakah yang berjarak tujuh semitone?",
            correctAnswer = "Kuin murni",
            incorrectAnswers = listOf("Kuart murni", "Sekst mayor", "Triton"),
            explanation = "Kuin murni (seperti nada C ke G) berjarak tujuh setengah-langkah dan menjadi tulang punggung harmoni."
        ),
        "seed_spt_1" to LocalizedQuestionCopy(
            questionText = "Berapa jumlah pemain dari satu tim di lapangan sepak bola standar saat bertanding?",
            correctAnswer = "11",
            incorrectAnswers = listOf("9", "10", "12"),
            explanation = "Setiap tim menurunkan 11 pemain — 10 pemain lapangan dan 1 penjaga gawang — sesuai aturan resmi."
        ),
        "seed_spt_2" to LocalizedQuestionCopy(
            questionText = "Turnamen tenis Grand Slam manakah yang dimainkan di lapangan rumput?",
            correctAnswer = "Wimbledon",
            incorrectAnswers = listOf("Roland-Garros", "US Open", "Australian Open"),
            explanation = "Didirikan pada 1877 di All England Club, Wimbledon adalah satu-satunya Grand Slam di lapangan rumput alami."
        ),
        "seed_spt_3" to LocalizedQuestionCopy(
            questionText = "Dalam permainan catur, bidak manakah yang boleh melompati bidak lain di papan?",
            correctAnswer = "Kuda",
            incorrectAnswers = listOf("Gajah", "Benteng", "Menteri"),
            explanation = "Bergerak membentuk huruf L, kuda adalah satu-satunya buah catur yang dapat melompati bidak lain."
        ),
        "seed_spt_4" to LocalizedQuestionCopy(
            questionText = "Kota manakah yang menjadi tuan rumah Olimpiade modern pertama pada tahun 1896?",
            correctAnswer = "Athena",
            incorrectAnswers = listOf("Paris", "London", "Roma"),
            explanation = "Digagas oleh Pierre de Coubertin, 241 atlet dari 14 negara berlaga di Athena pada April 1896."
        ),
        "seed_food_1" to LocalizedQuestionCopy(
            questionText = "Buah apakah yang dikeringkan untuk membuat kismis tradisional?",
            correctAnswer = "Anggur",
            incorrectAnswers = listOf("Plum", "Ara", "Kurma"),
            explanation = "Kismis dibuat dari buah anggur yang dijemur atau didehidrasi, sedangkan plum kering disebut prune."
        ),
        "seed_food_2" to LocalizedQuestionCopy(
            questionText = "Rempah kunyit safron (saffron) dipanen dari kepala putik bunga apa?",
            correctAnswer = "Crocus",
            incorrectAnswers = listOf("Marigold", "Kembang Sepatu", "Anggrek"),
            explanation = "Setiap bunga Crocus sativus hanya menghasilkan tiga helai putik merah tua yang dipetik dengan tangan saat fajar."
        ),
        "seed_food_3" to LocalizedQuestionCopy(
            questionText = "Teh hijau Jepang manakah yang digiling halus dari daun teh yang diteduhkan sebelum panen?",
            correctAnswer = "Matcha",
            incorrectAnswers = listOf("Oolong", "Darjeeling", "Rooibos"),
            explanation = "Peneduhan meningkatkan klorofil dan L-teanin, memberi matcha warna hijau cerah dan rasa umami khas."
        ),
        "seed_food_4" to LocalizedQuestionCopy(
            questionText = "Keju Italia manakah yang diperam di gua dan ditusuk untuk menumbuhkan urat biru kehijauan?",
            correctAnswer = "Gorgonzola",
            incorrectAnswers = listOf("Pecorino Romano", "Fontina", "Taleggio"),
            explanation = "Berasal dari Lombardy dan Piedmont, Gorgonzola ditusuk batang logam agar udara masuk membentuk urat biru."
        ),
        "seed_soc_1" to LocalizedQuestionCopy(
            questionText = "Festival cahaya manakah yang dirayakan di seluruh India dengan lampu minyak tanah liat bernama diya?",
            correctAnswer = "Diwali",
            incorrectAnswers = listOf("Holi", "Pongal", "Onam"),
            explanation = "Dirayakan selama lima hari pada musim gugur, Diwali melambangkan kemenangan terang atas kegelapan."
        ),
        "seed_soc_2" to LocalizedQuestionCopy(
            questionText = "Filsuf Yunani kuno manakah yang mendirikan Akademia di Athena?",
            correctAnswer = "Plato",
            incorrectAnswers = listOf("Socrates", "Aristoteles", "Epikuros"),
            explanation = "Sekitar 387 SM, Plato mendirikan Akademia, tempat Aristoteles kelak belajar selama dua puluh tahun."
        ),
        "seed_soc_3" to LocalizedQuestionCopy(
            questionText = "Disebut apakah seni tradisional Jepang melipat kertas?",
            correctAnswer = "Origami",
            incorrectAnswers = listOf("Ikebana", "Kintsugi", "Kaligrafi"),
            explanation = "Dari kata 'ori' (melipat) dan 'kami' (kertas), origami mengubah selembar kertas utuh menjadi aneka bentuk."
        ),
        "seed_soc_4" to LocalizedQuestionCopy(
            questionText = "Dalam mitologi Nordik, apa nama pohon dunia yang menghubungkan sembilan alam?",
            correctAnswer = "Yggdrasil",
            incorrectAnswers = listOf("Valhalla", "Bifröst", "Midgard"),
            explanation = "Digambarkan dalam Edda Puitis sebagai pohon ash suci raksasa, Yggdrasil menghubungkan Asgard, Midgard, dan alam bawah."
        ),
        "std_capital_france" to LocalizedQuestionCopy(
            questionText = "Apa nama ibu kota negara Prancis?",
            correctAnswer = "Paris",
            incorrectAnswers = listOf("London", "Berlin", "Madrid"),
            explanation = "Paris menjadi ibu kota Prancis sejak abad pertengahan dan termasyhur karena seni, kuliner, serta arsitekturnya."
        ),
        "std_atomic_hydrogen" to LocalizedQuestionCopy(
            questionText = "Unsur kimia manakah yang memiliki nomor atom 1?",
            correctAnswer = "Hidrogen",
            incorrectAnswers = listOf("Helium", "Oksigen", "Karbon"),
            explanation = "Hidrogen merupakan unsur kimia paling ringan sekaligus paling melimpah di alam semesta."
        ),
        "std_shakes_romeo" to LocalizedQuestionCopy(
            questionText = "Siapakah penulis naskah drama tragedi romansa 'Romeo dan Juliet'?",
            correctAnswer = "William Shakespeare",
            incorrectAnswers = listOf("Christopher Marlowe", "Geoffrey Chaucer", "John Milton"),
            explanation = "Ditulis pada era 1590-an, 'Romeo dan Juliet' mengisahkan sepasang kekasih muda di Verona."
        ),
        "std_speed_light" to LocalizedQuestionCopy(
            questionText = "Berapakah perkiraan kecepatan cahaya di ruang hampa dalam kilometer per detik?",
            correctAnswer = "300.000 km/detik",
            incorrectAnswers = listOf("150.000 km/detik", "450.000 km/detik", "1.000.000 km/detik"),
            explanation = "Cahaya merambat dengan kecepatan tepat 299.792.458 meter per detik dalam ruang hampa."
        ),
        "std_ww2_end" to LocalizedQuestionCopy(
            questionText = "Pada tahun berapakah Perang Dunia II berakhir secara resmi?",
            correctAnswer = "1945",
            incorrectAnswers = listOf("1943", "1944", "1946"),
            explanation = "Perang Dunia II berakhir pada 1945 setelah menyerahnya blok Poros di Eropa dan Pasifik."
        )
    )

    val questions: Map<String, LocalizedQuestionCopy> by lazy {
        baseQuestions + LocalizedSeedInExtra.questions
    }
}
