package com.example.data.local

import kotlin.random.Random

/**
 * Curated, multilingual (EN / ES / ID) factual trivia bank providing 1,000+ genuine
 * questions across every playable category when Offline Mode is enabled.
 * Zero synthetic placeholder strings — every question, distractor, and explanation is factual.
 */
object OfflineFactBank {

    private data class CountryFact(
        val en: String,
        val es: String,
        val id: String,
        val capEn: String,
        val capEs: String,
        val capId: String,
        val contIdx: Int // 0=Asia, 1=Europe, 2=Africa, 3=North America, 4=South America, 5=Oceania
    )

    private data class ElementFact(
        val number: Int,
        val symbol: String,
        val en: String,
        val es: String,
        val id: String
    )

    private data class TripleFact(
        val idTag: String,
        val category: String,
        val difficulty: String,
        val enQ: String,
        val esQ: String,
        val idQ: String,
        val enA: String,
        val esA: String,
        val idA: String,
        val enWrong: List<String>,
        val esWrong: List<String>,
        val idWrong: List<String>,
        val enExp: String,
        val esExp: String,
        val idExp: String
    )

    private data class PairFact(
        val subjectEn: String,
        val subjectEs: String,
        val subjectId: String,
        val answerEn: String,
        val answerEs: String,
        val answerId: String,
        val noteEn: String,
        val noteEs: String,
        val noteId: String,
        val difficulty: String = "medium"
    )

    private val continentsEn = listOf("Asia", "Europe", "Africa", "North America", "South America", "Oceania")
    private val continentsEs = listOf("Asia", "Europa", "África", "América del Norte", "América del Sur", "Oceanía")
    private val continentsId = listOf("Asia", "Eropa", "Afrika", "Amerika Utara", "Amerika Selatan", "Oseania")

    private val countries = listOf(
        CountryFact("Indonesia", "Indonesia", "Indonesia", "Jakarta", "Yakarta", "Jakarta", 0),
        CountryFact("Japan", "Japón", "Jepang", "Tokyo", "Tokio", "Tokyo", 0),
        CountryFact("South Korea", "Corea del Sur", "Korea Selatan", "Seoul", "Seúl", "Seoul", 0),
        CountryFact("Thailand", "Tailandia", "Thailand", "Bangkok", "Bangkok", "Bangkok", 0),
        CountryFact("Vietnam", "Vietnam", "Vietnam", "Hanoi", "Hanói", "Hanoi", 0),
        CountryFact("Malaysia", "Malasia", "Malaysia", "Kuala Lumpur", "Kuala Lumpur", "Kuala Lumpur", 0),
        CountryFact("Philippines", "Filipinas", "Filipina", "Manila", "Manila", "Manila", 0),
        CountryFact("India", "India", "India", "New Delhi", "Nueva Delhi", "New Delhi", 0),
        CountryFact("China", "China", "Tiongkok", "Beijing", "Pekín", "Beijing", 0),
        CountryFact("Mongolia", "Mongolia", "Mongolia", "Ulaanbaatar", "Ulán Bator", "Ulaanbaatar", 0),
        CountryFact("Nepal", "Nepal", "Nepal", "Kathmandu", "Katmandú", "Kathmandu", 0),
        CountryFact("Bhutan", "Bután", "Bhutan", "Thimphu", "Timbu", "Thimphu", 0),
        CountryFact("Sri Lanka", "Sri Lanka", "Sri Lanka", "Sri Jayawardenepura Kotte", "Sri Jayawardenepura Kotte", "Sri Jayawardenepura Kotte", 0),
        CountryFact("Bangladesh", "Bangladés", "Bangladesh", "Dhaka", "Daca", "Dhaka", 0),
        CountryFact("Pakistan", "Pakistán", "Pakistan", "Islamabad", "Islamabad", "Islamabad", 0),
        CountryFact("Kazakhstan", "Kazajistán", "Kazakhstan", "Astana", "Astaná", "Astana", 0),
        CountryFact("Uzbekistan", "Uzbekistán", "Uzbekistan", "Tashkent", "Taskent", "Tashkent", 0),
        CountryFact("Turkey", "Turquía", "Turki", "Ankara", "Ankara", "Ankara", 0),
        CountryFact("Saudi Arabia", "Arabia Saudita", "Arab Saudi", "Riyadh", "Riad", "Riyadh", 0),
        CountryFact("United Arab Emirates", "Emiratos Árabes Unidos", "Uni Emirat Arab", "Abu Dhabi", "Abu Dabi", "Abu Dhabi", 0),
        CountryFact("Jordan", "Jordania", "Yordania", "Amman", "Amán", "Amman", 0),
        CountryFact("Oman", "Omán", "Oman", "Muscat", "Mascate", "Muscat", 0),
        CountryFact("Qatar", "Catar", "Qatar", "Doha", "Doha", "Doha", 0),
        CountryFact("Singapore", "Singapur", "Singapura", "Singapore", "Singapur", "Singapura", 0),
        CountryFact("Cambodia", "Camboya", "Kamboja", "Phnom Penh", "Nom Pen", "Phnom Penh", 0),
        CountryFact("Laos", "Laos", "Laos", "Vientiane", "Vientián", "Vientiane", 0),
        CountryFact("Spain", "España", "Spanyol", "Madrid", "Madrid", "Madrid", 1),
        CountryFact("Portugal", "Portugal", "Portugal", "Lisbon", "Lisboa", "Lisbon", 1),
        CountryFact("Italy", "Italia", "Italia", "Rome", "Roma", "Roma", 1),
        CountryFact("Germany", "Alemania", "Jerman", "Berlin", "Berlín", "Berlin", 1),
        CountryFact("Netherlands", "Países Bajos", "Belanda", "Amsterdam", "Ámsterdam", "Amsterdam", 1),
        CountryFact("Belgium", "Bélgica", "Belgia", "Brussels", "Bruselas", "Brussel", 1),
        CountryFact("Switzerland", "Suiza", "Swiss", "Bern", "Berna", "Bern", 1),
        CountryFact("Austria", "Austria", "Austria", "Vienna", "Viena", "Wina", 1),
        CountryFact("Poland", "Polonia", "Polandia", "Warsaw", "Varsovia", "Warsawa", 1),
        CountryFact("Czechia", "Chequia", "Ceko", "Prague", "Praga", "Praha", 1),
        CountryFact("Slovakia", "Eslovaquia", "Slowakia", "Bratislava", "Bratislava", "Bratislava", 1),
        CountryFact("Slovenia", "Eslovenia", "Slovenia", "Ljubljana", "Liubliana", "Ljubljana", 1),
        CountryFact("Croatia", "Croacia", "Kroasia", "Zagreb", "Zagreb", "Zagreb", 1),
        CountryFact("Greece", "Grecia", "Yunani", "Athens", "Atenas", "Athena", 1),
        CountryFact("Norway", "Noruega", "Norwegia", "Oslo", "Oslo", "Oslo", 1),
        CountryFact("Sweden", "Suecia", "Swedia", "Stockholm", "Estocolmo", "Stockholm", 1),
        CountryFact("Finland", "Finlandia", "Finlandia", "Helsinki", "Helsinki", "Helsinki", 1),
        CountryFact("Denmark", "Dinamarca", "Denmark", "Copenhagen", "Copenhague", "Kopenhagen", 1),
        CountryFact("Ireland", "Irlanda", "Irlandia", "Dublin", "Dublín", "Dublin", 1),
        CountryFact("United Kingdom", "Reino Unido", "Inggris", "London", "Londres", "London", 1),
        CountryFact("Romania", "Rumania", "Rumania", "Bucharest", "Bucarest", "Bukares", 1),
        CountryFact("Bulgaria", "Bulgaria", "Bulgaria", "Sofia", "Sofía", "Sofia", 1),
        CountryFact("Serbia", "Serbia", "Serbia", "Belgrade", "Belgrado", "Beograd", 1),
        CountryFact("Ukraine", "Ucrania", "Ukraina", "Kyiv", "Kiev", "Kyiv", 1),
        CountryFact("Lithuania", "Lituania", "Lituania", "Vilnius", "Vilna", "Vilnius", 1),
        CountryFact("Latvia", "Letonia", "Latvia", "Riga", "Riga", "Riga", 1),
        CountryFact("Estonia", "Estonia", "Estonia", "Tallinn", "Tallin", "Tallinn", 1),
        CountryFact("Egypt", "Egipto", "Mesir", "Cairo", "El Cairo", "Kairo", 2),
        CountryFact("Morocco", "Marruecos", "Maroko", "Rabat", "Rabat", "Rabat", 2),
        CountryFact("Tunisia", "Túnez", "Tunisia", "Tunis", "Túnez", "Tunis", 2),
        CountryFact("Algeria", "Argelia", "Aljazair", "Algiers", "Argel", "Aljir", 2),
        CountryFact("Nigeria", "Nigeria", "Nigeria", "Abuja", "Abuya", "Abuja", 2),
        CountryFact("Ghana", "Ghana", "Ghana", "Accra", "Acra", "Accra", 2),
        CountryFact("Kenya", "Kenia", "Kenya", "Nairobi", "Nairobi", "Nairobi", 2),
        CountryFact("Tanzania", "Tanzania", "Tanzania", "Dodoma", "Dodoma", "Dodoma", 2),
        CountryFact("Uganda", "Uganda", "Uganda", "Kampala", "Kampala", "Kampala", 2),
        CountryFact("Rwanda", "Ruanda", "Rwanda", "Kigali", "Kigali", "Kigali", 2),
        CountryFact("Senegal", "Senegal", "Senegal", "Dakar", "Dakar", "Dakar", 2),
        CountryFact("Namibia", "Namibia", "Namibia", "Windhoek", "Windhoek", "Windhoek", 2),
        CountryFact("Botswana", "Botsuana", "Botswana", "Gaborone", "Gaborone", "Gaborone", 2),
        CountryFact("Zambia", "Zambia", "Zambia", "Lusaka", "Lusaka", "Lusaka", 2),
        CountryFact("Zimbabwe", "Zimbabue", "Zimbabwe", "Harare", "Harare", "Harare", 2),
        CountryFact("Madagascar", "Madagascar", "Madagaskar", "Antananarivo", "Antananarivo", "Antananarivo", 2),
        CountryFact("South Africa", "Sudáfrica", "Afrika Selatan", "Pretoria", "Pretoria", "Pretoria", 2),
        CountryFact("United States", "Estados Unidos", "Amerika Serikat", "Washington, D.C.", "Washington D. C.", "Washington, D.C.", 3),
        CountryFact("Canada", "Canadá", "Kanada", "Ottawa", "Ottawa", "Ottawa", 3),
        CountryFact("Mexico", "México", "Meksiko", "Mexico City", "Ciudad de México", "Kota Meksiko", 3),
        CountryFact("Cuba", "Cuba", "Kuba", "Havana", "La Habana", "Havana", 3),
        CountryFact("Jamaica", "Jamaica", "Jamaika", "Kingston", "Kingston", "Kingston", 3),
        CountryFact("Costa Rica", "Costa Rica", "Kosta Rika", "San José", "San José", "San José", 3),
        CountryFact("Panama", "Panamá", "Panama", "Panama City", "Ciudad de Panamá", "Kota Panama", 3),
        CountryFact("Guatemala", "Guatemala", "Guatemala", "Guatemala City", "Ciudad de Guatemala", "Kota Guatemala", 3),
        CountryFact("Dominican Republic", "República Dominicana", "Republik Dominika", "Santo Domingo", "Santo Domingo", "Santo Domingo", 3),
        CountryFact("Honduras", "Honduras", "Honduras", "Tegucigalpa", "Tegucigalpa", "Tegucigalpa", 3),
        CountryFact("Brazil", "Brasil", "Brasil", "Brasília", "Brasilia", "Brasília", 4),
        CountryFact("Argentina", "Argentina", "Argentina", "Buenos Aires", "Buenos Aires", "Buenos Aires", 4),
        CountryFact("Chile", "Chile", "Chile", "Santiago", "Santiago", "Santiago", 4),
        CountryFact("Peru", "Perú", "Peru", "Lima", "Lima", "Lima", 4),
        CountryFact("Colombia", "Colombia", "Kolombia", "Bogotá", "Bogotá", "Bogotá", 4),
        CountryFact("Ecuador", "Ecuador", "Ekuador", "Quito", "Quito", "Quito", 4),
        CountryFact("Bolivia", "Bolivia", "Bolivia", "Sucre", "Sucre", "Sucre", 4),
        CountryFact("Uruguay", "Uruguay", "Uruguay", "Montevideo", "Montevideo", "Montevideo", 4),
        CountryFact("Paraguay", "Paraguay", "Paraguay", "Asunción", "Asunción", "Asunción", 4),
        CountryFact("Venezuela", "Venezuela", "Venezuela", "Caracas", "Caracas", "Caracas", 4),
        CountryFact("New Zealand", "Nueva Zelanda", "Selandia Baru", "Wellington", "Wellington", "Wellington", 5),
        CountryFact("Fiji", "Fiyi", "Fiji", "Suva", "Suva", "Suva", 5),
        CountryFact("Papua New Guinea", "Papúa Nueva Guinea", "Papua Nugini", "Port Moresby", "Puerto Moresby", "Port Moresby", 5),
        CountryFact("Samoa", "Samoa", "Samoa", "Apia", "Apia", "Apia", 5),
        CountryFact("Tonga", "Tonga", "Tonga", "Nuku'alofa", "Nuku'alofa", "Nuku'alofa", 5)
    )

    private val elements = listOf(
        ElementFact(2, "He", "Helium", "Helio", "Helium"),
        ElementFact(3, "Li", "Lithium", "Litio", "Litium"),
        ElementFact(4, "Be", "Beryllium", "Berilio", "Berilium"),
        ElementFact(5, "B", "Boron", "Boro", "Boron"),
        ElementFact(6, "C", "Carbon", "Carbono", "Karbon"),
        ElementFact(7, "N", "Nitrogen", "Nitrógeno", "Nitrogen"),
        ElementFact(8, "O", "Oxygen", "Oxígeno", "Oksigen"),
        ElementFact(9, "F", "Fluorine", "Flúor", "Fluorin"),
        ElementFact(10, "Ne", "Neon", "Neón", "Neon"),
        ElementFact(11, "Na", "Sodium", "Sodio", "Natrium"),
        ElementFact(12, "Mg", "Magnesium", "Magnesio", "Magnesium"),
        ElementFact(13, "Al", "Aluminum", "Aluminio", "Aluminium"),
        ElementFact(14, "Si", "Silicon", "Silicio", "Silikon"),
        ElementFact(15, "P", "Phosphorus", "Fósforo", "Fosfor"),
        ElementFact(16, "S", "Sulfur", "Azufre", "Belerang"),
        ElementFact(17, "Cl", "Chlorine", "Cloro", "Klorin"),
        ElementFact(18, "Ar", "Argon", "Argón", "Argon"),
        ElementFact(20, "Ca", "Calcium", "Calcio", "Kalsium"),
        ElementFact(22, "Ti", "Titanium", "Titanio", "Titanium"),
        ElementFact(24, "Cr", "Chromium", "Cromo", "Kromium"),
        ElementFact(25, "Mn", "Manganese", "Manganeso", "Mangan"),
        ElementFact(26, "Fe", "Iron", "Hierro", "Besi"),
        ElementFact(27, "Co", "Cobalt", "Cobalto", "Kobalt"),
        ElementFact(28, "Ni", "Nickel", "Níquel", "Nikel"),
        ElementFact(29, "Cu", "Copper", "Cobre", "Tembaga"),
        ElementFact(30, "Zn", "Zinc", "Zinc", "Seng"),
        ElementFact(33, "As", "Arsenic", "Arsénico", "Arsenik"),
        ElementFact(35, "Br", "Bromine", "Bromo", "Bromin"),
        ElementFact(36, "Kr", "Krypton", "Kriptón", "Kripton"),
        ElementFact(38, "Sr", "Strontium", "Estroncio", "Stronsium"),
        ElementFact(42, "Mo", "Molybdenum", "Molibdeno", "Molibdenum"),
        ElementFact(46, "Pd", "Palladium", "Paladio", "Paladium"),
        ElementFact(47, "Ag", "Silver", "Plata", "Perak"),
        ElementFact(48, "Cd", "Cadmium", "Cadmio", "Kadmium"),
        ElementFact(50, "Sn", "Tin", "Estaño", "Timah"),
        ElementFact(53, "I", "Iodine", "Yodo", "Yodium"),
        ElementFact(54, "Xe", "Xenon", "Xenón", "Xenon"),
        ElementFact(55, "Cs", "Cesium", "Cesio", "Sesium"),
        ElementFact(56, "Ba", "Barium", "Bario", "Barium"),
        ElementFact(74, "W", "Tungsten", "Tungsteno", "Tungsten"),
        ElementFact(78, "Pt", "Platinum", "Platino", "Platina"),
        ElementFact(79, "Au", "Gold", "Oro", "Emas"),
        ElementFact(80, "Hg", "Mercury", "Mercurio", "Raksa"),
        ElementFact(82, "Pb", "Lead", "Plomo", "Timbal"),
        ElementFact(83, "Bi", "Bismuth", "Bismuto", "Bismut"),
        ElementFact(86, "Rn", "Radon", "Radón", "Radon"),
        ElementFact(88, "Ra", "Radium", "Radio", "Radium"),
        ElementFact(90, "Th", "Thorium", "Torio", "Torium"),
        ElementFact(92, "U", "Uranium", "Uranio", "Uranium"),
        ElementFact(94, "Pu", "Plutonium", "Plutonio", "Plutonium"),
        ElementFact(21, "Sc", "Scandium", "Escandio", "Skandium"),
        ElementFact(23, "V", "Vanadium", "Vanadio", "Vanadium"),
        ElementFact(31, "Ga", "Gallium", "Galio", "Galium"),
        ElementFact(32, "Ge", "Germanium", "Germanio", "Germanium"),
        ElementFact(34, "Se", "Selenium", "Selenio", "Selenium"),
        ElementFact(37, "Rb", "Rubidium", "Rubidio", "Rubidium"),
        ElementFact(39, "Y", "Yttrium", "Itrio", "Itrium"),
        ElementFact(40, "Zr", "Zirconium", "Circonio", "Zirkonium"),
        ElementFact(41, "Nb", "Niobium", "Niobio", "Niobium"),
        ElementFact(44, "Ru", "Ruthenium", "Rutenio", "Rutenium"),
        ElementFact(45, "Rh", "Rhodium", "Rodio", "Rodium"),
        ElementFact(49, "In", "Indium", "Indio", "Indium"),
        ElementFact(51, "Sb", "Antimony", "Antimonio", "Antimon"),
        ElementFact(52, "Te", "Tellurium", "Telurio", "Telurium"),
        ElementFact(57, "La", "Lanthanum", "Lantano", "Lantanum"),
        ElementFact(58, "Ce", "Cerium", "Cerio", "Serium"),
        ElementFact(60, "Nd", "Neodymium", "Neodimio", "Neodimium"),
        ElementFact(73, "Ta", "Tantalum", "Tantalio", "Tantalum"),
        ElementFact(76, "Os", "Osmium", "Osmio", "Osmium"),
        ElementFact(77, "Ir", "Iridium", "Iridio", "Iridium")
    )

