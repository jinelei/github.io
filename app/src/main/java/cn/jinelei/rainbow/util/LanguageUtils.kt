package cn.jinelei.rainbow.util

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.LocaleList
import java.util.*


fun attachBaseContext(context: Context, language: String): Context {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        updateResources(context, language)
    } else {
        context
    }
}

@TargetApi(Build.VERSION_CODES.N)
private fun updateResources(context: Context, language: String): Context {
    val resources = context.resources
    val locale = getLocaleByLanguage(language)
    val configuration = resources.configuration
    configuration.setLocale(locale)
    configuration.locales = LocaleList(locale)
    return context.createConfigurationContext(configuration)
}

fun getLocaleByLanguage(language: String): Locale? {
    return when (language) {
        Locale.ENGLISH.language -> Locale.ENGLISH
        Locale.SIMPLIFIED_CHINESE.language -> Locale.SIMPLIFIED_CHINESE
        else -> Locale.ENGLISH

    }
}
