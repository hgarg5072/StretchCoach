package com.example.audio

import com.example.data.AsanaPronunciationStyle

/**
 * Structured speech plan separating authentic Devanagari Sanskrit (for Hindi/Devanagari TTS)
 * from English-TTS-safe phonetic respellings and English pose names.
 */
data class SpokenAsanaPlan(
    val sanskritDevanagari: String?,
    val sanskritPhonetic: String?,
    val englishName: String?,
    val sideSuffix: String
)

object SanskritPhonetics {

    /**
     * Known dictionary mapping Sanskrit asana names to authentic English poses.
     * Used when the user selects English-only mode or needs fallback translations.
     */
    private val SANSKRIT_TO_ENGLISH_MAP = mapOf(
        "bhujangasana" to "Cobra Pose",
        "bhujangasan" to "Cobra Pose",
        "balasana" to "Child's Pose",
        "balasan" to "Child's Pose",
        "tadasana" to "Mountain Pose",
        "tadasan" to "Mountain Pose",
        "vrikshasana" to "Tree Pose",
        "vrikshasan" to "Tree Pose",
        "trikonasana" to "Triangle Pose",
        "trikonasan" to "Triangle Pose",
        "parivrtta trikonasana" to "Revolved Triangle Pose",
        "virabhadrasana" to "Warrior Pose",
        "virabhadrasan" to "Warrior Pose",
        "virabhadrasana i" to "Warrior One Pose",
        "virabhadrasana ii" to "Warrior Two Pose",
        "virabhadrasana iii" to "Warrior Three Pose",
        "utkatasana" to "Chair Pose",
        "utkatasan" to "Chair Pose",
        "garudasana" to "Eagle Pose",
        "garudasan" to "Eagle Pose",
        "paschimottanasana" to "Seated Forward Bend",
        "paschimottanasan" to "Seated Forward Bend",
        "janu sirsasana" to "Head to Knee Pose",
        "janu sirsasan" to "Head to Knee Pose",
        "baddha konasana" to "Butterfly Pose",
        "baddha konasan" to "Butterfly Pose",
        "supta baddha konasana" to "Reclining Butterfly Pose",
        "malasana" to "Yogi Squat Pose",
        "malasan" to "Yogi Squat Pose",
        "gomukhasana" to "Cow Face Pose",
        "gomukhasan" to "Cow Face Pose",
        "setu bandhasana" to "Bridge Pose",
        "setu bandhasan" to "Bridge Pose",
        "setu bandha sarvangasana" to "Bridge Pose",
        "ustrasana" to "Camel Pose",
        "ustrasan" to "Camel Pose",
        "matsyasana" to "Fish Pose",
        "matsyasan" to "Fish Pose",
        "dhanurasana" to "Bow Pose",
        "dhanurasan" to "Bow Pose",
        "urdhva dhanurasana" to "Wheel Pose",
        "salabhasana" to "Locust Pose",
        "salabhasan" to "Locust Pose",
        "shalabhasana" to "Locust Pose",
        "halasana" to "Plow Pose",
        "halasan" to "Plow Pose",
        "viparita karani" to "Legs Up the Wall Pose",
        "kapotasana" to "Pigeon Pose",
        "kapotasan" to "Pigeon Pose",
        "eka pada rajakapotasana" to "One-Legged King Pigeon Pose",
        "marjaryasana" to "Cat Pose",
        "marjaryasan" to "Cat Pose",
        "bitilasana" to "Cow Pose",
        "bitilasan" to "Cow Pose",
        "ardha matsyendrasana" to "Half Lord of the Fishes Pose",
        "supta matsyendrasana" to "Supine Spinal Twist",
        "anjaneyasana" to "Low Lunge Pose",
        "anjaneyasan" to "Low Lunge Pose",
        "ananda balasana" to "Happy Baby Pose",
        "shavasana" to "Corpse Rest Pose",
        "savasana" to "Corpse Rest Pose",
        "shavasan" to "Corpse Rest Pose",
        "savasan" to "Corpse Rest Pose",
        "adho mukha svanasana" to "Downward Facing Dog",
        "urdhva mukha svanasana" to "Upward Facing Dog",
        "uttanasana" to "Standing Forward Fold",
        "uttanasan" to "Standing Forward Fold",
        "ardha uttanasana" to "Half Forward Fold",
        "prasarita padottanasana" to "Wide-Leg Forward Fold",
        "parsvottanasana" to "Pyramid Pose",
        "parshvottanasana" to "Pyramid Pose",
        "supta padangusthasana" to "Reclining Leg Stretch",
        "utkata konasana" to "Goddess Pose",
        "padmasana" to "Lotus Pose",
        "padmasan" to "Lotus Pose",
        "vajrasana" to "Thunderbolt Pose",
        "vajrasan" to "Thunderbolt Pose",
        "sukhasana" to "Easy Seated Pose",
        "sukhasan" to "Easy Seated Pose",
        "chakrasana" to "Wheel Pose",
        "chakrasan" to "Wheel Pose",
        "sarvangasana" to "Shoulder Stand",
        "sarvangasan" to "Shoulder Stand",
        "sirsasana" to "Headstand",
        "shirshasana" to "Headstand",
        "naukasana" to "Boat Pose",
        "navasana" to "Boat Pose",
        "surya namaskar" to "Sun Salutation",
        "surya namaskara" to "Sun Salutation",
        "pavanamuktasana" to "Wind Relieving Pose",
        "pawanmuktasana" to "Wind Relieving Pose",
        "makarasana" to "Crocodile Pose",
        "bakasana" to "Crow Pose",
        "mayurasana" to "Peacock Pose",
        "chaturanga dandasana" to "Four-Limbed Staff Pose",
        "dandasana" to "Staff Pose",
        "ardha chandrasana" to "Half Moon Pose",
        "utthita parsvakonasana" to "Extended Side Angle Pose",
        "hanumanasana" to "Monkey Split Pose",
        "natarajasana" to "Dancer Pose",
        "virasana" to "Hero Pose",
        "supta virasana" to "Reclining Hero Pose",
        "mandukasana" to "Frog Pose",
        "simhasana" to "Lion Pose",
        "kurmasana" to "Tortoise Pose",
        "shashankasana" to "Hare Pose",
        "siddhasana" to "Accomplished Pose",
        "padahastasana" to "Hand to Foot Pose",
        "vasisthasana" to "Side Plank Pose",
        "skandasana" to "Side Lunge Pose"
    )

