package com.example.data.local

object LocalizedSeedEs {
    private val baseQuestions: Map<String, LocalizedQuestionCopy> = mapOf(
        "pic_taj_mahal" to LocalizedQuestionCopy(
            questionText = "¿Qué emblemático mausoleo de mármol blanco marfil aparece en esta ilustración?",
            correctAnswer = "Taj Mahal",
            incorrectAnswers = listOf("Santa Sofía", "Tesoro de Petra", "Palacio de la Alhambra"),
            explanation = "Encargado en 1632 en Agra, India, el Taj Mahal se alza a orillas del río Yamuna."
        ),
        "pic_saturn" to LocalizedQuestionCopy(
            questionText = "¿Qué gigante gaseoso de nuestro Sistema Solar es famoso por el prominente sistema de anillos mostrado aquí?",
            correctAnswer = "Saturno",
            incorrectAnswers = listOf("Neptuno", "Urano", "Júpiter"),
            explanation = "Los anillos de Saturno se extienden hasta 282,000 km de ancho, aunque suelen tener apenas decenas de metros de grosor."
        ),
        "pic_monarch" to LocalizedQuestionCopy(
            questionText = "¿Qué especie de mariposa migratoria aparece ilustrada sobre la equinácea?",
            correctAnswer = "Mariposa Monarca",
            incorrectAnswers = listOf("Morfo Azul", "Cola de Golondrina", "Vanesa de los Cardos"),
            explanation = "Las mariposas monarca migran miles de kilómetros por América del Norte cada otoño."
        ),
        "pic_dna_helix" to LocalizedQuestionCopy(
            questionText = "¿Qué molécula biológica forma esta estructura de doble hélice?",
            correctAnswer = "ADN",
            incorrectAnswers = listOf("ARN de transferencia", "ATP sintasa", "Hemoglobina"),
            explanation = "El ácido desoxirribonucleico (ADN) almacena las instrucciones genéticas en pares de bases de nucleótidos."
        ),
        "pic_pyramids_giza" to LocalizedQuestionCopy(
            questionText = "Esta silueta del desierto representa ¿qué Maravilla del Mundo Antiguo?",
            correctAnswer = "Gran Pirámide de Guiza",
            incorrectAnswers = listOf("Jardines Colgantes", "Templo de Artemisa", "Zigurat de Ur"),
            explanation = "Construida para el faraón Keops hacia el 2560 a. C., es la más antigua y única maravilla antigua que sobrevive."
        ),
        "pic_mount_fuji" to LocalizedQuestionCopy(
            questionText = "¿Qué estratovolcán nevado —la cumbre más alta de Japón— aparece en esta lámina estilo grabado?",
            correctAnswer = "Monte Fuji",
            incorrectAnswers = listOf("Monte Etna", "Monte Kilimanjaro", "Monte Rainier"),
            explanation = "Con 3,776 metros en la isla de Honshu, el Monte Fuji es un icono cultural inmortalizado por Hokusai."
        ),
        "pic_eiffel_tower" to LocalizedQuestionCopy(
            questionText = "Construida para la Exposición Universal de 1889, ¿qué torre de hierro forjado se muestra?",
            correctAnswer = "Torre Eiffel",
            incorrectAnswers = listOf("Torre de Blackpool", "Torre de Tokio", "Aguja Espacial"),
            explanation = "La torre de 330 metros diseñada por Gustave Eiffel en el Campo de Marte iba a permanecer solo 20 años."
        ),
        "pic_solar_eclipse" to LocalizedQuestionCopy(
            questionText = "¿Qué evento celeste produce la luminosa corona solar ilustrada aquí?",
            correctAnswer = "Eclipse Solar Total",
            incorrectAnswers = listOf("Eclipse Lunar", "Aurora Boreal", "Tránsito Planetario"),
            explanation = "Durante la totalidad, la Luna cubre por completo la fotosfera del Sol y revela su corona exterior."
        ),
        "pic_parthenon" to LocalizedQuestionCopy(
            questionText = "¿Qué templo dórico en la cima de la Acrópolis de Atenas ilustra esta lámina arquitectónica?",
            correctAnswer = "El Partenón",
            incorrectAnswers = listOf("El Panteón", "Templo de Karnak", "Sagrada Familia"),
            explanation = "Dedicado a Atenea en el 438 a. C., el Partenón representa la cumbre de la arquitectura dórica griega."
        ),
        "pic_prism_spectrum" to LocalizedQuestionCopy(
            questionText = "¿Qué fenómeno óptico ocurre cuando la luz blanca se separa a través de un prisma triangular?",
            correctAnswer = "Dispersión",
            incorrectAnswers = listOf("Polarización", "Fluorescencia", "Reflexión interna total"),
            explanation = "Las longitudes de onda cortas como el violeta se refractan más que el rojo, formando el espectro visible."
        ),
        "pic_compass_rose" to LocalizedQuestionCopy(
            questionText = "En las cartas náuticas tradicionales, ¿cómo se llama este emblema direccional de 8 puntas?",
            correctAnswer = "Rosa de los Vientos",
            incorrectAnswers = listOf("Astrolabio", "Sextante", "Arco Meridiano"),
            explanation = "La rosa de los vientos ha orientado a navegantes y cartógrafos con puntos cardinales desde el siglo XIV."
        ),
        "pic_trebel_clef" to LocalizedQuestionCopy(
            questionText = "¿Qué símbolo de notación musical gira alrededor de la línea Sol4 del pentagrama?",
            correctAnswer = "Clave de Sol",
            incorrectAnswers = listOf("Clave de Fa", "Clave de Do", "Calderón"),
            explanation = "La clave de Sol fija la nota Sol por encima del Do central en la segunda línea del pentagrama."
        ),
        "pic_colosseum" to LocalizedQuestionCopy(
            questionText = "¿Qué antiguo anfiteatro romano en el corazón de Roma aparece en esta ilustración?",
            correctAnswer = "Coliseo",
            incorrectAnswers = listOf("Circo Máximo", "Arena de Verona", "Panteón"),
            explanation = "Completado en el año 80 d. C. bajo el emperador Tito, el Anfiteatro Flavio albergaba unos 50,000 espectadores."
        ),
        "pic_golden_gate" to LocalizedQuestionCopy(
            questionText = "¿Qué puente colgante de color Naranja Internacional cruza la bahía de San Francisco?",
            correctAnswer = "Puente Golden Gate",
            incorrectAnswers = listOf("Puente de Brooklyn", "Puente de la Torre", "Puente Akashi Kaikyo"),
            explanation = "Inaugurado en 1937, el Golden Gate conecta la bahía de San Francisco con el océano Pacífico."
        ),
        "pic_volcano" to LocalizedQuestionCopy(
            questionText = "¿Qué formación geológica expulsa magma fundido, ceniza volcánica y gases desde la corteza terrestre?",
            correctAnswer = "Volcán",
            incorrectAnswers = listOf("Géiser", "Atolón", "Glaciar"),
            explanation = "Cuando el magma alcanza la superficie a través de un cráter volcánico, los geólogos lo llaman lava."
        ),
        "pic_crescent_moon" to LocalizedQuestionCopy(
            questionText = "Cuando solo una fina franja curva de la Luna aparece iluminada en el cielo nocturno, ¿qué fase lunar es?",
            correctAnswer = "Luna Creciente",
            incorrectAnswers = listOf("Luna Llena", "Luna Gibosa", "Cuarto Menguante"),
            explanation = "La fase creciente ocurre poco después de la Luna Nueva o justo antes de la siguiente."
        ),
        "pic_torii_gate" to LocalizedQuestionCopy(
            questionText = "¿Qué tradicional arco japonés de color bermellón marca la entrada a un santuario sintoísta?",
            correctAnswer = "Puerta Torii",
            incorrectAnswers = listOf("Pagoda", "Karesansui", "Biombo Shoji"),
            explanation = "Un torii simboliza la transición desde el mundo cotidiano hacia el recinto sagrado sintoísta."
        ),
        "pic_sundial" to LocalizedQuestionCopy(
            questionText = "¿Qué antiguo instrumento de medición del tiempo usa un gnomon para seguir la sombra del Sol?",
            correctAnswer = "Reloj de sol",
            incorrectAnswers = listOf("Astrolabio", "Cronómetro", "Planetario"),
            explanation = "Usado en el antiguo Egipto y Babilonia, indica la hora solar mediante la sombra proyectada sobre un cuadrante."
        ),
        "pic_lighthouse" to LocalizedQuestionCopy(
            questionText = "¿Qué torre luminosa costera advierte a los barcos sobre arrecifes y los guía hacia el puerto?",
            correctAnswer = "Faro",
            incorrectAnswers = listOf("Torre semafórica", "Rompeolas", "Boya"),
            explanation = "Famosos desde el Faro de Alejandría, los faros usan lentes de Fresnel para proyectar haces a kilómetros."
        ),
        "pic_windmill" to LocalizedQuestionCopy(
            questionText = "¿Qué estructura con aspas es símbolo icónico del drenaje de agua y la molienda de grano en Países Bajos?",
            correctAnswer = "Molino de viento",
            incorrectAnswers = listOf("Noria hidráulica", "Silo", "Acueducto"),
            explanation = "En los Países Bajos, los molinos históricos bombeaban agua de los pólderes bajos para ganar tierras fértiles."
        ),
        "pic_hourglass" to LocalizedQuestionCopy(
            questionText = "¿Qué reloj de vidrio relleno de arena mide intervalos gracias a la gravedad a través de un estrecho cuello?",
            correctAnswer = "Reloj de arena",
            incorrectAnswers = listOf("Barómetro", "Metrónomo", "Calorímetro"),
            explanation = "Los relojes de arena marinos se popularizaron en el siglo XIV porque el balanceo del mar no los alteraba."
        ),
        "pic_olympic_torch" to LocalizedQuestionCopy(
            questionText = "¿Qué portador de llama ceremonial se lleva en relevo desde Olimpia hasta la ciudad sede de los Juegos?",
            correctAnswer = "Antorcha Olímpica",
            incorrectAnswers = listOf("Corona de laurel", "Copa Stanley", "Testigo"),
            explanation = "Introducida en 1936, la antorcha olímpica simboliza la paz y la continuidad con la Antigua Grecia."
        ),
        "pic_chess_knight" to LocalizedQuestionCopy(
            questionText = "¿Qué pieza de ajedrez con forma de cabeza equina se mueve en L y puede saltar sobre otras piezas?",
            correctAnswer = "Caballo",
            incorrectAnswers = listOf("Alfil", "Torre", "Peón"),
            explanation = "El caballo siempre aterriza en una casilla de color opuesto a aquella desde la que partió."
        ),
        "pic_folding_fan" to LocalizedQuestionCopy(
            questionText = "Desarrollado en Japón durante el período Heian, ¿qué accesorio plegable de mano se conoce como sensu?",
            correctAnswer = "Abanico plegable",
            incorrectAnswers = listOf("Sombrilla", "Cinturón Obi", "Grulla de origami"),
            explanation = "Los abanicos plegables japoneses (sensu) se difundieron por Asia y Europa en el arte, la danza y la etiqueta."
        ),
        "seed_gk_1" to LocalizedQuestionCopy(
            questionText = "¿Qué metal es líquido a temperatura y presión ambiente estándar?",
            correctAnswer = "Mercurio",
            incorrectAnswers = listOf("Galio", "Plomo", "Tungsteno"),
            explanation = "El mercurio se funde a -38.8 °C, siendo el único elemento metálico líquido a temperatura ambiente."
        ),
        "seed_gk_2" to LocalizedQuestionCopy(
            questionText = "¿Cuántas teclas tiene un piano de cola moderno estándar?",
            correctAnswer = "88",
            incorrectAnswers = listOf("76", "84", "92"),
            explanation = "Un piano estándar cuenta con 52 teclas blancas y 36 negras, cubriendo algo más de siete octavas."
        ),
        "seed_gk_3" to LocalizedQuestionCopy(
            questionText = "¿Qué significan las siglas 'HTTP' en la World Wide Web?",
            correctAnswer = "Hypertext Transfer Protocol",
            incorrectAnswers = listOf("High Transmission Text Process", "Hyperlink Transport Terminal Protocol", "Host Transfer Type Protocol"),
            explanation = "Tim Berners-Lee diseñó HTTP en el CERN entre 1989 y 1991 como base para intercambiar documentos web."
        ),
        "seed_gk_4" to LocalizedQuestionCopy(
            questionText = "¿Qué país escandinavo usa la bandera llamada Dannebrog, la bandera estatal más antigua en uso continuo?",
            correctAnswer = "Dinamarca",
            incorrectAnswers = listOf("Noruega", "Suecia", "Finlandia"),
            explanation = "La cruz nórdica roja y blanca de Dinamarca se utiliza de forma continua al menos desde el siglo XIV."
        ),
        "seed_sci_1" to LocalizedQuestionCopy(
            questionText = "¿Cuál es el símbolo químico del elemento potasio?",
            correctAnswer = "K",
            incorrectAnswers = listOf("P", "Pt", "Po"),
            explanation = "El símbolo K proviene del neolatín 'kalium', derivado del término árabe 'al-qalyah' (cenizas vegetales)."
        ),
        "seed_sci_2" to LocalizedQuestionCopy(
            questionText = "¿Qué orgánulo de la célula humana produce principalmente la energía ATP?",
            correctAnswer = "Mitocondria",
            incorrectAnswers = listOf("Aparato de Golgi", "Ribosoma", "Lisosoma"),
            explanation = "Las mitocondrias convierten nutrientes en trifosfato de adenosina (ATP) mediante la respiración celular."
        ),
        "seed_sci_3" to LocalizedQuestionCopy(
            questionText = "¿Aproximadamente cuánto tarda la luz del Sol en llegar a la Tierra?",
            correctAnswer = "8 minutos",
            incorrectAnswers = listOf("8 segundos", "42 minutos", "2 horas"),
            explanation = "A 299,792 km/s a lo largo de unos 150 millones de km, la luz solar tarda unos 8 minutos y 20 segundos."
        ),
        "seed_sci_4" to LocalizedQuestionCopy(
            questionText = "¿Cómo se llama la frontera teórica alrededor de un agujero negro de la que nada puede escapar?",
            correctAnswer = "Horizonte de sucesos",
            incorrectAnswers = listOf("Límite de Roche", "Límite de Chandrasekhar", "Nube de Oort"),
            explanation = "Al cruzar el horizonte de sucesos, la velocidad de escape supera la velocidad de la luz."
        ),
        "seed_his_1" to LocalizedQuestionCopy(
            questionText = "¿En qué año la misión Apolo 11 llevó por primera vez humanos a la Luna?",
            correctAnswer = "1969",
            incorrectAnswers = listOf("1965", "1971", "1973"),
            explanation = "Neil Armstrong y Buzz Aldrin alunizaron en el Mar de la Tranquilidad el 20 de julio de 1969."
        ),
        "seed_his_2" to LocalizedQuestionCopy(
            questionText = "La Piedra de Rosetta ayudó a descifrar ¿qué sistema de escritura antiguo?",
            correctAnswer = "Jeroglíficos egipcios",
            incorrectAnswers = listOf("Cuneiforme", "Lineal B", "Sánscrito"),
            explanation = "Grabada en 196 a. C. en tres escrituras, dio a Jean-François Champollion la clave en 1822."
        ),
        "seed_his_3" to LocalizedQuestionCopy(
            questionText = "¿Qué inventor alemán del siglo XV introdujo la imprenta de tipos móviles en Europa?",
            correctAnswer = "Johannes Gutenberg",
            incorrectAnswers = listOf("Martín Lutero", "Alberto Durero", "Nicolás Copérnico"),
            explanation = "La prensa mecánica de Gutenberg en Maguncia (~1450) abarató los libros e impulsó el Renacimiento."
        ),
        "seed_his_4" to LocalizedQuestionCopy(
            questionText = "¿Qué antiguo imperio de Anatolia luchó contra Egipto en la Batalla de Qadesh en 1274 a. C.?",
            correctAnswer = "Los Hititas",
            incorrectAnswers = listOf("Los Asirios", "Los Minoicos", "Los Fenicios"),
            explanation = "Egipto bajo Ramsés II y el Imperio Hitita de Muwatalli II firmaron uno de los primeros tratados de paz."
        ),
        "seed_geo_1" to LocalizedQuestionCopy(
            questionText = "¿Qué río atraviesa El Cairo, la capital de Egipto?",
            correctAnswer = "El Nilo",
            incorrectAnswers = listOf("El Tigris", "El Congo", "El Danubio"),
            explanation = "Con unos 6,650 km hacia el Mediterráneo, el Nilo ha sustentado la civilización egipcia durante milenios."
        ),
        "seed_geo_2" to LocalizedQuestionCopy(
            questionText = "¿Qué nación soberana está completamente rodeada por Sudáfrica?",
            correctAnswer = "Lesoto",
            incorrectAnswers = listOf("Esuatini", "Botsuana", "Malaui"),
            explanation = "Conocido como el Reino en el Cielo, Lesoto es un enclave rodeado enteramente por Sudáfrica."
        ),
        "seed_geo_3" to LocalizedQuestionCopy(
            questionText = "¿Cuál es la ciudad capital de Australia?",
            correctAnswer = "Canberra",
            incorrectAnswers = listOf("Sídney", "Melbourne", "Brisbane"),
            explanation = "Elegida en 1908 como compromiso entre Sídney y Melbourne, Canberra fue diseñada como capital nacional."
        ),
        "seed_geo_4" to LocalizedQuestionCopy(
            questionText = "¿Qué estrecho separa las islas Norte y Sur de Nueva Zelanda?",
            correctAnswer = "Estrecho de Cook",
            incorrectAnswers = listOf("Estrecho de Bass", "Estrecho de Torres", "Estrecho de Magallanes"),
            explanation = "Nombrado por el capitán James Cook, conecta el mar de Tasmania con el océano Pacífico Sur."
        ),
        "seed_art_1" to LocalizedQuestionCopy(
            questionText = "¿Quién pintó 'La noche estrellada' en 1889?",
            correctAnswer = "Vincent van Gogh",
            incorrectAnswers = listOf("Claude Monet", "Paul Cézanne", "Pablo Picasso"),
            explanation = "Van Gogh pintó 'La noche estrellada' desde la ventana de su habitación en Saint-Rémy-de-Provence."
        ),
        "seed_art_2" to LocalizedQuestionCopy(
            questionText = "¿Qué novela distópica de George Orwell comienza con relojes dando las trece?",
            correctAnswer = "1984",
            incorrectAnswers = listOf("Un mundo feliz", "Fahrenheit 451", "Rebelión en la granja"),
            explanation = "Publicada en 1949, '1984' abre en un frío día de abril cuando Winston Smith entra en las Mansiones Victoria."
        ),
        "seed_art_3" to LocalizedQuestionCopy(
            questionText = "En la tradición griega, ¿a quién se atribuyen los poemas épicos 'Ilíada' y 'Odisea'?",
            correctAnswer = "Homero",
            incorrectAnswers = listOf("Sófocles", "Virgilio", "Eurípides"),
            explanation = "Compuestos en hexámetro dactílico hacia el siglo VIII a. C., los poemas de Homero moldearon la literatura clásica."
        ),
        "seed_art_4" to LocalizedQuestionCopy(
            questionText = "¿Qué maestro neerlandés pintó 'La joven de la perla' hacia 1665?",
            correctAnswer = "Johannes Vermeer",
            incorrectAnswers = listOf("Rembrandt van Rijn", "Frans Hals", "Jan Steen"),
            explanation = "La célebre obra de Vermeer en el Mauritshuis destaca por su turbante ultramar y el reflejo de la perla."
        ),
        "seed_film_1" to LocalizedQuestionCopy(
            questionText = "¿Qué película animada de Studio Ghibli sigue a Chihiro atrapada en un mundo espiritual?",
            correctAnswer = "El viaje de Chihiro",
            incorrectAnswers = listOf("El castillo ambulante", "Mi vecino Totoro", "La princesa Mononoke"),
            explanation = "Dirigida por Hayao Miyazaki (2001), ganó el Óscar a la Mejor Película de Animación."
        ),
        "seed_film_2" to LocalizedQuestionCopy(
            questionText = "¿Quién dirigió el thriller surcoreano ganador del Óscar 2019 'Parásitos'?",
            correctAnswer = "Bong Joon-ho",
            incorrectAnswers = listOf("Park Chan-wook", "Lee Chang-dong", "Hirokazu Koreeda"),
            explanation = "'Parásitos' de Bong Joon-ho fue la primera película de habla no inglesa en ganar el Óscar a Mejor Película."
        ),
        "seed_film_3" to LocalizedQuestionCopy(
            questionText = "En la serie 'Breaking Bad', ¿cuál es el alias de Walter White?",
            correctAnswer = "Heisenberg",
            incorrectAnswers = listOf("Schrödinger", "Planck", "Oppenheimer"),
            explanation = "Walter eligió el apellido del físico cuántico Werner Heisenberg, famoso por el principio de incertidumbre."
        ),
        "seed_film_4" to LocalizedQuestionCopy(
            questionText = "¿Qué película muda de ciencia ficción de 1927 fue dirigida por Fritz Lang?",
            correctAnswer = "Metrópolis",
            incorrectAnswers = listOf("Nosferatu", "El gabinete del doctor Caligari", "Amanecer"),
            explanation = "La visionaria 'Metrópolis' de Fritz Lang revolucionó las maquetas urbanas e inspiró un siglo de ciencia ficción."
        ),
        "seed_mus_1" to LocalizedQuestionCopy(
            questionText = "¿Cuántas cuerdas tiene un violín de concierto estándar?",
            correctAnswer = "4",
            incorrectAnswers = listOf("5", "6", "8"),
            explanation = "Las cuatro cuerdas de un violín estándar se afinan en quintas justas: Sol3, Re4, La4 y Mi5."
        ),
        "seed_mus_2" to LocalizedQuestionCopy(
            questionText = "¿Qué compositor clásico escribió los conciertos para violín 'Las cuatro estaciones'?",
            correctAnswer = "Antonio Vivaldi",
            incorrectAnswers = listOf("Johann Sebastian Bach", "Wolfgang Amadeus Mozart", "Georg Friedrich Händel"),
            explanation = "Publicados en 1725, los cuatro conciertos de Vivaldi evocan cantos primaverales, tormentas estivales y hielo invernal."
        ),
        "seed_mus_3" to LocalizedQuestionCopy(
            questionText = "¿Qué trompetista de jazz lanzó el histórico álbum de 1959 'Kind of Blue'?",
            correctAnswer = "Miles Davis",
            incorrectAnswers = listOf("John Coltrane", "Dizzy Gillespie", "Chet Baker"),
            explanation = "Grabado con John Coltrane y Bill Evans, 'Kind of Blue' definió el jazz modal."
        ),
        "seed_mus_4" to LocalizedQuestionCopy(
            questionText = "En teoría musical, ¿qué intervalo abarca siete semitonos?",
            correctAnswer = "Quinta justa",
            incorrectAnswers = listOf("Cuarta justa", "Sexta mayor", "Tritono"),
            explanation = "Una quinta justa (como de Do a Sol) abarca siete semitonos y es pilar de la armonía occidental."
        ),
        "seed_spt_1" to LocalizedQuestionCopy(
            questionText = "¿Cuántos jugadores de un equipo hay en un campo de fútbol estándar durante el juego?",
            correctAnswer = "11",
            incorrectAnswers = listOf("9", "10", "12"),
            explanation = "Cada equipo alinea 11 jugadores —10 de campo y 1 portero— según las Reglas del Juego."
        ),
        "seed_spt_2" to LocalizedQuestionCopy(
            questionText = "¿Qué torneo de Grand Slam de tenis se disputa sobre pistas de hierba?",
            correctAnswer = "Wimbledon",
            incorrectAnswers = listOf("Roland Garros", "Abierto de EE. UU.", "Abierto de Australia"),
            explanation = "Fundado en 1877 en el All England Club, Wimbledon es el único Grand Slam sobre césped natural."
        ),
        "seed_spt_3" to LocalizedQuestionCopy(
            questionText = "En ajedrez, ¿qué pieza puede saltar sobre otras piezas en el tablero?",
            correctAnswer = "Caballo",
            incorrectAnswers = listOf("Alfil", "Torre", "Dama"),
            explanation = "Moviéndose en forma de L, el caballo es la única pieza capaz de saltar por encima de otras."
        ),
        "seed_spt_4" to LocalizedQuestionCopy(
            questionText = "¿Qué ciudad albergó los primeros Juegos Olímpicos modernos en 1896?",
            correctAnswer = "Atenas",
            incorrectAnswers = listOf("París", "Londres", "Roma"),
            explanation = "Organizados por Pierre de Coubertin, 241 atletas de 14 países compitieron en Atenas en abril de 1896."
        ),
        "seed_food_1" to LocalizedQuestionCopy(
            questionText = "¿Qué fruta se seca para elaborar las pasas tradicionales?",
            correctAnswer = "Uvas",
            incorrectAnswers = listOf("Ciruelas", "Higos", "Dátiles"),
            explanation = "Las pasas son uvas secadas al sol o deshidratadas, mientras que las ciruelas secas son ciruelas pasas."
        ),
        "seed_food_2" to LocalizedQuestionCopy(
            questionText = "El azafrán se cosecha de los estigmas de ¿qué flor?",
            correctAnswer = "Azafrán (Crocus)",
            incorrectAnswers = listOf("Caléndula", "Hibisco", "Orquídea"),
            explanation = "Cada flor de Crocus sativus produce solo tres estigmas carmesí recolectados a mano al amanecer."
        ),
        "seed_food_3" to LocalizedQuestionCopy(
            questionText = "¿Qué té verde japonés se muele finamente a partir de hojas cultivadas a la sombra?",
            correctAnswer = "Matcha",
            incorrectAnswers = listOf("Oolong", "Darjeeling", "Rooibos"),
            explanation = "Sombrear las plantas aumenta la clorofila y la L-teanina, dando al matcha su intenso color verde y sabor umami."
        ),
        "seed_food_4" to LocalizedQuestionCopy(
            questionText = "¿Qué queso italiano se madura en cuevas y se perfora para desarrollar vetas azul verdosas?",
            correctAnswer = "Gorgonzola",
            incorrectAnswers = listOf("Pecorino Romano", "Fontina", "Taleggio"),
            explanation = "Originario de Lombardía y Piamonte, el gorgonzola se perfora para dejar entrar aire y formar vetas azules."
        ),
        "seed_soc_1" to LocalizedQuestionCopy(
            questionText = "¿Qué festival de las luces se celebra en toda la India con lámparas de barro llamadas diyas?",
            correctAnswer = "Diwali",
            incorrectAnswers = listOf("Holi", "Pongal", "Onam"),
            explanation = "Celebrado durante cinco días en otoño, Diwali simboliza el triunfo de la luz sobre la oscuridad."
        ),
        "seed_soc_2" to LocalizedQuestionCopy(
            questionText = "¿Qué filósofo de la Antigua Grecia fundó la Academia en Atenas?",
            correctAnswer = "Platón",
            incorrectAnswers = listOf("Sócrates", "Aristóteles", "Epicuro"),
            explanation = "Hacia el 387 a. C., Platón fundó la Academia, donde Aristóteles estudió posteriormente veinte años."
        ),
        "seed_soc_3" to LocalizedQuestionCopy(
            questionText = "¿Cómo se llama el arte tradicional japonés de plegar papel?",
            correctAnswer = "Origami",
            incorrectAnswers = listOf("Ikebana", "Kintsugi", "Caligrafía"),
            explanation = "De 'ori' (doblar) y 'kami' (papel), el origami transforma una sola hoja cuadrada sin cortar en figuras."
        ),
        "seed_soc_4" to LocalizedQuestionCopy(
            questionText = "En la mitología nórdica, ¿cómo se llama el árbol del mundo que conecta los nueve reinos?",
            correctAnswer = "Yggdrasil",
            incorrectAnswers = listOf("Valhalla", "Bifröst", "Midgard"),
            explanation = "Descrito en la Edda poética como un inmenso fresno sagrado, Yggdrasil une Asgard, Midgard y el inframundo."
        ),
        "std_capital_france" to LocalizedQuestionCopy(
            questionText = "¿Cuál es la ciudad capital de Francia?",
            correctAnswer = "París",
            incorrectAnswers = listOf("Londres", "Berlín", "Madrid"),
            explanation = "París es la capital de Francia desde la época medieval, célebre por su arte, gastronomía y arquitectura."
        ),
        "std_atomic_hydrogen" to LocalizedQuestionCopy(
            questionText = "¿Qué elemento químico tiene el número atómico 1?",
            correctAnswer = "Hidrógeno",
            incorrectAnswers = listOf("Helio", "Oxígeno", "Carbono"),
            explanation = "El hidrógeno es el elemento químico más ligero y abundante del universo."
        ),
        "std_shakes_romeo" to LocalizedQuestionCopy(
            questionText = "¿Qué dramaturgo escribió el romance trágico 'Romeo y Julieta'?",
            correctAnswer = "William Shakespeare",
            incorrectAnswers = listOf("Christopher Marlowe", "Geoffrey Chaucer", "John Milton"),
            explanation = "Escrita en la década de 1590, 'Romeo y Julieta' narra la historia de dos jóvenes amantes en Verona."
        ),
        "std_speed_light" to LocalizedQuestionCopy(
            questionText = "¿Cuál es la velocidad aproximada de la luz en el vacío en kilómetros por segundo?",
            correctAnswer = "300,000 km/s",
            incorrectAnswers = listOf("150,000 km/s", "450,000 km/s", "1,000,000 km/s"),
            explanation = "La luz viaja exactamente a 299,792,458 metros por segundo en el vacío."
        ),
        "std_ww2_end" to LocalizedQuestionCopy(
            questionText = "¿En qué año terminó oficialmente la Segunda Guerra Mundial?",
            correctAnswer = "1945",
            incorrectAnswers = listOf("1943", "1944", "1946"),
            explanation = "La Segunda Guerra Mundial concluyó en 1945 tras la rendición de las potencias del Eje en Europa y el Pacífico."
        )
    )

    val questions: Map<String, LocalizedQuestionCopy> by lazy {
        baseQuestions + LocalizedSeedEsExtra.questions
    }
}