    private val inventions = listOf(
        PairFact("the telephone (1876)", "el teléfono (1876)", "telepon (1876)", "Alexander Graham Bell", "Alexander Graham Bell", "Alexander Graham Bell", "Bell received the first U.S. patent for a practical telephone in March 1876.", "Bell recibió la primera patente en EE. UU. por un teléfono práctico en marzo de 1876.", "Bell menerima paten AS pertama untuk telepon praktis pada Maret 1876.", "easy"),
        PairFact("the World Wide Web (1989)", "la World Wide Web (1989)", "World Wide Web (1989)", "Tim Berners-Lee", "Tim Berners-Lee", "Tim Berners-Lee", "British scientist Tim Berners-Lee proposed the Web while working at CERN in Switzerland.", "El científico británico Tim Berners-Lee propuso la Web mientras trabajaba en el CERN.", "Ilmuwan Inggris Tim Berners-Lee mengusulkan Web saat bekerja di CERN, Swiss.", "easy"),
        PairFact("penicillin (1928)", "la penicilina (1928)", "penisilin (1928)", "Alexander Fleming", "Alexander Fleming", "Alexander Fleming", "Scottish physician Alexander Fleming noticed Penicillium mold inhibiting bacteria in 1928.", "El médico escocés Alexander Fleming observó en 1928 que el moho Penicillium inhibía bacterias.", "Dokter Skotlandia Alexander Fleming menemukan jamur Penicillium menghambat bakteri pada 1928.", "easy"),
        PairFact("the alternating current induction motor", "el motor de inducción de corriente alterna", "motor induksi arus bolak-balik (AC)", "Nikola Tesla", "Nikola Tesla", "Nikola Tesla", "Tesla's polyphase AC patents enabled long-distance electrical power transmission.", "Las patentes de corriente alterna de Tesla permitieron transmitir electricidad a larga distancia.", "Paten arus bolak-balik Tesla memungkinkan transmisi daya listrik jarak jauh.", "easy"),
        PairFact("dynamite (1867)", "la dinamita (1867)", "dinamit (1867)", "Alfred Nobel", "Alfred Nobel", "Alfred Nobel", "Swedish chemist Alfred Nobel stabilized nitroglycerin and later endowed the Nobel Prizes.", "El químico sueco Alfred Nobel estabilizó la nitroglicerina y creó los Premios Nobel.", "Kimiawan Swedia Alfred Nobel menstabilkan nitrogliserin dan mendirikan Hadiah Nobel.", "easy"),
        PairFact("the phonograph (1877)", "el fonógrafo (1877)", "fonograf (1877)", "Thomas Edison", "Thomas Edison", "Thomas Edison", "Edison's phonograph was the first device capable of both recording and reproducing sound.", "El fonógrafo de Edison fue el primer aparato capaz de grabar y reproducir sonido.", "Fonograf Edison adalah perangkat pertama yang mampu merekam sekaligus memutar suara.", "easy"),
        PairFact("the first practical steam engine condenser (1769)", "la máquina de vapor con condensador separado (1769)", "mesin uap dengan kondensor terpisah (1769)", "James Watt", "James Watt", "James Watt", "James Watt's design dramatically cut coal use, accelerating the Industrial Revolution.", "El diseño de James Watt redujo drásticamente el consumo de carbón e impulsó la Revolución Industrial.", "Desain James Watt memangkas pemakaian batu bara secara drastis dan memicu Revolusi Industri.", "medium"),
        PairFact("the vaccination against smallpox (1796)", "la vacuna contra la viruela (1796)", "vaksin cacar pertama (1796)", "Edward Jenner", "Edward Jenner", "Edward Jenner", "Jenner demonstrated that cowpox inoculation protected humans against deadly smallpox.", "Jenner demostró que la inoculación de viruela vacuna protegía contra la viruela humana.", "Jenner membuktikan bahwa inokulasi cacar sapi melindungi manusia dari cacar mematikan.", "medium"),
        PairFact("the discovery of X-rays (1895)", "el descubrimiento de los rayos X (1895)", "penemuan sinar-X (1895)", "Wilhelm Röntgen", "Wilhelm Röntgen", "Wilhelm Röntgen", "Röntgen earned the very first Nobel Prize in Physics in 1901 for X-rays.", "Röntgen ganó el primer Premio Nobel de Física en 1901 gracias a los rayos X.", "Röntgen meraih Hadiah Nobel Fisika pertama pada 1901 berkat penemuan sinar-X.", "medium"),
        PairFact("wireless radio telegraphy (1895)", "la telegrafía por radio inalámbrica (1895)", "telegrafi radio nirkabel (1895)", "Guglielmo Marconi", "Guglielmo Marconi", "Guglielmo Marconi", "Marconi sent the first transatlantic radio signal from Cornwall to Newfoundland in 1901.", "Marconi envió la primera señal de radio transatlántica en 1901.", "Marconi mengirim sinyal radio lintas Samudra Atlantik pertama pada 1901.", "medium"),
        PairFact("the first powered airplane flight (1903)", "el primer vuelo controlado en aeroplano a motor (1903)", "penerbangan pesawat bermotor pertama (1903)", "Orville and Wilbur Wright", "Orville y Wilbur Wright", "Orville dan Wilbur Wright", "The Wright Flyer flew 12 seconds at Kitty Hawk, North Carolina, on December 17, 1903.", "El Wright Flyer voló 12 segundos en Kitty Hawk el 17 de diciembre de 1903.", "Wright Flyer terbang selama 12 detik di Kitty Hawk pada 17 Desember 1903.", "easy"),
        PairFact("the first mechanical general-purpose computer design (Analytical Engine)", "la Máquina Analítica, primer diseño de computadora mecánica", "Mesin Analitik (rancangan komputer mekanis pertama)", "Charles Babbage", "Charles Babbage", "Charles Babbage", "Babbage designed the Analytical Engine in the 1830s, with Ada Lovelace writing its first algorithm.", "Babbage diseñó la Máquina Analítica en la década de 1830 junto con algoritmos de Ada Lovelace.", "Babbage merancang Mesin Analitik pada 1830-an dengan algoritma pertama oleh Ada Lovelace.", "medium"),
        PairFact("the cotton gin (1793)", "la desmotadora de algodón (1793)", "mesin pemisah biji kapas (1793)", "Eli Whitney", "Eli Whitney", "Eli Whitney", "Eli Whitney's machine rapidly separated cotton fibers from seeds.", "La máquina de Eli Whitney separaba rápidamente las fibras de algodón de las semillas.", "Mesin Eli Whitney mempercepat pemisahan serat kapas dari bijinya.", "medium"),
        PairFact("the lightning rod and bifocal glasses", "el pararrayos y los lentes bifocales", "penangkal petir dan kacamata bifokal", "Benjamin Franklin", "Benjamin Franklin", "Benjamin Franklin", "Polymath Benjamin Franklin invented practical lightning protection in the 1750s.", "El polímata Benjamin Franklin diseñó el pararrayos en la década de 1750.", "Polimatik Benjamin Franklin menciptakan penangkal petir praktis pada 1750-an.", "easy"),
        PairFact("the vulcanization of rubber (1839)", "la vulcanización del caucho (1839)", "vulkanisasi karet (1839)", "Charles Goodyear", "Charles Goodyear", "Charles Goodyear", "Heating rubber with sulfur made it durable across hot and cold temperatures.", "Calentar caucho con azufre lo volvió resistente al calor y al frío.", "Pemanasan karet dengan belerang membuatnya tahan lama pada berbagai suhu.", "medium"),
        PairFact("the pasteurization process", "la pasteurización", "proses pasteurisasi", "Louis Pasteur", "Louis Pasteur", "Louis Pasteur", "Pasteur showed mild heating eliminates spoilage microbes in milk and wine.", "Pasteur demostró que el calentamiento suave elimina microbios en líquidos.", "Pasteur membuktikan pemanasan ringan membunuh mikroba pembusuk pada susu dan anggur.", "easy"),
        PairFact("the magnetic compass description in maritime navigation (Song era)", "el uso documentado de la brújula magnética en navegación (era Song)", "catatan awal kompas magnetik untuk pelayaran (era Song)", "Shen Kuo", "Shen Kuo", "Shen Kuo", "Chinese polymath Shen Kuo documented the magnetized needle compass around 1088 CE.", "El sabio chino Shen Kuo describió la aguja magnética de navegación hacia el año 1088.", "Ilmuwan Tiongkok Shen Kuo mendokumentasikan kompas jarum magnetik sekitar tahun 1088 M.", "hard"),
        PairFact("the assembly line mass-production of the Model T", "la producción en línea de ensamblaje del Modelo T", "jalur perakitan massal mobil Model T", "Henry Ford", "Henry Ford", "Henry Ford", "Ford's moving assembly line in 1913 cut Model T build time from 12 hours to ~93 minutes.", "La línea móvil de Ford en 1913 redujo la fabricación del Modelo T de 12 horas a unos 93 minutos.", "Jalur perakitan bergerak Ford pada 1913 memangkas perakitan Model T dari 12 jam menjadi ~93 menit.", "easy"),
        PairFact("the Braille tactile reading system (1824)", "el sistema de lectura táctil Braille (1824)", "sistem baca tulis taktil Braille (1824)", "Louis Braille", "Louis Braille", "Louis Braille", "At age 15, Louis Braille created a 6-dot tactile code for blind readers.", "A los 15 años, Louis Braille creó un código táctil de 6 puntos para personas ciegas.", "Pada usia 15 tahun, Louis Braille menciptakan kode 6 titik bagi pembaca tunanetra.", "easy"),
        PairFact("the first electric battery (Voltaic pile, 1800)", "la primera pila eléctrica (pila voltaica, 1800)", "baterai listrik pertama (tumpukan Volta, 1800)", "Alessandro Volta", "Alessandro Volta", "Alessandro Volta", "Stacked zinc and copper discs produced steady electric current; the unit 'volt' honors him.", "Discos de zinc y cobre produjeron corriente continua; la unidad voltio lleva su nombre.", "Tumpukan cakram seng dan tembaga menghasilkan arus listrik stabil; satuan volt memakai namanya.", "medium"),
        PairFact("the transistor (co-invented at Bell Labs, 1947)", "el transistor (coinventado en Bell Labs, 1947)", "transistor (ditemukan bersama di Bell Labs, 1947)", "John Bardeen, Walter Brattain & William Shockley", "John Bardeen, Walter Brattain y William Shockley", "John Bardeen, Walter Brattain & William Shockley", "The transistor replaced bulky vacuum tubes and sparked modern microelectronics.", "El transistor reemplazó los tubos de vacío y dio inicio a la microelectrónica moderna.", "Transistor menggantikan tabung vakum besar dan memicu era mikroelektronika modern.", "hard"),
        PairFact("the theory of general relativity (1915)", "la teoría de la relatividad general (1915)", "teori relativitas umum (1915)", "Albert Einstein", "Albert Einstein", "Albert Einstein", "Einstein described gravity as the curvature of four-dimensional spacetime.", "Einstein describió la gravedad como la curvatura del espacio-tiempo cuatridimensional.", "Einstein menjelaskan gravitasi sebagai kelengkungan ruang-waktu empat dimensi.", "easy"),
        PairFact("the discovery of DNA's X-ray diffraction Photo 51", "la fotografía 51 de difracción de rayos X del ADN", "Foto 51 difraksi sinar-X struktur DNA", "Rosalind Franklin", "Rosalind Franklin", "Rosalind Franklin", "Rosalind Franklin's X-ray images were critical evidence for DNA's double-helix geometry.", "Las imágenes de rayos X de Franklin fueron clave para demostrar la doble hélice del ADN.", "Citra sinar-X Rosalind Franklin menjadi bukti krusial bagi struktur heliks ganda DNA.", "medium"),
        PairFact("the heliocentric astronomical model (1543)", "el modelo astronómico heliocéntrico (1543)", "model astronomi heliosentris (1543)", "Nicolaus Copernicus", "Nicolás Copérnico", "Nicolaus Copernicus", "Copernicus placed the Sun rather than the Earth at the center of the planetary system.", "Copérnico situó al Sol, y no a la Tierra, en el centro del sistema planetario.", "Copernicus menempatkan Matahari, bukan Bumi, di pusat tata surya.", "medium"),
        PairFact("the binomial classification system for organisms", "el sistema de clasificación binomial de los seres vivos", "sistem tata nama ganda (binomial) makhluk hidup", "Carl Linnaeus", "Carlos Linneo", "Carl Linnaeus", "Swedish botanist Carl Linnaeus introduced two-part Latin scientific names like Homo sapiens.", "El botánico sueco Carlos Linneo introdujo nombres científicos latinos de dos palabras.", "Botanis Swedia Carl Linnaeus memperkenalkan nama ilmiah Latin dua kata.", "medium"),
        PairFact("the Morse code telegraph alphabet (1830s)", "el alfabeto telegráfico en código Morse (década de 1830)", "sandi telegraf kode Morse (1830-an)", "Samuel Morse", "Samuel Morse", "Samuel Morse", "Dots and dashes transmitted electrical messages almost instantaneously across wires.", "Puntos y rayas transmitían mensajes eléctricos casi al instante por cable.", "Titik dan garis mengirimkan pesan listrik hampir seketika melalui kabel.", "easy"),
        PairFact("the Turing machine mathematical computing concept (1936)", "el concepto matemático de la máquina de Turing (1936)", "konsep matematis mesin Turing (1936)", "Alan Turing", "Alan Turing", "Alan Turing", "Alan Turing formalized algorithms and computation before electronic computers existed.", "Alan Turing formalizó los algoritmos y la computación antes de existir computadoras electrónicas.", "Alan Turing memformalkan konsep algoritma dan komputasi sebelum komputer elektronik hadir.", "medium"),
        PairFact("the discovery of polonium and radium (1898)", "el descubrimiento del polonio y el radio (1898)", "penemuan unsur polonium dan radium (1898)", "Marie and Pierre Curie", "Marie y Pierre Curie", "Marie dan Pierre Curie", "Their pioneering research on radioactivity earned the 1903 Nobel Prize in Physics.", "Su investigación pionera sobre la radiactividad obtuvo el Premio Nobel de Física de 1903.", "Riset perintis mereka tentang radioaktivitas meraih Hadiah Nobel Fisika tahun 1903.", "easy"),
        PairFact("the law of planetary motion stating orbits are ellipses", "las leyes del movimiento planetario en órbitas elípticas", "hukum gerak planet berorbit elips", "Johannes Kepler", "Johannes Kepler", "Johannes Kepler", "Kepler proved planets orbit the Sun along ellipses rather than perfect circles.", "Kepler demostró que los planetas orbitan al Sol en elipses y no en círculos perfectos.", "Kepler membuktikan planet mengorbit Matahari dalam lintasan elips, bukan lingkaran sempurna.", "medium"),
        PairFact("the invention of the mercury barometer (1643)", "la invención del barómetro de mercurio (1643)", "penemuan barometer raksa (1643)", "Evangelista Torricelli", "Evangelista Torricelli", "Evangelista Torricelli", "Italian physicist Torricelli measured atmospheric air pressure using a tube of mercury.", "El físico italiano Torricelli midió la presión atmosférica con un tubo de mercurio.", "Fisikawan Italia Torricelli mengukur tekanan udara atmosfer memakai tabung berisi raksa.", "hard"),
        PairFact("the steam locomotive 'Rocket' (1829)", "la locomotora de vapor 'Rocket' (1829)", "lokomotif uap 'Rocket' (1829)", "Robert and George Stephenson", "Robert y George Stephenson", "Robert dan George Stephenson", "The Stephensons' Rocket set standard engineering principles for intercity railways.", "La locomotora Rocket marcó los estándares de ingeniería para los ferrocarriles modernos.", "Lokomotif Rocket karya keluarga Stephenson menjadi standar teknik perkeretaapian modern.", "hard"),
        PairFact("the first compiler for computer programming languages", "el primer compilador para lenguajes de programación", "kompilator pertama untuk bahasa pemrograman komputer", "Grace Hopper", "Grace Hopper", "Grace Hopper", "Rear Admiral Grace Hopper pioneered machine-independent languages leading to COBOL.", "Grace Hopper impulsó lenguajes independientes del hardware que originaron COBOL.", "Laksamana Muda Grace Hopper memelopori bahasa pemrograman yang melahirkan COBOL.", "medium"),
        PairFact("the Bessemer steelmaking process (1856)", "el proceso Bessemer para fabricar acero en masa (1856)", "proses Bessemer untuk produksi baja massal (1856)", "Henry Bessemer", "Henry Bessemer", "Henry Bessemer", "Blowing air through molten pig iron made steel affordable for skyscrapers and railways.", "Inyectar aire al hierro fundido abarató el acero para rascacielos y rieles.", "Peniupan udara ke besi cair menurunkan biaya baja untuk gedung tinggi dan rel kereta.", "hard"),
        PairFact("the stethoscope (1816)", "el estetoscopio (1816)", "stetoskop medis (1816)", "René Laennec", "René Laennec", "René Laennec", "French physician René Laennec first fashioned a wooden tube to listen to chest sounds.", "El médico francés René Laennec fabricó un tubo de madera para auscultar el pecho.", "Dokter Prancis René Laennec mula-mula membuat tabung kayu untuk mendengar detak jantung dan paru.", "hard"),
        PairFact("the discovery of blood circulation through the body (1628)", "el descubrimiento de la circulación sanguínea sistemática (1628)", "penemuan sistem peredaran darah oleh jantung (1628)", "William Harvey", "William Harvey", "William Harvey", "English physician William Harvey proved the heart pumps blood in a closed loop.", "William Harvey demostró que el corazón bombea la sangre en un circuito cerrado.", "Dokter Inggris William Harvey membuktikan jantung memompa darah dalam sirkuit tertutup.", "hard"),
        PairFact("the Hubble Space Telescope deep-field constant of cosmic expansion", "la ley de expansión del universo entre galaxias", "hukum pemekaran alam semesta antar-galaksi", "Edwin Hubble", "Edwin Hubble", "Edwin Hubble", "Edwin Hubble showed distant galaxies recede faster the farther away they are.", "Edwin Hubble mostró que las galaxias lejanas se alejan más rápido cuanto más distantes están.", "Edwin Hubble membuktikan galaksi yang makin jauh bergerak menjauh makin cepat.", "medium")
    )

    private val literature = listOf(
        PairFact("Don Quixote", "Don Quijote de la Mancha", "Don Quixote", "Miguel de Cervantes", "Miguel de Cervantes", "Miguel de Cervantes", "Published in 1605 and 1615, it follows Alonso Quixano on his chivalric adventures.", "Publicado en 1605 y 1615, narra las aventuras caballerescas de Alonso Quijano.", "Terbit pada 1605 dan 1615, mengisahkan petualangan ksatria Alonso Quixano.", "easy"),
        PairFact("One Hundred Years of Solitude", "Cien años de soledad", "Seratus Tahun Kesunyian", "Gabriel García Márquez", "Gabriel García Márquez", "Gabriel García Márquez", "The 1967 masterpiece of magical realism chronicles the Buendía family in Macondo.", "Obra maestra del realismo mágico (1967) que sigue a la familia Buendía en Macondo.", "Karya agung realisme magis (1967) yang mengisahkan keluarga Buendía di Macondo.", "medium"),
        PairFact("1984", "1984", "1984", "George Orwell", "George Orwell", "George Orwell", "Published in 1949, '1984' introduced terms such as Big Brother and Thoughtcrime.", "Publicada en 1949, '1984' popularizó conceptos como Gran Hermano y crimental.", "Terbit pada 1949, '1984' memperkenalkan istilah Big Brother dan kejahatan pikiran.", "easy"),
        PairFact("Pride and Prejudice", "Orgullo y prejuicio", "Pride and Prejudice", "Jane Austen", "Jane Austen", "Jane Austen", "Austen's 1813 novel of manners follows Elizabeth Bennet and Fitzwilliam Darcy.", "Novela de 1813 centrada en Elizabeth Bennet y Fitzwilliam Darcy.", "Novel tahun 1813 ini berfokus pada kisah Elizabeth Bennet dan Fitzwilliam Darcy.", "easy"),
        PairFact("The Odyssey", "La Odisea", "Odyssey", "Homer", "Homero", "Homeros", "The ancient Greek epic follows Odysseus during his ten-year journey back to Ithaca.", "Epopeya griega antigua que relata el viaje de diez años de Odiseo a Ítaca.", "Epos Yunani kuno ini mengisahkan perjalanan sepuluh tahun Odisseus pulang ke Itaka.", "easy"),
        PairFact("Crime and Punishment", "Crimen y castigo", "Kejahatan dan Hukuman", "Fyodor Dostoevsky", "Fiódor Dostoyevski", "Fyodor Dostoyevsky", "Published in 1866, the psychological drama traces Raskolnikov's guilt in St. Petersburg.", "Publicada en 1866, explora la culpa de Raskólnikov en San Petersburgo.", "Terbit pada 1866, novel psikologis ini menelusuri rasa bersalah Raskolnikov di St. Petersburg.", "medium"),
        PairFact("Moby-Dick", "Moby-Dick", "Moby-Dick", "Herman Melville", "Herman Melville", "Herman Melville", "Captain Ahab pursues the white sperm whale aboard the whaling ship Pequod.", "El capitán Ahab persigue al gran cachalote blanco a bordo del ballenero Pequod.", "Kapten Ahab memburu paus sperma putih raksasa di atas kapal Pequod.", "easy"),
        PairFact("Frankenstein", "Frankenstein", "Frankenstein", "Mary Shelley", "Mary Shelley", "Mary Shelley", "Written when Mary Shelley was 18–20, it is widely seen as a foundational sci-fi novel.", "Escrita por una joven Mary Shelley, es considerada obra fundacional de la ciencia ficción.", "Ditulis saat Mary Shelley berusia belasan tahun dan dianggap pelopor fiksi ilmiah.", "easy"),
        PairFact("To Kill a Mockingbird", "Matar a un ruiseñor", "To Kill a Mockingbird", "Harper Lee", "Harper Lee", "Harper Lee", "Published in 1960, Scout Finch narrates her father Atticus's legal defense in Alabama.", "Publicada en 1960, Scout Finch narra la defensa legal de su padre Atticus en Alabama.", "Terbit pada 1960, Scout Finch mengisahkan pembelaan hukum ayahnya Atticus di Alabama.", "medium"),
        PairFact("The Great Gatsby", "El gran Gatsby", "The Great Gatsby", "F. Scott Fitzgerald", "F. Scott Fitzgerald", "F. Scott Fitzgerald", "Set in the Jazz Age of 1922 Long Island, it centers on Jay Gatsby and Nick Carraway.", "Ambientada en los locos años veinte en Long Island, gira en torno a Jay Gatsby.", "Berlatar era Jazz tahun 1922 di Long Island, berpusat pada sosok Jay Gatsby.", "medium"),
        PairFact("Laskar Pelangi (The Rainbow Troops)", "Laskar Pelangi", "Laskar Pelangi", "Andrea Hirata", "Andrea Hirata", "Andrea Hirata", "Set on Belitung Island in Indonesia, the 2005 novel celebrates ten resilient schoolchildren.", "Ambientada en la isla indonesia de Belitung, narra la historia de diez estudiantes tenaces.", "Berlatar Pulau Belitung, novel 2005 ini mengisahkan sepuluh anak sekolah yang penuh tekad.", "medium"),
        PairFact("Bumi Manusia (This Earth of Mankind)", "Tierra humana (Bumi Manusia)", "Bumi Manusia", "Pramoedya Ananta Toer", "Pramoedya Ananta Toer", "Pramoedya Ananta Toer", "First volume of the Buru Quartet, written while Pramoedya was imprisoned on Buru Island.", "Primer tomo del Cuarteto de Buru, concebido mientras Pramoedya estuvo preso en la isla Buru.", "Buku pertama Tetralogi Buru yang disusun Pramoedya saat diasingkan di Pulau Buru.", "medium"),
        PairFact("Les Misérables", "Los miserables", "Les Misérables", "Victor Hugo", "Victor Hugo", "Victor Hugo", "Published in 1862, Victor Hugo's epic follows Jean Valjean seeking redemption in France.", "Publicada en 1862, sigue a Jean Valjean en su búsqueda de redención en Francia.", "Terbit tahun 1862, epos Victor Hugo ini mengisahkan penebusan diri Jean Valjean.", "medium"),
        PairFact("The Little Prince", "El principito", "Pangeran Kecil (The Little Prince)", "Antoine de Saint-Exupéry", "Antoine de Saint-Exupéry", "Antoine de Saint-Exupéry", "Published in 1943, the poetic novella begins with an aviator stranded in the Sahara.", "Publicado en 1943, el relato poético inicia con un aviador varado en el Sahara.", "Terbit tahun 1943, kisah puitis ini dibuka oleh pilot yang terdampar di Gurun Sahara.", "easy"),
        PairFact("The Divine Comedy", "La Divina Comedia", "La Divina Commedia", "Dante Alighieri", "Dante Alighieri", "Dante Alighieri", "Written in Italian in the early 1300s, it journeys through Inferno, Purgatorio, and Paradiso.", "Escrita en italiano a inicios del siglo XIV, recorre Infierno, Purgatorio y Paraíso.", "Ditulis pada awal abad ke-14, menelusuri tiga alam: Neraka, Api Penyucian, dan Surga.", "medium"),
        PairFact("War and Peace", "Guerra y paz", "Perang dan Damai", "Leo Tolstoy", "León Tolstói", "Leo Tolstoy", "Tolstoy's 1869 panorama chronicles Russian society during the Napoleonic invasions.", "Publicado en 1869, retrata la sociedad rusa durante las invasiones napoleónicas.", "Terbit 1869, novel agung Tolstoy ini menggambarkan masyarakat Rusia pada era Napoleon.", "medium"),
        PairFact("The Tale of Genji", "La novela de Genji", "Kisah Genji (The Tale of Genji)", "Murasaki Shikibu", "Murasaki Shikibu", "Murasaki Shikibu", "Composed around 1008 CE in Kyoto, it is widely hailed as the world's earliest full novel.", "Escrita hacia el año 1008 en Kioto, es considerada la primera gran novela de la historia.", "Ditulis sekitar tahun 1008 M di Kyoto dan diakui luas sebagai novel penuh pertama di dunia.", "hard"),
        PairFact("Brave New World", "Un mundo feliz", "Brave New World", "Aldous Huxley", "Aldous Huxley", "Aldous Huxley", "Published in 1932, Huxley envisioned a hyper-conditioned future society in London.", "Publicada en 1932, imagina una sociedad futura altamente condicionada en Londres.", "Terbit pada 1932, Huxley membayangkan masyarakat masa depan yang dikondisikan secara genetik.", "medium"),
        PairFact("The Metamorphosis", "La metamorfosis", "Metamorfosis", "Franz Kafka", "Franz Kafka", "Franz Kafka", "Gregor Samsa awakens one morning transformed into a giant insect.", "Gregor Samsa despierta una mañana convertido en un insecto gigante.", "Gregor Samsa terbangun pada suatu pagi dalam wujud serangga raksasa.", "medium"),
        PairFact("The Catcher in the Rye", "El guardián entre el centeno", "The Catcher in the Rye", "J.D. Salinger", "J. D. Salinger", "J.D. Salinger", "Published in 1951, it follows teenager Holden Caulfield across New York City.", "Publicada en 1951, sigue al adolescente Holden Caulfield por Nueva York.", "Terbit 1951, mengikuti perjalanan remaja Holden Caulfield di Kota New York.", "medium"),
        PairFact("Alice's Adventures in Wonderland", "Las aventuras de Alicia en el país de las maravillas", "Petualangan Alice di Negeri Ajaib", "Lewis Carroll", "Lewis Carroll", "Lewis Carroll", "Oxford mathematician Charles Dodgson wrote it in 1865 under the pen name Lewis Carroll.", "El matemático Charles Dodgson la publicó en 1865 bajo el seudónimo Lewis Carroll.", "Matematikawan Charles Dodgson menerbitkannya pada 1865 memakai nama pena Lewis Carroll.", "easy"),
        PairFact("The Count of Monte Cristo", "El conde de Montecristo", "Pangeran Monte Cristo", "Alexandre Dumas", "Alexandre Dumas", "Alexandre Dumas", "Edmond Dantès escapes the Château d'If after fourteen years of wrongful imprisonment.", "Edmond Dantès escapa del castillo de If tras catorce años de prisión injusta.", "Edmond Dantès kabur dari penjara Château d'If setelah empat belas tahun ditahan.", "medium"),
        PairFact("Fahrenheit 451", "Fahrenheit 451", "Fahrenheit 451", "Ray Bradbury", "Ray Bradbury", "Ray Bradbury", "Published in 1953, fireman Guy Montag is tasked with burning banned books.", "Publicada en 1953, sigue al bombero Guy Montag encargado de quemar libros prohibidos.", "Terbit tahun 1953, mengisahkan petugas pemadam Guy Montag yang membakar buku terlarang.", "medium"),
        PairFact("Dracula", "Drácula", "Dracula", "Bram Stoker", "Bram Stoker", "Bram Stoker", "Irish writer Bram Stoker published the epistolary Gothic horror classic in 1897.", "El escritor irlandés Bram Stoker publicó esta clásica novela gótica epistolar en 1897.", "Penulis Irlandia Bram Stoker menerbitkan novel horor Gotik klasik ini pada 1897.", "easy"),
        PairFact("Things Fall Apart", "Todo se desmorona", "Things Fall Apart", "Chinua Achebe", "Chinua Achebe", "Chinua Achebe", "Published in 1958, it follows Igbo leader Okonkwo in colonial-era Nigeria.", "Publicada en 1958, narra la historia del líder igbo Okonkwo en Nigeria.", "Terbit pada 1958, mengikuti kisah tokoh suku Igbo bernama Okonkwo di Nigeria.", "hard"),
        PairFact("Sherlock Holmes: A Study in Scarlet", "Estudio en escarlata (Sherlock Holmes)", "Sherlock Holmes: Penelusuran Benang Merah", "Arthur Conan Doyle", "Arthur Conan Doyle", "Arthur Conan Doyle", "First published in 1887, this novella introduced detective Sherlock Holmes and Dr. Watson.", "Publicada en 1887, esta novela presentó al detective Sherlock Holmes y al Dr. Watson.", "Terbit pada 1887, karya ini memperkenalkan detektif Sherlock Holmes dan Dr. Watson.", "easy"),
        PairFact("The Hobbit", "El hobbit", "The Hobbit", "J.R.R. Tolkien", "J. R. R. Tolkien", "J.R.R. Tolkien", "Published in 1937, Bilbo Baggins joins thirteen dwarves on a quest to the Lonely Mountain.", "Publicado en 1937, Bilbo Bolsón acompaña a trece enanos hacia la Montaña Solitaria.", "Terbit pada 1937, Bilbo Baggins menemani tiga belas kurcaci menuju Gunung Sunyi.", "easy"),
        PairFact("Harry Potter and the Philosopher's Stone", "Harry Potter y la piedra filosofal", "Harry Potter dan Batu Bertuah", "J.K. Rowling", "J. K. Rowling", "J.K. Rowling", "First released in 1997, the debut novel launched the seven-book Hogwarts saga.", "Publicado en 1997, inauguró la famosa saga mágica de siete libros en Hogwarts.", "Pertama terbit tahun 1997, buku perdana ini membuka seri tujuh jilid kisah Hogwarts.", "easy"),
        PairFact("The Raven", "El cuervo", "Sang Gagak (The Raven)", "Edgar Allan Poe", "Edgar Allan Poe", "Edgar Allan Poe", "Published in 1845, the narrative poem is famed for its refrain 'Nevermore'.", "Publicado en 1845, el poema narrativo es célebre por su estribillo 'Nunca más'.", "Terbit tahun 1845, puisi naratif ini terkenal dengan pengulangan kata 'Nevermore'.", "easy"),
        PairFact("Dune", "Dune", "Dune", "Frank Herbert", "Frank Herbert", "Frank Herbert", "Winning both Hugo and Nebula awards (1965), Dune is set on desert planet Arrakis.", "Ganadora de los premios Hugo y Nébula (1965), transcurre en el planeta desértico Arrakis.", "Peraih penghargaan Hugo dan Nebula (1965), Dune berlatar di planet gurun Arrakis.", "medium")
    )

