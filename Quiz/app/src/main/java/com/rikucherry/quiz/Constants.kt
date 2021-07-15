package com.rikucherry.quiz

object Constants{

    const val INTENT_QUIZ_TOTAL = "INTENT_QUIZ_TOTAL"
    const val INTENT_QUIZ_CORRECT = "INTENT_QUIZ_CORRECT"
    const val SP_USER_NAME = "SP_USER_NAME"

    // return an arraylist that holds bunch of questions
    fun getQuestions(): ArrayList<Question> {
        val questionList = ArrayList<Question>()

        val que1 = Question(1, "请问这是哪国国旗?",
            R.drawable.ic_flag_of_argentina,
            "阿根廷",
            "澳大利亚",
            "亚美尼亚",
            "西班牙",
            1
            )

        val que2 = Question(2, "请问这是哪国国旗?",
            R.drawable.ic_flag_of_denmark,
            "叙利亚",
            "丹麦",
            "希腊",
            "智利",
            2
        )

        val que3 = Question(3, "请问这是哪国国旗?",
            R.drawable.ic_flag_of_kuwait,
            "斯洛伐克",
            "科威特",
            "丹麦",
            "斐济",
            2
        )

        val que4 = Question(4, "请问这是哪国国旗?",
            R.drawable.ic_flag_of_belgium,
            "德国",
            "法国",
            "意大利",
            "比利时",
            4
        )

        val que5 = Question(5, "请问这是哪国国旗?",
            R.drawable.ic_flag_of_new_zealand,
            "澳大利亚",
            "英国",
            "新西兰",
            "巴西",
            3
        )

        questionList.add(que1)
        questionList.add(que2)
        questionList.add(que3)
        questionList.add(que4)
        questionList.add(que5)
        return questionList
    }


}