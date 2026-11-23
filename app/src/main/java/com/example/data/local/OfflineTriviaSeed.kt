package com.example.data.local

import com.example.R

object OfflineTriviaSeed {
    private const val SEP = "|||"

    fun encodeIncorrect(answers: List<String>): String = answers.joinToString(SEP)

    fun decodeIncorrect(serialized: String): List<String> =
        if (serialized.isBlank()) emptyList() else serialized.split(SEP)

    fun drawableResByName(name: String?): Int? = when (name) {
        "img_quiz_taj_mahal" -> R.drawable.img_quiz_taj_mahal
        "img_quiz_saturn" -> R.drawable.img_quiz_saturn
        "img_quiz_monarch" -> R.drawable.img_quiz_monarch
        "img_quiz_dna_helix" -> R.drawable.img_quiz_dna_helix
        else -> null
    }

    fun drawableNameByRes(resId: Int?): String? = when (resId) {
        R.drawable.img_quiz_taj_mahal -> "img_quiz_taj_mahal"
        R.drawable.img_quiz_saturn -> "img_quiz_saturn"
        R.drawable.img_quiz_monarch -> "img_quiz_monarch"
        R.drawable.img_quiz_dna_helix -> "img_quiz_dna_helix"
        else -> null
    }

    fun initialSeedQuestions(): List<CachedQuestionEntity> {
        val now = System.currentTimeMillis()
        return pictureQuestions(now) +
            standardQuestions(now) +
            curatedCategorySeeds(now) +
            OfflineTriviaSeedExtra.questions(now) +
            OfflineFactBank.entities(now) +
            OfflineTechnologyBank.entities(now)
    }

