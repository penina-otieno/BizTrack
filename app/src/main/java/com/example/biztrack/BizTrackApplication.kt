
package com.example.biztrack

import android.app.Application
import com.example.biztrack.data.local.BizTrackDatabase

class BizTrackApplication : Application() {

    val database by lazy {
        BizTrackDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()

        FinancialRepository.init(database, this)
    }
}
