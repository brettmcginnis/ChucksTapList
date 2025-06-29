import SwiftUI
import kmm

@main
struct iOSApp: App {
    
    init() {
        KoinKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