    /**
     * Curated Devanagari script dictionary for authentic Sanskrit pronunciation
     * when Android Hindi (hi-IN) TTS voice is available.
     * Ordered longest/most-specific first so compound asanas match before roots.
     */
    private val SANSKRIT_TO_DEVANAGARI_MAP = listOf(
        Regex("(?i)\\b(eka\\s+pada\\s+rajakapotasana)\\b") to "एक पाद राजकपोतासन",
        Regex("(?i)\\b(adho\\s+mukha\\s+svanasana)\\b") to "अधो मुख श्वानासन",
        Regex("(?i)\\b(urdhva\\s+mukha\\s+svanasana)\\b") to "ऊर्ध्व मुख श्वानासन",
        Regex("(?i)\\b(supta\\s+baddha\\s+konasana)\\b") to "सुप्त बद्ध कोणासन",
        Regex("(?i)\\b(setu\\s+bandha\\s+sarvangasana)\\b") to "सेतु बंध सर्वांगासन",
        Regex("(?i)\\b(prasarita\\s+padottanasana)\\b") to "प्रसारित पादोत्तानासन",
        Regex("(?i)\\b(utthita\\s+parsvakonasana)\\b") to "उत्थित पार्श्वकोणासन",
        Regex("(?i)\\b(parivrtta\\s+trikonasana)\\b") to "परिवृत्त त्रिकोणासन",
        Regex("(?i)\\b(supta\\s+padangusthasana)\\b") to "सुप्त पादांगुष्ठासन",
        Regex("(?i)\\b(ardha\\s+matsyendrasana)\\b") to "अर्ध मत्स्येन्द्रासन",
        Regex("(?i)\\b(supta\\s+matsyendrasana)\\b") to "सुप्त मत्स्येन्द्रासन",
        Regex("(?i)\\b(chaturanga\\s+dandasana)\\b") to "चतुरंग दंडासन",
        Regex("(?i)\\b(virabhadrasana\\s+iii|virabhadrasan\\s+iii|virabhadrasana\\s+3)\\b") to "वीरभद्रासन तीन",
        Regex("(?i)\\b(virabhadrasana\\s+ii|virabhadrasan\\s+ii|virabhadrasana\\s+2)\\b") to "वीरभद्रासन दो",
        Regex("(?i)\\b(virabhadrasana\\s+i|virabhadrasan\\s+i|virabhadrasana\\s+1)\\b") to "वीरभद्रासन एक",
        Regex("(?i)\\b(virabhadrasana|virabhadrasan)\\b") to "वीरभद्रासन",
        Regex("(?i)\\b(urdhva\\s+dhanurasana)\\b") to "ऊर्ध्व धनुरासन",
        Regex("(?i)\\b(setu\\s+bandhasana|setu\\s+bandhasan)\\b") to "सेतु बंधासन",
        Regex("(?i)\\b(baddha\\s+konasana|baddha\\s+konasan)\\b") to "बद्ध कोणासन",
        Regex("(?i)\\b(utkata\\s+konasana)\\b") to "उत्कट कोणासन",
        Regex("(?i)\\b(ananda\\s+balasana)\\b") to "आनंद बालासन",
        Regex("(?i)\\b(janu\\s+sirsasana|janu\\s+sirsasan)\\b") to "जानु शीर्षासन",
        Regex("(?i)\\b(viparita\\s+karani)\\b") to "विपरीत करणी",
        Regex("(?i)\\b(ardha\\s+chandrasana)\\b") to "अर्ध चन्द्रासन",
        Regex("(?i)\\b(ardha\\s+uttanasana)\\b") to "अर्ध उत्तानासन",
        Regex("(?i)\\b(supta\\s+virasana)\\b") to "सुप्त वीरासन",
        Regex("(?i)\\b(surya\\s+namaskara|surya\\s+namaskar)\\b") to "सूर्य नमस्कार",
        Regex("(?i)\\b(paschimottanasana|paschimottanasan)\\b") to "पश्चिमोत्तानासन",
        Regex("(?i)\\b(pavanamuktasana|pawanmuktasana)\\b") to "पवनमुक्तासन",
        Regex("(?i)\\b(parsvottanasana|parshvottanasana)\\b") to "पार्श्वोत्तानासन",
        Regex("(?i)\\b(bhujang\\s*asana|bhujangasana|bhujang\\s*asan|bhujangasan)\\b") to "भुजंगासन",
        Regex("(?i)\\b(sarvangasana|sarvangasan)\\b") to "सर्वांगासन",
        Regex("(?i)\\b(shashankasana|shashankasan)\\b") to "शशांकासन",
        Regex("(?i)\\b(padahastasana|padahastasan)\\b") to "पादहस्तासन",
        Regex("(?i)\\b(anjaneyasana|anjaneyasan)\\b") to "आञ्जनेयासन",
        Regex("(?i)\\b(marjaryasana|marjaryasan)\\b") to "मार्जारी आसन",
        Regex("(?i)\\b(natarajasana|natarajasan)\\b") to "नटराजासन",
        Regex("(?i)\\b(vasisthasana|vasisthasan)\\b") to "वसिष्ठासन",
        Regex("(?i)\\b(hanumanasana|hanumanasan)\\b") to "हनुमानासन",
        Regex("(?i)\\b(trikonasana|trikonasan)\\b") to "त्रिकोणासन",
        Regex("(?i)\\b(vrikshasana|vrikshasan)\\b") to "वृक्षासन",
        Regex("(?i)\\b(gomukhasana|gomukhasan)\\b") to "गोमुखासन",
        Regex("(?i)\\b(salabhasana|salabhasan|shalabhasana)\\b") to "शलभासन",
        Regex("(?i)\\b(mandukasana|mandukasan)\\b") to "मंडूकासन",
        Regex("(?i)\\b(siddhasana|siddhasan)\\b") to "सिद्धासन",
        Regex("(?i)\\b(skandasana|skandasan)\\b") to "स्कंदासन",
        Regex("(?i)\\b(utkatasana|utkatasan)\\b") to "उत्कटासन",
        Regex("(?i)\\b(dhanurasana|dhanurasan)\\b") to "धनुरासन",
        Regex("(?i)\\b(kapotasana|kapotasan)\\b") to "कपोतासन",
        Regex("(?i)\\b(bitilasana|bitilasan)\\b") to "बिटिलासन",
        Regex("(?i)\\b(uttanasana|uttanasan)\\b") to "उत्तानासन",
        Regex("(?i)\\b(matsyasana|matsyasan)\\b") to "मत्स्यासन",
        Regex("(?i)\\b(garudasana|garudasan)\\b") to "गरुड़ासन",
        Regex("(?i)\\b(chakrasana|chakrasan)\\b") to "चक्रासन",
        Regex("(?i)\\b(sirsasana|shirshasana|sirsasan)\\b") to "शीर्षासन",
        Regex("(?i)\\b(mayurasana|mayurasan)\\b") to "मयूरासन",
        Regex("(?i)\\b(makarasana|makarasan)\\b") to "मकरासन",
        Regex("(?i)\\b(naukasana|naukasan)\\b") to "नौकासन",
        Regex("(?i)\\b(navasana|navasan)\\b") to "नावासन",
        Regex("(?i)\\b(simhasana|simhasan)\\b") to "सिंहासन",
        Regex("(?i)\\b(kurmasana|kurmasan)\\b") to "कूर्मासन",
        Regex("(?i)\\b(vajrasana|vajrasan)\\b") to "वज्रासन",
        Regex("(?i)\\b(padmasana|padmasan)\\b") to "पद्मासन",
        Regex("(?i)\\b(sukhasana|sukhasan)\\b") to "सुखासन",
        Regex("(?i)\\b(dandasana|dandasan)\\b") to "दंडासन",
        Regex("(?i)\\b(ustrasana|ustrasan)\\b") to "उष्ट्रासन",
        Regex("(?i)\\b(shavasana|savasana|shavasan|savasan)\\b") to "शवासन",
        Regex("(?i)\\b(balasana|balasan)\\b") to "बालासन",
        Regex("(?i)\\b(tadasana|tadasan)\\b") to "ताड़ासन",
        Regex("(?i)\\b(malasana|malasan)\\b") to "मालासन",
        Regex("(?i)\\b(halasana|halasan)\\b") to "हलासन",
        Regex("(?i)\\b(bakasana|bakasan)\\b") to "बकासन",
        Regex("(?i)\\b(virasana|virasan)\\b") to "वीरासन"
    )