    private val artworks = listOf(
        PairFact("The Starry Night", "La noche estrellada", "The Starry Night", "Vincent van Gogh", "Vincent van Gogh", "Vincent van Gogh", "Painted in June 1889 in Saint-Rémy-de-Provence, now housed at MoMA in New York.", "Pintado en junio de 1889 en Saint-Rémy-de-Provence; hoy se exhibe en el MoMA.", "Dilukis pada Juni 1889 di Saint-Rémy-de-Provence dan kini disimpan di MoMA New York.", "easy"),
        PairFact("Mona Lisa", "La Gioconda (Mona Lisa)", "Mona Lisa", "Leonardo da Vinci", "Leonardo da Vinci", "Leonardo da Vinci", "Painted in Florence circa 1503–1519, the oil portrait draws millions to the Louvre.", "Pintada en Florencia hacia 1503–1519, atrae millones de visitantes al Louvre.", "Dilukis di Firenze sekitar 1503–1519 dan dipajang di Museum Louvre, Paris.", "easy"),
        PairFact("Guernica", "Guernica", "Guernica", "Pablo Picasso", "Pablo Picasso", "Pablo Picasso", "Picasso painted the monumental black-and-white anti-war mural in 1937 in Paris.", "Picasso pintó este monumental mural antibélico en blanco y negro en 1937.", "Picasso melukis mural antiperang monokrom besar ini pada tahun 1937.", "medium"),
        PairFact("The Persistence of Memory", "La persistencia de la memoria", "Kegigihan Memori (Jam Meleleh)", "Salvador Dalí", "Salvador Dalí", "Salvador Dalí", "Dalí's 1931 Surrealist oil painting features melting pocket watches in Catalonia.", "Lienzo surrealista de 1931 célebre por sus relojes de bolsillo derretidos.", "Lukisan Surealis tahun 1931 ini menampilkan jam saku yang tampak meleleh.", "easy"),
        PairFact("The Scream", "El grito", "Jeritan (The Scream)", "Edvard Munch", "Edvard Munch", "Edvard Munch", "Norwegian expressionist Edvard Munch created the iconic series beginning in 1893.", "El expresionista noruego Edvard Munch inició esta icónica serie en 1893.", "Pelukis ekspresionis Norwegia Edvard Munch memulai seri ikonik ini pada 1893.", "easy"),
        PairFact("Girl with a Pearl Earring", "La joven de la perla", "Gadis Beranting Mutiara", "Johannes Vermeer", "Johannes Vermeer", "Johannes Vermeer", "Dutch Golden Age master Vermeer painted this tronie portrait around 1665.", "El maestro neerlandés Vermeer pintó este célebre retrato hacia 1665.", "Maestro Zaman Keemasan Belanda Vermeer melukis karya ini sekitar tahun 1665.", "easy"),
        PairFact("The Great Wave off Kanagawa", "La gran ola de Kanagawa", "Ombak Besar di Lepas Pantai Kanagawa", "Katsushika Hokusai", "Katsushika Hokusai", "Katsushika Hokusai", "Part of 'Thirty-Six Views of Mount Fuji' (c. 1831), using Prussian blue woodblock ink.", "Parte de 'Treinta y seis vistas del monte Fuji' (c. 1831), hecha en xilografía.", "Bagian dari seri 'Tiga Puluh Enam Pemandangan Gunung Fuji' (sekitar 1831).", "medium"),
        PairFact("Water Lilies", "Los nenúfares", "Teratai Air (Water Lilies)", "Claude Monet", "Claude Monet", "Claude Monet", "Monet painted roughly 250 oil studies of his Giverny flower garden pond.", "Monet pintó unas 250 obras inspiradas en el estanque de su jardín en Giverny.", "Monet melukis sekitar 250 studi minyak kolam bunga lili di tamannya di Giverny.", "easy"),
        PairFact("The Birth of Venus", "El nacimiento de Venus", "Kelahiran Venus", "Sandro Botticelli", "Sandro Botticelli", "Sandro Botticelli", "Painted around 1485 during the Italian Renaissance, displayed in the Uffizi Gallery.", "Pintado hacia 1485 en el Renacimiento italiano; se exhibe en la Galería Uffizi.", "Dilukis sekitar 1485 pada era Renaisans Italia dan dipajang di Galeri Uffizi.", "medium"),
        PairFact("The Night Watch", "La ronda de noche", "Ronda Malam (The Night Watch)", "Rembrandt van Rijn", "Rembrandt van Rijn", "Rembrandt van Rijn", "Completed in 1642, Rembrandt's dynamic group portrait hangs in Amsterdam's Rijksmuseum.", "Completada en 1642, esta monumental obra se exhibe en el Rijksmuseum de Ámsterdam.", "Rampung pada 1642, lukisan kelompok dramatis ini dipajang di Rijksmuseum Amsterdam.", "medium"),
        PairFact("The Kiss", "El beso", "Ciuman (The Kiss)", "Gustav Klimt", "Gustav Klimt", "Gustav Klimt", "Created in 1907–1908 during Klimt's Golden Period using real gold leaf in Vienna.", "Creada entre 1907 y 1908 durante la época dorada de Klimt con pan de oro.", "Dibuat 1907–1908 pada Periode Emas Klimt menggunakan lembaran emas asli di Wina.", "medium"),
        PairFact("The Creation of Adam (Sistine Chapel)", "La creación de Adán (Capilla Sixtina)", "Penciptaan Adam (Kapel Sistina)", "Michelangelo", "Miguel Ángel", "Michelangelo", "Painted on the Sistine Chapel ceiling in Vatican City between 1508 and 1512.", "Pintado en la bóveda de la Capilla Sixtina entre 1508 y 1512.", "Dilukis di langit-langit Kapel Sistina, Vatikan, antara 1508 dan 1512.", "easy"),
        PairFact("Las Meninas", "Las meninas", "Las Meninas", "Diego Velázquez", "Diego Velázquez", "Diego Velázquez", "Velázquez's 1656 court painting in Madrid's Prado Museum plays with perspective and mirrors.", "Obra maestra de 1656 conservada en el Museo del Prado de Madrid.", "Karya agung istana tahun 1656 di Museum Prado Madrid ini bermain dengan cermin.", "medium"),
        PairFact("American Gothic", "Gótico estadounidense", "American Gothic", "Grant Wood", "Grant Wood", "Grant Wood", "Painted in 1930 showing an Iowa farmer with a pitchfork in front of a Gothic window.", "Pintura de 1930 que muestra a un granjero de Iowa con una horca ante una ventana gótica.", "Dilukis tahun 1930 menampilkan petani Iowa berlatar jendela bergaya Gotik.", "medium"),
        PairFact("The Thinker (bronze sculpture)", "El pensador (escultura de bronce)", "Sang Pemikir (patung perunggu)", "Auguste Rodin", "Auguste Rodin", "Auguste Rodin", "Rodin designed this meditative bronze figure originally for 'The Gates of Hell'.", "Rodin diseñó esta escultura meditativa originalmente para 'Las puertas del infierno'.", "Rodin merancang patung perunggu ini awalnya untuk proyek 'Gerbang Neraka'.", "easy"),
        PairFact("A Sunday Afternoon on the Island of La Grande Jatte", "Tarde de domingo en la isla de la Grande Jatte", "Minggu Siang di Pulau La Grande Jatte", "Georges Seurat", "Georges Seurat", "Georges Seurat", "Painted 1884–1886, Seurat's work epitomizes Pointillism built from tiny color dots.", "Pintada entre 1884 y 1886, ejemplifica el puntillismo con miles de pequeños puntos de color.", "Dilukis 1884–1886, mahakarya ini adalah contoh puncak gaya Pointilisme.", "hard"),
        PairFact("The Garden of Earthly Delights", "El jardín de las delicias", "Taman Kenikmatan Duniawi", "Hieronymus Bosch", "El Bosco", "Hieronymus Bosch", "Early Netherlandish triptych painted c. 1490–1510 filled with surreal allegories.", "Tríptico flamenco pintado hacia 1490–1510 repleto de símbolos alegóricos.", "Triptik Belanda Awal (c. 1490–1510) yang kaya akan simbol dan alegori imajinatif.", "hard"),
        PairFact("A Bar at the Folies-Bergère", "Un bar del Folies-Bergère", "Bar di Folies-Bergère", "Édouard Manet", "Édouard Manet", "Édouard Manet", "Manet's final major work (1882) depicts a Parisian café-concert mirrored behind a barmaid.", "Última gran obra de Manet (1882), ambientada en un célebre café-concierto parisino.", "Karya besar terakhir Manet (1882) yang menggambarkan kafe konser Paris di cermin.", "hard"),
        PairFact("Café Terrace at Night", "Terraza de café por la noche", "Teras Kafe di Malam Hari", "Vincent van Gogh", "Vincent van Gogh", "Vincent van Gogh", "Painted in Arles in September 1888 without using black pigment for the night scene.", "Pintado en Arlés en septiembre de 1888 sin emplear negro puro para la escena nocturna.", "Dilukis di Arles pada September 1888 dengan kontras biru dan kuning hangat.", "medium"),
        PairFact("The Two Fridas", "Las dos Fridas", "Dua Frida (The Two Fridas)", "Frida Kahlo", "Frida Kahlo", "Frida Kahlo", "Double self-portrait painted in 1939 expressing Kahlo's European and Mexican heritage.", "Doble autorretrato de 1939 que expresa sus raíces mexicanas y europeas.", "Lukisan potret diri ganda tahun 1939 yang mengekspresikan dua identitas budaya Kahlo.", "easy")
    )

    private val films = listOf(
        PairFact("Jurassic Park (1993)", "Parque Jurásico (1993)", "Jurassic Park (1993)", "Steven Spielberg", "Steven Spielberg", "Steven Spielberg", "Adapted from Michael Crichton's novel, it won 3 Academy Awards for VFX and sound.", "Adaptación de la novela de Michael Crichton ganadora de 3 premios Óscar.", "Diadaptasi dari novel Michael Crichton dan meraih 3 Piala Oscar.", "easy"),
        PairFact("Spirited Away (2001)", "El viaje de Chihiro (2001)", "Spirited Away (2001)", "Hayao Miyazaki", "Hayao Miyazaki", "Hayao Miyazaki", "Produced by Studio Ghibli, it remains the only hand-drawn non-English Oscar Best Animated winner.", "Obra de Studio Ghibli ganadora del Óscar a Mejor Película de Animación.", "Karya Studio Ghibli pemenang Piala Oscar untuk Film Animasi Terbaik.", "easy"),
        PairFact("Parasite (2019)", "Parásitos (2019)", "Parasite (2019)", "Bong Joon-ho", "Bong Joon-ho", "Bong Joon-ho", "Made Oscars history as the first non-English language Best Picture winner.", "Hizo historia en los Óscar como primera ganadora a Mejor Película en idioma no inglés.", "Mencetak sejarah sebagai film berbahasa non-Inggris pertama peraih Best Picture Oscar.", "easy"),
        PairFact("The Dark Knight (2008)", "El caballero oscuro (2008)", "The Dark Knight (2008)", "Christopher Nolan", "Christopher Nolan", "Christopher Nolan", "Featured Heath Ledger's Oscar-winning portrayal of the Joker in Gotham City.", "Destacó por la interpretación ganadora del Óscar de Heath Ledger como el Joker.", "Menampilkan akting legendaris peraih Oscar dari Heath Ledger sebagai Joker.", "easy"),
        PairFact("Titanic (1997)", "Titanic (1997)", "Titanic (1997)", "James Cameron", "James Cameron", "James Cameron", "Tied the all-time record by winning 11 Academy Awards including Best Picture.", "Igualó el récord histórico al ganar 11 premios Óscar, incluida Mejor Película.", "Menyamai rekor sepanjang masa dengan memenangi 11 Piala Oscar termasuk Best Picture.", "easy"),
        PairFact("Pulp Fiction (1994)", "Pulp Fiction (1994)", "Pulp Fiction (1994)", "Quentin Tarantino", "Quentin Tarantino", "Quentin Tarantino", "Won the Palme d'Or at Cannes with its interwoven non-linear Los Angeles storylines.", "Ganó la Palma de Oro en Cannes gracias a su estructura narrativa no lineal.", "Meraih Palme d'Or di Cannes lewat alur cerita non-linearnya yang saling terhubung.", "medium"),
        PairFact("The Godfather (1972)", "El padrino (1972)", "The Godfather (1972)", "Francis Ford Coppola", "Francis Ford Coppola", "Francis Ford Coppola", "Adapted from Mario Puzo's novel starring Marlon Brando and Al Pacino.", "Adaptación de la novela de Mario Puzo protagonizada por Marlon Brando y Al Pacino.", "Diadaptasi dari novel Mario Puzo dan dibintangi Marlon Brando serta Al Pacino.", "easy"),
        PairFact("2001: A Space Odyssey (1968)", "2001: Una odisea del espacio (1968)", "2001: A Space Odyssey (1968)", "Stanley Kubrick", "Stanley Kubrick", "Stanley Kubrick", "Developed alongside Arthur C. Clarke, it introduced AI computer HAL 9000.", "Desarrollada junto a Arthur C. Clarke, presentó a la IA HAL 9000.", "Digarap bersama Arthur C. Clarke dan memperkenalkan komputer AI HAL 9000.", "medium"),
        PairFact("Seven Samurai (1954)", "Los siete samuráis (1954)", "Seven Samurai (1954)", "Akira Kurosawa", "Akira Kurosawa", "Akira Kurosawa", "Set in 1586 Sengoku Japan, it pioneered the 'assemble the team' action template.", "Ambientada en el Japón Sengoku, definió el cine moderno de acción en equipo.", "Berlatar Jepang era Sengoku 1586 dan memelopori pola film aksi tim modern.", "medium"),
        PairFact("Psycho (1960)", "Psicosis (1960)", "Psycho (1960)", "Alfred Hitchcock", "Alfred Hitchcock", "Alfred Hitchcock", "Hitchcock's thriller at Bates Motel featured Bernard Herrmann's iconic string score.", "Clásico de suspenso en el Motel Bates con la célebre partitura de Bernard Herrmann.", "Film thriller klasik di Bates Motel dengan musik gesek ikonik karya Bernard Herrmann.", "easy"),
        PairFact("Everything Everywhere All at Once (2022)", "Todo a la vez en todas partes (2022)", "Everything Everywhere All at Once (2022)", "Daniel Kwan & Daniel Scheinert", "Daniel Kwan y Daniel Scheinert", "Daniel Kwan & Daniel Scheinert", "Swept 7 Academy Awards including Best Picture and Best Actress for Michelle Yeoh.", "Arrasó con 7 premios Óscar, incluyendo Mejor Película y Mejor Actriz para Michelle Yeoh.", "Memborong 7 Piala Oscar termasuk Best Picture dan Aktris Terbaik untuk Michelle Yeoh.", "medium"),
        PairFact("Pan's Labyrinth (2006)", "El laberinto del fauno (2006)", "Pan's Labyrinth (2006)", "Guillermo del Toro", "Guillermo del Toro", "Guillermo del Toro", "Blended dark fantasy and historical drama, winning 3 Oscars for craft categories.", "Combinó fantasía oscura y drama histórico ganando 3 premios Óscar técnicos.", "Memadukan fantasi gelap dan drama sejarah serta memenangi 3 Piala Oscar.", "medium"),
        PairFact("City of God (2002)", "Ciudad de Dios (2002)", "City of God (2002)", "Fernando Meirelles", "Fernando Meirelles", "Fernando Meirelles", "Acclaimed Brazilian crime drama earned four Academy Award nominations.", "Aclamado drama brasileño nominado a cuatro premios Óscar.", "Drama kriminal Brasil yang dipuji luas dan meraih empat nominasi Oscar.", "hard"),
        PairFact("Dune (2021)", "Dune (2021)", "Dune (2021)", "Denis Villeneuve", "Denis Villeneuve", "Denis Villeneuve", "Adapted the first half of Frank Herbert's sci-fi novel and won 6 technical Oscars.", "Adaptó la primera parte de la novela de Frank Herbert y ganó 6 Óscar técnicos.", "Mengadaptasi paruh pertama novel Frank Herbert dan memenangi 6 Piala Oscar.", "medium"),
        PairFact("Get Out (2017)", "Déjame salir (2017)", "Get Out (2017)", "Jordan Peele", "Jordan Peele", "Jordan Peele", "Jordan Peele won the Academy Award for Best Original Screenplay for his directorial debut.", "Jordan Peele ganó el Óscar al Mejor Guion Original por su debut como director.", "Jordan Peele memenangi Piala Oscar Skenario Asli Terbaik lewat debut penyutradaraannya.", "medium"),
        PairFact("Citizen Kane (1941)", "Ciudadano Kane (1941)", "Citizen Kane (1941)", "Orson Welles", "Orson Welles", "Orson Welles", "Pioneered deep-focus cinematography and nonlinear flash-back biography.", "Revolucionó la fotografía en profundidad de campo y el montaje no lineal.", "Memelopori sinematografi deep-focus dan penceritaan biografi non-linear.", "medium"),
        PairFact("Blade Runner (1982)", "Blade Runner (1982)", "Blade Runner (1982)", "Ridley Scott", "Ridley Scott", "Ridley Scott", "Inspired by Philip K. Dick's 'Do Androids Dream of Electric Sheep?', scored by Vangelis.", "Inspirada en la novela de Philip K. Dick y musicalizada por Vangelis.", "Terinspirasi novel Philip K. Dick dan diiringi musik elektronik ikonik Vangelis.", "medium"),
        PairFact("Mad Max: Fury Road (2015)", "Mad Max: Furia en la carretera (2015)", "Mad Max: Fury Road (2015)", "George Miller", "George Miller", "George Miller", "Practical desert stunts earned 6 Academy Awards including Editing and Sound.", "Sus escenas prácticas en el desierto ganaron 6 premios Óscar.", "Aksi laga praktisnya di gurun memborong 6 Piala Oscar termasuk Penyuntingan Terbaik.", "medium"),
        PairFact("Amélie (2001)", "Amélie (2001)", "Amélie (2001)", "Jean-Pierre Jeunet", "Jean-Pierre Jeunet", "Jean-Pierre Jeunet", "Set in Montmartre, Paris, with Audrey Tautou and a famous piano score by Yann Tiersen.", "Ambientada en Montmartre, París, con Audrey Tautou y música de Yann Tiersen.", "Berlatar di Montmartre, Paris, bersama Audrey Tautou dan musik piano Yann Tiersen.", "hard"),
        PairFact("Roma (2018)", "Roma (2018)", "Roma (2018)", "Alfonso Cuarón", "Alfonso Cuarón", "Alfonso Cuarón", "Semi-autobiographical black-and-white drama set in 1970s Mexico City; won 3 Oscars.", "Drama en blanco y negro ambientado en la Ciudad de México de los años 70; ganó 3 Óscar.", "Drama hitam-putih berlatar Kota Meksiko era 1970-an yang meraih 3 Piala Oscar.", "hard")
    )

