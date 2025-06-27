import com.serge.chuckstaplist.chucksModule
import org.koin.core.context.startKoin

fun doInitKoin() {
    startKoin { modules(chucksModule(BuildConfig.CALENDAR_API_KEY)) }
}