    /**
     * English-TTS-safe phonetic respellings using standard English vowel conventions
     * ("ah" for /ɑː/, "uh" for /ə/, "oo" for /uː/, "ee" for /iː/, "oh" for /oː/, "ay" for /eː/)
     * so English (US/UK/IN) TTS engines pronounce Sanskrit smoothly without saying
     * "AY-san" or "gas-ana".
     */
    private val PHONETIC_RESPELLING_MAP = listOf(
        Regex("(?i)\\b(eka\\s+pada\\s+rajakapotasana)\\b") to "Ay-kah Pah-dah Rah-jah Kah-poh-tah-suh-nuh",
        Regex("(?i)\\b(adho\\s+mukha\\s+svanasana)\\b") to "Ah-doh Moo-kah Shvah-nah-suh-nuh",
        Regex("(?i)\\b(urdhva\\s+mukha\\s+svanasana)\\b") to "Oord-vah Moo-kah Shvah-nah-suh-nuh",
        Regex("(?i)\\b(supta\\s+baddha\\s+konasana)\\b") to "Soop-tah Bud-dhah Koh-nah-suh-nuh",
        Regex("(?i)\\b(setu\\s+bandha\\s+sarvangasana)\\b") to "Say-too Bun-dah Sar-vahn-gah-suh-nuh",
        Regex("(?i)\\b(prasarita\\s+padottanasana)\\b") to "Prah-sah-ree-tah Pah-doh-tah-nah-suh-nuh",
        Regex("(?i)\\b(utthita\\s+parsvakonasana)\\b") to "Oot-tee-tah Parsh-vah Koh-nah-suh-nuh",
        Regex("(?i)\\b(parivrtta\\s+trikonasana)\\b") to "Pah-ree-vrit-tah Tree-koh-nah-suh-nuh",
        Regex("(?i)\\b(supta\\s+padangusthasana)\\b") to "Soop-tah Pah-dahn-goosh-tah-suh-nuh",
        Regex("(?i)\\b(ardha\\s+matsyendrasana)\\b") to "Ar-dah Mut-syen-drah-suh-nuh",
        Regex("(?i)\\b(supta\\s+matsyendrasana)\\b") to "Soop-tah Mut-syen-drah-suh-nuh",
        Regex("(?i)\\b(chaturanga\\s+dandasana)\\b") to "Chuh-too-rung-gah Dun-dah-suh-nuh",
        Regex("(?i)\\b(virabhadrasana\\s+iii|virabhadrasan\\s+iii|virabhadrasana\\s+3)\\b") to "Vee-rah-bhud-rah-suh-nuh Three",
        Regex("(?i)\\b(virabhadrasana\\s+ii|virabhadrasan\\s+ii|virabhadrasana\\s+2)\\b") to "Vee-rah-bhud-rah-suh-nuh Two",
        Regex("(?i)\\b(virabhadrasana\\s+i|virabhadrasan\\s+i|virabhadrasana\\s+1)\\b") to "Vee-rah-bhud-rah-suh-nuh One",
        Regex("(?i)\\b(virabhadrasana|virabhadrasan)\\b") to "Vee-rah-bhud-rah-suh-nuh",
        Regex("(?i)\\b(urdhva\\s+dhanurasana)\\b") to "Oord-vah Dhuh-noo-rah-suh-nuh",
        Regex("(?i)\\b(setu\\s+bandhasana|setu\\s+bandhasan)\\b") to "Say-too Bun-dah-suh-nuh",
        Regex("(?i)\\b(baddha\\s+konasana|baddha\\s+konasan)\\b") to "Bud-dhah Koh-nah-suh-nuh",
        Regex("(?i)\\b(utkata\\s+konasana)\\b") to "Oot-kah-tah Koh-nah-suh-nuh",
        Regex("(?i)\\b(ananda\\s+balasana)\\b") to "Ah-nun-dah Bah-lah-suh-nuh",
        Regex("(?i)\\b(janu\\s+sirsasana|janu\\s+sirsasan)\\b") to "Jah-noo Sheer-shah-suh-nuh",
        Regex("(?i)\\b(viparita\\s+karani)\\b") to "Vee-pah-ree-tah Kah-rah-nee",
        Regex("(?i)\\b(ardha\\s+chandrasana)\\b") to "Ar-dah Chun-drah-suh-nuh",
        Regex("(?i)\\b(ardha\\s+uttanasana)\\b") to "Ar-dah Oot-tah-nah-suh-nuh",
        Regex("(?i)\\b(supta\\s+virasana)\\b") to "Soop-tah Vee-rah-suh-nuh",
        Regex("(?i)\\b(surya\\s+namaskara|surya\\s+namaskar)\\b") to "Soor-yah Nah-mus-kar",
        Regex("(?i)\\b(paschimottanasana|paschimottanasan)\\b") to "Push-chee-moh-tah-nah-suh-nuh",
        Regex("(?i)\\b(pavanamuktasana|pawanmuktasana)\\b") to "Puh-vuh-nuh Mook-tah-suh-nuh",
        Regex("(?i)\\b(parsvottanasana|parshvottanasana)\\b") to "Parsh-voh-tah-nah-suh-nuh",
        Regex("(?i)\\b(bhujang\\s*asana|bhujangasana|bhujang\\s*asan|bhujangasan)\\b") to "Bhoo-jung-ah-suh-nuh",
        Regex("(?i)\\b(sarvangasana|sarvangasan)\\b") to "Sar-vahn-gah-suh-nuh",
        Regex("(?i)\\b(shashankasana|shashankasan)\\b") to "Shuh-shahn-kah-suh-nuh",
        Regex("(?i)\\b(padahastasana|padahastasan)\\b") to "Pah-dah Hus-tah-suh-nuh",
        Regex("(?i)\\b(anjaneyasana|anjaneyasan)\\b") to "Ahn-jah-nay-ah-suh-nuh",
        Regex("(?i)\\b(marjaryasana|marjaryasan)\\b") to "Mar-jar-yah-suh-nuh",
        Regex("(?i)\\b(natarajasana|natarajasan)\\b") to "Nuh-tuh-rah-jah-suh-nuh",
        Regex("(?i)\\b(vasisthasana|vasisthasan)\\b") to "Vuh-sish-tah-suh-nuh",
        Regex("(?i)\\b(hanumanasana|hanumanasan)\\b") to "Huh-noo-mah-nah-suh-nuh",
        Regex("(?i)\\b(trikonasana|trikonasan)\\b") to "Tree-koh-nah-suh-nuh",
        Regex("(?i)\\b(vrikshasana|vrikshasan)\\b") to "Vrik-shah-suh-nuh",
        Regex("(?i)\\b(gomukhasana|gomukhasan)\\b") to "Goh-moo-kah-suh-nuh",
        Regex("(?i)\\b(salabhasana|salabhasan|shalabhasana)\\b") to "Shuh-luh-bah-suh-nuh",
        Regex("(?i)\\b(mandukasana|mandukasan)\\b") to "Mun-doo-kah-suh-nuh",
        Regex("(?i)\\b(siddhasana|siddhasan)\\b") to "Sid-dhah-suh-nuh",
        Regex("(?i)\\b(skandasana|skandasan)\\b") to "Skun-dah-suh-nuh",
        Regex("(?i)\\b(utkatasana|utkatasan)\\b") to "Oot-kah-tah-suh-nuh",
        Regex("(?i)\\b(dhanurasana|dhanurasan)\\b") to "Dhuh-noo-rah-suh-nuh",
        Regex("(?i)\\b(kapotasana|kapotasan)\\b") to "Kah-poh-tah-suh-nuh",
        Regex("(?i)\\b(bitilasana|bitilasan)\\b") to "Bee-tee-lah-suh-nuh",
        Regex("(?i)\\b(uttanasana|uttanasan)\\b") to "Oot-tah-nah-suh-nuh",
        Regex("(?i)\\b(matsyasana|matsyasan)\\b") to "Mut-syah-suh-nuh",
        Regex("(?i)\\b(garudasana|garudasan)\\b") to "Gah-roo-dah-suh-nuh",
        Regex("(?i)\\b(chakrasana|chakrasan)\\b") to "Chuk-rah-suh-nuh",
        Regex("(?i)\\b(sirsasana|shirshasana|sirsasan)\\b") to "Sheer-shah-suh-nuh",
        Regex("(?i)\\b(mayurasana|mayurasan)\\b") to "Muh-yoo-rah-suh-nuh",
        Regex("(?i)\\b(makarasana|makarasan)\\b") to "Muh-kah-rah-suh-nuh",
        Regex("(?i)\\b(naukasana|naukasan)\\b") to "Now-kah-suh-nuh",
        Regex("(?i)\\b(navasana|navasan)\\b") to "Nah-vah-suh-nuh",
        Regex("(?i)\\b(simhasana|simhasan)\\b") to "Sim-hah-suh-nuh",
        Regex("(?i)\\b(kurmasana|kurmasan)\\b") to "Koor-mah-suh-nuh",
        Regex("(?i)\\b(vajrasana|vajrasan)\\b") to "Vuj-rah-suh-nuh",
        Regex("(?i)\\b(padmasana|padmasan)\\b") to "Pud-mah-suh-nuh",
        Regex("(?i)\\b(sukhasana|sukhasan)\\b") to "Soo-kah-suh-nuh",
        Regex("(?i)\\b(dandasana|dandasan)\\b") to "Dun-dah-suh-nuh",
        Regex("(?i)\\b(ustrasana|ustrasan)\\b") to "Oosh-trah-suh-nuh",
        Regex("(?i)\\b(shavasana|savasana|shavasan|savasan)\\b") to "Shuh-vah-suh-nuh",
        Regex("(?i)\\b(balasana|balasan)\\b") to "Bah-lah-suh-nuh",
        Regex("(?i)\\b(tadasana|tadasan)\\b") to "Tah-dah-suh-nuh",
        Regex("(?i)\\b(malasana|malasan)\\b") to "Mah-lah-suh-nuh",
        Regex("(?i)\\b(halasana|halasan)\\b") to "Huh-lah-suh-nuh",
        Regex("(?i)\\b(bakasana|bakasan)\\b") to "Buh-kah-suh-nuh",
        Regex("(?i)\\b(virasana|virasan)\\b") to "Vee-rah-suh-nuh"
    )