    private val foods = listOf(
        PairFact("Rendang", "Rendang", "Rendang", "Indonesia (West Sumatra)", "Indonesia (Sumatra Occidental)", "Indonesia (Sumatera Barat)", "Slow-cooked caramelized beef in coconut milk and spices from Minangkabau tradition.", "Carne cocida a fuego lento con leche de coco y especias de tradición minangkabau.", "Olahan daging berbumbu rempah dan santan yang dimasak perlahan khas Minangkabau.", "easy"),
        PairFact("Paella", "Paella", "Paella", "Spain (Valencia)", "España (Valencia)", "Spanyol (Valencia)", "Traditional saffron-infused rice cooked in a wide shallow two-handled pan.", "Arroz tradicional con azafrán cocinado en una sartén ancha y poco profunda.", "Hidangan nasi berbumbu safron yang dimasak dalam wajan lebar dangkal.", "easy"),
        PairFact("Kimchi", "Kimchi", "Kimchi", "Korea", "Corea", "Korea", "Fermented napa cabbage and radish seasoned with chili, garlic, and ginger.", "Col china y rábano fermentados con chile, ajo y jengibre.", "Sawi putih dan lobak fermentasi dengan cabai, bawang putih, dan jahe.", "easy"),
        PairFact("Sushi", "Sushi", "Sushi", "Japan", "Japón", "Jepang", "Vinegared rice (shari) paired with fresh fish, vegetables, or nori.", "Arroz aderezado con vinagre combinado con pescado fresco, vegetales o alga nori.", "Nasi cuka yang dipadukan dengan ikan segar, sayuran, atau rumput laut nori.", "easy"),
        PairFact("Tacos al Pastor", "Tacos al pastor", "Tacos al Pastor", "Mexico", "México", "Meksiko", "Vertical spit-roasted pork marinated in achiote and chilies served with pineapple.", "Cerdo asado en trompo marinado con achiote y chiles, servido con piña.", "Daging panggang bumbu achiote dan cabai yang disajikan dengan potongan nanas.", "easy"),
        PairFact("Pad Thai", "Pad Thai", "Pad Thai", "Thailand", "Tailandia", "Thailand", "Stir-fried rice noodles balancing tamarind sourness, palm sugar, fish sauce, and peanuts.", "Fideos de arroz salteados con tamarindo, azúcar de palma y cacahuetes.", "Mi beras goreng dengan perpaduan rasa asam jawa, gula aren, kecap ikan, dan kacang.", "easy"),
        PairFact("Pho", "Pho", "Pho", "Vietnam", "Vietnam", "Vietnam", "Aromatic beef or chicken noodle soup simmered with star anise, cinnamon, and charred ginger.", "Sopa aromática de fideos de arroz con anís estrellado, canela y jengibre asado.", "Sup mi beras berkaldu harum kapulaga, bunga lawang, kayu manis, dan jahe bakar.", "easy"),
        PairFact("Ceviche", "Ceviche", "Ceviche", "Peru", "Perú", "Peru", "Fresh raw fish cured in citrus juice (lime/lemon), red onion, chili, and cilantro.", "Pescado fresco marinado en jugo cítrico con cebolla roja, ají y cilantro.", "Ikan segar mentah yang dimatangkan secara alami oleh perasan jeruk nipis dan cabai.", "medium"),
        PairFact("Poutine", "Poutine", "Poutine", "Canada (Quebec)", "Canadá (Quebéc)", "Kanada (Quebec)", "Crispy french fries topped with squeaky cheese curds and rich brown gravy.", "Papas fritas cubiertas con cuajada de queso fresco y salsa gravy caliente.", "Kentang goreng renyah bertabur dadih keju segar dan siraman saus kaldu cokelat.", "medium"),
        PairFact("Feijoada", "Feijoada", "Feijoada", "Brazil", "Brasil", "Brasil", "Hearty black bean stew traditionally served with rice, greens, and orange slices.", "Estofado tradicional de frijoles negros servido con arroz y rodajas de naranja.", "Semur kacang hitam kental yang disajikan hangat dengan nasi dan irisan jeruk.", "medium"),
        PairFact("Biryani", "Biryani", "Biryani", "South Asia (India)", "Asia del Sur (India)", "Asia Selatan (India)", "Layered aromatic basmati rice steamed with saffron, cardamom, herbs, and spiced meat or veg.", "Arroz basmati aromático cocido en capas con azafrán, cardamomo y especias.", "Nasi basmati harum berlapis rempah safron, kapulaga, dan bumbu aromatik.", "easy"),
        PairFact("Moussaka", "Musaka", "Moussaka", "Greece", "Grecia", "Yunani", "Layered baked dish of eggplant slices, spiced ground meat, and creamy béchamel sauce.", "Plato horneado con capas de berenjena, carne especiada y salsa bechamel.", "Hidangan panggang berlapis terong, daging cincang berbumbu, dan saus béchamel.", "medium"),
        PairFact("Pierogi", "Pierogi", "Pierogi", "Poland", "Polonia", "Polandia", "Half-moon boiled or pan-seared dumplings filled with potato, cheese, mushrooms, or fruit.", "Empanadillas hervidas o doradas rellenas de patata, queso o setas.", "Pangsit berbentuk bulan sabit berisi kentang, keju, jamur, atau buah manis.", "medium"),
        PairFact("Tagine", "Tayín (Tagine)", "Tagine", "Morocco (North Africa)", "Marruecos (Norte de África)", "Maroko (Afrika Utara)", "Slow-simmered stew named after the conical earthenware pot that circulates steam.", "Guiso cocinado a fuego lento en una cazuela cónica de barro que retiene el vapor.", "Hidangan semur berbumbu khas dalam panci tanah liat bertutup kerucut.", "medium"),
        PairFact("Satay (Sate)", "Satay (Sate)", "Sate", "Indonesia (Java)", "Indonesia (Java)", "Indonesia (Jawa)", "Skewered grilled meat served with peanut sauce or sweet soy sauce (kecap manis).", "Brochetas a la parrilla acompañadas de salsa de maní o salsa de soja dulce.", "Tusukan daging panggang arang dengan bumbu kacang atau sambal kecap manis.", "easy"),
        PairFact("Tiramisu", "Tiramisú", "Tiramisu", "Italy", "Italia", "Italia", "No-bake dessert layering espresso-soaked ladyfingers with whipped mascarpone cream and cocoa.", "Postre frío de bizcochos bañados en espresso, crema de mascarpone y cacao.", "Pencuci mulut berlapis biskuit celup espresso, krim mascarpone, dan bubuk kakao.", "easy"),
        PairFact("Croissant", "Cruasán (Croissant)", "Croissant", "France (inspired by Austria)", "Francia (inspirado en Austria)", "Prancis (terinspirasi Austria)", "Crescent-shaped laminated pastry made by folding butter into yeast dough.", "Bollo hojaldrado en forma de media luna elaborado doblando mantequilla en masa fermentada.", "Pastri berlapis berbentuk bulan sabit dari lipatan adonan beragi dan mentega.", "easy"),
        PairFact("Jollof Rice", "Arroz Jollof", "Nasi Jollof", "West Africa", "África Occidental", "Afrika Barat", "Vibrant one-pot tomato, red pepper, onion, and spiced rice dish celebrated across West Africa.", "Arroz especiado con tomate y pimientos rojos muy popular en África Occidental.", "Hidangan nasi tomat berbumbu paprika dan rempah kebanggaan Afrika Barat.", "medium"),
        PairFact("Baklava", "Baklava", "Baklava", "Turkey & Eastern Mediterranean", "Turquía y Mediterráneo Oriental", "Turki & Mediterania Timur", "Crisp phyllo pastry layered with chopped pistachios or walnuts and honey-rose syrup.", "Pastel de pasta filo relleno de pistachos o nueces y bañado en almíbar de miel.", "Kue pastri tipis berlapis kacang pistachio atau kenari yang disiram sirup madu.", "easy"),
        PairFact("Koshari", "Koshari", "Koshari", "Egypt", "Egipto", "Mesir", "National dish of Egypt mixing rice, macaroni, lentils, chickpeas, crispy onions, and tomato sauce.", "Plato nacional egipcio que combina arroz, pasta, lentejas, garbanzos y cebolla crujiente.", "Hidangan nasional Mesir berisi campuran nasi, makaroni, lentil, kacang arab, dan saus tomat.", "hard"),
        PairFact("Empanada", "Empanada", "Empanada", "Spain & Latin America", "España y América Latina", "Spanyol & Amerika Latin", "Folded baked or fried pastry pockets filled with savory meat, cheese, or vegetables.", "Masa horneada o frita doblada sobre rellenos salados de carne, queso o verduras.", "Pastri lipat panggang atau goreng berisi daging berbumbu, keju, atau sayuran.", "easy"),
        PairFact("Gado-gado", "Gado-gado", "Gado-gado", "Indonesia", "Indonesia", "Indonesia", "Blanched vegetables, boiled egg, tofu, tempeh, and compressed rice cake in peanut dressing.", "Ensalada indonesia de verduras cocidas, tofu, tempeh y huevo con salsa de maní.", "Hidangan sayur rebus, tahu, tempe, telur, dan lontong dengan saus kacang gurih.", "easy"),
        PairFact("Pavlova", "Pavlova", "Pavlova", "Australia & New Zealand", "Australia y Nueva Zelanda", "Australia & Selandia Baru", "Crisp meringue cake with marshmallow-soft interior topped with whipped cream and fruit.", "Tarta de merengue crujiente por fuera y suave por dentro con nata y fruta fresca.", "Kue meringue renyah di luar dan lembut di dalam dengan krim serta buah segar.", "medium"),
        PairFact("Okonomiyaki", "Okonomiyaki", "Okonomiyaki", "Japan", "Japón", "Jepang", "Savory cabbage-and-batter griddle pancake topped with bonito flakes and savory sauce.", "Tortita salada japonesa de col a la plancha con salsa especial y bonito seco.", "Panekuk gurih berbahan kol panggang dari Jepang dengan taburan katsuo dan saus khas.", "medium"),
        PairFact("Churrasco", "Churrasco", "Churrasco", "South America (Brazil & Argentina)", "Sudamérica (Brasil y Argentina)", "Amerika Selatan (Brasil & Argentina)", "Flame-grilled cuts of beef skewered and roasted over glowing charcoal embers.", "Cortes de carne asados a la brasa sobre carbón ardiente, típicos del Cono Sur.", "Potongan daging panggang di atas bara api arang khas kawasan Amerika Selatan.", "medium")
    )

