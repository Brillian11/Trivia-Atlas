package com.example.data.local

object LocalizedSeedEsExtra {
    val questions: Map<String, LocalizedQuestionCopy> = mapOf(
        // Cultura General (5..15)
        "seed_gk_5" to LocalizedQuestionCopy(
            questionText = "¿Cuántos lados tiene un hexágono regular?",
            correctAnswer = "6",
            incorrectAnswers = listOf("5", "7", "8"),
            explanation = "Un hexágono es un polígono de seis lados cuyos ángulos interiores suman 720 grados."
        ),
        "seed_gk_6" to LocalizedQuestionCopy(
            questionText = "¿Qué gema es el mineral natural más duro en la escala de Mohs?",
            correctAnswer = "Diamante",
            incorrectAnswers = listOf("Rubí", "Cuarzo", "Topacio"),
            explanation = "El diamante ocupa el nivel 10 —el máximo de la escala de Mohs— por sus fuertes enlaces tetraédricos de carbono."
        ),
        "seed_gk_7" to LocalizedQuestionCopy(
            questionText = "¿De qué color suele ser la piedra preciosa esmeralda?",
            correctAnswer = "Verde",
            incorrectAnswers = listOf("Azul", "Rojo", "Ámbar"),
            explanation = "La esmeralda es una variedad verde del mineral berilo coloreada por trazas de cromo o vanadio."
        ),
        "seed_gk_8" to LocalizedQuestionCopy(
            questionText = "En números romanos, ¿qué valor representa la letra 'L'?",
            correctAnswer = "50",
            incorrectAnswers = listOf("100", "500", "40"),
            explanation = "En numeración romana, I=1, V=5, X=10, L=50, C=100, D=500 y M=1000."
        ),
        "seed_gk_9" to LocalizedQuestionCopy(
            questionText = "¿Cuál es la moneda oficial de Japón?",
            correctAnswer = "Yen",
            incorrectAnswers = listOf("Won", "Yuan", "Baht"),
            explanation = "Introducido en 1871 durante la era Meiji, el yen japonés se representa con el símbolo ¥."
        ),
        "seed_gk_10" to LocalizedQuestionCopy(
            questionText = "¿Qué término de ajedrez describe cuando el rey no está en jaque pero no tiene jugadas legales?",
            correctAnswer = "Ahogado",
            incorrectAnswers = listOf("Jaque mate", "Enroque", "Captura al paso"),
            explanation = "Según las reglas oficiales del ajedrez, una posición de ahogado termina la partida de inmediato en tablas."
        ),
        "seed_gk_11" to LocalizedQuestionCopy(
            questionText = "¿Cuál es la unidad del Sistema Internacional para la resistencia eléctrica?",
            correctAnswer = "Ohmio",
            incorrectAnswers = listOf("Vatio", "Julio", "Tesla"),
            explanation = "Nombrada en honor al físico alemán Georg Simon Ohm, la resistencia se simboliza con la letra griega omega (Ω)."
        ),
        "seed_gk_12" to LocalizedQuestionCopy(
            questionText = "¿A qué familia lingüística pertenecen el húngaro, el finés y el estonio?",
            correctAnswer = "Urálica",
            incorrectAnswers = listOf("Eslava", "Celta", "Semítica"),
            explanation = "A diferencia de la mayoría de idiomas europeos indoeuropeos, el finés, el estonio y el húngaro son lenguas urálicas."
        ),
        "seed_gk_13" to LocalizedQuestionCopy(
            questionText = "¿Cómo se llama el arte tradicional japonés de cultivar árboles en miniatura en bandejas poco profundas?",
            correctAnswer = "Bonsái",
            incorrectAnswers = listOf("Ikebana", "Kintsugi", "Suiseki"),
            explanation = "Literalmente 'plantado en bandeja', el bonsái moldea árboles durante décadas mediante poda y alambrado."
        ),
        "seed_gk_14" to LocalizedQuestionCopy(
            questionText = "¿Qué metal otorga a los rubíes su intenso color rojo como impureza en el corindón?",
            correctAnswer = "Cromo",
            incorrectAnswers = listOf("Cobalto", "Cobre", "Manganeso"),
            explanation = "Tanto los rubíes como los zafiros son variedades de corindón; las trazas de cromo producen el rojo del rubí."
        ),
        "seed_gk_15" to LocalizedQuestionCopy(
            questionText = "¿Cuántos días tiene un año bisiesto?",
            correctAnswer = "366",
            incorrectAnswers = listOf("364", "365", "367"),
            explanation = "Un año bisiesto añade el 29 de febrero cada cuatro años (salvo excepciones seculares) para ajustar el calendario."
        ),

        // Ciencia (5..15)
        "seed_sci_5" to LocalizedQuestionCopy(
            questionText = "¿Qué planeta de nuestro Sistema Solar es el más cercano al Sol?",
            correctAnswer = "Mercurio",
            incorrectAnswers = listOf("Venus", "Marte", "Tierra"),
            explanation = "Mercurio orbita a una distancia media de 58 millones de km del Sol y completa un año en 88 días terrestres."
        ),
        "seed_sci_6" to LocalizedQuestionCopy(
            questionText = "¿Qué gas constituye aproximadamente el 78 % de la atmósfera seca de la Tierra?",
            correctAnswer = "Nitrógeno",
            incorrectAnswers = listOf("Oxígeno", "Dióxido de carbono", "Argón"),
            explanation = "El aire seco contiene cerca de 78 % de nitrógeno, 21 % de oxígeno y 1 % de argón y otros gases."
        ),
        "seed_sci_7" to LocalizedQuestionCopy(
            questionText = "¿Qué fuerza mantiene a la Luna en órbita alrededor de la Tierra?",
            correctAnswer = "Gravedad",
            incorrectAnswers = listOf("Magnetismo", "Fricción", "Fuerza nuclear"),
            explanation = "La gravedad proporciona la atracción centrípeta que equilibra la velocidad orbital de la Luna."
        ),
        "seed_sci_8" to LocalizedQuestionCopy(
            questionText = "¿Cuál es el valor de pH del agua pura neutra a 25 °C?",
            correctAnswer = "7",
            incorrectAnswers = listOf("0", "5", "14"),
            explanation = "La escala de pH va de 0 a 14; por debajo de 7 es ácida, 7 es neutra y por encima de 7 es básica."
        ),
        "seed_sci_9" to LocalizedQuestionCopy(
            questionText = "¿Qué gas noble tiene número atómico 2 y se usa habitualmente para elevar globos meteorológicos?",
            correctAnswer = "Helio",
            incorrectAnswers = listOf("Neón", "Hidrógeno", "Xenón"),
            explanation = "El helio es más ligero que el aire y químicamente inerte, ideal para globos y refrigeración de resonancias."
        ),
        "seed_sci_10" to LocalizedQuestionCopy(
            questionText = "¿Qué científico formuló las tres leyes del movimiento y la gravitación universal?",
            correctAnswer = "Isaac Newton",
            incorrectAnswers = listOf("Galileo Galilei", "Johannes Kepler", "Niels Bohr"),
            explanation = "Newton publicó sus leyes del movimiento y la gravedad en 'Principia Mathematica' en 1687."
        ),
        "seed_sci_11" to LocalizedQuestionCopy(
            questionText = "¿Cuál es el órgano interno más grande del cuerpo humano?",
            correctAnswer = "Hígado",
            incorrectAnswers = listOf("Bazo", "Páncreas", "Riñón"),
            explanation = "El hígado humano adulto realiza cientos de funciones metabólicas, incluidas la desintoxicación y producción de bilis."
        ),
        "seed_sci_12" to LocalizedQuestionCopy(
            questionText = "¿Qué partícula subatómica posee carga eléctrica negativa?",
            correctAnswer = "Electrón",
            incorrectAnswers = listOf("Protón", "Neutrón", "Positrón"),
            explanation = "Los electrones tienen carga negativa (-1), mientras que los protones tienen +1 y los neutrones son neutros."
        ),
        "seed_sci_13" to LocalizedQuestionCopy(
            questionText = "¿Cómo se llama el proceso por el que las plantas transforman luz solar, agua y CO₂ en glucosa?",
            correctAnswer = "Fotosíntesis",
            incorrectAnswers = listOf("Fermentación", "Transpiración", "Quimiosíntesis"),
            explanation = "Los cloroplastos capturan energía luminosa mediante la clorofila para sintetizar azúcares y liberar oxígeno."
        ),
        "seed_sci_14" to LocalizedQuestionCopy(
            questionText = "¿Qué elemento posee la mayor conductividad eléctrica a temperatura ambiente?",
            correctAnswer = "Plata",
            incorrectAnswers = listOf("Oro", "Cobre", "Aluminio"),
            explanation = "La plata conduce la electricidad ligeramente mejor que el cobre, aunque el cobre se usa más por su costo."
        ),
        "seed_sci_15" to LocalizedQuestionCopy(
            questionText = "A presión estándar al nivel del mar, ¿a qué temperatura en grados Celsius hierve el agua pura?",
            correctAnswer = "100",
            incorrectAnswers = listOf("80", "90", "112"),
            explanation = "En la escala Celsius, el agua pura se congela a 0 °C y hierve a 100 °C a 1 atmósfera."
        ),

        // Historia (5..15)
        "seed_his_5" to LocalizedQuestionCopy(
            questionText = "¿Quién fue el primer presidente de los Estados Unidos?",
            correctAnswer = "George Washington",
            incorrectAnswers = listOf("Thomas Jefferson", "John Adams", "Benjamin Franklin"),
            explanation = "George Washington asumió como primer presidente estadounidense el 30 de abril de 1789."
        ),
        "seed_his_6" to LocalizedQuestionCopy(
            questionText = "¿Qué civilización antigua construyó Machu Picchu en lo alto de los Andes?",
            correctAnswer = "Inca",
            incorrectAnswers = listOf("Maya", "Azteca", "Olmeca"),
            explanation = "Edificada en el siglo XV bajo el emperador Pachacútec, Machu Picchu se ubica a 2,430 metros en Perú."
        ),
        "seed_his_7" to LocalizedQuestionCopy(
            questionText = "¿En qué año cayó el Muro de Berlín, simbolizando el fin de la Guerra Fría?",
            correctAnswer = "1989",
            incorrectAnswers = listOf("1985", "1991", "1993"),
            explanation = "Los ciudadanos derribaron tramos del Muro de Berlín el 9 de noviembre de 1989 tras abrirse los cruces."
        ),
        "seed_his_8" to LocalizedQuestionCopy(
            questionText = "¿Qué barco se hundió en su viaje inaugural por el Atlántico Norte en abril de 1912?",
            correctAnswer = "Titanic",
            incorrectAnswers = listOf("Lusitania", "Britannic", "Carpathia"),
            explanation = "El RMS Titanic chocó contra un iceberg el 14-15 de abril de 1912, impulsando reformas de seguridad marítima."
        ),
        "seed_his_9" to LocalizedQuestionCopy(
            questionText = "¿Qué ciudad-estado renacentista fue gobernada durante generaciones por la familia de banqueros Médici?",
            correctAnswer = "Florencia",
            incorrectAnswers = listOf("Venecia", "Milán", "Nápoles"),
            explanation = "El mecenazgo de los Médici convirtió a Florencia en la cuna del Renacimiento italiano."
        ),
        "seed_his_10" to LocalizedQuestionCopy(
            questionText = "¿Quién fue la primera mujer en ganar un Premio Nobel, al recibir el de Física en 1903?",
            correctAnswer = "Marie Curie",
            incorrectAnswers = listOf("Rosalind Franklin", "Ada Lovelace", "Lise Meitner"),
            explanation = "Marie Curie obtuvo el Premio Nobel de Física en 1903 y el de Química en 1911."
        ),
        "seed_his_11" to LocalizedQuestionCopy(
            questionText = "¿Qué célebre documento selló el rey Juan I de Inglaterra en Runnymede en 1215?",
            correctAnswer = "Carta Magna",
            incorrectAnswers = listOf("Libro Domesday", "Tratado de Verdún", "Declaración de Derechos"),
            explanation = "La Carta Magna sentó el principio de que incluso el monarca está sujeto a la ley."
        ),
        "seed_his_12" to LocalizedQuestionCopy(
            questionText = "¿Qué antigua red comercial conectaba Chang'an en China con el mundo mediterráneo?",
            correctAnswer = "Ruta de la Seda",
            incorrectAnswers = listOf("Ruta del Ámbar", "Ruta del Incienso", "Calzada Real"),
            explanation = "Activa desde la dinastía Han, la Ruta de la Seda transportó seda, especias, papel e ideas por toda Eurasia."
        ),
        "seed_his_13" to LocalizedQuestionCopy(
            questionText = "¿En qué año proclamó su independencia Indonesia?",
            correctAnswer = "1945",
            incorrectAnswers = listOf("1942", "1949", "1950"),
            explanation = "Sukarno y Mohammad Hatta proclamaron la independencia de Indonesia el 17 de agosto de 1945."
        ),
        "seed_his_14" to LocalizedQuestionCopy(
            questionText = "¿Qué emperador bizantino ordenó construir Santa Sofía en Constantinopla?",
            correctAnswer = "Justiniano I",
            incorrectAnswers = listOf("Constantino I", "Heraclio", "Basilio II"),
            explanation = "Terminada en el año 537 d. C. bajo Justiniano I, su inmensa cúpula fue una proeza bizantina."
        ),
        "seed_his_15" to LocalizedQuestionCopy(
            questionText = "¿Quién dirigió la expedición de 1911 que llegó por primera vez al Polo Sur?",
            correctAnswer = "Roald Amundsen",
            incorrectAnswers = listOf("Robert Falcon Scott", "Ernest Shackleton", "Richard Byrd"),
            explanation = "El explorador noruego Roald Amundsen alcanzó el Polo Sur geográfico el 14 de diciembre de 1911."
        ),

        // Geografía (5..15)
        "seed_geo_5" to LocalizedQuestionCopy(
            questionText = "¿Cuál es el océano más grande de la Tierra?",
            correctAnswer = "Océano Pacífico",
            incorrectAnswers = listOf("Océano Atlántico", "Océano Índico", "Océano Ártico"),
            explanation = "Con más de 165 millones de km², el océano Pacífico cubre casi un tercio de la superficie terrestre."
        ),
        "seed_geo_6" to LocalizedQuestionCopy(
            questionText = "¿En qué continente se encuentra el desierto del Sahara?",
            correctAnswer = "África",
            incorrectAnswers = listOf("Asia", "Australia", "América del Sur"),
            explanation = "El Sahara abarca 11 países del norte de África y es el desierto cálido más extenso del planeta."
        ),
        "seed_geo_7" to LocalizedQuestionCopy(
            questionText = "¿Qué país posee la línea costera más larga del mundo?",
            correctAnswer = "Canadá",
            incorrectAnswers = listOf("Rusia", "Indonesia", "Australia"),
            explanation = "Con miles de islas árticas y costeras, la costa canadiense supera los 202,000 kilómetros."
        ),
        "seed_geo_8" to LocalizedQuestionCopy(
            questionText = "¿Cuál es la ciudad capital de Islandia?",
            correctAnswer = "Reikiavik",
            incorrectAnswers = listOf("Oslo", "Helsinki", "Tórshavn"),
            explanation = "Reikiavik es la capital de un estado soberano situada más al norte en el mundo."
        ),
        "seed_geo_9" to LocalizedQuestionCopy(
            questionText = "¿Qué cordillera sudamericana es la cadena montañosa continental más larga de la Tierra?",
            correctAnswer = "Los Andes",
            incorrectAnswers = listOf("Las Rocosas", "Los Alpes", "Los Urales"),
            explanation = "Con unos 7,000 km a lo largo del oeste de Sudamérica, los Andes recorren siete países."
        ),
        "seed_geo_10" to LocalizedQuestionCopy(
            questionText = "¿Qué capital europea está dividida por el río Danubio en Buda y Pest?",
            correctAnswer = "Budapest",
            incorrectAnswers = listOf("Viena", "Praga", "Belgrado"),
            explanation = "Buda y Pest se unieron con Óbuda en 1873 para formar Budapest, capital de Hungría."
        ),
        "seed_geo_11" to LocalizedQuestionCopy(
            questionText = "El lago Baikal, el lago de agua dulce más profundo del mundo, se encuentra en ¿qué país?",
            correctAnswer = "Rusia",
            incorrectAnswers = listOf("Kazajistán", "Mongolia", "Canadá"),
            explanation = "Ubicado en el sur de Siberia, el Baikal alcanza 1,642 m de profundidad y concentra ~20 % del agua dulce superficial."
        ),
        "seed_geo_12" to LocalizedQuestionCopy(
            questionText = "¿Qué dos océanos conecta el Canal de Panamá?",
            correctAnswer = "Atlántico y Pacífico",
            incorrectAnswers = listOf("Índico y Pacífico", "Atlántico y Ártico", "Índico y Atlántico"),
            explanation = "Inaugurado en 1914, el canal de 82 km une el mar Caribe (Atlántico) con el golfo de Panamá (Pacífico)."
        ),
        "seed_geo_13" to LocalizedQuestionCopy(
            questionText = "¿Cómo se llama la parte indonesia de la isla de Borneo?",
            correctAnswer = "Kalimantan",
            incorrectAnswers = listOf("Sumatra", "Célebes", "Java"),
            explanation = "Kalimantan agrupa las provincias indonesias de Borneo, mientras que Sumatra es la mayor isla íntegramente indonesia."
        ),
        "seed_geo_14" to LocalizedQuestionCopy(
            questionText = "¿De qué país africano es capital Adís Abeba?",
            correctAnswer = "Etiopía",
            incorrectAnswers = listOf("Kenia", "Tanzania", "Ghana"),
            explanation = "Situada a unos 2,355 metros de altitud, Adís Abeba alberga la sede de la Unión Africana."
        ),
        "seed_geo_15" to LocalizedQuestionCopy(
            questionText = "¿Qué línea imaginaria divide la Tierra en hemisferio norte y hemisferio sur?",
            correctAnswer = "Ecuador",
            incorrectAnswers = listOf("Meridiano de Greenwich", "Trópico de Cáncer", "Línea internacional de cambio de fecha"),
            explanation = "A 0 grados de latitud, el ecuador rodea la Tierra exactamente a mitad de camino entre ambos polos."
        ),

        // Arte y Literatura (5..15)
        "seed_art_5" to LocalizedQuestionCopy(
            questionText = "¿Quién escribió la tragedia 'Romeo y Julieta'?",
            correctAnswer = "William Shakespeare",
            incorrectAnswers = listOf("Christopher Marlowe", "Charles Dickens", "Jane Austen"),
            explanation = "Escrita al inicio de la carrera de Shakespeare hacia 1595, la obra transcurre en Verona."
        ),
        "seed_art_6" to LocalizedQuestionCopy(
            questionText = "¿Qué retrato de Leonardo da Vinci se exhibe en el Museo del Louvre en París?",
            correctAnswer = "La Gioconda (Mona Lisa)",
            incorrectAnswers = listOf("La última cena", "La dama del armiño", "Salvator Mundi"),
            explanation = "Pintada sobre tabla de álamo a principios del siglo XVI, es célebre por su sutil sonrisa en sfumato."
        ),
        "seed_art_7" to LocalizedQuestionCopy(
            questionText = "En 'El hobbit' de J.R.R. Tolkien, ¿cómo se llama el hobbit protagonista?",
            correctAnswer = "Bilbo Bolsón",
            incorrectAnswers = listOf("Frodo Bolsón", "Samsagaz Gamyi", "Peregrin Tuk"),
            explanation = "Bilbo Bolsón deja la Comarca junto a Gandalf y trece enanos para recuperar Erebor."
        ),
        "seed_art_8" to LocalizedQuestionCopy(
            questionText = "¿Qué escritor español creó 'Don Quijote de la Mancha'?",
            correctAnswer = "Miguel de Cervantes",
            incorrectAnswers = listOf("Lope de Vega", "Gabriel García Márquez", "Federico García Lorca"),
            explanation = "Publicada en 1605 y 1615, narra las aventuras caballerescas de Alonso Quijano."
        ),
        "seed_art_9" to LocalizedQuestionCopy(
            questionText = "¿Qué artista expresionista noruego pintó 'El grito' en 1893?",
            correctAnswer = "Edvard Munch",
            incorrectAnswers = listOf("Gustav Klimt", "Egon Schiele", "Vasili Kandinski"),
            explanation = "Munch creó varias versiones de 'El grito' inspirado en un atardecer rojizo sobre el fiordo de Oslo."
        ),
        "seed_art_10" to LocalizedQuestionCopy(
            questionText = "¿Quién escribió la novela 'Orgullo y prejuicio'?",
            correctAnswer = "Jane Austen",
            incorrectAnswers = listOf("Charlotte Brontë", "Mary Shelley", "George Eliot"),
            explanation = "Publicada en 1813, 'Orgullo y prejuicio' gira en torno a Elizabeth Bennet y Fitzwilliam Darcy."
        ),
        "seed_art_11" to LocalizedQuestionCopy(
            questionText = "¿Qué escultor francés creó la estatua de bronce 'El pensador'?",
            correctAnswer = "Auguste Rodin",
            incorrectAnswers = listOf("Constantin Brâncuși", "Alberto Giacometti", "Edgar Degas"),
            explanation = "Concebida originalmente para 'La puerta del infierno', se fundió en gran formato en 1904."
        ),
        "seed_art_12" to LocalizedQuestionCopy(
            questionText = "¿Qué premio Nobel colombiano escribió 'Cien años de soledad'?",
            correctAnswer = "Gabriel García Márquez",
            incorrectAnswers = listOf("Mario Vargas Llosa", "Jorge Luis Borges", "Julio Cortázar"),
            explanation = "Hito del realismo mágico (1967), narra siete generaciones de la familia Buendía en Macondo."
        ),
        "seed_art_13" to LocalizedQuestionCopy(
            questionText = "¿Qué noble japonesa del siglo XI escribió 'La historia de Genji'?",
            correctAnswer = "Murasaki Shikibu",
            incorrectAnswers = listOf("Sei Shōnagon", "Ono no Komachi", "Izumi Shikibu"),
            explanation = "Considerada a menudo la primera novela del mundo, retrata la vida cortesana del período Heian."
        ),
        "seed_art_14" to LocalizedQuestionCopy(
            questionText = "¿Qué movimiento pictórico liderado por Claude Monet buscó capturar la luz natural cambiante?",
            correctAnswer = "Impresionismo",
            incorrectAnswers = listOf("Cubismo", "Barroco", "Surrealismo"),
            explanation = "Bautizado por la obra de Monet 'Impresión, sol naciente' (1872), transformó el arte moderno."
        ),
        "seed_art_15" to LocalizedQuestionCopy(
            questionText = "En 'Moby-Dick' de Herman Melville, ¿qué tipo de animal es Moby Dick?",
            correctAnswer = "Cachalote",
            incorrectAnswers = listOf("Tiburón blanco", "Calamar gigante", "Orca"),
            explanation = "El capitán Ahab persigue incansablemente al gran cachalote blanco por los océanos del mundo."
        ),

        // Cine y TV (5..15)
        "seed_film_5" to LocalizedQuestionCopy(
            questionText = "En 'El rey león' (1994), ¿cómo se llama el padre de Simba?",
            correctAnswer = "Mufasa",
            incorrectAnswers = listOf("Scar", "Rafiki", "Zazú"),
            explanation = "El rey Mufasa gobierna las Tierras del Reino hasta ser traicionado por su hermano Scar."
        ),
        "seed_film_6" to LocalizedQuestionCopy(
            questionText = "¿Qué éxito cinematográfico de dinosaurios de 1993 fue dirigido por Steven Spielberg?",
            correctAnswer = "Parque Jurásico",
            incorrectAnswers = listOf("King Kong", "Godzilla", "Avatar"),
            explanation = "Basada en la novela de Michael Crichton, combinó animación por ordenador y animatrónica pionera."
        ),
        "seed_film_7" to LocalizedQuestionCopy(
            questionText = "¿Cómo se llama la nación africana ficticia en 'Black Panther' de Marvel?",
            correctAnswer = "Wakanda",
            incorrectAnswers = listOf("Genosha", "Sokovia", "Latveria"),
            explanation = "Wakanda oculta al mundo su avanzada tecnología impulsada por el metal vibranium."
        ),
        "seed_film_8" to LocalizedQuestionCopy(
            questionText = "¿Qué película de Pixar de 1995 fue el primer largometraje realizado íntegramente por ordenador?",
            correctAnswer = "Toy Story",
            incorrectAnswers = listOf("Bichos", "Monstruos, S. A.", "Shrek"),
            explanation = "Dirigida por John Lasseter, 'Toy Story' presentó a Woody y Buzz Lightyear."
        ),
        "seed_film_9" to LocalizedQuestionCopy(
            questionText = "¿Qué serie británica de ciencia ficción sigue a un viajero del tiempo en una cabina azul de policía?",
            correctAnswer = "Doctor Who",
            incorrectAnswers = listOf("Torchwood", "Black Mirror", "Enano Rojo"),
            explanation = "Emitida por la BBC desde 1963, el Doctor viaja por el espacio y el tiempo en la TARDIS."
        ),
        "seed_film_10" to LocalizedQuestionCopy(
            questionText = "¿Quién compuso las míticas bandas sonoras de 'Star Wars', 'Indiana Jones' y 'Tiburón'?",
            correctAnswer = "John Williams",
            incorrectAnswers = listOf("Hans Zimmer", "Ennio Morricone", "Howard Shore"),
            explanation = "John Williams acumula más de 50 nominaciones al Óscar a lo largo de seis décadas."
        ),
        "seed_film_11" to LocalizedQuestionCopy(
            questionText = "¿Quién dirigió la trilogía cinematográfica de 'El Señor de los Anillos'?",
            correctAnswer = "Peter Jackson",
            incorrectAnswers = listOf("Ridley Scott", "James Cameron", "Guillermo del Toro"),
            explanation = "Rodada en Nueva Zelanda, la trilogía de Peter Jackson ganó 17 premios Óscar en total."
        ),
        "seed_film_12" to LocalizedQuestionCopy(
            questionText = "¿Qué epopeya de Akira Kurosawa de 1954 muestra a campesinos contratando guerreros para defender su aldea?",
            correctAnswer = "Los siete samuráis",
            incorrectAnswers = listOf("Rashomon", "Ran", "Yojimbo"),
            explanation = "Influyó en clásicos occidentales como 'Los siete magníficos' y en el cine de acción moderno."
        ),
        "seed_film_13" to LocalizedQuestionCopy(
            questionText = "¿Qué drama de Orson Welles de 1941 comienza con la misteriosa palabra final 'Rosebud'?",
            correctAnswer = "Ciudadano Kane",
            incorrectAnswers = listOf("Casablanca", "El crepúsculo de los dioses", "Vértigo"),
            explanation = "Welles coescribió, dirigió y protagonizó al magnate de la prensa Charles Foster Kane con 25 años."
        ),
        "seed_film_14" to LocalizedQuestionCopy(
            questionText = "En 'Origen' (Inception) de Christopher Nolan, ¿qué objeto usan los personajes para saber si sueñan?",
            correctAnswer = "Tótem",
            incorrectAnswers = listOf("Cronómetro", "Péndulo", "Brújula"),
            explanation = "La peonza tótem de Cobb le ayuda a distinguir la realidad de los sueños compartidos."
        ),
        "seed_film_15" to LocalizedQuestionCopy(
            questionText = "¿Qué esponja amarilla vive en una piña bajo el mar en Fondo de Bikini?",
            correctAnswer = "Bob Esponja",
            incorrectAnswers = listOf("Patricio Estrella", "Calamardo", "Plankton"),
            explanation = "Creada por el biólogo marino y animador Stephen Hillenburg, la serie se estrenó en 1999."
        ),

        // Música (5..15)
        "seed_mus_5" to LocalizedQuestionCopy(
            questionText = "¿Qué banda de rock de Liverpool integraron John Lennon, Paul McCartney, George Harrison y Ringo Starr?",
            correctAnswer = "The Beatles",
            incorrectAnswers = listOf("The Rolling Stones", "The Who", "Queen"),
            explanation = "Formados en Liverpool en 1960, The Beatles se convirtieron en el grupo más vendido de la historia."
        ),
        "seed_mus_6" to LocalizedQuestionCopy(
            questionText = "¿Qué instrumento de viento-madera usa lengüeta simple y fue inventado por Adolphe Sax?",
            correctAnswer = "Saxofón",
            incorrectAnswers = listOf("Clarinete", "Oboe", "Fagot"),
            explanation = "Patentado en París en 1846, el saxofón une la potencia del metal con la agilidad de la madera."
        ),
        "seed_mus_7" to LocalizedQuestionCopy(
            questionText = "En dinámica musical, ¿qué indica el término italiano 'forte'?",
            correctAnswer = "Fuerte (alto)",
            incorrectAnswers = listOf("Suave", "Rápido", "Lento"),
            explanation = "Indicado con 'f', forte significa tocar con intensidad fuerte, mientras que 'p' (piano) significa suave."
        ),
        "seed_mus_8" to LocalizedQuestionCopy(
            questionText = "¿Qué compositor alemán sordo escribió la Novena Sinfonía con la 'Oda a la Alegría'?",
            correctAnswer = "Ludwig van Beethoven",
            incorrectAnswers = listOf("Franz Schubert", "Johannes Brahms", "Joseph Haydn"),
            explanation = "Estrenada en 1824 cuando Beethoven ya estaba sordo, incorporó un coro en el movimiento final."
        ),
        "seed_mus_9" to LocalizedQuestionCopy(
            questionText = "¿Qué género musical jamaicano surgió a fines de los 60 y fue popularizado mundialmente por Bob Marley?",
            correctAnswer = "Reggae",
            incorrectAnswers = listOf("Bossa nova", "Flamenco", "Afrobeat"),
            explanation = "Caracterizado por su ritmo sincopado y líneas graves profundas, el reggae es patrimonio cultural de la UNESCO."
        ),
        "seed_mus_10" to LocalizedQuestionCopy(
            questionText = "¿Qué conjunto tradicional de percusión indonesio emplea metalófonos de bronce, gongs y tambores kendang?",
            correctAnswer = "Gamelán",
            incorrectAnswers = listOf("Sitar", "Taiko", "Mariachi"),
            explanation = "Tradicional en Java y Bali, el gamelán acompaña danzas, ceremonias y teatro de sombras wayang."
        ),
        "seed_mus_11" to LocalizedQuestionCopy(
            questionText = "¿Cuál es el tipo de voz masculina más grave en la música vocal clásica?",
            correctAnswer = "Bajo",
            incorrectAnswers = listOf("Tenor", "Barítono", "Alto"),
            explanation = "De grave a agudo, los registros clásicos son bajo, barítono, tenor, contralto, mezzosoprano y soprano."
        ),
        "seed_mus_12" to LocalizedQuestionCopy(
            questionText = "¿Qué prodigio austriaco del siglo XVIII compuso 'La flauta mágica' y 'Pequeña serenata nocturna'?",
            correctAnswer = "Wolfgang Amadeus Mozart",
            incorrectAnswers = listOf("Franz Liszt", "Frédéric Chopin", "Claude Debussy"),
            explanation = "Mozart compuso más de 600 obras —sinfonías, conciertos, música de cámara y óperas— antes de los 36 años."
        ),
        "seed_mus_13" to LocalizedQuestionCopy(
            questionText = "¿Qué género brasileño que fusiona samba y jazz dio origen a 'La chica de Ipanema'?",
            correctAnswer = "Bossa nova",
            incorrectAnswers = listOf("Tango", "Cumbia", "Merengue"),
            explanation = "Impulsada en Río de Janeiro a finales de los 50 por João Gilberto y Antônio Carlos Jobim."
        ),
        "seed_mus_14" to LocalizedQuestionCopy(
            questionText = "En notación occidental, ¿cuántos tiempos dura una blanca con puntillo en compás de 4/4?",
            correctAnswer = "3",
            incorrectAnswers = listOf("2", "4", "1.5"),
            explanation = "Una blanca vale 2 tiempos y el puntillo suma la mitad de su valor (1 tiempo), dando 3 tiempos."
        ),
        "seed_mus_15" to LocalizedQuestionCopy(
            questionText = "¿Qué banda británica lanzó en 1975 el himno de rock operístico 'Bohemian Rhapsody'?",
            correctAnswer = "Queen",
            incorrectAnswers = listOf("Pink Floyd", "Led Zeppelin", "Deep Purple"),
            explanation = "Compuesta por Freddie Mercury, 'Bohemian Rhapsody' combinó armonías corales con rock duro."
        ),

        // Deportes y Ocio (5..15)
        "seed_spt_5" to LocalizedQuestionCopy(
            questionText = "En baloncesto, ¿cuántos puntos vale un tiro anotado desde detrás del arco de tres puntos?",
            correctAnswer = "3",
            incorrectAnswers = listOf("1", "2", "4"),
            explanation = "Las canastas dentro del arco valen 2 puntos, los tiros libres 1 punto y los triples 3 puntos."
        ),
        "seed_spt_6" to LocalizedQuestionCopy(
            questionText = "¿Qué deporte de raqueta utiliza un volante con plumas o material sintético?",
            correctAnswer = "Bádminton",
            incorrectAnswers = listOf("Squash", "Tenis de mesa", "Pádel"),
            explanation = "Deporte olímpico desde 1992, el bádminton registra remates que superan los 400 km/h."
        ),
        "seed_spt_7" to LocalizedQuestionCopy(
            questionText = "¿Cuántos aros aparecen en la bandera olímpica oficial?",
            correctAnswer = "5",
            incorrectAnswers = listOf("4", "6", "7"),
            explanation = "Los cinco aros entrelazados —azul, amarillo, negro, verde y rojo— representan los cinco continentes."
        ),
        "seed_spt_8" to LocalizedQuestionCopy(
            questionText = "¿Cuál es la puntuación máxima posible en una partida estándar de bolos de diez pinos?",
            correctAnswer = "300",
            incorrectAnswers = listOf("200", "250", "350"),
            explanation = "Conseguir doce plenos (strikes) consecutivos en los diez cuadros otorga una partida perfecta de 300."
        ),
        "seed_spt_9" to LocalizedQuestionCopy(
            questionText = "¿Qué carrera ciclista anual de tres semanas culmina en los Campos Elíseos de París?",
            correctAnswer = "Tour de Francia",
            incorrectAnswers = listOf("Giro de Italia", "Vuelta a España", "París-Roubaix"),
            explanation = "Disputado desde 1903, el Tour otorga el maillot amarillo al líder de la clasificación general."
        ),
        "seed_spt_10" to LocalizedQuestionCopy(
            questionText = "En golf, ¿cómo se llama completar un hoyo con un golpe por debajo del par?",
            correctAnswer = "Birdie",
            incorrectAnswers = listOf("Eagle", "Bogey", "Albatros"),
            explanation = "Uno bajo par es birdie, dos bajo par es eagle y uno sobre par es bogey."
        ),
        "seed_spt_11" to LocalizedQuestionCopy(
            questionText = "¿Qué arte marcial japonés se traduce como 'el camino de la suavidad' y se centra en proyecciones?",
            correctAnswer = "Judo",
            incorrectAnswers = listOf("Karate", "Kendo", "Taekwondo"),
            explanation = "Fundado por Jigoro Kano en 1882, el judo debutó como deporte olímpico en Tokio 1964."
        ),
        "seed_spt_12" to LocalizedQuestionCopy(
            questionText = "¿Qué distancia en kilómetros tiene un maratón olímpico estándar?",
            correctAnswer = "42.195",
            incorrectAnswers = listOf("38.5", "40.0", "45.2"),
            explanation = "Fijada oficialmente en 1921 en 42.195 km, siguiendo el recorrido de los Juegos de Londres 1908."
        ),
        "seed_spt_13" to LocalizedQuestionCopy(
            questionText = "En Fórmula 1, ¿qué circuito urbano de Montecarlo incluye la célebre horquilla Fairmont?",
            correctAnswer = "Circuito de Mónaco",
            incorrectAnswers = listOf("Silverstone", "Monza", "Spa-Francorchamps"),
            explanation = "Corrido desde 1929 por las estrechas calles del puerto de Mónaco, integra la Triple Corona."
        ),
        "seed_spt_14" to LocalizedQuestionCopy(
            questionText = "En rugby union, ¿cuántos puntos vale un ensayo (try)?",
            correctAnswer = "5",
            incorrectAnswers = listOf("3", "4", "6"),
            explanation = "Apoyar el balón en la zona de marca suma 5 puntos, más 2 si se acierta la conversión."
        ),
        "seed_spt_15" to LocalizedQuestionCopy(
            questionText = "¿Qué país ganó la primera Copa Mundial de Fútbol de la FIFA en 1930?",
            correctAnswer = "Uruguay",
            incorrectAnswers = listOf("Argentina", "Brasil", "Italia"),
            explanation = "La anfitriona Uruguay venció 4-2 a Argentina en la final disputada en Montevideo en 1930."
        ),

        // Gastronomía (5..15)
        "seed_food_5" to LocalizedQuestionCopy(
            questionText = "¿Qué plato de col fermentada es un acompañamiento esencial de la cocina coreana?",
            correctAnswer = "Kimchi",
            incorrectAnswers = listOf("Chucrut", "Miso", "Tempeh"),
            explanation = "Sazonado con chile, ajo y jengibre, el kimchi cuenta con cientos de variantes regionales."
        ),
        "seed_food_6" to LocalizedQuestionCopy(
            questionText = "¿Con qué fruta machacada se prepara principalmente el guacamole?",
            correctAnswer = "Aguacate",
            incorrectAnswers = listOf("Mango", "Papaya", "Calabacín"),
            explanation = "De origen mesoamericano, el guacamole mezcla aguacate maduro con lima, sal y cilantro."
        ),
        "seed_food_7" to LocalizedQuestionCopy(
            questionText = "¿Qué fruto seco es ingrediente básico del pesto genovés tradicional junto con albahaca y aceite?",
            correctAnswer = "Piñones",
            incorrectAnswers = listOf("Nueces", "Anacardos", "Cacahuetes"),
            explanation = "El pesto genovés tradicional combina albahaca, ajo, piñones, queso parmesano y aceite de oliva."
        ),
        "seed_food_8" to LocalizedQuestionCopy(
            questionText = "¿Qué guiso indonesio de ternera cocinado lentamente en leche de coco y especias procede de Sumatra Occidental?",
            correctAnswer = "Rendang",
            incorrectAnswers = listOf("Gado-gado", "Laksa", "Bakso"),
            explanation = "Cocinado horas con leche de coco, limoncillo y galanga, el rendang de Minangkabau es famoso en todo el mundo."
        ),
        "seed_food_9" to LocalizedQuestionCopy(
            questionText = "¿Qué bebida de café espresso lleva leche vaporizada y una espesa capa de espuma?",
            correctAnswer = "Capuchino",
            incorrectAnswers = listOf("Americano", "Ristretto", "Cold brew"),
            explanation = "Un capuchino clásico equilibra espresso, leche caliente y una corona cremosa de espuma."
        ),
        "seed_food_10" to LocalizedQuestionCopy(
            questionText = "El tofu y el tempeh se elaboran tradicionalmente a partir de ¿qué legumbre?",
            correctAnswer = "Soja",
            incorrectAnswers = listOf("Garbanzos", "Lentejas", "Frijoles negros"),
            explanation = "Originario de Java, el tempeh fermenta granos enteros de soja con el cultivo Rhizopus."
        ),
        "seed_food_11" to LocalizedQuestionCopy(
            questionText = "¿Qué plato valenciano de arroz se cocina en una sartén ancha y poco profunda con azafrán?",
            correctAnswer = "Paella",
            incorrectAnswers = listOf("Risotto", "Biryani", "Bibimbap"),
            explanation = "La paella valenciana toma su nombre de la sartén ancha con dos asas empleada sobre fuego vivo."
        ),
        "seed_food_12" to LocalizedQuestionCopy(
            questionText = "¿Qué especia en vaina es la segunda más cara del mundo por peso después del azafrán?",
            correctAnswer = "Vainilla",
            incorrectAnswers = listOf("Cardamomo", "Nuez moscada", "Clavo"),
            explanation = "Nativa de Mesoamérica, la vainilla procede de la orquídea Vanilla planifolia y exige polinización manual."
        ),
        "seed_food_13" to LocalizedQuestionCopy(
            questionText = "¿Qué término culinario francés designa cocinar alimentos envasados al vacío en baño de agua controlada?",
            correctAnswer = "Sous vide",
            incorrectAnswers = listOf("Flambeado", "Juliana", "Baño María"),
            explanation = "Literalmente 'al vacío', el sous vide logra una cocción uniforme de borde a centro."
        ),
        "seed_food_14" to LocalizedQuestionCopy(
            questionText = "¿Qué bebida de té fermentado se prepara con un cultivo simbiótico de bacterias y levaduras (SCOBY)?",
            correctAnswer = "Kombucha",
            incorrectAnswers = listOf("Kéfir", "Mate", "Horchata"),
            explanation = "El té endulzado fermentado con un SCOBY produce una bebida ligeramente efervescente y ácida."
        ),
        "seed_food_15" to LocalizedQuestionCopy(
            questionText = "El chocolate se elabora a partir de las semillas fermentadas y tostadas de ¿qué árbol tropical?",
            correctAnswer = "Cacao",
            incorrectAnswers = listOf("Cafeto", "Cola", "Algarrobo"),
            explanation = "Las mazorcas de Theobroma cacao crecen directamente del tronco y ramas en climas tropicales."
        ),

        // Sociedad y Cultura (5..15)
        "seed_soc_5" to LocalizedQuestionCopy(
            questionText = "¿Qué tejido tradicional indonesio teñido mediante reserva con cera es Patrimonio de la Humanidad?",
            correctAnswer = "Batik",
            incorrectAnswers = listOf("Kente", "Tartán", "Tweed"),
            explanation = "Los artesanos aplican cera caliente con un canting antes de teñir la tela creando diseños simbólicos."
        ),
        "seed_soc_6" to LocalizedQuestionCopy(
            questionText = "¿Qué celebración anual marca el inicio del año en el calendario lunisolar chino tradicional?",
            correctAnswer = "Año Nuevo Lunar",
            incorrectAnswers = listOf("Festival del Medio Otoño", "Songkran", "Nowruz"),
            explanation = "También llamado Fiesta de la Primavera, cada Año Nuevo Lunar corresponde a un animal del zodíaco."
        ),
        "seed_soc_7" to LocalizedQuestionCopy(
            questionText = "¿Cuál es el idioma oficial de Brasil?",
            correctAnswer = "Portugués",
            incorrectAnswers = listOf("Español", "Francés", "Italiano"),
            explanation = "Brasil es el país con más hablantes de portugués del mundo y el único lusófono de América."
        ),
        "seed_soc_8" to LocalizedQuestionCopy(
            questionText = "¿Qué corriente filosófica china propone vivir en armonía con el flujo natural del universo?",
            correctAnswer = "Taoísmo",
            incorrectAnswers = listOf("Estoicismo", "Existencialismo", "Utilitarismo"),
            explanation = "Vinculado al 'Tao Te Ching' atribuido a Laozi, el taoísmo destaca la sencillez y la acción sin esfuerzo."
        ),
        "seed_soc_9" to LocalizedQuestionCopy(
            questionText = "¿Qué sistema de escritura de 24 letras fue creado en Corea en el siglo XV bajo el rey Sejong?",
            correctAnswer = "Hangul",
            incorrectAnswers = listOf("Kanji", "Cirílico", "Devanagari"),
            explanation = "Promulgado en 1446, el hangul se diseñó para que todo el pueblo pudiera aprender a leer rápidamente."
        ),
        "seed_soc_10" to LocalizedQuestionCopy(
            questionText = "¿Qué festival romano de mediados de diciembre incluía banquetes e intercambio de regalos?",
            correctAnswer = "Saturnales",
            incorrectAnswers = listOf("Lupercales", "Beltane", "Samhain"),
            explanation = "Dedicadas al dios Saturno, las Saturnales invertían temporalmente las normas sociales con banquetes."
        ),
        "seed_soc_11" to LocalizedQuestionCopy(
            questionText = "¿Qué teatro tradicional indonesio de marionetas de sombra narra epopeyas como el Ramayana?",
            correctAnswer = "Wayang Kulit",
            incorrectAnswers = listOf("Kabuki", "Kathakali", "Bunraku"),
            explanation = "Interpretado por un maestro dalang detrás de una pantalla iluminada, combina música gamelán y filosofía."
        ),
        "seed_soc_12" to LocalizedQuestionCopy(
            questionText = "¿Qué rama de la filosofía estudia el origen, naturaleza y validez del conocimiento?",
            correctAnswer = "Epistemología",
            incorrectAnswers = listOf("Estética", "Metafísica", "Ontología"),
            explanation = "La epistemología analiza cómo sabemos lo que sabemos y qué justifica una creencia verdadera."
        ),
        "seed_soc_13" to LocalizedQuestionCopy(
            questionText = "¿Qué antiguo código legal mesopotámico grabado en basalto incluye el principio de 'ojo por ojo'?",
            correctAnswer = "Código de Hammurabi",
            incorrectAnswers = listOf("Doce Tablas", "Edictos de Ashoka", "Código de Justiniano"),
            explanation = "Redactado hacia 1750 a. C. en Babilonia, recopiló 282 leyes en escritura cuneiforme acadia."
        ),
        "seed_soc_14" to LocalizedQuestionCopy(
            questionText = "¿Qué arte tradicional polinesio navegaba por el Pacífico guiándose por estrellas y oleaje sin brújula?",
            correctAnswer = "Navegación tradicional polinesia",
            incorrectAnswers = listOf("Navegación por estima", "Triangulación láser", "Sondeo acústico"),
            explanation = "Los navegantes polinesios cruzaron miles de kilómetros de océano abierto en canoas de doble casco."
        ),
        "seed_soc_15" to LocalizedQuestionCopy(
            questionText = "En la cultura japonesa, ¿qué es el 'kintsugi'?",
            correctAnswer = "Reparar cerámica con laca de oro",
            incorrectAnswers = listOf("Arreglar flores estacionales", "Tallar sellos de madera", "Tejer cestas de bambú"),
            explanation = "El kintsugi honra las fracturas y reparaciones de una pieza como parte valiosa de su historia."
        )
    )
}