    private fun isSanskritTerm(text: String): Boolean {
        val lower = text.lowercase().trim()
        if (lower.isBlank()) return false
        // Already Devanagari script
        if (lower.any { it in '\u0900'..'\u097F' }) return true
        if (lower.endsWith("asana") || lower.endsWith("asan") ||
            lower.contains("asana ") || lower.contains("asan ") ||
            lower.contains("karani") || lower.contains("namaskar") ||
            lower.contains("pranayama") || lower.contains("mudra") ||
            lower.contains("bandha")
        ) {
            return true
        }
        return SANSKRIT_TO_ENGLISH_MAP.keys.any { lower.contains(it) }
    }

    /**
     * Converts a Sanskrit term to Devanagari script using the curated dictionary first,
     * falling back to a rule-based Latin-to-Devanagari transliterator for custom asanas.
     */
    fun toDevanagari(sanskritTerm: String): String {
        val trimmed = sanskritTerm.trim()
        if (trimmed.isBlank()) return ""
        if (trimmed.any { it in '\u0900'..'\u097F' }) return trimmed

        var result = trimmed
        var matchedCurated = false
        for ((regex, devanagari) in SANSKRIT_TO_DEVANAGARI_MAP) {
            if (regex.containsMatchIn(result)) {
                result = regex.replace(result, devanagari)
                matchedCurated = true
            }
        }
        if (matchedCurated && result.none { it in 'a'..'z' || it in 'A'..'Z' }) {
            return result
        }

        // Transliterate any remaining Latin words into Devanagari
        return result.split(Regex("\\s+")).joinToString(" ") { word ->
            if (word.any { it in '\u0900'..'\u097F' }) {
                word
            } else {
                transliterateWordToDevanagari(word)
            }
        }
    }