    internal fun pictureQuestions(now: Long): List<CachedQuestionEntity> = listOf(
        CachedQuestionEntity(
            id = "pic_taj_mahal",
            category = "geography",
            difficulty = "easy",
            questionText = "Which iconic ivory-white marble mausoleum is depicted in this illustration?",
            correctAnswer = "Taj Mahal",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Hagia Sophia", "Petra Treasury", "Alhambra Palace")),
            pictureQuestion = true,
            imageDrawableName = "img_quiz_taj_mahal",
            explanation = "Commissioned in 1632 in Agra, India, the Taj Mahal sits along the Yamuna River.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_saturn",
            category = "science",
            difficulty = "easy",
            questionText = "Which gas giant in our Solar System is renowned for the prominent ring system shown here?",
            correctAnswer = "Saturn",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Neptune", "Uranus", "Jupiter")),
            pictureQuestion = true,
            imageDrawableName = "img_quiz_saturn",
            explanation = "Saturn's rings span up to 282,000 km across yet are often only tens of meters thick.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_monarch",
            category = "science",
            difficulty = "medium",
            questionText = "Which migratory butterfly species is illustrated resting on the coneflower?",
            correctAnswer = "Monarch Butterfly",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Blue Morpho", "Swallowtail", "Painted Lady")),
            pictureQuestion = true,
            imageDrawableName = "img_quiz_monarch",
            explanation = "Monarch butterflies migrate thousands of kilometers across North America each autumn.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_dna_helix",
            category = "science",
            difficulty = "easy",
            questionText = "Which biological molecule forms this double-helix ladder structure?",
            correctAnswer = "DNA",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Transfer RNA", "ATP Synthase", "Hemoglobin")),
            pictureQuestion = true,
            imageDrawableName = "img_quiz_dna_helix",
            explanation = "Deoxyribonucleic acid (DNA) stores genetic instructions in paired nucleotide bases.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_pyramids_giza",
            category = "history",
            difficulty = "easy",
            questionText = "This desert silhouette represents which Ancient Wonder of the World?",
            correctAnswer = "Great Pyramid of Giza",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Hanging Gardens", "Temple of Artemis", "Ziggurat of Ur")),
            pictureQuestion = true,
            illustrationTag = "pyramids",
            explanation = "Built for Pharaoh Khufu around 2560 BCE, it is the oldest and only surviving Ancient Wonder.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_mount_fuji",
            category = "geography",
            difficulty = "easy",
            questionText = "Which snow-capped stratovolcano—Japan's highest peak—is depicted in this woodblock-style plate?",
            correctAnswer = "Mount Fuji",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Mount Etna", "Mount Kilimanjaro", "Mount Rainier")),
            pictureQuestion = true,
            illustrationTag = "mount_fuji",
            explanation = "Rising to 3,776 meters on Honshu Island, Mount Fuji is a cultural icon featured by Hokusai.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_eiffel_tower",
            category = "arts_and_literature",
            difficulty = "easy",
            questionText = "Built for the 1889 Exposition Universelle, which wrought-iron lattice landmark is shown?",
            correctAnswer = "Eiffel Tower",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Blackpool Tower", "Tokyo Tower", "Space Needle")),
            pictureQuestion = true,
            illustrationTag = "eiffel",
            explanation = "Gustave Eiffel's 330-meter tower on the Champ de Mars was originally intended to stand for 20 years.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_solar_eclipse",
            category = "science",
            difficulty = "medium",
            questionText = "What celestial event produces the luminous solar corona ring illustrated here?",
            correctAnswer = "Total Solar Eclipse",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Lunar Eclipse", "Aurora Borealis", "Planetary Transit")),
            pictureQuestion = true,
            illustrationTag = "eclipse",
            explanation = "During totality, the Moon completely covers the Sun's photosphere, revealing the outer corona.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_parthenon",
            category = "history",
            difficulty = "medium",
            questionText = "Which Doric temple crowning the Athenian Acropolis is illustrated in this architectural plate?",
            correctAnswer = "The Parthenon",
            incorrectAnswersSerialized = encodeIncorrect(listOf("The Pantheon", "Temple of Karnak", "Sagrada Familia")),
            pictureQuestion = true,
            illustrationTag = "parthenon",
            explanation = "Dedicated to Athena in 438 BCE, the Parthenon epitomizes classical Greek Doric architecture.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_prism_spectrum",
            category = "science",
            difficulty = "medium",
            questionText = "Which optical phenomenon is demonstrated when white light splits through a triangular glass prism?",
            correctAnswer = "Dispersion",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Polarization", "Fluorescence", "Total Internal Reflection")),
            pictureQuestion = true,
            illustrationTag = "prism",
            explanation = "Dispersion occurs because different wavelengths of light refract at slightly different angles.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_compass_rose",
            category = "geography",
            difficulty = "easy",
            questionText = "On traditional nautical charts, what is this 8-point directional emblem called?",
            correctAnswer = "Compass Rose",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Astrolabe", "Sextant", "Meridian Arc")),
            pictureQuestion = true,
            illustrationTag = "compass_rose",
            explanation = "The compass rose has oriented mariners and cartographers with cardinal directions since the 14th century.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_trebel_clef",
            category = "music",
            difficulty = "medium",
            questionText = "Which musical notation symbol curls around the G4 line on a standard five-line staff?",
            correctAnswer = "Treble Clef",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Bass Clef", "Alto Clef", "Fermata")),
            pictureQuestion = true,
            illustrationTag = "treble_clef",
            explanation = "Also known as the G clef, it establishes the pitch G above middle C on the second line of the staff.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_colosseum",
            category = "history",
            difficulty = "easy",
            questionText = "Which ancient Roman amphitheater in the heart of Rome is depicted in this illustration?",
            correctAnswer = "Colosseum",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Circus Maximus", "Verona Arena", "Pantheon")),
            pictureQuestion = true,
            illustrationTag = "colosseum",
            explanation = "Completed in 80 CE under Emperor Titus, the Flavian Amphitheater seated roughly 50,000 spectators.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_golden_gate",
            category = "geography",
            difficulty = "easy",
            questionText = "Which iconic International Orange suspension bridge spans the entrance to San Francisco Bay?",
            correctAnswer = "Golden Gate Bridge",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Brooklyn Bridge", "Tower Bridge", "Akashi Kaikyo Bridge")),
            pictureQuestion = true,
            illustrationTag = "golden_gate",
            explanation = "Opened in 1937, the Golden Gate Bridge links the San Francisco Peninsula with Marin County.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_volcano",
            category = "geography",
            difficulty = "easy",
            questionText = "Which geological landform erupts molten magma, volcanic ash, and gases from Earth's crust?",
            correctAnswer = "Volcano",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Geyser", "Atoll", "Glacier")),
            pictureQuestion = true,
            illustrationTag = "volcano",
            explanation = "Once magma reaches Earth's surface through a volcanic vent, geologists refer to it as lava.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_crescent_moon",
            category = "science",
            difficulty = "easy",
            questionText = "When only a thin curved sliver of the Moon appears illuminated in the night sky, which lunar phase is it?",
            correctAnswer = "Crescent Moon",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Full Moon", "Gibbous Moon", "First Quarter")),
            pictureQuestion = true,
            illustrationTag = "crescent_moon",
            explanation = "The crescent phase appears shortly after a New Moon or just before the next New Moon.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_torii_gate",
            category = "society_and_culture",
            difficulty = "medium",
            questionText = "Which traditional vermilion Japanese archway marks the sacred entrance to a Shinto shrine?",
            correctAnswer = "Torii Gate",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Pagoda", "Karesansui", "Shoji Screen")),
            pictureQuestion = true,
            illustrationTag = "torii_gate",
            explanation = "A torii symbolically marks the transition from the everyday world into sacred Shinto ground.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_sundial",
            category = "history",
            difficulty = "medium",
            questionText = "Which ancient timekeeping device uses a gnomon to cast the Sun's shadow across hour markings?",
            correctAnswer = "Sundial",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Astrolabe", "Chronometer", "Orrery")),
            pictureQuestion = true,
            illustrationTag = "sundial",
            explanation = "Used in ancient Egypt and Babylon, sundials indicate apparent solar time via shadow angles.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_lighthouse",
            category = "geography",
            difficulty = "easy",
            questionText = "Which coastal beacon tower warns ships away from reefs and guides mariners safely into harbor?",
            correctAnswer = "Lighthouse",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Semaphore Tower", "Breakwater", "Buoy")),
            pictureQuestion = true,
            illustrationTag = "lighthouse",
            explanation = "Famed since the Pharos of Alexandria, lighthouses focus beams for miles with Fresnel lenses.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_windmill",
            category = "society_and_culture",
            difficulty = "easy",
            questionText = "Which bladed structure is an iconic symbol of land drainage and grain milling in the Netherlands?",
            correctAnswer = "Windmill",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Watermill", "Grain Silo", "Aqueduct")),
            pictureQuestion = true,
            illustrationTag = "windmill",
            explanation = "Historic Dutch windmills pumped water out of low-lying polders to reclaim fertile farmland.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_hourglass",
            category = "general_knowledge",
            difficulty = "easy",
            questionText = "Which sand-filled glass timer measures intervals as grains trickle through a narrow waist?",
            correctAnswer = "Hourglass",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Barometer", "Metronome", "Calorimeter")),
            pictureQuestion = true,
            illustrationTag = "hourglass",
            explanation = "Marine sandglasses were prized at sea from the 14th century because ship motion did not disrupt flow.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_olympic_torch",
            category = "sport_and_leisure",
            difficulty = "easy",
            questionText = "Which ceremonial flame carrier travels in a relay from Olympia to the host city of the Games?",
            correctAnswer = "Olympic Torch",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Laurel Wreath", "Stanley Cup", "Relay Baton")),
            pictureQuestion = true,
            illustrationTag = "olympic_torch",
            explanation = "Introduced for the 1936 Games, the Olympic torch relay symbolizes peace and continuity with ancient Greece.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_chess_knight",
            category = "sport_and_leisure",
            difficulty = "easy",
            questionText = "Which horse-shaped chess piece moves in an L-pattern and can leap over other pieces?",
            correctAnswer = "Knight",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Bishop", "Rook", "Pawn")),
            pictureQuestion = true,
            illustrationTag = "chess_knight",
            explanation = "With every L-shaped move, a knight always lands on a square of the opposite color.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "pic_folding_fan",
            category = "arts_and_literature",
            difficulty = "medium",
            questionText = "Developed in Heian-period Japan, what pleated handheld accessory is known as a sensu?",
            correctAnswer = "Folding Fan",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Paper Parasol", "Obi Sash", "Origami Crane")),
            pictureQuestion = true,
            illustrationTag = "folding_fan",
            explanation = "Japanese folding fans (sensu) spread across Asia and Europe for art, dance, and courtly etiquette.",
            cachedAt = now
        )
    )