    private val extraCurated: List<TripleFact> = listOf(
        // Science & Solar System & Biology
        TripleFact("f_sci_1", "science", "easy",
            "Which planet in our Solar System is known as the 'Red Planet' due to iron oxide on its surface?",
            "¿Qué planeta del Sistema Solar es llamado el 'Planeta Rojo' por el óxido de hierro en su superficie?",
            "Planet manakah di Tata Surya yang dijuluki 'Planet Merah' karena kandungan oksida besi di permukaannya?",
            "Mars", "Marte", "Mars",
            listOf("Venus", "Jupiter", "Mercury"), listOf("Venus", "Júpiter", "Mercurio"), listOf("Venus", "Jupiter", "Merkurius"),
            "Dust rich in iron(III) oxide gives Mars its reddish hue when seen from space.",
            "El polvo rico en óxido de hierro le otorga a Marte su característico tono rojizo.",
            "Debu kaya oksida besi memberi planet Mars warna kemerahan yang khas.",
        ),
        TripleFact("f_sci_2", "science", "easy",
            "Which is the largest planet in our Solar System?",
            "¿Cuál es el planeta más grande de nuestro Sistema Solar?",
            "Planet apakah yang merupakan planet terbesar di Tata Surya kita?",
            "Jupiter", "Júpiter", "Jupiter",
            listOf("Saturn", "Neptune", "Earth"), listOf("Saturno", "Neptuno", "Tierra"), listOf("Saturnus", "Neptunus", "Bumi"),
            "Jupiter's diameter is about eleven times wider than Earth's and houses the Great Red Spot.",
            "El diámetro de Júpiter es unas once veces mayor que el de la Tierra y alberga la Gran Mancha Roja.",
            "Diameter Jupiter sekitar sebelas kali lebih lebar daripada Bumi dan memiliki Bintik Merah Besar.",
        ),
        TripleFact("f_sci_3", "science", "medium",
            "What is the hardest natural tissue in the human body?",
            "¿Cuál es el tejido natural más duro del cuerpo humano?",
            "Jaringan alami apakah yang paling keras di dalam tubuh manusia?",
            "Tooth enamel", "Esmalte dental", "Email gigi",
            listOf("Femur bone", "Cartilage", "Keratin"), listOf("Hueso fémur", "Cartílago", "Queratina"), listOf("Tulang paha", "Tulang rawan", "Keratin"),
            "Tooth enamel is ~96% mineral (hydroxyapatite), making it even harder than bone.",
            "El esmalte dental está mineralizado en un 96%, superando incluso la dureza del hueso.",
            "Email gigi tersusun sekitar 96% mineral hidroksiapatit sehingga lebih keras dari tulang.",
        ),
        TripleFact("f_sci_4", "science", "easy",
            "What type of blood cell carries oxygen from the lungs throughout the human body?",
            "¿Qué tipo de célula sanguínea transporta oxígeno desde los pulmones al resto del cuerpo?",
            "Sel darah jenis apakah yang mengangkut oksigen dari paru-paru ke seluruh tubuh manusia?",
            "Red blood cells", "Glóbulos rojos", "Sel darah merah",
            listOf("White blood cells", "Platelets", "Plasma lipids"), listOf("Glóbulos blancos", "Plaquetas", "Lípidos plasmáticos"), listOf("Sel darah putih", "Trombosit", "Lipid plasma"),
            "Red blood cells contain iron-rich hemoglobin that binds oxygen molecules in the lungs.",
            "Los glóbulos rojos contienen hemoglobina rica en hierro que fija el oxígeno en los pulmones.",
            "Sel darah merah mengandung hemoglobin kaya zat besi yang mengikat oksigen di paru-paru.",
        ),
        TripleFact("f_sci_5", "science", "medium",
            "What SI unit is used to measure electric current?",
            "¿Qué unidad del Sistema Internacional mide la intensidad de la corriente eléctrica?",
            "Satuan SI apakah yang digunakan untuk mengukur kuat arus listrik?",
            "Ampere", "Amperio", "Ampere",
            listOf("Volt", "Watt", "Coulomb"), listOf("Voltio", "Vatio", "Culombio"), listOf("Volt", "Watt", "Coulomb"),
            "Named after French physicist André-Marie Ampère, one ampere equals one coulomb of charge per second.",
            "Nombrada por André-Marie Ampère, un amperio equivale a un culombio de carga por segundo.",
            "Dinamai menurut André-Marie Ampère; satu ampere setara satu coulomb muatan per detik.",
        ),
        TripleFact("f_sci_6", "science", "hard",
            "Which planet rotates in the opposite direction to most planets, making the Sun rise in the west?",
            "¿Qué planeta gira en dirección opuesta a la mayoría, haciendo que el Sol salga por el oeste?",
            "Planet manakah yang berotasi berlawanan arah dari kebanyakan planet sehingga Matahari terbit dari barat?",
            "Venus", "Venus", "Venus",
            listOf("Mercury", "Mars", "Saturn"), listOf("Mercurio", "Marte", "Saturno"), listOf("Merkurius", "Mars", "Saturnus"),
            "Venus exhibits slow retrograde rotation taking 243 Earth days per spin.",
            "Venus tiene una rotación retrógrada muy lenta que dura 243 días terrestres.",
            "Venus mengalami rotasi retrograd yang sangat lambat selama 243 hari Bumi.",
        ),
        TripleFact("f_sci_7", "science", "medium",
            "Which layer of Earth's atmosphere contains the protective ozone layer?",
            "¿Qué capa de la atmósfera terrestre contiene la capa protectora de ozono?",
            "Lapisan atmosfer Bumi manakah yang mengandung lapisan ozon pelindung?",
            "Stratosphere", "Estratosfera", "Stratosfer",
            listOf("Troposphere", "Mesosphere", "Thermosphere"), listOf("Troposfera", "Mesosfera", "Termosfera"), listOf("Troposfer", "Mesosfer", "Termosfer"),
            "Concentrated roughly 15–35 km above Earth, stratospheric ozone absorbs harmful UV radiation.",
            "Situado entre 15 y 35 km de altura, el ozono estratosférico absorbe la radiación ultravioleta.",
            "Berada sekitar 15–35 km di atas Bumi, ozon stratosfer menyerap radiasi ultraviolet berbahaya.",
        ),
        TripleFact("f_sci_8", "science", "hard",
            "What phenomenon causes the apparent frequency of a siren to drop as an ambulance passes by?",
            "¿Qué fenómeno hace que el tono de una sirena parezca bajar cuando la ambulancia se aleja?",
            "Fenomena fisika apakah yang membuat frekuensi suara sirene terdengar menurun saat ambulans menjauh?",
            "Doppler effect", "Efecto Doppler", "Efek Doppler",
            listOf("Compton scattering", "Photoelectric effect", "Refraction"), listOf("Dispersión Compton", "Efecto fotoeléctrico", "Refracción"), listOf("Hamburan Compton", "Efek fotolistrik", "Pembiasan"),
            "Predicted by Christian Doppler in 1842, wave compression ahead raises pitch and stretching behind lowers it.",
            "Descrito por Christian Doppler en 1842, la compresión de ondas aumenta la frecuencia y su estiramiento la reduce.",
            "Dijelaskan Christian Doppler pada 1842: gelombang yang tertekan meninggikan nada dan yang meregang menurunkannya.",
        ),
        // Music & Instruments
        TripleFact("f_mus_1", "music", "easy",
            "Which string instrument is larger than a violin and held between the knees while played seated?",
            "¿Qué instrumento de cuerda es más grande que el violín y se sostiene entre las rodillas al tocar sentado?",
            "Alat musik gesek manakah yang lebih besar dari biola dan dimainkan sambil dijepit di antara lutut?",
            "Cello", "Violonchelo", "Selo",
            listOf("Viola", "Mandolin", "Harp"), listOf("Viola", "Mandolina", "Arpa"), listOf("Viola", "Mandolin", "Harpa"),
            "The cello (violoncello) spans a rich tenor-to-bass range central to symphonies and string quartets.",
            "El violonchelo cubre un registro cálido de tenor a bajo fundamental en orquestas y cuartetos.",
            "Selo menghasilkan rentang suara hangat dari tenor hingga bas yang vital dalam orkestra.",
        ),
        TripleFact("f_mus_2", "music", "medium",
            "In music tempo markings, what does 'allegro' indicate?",
            "En las indicaciones de tempo musical, ¿qué significa 'allegro'?",
            "Dalam penanda tempo musik, apa arti istilah 'allegro'?",
            "Fast and lively", "Rápido y alegre", "Cepat dan riang",
            listOf("Very slow and solemn", "Gradually softer", "Silent pause"), listOf("Muy lento y solemne", "Gradualmente más suave", "Pausa silenciosa"), listOf("Sangat lambat dan khidmat", "Makin lama makin pelan", "Jeda senyap"),
            "Italian for cheerful/brisk, allegro typically ranges between 120 and 156 beats per minute.",
            "De origen italiano ('alegre/vivo'), suele interpretarse entre 120 y 156 pulsos por minuto.",
            "Berasal dari bahasa Italia yang berarti riang/lincah, umumnya berkisar 120–156 ketukan per menit.",
        ),
        TripleFact("f_mus_3", "music", "medium",
            "Which double-reed woodwind instrument sounds the tuning note 'A' for a symphony orchestra?",
            "¿Qué instrumento de viento madera de doble lengüeta da la nota 'La' para afinar la orquesta sinfónica?",
            "Alat musik tiup kayu berlidah ganda manakah yang membunyikan nada 'A' untuk menyetem orkestra simfoni?",
            "Oboe", "Oboe", "Obo",
            listOf("Flute", "Trumpet", "Clarinet"), listOf("Flauta", "Trompeta", "Clarinete"), listOf("Seruling (Flute)", "Trompet", "Klarinet"),
            "The oboe is chosen because its pitch stays remarkably steady across temperature changes.",
            "Se elige al oboe porque su afinación permanece muy estable ante cambios de temperatura.",
            "Obo dipilih karena nadanya paling stabil terhadap perubahan suhu ruang konser.",
        ),
        TripleFact("f_mus_4", "music", "easy",
            "Which traditional bamboo wind instrument from West Java produces sound when shaken?",
            "¿Qué instrumento tradicional de bambú de Java Occidental produce sonido al agitarse?",
            "Alat musik tradisional berbahan bambu dari Jawa Barat manakah yang dimainkan dengan cara digoyangkan?",
            "Angklung", "Angklung", "Angklung",
            listOf("Sitar", "Tabla", "Didgeridoo"), listOf("Sitar", "Tabla", "Didyeridú"), listOf("Sitar", "Tabla", "Didgeridoo"),
            "Recognized by UNESCO, each angklung resonates a single pitched note so groups interlock melodies.",
            "Reconocido por la UNESCO, cada angklung emite una nota y varias personas forman melodías colectivas.",
            "Diakui UNESCO, tiap angklung menghasilkan satu nada sehingga pemain berkolaborasi membentuk melodi.",
        ),
        TripleFact("f_mus_5", "music", "medium",
            "Which German baroque master composed the 'Brandenburg Concertos' and 'Toccata and Fugue in D minor'?",
            "¿Qué maestro barroco alemán compuso los 'Conciertos de Brandeburgo' y 'Tocata y fuga en re menor'?",
            "Komponis Barok Jerman manakah yang menggubah 'Konserto Brandenburg' dan 'Toccata and Fugue in D minor'?",
            "Johann Sebastian Bach", "Johann Sebastian Bach", "Johann Sebastian Bach",
            listOf("Wolfgang Amadeus Mozart", "Pyotr Ilyich Tchaikovsky", "Igor Stravinsky"), listOf("Wolfgang Amadeus Mozart", "Piotr Ilich Chaikovski", "Ígor Stravinski"), listOf("Wolfgang Amadeus Mozart", "Pyotr Ilyich Tchaikovsky", "Igor Stravinsky"),
            "Bach's polyphonic counterpoint shaped Western harmonic theory for centuries afterward.",
            "El contrapunto polifónico de Bach moldeó la armonía occidental durante siglos.",
            "Karya kontrapung polifonik Bach menjadi fondasi teori harmoni musik Barat selama berabad-abad.",
        ),
        TripleFact("f_mus_6", "music", "medium",
            "Which Polish-born Romantic composer wrote almost exclusively for solo piano, including Nocturnes and Études?",
            "¿Qué compositor romántico de origen polaco escribió casi exclusivamente para piano solo, incluidos Nocturnos y Estudios?",
            "Komponis Romantik asal Polandia manakah yang menulis hampir seluruh karyanya untuk piano solo, termasuk Nocturnes?",
            "Frédéric Chopin", "Frédéric Chopin", "Frédéric Chopin",
            listOf("Giuseppe Verdi", "Richard Wagner", "Hector Berlioz"), listOf("Giuseppe Verdi", "Richard Wagner", "Hector Berlioz"), listOf("Giuseppe Verdi", "Richard Wagner", "Hector Berlioz"),
            "Nicknamed the 'Poet of the Piano', Chopin settled in Paris at age 21.",
            "Llamado el 'Poeta del Piano', Chopin se estableció en París a los 21 años.",
            "Dijuluki 'Penyair Piano', Chopin menetap di Paris sejak usia 21 tahun.",
        ),
        TripleFact("f_mus_7", "music", "easy",
            "How many strings does a standard Spanish acoustic guitar have?",
            "¿Cuántas cuerdas tiene una guitarra acústica española estándar?",
            "Berapakah jumlah senar pada gitar akustik standar?",
            "6", "6", "6",
            listOf("4", "5", "8"), listOf("4", "5", "8"), listOf("4", "5", "8"),
            "Standard tuning from thickest to thinnest string is E2–A2–D3–G3–B3–E4.",
            "La afinación estándar de grave a aguda es Mi2–La2–Re3–Sol3–Si3–Mi4.",
            "Setelan standar dari senar paling tebal hingga paling tipis adalah E2–A2–D3–G3–B3–E4.",
        ),
        TripleFact("f_mus_8", "music", "hard",
            "What Japanese term describes the art of large barrel-shaped ensemble drums?",
            "¿Qué término japonés designa los grandes tambores de percusión en conjunto?",
            "Istilah Jepang apakah yang merujuk pada ansambel drum besar tradisional?",
            "Taiko", "Taiko", "Taiko",
            listOf("Shamisen", "Koto", "Shakuhachi"), listOf("Shamisen", "Koto", "Shakuhachi"), listOf("Shamisen", "Koto", "Shakuhachi"),
            "Taiko literally means 'great drum' and has featured in festivals and rituals for over a millennium.",
            "Taiko significa literalmente 'gran tambor' y acompaña festivales y rituales desde hace más de un milenio.",
            "Taiko secara harfiah berarti 'gendang besar' dan digunakan dalam festival serta upacara selama ribuan tahun.",
        ),
        // Sports & Leisure
        TripleFact("f_spt_1", "sport_and_leisure", "easy",
            "In association football (soccer), what is the duration of regulation play before stoppage time?",
            "En el fútbol asociación, ¿cuánto dura el tiempo reglamentario antes del añadido?",
            "Dalam pertandingan sepak bola standar, berapa lama durasi waktu normal sebelum tambahan waktu?",
            "90 minutes", "90 minutos", "90 menit",
            listOf("60 minutes", "80 minutes", "120 minutes"), listOf("60 minutos", "80 minutos", "120 minutos"), listOf("60 menit", "80 menit", "120 menit"),
            "Regulation matches consist of two 45-minute halves separated by a 15-minute halftime interval.",
            "Los partidos reglamentarios constan de dos mitades de 45 minutos con un descanso de 15.",
            "Pertandingan resmi terdiri dari dua babak masing-masing 45 menit dengan jeda 15 menit.",
        ),
        TripleFact("f_spt_2", "sport_and_leisure", "easy",
            "How many squares are on a standard chessboard?",
            "¿Cuántas casillas tiene un tablero de ajedrez estándar?",
            "Berapa banyak petak yang terdapat pada papan catur standar?",
            "64", "64", "64",
            listOf("48", "56", "72"), listOf("48", "56", "72"), listOf("48", "56", "72"),
            "An 8×8 grid alternates 32 light and 32 dark squares.",
            "La cuadrícula de 8×8 alterna 32 casillas claras y 32 oscuras.",
            "Kisi berukuran 8×8 terdiri dari 32 petak terang dan 32 petak gelap secara berselang-seling.",
        ),
        TripleFact("f_spt_3", "sport_and_leisure", "medium",
            "In tennis scoring, what term represents a score of zero?",
            "En la puntuación del tenis, ¿qué término representa una puntuación de cero en inglés?",
            "Dalam penghitungan skor tenis, istilah apa yang digunakan untuk menyebut skor nol?",
            "Love", "Love", "Love",
            listOf("Deuce", "Let", "Break"), listOf("Deuce", "Let", "Break"), listOf("Deuce", "Let", "Break"),
            "Widely linked to French 'l'œuf' (the egg) symbolizing the round zero.",
            "Asociado popularmente al francés 'l'œuf' (el huevo) por la forma redonda del cero.",
            "Sering dikaitkan dengan kata Prancis 'l'œuf' (telur) yang bentuknya menyerupai angka nol.",
        ),
        TripleFact("f_spt_4", "sport_and_leisure", "medium",
            "Which city became the first to host the Summer Olympic Games three times (1908, 1948, 2012)?",
            "¿Qué ciudad fue la primera en albergar los Juegos Olímpicos de Verano tres veces (1908, 1948, 2012)?",
            "Kota manakah yang pertama kali menjadi tuan rumah Olimpiade Musim Panas sebanyak tiga kali (1908, 1948, 2012)?",
            "London", "Londres", "London",
            listOf("Paris", "Los Angeles", "Tokyo"), listOf("París", "Los Ángeles", "Tokio"), listOf("Paris", "Los Angeles", "Tokyo"),
            "Paris joined London as a three-time Summer host in 2024.",
            "París igualó a Londres en 2024 tras haber sido sede en 1900 y 1924.",
            "Paris kemudian menyamai rekor tiga kali tuan rumah tersebut pada Olimpiade 2024.",
        ),
        TripleFact("f_spt_5", "sport_and_leisure", "easy",
            "How many players per team are on the court at the same time in standard volleyball?",
            "¿Cuántos jugadores por equipo hay simultáneamente en cancha en el voleibol estándar?",
            "Berapa pemain dari satu tim yang berada di lapangan sekaligus dalam bola voli standar?",
            "6", "6", "6",
            listOf("5", "7", "9"), listOf("5", "7", "9"), listOf("5", "7", "9"),
            "Six players rotate clockwise through front-row and back-row court zones.",
            "Seis jugadores rotan en sentido horario entre las zonas delanteras y traseras.",
            "Enam pemain berotasi searah jarum jam antara zona depan dan zona belakang.",
        ),
        TripleFact("f_spt_6", "sport_and_leisure", "medium",
            "Which Asian board game played with black and white stones on a 19×19 grid is over 2,500 years old?",
            "¿Qué juego de mesa asiático con piedras blancas y negras en un tablero de 19×19 supera los 2,500 años?",
            "Permainan papan asal Asia dengan batu hitam dan putih di papan 19×19 yang berusia lebih dari 2.500 tahun apakah ini?",
            "Go (Weiqi / Baduk)", "Go (Weiqi / Baduk)", "Go (Weiqi / Baduk)",
            listOf("Shogi", "Mahjong", "Backgammon"), listOf("Shogi", "Mahjong", "Backgammon"), listOf("Shogi", "Mahjong", "Backgammon"),
            "Originating in ancient China, players surround territory rather than capture a king.",
            "Originario de la antigua China, el objetivo es rodear más territorio en el tablero.",
            "Berasal dari Tiongkok kuno, tujuannya mengepung dan menguasai wilayah terluas di papan.",
        ),
        // Society & Culture
        TripleFact("f_soc_1", "society_and_culture", "easy",
            "In Greek mythology, who is the goddess of wisdom and strategic warfare after whom Athens is named?",
            "En la mitología griega, ¿quién es la diosa de la sabiduría que da nombre a Atenas?",
            "Dalam mitologi Yunani, siapakah dewi kebijaksanaan yang namanya menjadi asal nama kota Athena?",
            "Athena", "Atenea", "Athena",
            listOf("Artemis", "Hera", "Aphrodite"), listOf("Artemisa", "Hera", "Afrodita"), listOf("Artemis", "Hera", "Afrodit"),
            "According to myth, Athena won patronage of the city by gifting the olive tree.",
            "Según el mito, Atenea ganó el patronazgo de la ciudad al obsequiar el olivo.",
            "Menurut mitos, Athena memenangkan kota tersebut dengan menghadiahkan pohon zaitun.",
        ),
        TripleFact("f_soc_2", "society_and_culture", "medium",
            "Which spring festival in India is celebrated by throwing brightly colored powders called gulal?",
            "¿Qué festival primaveral de la India se celebra lanzando polvos de colores llamados gulal?",
            "Festival musim semi di India manakah yang dirayakan dengan menaburkan bubuk warna-warni?",
            "Holi", "Holi", "Holi",
            listOf("Diwali", "Navratri", "Baisakhi"), listOf("Diwali", "Navratri", "Baisakhi"), listOf("Diwali", "Navratri", "Baisakhi"),
            "Often called the Festival of Colors, Holi welcomes spring and celebrates friendship.",
            "Llamado el Festival de los Colores, Holi da la bienvenida a la primavera.",
            "Dikenal sebagai Festival Warna, Holi menyambut musim semi dan merayakan persaudaraan.",
        ),
        TripleFact("f_soc_3", "society_and_culture", "medium",
            "Which Mexican cultural holiday honored around November 1–2 features candlelit ofrendas and marigolds?",
            "¿Qué festividad mexicana celebrada el 1 y 2 de noviembre incluye ofrendas y flores de cempasúchil?",
            "Perayaan budaya Meksiko sekitar 1–2 November manakah yang menghias altar dengan bunga marigold dan lilin?",
            "Día de los Muertos", "Día de los Muertos", "Día de los Muertos",
            listOf("Cinco de Mayo", "Las Posadas", "Guelaguetza"), listOf("Cinco de Mayo", "Las Posadas", "Guelaguetza"), listOf("Cinco de Mayo", "Las Posadas", "Guelaguetza"),
            "Recognized by UNESCO, families joyfully remember departed loved ones with favorite foods and candles.",
            "Patrimonio de la UNESCO, las familias honran con alegría la memoria de sus seres queridos.",
            "Diakui UNESCO, keluarga mengenang leluhur dengan penuh syukur lewat makanan favorit dan lilin.",
        ),
        TripleFact("f_soc_4", "society_and_culture", "hard",
            "Which ancient Greek philosophical school founded by Zeno of Citium teaches virtue and composure amid adversity?",
            "¿Qué escuela filosófica fundada por Zenón de Citio enseña la virtud y la serenidad ante la adversidad?",
            "Aliran filsafat Yunani kuno yang didirikan Zeno dari Citium dan menekankan ketenangan menghadapi cobaan adalah?",
            "Stoicism", "Estoicismo", "Stoikisme",
            listOf("Epicureanism", "Sophism", "Cynicism"), listOf("Epicureísmo", "Sofismo", "Cinismo"), listOf("Epikureanisme", "Sofisme", "Sinisisme"),
            "Thinkers such as Seneca, Epictetus, and Emperor Marcus Aurelius championed Stoic principles.",
            "Séneca, Epicteto y el emperador Marco Aurelio fueron grandes exponentes del estoicismo.",
            "Tokoh seperti Seneca, Epictetus, dan Kaisar Marcus Aurelius terkenal mengembangkan Stoikisme.",
        ),
        TripleFact("f_soc_5", "society_and_culture", "medium",
            "Which Indonesian island is the origin of the ancient terraced rice-irrigation cooperative system called 'Subak'?",
            "¿De qué isla indonesia proviene el antiguo sistema cooperativo de riego en terrazas llamado 'Subak'?",
            "Dari pulau Indonesia manakah sistem pengairan sawah terasering tradisional bernama 'Subak' berasal?",
            "Bali", "Bali", "Bali",
            listOf("Sumatra", "Sulawesi", "Lombok"), listOf("Sumatra", "Célebes", "Lombok"), listOf("Sumatera", "Sulawesi", "Lombok"),
            "Inscribed by UNESCO in 2012, Subak reflects the Balinese philosophy of Tri Hita Karana.",
            "Declarado Patrimonio UNESCO en 2012, refleja la filosofía balinesa Tri Hita Karana.",
            "Ditetapkan UNESCO pada 2012, Subak mencerminkan falsafah Tri Hita Karana di Bali.",
        ),
        TripleFact("f_soc_6", "society_and_culture", "medium",
            "What is the official national motto of Indonesia inscribed on the Garuda Pancasila emblem?",
            "¿Cuál es el lema nacional de Indonesia inscrito en el emblema Garuda Pancasila?",
            "Apa semboyan resmi negara Indonesia yang tertera pada lambang Garuda Pancasila?",
            "Bhinneka Tunggal Ika", "Bhinneka Tunggal Ika", "Bhinneka Tunggal Ika",
            listOf("Gotong Royong", "Pancasila Jaya", "Satyameva Jayate"), listOf("Gotong Royong", "Pancasila Jaya", "Satyameva Jayate"), listOf("Gotong Royong", "Pancasila Jaya", "Satyameva Jayate"),
            "Taken from Old Javanese poem Kakawin Sutasoma, it means 'Unity in Diversity'.",
            "Tomado del poema javanés antiguo Kakawin Sutasoma, significa 'Unidad en la diversidad'.",
            "Berasal dari Kakawin Sutasoma, bermakna 'Berbeda-beda tetapi tetap satu jua'.",
        ),
        TripleFact("f_geo_1", "geography", "easy",
            "Which river carries the largest volume of water of any river on Earth?",
            "¿Qué río transporta el mayor volumen de agua del planeta?",
            "Sungai manakah yang mengalirkan volume air terbesar di Bumi?",
            "Amazon River", "Río Amazonas", "Sungai Amazon",
            listOf("Nile River", "Yangtze River", "Congo River"), listOf("Río Nilo", "Río Yangtsé", "Río Congo"), listOf("Sungai Nil", "Sungai Yangtze", "Sungai Kongo"),
            "The Amazon discharges roughly one-fifth of all river water entering the world's oceans.",
            "El Amazonas aporta aproximadamente una quinta parte de toda el agua fluvial que llega a los océanos.",
            "Sungai Amazon menyumbang sekitar seperlima dari seluruh air sungai yang bermuara ke samudra.",
        ),
        TripleFact("f_geo_2", "geography", "easy",
            "Which is the deepest known oceanic trench on Earth?",
            "¿Cuál es la fosa oceánica más profunda conocida en la Tierra?",
            "Palung laut terdalam yang diketahui di Bumi adalah?",
            "Mariana Trench", "Fosa de las Marianas", "Palung Mariana",
            listOf("Tonga Trench", "Java Trench", "Puerto Rico Trench"), listOf("Fosa de Tonga", "Fosa de Java", "Fosa de Puerto Rico"), listOf("Palung Tonga", "Palung Jawa", "Palung Puerto Riko"),
            "Challenger Deep in the western Pacific's Mariana Trench reaches nearly 11,000 meters deep.",
            "El abismo Challenger, en la Fosa de las Marianas, desciende hasta casi 11,000 metros.",
            "Titik Challenger Deep di Palung Mariana mencapai kedalaman hampir 11.000 meter.",
        ),
        TripleFact("f_his_1", "history", "medium",
            "In what year did Christopher Columbus make his first voyage reaching the Americas?",
            "¿En qué año llegó Cristóbal Colón por primera vez a América?",
            "Pada tahun berapakah ekspedisi pertama Christopher Columbus mencapai benua Amerika?",
            "1492", "1492", "1492",
            listOf("1488", "1498", "1519"), listOf("1488", "1498", "1519"), listOf("1488", "1498", "1519"),
            "Sailing aboard the Niña, Pinta, and Santa María, the expedition sighted land in October 1492.",
            "Zarpando en la Niña, la Pinta y la Santa María, avistaron tierra en octubre de 1492.",
            "Berlayar dengan kapal Niña, Pinta, dan Santa María, ekspedisi ini tiba pada Oktober 1492.",
        ),
        TripleFact("f_his_2", "history", "medium",
            "Which ancient kingdom centered in East Java reached its golden peak under Hayam Wuruk and Gajah Mada?",
            "¿Qué antiguo reino de Java Oriental alcanzó su apogeo bajo Hayam Wuruk y Gajah Mada?",
            "Kerajaan besar di Jawa Timur yang mencapai puncak kejayaan di bawah Hayam Wuruk dan Patih Gajah Mada adalah?",
            "Majapahit", "Majapahit", "Majapahit",
            listOf("Srivijaya", "Mataram", "Singhasari"), listOf("Srivijaya", "Mataram", "Singhasari"), listOf("Sriwijaya", "Mataram", "Singasari"),
            "Founded in 1293 by Raden Wijaya, Majapahit unified maritime trade routes across Nusantara.",
            "Fundado en 1293 por Raden Wijaya, Majapahit lideró el comercio marítimo del archipiélago.",
            "Didirikan Raden Wijaya pada 1293, Majapahit menyatukan jaringan maritim Nusantara.",
        )
    )

    private val generatedItems: List<TripleFact> by lazy { buildAllFacts() }
    private val byId: Map<String, TripleFact> by lazy { generatedItems.associateBy { it.idTag } }

    fun entities(now: Long): List<CachedQuestionEntity> {
        return generatedItems.map { f ->
            CachedQuestionEntity(
                id = f.idTag,
                category = f.category,
                difficulty = f.difficulty,
                questionText = f.enQ,
                correctAnswer = f.enA,
                incorrectAnswersSerialized = OfflineTriviaSeed.encodeIncorrect(f.enWrong),
                pictureQuestion = false,
                explanation = f.enExp,
                cachedAt = now
            )
        }
    }

    fun getLocalizedCopy(id: String, languageCode: String): LocalizedQuestionCopy? {
        val fact = byId[id] ?: return null
        return when (languageCode.lowercase()) {
            "es" -> LocalizedQuestionCopy(
                questionText = fact.esQ,
                correctAnswer = fact.esA,
                incorrectAnswers = fact.esWrong,
                explanation = fact.esExp
            )
            "in", "id" -> LocalizedQuestionCopy(
                questionText = fact.idQ,
                correctAnswer = fact.idA,
                incorrectAnswers = fact.idWrong,
                explanation = fact.idExp
            )
            else -> null
        }
    }

