package com.simple.meditrack.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.simple.core.utils.extentions.orZero
import com.simple.meditrack.Param
import com.simple.meditrack.domain.usecases.GetKeyTranslateAsyncUseCase
import com.simple.meditrack.domain.usecases.alarm.GetAlarmByIdAsyncUseCase
import com.simple.meditrack.domain.usecases.alarm.GetListAlarmAsyncUseCase
import com.simple.meditrack.domain.usecases.medicine.GetListMedicineAsyncUseCase
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.ui.notification.NotificationActivity
import com.simple.meditrack.utils.AlarmUtils
import com.simple.meditrack.utils.NotificationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val getListAlarmAsyncUseCase: GetListAlarmAsyncUseCase by inject()

    private val getAlarmByIdAsyncUseCase: GetAlarmByIdAsyncUseCase by inject()

    private val getListMedicineAsyncUseCase: GetListMedicineAsyncUseCase by inject()

    private val getKeyTranslateAsyncUseCase: GetKeyTranslateAsyncUseCase by inject()


    override fun onReceive(context: Context?, intent: Intent?) {

        context ?: return

        val id = intent?.extras?.getString(Param.ID)

        if (!id.isNullOrBlank()) GlobalScope.launch(Dispatchers.IO) {

            val alarm = getAlarmByIdAsyncUseCase.execute(GetAlarmByIdAsyncUseCase.Param(id)).first()

            if (alarm == null) {

                AlarmUtils.cancelAlarm(context = context, intent)
                return@launch
            }

            val notificationIntent = Intent(context, NotificationActivity::class.java)
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
            notificationIntent.putExtras(intent.extras ?: bundleOf())
            context.startActivity(notificationIntent)
        } else GlobalScope.launch(Dispatchers.IO) {

            val list = getListAlarmAsyncUseCase.execute().first()

            list.forEach {

                AlarmUtils.setAlarm(context, it)
            }

            AlarmUtils.setDailyAlarm(context)


            val medicineList = getListMedicineAsyncUseCase.execute().firstOrNull()?.filter {

                it.quantity != Medicine.UNLIMITED && it.countForNextDays.keys.lastOrNull().orZero() < 3
            } ?: return@launch

            // không có loại thuốc nào sắp hết hạn
            if (medicineList.isEmpty()) {

                return@launch
            }

            val translate = getKeyTranslateAsyncUseCase.execute().firstOrNull() ?: return@launch

            NotificationUtils.sendNotification(
                context = context,

                title = translate["title_out_of_medicine"].orEmpty(),
                message = translate["message_out_of_medicine"].orEmpty(),
            )
        }
    }
}