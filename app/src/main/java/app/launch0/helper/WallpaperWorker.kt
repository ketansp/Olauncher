package app.launch0.helper

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.launch0.data.Constants
import app.launch0.data.Prefs
import kotlinx.coroutines.coroutineScope

class WallpaperWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val prefs = Prefs(applicationContext)

    override suspend fun doWork(): Result = coroutineScope {
        val success =
            if (isLaunch0Default(applicationContext).not())
                true
            else if (prefs.dailyWallpaper) {
                val isDark = checkWallpaperType() == Constants.WALL_TYPE_DARK
                val seed = WallpaperGenerator.getTodaySeed()
                if (prefs.dailyWallpaperSeed == seed)
                    true
                else {
                    val result = WallpaperGenerator.generateAndSetWallpaper(applicationContext, seed, isDark)
                    if (result) prefs.dailyWallpaperSeed = seed
                    result
                }
            } else
                true

        if (success)
            Result.success()
        else
            Result.retry()
    }

    private fun checkWallpaperType(): String {
        return when (prefs.appTheme) {
            AppCompatDelegate.MODE_NIGHT_YES -> Constants.WALL_TYPE_DARK
            AppCompatDelegate.MODE_NIGHT_NO -> Constants.WALL_TYPE_LIGHT
            else -> if (applicationContext.isDarkThemeOn())
                Constants.WALL_TYPE_DARK
            else
                Constants.WALL_TYPE_LIGHT
        }
    }
}