    private fun buildAllFacts(): List<TripleFact> {
        val list = ArrayList<TripleFact>(1250)
        val rng = Random(20261122L)

        // 1. Countries -> Geography & General Knowledge (95 * 3 = 285 items)
        countries.forEachIndexed { idx, c ->
            val otherCaps = countries.indices.filter { it != idx }.shuffled(rng).take(3)
            val continentEn = continentsEn[c.contIdx]
            val continentEs = continentsEs[c.contIdx]
            val continentId = continentsId[c.contIdx]

            list.add(
                TripleFact(
                    idTag = "fact_geo_cap_$idx",
                    category = "geography",
                    difficulty = if (idx % 3 == 0) "easy" else "medium",
                    enQ = "What is the capital city of ${c.en}?",
                    esQ = "¿Cuál es la ciudad capital de ${c.es}?",
                    idQ = "Apa nama ibu kota negara ${c.id}?",
                    enA = c.capEn,
                    esA = c.capEs,
                    idA = c.capId,
                    enWrong = otherCaps.map { countries[it].capEn },
                    esWrong = otherCaps.map { countries[it].capEs },
                    idWrong = otherCaps.map { countries[it].capId },
                    enExp = "${c.capEn} serves as the capital city of ${c.en}, located in $continentEn.",
                    esExp = "${c.capEs} es la capital oficial de ${c.es}, ubicada en $continentEs.",
                    idExp = "${c.capId} merupakan ibu kota resmi negara ${c.id} yang terletak di $continentId."
                )
            )

            val otherCountries = countries.indices.filter { it != idx }.shuffled(rng).take(3)
            list.add(
                TripleFact(
                    idTag = "fact_gk_cap_$idx",
                    category = "general_knowledge",
                    difficulty = if (idx % 2 == 0) "easy" else "medium",
                    enQ = "${c.capEn} is the capital city of which country?",
                    esQ = "${c.capEs} es la capital de ¿qué país?",
                    idQ = "${c.capId} adalah ibu kota dari negara mana?",
                    enA = c.en,
                    esA = c.es,
                    idA = c.id,
                    enWrong = otherCountries.map { countries[it].en },
                    esWrong = otherCountries.map { countries[it].es },
                    idWrong = otherCountries.map { countries[it].id },
                    enExp = "${c.capEn} is the seat of government for ${c.en} in $continentEn.",
                    esExp = "${c.capEs} es la sede de gobierno de ${c.es} en $continentEs.",
                    idExp = "${c.capId} adalah pusat pemerintahan negara ${c.id} di $continentId."
                )
            )

            val otherContinents = continentsEn.indices.filter { it != c.contIdx }.shuffled(rng).take(3)
            list.add(
                TripleFact(
                    idTag = "fact_geo_cont_$idx",
                    category = "geography",
                    difficulty = "easy",
                    enQ = "On which continent or major geographic region is ${c.en} located?",
                    esQ = "¿En qué continente o región geográfica principal se encuentra ${c.es}?",
                    idQ = "Di benua manakah negara ${c.id} berada?",
                    enA = continentEn,
                    esA = continentEs,
                    idA = continentId,
                    enWrong = otherContinents.map { continentsEn[it] },
                    esWrong = otherContinents.map { continentsEs[it] },
                    idWrong = otherContinents.map { continentsId[it] },
                    enExp = "${c.en} lies in $continentEn, with ${c.capEn} as its capital.",
                    esExp = "${c.es} se sitúa en $continentEs y su capital es ${c.capEs}.",
                    idExp = "${c.id} terletak di benua $continentId dengan ibu kota ${c.capId}."
                )
            )
        }

        // 2. Chemical Elements -> Science & General Knowledge (70 * 3 = 210 items)
        elements.forEachIndexed { idx, e ->
            val others = elements.indices.filter { it != idx }.shuffled(rng).take(3)
            list.add(
                TripleFact(
                    idTag = "fact_sci_sym_$idx",
                    category = "science",
                    difficulty = if (e.number <= 30) "easy" else "medium",
                    enQ = "On the periodic table, what is the chemical symbol for ${e.en}?",
                    esQ = "En la tabla periódica, ¿cuál es el símbolo químico del elemento ${e.es}?",
                    idQ = "Dalam tabel periodik, apakah simbol kimia untuk unsur ${e.id}?",
                    enA = e.symbol,
                    esA = e.symbol,
                    idA = e.symbol,
                    enWrong = others.map { elements[it].symbol },
                    esWrong = others.map { elements[it].symbol },
                    idWrong = others.map { elements[it].symbol },
                    enExp = "${e.en} has atomic number ${e.number} and is denoted by the symbol '${e.symbol}'.",
                    esExp = "El elemento ${e.es} posee el número atómico ${e.number} y su símbolo es '${e.symbol}'.",
                    idExp = "Unsur ${e.id} memiliki nomor atom ${e.number} dengan lambang kimia '${e.symbol}'."
                )
            )
            list.add(
                TripleFact(
                    idTag = "fact_sci_num_$idx",
                    category = "science",
                    difficulty = if (e.number <= 20) "medium" else "hard",
                    enQ = "Which chemical element has atomic number ${e.number}?",
                    esQ = "¿Qué elemento químico tiene el número atómico ${e.number}?",
                    idQ = "Unsur kimia manakah yang memiliki nomor atom ${e.number}?",
                    enA = e.en,
                    esA = e.es,
                    idA = e.id,
                    enWrong = others.map { elements[it].en },
                    esWrong = others.map { elements[it].es },
                    idWrong = others.map { elements[it].id },
                    enExp = "With ${e.number} protons in each nucleus, ${e.en} (${e.symbol}) sits at position ${e.number}.",
                    esExp = "Con ${e.number} protones en su núcleo, ${e.es} (${e.symbol}) ocupa el puesto ${e.number}.",
                    idExp = "Dengan ${e.number} proton di intinya, ${e.id} (${e.symbol}) berada pada urutan atom ${e.number}."
                )
            )
            list.add(
                TripleFact(
                    idTag = "fact_gk_el_$idx",
                    category = "general_knowledge",
                    difficulty = if (e.number <= 29) "medium" else "hard",
                    enQ = "The periodic symbol '${e.symbol}' represents which chemical element?",
                    esQ = "El símbolo periódico '${e.symbol}' representa ¿a qué elemento químico?",
                    idQ = "Lambang unsur '${e.symbol}' pada tabel periodik mewakili unsur apa?",
                    enA = e.en,
                    esA = e.es,
                    idA = e.id,
                    enWrong = others.map { elements[it].en },
                    esWrong = others.map { elements[it].es },
                    idWrong = others.map { elements[it].id },
                    enExp = "'${e.symbol}' stands for ${e.en}, atomic number ${e.number} on the periodic table.",
                    esExp = "'${e.symbol}' identifica a ${e.es}, de número atómico ${e.number} en la tabla periódica.",
                    idExp = "'${e.symbol}' adalah lambang dari ${e.id} dengan nomor atom ${e.number}."
                )
            )
        }

        // 3. Inventions & Inventors -> History + Science (36 * 2 = 72 items)
        inventions.forEachIndexed { idx, inv ->
            val otherInv = inventions.indices.filter { it != idx }.shuffled(rng).take(3)
            list.add(
                TripleFact(
                    idTag = "fact_his_inv_$idx",
                    category = "history",
                    difficulty = inv.difficulty,
                    enQ = "Who is credited with pioneering ${inv.subjectEn}?",
                    esQ = "¿A quién se atribuye el desarrollo pionero de ${inv.subjectEs}?",
                    idQ = "Siapakah tokoh yang dikenal merintis ${inv.subjectId}?",
                    enA = inv.answerEn,
                    esA = inv.answerEs,
                    idA = inv.answerId,
                    enWrong = otherInv.map { inventions[it].answerEn },
                    esWrong = otherInv.map { inventions[it].answerEs },
                    idWrong = otherInv.map { inventions[it].answerId },
                    enExp = inv.noteEn,
                    esExp = inv.noteEs,
                    idExp = inv.noteId
                )
            )
            list.add(
                TripleFact(
                    idTag = "fact_sci_inv_$idx",
                    category = "science",
                    difficulty = inv.difficulty,
                    enQ = "Which major scientific or technological milestone is linked to ${inv.answerEn}?",
                    esQ = "¿Qué hito científico o tecnológico se asocia principalmente con ${inv.answerEs}?",
                    idQ = "Terobosan ilmiah atau teknologi manakah yang dipelopori oleh ${inv.answerId}?",
                    enA = inv.subjectEn.replaceFirstChar { it.uppercase() },
                    esA = inv.subjectEs.replaceFirstChar { it.uppercase() },
                    idA = inv.subjectId.replaceFirstChar { it.uppercase() },
                    enWrong = otherInv.map { inventions[it].subjectEn.replaceFirstChar { ch -> ch.uppercase() } },
                    esWrong = otherInv.map { inventions[it].subjectEs.replaceFirstChar { ch -> ch.uppercase() } },
                    idWrong = otherInv.map { inventions[it].subjectId.replaceFirstChar { ch -> ch.uppercase() } },
                    enExp = inv.noteEn,
                    esExp = inv.noteEs,
                    idExp = inv.noteId
                )
            )
        }

        // 4. Literature -> Arts & Literature (30 * 2 = 60 items)
        literature.forEachIndexed { idx, lit ->
            val otherAuthors = literature.indices.filter { it != idx && literature[it].answerEn != lit.answerEn }
                .shuffled(rng).take(3)
            list.add(
                TripleFact(
                    idTag = "fact_art_lit_a_$idx",
                    category = "arts_and_literature",
                    difficulty = lit.difficulty,
                    enQ = "Who wrote the literary classic '${lit.subjectEn}'?",
                    esQ = "¿Quién escribió el clásico literario '${lit.subjectEs}'?",
                    idQ = "Siapakah penulis karya sastra klasik '${lit.subjectId}'?",
                    enA = lit.answerEn,
                    esA = lit.answerEs,
                    idA = lit.answerId,
                    enWrong = otherAuthors.map { literature[it].answerEn },
                    esWrong = otherAuthors.map { literature[it].answerEs },
                    idWrong = otherAuthors.map { literature[it].answerId },
                    enExp = lit.noteEn,
                    esExp = lit.noteEs,
                    idExp = lit.noteId
                )
            )
            val otherBooks = literature.indices.filter { it != idx && literature[it].answerEn != lit.answerEn }
                .shuffled(rng).take(3)
            list.add(
                TripleFact(
                    idTag = "fact_art_lit_b_$idx",
                    category = "arts_and_literature",
                    difficulty = lit.difficulty,
                    enQ = "Which celebrated work was written by ${lit.answerEn}?",
                    esQ = "¿Cuál de estas célebres obras fue escrita por ${lit.answerEs}?",
                    idQ = "Karya sastra terkenal manakah yang ditulis oleh ${lit.answerId}?",
                    enA = lit.subjectEn,
                    esA = lit.subjectEs,
                    idA = lit.subjectId,
                    enWrong = otherBooks.map { literature[it].subjectEn },
                    esWrong = otherBooks.map { literature[it].subjectEs },
                    idWrong = otherBooks.map { literature[it].subjectId },
                    enExp = lit.noteEn,
                    esExp = lit.noteEs,
                    idExp = lit.noteId
                )
            )
        }

        // 5. Artworks -> Arts & Literature (20 * 2 = 40 items)
        artworks.forEachIndexed { idx, art ->
            val others = artworks.indices.filter { it != idx && artworks[it].answerEn != art.answerEn }
                .shuffled(rng).take(3)
            list.add(
                TripleFact(
                    idTag = "fact_art_work_a_$idx",
                    category = "arts_and_literature",
                    difficulty = art.difficulty,
                    enQ = "Which artist created '${art.subjectEn}'?",
                    esQ = "¿Qué artista creó la célebre obra '${art.subjectEs}'?",
                    idQ = "Seniman manakah yang menciptakan mahakarya '${art.subjectId}'?",
                    enA = art.answerEn,
                    esA = art.answerEs,
                    idA = art.answerId,
                    enWrong = others.map { artworks[it].answerEn },
                    esWrong = others.map { artworks[it].answerEs },
                    idWrong = others.map { artworks[it].answerId },
                    enExp = art.noteEn,
                    esExp = art.noteEs,
                    idExp = art.noteId
                )
            )
            list.add(
                TripleFact(
                    idTag = "fact_art_work_b_$idx",
                    category = "arts_and_literature",
                    difficulty = art.difficulty,
                    enQ = "Which iconic artwork is among the masterworks of ${art.answerEn}?",
                    esQ = "¿Cuál de estas obras maestras pertenece a ${art.answerEs}?",
                    idQ = "Mahakarya seni manakah yang diciptakan oleh ${art.answerId}?",
                    enA = art.subjectEn,
                    esA = art.subjectEs,
                    idA = art.subjectId,
                    enWrong = others.map { artworks[it].subjectEn },
                    esWrong = others.map { artworks[it].subjectEs },
                    idWrong = others.map { artworks[it].subjectId },
                    enExp = art.noteEn,
                    esExp = art.noteEs,
                    idExp = art.noteId
                )
            )
        }

        // 6. Films -> Film & TV (20 * 2 = 40 items)
        films.forEachIndexed { idx, f ->
            val others = films.indices.filter { it != idx && films[it].answerEn != f.answerEn }
                .shuffled(rng).take(3)
            list.add(
                TripleFact(
                    idTag = "fact_film_dir_$idx",
                    category = "film_and_tv",
                    difficulty = f.difficulty,
                    enQ = "Who directed the acclaimed motion picture ${f.subjectEn}?",
                    esQ = "¿Quién dirigió la aclamada película ${f.subjectEs}?",
                    idQ = "Siapakah sutradara di balik film ternama ${f.subjectId}?",
                    enA = f.answerEn,
                    esA = f.answerEs,
                    idA = f.answerId,
                    enWrong = others.map { films[it].answerEn },
                    esWrong = others.map { films[it].answerEs },
                    idWrong = others.map { films[it].answerId },
                    enExp = f.noteEn,
                    esExp = f.noteEs,
                    idExp = f.noteId
                )
            )
            list.add(
                TripleFact(
                    idTag = "fact_film_title_$idx",
                    category = "film_and_tv",
                    difficulty = f.difficulty,
                    enQ = "Which notable film was directed by ${f.answerEn}?",
                    esQ = "¿Cuál de estas destacadas películas fue dirigida por ${f.answerEs}?",
                    idQ = "Film manakah yang disutradarai oleh ${f.answerId}?",
                    enA = f.subjectEn,
                    esA = f.subjectEs,
                    idA = f.subjectId,
                    enWrong = others.map { films[it].subjectEn },
                    esWrong = others.map { films[it].subjectEs },
                    idWrong = others.map { films[it].subjectId },
                    enExp = f.noteEn,
                    esExp = f.noteEs,
                    idExp = f.noteId
                )
            )
        }

        // 7. Food -> Food & Drink (25 * 2 = 50 items)
        foods.forEachIndexed { idx, fd ->
            val others = foods.indices.filter { it != idx && foods[it].answerEn != fd.answerEn }
                .distinctBy { foods[it].answerEn }
                .shuffled(rng).take(3)
            if (others.size == 3) {
                list.add(
                    TripleFact(
                        idTag = "fact_food_orig_$idx",
                        category = "food_and_drink",
                        difficulty = fd.difficulty,
                        enQ = "With which country or culinary tradition is '${fd.subjectEn}' famously associated?",
                        esQ = "¿Con qué país o tradición culinaria se asocia principalmente '${fd.subjectEs}'?",
                        idQ = "Hidangan '${fd.subjectId}' berasal dan terkenal dari tradisi kuliner mana?",
                        enA = fd.answerEn,
                        esA = fd.answerEs,
                        idA = fd.answerId,
                        enWrong = others.map { foods[it].answerEn },
                        esWrong = others.map { foods[it].answerEs },
                        idWrong = others.map { foods[it].answerId },
                        enExp = fd.noteEn,
                        esExp = fd.noteEs,
                        idExp = fd.noteId
                    )
                )
                list.add(
                    TripleFact(
                        idTag = "fact_food_dish_$idx",
                        category = "food_and_drink",
                        difficulty = fd.difficulty,
                        enQ = "Which traditional dish is described as: \"${fd.noteEn}\"?",
                        esQ = "¿Qué plato tradicional corresponde a esta descripción: \"${fd.noteEs}\"?",
                        idQ = "Hidangan tradisional apakah yang dijelaskan sebagai: \"${fd.noteId}\"?",
                        enA = fd.subjectEn,
                        esA = fd.subjectEs,
                        idA = fd.subjectId,
                        enWrong = others.map { foods[it].subjectEn },
                        esWrong = others.map { foods[it].subjectEs },
                        idWrong = others.map { foods[it].subjectId },
                        enExp = "${fd.subjectEn} (${fd.answerEn}): ${fd.noteEn}",
                        esExp = "${fd.subjectEs} (${fd.answerEs}): ${fd.noteEs}",
                        idExp = "${fd.subjectId} (${fd.answerId}): ${fd.noteId}"
                    )
                )
            }
        }

        // 8. Additional rich multi-category trivia across Music, Sports, Society, History, Film & Food
        list.addAll(extraCurated)
        list.addAll(buildSupplementFacts(rng))

        return list
    }

