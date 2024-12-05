package com.cs407.lingua


object DataLoader {

    data class QInfo(val fragmentID: String, val question: String, val answer: String,
                     val options: Array<String>)

    fun simplePhonetics(): QInfo {

        val choice = (1..6).random()
        when (choice) {
            1 -> { // hardcoded questions
                val questions = StoredData.getSimHardcodedQuestions()
                val q = questions.indices.random()

                val num = (1..2).random()
                when(num) {
                    1 -> {
                        return QInfo("fillBlank", questions[q][0], questions[q][1], emptyArray<String>())
                    }
                    2 -> {
                        val options = questions[q][2].split("_").toTypedArray()
                        return QInfo("mc4", questions[q][0], questions[q][1], options)
                    }
                    else -> return QInfo("", "choice 1 error", "choice 1 error", emptyArray<String>())
                }
            }

            2 -> { // Which phone is [feature]?
                val data = StoredData.getIpaByFeature()
                var questionString = "Which phone is "
                var answer = "error"
                var incorrectOption = "error"

                val q = (0..14).random()
                if(q <= 4) { // manner
                    questionString += "a " + data[q][0] + "?"
                    // choose symbol from correct category
                    answer = data[q][(1..< data[q].size).random()]
                    // choose a wrong category, choose symbol
                    val wrongCategories = ArrayList<Int>()
                    wrongCategories.add(0)
                    wrongCategories.add(1)
                    wrongCategories.add(2)
                    wrongCategories.add(3)
                    wrongCategories.add(4)
                    wrongCategories.remove(q)
                    val wrongCatIndex = wrongCategories[(0..3).random()]
                    incorrectOption = data[wrongCatIndex][(1..< data[wrongCatIndex].size).random()]
                }
                else if(q <= 12) { // place
                    questionString += data[q][0] + "?"
                    // choose symbol from correct category
                    answer = data[q][(1..< data[q].size).random()]
                    // choose a wrong category, choose symbol
                    val wrongCategories = ArrayList<Int>()
                    wrongCategories.add(5)
                    wrongCategories.add(6)
                    wrongCategories.add(7)
                    wrongCategories.add(8)
                    wrongCategories.add(9)
                    wrongCategories.add(10)
                    wrongCategories.add(11)
                    wrongCategories.add(12)
                    wrongCategories.remove(q)
                    val wrongCatIndex = wrongCategories[(0..6).random()]
                    incorrectOption = data[wrongCatIndex][(1..< data[wrongCatIndex].size).random()]
                }
                else { // voicing
                    questionString += data[q][0] + "?"
                    // choose symbol from correct category
                    answer = data[q][(1..< data[q].size).random()]
                    // choose a wrong category, choose symbol
                    incorrectOption = if(q == 13) {
                        data[14][(1..< data[14].size).random()]
                    } else {
                        data[13][(1..< data[13].size).random()]
                    }
                }

                return QInfo("mc2", questionString, answer, arrayOf(incorrectOption))
            }

            3 -> { // questions using IPA symbol info

                var data = emptyArray<Array<String>>()
                var consonant = false

                var num = (1..2).random()
                when (num) {
                    1 -> data = StoredData.getIpaVowels()
                    2 -> {
                        data = StoredData.getIpaConsonants()
                        consonant = true
                    }
                }

                var fragID = ""
                var question = "choice 3 error"
                var answer = "choice 3 error"
                var options = emptyArray<String>()

                num = (1..2).random()
                when (num) {
                    1 -> { // Which IPA symbol represents a [voiced velar stop]
                        fragID = "mc4"

                        val q = data.indices.random()
                        question = "Which IPA symbol represents a "
                        question += if(consonant) {
                            data[q][3] + " " + data[q][2] + " " + data[q][1] + " consonant?"
                        } else {
                            data[q][1] + " " + data[q][2] + " " + data[q][3] + " vowel?"
                        }

                        answer = data[q][0]

                        // choose 3 other rows (that aren't q) (to get their 'incorrect symbols')
                        val wrongSymbolIndices = ArrayList<Int>()
                        for (i:Int in data.indices) {
                            wrongSymbolIndices.add(i)
                        }
                        wrongSymbolIndices.remove(q)
                        options = arrayOf("","","")
                        for (i:Int in (0..2)) {
                            val randomValue = wrongSymbolIndices.indices.random()
                            options[i] = data[wrongSymbolIndices[randomValue]][0]
                            wrongSymbolIndices.remove(wrongSymbolIndices[randomValue])
                        }

                    }
                    2 -> { // What is the [manner of articulation] for this symbol? /[]/
                        fragID = "fillBlank"

                        val q = data.indices.random()
                        val feature = (1..3).random()

                        if(consonant) {
                            question = when(feature) {
                                1 -> "What is the manner of articulation for this symbol? /"
                                2 -> "What is the place of articulation for this symbol? /"
                                3 -> "What is the voicing for this symbol? /"
                                else -> "choice 3 consonant error"
                            }
                        }
                        else {
                            question = when(feature) {
                                1 -> "What is the height of this symbol? /"
                                2 -> "What is the depth of this symbol? /"
                                3 -> "What is the roundedness of this symbol? /"
                                else -> "choice 3 vowel error"
                            }
                        }
                        question += data[q][0] + "/"

                        answer = data[q][feature]

                    }
                }

                return QInfo(fragID, question, answer, options)
            }

            4 -> { // vowel chart
                val data = StoredData.getIpaVowels()
                val q = data.indices.random()
                return QInfo("vowelChart",
                    "Click on the correct location in the vowel IPA chart for this symbol: /" + data[q][0] + "/",
                    data[q][0], emptyArray<String>())
            }

            5 -> { // sagittal diagram
                val data = StoredData.getIpaByFeature()

                val q = (5..12).random()

                val answer = data[q][0]

                var question = "choice 5 error"
                val num = (1..2).random()
                when(num) {
                    1 -> {
                        question = "Where is the " + data[q][0] + " place of articulation?"
                    }
                    2 -> {
                        val symbol = data[q][(1..<data[q].size).random()]
                        question = "Where is the place of articulation for this symbol? /$symbol/"
                    }
                }

                return QInfo("sagittal", question, answer, emptyArray<String>())
            }

            6 -> { // ipa transcription (json)
                // TODO !!!!!!!!!!!!!!!!!
                return QInfo("", "choice 6 error", "error", emptyArray<String>())
            }

            else -> {
                return QInfo("", "choice ?? error", "error", emptyArray<String>())
            }
        }
    }