    /**
     * Converts a Sanskrit term to English-TTS-safe phonetic syllables using the curated map
     * plus a rule-based fallback for custom/unknown asanas ending in -asana/-asan.
     */
    fun toEnglishPhonetic(sanskritTerm: String): String {
        val trimmed = sanskritTerm.trim()
        if (trimmed.isBlank()) return ""

        var result = trimmed
        for ((regex, phonetic) in PHONETIC_RESPELLING_MAP) {
            if (regex.containsMatchIn(result)) {
                result = regex.replace(result, phonetic)
            }
        }
        // Fallback for any custom Sanskrit asana not in the curated dictionary
        result = result.replace(Regex("(?i)\\b([a-z]{3,})(asana)\\b")) { m ->
            "${m.groupValues[1]}-ah-suh-nuh"
        }
        result = result.replace(Regex("(?i)\\b([a-z]{3,})(asan)\\b")) { m ->
            "${m.groupValues[1]}-ah-sun"
        }
        return result
    }

    /**
     * Builds a structured plan separating Devanagari Sanskrit, English-safe phonetics,
     * English pose name, and side direction so SpeechController can route Sanskrit
     * to a Hindi/Devanagari voice and English to an English voice.
     */
    fun buildSpokenPlan(rawName: String): SpokenAsanaPlan {
        val trimmed = rawName.trim()

        var sideSuffix = ""
        var cleaned = trimmed
        val sidePatterns = listOf(
            Regex("(?i)[-\\s]*\\b(right|right side|right leg|right shin forward|right forward|right leg back)\\b") to "right side",
            Regex("(?i)[-\\s]*\\b(left|left side|left leg|left shin forward|left forward|left leg back)\\b") to "left side"
        )

        val rightBrackets = listOf("(Right)", "(Right Leg)", "(Right Shin Forward)", "(Right Forward)", "(Right Leg Back)", "(Right Side)")
        val leftBrackets = listOf("(Left)", "(Left Leg)", "(Left Shin Forward)", "(Left Forward)", "(Left Leg Back)", "(Left Side)")

        if (rightBrackets.any { cleaned.contains(it, ignoreCase = true) }) {
            sideSuffix = "right side"
            rightBrackets.forEach { cleaned = cleaned.replace(it, "", ignoreCase = true) }
            cleaned = cleaned.trim()
        } else if (leftBrackets.any { cleaned.contains(it, ignoreCase = true) }) {
            sideSuffix = "left side"
            leftBrackets.forEach { cleaned = cleaned.replace(it, "", ignoreCase = true) }
            cleaned = cleaned.trim()
        }

        val bracketMatch = Regex("(.*?)\\((.*?)\\)").find(cleaned)
        var outerPart = bracketMatch?.groupValues?.get(1)?.trim() ?: cleaned
        var innerPart = bracketMatch?.groupValues?.get(2)?.trim() ?: ""

        if (sideSuffix.isEmpty()) {
            for ((regex, suffix) in sidePatterns) {
                if (innerPart.contains(regex)) {
                    sideSuffix = suffix
                    innerPart = innerPart.replace(regex, "").trim()
                    break
                }
                if (outerPart.contains(regex)) {
                    sideSuffix = suffix
                    outerPart = outerPart.replace(regex, "").trim()
                    break
                }
            }
        } else {
            for ((regex, _) in sidePatterns) {
                innerPart = innerPart.replace(regex, "").trim()
                outerPart = outerPart.replace(regex, "").trim()
            }
        }

        val sanskritCandidate: String
        val englishCandidate: String

        if (innerPart.isNotBlank() && isSanskritTerm(innerPart)) {
            sanskritCandidate = innerPart
            englishCandidate = outerPart
        } else if (isSanskritTerm(outerPart)) {
            sanskritCandidate = outerPart
            englishCandidate = innerPart
        } else {
            sanskritCandidate = ""
            englishCandidate = if (innerPart.isNotBlank()) "$outerPart, $innerPart" else outerPart
        }

        val resolvedEnglish = if (englishCandidate.isNotBlank()) {
            sanitizePunctuation(englishCandidate)
        } else if (sanskritCandidate.isNotBlank()) {
            val cleanKey = sanskritCandidate.lowercase().replace("-", " ").trim()
            SANSKRIT_TO_ENGLISH_MAP[cleanKey]
                ?: SANSKRIT_TO_ENGLISH_MAP.entries.firstOrNull { cleanKey.contains(it.key) }?.value
        } else {
            null
        }

        return SpokenAsanaPlan(
            sanskritDevanagari = if (sanskritCandidate.isNotBlank()) toDevanagari(sanskritCandidate) else null,
            sanskritPhonetic = if (sanskritCandidate.isNotBlank()) sanitizePunctuation(toEnglishPhonetic(sanskritCandidate)) else null,
            englishName = resolvedEnglish?.takeIf { it.isNotBlank() },
            sideSuffix = sideSuffix
        )
    }