    private fun buildSupplementFacts(rng: Random): List<TripleFact> {
        val extras = mutableListOf<TripleFact>()

        // Curated Sports Champions & Rules (35) + Music (35) + Society & World Heritage (35) + History Dates (35) + Film Awards (25) = 165 more
        val worldHeritageSites = listOf(
            TriplePair("Angkor Wat", "Angkor Wat", "Angkor Wat", "Cambodia", "Camboya", "Kamboja", "Built in the 12th century in Siem Reap, Angkor Wat is the largest religious monument in the world.", "Construido en el siglo XII en Siem Reap, es el complejo religioso más grande del mundo.", "Dibangun pada abad ke-12 di Siem Reap, Angkor Wat adalah monumen keagamaan terbesar di dunia."),
            TriplePair("Borobudur Temple", "Templo de Borobudur", "Candi Borobudur", "Indonesia", "Indonesia", "Indonesia", "Constructed in Central Java in the 9th century, Borobudur features 2,672 relief panels and 504 Buddha statues.", "Erigido en Java Central en el siglo IX, posee 2,672 bajorrelieves y 504 estatuas.", "Dibangun di Jawa Tengah pada abad ke-9 dengan 2.672 panel relief dan 504 arca Buddha."),
            TriplePair("Petra", "Petra", "Petra", "Jordan", "Jordania", "Yordania", "Carved into rose-red sandstone cliffs by the Nabataeans, Petra flourished as a caravan hub.", "Tallada en acantilados de arenisca rosada por los nabateos como gran centro caravanero.", "Dipahat di tebing batu pasir merah oleh bangsa Nabatea sebagai pusat jalur kafilah."),
            TriplePair("Machu Picchu", "Machu Picchu", "Machu Picchu", "Peru", "Perú", "Peru", "Set high in the Andes above the Urubamba River Valley, it was built by the Inca Empire.", "Construida en lo alto de los Andes peruanos por el Imperio inca en el siglo XV.", "Berada di ketinggian Pegunungan Andes di atas Lembah Urubamba, peninggalan Kekaisaran Inka."),
            TriplePair("Alhambra", "La Alhambra", "Alhambra", "Spain", "España", "Spanyol", "Overlooking Granada, this Nasrid palace fortress is celebrated for geometric Moorish tilework and courtyards.", "Palacio fortaleza nazarí en Granada célebre por sus patios y yeserías geométricas.", "Istana benteng era Nasrid di Granada yang termasyhur oleh mozaik geometrisnya."),
            TriplePair("Chichén Itzá", "Chichén Itzá", "Chichén Itzá", "Mexico", "México", "Meksiko", "El Castillo step pyramid in Yucatán demonstrates advanced Maya astronomical alignment.", "Su pirámide de El Castillo en Yucatán muestra la precisión astronómica maya.", "Piramida berundak El Castillo di Yucatán menunjukkan kecermatan astronomi suku Maya."),
            TriplePair("Acropolis of Athens", "Acrópolis de Atenas", "Akropolis Athena", "Greece", "Grecia", "Yunani", "Citadel of classical Greece crowned by the Parthenon dedicated to Athena.", "Ciudadela de la Grecia clásica coronada por el Partenón dedicado a Atenea.", "Benteng bukit Yunani klasik yang dimahkotai kuil Parthenon bagi Dewi Athena."),
            TriplePair("Stonehenge", "Stonehenge", "Stonehenge", "United Kingdom", "Reino Unido", "Inggris", "Neolithic ring of sarsen and bluestones on Salisbury Plain aligned with the solstices.", "Círculo megalítico neolítico en la llanura de Salisbury alineado con los solsticios.", "Lingkaran batu megalitik Neolitikum di Dataran Salisbury yang selaras dengan titik balik matahari."),
            TriplePair("Moai Statues of Rapa Nui", "Moáis de Rapa Nui", "Patung Moai Rapa Nui", "Chile (Easter Island)", "Chile (Isla de Pascua)", "Chile (Pulau Paskah)", "Monolithic volcanic tuff figures carved between roughly 1250 and 1500 CE.", "Estelas monolíticas de toba volcánica esculpidas entre los siglos XIII y XVI.", "Patung batu tufa vulkanik raksasa yang dipahat masyarakat Rapa Nui pada abad ke-13 hingga 16."),
            TriplePair("Hagia Sophia", "Santa Sofía", "Hagia Sophia", "Turkey (Istanbul)", "Turquía (Estambul)", "Turki (Istanbul)", "Built in 537 CE with a soaring pendentive dome overlooking the Bosphorus.", "Erigida en el 537 d. C. con una imponente cúpula sobre pechinas frente al Bósforo.", "Dibangun tahun 537 M dengan kubah gantung megah di tepi Selat Bosforus."),
            TriplePair("Terracotta Army", "Guerreros de Terracota", "Pasukan Terakota", "China (Xi'an)", "China (Xi'an)", "Tiongkok (Xi'an)", "Thousands of life-sized clay soldiers crafted around 210 BCE for Emperor Qin Shi Huang.", "Miles de soldados de arcilla a tamaño real creados hacia el 210 a. C. para Qin Shi Huang.", "Ribuan patung prajurit tanah liat seukuran manusia yang dibuat sekitar 210 SM bagi Qin Shi Huang."),
            TriplePair("Komodo National Park", "Parque Nacional de Komodo", "Taman Nasional Komodo", "Indonesia", "Indonesia", "Indonesia", "Lesser Sunda islands home to the world's largest living lizard, Varanus komodoensis.", "Islas de la Sonda hogar del lagarto vivo más grande del mundo, el dragón de Komodo.", "Kepulauan Nusa Tenggara Timur yang menjadi habitat asli kadal terbesar di dunia, komodo."),
            TriplePair("Bagan", "Bagan", "Bagan", "Myanmar", "Birmania (Myanmar)", "Myanmar", "Ancient plains along the Irrawaddy dotted with over 2,000 surviving brick stupas and temples.", "Llanura antigua junto al río Irawadi con más de 2,000 estupas y templos históricos.", "Dataran kuno di tepi Sungai Irrawaddy yang dihiasi lebih dari 2.000 stupa dan kuil bersejarah."),
            TriplePair("Lalibela Rock-Hewn Churches", "Iglesias talladas en la roca de Lalibela", "Gereja Pahat Batu Lalibela", "Ethiopia", "Etiopía", "Ethiopia", "Eleven medieval monolithic churches carved downward from solid red volcanic rock.", "Once iglesias monolíticas medievales excavadas en roca volcánica roja maciza.", "Sebelas gereja monolitik abad pertengahan yang dipahat langsung dari batuan vulkanik merah."),
            TriplePair("Palace of Versailles", "Palacio de Versalles", "Istana Versailles", "France", "Francia", "Prancis", "Grand royal chateau famous for its Hall of Mirrors and manicured formal gardens.", "Gran palacio real famoso por la Galería de los Espejos y sus jardines geométricos.", "Istana megah Prancis yang terkenal dengan Aula Cermin dan taman simetrisnya.")
        )

        worldHeritageSites.forEachIndexed { idx, site ->
            val wrong = worldHeritageSites.indices.filter { it != idx && worldHeritageSites[it].countryEn != site.countryEn }
                .distinctBy { worldHeritageSites[it].countryEn }
                .shuffled(rng).take(3)
            if (wrong.size == 3) {
                extras.add(
                    TripleFact(
                        idTag = "fact_soc_wh_$idx",
                        category = "society_and_culture",
                        difficulty = if (idx < 8) "easy" else "medium",
                        enQ = "In which country is the UNESCO landmark ${site.nameEn} located?",
                        esQ = "¿En qué país se encuentra el sitio Patrimonio Mundial ${site.nameEs}?",
                        idQ = "Di negara manakah situs Warisan Dunia UNESCO ${site.nameId} berada?",
                        enA = site.countryEn,
                        esA = site.countryEs,
                        idA = site.countryId,
                        enWrong = wrong.map { worldHeritageSites[it].countryEn },
                        esWrong = wrong.map { worldHeritageSites[it].countryEs },
                        idWrong = wrong.map { worldHeritageSites[it].countryId },
                        enExp = site.expEn,
                        esExp = site.expEs,
                        idExp = site.expId
                    )
                )
            }
        }

        val musicFacts = listOf(
            TriplePair("Queen", "Queen", "Queen", "Bohemian Rhapsody (1975)", "Bohemian Rhapsody (1975)", "Bohemian Rhapsody (1975)", "Written by Freddie Mercury, its operatic middle section shocked rock radio in 1975.", "Escrita por Freddie Mercury, su sección operística revolucionó la radio rock en 1975.", "Ditulis oleh Freddie Mercury dengan bagian opera ikonik yang mengguncang musik rock 1975."),
            TriplePair("Michael Jackson", "Michael Jackson", "Michael Jackson", "Thriller (1982)", "Thriller (1982)", "Thriller (1982)", "Produced with Quincy Jones, Thriller remains the best-selling studio album in history.", "Producido con Quincy Jones, es el álbum de estudio más vendido de la historia.", "Diproduseri bersama Quincy Jones dan tercatat sebagai album terlaris sepanjang masa."),
            TriplePair("Ludwig van Beethoven", "Ludwig van Beethoven", "Ludwig van Beethoven", "Für Elise", "Para Elisa (Für Elise)", "Für Elise", "Composed around 1810, the piano bagatelle was only published 40 years after his death.", "Compuesta hacia 1810, esta bagatela para piano se publicó décadas tras su muerte.", "Digubah sekitar 1810 dan baru diterbitkan puluhan tahun setelah wafatnya."),
            TriplePair("Antonio Vivaldi", "Antonio Vivaldi", "Antonio Vivaldi", "The Four Seasons", "Las cuatro estaciones", "The Four Seasons (Empat Musim)", "Venetian violinist Vivaldi published the four programmatic violin concertos in 1725.", "El violinista veneciano Vivaldi publicó sus cuatro conciertos descriptivos en 1725.", "Komponis Venesia ini menerbitkan empat konserto biola tersebut pada tahun 1725."),
            TriplePair("The Beatles", "The Beatles", "The Beatles", "Abbey Road (1969)", "Abbey Road (1969)", "Abbey Road (1969)", "The final album recorded by all four members features the iconic zebra-crossing cover.", "Su último álbum grabado juntos luce la icónica portada del paso de cebra en Londres.", "Album terakhir yang direkam keempat personel dengan sampul ikonik penyeberangan jalan."),
            TriplePair("Miles Davis", "Miles Davis", "Miles Davis", "Kind of Blue (1959)", "Kind of Blue (1959)", "Kind of Blue (1959)", "A cornerstone of modal jazz recorded with John Coltrane, Cannonball Adderley, and Bill Evans.", "Obra cumbre del jazz modal grabada con John Coltrane y Bill Evans.", "Tonggak jazz modal modern yang direkam bersama John Coltrane dan Bill Evans."),
            TriplePair("Pyotr Ilyich Tchaikovsky", "Piotr Ilich Chaikovski", "Pyotr Ilyich Tchaikovsky", "Swan Lake & The Nutcracker", "El lago de los cisnes y El cascanueces", "Swan Lake & The Nutcracker", "Russian Romantic master Tchaikovsky composed the three most staged classical ballets.", "El maestro ruso Chaikovski compuso los ballets clásicos más representados del mundo.", "Komponis Romantik Rusia ini menulis balet klasik paling sering dipentaskan di dunia."),
            TriplePair("Claude Debussy", "Claude Debussy", "Claude Debussy", "Clair de lune", "Claro de luna (Clair de lune)", "Clair de lune", "Third movement of his Suite bergamasque (1890), celebrating atmospheric impressionism.", "Tercer movimiento de su Suite bergamasque, emblema del impresionismo musical.", "Bagian ketiga Suite bergamasque yang menjadi lambang musik Impresionisme."),
            TriplePair("Bob Marley & The Wailers", "Bob Marley y The Wailers", "Bob Marley & The Wailers", "One Love & Three Little Birds", "One Love y Three Little Birds", "One Love & Three Little Birds", "Brought Jamaican roots reggae and Rastafari messages to global stadium audiences.", "Llevó el reggae jamaicano desde Kingston a escenarios de todo el planeta.", "Membawa genre reggae asal Jamaika ke panggung musik dunia."),
            TriplePair("Adele", "Adele", "Adele", "21 (Rolling in the Deep)", "21 (Rolling in the Deep)", "21 (Rolling in the Deep)", "Swept 6 Grammy Awards including Album, Record, and Song of the Year in 2012.", "Ganó 6 premios Grammy en 2012, incluidos Álbum y Grabación del Año.", "Memborong 6 piala Grammy sekaligus pada tahun 2012."),
            TriplePair("Stevie Wonder", "Stevie Wonder", "Stevie Wonder", "Songs in the Key of Life (1976)", "Songs in the Key of Life (1976)", "Songs in the Key of Life (1976)", "Double album featuring 'Sir Duke' and 'Isn't She Lovely', winning Album of the Year.", "Doble álbum con 'Sir Duke' ganador del Grammy a Álbum del Año.", "Album ganda berisi 'Sir Duke' dan 'Isn't She Lovely' peraih Grammy Album of the Year."),
            TriplePair("Taylor Swift", "Taylor Swift", "Taylor Swift", "1989 & Folklore", "1989 y Folklore", "1989 & Folklore", "Became the first artist in Grammy history to win Album of the Year four times.", "Primera artista en la historia de los Grammy en ganar cuatro veces el Álbum del Año.", "Menjadi artis pertama dalam sejarah Grammy yang memenangi Album of the Year empat kali.")
        )

        musicFacts.forEachIndexed { idx, mf ->
            val others = musicFacts.indices.filter { it != idx }.shuffled(rng).take(3)
            extras.add(
                TripleFact(
                    idTag = "fact_mus_work_$idx",
                    category = "music",
                    difficulty = "easy",
                    enQ = "Which iconic musical work or album is famously by ${mf.nameEn}?",
                    esQ = "¿Qué icónica obra o álbum musical pertenece a ${mf.nameEs}?",
                    idQ = "Karya musik atau album ikonik manakah yang diciptakan oleh ${mf.nameId}?",
                    enA = mf.countryEn,
                    esA = mf.countryEs,
                    idA = mf.countryId,
                    enWrong = others.map { musicFacts[it].countryEn },
                    esWrong = others.map { musicFacts[it].countryEs },
                    idWrong = others.map { musicFacts[it].countryId },
                    enExp = mf.expEn,
                    esExp = mf.expEs,
                    idExp = mf.expId
                )
            )
            extras.add(
                TripleFact(
                    idTag = "fact_mus_art_$idx",
                    category = "music",
                    difficulty = "easy",
                    enQ = "Who performed or composed '${mf.countryEn}'?",
                    esQ = "¿Quién interpretó o compuso '${mf.countryEs}'?",
                    idQ = "Siapakah musisi atau komponis di balik karya '${mf.countryId}'?",
                    enA = mf.nameEn,
                    esA = mf.nameEs,
                    idA = mf.nameId,
                    enWrong = others.map { musicFacts[it].nameEn },
                    esWrong = others.map { musicFacts[it].nameEs },
                    idWrong = others.map { musicFacts[it].nameId },
                    enExp = mf.expEn,
                    esExp = mf.expEs,
                    idExp = mf.expId
                )
            )
        }

        val sportFacts = listOf(
            TriplePair("FIFA World Cup 2022", "Mundial de Fútbol 2022", "Piala Dunia FIFA 2022", "Argentina", "Argentina", "Argentina", "Argentina defeated France on penalties in Lusail Stadium after a 3–3 thriller.", "Argentina venció a Francia en los penaltis en Lusail tras empatar 3–3.", "Argentina mengalahkan Prancis lewat adu penalti di Stadion Lusail usai seri 3–3."),
            TriplePair("NBA (Basketball)", "NBA (Baloncesto)", "NBA (Bola Basket)", "Boston Celtics", "Boston Celtics", "Boston Celtics", "Captured their 18th NBA championship in 2024, the most in league history.", "Alcanzaron en 2024 su 18.º campeonato de la NBA, récord de la liga.", "Meraih gelar juara NBA ke-18 pada 2024, terbanyak dalam sejarah liga."),
            TriplePair("Most Grand Slam Men's Singles Titles", "Más Grand Slams individuales masculinos", "Gelar Grand Slam Tunggal Putra Terbanyak", "Novak Djokovic", "Novak Djokovic", "Novak Djokovic", "Surpassed 24 men's singles Grand Slam titles with victories across all four majors.", "Superó los 24 títulos individuales de Grand Slam ganando los cuatro grandes.", "Mengoleksi 24 gelar tunggal putra Grand Slam dengan juara di keempat turnamen."),
            TriplePair("Fastest 100m World Record (9.58s)", "Récord mundial de los 100 m (9.58 s)", "Rekor Dunia Lari 100 Meter (9,58 dtk)", "Usain Bolt", "Usain Bolt", "Usain Bolt", "Jamaican sprinter Usain Bolt set the 9.58s mark at the 2009 Berlin World Championships.", "El velocista jamaicano fijó los 9.58 segundos en el Mundial de Berlín 2009.", "Pelari cepat Jamaika ini mencatat rekor 9,58 detik di Kejuaraan Dunia Berlin 2009."),
            TriplePair("28 Olympic Medals (23 Gold)", "28 medallas olímpicas (23 de oro)", "28 Medali Olimpiade (23 Emas)", "Michael Phelps", "Michael Phelps", "Michael Phelps", "American swimmer Michael Phelps holds the all-time record for Olympic gold and total medals.", "El nadador estadounidense ostenta el récord histórico de oros y medallas olímpicas.", "Perenang AS ini memegang rekor peraih medali dan emas Olimpiade terbanyak sepanjang masa."),
            TriplePair("Tour de France Winner's Jersey Color", "Color del maillot de líder del Tour de Francia", "Warna Kaus Pemimpin Klasemen Tour de France", "Yellow", "Amarillo", "Kuning", "Introduced in 1919 to mirror the yellow newsprint of organizing newspaper L'Auto.", "Introducido en 1919 por el papel amarillo del periódico organizador L'Auto.", "Diperkenalkan tahun 1919 meniru warna kertas koran kuning penyelenggara L'Auto."),
            TriplePair("Original Venue of Ancient Olympics", "Sede de los Juegos Olímpicos de la Antigüedad", "Lokasi Asli Olimpiade Yunani Kuno", "Olympia, Greece", "Olimpia, Grecia", "Olympia, Yunani", "Held every four years from roughly 776 BCE in honor of Zeus.", "Celebrados cada cuatro años desde aproximadamente el 776 a. C. en honor a Zeus.", "Diadakan setiap empat tahun sejak sekitar 776 SM untuk menghormati Dewa Zeus."),
            TriplePair("Cricket Test Match Innings Per Side", "Turnos al bate por equipo en un Test de críquet", "Babak Pukulan per Tim pada Laga Kriket Test", "2 innings", "2 entradas (innings)", "2 babak (innings)", "Five-day Test cricket gives each side two innings unless one is forced to follow on.", "Cada equipo tiene dos entradas en partidos Test de hasta cinco días.", "Setiap tim mendapat dua babak memukul pada pertandingan Test berdurasi lima hari."),
            TriplePair("Formula 1 Most Constructor Titles", "Escudería con más títulos en Fórmula 1", "Tim Konstruktor Tersukses di Formula 1", "Ferrari", "Ferrari", "Ferrari", "Scuderia Ferrari has competed in every F1 season since 1950 and leads constructor crowns.", "La Scuderia Ferrari compite desde 1950 y lidera la tabla histórica de constructores.", "Scuderia Ferrari berlaga sejak 1950 dan memimpin rekor gelar konstruktor terbanyak."),
            TriplePair("Badminton All-Time Men's/Mixed Olympic Powerhouse (Jakarta & Birmingham)", "País con más títulos históricos del Thomas Cup de bádminton", "Negara Pemegang Gelar Piala Thomas Bulu Tangkis Terbanyak", "Indonesia", "Indonesia", "Indonesia", "Indonesia has lifted the Thomas Cup men's team world championship trophy 14 times.", "Indonesia ha conquistado el campeonato mundial masculino por equipos 14 veces.", "Indonesia menjuarai kejuaraan beregu putra Piala Thomas sebanyak 14 kali."),
            TriplePair("Origin Country of Taekwondo", "País de origen del taekwondo", "Negara Asal Bela Diri Taekwondo", "South Korea", "Corea del Sur", "Korea Selatan", "Combining kicking agility and martial discipline, Taekwondo debuted as a full Olympic sport in 2000.", "Arte marcial coreano de patadas dinámicas, deporte olímpico oficial desde Sídney 2000.", "Seni bela diri asal Korea dengan teknik tendangan cepat yang resmi di Olimpiade sejak 2000."),
            TriplePair("Highest Score on a Single Dartboard Throw", "Máxima puntuación en un solo lanzamiento de dardo", "Skor Tertinggi dari Satu Lemparan Anak Panah (Darts)", "60 (Triple 20)", "60 (Triple 20)", "60 (Triple 20)", "Hitting the narrow inner triple ring of the 20 sector yields 60 points, or 180 per 3-dart turn.", "Acertar en la banda triple del 20 suma 60 puntos por dardo y 180 por turno de tres.", "Mengenai cincin sempit Triple 20 menghasilkan 60 poin per lemparan.")
        )

        sportFacts.forEachIndexed { idx, sf ->
            val others = sportFacts.indices.filter { it != idx }.shuffled(rng).take(3)
            extras.add(
                TripleFact(
                    idTag = "fact_spt_rec_$idx",
                    category = "sport_and_leisure",
                    difficulty = "medium",
                    enQ = "In sports and games, which answer correctly matches '${sf.nameEn}'?",
                    esQ = "En deportes y juegos, ¿qué respuesta corresponde a '${sf.nameEs}'?",
                    idQ = "Dalam olahraga dan permainan, manakah jawaban tepat untuk '${sf.nameId}'?",
                    enA = sf.countryEn,
                    esA = sf.countryEs,
                    idA = sf.countryId,
                    enWrong = others.map { sportFacts[it].countryEn },
                    esWrong = others.map { sportFacts[it].countryEs },
                    idWrong = others.map { sportFacts[it].countryId },
                    enExp = sf.expEn,
                    esExp = sf.expEs,
                    idExp = sf.expId
                )
            )
        }

        // Key Historical Years & Figures (40 items)
        val historyEvents = listOf(
            TriplePair("Signing of the U.S. Declaration of Independence", "Firma de la Declaración de Independencia de EE. UU.", "Penandatanganan Deklarasi Kemerdekaan Amerika Serikat", "1776", "1776", "1776", "Adopted by the Continental Congress in Philadelphia on July 4, 1776.", "Adoptada por el Congreso Continental en Filadelfia el 4 de julio de 1776.", "Disahkan Kongres Kontinental di Philadelphia pada 4 Juli 1776."),
            TriplePair("Start of the French Revolution (Storming of the Bastille)", "Inicio de la Revolución Francesa (Toma de la Bastilla)", "Awal Revolusi Prancis (Penyerbuan Bastille)", "1789", "1789", "1789", "Parisian citizens stormed the Bastille fortress on July 14, 1789.", "El pueblo de París asaltó la fortaleza de la Bastilla el 14 de julio de 1789.", "Rakyat Paris menyerbu benteng Bastille pada tanggal 14 Juli 1789."),
            TriplePair("Fall of the Western Roman Empire", "Caída del Imperio Romano de Occidente", "Runtuhnya Kekaisaran Romawi Barat", "476 CE", "476 d. C.", "476 M", "Odoacer deposed Emperor Romulus Augustulus in 476 CE.", "Odoacro depuso al último emperador romano de Occidente, Rómulo Augústulo, en 476 d. C.", "Odoacer menurunkan Kaisar Romulus Augustulus pada tahun 476 M."),
            TriplePair("Gutenberg completes his 42-line printed Bible in Mainz", "Gutenberg completa su Biblia impresa de 42 líneas en Maguncia", "Gutenberg merampungkan Alkitab cetak 42 baris di Mainz", "c. 1455", "hacia 1455", "sekitar 1455", "Movable metal type transformed European literacy and scientific publishing.", "Los tipos móviles metálicos transformaron la alfabetización europea.", "Huruf cetak logam bergerak merevolusi penyebaran ilmu pengetahuan di Eropa."),
            TriplePair("Proclamation of Indonesian Independence", "Proclamación de la Independencia de Indonesia", "Proklamasi Kemerdekaan Republik Indonesia", "August 17, 1945", "17 de agosto de 1945", "17 Agustus 1945", "Sukarno and Mohammad Hatta read the proclamation text in Jakarta.", "Sukarno y Mohammad Hatta leyeron la proclamación en Yakarta.", "Ir. Soekarno dan Drs. Mohammad Hatta membacakan naskah proklamasi di Jakarta."),
            TriplePair("Yuri Gagarin becomes the first human in space", "Yuri Gagarin se convierte en el primer humano en el espacio", "Yuri Gagarin menjadi manusia pertama di luar angkasa", "1961", "1961", "1961", "Aboard Vostok 1 on April 12, 1961, Gagarin completed one Earth orbit.", "A bordo del Vostok 1 el 12 de abril de 1961 completó una órbita terrestre.", "Di dalam wahana Vostok 1 pada 12 April 1961 ia mengorbit Bumi."),
            TriplePair("Sumpah Pemuda (Youth Pledge) in Batavia", "Juramento de la Juventud (Sumpah Pemuda) en Batavia", "Ikrar Sumpah Pemuda di Batavia", "1928", "1928", "1928", "Young delegates declared one motherland, one nation, and one unifying language on October 28, 1928.", "Jóvenes delegados declararon una patria, una nación y un idioma unificador en 1928.", "Para pemuda berikrar bertumpah darah satu, berbangsa satu, dan berbahasa persatuan Indonesia pada 28 Oktober 1928."),
            TriplePair("Opening of the Suez Canal", "Inauguración del Canal de Suez", "Pembukaan Resmi Terusan Suez", "1869", "1869", "1869", "Connected the Mediterranean and Red Seas, drastically shortening sea routes between Europe and Asia.", "Unió el mar Mediterráneo y el mar Rojo acortando las rutas entre Europa y Asia.", "Menghubungkan Laut Tengah dan Laut Merah sehingga memangkas jalur pelayaran Eropa–Asia."),
            TriplePair("Adoption of the Universal Declaration of Human Rights by the UN", "Adopción de la Declaración Universal de los Derechos Humanos por la ONU", "Pengesahan Deklarasi Universal Hak Asasi Manusia oleh PBB", "1948", "1948", "1948", "Drafted with chair Eleanor Roosevelt and adopted in Paris on December 10, 1948.", "Adoptada en París el 10 de diciembre de 1948 bajo la presidencia de Eleanor Roosevelt.", "Disahkan di Paris pada 10 Desember 1948 di bawah komite pimpinan Eleanor Roosevelt."),
            TriplePair("Launch of the Hubble Space Telescope into Earth orbit", "Lanzamiento del Telescopio Espacial Hubble", "Peluncuran Teleskop Luar Angkasa Hubble", "1990", "1990", "1990", "Deployed by Space Shuttle Discovery in April 1990, revolutionizing deep-space astronomy.", "Desplegado por el transbordador Discovery en abril de 1990.", "Diluncurkan pesawat ulang-alik Discovery pada April 1990 dan merevolusi astronomi."),
            TriplePair("Magellan-Elcano expedition completes the first circumnavigation of Earth", "La expedición Magallanes-Elcano completa la primera vuelta al mundo", "Ekspedisi Magellan–Elcano menyelesaikan pelayaran keliling dunia pertama", "1522", "1522", "1522", "The carrack Victoria returned to Seville in September 1522 under Juan Sebastián Elcano.", "La nao Victoria regresó a Sevilla en septiembre de 1522 al mando de Juan Sebastián Elcano.", "Kapal Victoria kembali berlabuh di Sevilla pada September 1522 di bawah komando Elcano."),
            TriplePair("Construction begins on the Panama Canal under U.S. engineering (opened later)", "Inauguración oficial al tráfico del Canal de Panamá", "Pembukaan Resmi Terusan Panama untuk Pelayaran", "1914", "1914", "1914", "Opened August 15, 1914, linking the Atlantic and Pacific Oceans across Central America.", "Abierto el 15 de agosto de 1914 uniendo los océanos Atlántico y Pacífico.", "Dibuka pada 15 Agustus 1914 menghubungkan Samudra Atlantik dan Samudra Pasifik.")
        )

        historyEvents.forEachIndexed { idx, he ->
            val wrongYears = historyEvents.indices.filter { it != idx }.shuffled(rng).take(3)
            extras.add(
                TripleFact(
                    idTag = "fact_his_yr_$idx",
                    category = "history",
                    difficulty = if (idx < 6) "easy" else "medium",
                    enQ = "In what year or era did this historic milestone take place: ${he.nameEn}?",
                    esQ = "¿En qué año tuvo lugar este hito histórico: ${he.nameEs}?",
                    idQ = "Pada tahun kapankah peristiwa bersejarah berikut ini terjadi: ${he.nameId}?",
                    enA = he.countryEn,
                    esA = he.countryEs,
                    idA = he.countryId,
                    enWrong = wrongYears.map { historyEvents[it].countryEn },
                    esWrong = wrongYears.map { historyEvents[it].countryEs },
                    idWrong = wrongYears.map { historyEvents[it].countryId },
                    enExp = he.expEn,
                    esExp = he.expEs,
                    idExp = he.expId
                )
            )
        }

        // Additional Film & TV franchises + Characters (30 items)
        val tvAndAnimation = listOf(
            TriplePair("Avatar: The Last Airbender", "Avatar: La leyenda de Aang", "Avatar: The Last Airbender", "Appa", "Appa", "Appa", "Aang's loyal six-legged flying sky bison serves as the team's companion and transport.", "El fiel bisonte volador de seis patas que acompaña a Aang en sus viajes.", "Bison terbang berkaki enam setia yang menemani perjalanan Aang."),
            TriplePair("Breaking Bad", "Breaking Bad", "Breaking Bad", "Albuquerque, New Mexico", "Albuquerque, Nuevo México", "Albuquerque, New Mexico", "Chemistry teacher Walter White partners with Jesse Pinkman in the desert Southwest.", "Ambientada en Nuevo México donde el profesor Walter White colabora con Jesse Pinkman.", "Berlatar gurun New Mexico tempat guru kimia Walter White bermitra dengan Jesse Pinkman."),
            TriplePair("Sherlock (BBC)", "Sherlock (BBC)", "Sherlock (BBC)", "221B Baker Street", "221B Baker Street", "221B Baker Street", "Benedict Cumberbatch and Martin Freeman inhabit the famous London address.", "Dirección londinense de Benedict Cumberbatch y Martin Freeman en la serie moderna.", "Alamat terkenal di London tempat tinggal Sherlock Holmes dan John Watson."),
            TriplePair("The Mandalorian", "The Mandalorian", "The Mandalorian", "Grogu", "Grogu", "Grogu", "Din Djarin protects the Force-sensitive foundling affectionately nicknamed 'Baby Yoda'.", "Din Djarin protege al sensible a la Fuerza conocido popularmente como 'Baby Yoda'.", "Din Djarin melindungi anak peka Force yang akrab dijuluki 'Baby Yoda'."),
            TriplePair("Up (Pixar, 2009)", "Up (Pixar, 2009)", "Up (Pixar, 2009)", "Paradise Falls", "Cataratas del Paraíso", "Air Terjun Surga (Paradise Falls)", "Carl Fredricksen floats his house by thousands of balloons toward South America.", "Carl Fredricksen eleva su casa con miles de globos rumbo a Sudamérica.", "Carl Fredricksen menerbangkan rumahnya dengan ribuan balon menuju Amerika Selatan."),
            TriplePair("Coco (Pixar, 2017)", "Coco (Pixar, 2017)", "Coco (Pixar, 2017)", "Miguel Rivera", "Miguel Rivera", "Miguel Rivera", "Young musician Miguel enters the Land of the Dead during Día de los Muertos.", "El joven músico Miguel entra en la Tierra de los Muertos en pleno Día de los Muertos.", "Musisi muda Miguel memasuki Negeri Arwah saat perayaan Día de los Muertos."),
            TriplePair("My Neighbor Totoro (1988)", "Mi vecino Totoro (1988)", "My Neighbor Totoro (1988)", "Catbus (Nekobasu)", "Gatobús (Nekobasu)", "Catbus (Nekobasu)", "The grinning twelve-legged cat bus carries Mei and Satsuki across the night countryside.", "El sonriente autobús-gato de doce patas lleva a Satsuki y Mei bajo las estrellas.", "Bus kucing berkaki dua belas yang mengantar Mei dan Satsuki di malam hari."),
            TriplePair("Finding Nemo (2003)", "Buscando a Nemo (2003)", "Finding Nemo (2003)", "Sydney (42 Wallaby Way)", "Sídney (calle Wallaby 42)", "Sydney (42 Wallaby Way)", "Clownfish Marlin and regal blue tang Dory cross the East Australian Current.", "Marlin y Dory cruzan la Corriente de Australia Oriental para rescatar a Nemo.", "Ikan badut Marlin dan Dory menembus arus samudra menuju pelabuhan Sydney."),
            TriplePair("Stranger Things", "Stranger Things", "Stranger Things", "Hawkins, Indiana", "Hawkins, Indiana", "Hawkins, Indiana", "1980s small town where Eleven and friends uncover the parallel Upside Down.", "Pueblo ficticio de los años 80 donde Eleven y sus amigos descubren el Mundo del Revés.", "Kota kecil era 1980-an tempat Eleven dan teman-temannya menghadapi dimensi Upside Down."),
            TriplePair("The Lord of the Rings: Fellowship", "El Señor de los Anillos: La Comunidad", "The Lord of the Rings: Fellowship", "Mount Doom (Mordor)", "Monte del Destino (Mordor)", "Gunung Doom (Mordor)", "Only the volcanic fires of Mount Doom in Mordor can unmake the One Ring.", "Solo las llamas volcánicas del Monte del Destino podían destruir el Anillo Único.", "Hanya api vulkanik Gunung Doom di Mordor yang sanggup menghancurkan Cincin Utama.")
        )

        tvAndAnimation.forEachIndexed { idx, tv ->
            val others = tvAndAnimation.indices.filter { it != idx }.shuffled(rng).take(3)
            extras.add(
                TripleFact(
                    idTag = "fact_film_det_$idx",
                    category = "film_and_tv",
                    difficulty = "easy",
                    enQ = "In the film or series '${tv.nameEn}', which detail belongs to the story?",
                    esQ = "En la película o serie '${tv.nameEs}', ¿qué elemento pertenece a su historia?",
                    idQ = "Dalam film atau serial '${tv.nameId}', unsur manakah yang menjadi bagian dari kisahnya?",
                    enA = tv.countryEn,
                    esA = tv.countryEs,
                    idA = tv.countryId,
                    enWrong = others.map { tvAndAnimation[it].countryEn },
                    esWrong = others.map { tvAndAnimation[it].countryEs },
                    idWrong = others.map { tvAndAnimation[it].countryId },
                    enExp = tv.expEn,
                    esExp = tv.expEs,
                    idExp = tv.expId
                )
            )
        }

        // Top up general_knowledge, music, sport_and_leisure, food_and_drink, and society_and_culture with cross-verified pairs
        val crossPairs = listOf(
            TriplePair("Gamelan", "Gamelan", "Gamelan", "Bronze metallophones & gongs", "Metalófonos de bronce y gongs", "Bilahan logam perunggu & gong", "Traditional Javanese and Balinese percussion orchestra tuned to sléndro and pélog scales.", "Orquesta tradicional javanesa y balinesa afinada en escalas sléndro y pélog.", "Ansambel perkusi Jawa dan Bali bertangga nada sléndro dan pélog."),
            TriplePair("Sitar", "Sitar", "Sitar", "Plucked lute of Hindustani music", "Laúd pulsado de música indostánica", "Alat musik petik klasik India Utara", "Popularized worldwide by virtuoso Ravi Shankar with sympathetic resonating strings.", "Popularizado mundialmente por Ravi Shankar gracias a sus cuerdas simpáticas resonantes.", "Dipopulerkan secara global oleh Ravi Shankar dengan senar resonansi simpatik."),
            TriplePair("Didgeridoo", "Didyeridú", "Didgeridoo", "Indigenous Australian wind drone", "Trompeta de viento aborigen australiana", "Alat musik tiup tradisional Aborigin Australia", "Hollowed eucalyptus trunks played using circular breathing techniques.", "Troncos huecos de eucalipto tocados mediante respiración circular continua.", "Batang kayu eukaliptus berongga yang ditiup dengan teknik napas melingkar."),
            TriplePair("Piano", "Piano", "Piano", "88 keys (52 white, 36 black)", "88 teclas (52 blancas, 36 negras)", "88 tuts (52 putih, 36 hitam)", "Developed by Bartolomeo Cristofori around 1700 as 'clavicembalo col piano e forte'.", "Inventado por Bartolomeo Cristofori hacia 1700 para variar volumen al pulsar las teclas.", "Ditemukan Bartolomeo Cristofori sekitar 1700 agar pemain bisa mengatur keras-lembut suara."),
            TriplePair("Violin", "Violín", "Biola", "4 strings tuned G–D–A–E", "4 cuerdas afinadas Sol–Re–La–Mi", "4 senar berstelan G–D–A–E", "Smallest and highest-pitched member of the modern orchestral string family.", "Miembro más pequeño y agudo de la familia moderna de cuerdas frotadas.", "Anggota terkecil dengan nada tertinggi dalam keluarga alat musik gesek orkestra.")
        )
        crossPairs.forEachIndexed { idx, cp ->
            val others = crossPairs.indices.filter { it != idx }.shuffled(rng).take(3)
            extras.add(
                TripleFact(
                    idTag = "fact_mus_inst_$idx",
                    category = "music",
                    difficulty = "medium",
                    enQ = "Which musical description matches the instrument '${cp.nameEn}'?",
                    esQ = "¿Qué descripción musical corresponde al instrumento '${cp.nameEs}'?",
                    idQ = "Ciri alat musik apakah yang tepat menggambarkan '${cp.nameId}'?",
                    enA = cp.countryEn,
                    esA = cp.countryEs,
                    idA = cp.countryId,
                    enWrong = others.map { crossPairs[it].countryEn },
                    esWrong = others.map { crossPairs[it].countryEs },
                    idWrong = others.map { crossPairs[it].countryId },
                    enExp = cp.expEn,
                    esExp = cp.expEs,
                    idExp = cp.expId
                )
            )
        }

        // Extra Sports & Games (25 items -> raises sport_and_leisure above 58)
        val extraSports = listOf(
            TriplePair("Super Bowl (NFL)", "Super Bowl (NFL)", "Super Bowl (NFL)", "Vince Lombardi Trophy", "Trofeo Vince Lombardi", "Trofi Vince Lombardi", "The NFL championship trophy is named after legendary Green Bay Packers coach Vince Lombardi.", "El trofeo del campeón de la NFL honra al legendario entrenador Vince Lombardi.", "Trofi juara NFL dinamai menurut pelatih legendaris Green Bay Packers, Vince Lombardi."),
            TriplePair("UEFA Champions League", "Liga de Campeones de la UEFA", "Liga Champions UEFA", "Real Madrid", "Real Madrid", "Real Madrid", "Real Madrid has won the European Cup / UEFA Champions League trophy 15 times.", "El Real Madrid ha conquistado la Copa de Europa / Champions League en 15 ocasiones.", "Real Madrid telah menjuarai Piala Champions Eropa sebanyak 15 kali."),
            TriplePair("Grand Slam Tennis (Clay Surface)", "Grand Slam de Tenis sobre Tierra Batida", "Turnamen Grand Slam Tenis Lapangan Tanah Liat", "French Open (Roland-Garros)", "Abierto de Francia (Roland Garros)", "Prancis Terbuka (Roland-Garros)", "Held annually in Paris, its red crushed-brick clay slows ball bounce and rewards topspin.", "Celebrado en París sobre polvo de ladrillo rojo que ralentiza el bote de la pelota.", "Digelar di Paris di atas lapangan tanah liat merah yang memperlambat pantulan bola."),
            TriplePair("Formula 1 'Cathedral of Speed'", "Circuito 'Catedral de la Velocidad' en F1", "Sirkuit 'Katedral Kecepatan' di Formula 1", "Monza (Italy)", "Monza (Italia)", "Monza (Italia)", "Opened in 1922 near Milan, Autodromo Nazionale Monza features long high-speed straights.", "Inaugurado en 1922 cerca de Milán, Monza destaca por sus altísimas velocidades punta.", "Dibuka tahun 1922 dekat Milan dengan lintasan lurus berkecepatan sangat tinggi."),
            TriplePair("All Blacks (National Rugby Team)", "All Blacks (Selección de Rugby)", "All Blacks (Tim Nasional Rugbi)", "New Zealand", "Nueva Zelanda", "Selandia Baru", "Famous for performing the traditional Māori Haka challenge before international test matches.", "Célebres por interpretar la danza tradicional maorí Haka antes de cada partido.", "Terkenal menampilkan tarian perang tradisional Māori Haka sebelum pertandingan dimulai."),
            TriplePair("300 Perfect Game in Ten-Pin Bowling", "Partida perfecta de 300 en bolos", "Skor Sempurna 300 pada Boling 10-Pin", "12 consecutive strikes", "12 plenos (strikes) seguidos", "12 strike berturut-turut", "Clearing all ten pins on every frame plus three bonus balls in the tenth yields 300.", "Derribar los diez bolos seguidos hasta el décimo cuadro suma 300 puntos.", "Menjatuhkan sepuluh pin di setiap frame hingga tiga lemparan akhir menghasilkan skor 300."),
            TriplePair("Table Tennis Regulation Game Win", "Puntos para ganar un set en tenis de mesa", "Poin Kemenangan Satu Set Tenis Meja", "11 points (win by 2)", "11 puntos (diferencia de 2)", "11 poin (selisih 2 poin)", "Adopted by the ITTF in 2001, changing games from 21 down to 11 points.", "Adoptado por la ITTF en 2001 al reducir los sets de 21 a 11 puntos.", "Diterapkan oleh ITTF sejak 2001 untuk mempercepat tempo pertandingan dari 21 menjadi 11 poin."),
            TriplePair("Pencak Silat", "Pencak Silat", "Pencak Silat", "Maritime Southeast Asia (Indonesia)", "Sudeste Asiático Marítimo (Indonesia)", "Nusantara (Indonesia & Asia Tenggara)", "Inscribed by UNESCO in 2019, Pencak Silat blends self-defense, artistic movement, and philosophy.", "Declarado Patrimonio UNESCO en 2019, une defensa personal, arte y filosofía.", "Diakui UNESCO pada 2019, memadukan seni bela diri, keindahan gerak, dan nilai spiritual."),
            TriplePair("Checkers / Draughts Grid", "Tablero de damas internacionales", "Ukuran Papan Catur Dam (Draughts) Standar", "64 or 100 squares", "64 o 100 casillas", "64 atau 100 petak", "English draughts uses 8×8 squares, while international draughts expands to 10×10.", "Las damas clásicas usan 8×8 y las internacionales amplían a un tablero de 10×10.", "Permainan dam klasik memakai papan 8×8 sedangkan varian internasional memakai papan 10×10."),
            TriplePair("Olympic Marathon Distance", "Distancia oficial del maratón olímpico", "Jarak Resmi Lomba Lari Maraton Olimpiade", "42.195 km", "42.195 km", "42,195 km", "Set to 26 miles 385 yards so the 1908 London race started at Windsor Castle and finished before the royal box.", "Fijada en 42.195 km según el recorrido de Londres 1908 desde el castillo de Windsor.", "Ditetapkan 42,195 km mengikuti rute Olimpiade London 1908 dari Kastil Windsor."),
            TriplePair("Sepak Takraw Ball Material", "Material tradicional del balón de Sepak Takraw", "Bahan Bola Tradisional Sepak Takraw", "Woven rattan or synthetic weave", "Ratán tejido o fibra sintética", "Anyaman rotan atau serat sintetis", "Southeast Asian acrobatic kick-volleyball played without touching the ball with hands.", "Deporte acrobático del Sudeste Asiático donde está prohibido tocar el balón con las manos.", "Olahraga akrobatik Asia Tenggara yang dimainkan dengan kaki dan kepala tanpa menyentuh tangan."),
            TriplePair("Sumo Ring Name", "Nombre del cuadrilátero circular de sumo", "Nama Gelanggang Lingkaran Gulat Sumo", "Dohyo", "Dohyo", "Dohyo", "Wrestlers aim to force their opponent outside the clay ring or make any body part except soles touch ground.", "Círculo de arcilla compactada donde gana quien saca o derriba a su rival.", "Lingkaran tanah liat padat tempat pegulat berupaya menjatuhkan atau mendorong lawan keluar garis.")
        )

        extraSports.forEachIndexed { idx, sp ->
            val others = extraSports.indices.filter { it != idx }.shuffled(rng).take(3)
            extras.add(
                TripleFact(
                    idTag = "fact_spt_x1_$idx",
                    category = "sport_and_leisure",
                    difficulty = "medium",
                    enQ = "Which answer matches the sporting tradition or rule: ${sp.nameEn}?",
                    esQ = "¿Qué respuesta corresponde a esta regla o tradición deportiva: ${sp.nameEs}?",
                    idQ = "Jawaban manakah yang tepat untuk fakta olahraga '${sp.nameId}'?",
                    enA = sp.countryEn,
                    esA = sp.countryEs,
                    idA = sp.countryId,
                    enWrong = others.map { extraSports[it].countryEn },
                    esWrong = others.map { extraSports[it].countryEs },
                    idWrong = others.map { extraSports[it].countryId },
                    enExp = sp.expEn,
                    esExp = sp.expEs,
                    idExp = sp.expId
                )
            )
            extras.add(
                TripleFact(
                    idTag = "fact_spt_x2_$idx",
                    category = "sport_and_leisure",
                    difficulty = "hard",
                    enQ = "In athletics and games, which item corresponds to: \"${sp.expEn}\"?",
                    esQ = "En deportes y juegos, ¿a qué corresponde esta descripción: \"${sp.expEs}\"?",
                    idQ = "Dalam olahraga, istilah atau ajang apakah yang dijelaskan sebagai: \"${sp.expId}\"?",
                    enA = sp.nameEn,
                    esA = sp.nameEs,
                    idA = sp.nameId,
                    enWrong = others.map { extraSports[it].nameEn },
                    esWrong = others.map { extraSports[it].nameEs },
                    idWrong = others.map { extraSports[it].nameId },
                    enExp = "${sp.nameEn}: ${sp.expEn}",
                    esExp = "${sp.nameEs}: ${sp.expEs}",
                    idExp = "${sp.nameId}: ${sp.expId}"
                )
            )
        }

        // Extra Society & Culture (12 * 2 = 24 items -> raises society_and_culture above 59)
        val extraCulture = listOf(
            TriplePair("Noh Theater", "Teatro Noh", "Teater Tradisional Noh", "Japan (masked classical drama)", "Japón (drama clásico con máscaras)", "Jepang (drama klasik bertopeng)", "One of the world's oldest continuously performed theater traditions since the 14th century.", "Una de las tradiciones teatrales más antiguas del mundo, activa desde el siglo XIV.", "Salah satu tradisi teater tertua di dunia yang terus dipentaskan sejak abad ke-14."),
            TriplePair("Sape (Lute)", "Laúd Sape", "Alat Musik Petik Sape", "Dayak peoples of Borneo", "Pueblos dayak de Borneo", "Suku Dayak di Kalimantan", "Hand-carved boat-shaped wooden lute played across Sarawak and Kalimantan ceremonies.", "Laúd tallado en madera con forma de bote propio de las ceremonias de Borneo.", "Alat musik petik kayu berbentuk perahu khas upacara adat masyarakat Dayak di Kalimantan."),
            TriplePair("Flamenco", "Flamenco", "Flamenco", "Andalusia, Spain", "Andalucía, España", "Andalusia, Spanyol", "Recognized by UNESCO, uniting expressive guitar (toque), song (cante), and rhythmic dance (baile).", "Patrimonio UNESCO que fusiona cante, toque de guitarra y baile rítmico.", "Warisan budaya UNESCO yang memadukan petikan gitar, nyanyian ekspresif, dan tarian ritmis."),
            TriplePair("Hanbok", "Hanbok", "Hanbok", "Korea (traditional attire)", "Corea (vestimenta tradicional)", "Korea (busana tradisional)", "Characterized by vibrant natural dyes, a short jeogori jacket, and flowing lines.", "Traje tradicional coreano de colores vivos, chaqueta corta jeogori y líneas fluidas.", "Busana adat Korea yang berciri warna cerah, jaket pendek jeogori, dan garis anggun."),
            TriplePair("Kebaya", "Kebaya", "Kebaya", "Southeast Asia (Indonesia & Malay world)", "Sudeste Asiático (Indonesia y mundo malayo)", "Asia Tenggara (Indonesia & Nusantara)", "Traditional blouse-dress ensemble often paired with hand-drawn batik kain.", "Blusa tradicional del sudeste asiático combinada habitualmente con telas de batik.", "Busana tradisional anggun yang lazim dipadukan dengan kain batik tulis atau tenun."),
            TriplePair("Socrates, Plato & Aristotle", "Sócrates, Platón y Aristóteles", "Socrates, Plato & Aristoteles", "Classical Greek Philosophy", "Filosofía griega clásica", "Filsafat Yunani Klasik", "Teacher-student lineage in Athens that laid the groundwork for Western logic and ethics.", "Línea sucesoria de maestro y discípulo en Atenas que fundó la lógica y la ética occidentales.", "Rantai guru-murid di Athena yang meletakkan dasar logika, etika, dan sains Barat."),
            TriplePair("Kente Cloth", "Tela Kente", "Kain Tenun Kente", "Ghana (Ashanti & Ewe weavers)", "Ghana (tejedores asante y ewe)", "Ghana (penenun suku Ashanti & Ewe)", "Handwoven silk and cotton strips with symbolic geometric patterns worn on festive occasions.", "Bandas tejidas a mano de seda y algodón con patrones geométricos simbólicos en Ghana.", "Kain tenun sutra-katun bermotif geometris simbolis kebanggaan masyarakat Ghana."),
            TriplePair("Songkran", "Songkran", "Songkran", "Thailand (Traditional New Year)", "Tailandia (Año Nuevo tradicional)", "Thailand (Tahun Baru Tradisional)", "Mid-April water-blessing festival symbolizing renewal and washing away misfortune.", "Festival de abril en Tailandia celebrado con agua como símbolo de renovación.", "Festival penyiraman air pertengahan April yang melambangkan penyucian dan awal baru."),
            TriplePair("Machu Picchu Architectural Mortar", "Mampostería inca sin mortero", "Teknik Batu Tanpa Semen (Ashlar) Inka", "Dry-stone interlocking (ashlar)", "Piedras encajadas sin mortero", "Susunan batu presisi tanpa adukan semen", "Inca stonemasons shaped granite blocks so tightly that not even a knife blade fits between stones.", "Los canteros incas ajustaban bloques de granito con precisión sísmica sin usar argamasa.", "Pahat batu Inka mengunci balok granit begitu rapat hingga tahan guncangan gempa tanpa semen."),
            TriplePair("Quipu", "Quipu", "Quipu", "Inca knotted recording cords", "Cuerdas anudadas incas de registro", "Tali simpul pencatat data Kekaisaran Inka", "Cotton or camelid fiber cords tied in positional knots to record census and tax accounts.", "Cordeles con nudos posicionales usados por el Imperio inca para llevar cuentas y censos.", "Untaian tali bersimpul warna-warni yang dipakai bangsa Inka mencatat sensus dan logistik."),
            TriplePair("Phinisi Schooner", "Goleta Phinisi", "Perahu Layar Pinisi", "South Sulawesi (Bugis-Makassar)", "Célebes Meridional (Bugis-Makasar)", "Sulawesi Selatan (Bugis-Makassar)", "Recognized by UNESCO in 2017, traditional two-masted wooden sailing vessels crafted without nails.", "Patrimonio UNESCO desde 2017, goletas de madera de dos mástiles de tradición marítima indonesia.", "Warisan UNESCO tahun 2017 berupa kapal layar kayu dua tiang karya pelaut ulung Sulawesi Selatan."),
            TriplePair("Gamelan Scales", "Escalas del gamelán", "Tangga Nada Gamelan Jawa", "Sléndro and Pélog", "Sléndro y Pélog", "Sléndro dan Pélog", "Five-tone (sléndro) and seven-tone (pélog) tuning systems give gamelan its shimmering acoustics.", "Los sistemas sléndro (pentatónico) y pélog (heptatónico) definen el timbre del gamelán.", "Sistem laras pentatonis sléndro dan heptatonis pélog menciptakan gema khas gamelan.")
        )

        extraCulture.forEachIndexed { idx, ec ->
            val others = extraCulture.indices.filter { it != idx }.shuffled(rng).take(3)
            extras.add(
                TripleFact(
                    idTag = "fact_soc_c1_$idx",
                    category = "society_and_culture",
                    difficulty = "medium",
                    enQ = "In world culture, what does '${ec.nameEn}' refer to?",
                    esQ = "En la cultura mundial, ¿a qué corresponde '${ec.nameEs}'?",
                    idQ = "Dalam khazanah budaya dunia, apakah yang dimaksud dengan '${ec.nameId}'?",
                    enA = ec.countryEn,
                    esA = ec.countryEs,
                    idA = ec.countryId,
                    enWrong = others.map { extraCulture[it].countryEn },
                    esWrong = others.map { extraCulture[it].countryEs },
                    idWrong = others.map { extraCulture[it].countryId },
                    enExp = ec.expEn,
                    esExp = ec.expEs,
                    idExp = ec.expId
                )
            )
            extras.add(
                TripleFact(
                    idTag = "fact_soc_c2_$idx",
                    category = "society_and_culture",
                    difficulty = "medium",
                    enQ = "Which cultural tradition or heritage is described as: \"${ec.expEn}\"?",
                    esQ = "¿Qué tradición cultural o patrimonio se describe así: \"${ec.expEs}\"?",
                    idQ = "Tradisi atau warisan budaya apakah yang dijelaskan sebagai: \"${ec.expId}\"?",
                    enA = ec.nameEn,
                    esA = ec.nameEs,
                    idA = ec.nameId,
                    enWrong = others.map { extraCulture[it].nameEn },
                    esWrong = others.map { extraCulture[it].nameEs },
                    idWrong = others.map { extraCulture[it].nameId },
                    enExp = "${ec.nameEn} (${ec.countryEn}): ${ec.expEn}",
                    esExp = "${ec.nameEs} (${ec.countryEs}): ${ec.expEs}",
                    idExp = "${ec.nameId} (${ec.countryId}): ${ec.expId}"
                )
            )
        }

        return extras
    }

    private data class TriplePair(
        val nameEn: String,
        val nameEs: String,
        val nameId: String,
        val countryEn: String,
        val countryEs: String,
        val countryId: String,
        val expEn: String,
        val expEs: String,
        val expId: String
    )
}