    fun advancedPhonetics(): QInfo {
        val questions = StoredData.getAdvHardcodedQuestions()
        val q = questions.indices.random()

        when((1..2).random()) {
            1 -> {
                return QInfo("fillBlank", questions[q][0], questions[q][1], emptyArray<String>())
            }
            2 -> {
                val options = questions[q][2].split("_").toTypedArray()
                var fragID = ""
                when(options.size) {
                    1 -> fragID = "mc2"
                    2 -> fragID = "mc3"
                    3 -> fragID = "mc4"
                }
                return QInfo(fragID, questions[q][0], questions[q][1], options)
            }
            else -> return QInfo("", "error", "error", emptyArray<String>())
        }
    }

    fun simpleSyntax(): QInfo {
        val questions = StoredData.getSyntaxSimple()
        val q = questions.indices.random()

        return QInfo("syntaxSimple", questions[q][0], questions[q][1], emptyArray<String>())
    }

    fun advancedSyntax(): QInfo {
        val questions = StoredData.getSyntaxAdvanced()
        val q = questions.indices.random()

        return QInfo("syntaxAdv", questions[q][0], questions[q][1], emptyArray<String>())
    }

}

class StoredData {
    companion object {
        fun getSimHardcodedQuestions(): Array<Array<String>> { // question, answer, options
            return arrayOf(
                arrayOf(
                    "Which manner of articulation has a complete blockage of airflow then sudden release?",
                    "stop",
                    "fricative_nasal_liquid" ),
                arrayOf(
                    "Which manner of articulation has a continuous but constricted airflow through the mouth?",
                    "fricative",
                    "stop_nasal_approximant" ),
                arrayOf(
                    "Which manner of articulation has the articulators very close together but not touching?",
                    "fricative",
                    "plosive_glide_nasal" ),
                arrayOf(
                    "Which manner of articulation has a complete blockage of airflow then a slow release?",
                    "affricate",
                    "stop_approximant_tap" ),
                arrayOf(
                    "Which manner of articulation has the tip of tongue rapidly contact the roof the mouth once?",
                    "tap",
                    "stop_fricative_affricate" ),
                arrayOf(
                    "Which manner of articulation can have either oral or nasal airflow?",
                    "plosive",
                    "fricative_flap_glide" )
            )
        }

        fun getIpaConsonants(): Array<Array<String>> { // symbol, manner, place, voicing
            return arrayOf(
                arrayOf(
                    "p",
                    "stop",
                    "bilabial",
                    "voiceless"
                ),
                arrayOf(
                    "b",
                    "stop",
                    "bilabial",
                    "voiced"
                ),
                arrayOf(
                    "t",
                    "stop",
                    "alveolar",
                    "voiceless"
                ),
                arrayOf(
                    "d",
                    "stop",
                    "alveolar",
                    "voiced"
                ),
                arrayOf(
                    "k",
                    "stop",
                    "velar",
                    "voiceless"
                ),
                arrayOf(
                    "g",
                    "stop",
                    "velar",
                    "voiced"
                ),
                arrayOf(
                    "ʔ",
                    "stop",
                    "glottal",
                    "voiceless"
                ),
                arrayOf(
                    "m",
                    "nasal",
                    "bilabial",
                    "voiced"
                ),
                arrayOf(
                    "n",
                    "nasal",
                    "alveolar",
                    "voiced"
                ),
                arrayOf(
                    "ŋ",
                    "nasal",
                    "velar",
                    "voiced"
                ),
                arrayOf(
                    "ɾ",
                    "tap",
                    "alveolar",
                    "voiced"
                ),
                arrayOf(
                    "f",
                    "fricative",
                    "labio-dental",
                    "voiceless"
                ),
                arrayOf(
                    "v",
                    "fricative",
                    "labio-dental",
                    "voiced"
                ),
                arrayOf(
                    "θ",
                    "fricative",
                    "dental",
                    "voiceless"
                ),
                arrayOf(
                    "ð",
                    "fricative",
                    "dental",
                    "voiced"
                ),
                arrayOf(
                    "s",
                    "fricative",
                    "alveolar",
                    "voiceless"
                ),
                arrayOf(
                    "z",
                    "fricative",
                    "alveolar",
                    "voiced"
                ),
                arrayOf(
                    "ʃ",
                    "fricative",
                    "post-alveolar",
                    "voiceless"
                ),
                arrayOf(
                    "ʒ",
                    "fricative",
                    "post-alveolar",
                    "voiced"
                ),
                arrayOf(
                    "h",
                    "fricative",
                    "glottal",
                    "voiceless"
                ),
                arrayOf(
                    "ɹ",
                    "approximant",
                    "alveolar",
                    "voiced"
                ),
                arrayOf(
                    "j",
                    "approximant",
                    "palatal",
                    "voiced"
                ),
                arrayOf(
                    "l",
                    "approximant",
                    "alveolar",
                    "voiced"
                ),
                arrayOf(
                    "w",
                    "approximant",
                    "labio-velar",
                    "voiced"
                )
            )
        }

        fun getIpaVowels(): Array<Array<String>> { // symbol, height, depth, roundness
            return arrayOf(
                arrayOf(
                    "i",
                    "close",
                    "front",
                    "unrounded"
                ),
                arrayOf(
                    "ɪ",
                    "near-close",
                    "front",
                    "unrounded"
                ),
                arrayOf(
                    "e",
                    "close-mid",
                    "front",
                    "unrounded"
                ),
                arrayOf(
                    "ɛ",
                    "open-mid",
                    "front",
                    "unrounded"
                ),
                arrayOf(
                    "æ",
                    "near-open",
                    "front",
                    "unrounded"
                ),
                arrayOf(
                    "ə",
                    "mid",
                    "central",
                    "unrounded"
                ),
                arrayOf(
                    "ʌ",
                    "open-mid",
                    "back",
                    "unrounded"
                ),
                arrayOf(
                    "u",
                    "close",
                    "back",
                    "rounded"
                ),
                arrayOf(
                    "ʊ",
                    "near-close",
                    "back",
                    "rounded"
                ),
                arrayOf(
                    "o",
                    "close-mid",
                    "back",
                    "rounded"
                ),
                arrayOf(
                    "ɔ",
                    "open-mid",
                    "back",
                    "rounded"
                ),
                arrayOf(
                    "ɑ",
                    "open",
                    "back",
                    "unrounded"
                )
            )
        }

        fun getIpaByFeature(): Array<Array<String>> {
            return arrayOf(
                arrayOf("stop","p","b","t","d","k","g","ʔ"),
                arrayOf("nasal","m","n","ŋ"),
                arrayOf("tap","ɾ"),
                arrayOf("fricative","f","v","θ","ð","s","z","ʃ","ʒ","h"),
                arrayOf("approximant","ɹ","j","l","w"),
                arrayOf("bilabial","p","b","m"),
                arrayOf("labio-dental","f","v"),
                arrayOf("dental","θ","ð"),
                arrayOf("alveolar","t","d","n","ɾ","s","z","ɹ","l"),
                arrayOf("post-alveolar","ʃ","ʒ"),
                arrayOf("palatal","j"),
                arrayOf("velar","k","g","ŋ"),
                arrayOf("glottal","h","ʔ"),
                arrayOf("voiced","b","d","g","v","ʒ","ð","z"),
                arrayOf("voiceless","p","t","k","f","ʃ","θ","s")
            )
        }

        fun getAdvHardcodedQuestions(): Array<Array<String>> { // question, answer, options
            return arrayOf (
                arrayOf(
                "If a spectrogram is in Hz and the first formant is high, what type of vowel does that indicate?",
                "low",
                "high" ),
            arrayOf(
                "If a spectrogram is in Hz and the first formant is low, what type of vowel does that indicate?",
                "high",
                "low" ),
            arrayOf(
                "If a spectrogram is in Hz and the second formant is high, what type of vowel does that indicate?",
                "front",
                "back" ),
            arrayOf(
                "If a spectrogram is in Hz and the second formant is low, what type of vowel does that indicate?",
                "back",
                "front" ),
            arrayOf(
                "If a spectrogram is in Bark and the first formant is high, what type of vowel does that indicate?",
                "high",
                "low" ),
            arrayOf(
                "If a spectrogram is in Bark and the first formant is low, what type of vowel does that indicate?",
                "low",
                "high" ),
            arrayOf(
                "If a spectrogram is in Bark and the second formant is high, what type of vowel does that indicate?",
                "back",
                "front" ),
            arrayOf(
                "If a spectrogram is in Bark and the second formant is low, what type of vowel does that indicate?",
                "front",
                "back" ),
            arrayOf(
                "Which manner of articulation doesn’t have formants?",
                "fricatives",
                "plosives_nasals_approximants" ),
            arrayOf(
                "Which manner of articulation has one formant and one 'anti-formant'?",
                "nasals",
                "plosives_fricatives_approximants" ),
            arrayOf(
                "Which approximants are distinguished by their 1st and 2nd formants?",
                "glides",
                "liquids" ),
            arrayOf(
                "Which approximants rae distinguished by their 2nd and 3rd formants?",
                "liquids",
                "glides" ),
            arrayOf(
                "What is the voice onset time (VOT) for voiced consonants?",
                "< 0 ms",
                "0 - 35 ms_35+ ms" ),
            arrayOf(
                "What is the voice onset time (VOT) for voiceless unaspirated consonants?",
                "0 - 35 ms",
                "< 0 ms_35+ ms" ),
            arrayOf(
                "What is the voice onset time (VOT) for voiceless plain consonants?",
                "0 - 35 ms",
                "< 0 ms_35+ ms" ),
            arrayOf(
                "What is the voice onset time (VOT) for voiceless aspirated consonants?",
                "35+ ms",
                "< 0 ms_0 - 35 ms" ),
            arrayOf(
                "Which voicing is also called ‘prevoiced’?",
                "voiced",
                "voiceless plain_voiceless aspirated" ),
            arrayOf(
                "Which voicing is also called ‘short lag’?",
                "voiceless plain",
                "voiced_voiceless aspirated" ),
            arrayOf(
                "Which voicing is also called ‘long lag’?",
                "voiceless aspirated",
                "voiced_voiceless plain" ),
            arrayOf(
                "Which feature does not help identify plosives in a spectrogram?",
                "amplitude",
                "duration_VOT_voicing band" ),
            arrayOf(
                "Low vowels are inherently shorter than high vowels.",
                "false",
                "true" ),
            arrayOf(
                "In a spectrogram, affricates look like noisy stops.",
                "true",
                "false" ),
            arrayOf(
                "[h] & [ʔ] pattern sometimes as obstruents and sometimes as sonorants.",
                "true",
                "false" ),
            arrayOf(
                "Liquids are also called semivowels, and appear as exaggerated vowels in a spectrogram.",
                "false",
                "true" )
            )
        }

        fun getSyntaxSimple(): Array<Array<String>> { // orthography, answer
            return arrayOf(
                arrayOf(
                    "this time",
                    "[NP[D][N]]"
                ),
                arrayOf(
                    "the first drops of rain",
                    "[NP[NP[D][A][N]][PP[P][NP[N]]]]"
                ),
                arrayOf(
                    "an indefinite time",
                    "[NP[D][A][N]]"
                ),
                arrayOf(
                    "night and day",
                    "[NP[NP[N]][Conj][NP[N]]]"
                ),
                arrayOf(
                    "his latest book",
                    "[NP[D][A][N]]"
                ),
                arrayOf(
                    "one of the first opponents",
                    "[NP[NP[N]][PP[P][NP[D][A][N]]]]"
                ),
                arrayOf(
                    "the obvious question",
                    "[NP[D][A][N]]"
                ),
                arrayOf(
                    "a quite remarkable concession",
                    "[NP[D][AP[Adv][A]][N]]"
                ),
                arrayOf(
                    "all tropical cyclones",
                    "[NP[D][A][N]]"
                ),
                arrayOf(
                    "more and more of these swirling winds",
                    "[NP[NP[N[N][Conj][N]]][PP[P][NP[D][A][N]]]]"
                ),
                arrayOf(
                    "this unplaced and forgotten spot",
                    "[NP[D][AP[A[A][Conj][A]]][N]]"
                ),
                arrayOf(
                    "have them in stock",
                    "[VP[V][NP[N]][PP[P][NP[N]]]]"
                ),
                arrayOf(
                    "eyed the handkerchief more closely",
                    "[VP[V][NP[D][N]][AdvP[Adv][Adv]]]"
                ),
                arrayOf(
                    "landed back on Earth",
                    "[VP[V][AdvP[Adv][PP[P][NP[N]]]]]"
                ),
                arrayOf(
                    "brought back a sample",
                    "[VP[V][AdvP[Adv]][NP[D][N]]]"
                ),
                arrayOf(
                    "hate the moon",
                    "[VP[V][NP[D][N]]]"
                ),
                arrayOf(
                    "are not in the world",
                    "[VP[V][AdvP[Adv]][PP[P][NP[D][N]]]]"
                ),
                arrayOf(
                    "dropped despairingly into the stream",
                    "[VP[V][AdvP[Adv]][PP[P][NP[D][N]]]]"
                ),
                arrayOf(
                    "ran along the shore",
                    "[VP[V][PP[P][NP[D][N]]]]"
                ),
                arrayOf(
                    "became a river",
                    "[VP[V][NP[D][N]]]"
                ),
                arrayOf(
                    "might capture them and learn from them",
                    "[VP[V][VP[VP[V][NP[N]]][Conj][VP[V][PP[P][NP[N]]]]]]"
                ),
                arrayOf(
                    "at the library",
                    "[PP[P][NP[D][N]]]"
                ),
                arrayOf(
                    "from a cloudless blue sky",
                    "[PP[P][NP[D][AP[A][A]][N]]]"
                ),
                arrayOf(
                    "beyond belief or necessity",
                    "[PP[P][NP[N[N][Conj][N]]]]"
                ),
                arrayOf(
                    "under the choppy surface",
                    "[PP[P][NP[D][A][N]]]"
                ),
                arrayOf(
                    "just out of reach",
                    "[PP[AdvP[Adv]][P][PP[P][NP[N]]]]"
                ),
                arrayOf(
                    "in an abstract way",
                    "[PP[P][NP[D][A][N]]]"
                ),
                arrayOf(
                    "as a young child",
                    "[PP[P][NP[D][A][N]]]"
                ),
                arrayOf(
                    "with masts like a dead man’s fingers",
                    "[PP[P][NP[NP[N]][PP[P][NP[NP[D][A][N]][N]]]]]"
                ),
                arrayOf(
                    "without much mystery or power",
                    "[PP[P][NP[A][N[N][Conj][N]]]]"
                ),
                arrayOf(
                    "on the stage",
                    "[PP[P][NP[D][N]]]"
                ),
                arrayOf(
                    "hard polished",
                    "[AP[A][A]]"
                ),
                arrayOf(
                    "so many",
                    "[AP[Adv][A]]"
                )
            )
        }

        fun getSyntaxAdvanced(): Array<Array<String>> { // orthography, answer
            return arrayOf(
                arrayOf(
                    "This gives a little context",
                    "[S[NP[N]][VP[V][NP[D][A][N]]]]"
                ),
                arrayOf(
                    "It surges over land",
                    "[S[NP[N]][VP[V][PP[P][NP[N]]]]]"
                ),
                arrayOf(
                    "I am afraid of it",
                    "[S[NP[N]][VP[V][AP[A][PP[P][NP[N]]]]]]"
                ),
                arrayOf(
                    "I walked by the shallow crystal stream",
                    "[S[NP[N]][VP[V][PP[P][NP[D][AP[A][A]][N]]]]]"
                ),
                arrayOf(
                    "I did not include my schedule for college algebra",
                    "[S[NP[N]][VP[V][Adv][VP[V][NP[NP[D][N]][PP[P][NP[N][N]]]]"
                ),
                arrayOf(
                    "I have a final on Thursday of next week for that",
                    "[S[NP[N]][VP[V][NP[NP[D][N]][PP[P][NP[NP[N]][PP[P][NP[A][N]]]]]][PP[P][NP[N]]]]]"
                ),
                arrayOf(
                    "They are really cute",
                    "[S[NP[N]][VP[V][AP[Adv][A]]]]"
                ),
                arrayOf(
                    "I’m really proud of myself",
                    "[S[NP[N]][VP[V][AP[Adv][A][PP[P][NP[N]]]]]]"
                ),
                arrayOf(
                    "She had to give me a return and re-scan it",
                    "[S[NP[N]][VP[V][S[VP[TO][VP[VP[V][NP[N]][NP[D][N]]][Conj][VP[V][NP[N]]]]]]]]"
                ),
                arrayOf(
                    "Hopefully they’ll be here in about a week",
                    "[S[AdvP[Adv]][NP[N]][VP[V][VP[V][Adv][PP[P][NP[Adv][D][N]]]]]]"
                ),
                arrayOf(
                    "Fear is arguably as old as life",
                    "[S[NP[N]][VP[V][Adv][AP[AP[Adv][A]][PP[P][NP[N]]]]]]"
                ),
                arrayOf(
                    "It is deeply ingrained in the living organisms",
                    "[S[NP[N]][VP[V][AP[Adv][A][PP[P][NP[D][A][N]]]]]]"
                ),
                arrayOf(
                    "We also learn from observation such witnessing a predator attacking another human",
                    "[S[NP[N]][Adv][VP[V][PP[P][NP[NP[N]][PP[A][P][VP[V][S[NP[D][N]][VP[V][NP[D][N]]]]]]]]]]"
                ),
                arrayOf(
                    "By trusting them we would not die",
                    "[S[PP[P][VP[V][NP[N]]]][NP[N]][VP[V][Adv][VP[V]]]]"
                ),
                arrayOf(
                    "It may be dogs or cats or something else",
                    "[S[NP[N]][VP[V][VP[V][NP[NP[N]][Conj][NP[N]][Conj][NP[N][Adv]]]]]]"
                ),
                arrayOf(
                    "It is because it is spring and inside the ground something is stirring",
                    "[S[NP[N]][VP[V][S[Conj][S[S[NP[N]][VP[V][NP[N]]]][Conj][S[PP[P][NP[D][N]]][NP[N]][VP[V][VP[V]]]]]]]]"
                ),
                arrayOf(
                    "I wanted to feel the rough edge of the collar of his coat against my cheek",
                    "[S[NP[N]][VP[V][VP[TO][VP[V][NP[NP[D][A][N]][PP[P][NP[NP[D][N]][PP[P][NP[D][N]]]]]][PP[P][NP[D][N]]]]]]]"
                ),
                arrayOf(
                    "I wanted to be young again and safe",
                    "[S[NP[N]][VP[V][VP[TO][VP[V][AP[AP[A][Adv]][Conj][AP[A]]]]]]"
                ),
                arrayOf(
                    "We walked back through the school",
                    "[S[NP[N]][VP[V][Adv][PP[P][NP[D][N]]]]]"
                ),
                arrayOf(
                    "He saw his friends run forward as their names were called",
                    "[S[NP[N]][VP[V][S[NP[D][N]][VP[V][Adv][S[Conj][S[NP[D][N]][VP[V][VP[V]]]]]]]]]"
                ),
                arrayOf(
                    "This is where we must make our stand",
                    "[S[NP[N]][VP[V][S[Conj][S[NP[N]][VP[V][VP[V][NP[D][N]]]]]]]]"
                ),
                arrayOf(
                    "The enemy can not be allowed to advance any further",
                    "[S[NP[D][N]][VP[V][Adv][VP[V][VP[V][VP[TO][VP[V][AdvP[D][Adv]]]]]]]]"
                ),
                arrayOf(
                    "His name would be called soon",
                    "[S[NP[D][N]][VP[V][VP[V][VP[V][Adv]]]]"
                ),
                arrayOf(
                    "He felt a sense of doom",
                    "[S[NP[N]][VP[V][NP[NP[D][N]][PP[P][NP[N]]]]]"
                ),
                arrayOf(
                    "At that moment his worst fears had not only been altogether realised but far exceeded",
                    "[S[PP[P][NP[D][N]]][NP[D][A][N]][VP[V][VP[AdvP[Adv][Adv]][V][VP[VP[Adv][V]][Conj][VP[Adv][V]]]]]]"
                )
            )
        }
    }
}