    /**
     * Converts a stretch or asana title into a single spoken string based on the selected style
     * and whether a Devanagari/Hindi voice is available on the phone.
     */
    fun formatForSpeech(
        rawName: String,
        style: AsanaPronunciationStyle = AsanaPronunciationStyle.BILINGUAL,
        isIndianVoiceInstalled: Boolean = false
    ): String {
        val plan = buildSpokenPlan(rawName)
        val sidePart = if (plan.sideSuffix.isNotBlank()) ", ${plan.sideSuffix}" else ""

        return when (style) {
            AsanaPronunciationStyle.ENGLISH_ONLY -> {
                val base = plan.englishName ?: plan.sanskritPhonetic ?: rawName
                sanitizePunctuation("$base$sidePart")
            }

            AsanaPronunciationStyle.PHONETIC_SANSKRIT -> {
                val base = plan.sanskritPhonetic ?: plan.englishName ?: rawName
                sanitizePunctuation("$base$sidePart")
            }

            AsanaPronunciationStyle.SANSKRIT_ONLY -> {
                val base = when {
                    plan.sanskritDevanagari != null && isIndianVoiceInstalled -> plan.sanskritDevanagari
                    plan.sanskritPhonetic != null -> plan.sanskritPhonetic
                    else -> plan.englishName ?: rawName
                }
                sanitizePunctuation("$base$sidePart")
            }

            AsanaPronunciationStyle.BILINGUAL -> {
                val sanskritPart = when {
                    plan.sanskritDevanagari != null && isIndianVoiceInstalled -> plan.sanskritDevanagari
                    plan.sanskritPhonetic != null -> plan.sanskritPhonetic
                    else -> null
                }
                val englishPart = plan.englishName

                when {
                    sanskritPart != null && !englishPart.isNullOrBlank() && !sanskritPart.equals(englishPart, ignoreCase = true) ->
                        sanitizePunctuation("$sanskritPart, $englishPart$sidePart")
                    sanskritPart != null ->
                        sanitizePunctuation("$sanskritPart$sidePart")
                    !englishPart.isNullOrBlank() ->
                        sanitizePunctuation("$englishPart$sidePart")
                    else ->
                        sanitizePunctuation("$rawName$sidePart")
                }
            }
        }
    }

