package com.simple.meditrack

object Id {

    const val UNIT = "UNIT"
    const val NAME = "NAME"
    const val NOTE = "NOTE"
    const val TIME = "TIME"
    const val DOSAGE = "DOSAGE"
    const val MEDICINE = "MEDICINE"
    const val QUANTITY = "QUANTITY"
    const val ADD_MEDICINE = "ADD_MEDICINE"
}

object Tag {

    const val THEME = "THEME"
}

object Param {

    const val ID = "ID"
    const val HOUR = "HOUR"
    const val NAME = "NAME"
    const val TOTAL = "TOTAL"
    const val FIRST = "FIRST"
    const val MINUTE = "MINUTE"
    const val RESULT = "RESULT"
    const val MEDICINE = "MEDICINE"
    const val IMAGE_PATH = "IMAGE_PATH"
    const val LANGUAGE_CODE = "LANGUAGE_CODE"
    const val TRANSITION_DURATION = "TRANSITION_DURATION"

    const val VOICE_ID = "VOICE_ID"
    const val KEY_REQUEST = "KEY_REQUEST"

    const val ROOT_TRANSITION_NAME = "ROOT_TRANSITION_NAME"
}

object Payload {

    const val THEME = "THEME"
}

object EventName {

    const val TIME = "TIME"
    const val RETRY = "RETRY"
    const val CHANGE_UNIT = "CHANGE_UNIT"
    const val ADD_MEDICINE = "ADD_MEDICINE"
}

object Deeplink {

    const val ADD_ALARM = "app://add_alarm"
    const val PICK_TIME = "app://pick_time"
    const val CHOOSE_UNIT = "app://choose_unit"
    const val ADD_MEDICINE = "app://add_medicine"
    const val NOTIFICATION = "app://notification"
    const val NOTIFICATION_VIEW = "app://notification_view"

    const val RESULT = "app://result"
    const val CONFIRM = "app://confirm"
}