    internal fun standardQuestions(now: Long): List<CachedQuestionEntity> = listOf(
        CachedQuestionEntity(
            id = "std_capital_france",
            category = "geography",
            difficulty = "easy",
            questionText = "What is the capital city of France?",
            correctAnswer = "Paris",
            incorrectAnswersSerialized = encodeIncorrect(listOf("London", "Berlin", "Madrid")),
            explanation = "Paris has been the capital of France since the medieval era, famed for art, gastronomy, and architecture.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "std_atomic_hydrogen",
            category = "science",
            difficulty = "easy",
            questionText = "What is the chemical element with atomic number 1?",
            correctAnswer = "Hydrogen",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Helium", "Oxygen", "Carbon")),
            explanation = "Hydrogen is the lightest and most abundant chemical element in the universe.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "std_shakes_romeo",
            category = "arts_and_literature",
            difficulty = "medium",
            questionText = "Which playwright authored the tragic romance 'Romeo and Juliet'?",
            correctAnswer = "William Shakespeare",
            incorrectAnswersSerialized = encodeIncorrect(listOf("Christopher Marlowe", "Geoffrey Chaucer", "John Milton")),
            explanation = "Written in the 1590s, Romeo and Juliet tells the story of two young star-crossed lovers in Verona.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "std_speed_light",
            category = "science",
            difficulty = "hard",
            questionText = "What is the approximate speed of light in a vacuum in kilometers per second?",
            correctAnswer = "300,000 km/s",
            incorrectAnswersSerialized = encodeIncorrect(listOf("150,000 km/s", "450,000 km/s", "1,000,000 km/s")),
            explanation = "Light travels at exactly 299,792,458 meters per second in a vacuum.",
            cachedAt = now
        ),
        CachedQuestionEntity(
            id = "std_ww2_end",
            category = "history",
            difficulty = "easy",
            questionText = "In what year did World War II officially come to an end?",
            correctAnswer = "1945",
            incorrectAnswersSerialized = encodeIncorrect(listOf("1943", "1944", "1946")),
            explanation = "World War II concluded in 1945 following the surrender of Axis powers in Europe and the Pacific.",
            cachedAt = now
        )
    )