    /**
     * Rule-based Latin-to-Devanagari transliterator for custom Sanskrit asanas
     * not present in the curated dictionary.
     */
    private fun transliterateWordToDevanagari(rawWord: String): String {
        val word = rawWord.lowercase().replace(Regex("[^a-z]"), "")
        if (word.isBlank()) return rawWord

        // Special handling for trailing -asana / -asan
        val (stem, suffix) = when {
            word.length > 5 && word.endsWith("asana") -> word.dropLast(5) to "आसन"
            word.length > 4 && word.endsWith("asan") -> word.dropLast(4) to "आसन"
            else -> word to ""
        }

        val consonants = linkedMapOf(
            "ksh" to "क्ष", "kh" to "ख", "gh" to "घ", "ch" to "च", "jh" to "झ",
            "th" to "थ", "dh" to "ध", "ph" to "फ", "bh" to "भ", "sh" to "श",
            "k" to "क", "g" to "ग", "c" to "च", "j" to "ज", "t" to "त", "d" to "द",
            "n" to "न", "p" to "प", "f" to "फ", "b" to "ब", "m" to "म", "y" to "य",
            "r" to "र", "l" to "ल", "v" to "व", "w" to "व", "s" to "स", "h" to "ह"
        )
        val independentVowels = linkedMapOf(
            "aa" to "आ", "ee" to "ई", "oo" to "ऊ", "ai" to "ऐ", "au" to "औ",
            "a" to "अ", "i" to "इ", "u" to "उ", "e" to "ए", "o" to "ओ"
        )
        val matras = linkedMapOf(
            "aa" to "ा", "ee" to "ी", "oo" to "ू", "ai" to "ै", "au" to "ौ",
            "a" to "", "i" to "ि", "u" to "ु", "e" to "े", "o" to "ो"
        )

        val sb = StringBuilder()
        var i = 0
        var lastWasConsonant = false

        while (i < stem.length) {
            val remaining = stem.substring(i)
            val matchedConsonant = consonants.entries.firstOrNull { remaining.startsWith(it.key) }
            if (matchedConsonant != null) {
                if (lastWasConsonant) {
                    sb.append("्")
                }
                sb.append(matchedConsonant.value)
                i += matchedConsonant.key.length
                lastWasConsonant = true
            } else {
                val vowelMap = if (lastWasConsonant) matras else independentVowels
                val matchedVowel = vowelMap.entries.firstOrNull { remaining.startsWith(it.key) }
                if (matchedVowel != null) {
                    sb.append(matchedVowel.value)
                    i += matchedVowel.key.length
                    lastWasConsonant = false
                } else {
                    i++
                    lastWasConsonant = false
                }
            }
        }

        if (suffix.isNotEmpty()) {
            if (lastWasConsonant) {
                // Merge consonant + आसन -> consonant + ासन (e.g. क + आसन -> कासन)
                sb.append("ासन")
            } else {
                sb.append(suffix)
            }
        }
        return sb.toString()
    }

    private fun sanitizePunctuation(text: String): String {
        return text
            .replace("(", ", ")
            .replace(")", "")
            .replace(Regex("\\s+-\\s+"), ", ")
            .replace(Regex(",\\s*,"), ",")
            .replace(Regex("\\s+"), " ")
            .trim()
            .trimEnd(',')
            .trim()
    }
}