    private fun seedItem(
        id: String,
        cat: String,
        diff: String,
        q: String,
        a: String,
        wrong: List<String>,
        exp: String,
        now: Long
    ) = CachedQuestionEntity(
        id = id,
        category = cat,
        difficulty = diff,
        questionText = q,
        correctAnswer = a,
        incorrectAnswersSerialized = encodeIncorrect(wrong),
        pictureQuestion = false,
        explanation = exp,
        cachedAt = now
    )

    private fun curatedCategorySeeds(now: Long): List<CachedQuestionEntity> = listOf(
        seedItem("seed_gk_1", "general_knowledge", "easy", "Which metal is liquid at standard room temperature and pressure?", "Mercury", listOf("Gallium", "Lead", "Tungsten"), "Mercury melts at -38.8 °C, making it the only metallic element liquid at room temperature.", now),
        seedItem("seed_gk_2", "general_knowledge", "easy", "How many keys are on a standard modern grand piano?", "88", listOf("76", "84", "92"), "A standard piano features 52 white keys and 36 black keys spanning over seven octaves.", now),
        seedItem("seed_gk_3", "general_knowledge", "medium", "What does the acronym 'HTTP' stand for on the World Wide Web?", "Hypertext Transfer Protocol", listOf("High Transmission Text Process", "Hyperlink Transport Terminal Protocol", "Host Transfer Type Protocol"), "Tim Berners-Lee designed HTTP at CERN in 1989–1991 to exchange linked web documents.", now),
        seedItem("seed_gk_4", "general_knowledge", "hard", "Which Scandinavian country uses the flag called the Dannebrog, the oldest continuously used state flag?", "Denmark", listOf("Norway", "Sweden", "Finland"), "Denmark's red-and-white Nordic cross flag has been in continuous use since at least the 14th century.", now),
        seedItem("seed_sci_1", "science", "easy", "What is the chemical symbol for the element potassium?", "K", listOf("P", "Pt", "Po"), "The symbol K derives from Neo-Latin 'kalium', rooted in Arabic 'al-qalyah' (plant ashes).", now),
        seedItem("seed_sci_2", "science", "easy", "Which organelle inside human cells is primarily responsible for producing ATP energy?", "Mitochondrion", listOf("Golgi apparatus", "Ribosome", "Lysosome"), "Mitochondria convert nutrients into adenosine triphosphate (ATP) through cellular respiration.", now),
        seedItem("seed_sci_3", "science", "medium", "Approximately how long does light from the Sun take to reach Earth?", "8 minutes", listOf("8 seconds", "42 minutes", "2 hours"), "Traveling 150 million km at ~300,000 km/s, sunlight takes roughly 8 minutes and 20 seconds.", now),
        seedItem("seed_sci_4", "science", "hard", "What is the theoretical boundary around a black hole beyond which nothing can escape?", "Event horizon", listOf("Roche limit", "Chandrasekhar limit", "Oort cloud"), "Once inside the event horizon, the required escape velocity exceeds the speed of light.", now),
        seedItem("seed_his_1", "history", "easy", "In what year did Apollo 11 first land humans on the Moon?", "1969", listOf("1965", "1971", "1973"), "Neil Armstrong and Buzz Aldrin landed on the Sea of Tranquility on July 20, 1969.", now),
        seedItem("seed_his_2", "history", "medium", "The Rosetta Stone helped scholars decipher which ancient writing system?", "Egyptian hieroglyphs", listOf("Cuneiform", "Linear B", "Sanskrit"), "Carved in 196 BCE in three scripts, it gave Jean-François Champollion the key in 1822.", now),
        seedItem("seed_his_3", "history", "easy", "Which 15th-century German inventor introduced movable-type printing to Europe?", "Johannes Gutenberg", listOf("Martin Luther", "Albrecht Dürer", "Nicolaus Copernicus"), "Gutenberg's Mainz press (~1450) dramatically lowered book costs and accelerated the Renaissance.", now),
        seedItem("seed_his_4", "history", "hard", "Which ancient Anatolian empire fought Egypt at the Battle of Kadesh in 1274 BCE?", "The Hittites", listOf("Assyrians", "Minoans", "Phoenicians"), "Ramesses II's Egypt and Muwatalli II's Hittite Empire later signed one of history's earliest peace treaties.", now),
        seedItem("seed_geo_1", "geography", "easy", "Which river flows directly through Cairo, the capital of Egypt?", "The Nile", listOf("The Tigris", "The Congo", "The Danube"), "Flowing roughly 6,650 km north to the Mediterranean, the Nile has sustained Egypt for millennia.", now),
        seedItem("seed_geo_2", "geography", "medium", "Which sovereign nation is completely landlocked inside South Africa?", "Lesotho", listOf("Eswatini", "Botswana", "Malawi"), "Known as the Kingdom in the Sky, Lesotho is an enclave wholly surrounded by South Africa.", now),
        seedItem("seed_geo_3", "geography", "easy", "What is the capital city of Australia?", "Canberra", listOf("Sydney", "Melbourne", "Brisbane"), "Selected in 1908 as a compromise between Sydney and Melbourne, Canberra is a planned capital.", now),
        seedItem("seed_geo_4", "geography", "medium", "Which strait separates New Zealand's North Island and South Island?", "Cook Strait", listOf("Bass Strait", "Torres Strait", "Strait of Magellan"), "Named for Captain James Cook, it links the Tasman Sea with the South Pacific Ocean.", now),
        seedItem("seed_art_1", "arts_and_literature", "easy", "Who painted 'The Starry Night' in 1889?", "Vincent van Gogh", listOf("Claude Monet", "Paul Cézanne", "Pablo Picasso"), "Van Gogh painted the swirling night sky from his asylum window in Saint-Rémy-de-Provence.", now),
        seedItem("seed_art_2", "arts_and_literature", "easy", "Which George Orwell dystopian novel opens with clocks striking thirteen?", "1984", listOf("Brave New World", "Fahrenheit 451", "Animal Farm"), "Published in 1949, '1984' begins on a bright cold day in April as Winston Smith enters Victory Mansions.", now),
        seedItem("seed_art_3", "arts_and_literature", "medium", "In Greek tradition, who is credited with composing the epic poems the 'Iliad' and the 'Odyssey'?", "Homer", listOf("Sophocles", "Virgil", "Euripides"), "Composed in dactylic hexameter around the 8th century BCE, Homer's epics shaped Western literature.", now),
        seedItem("seed_art_4", "arts_and_literature", "medium", "Which Dutch master painted 'Girl with a Pearl Earring' around 1665?", "Johannes Vermeer", listOf("Rembrandt van Rijn", "Frans Hals", "Jan Steen"), "Vermeer's celebrated painting in the Mauritshuis shines with ultramarine pigment and pearl light.", now),
        seedItem("seed_film_1", "film_and_tv", "easy", "Which Studio Ghibli animated film follows Chihiro trapped in a spirit world?", "Spirited Away", listOf("Howl's Moving Castle", "My Neighbor Totoro", "Princess Mononoke"), "Directed by Hayao Miyazaki (2001), 'Spirited Away' won the Academy Award for Best Animated Feature.", now),
        seedItem("seed_film_2", "film_and_tv", "easy", "Who directed the 2019 Oscar-winning South Korean thriller 'Parasite'?", "Bong Joon-ho", listOf("Park Chan-wook", "Lee Chang-dong", "Hirokazu Koreeda"), "Bong Joon-ho's 'Parasite' became the first non-English language film to win Best Picture.", now),
        seedItem("seed_film_3", "film_and_tv", "medium", "In the series 'Breaking Bad', what alias does chemistry teacher Walter White adopt?", "Heisenberg", listOf("Schrödinger", "Planck", "Oppenheimer"), "Walter chose the surname of quantum physicist Werner Heisenberg, famed for the uncertainty principle.", now),
        seedItem("seed_film_4", "film_and_tv", "hard", "Which landmark 1927 silent science-fiction film was directed by Fritz Lang?", "Metropolis", listOf("Nosferatu", "The Cabinet of Dr. Caligari", "Sunrise"), "Fritz Lang's visionary 'Metropolis' pioneered miniature cityscapes and inspired modern sci-fi cinema.", now),
        seedItem("seed_mus_1", "music", "easy", "How many strings does a standard concert violin have?", "4", listOf("5", "6", "8"), "A violin's four strings are tuned in perfect fifths: G3, D4, A4, and E5.", now),
        seedItem("seed_mus_2", "music", "easy", "Which Baroque composer wrote the violin concertos 'The Four Seasons'?", "Antonio Vivaldi", listOf("Johann Sebastian Bach", "Wolfgang Amadeus Mozart", "George Frideric Handel"), "Published in 1725, Vivaldi's four concertos evoke spring birdsong, summer storms, harvest, and winter frost.", now),
        seedItem("seed_mus_3", "music", "medium", "Which jazz trumpeter led the landmark 1959 modal jazz album 'Kind of Blue'?", "Miles Davis", listOf("John Coltrane", "Dizzy Gillespie", "Chet Baker"), "Recorded with John Coltrane and Bill Evans, 'Kind of Blue' became the best-selling jazz album.", now),
        seedItem("seed_mus_4", "music", "hard", "In Western music theory, which interval spans seven semitones?", "Perfect fifth", listOf("Perfect fourth", "Major sixth", "Tritone"), "Spanning seven half-steps (such as C to G), the perfect fifth anchors Western harmony.", now),
        seedItem("seed_spt_1", "sport_and_leisure", "easy", "How many players from one team are on a standard soccer pitch during play?", "11", listOf("9", "10", "12"), "Each side fields 11 players—10 outfielders and 1 goalkeeper—under the Laws of the Game.", now),
        seedItem("seed_spt_2", "sport_and_leisure", "easy", "Which tennis Grand Slam tournament is played on grass courts?", "Wimbledon", listOf("French Open", "US Open", "Australian Open"), "Founded in 1877 at the All England Club, Wimbledon is the sole Grand Slam held on natural grass.", now),
        seedItem("seed_spt_3", "sport_and_leisure", "easy", "In chess, which piece may jump directly over other pieces on the board?", "Knight", listOf("Bishop", "Rook", "Queen"), "Moving in an L-shape, the knight is the only chess piece capable of leaping over other pieces.", now),
        seedItem("seed_spt_4", "sport_and_leisure", "medium", "Which city hosted the first modern Olympic Games in 1896?", "Athens", listOf("Paris", "London", "Rome"), "Organized by Pierre de Coubertin, 241 athletes from 14 nations competed in Athens in April 1896.", now),
        seedItem("seed_food_1", "food_and_drink", "easy", "Which fruit is dried to make traditional raisins?", "Grapes", listOf("Plums", "Figs", "Dates"), "Raisins are sun-dried or dehydrated grapes, whereas dried plums are known as prunes.", now),
        seedItem("seed_food_2", "food_and_drink", "medium", "Saffron threads are harvested from the stigmas of which flower?", "Crocus", listOf("Marigold", "Hibiscus", "Orchid"), "Each Crocus sativus flower yields only three crimson stigmas, hand-picked at dawn.", now),
        seedItem("seed_food_3", "food_and_drink", "easy", "Which Japanese green tea is finely stone-ground from shade-grown leaves?", "Matcha", listOf("Oolong", "Darjeeling", "Rooibos"), "Shading tea bushes boosts chlorophyll and L-theanine, giving matcha vibrant color and umami flavor.", now),
        seedItem("seed_food_4", "food_and_drink", "hard", "Which Italian cheese is cave-aged and pierced so blue-green veins develop inside?", "Gorgonzola", listOf("Pecorino Romano", "Fontina", "Taleggio"), "Hailing from Lombardy and Piedmont, Gorgonzola wheels are pierced with needles so air forms blue veining.", now),
        seedItem("seed_soc_1", "society_and_culture", "easy", "Which Festival of Lights is celebrated across India with clay oil lamps called diyas?", "Diwali", listOf("Holi", "Pongal", "Onam"), "Observed over five autumn days, Diwali symbolizes the victory of light over darkness and knowledge over ignorance.", now),
        seedItem("seed_soc_2", "society_and_culture", "medium", "Which ancient Greek philosopher founded the Academy in Athens?", "Plato", listOf("Socrates", "Aristotle", "Epicurus"), "Around 387 BCE, Plato established the Academy, where Aristotle later studied for twenty years.", now),
        seedItem("seed_soc_3", "society_and_culture", "easy", "What is the traditional Japanese art of paper folding called?", "Origami", listOf("Ikebana", "Kintsugi", "Calligraphy"), "From 'ori' (folding) and 'kami' (paper), origami transforms a single uncut sheet into intricate figures.", now),
        seedItem("seed_soc_4", "society_and_culture", "medium", "In Norse mythology, what is the sacred world tree connecting the Nine Realms?", "Yggdrasil", listOf("Valhalla", "Bifröst", "Midgard"), "Described in the Poetic Edda as an immense sacred ash tree, Yggdrasil links Asgard, Midgard, and the underworld.", now)
    )
}
