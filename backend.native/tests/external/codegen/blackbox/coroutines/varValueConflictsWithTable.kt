// IGNORE_BACKEND: NATIVE
// WITH_RUNTIME
// WITH_COROUTINES
import kotlin.coroutines.*

class Controller {
    suspend fun suspendHere(): String = CoroutineIntrinsics.suspendCoroutineOrReturn { x ->
        x.resume("OK")
        CoroutineIntrinsics.SUSPENDED
    }
}

fun builder(c: suspend Controller.() -> Unit) {
    c.startCoroutine(Controller(), EmptyContinuation)
}

fun box(): String {
    var result = "fail 1"
    builder {
        // Initialize var with Int value
        for (i in 1..1) {
            if (i != 1) continue
        }

        // This variable should take the same slot as 'i' had
        var s: String

        // We should not spill 's' to continuation field because it's not initialized
        // More precisely it contains a value of wrong type (it conflicts with contents of local var table),
        // so an attempt of spilling may lead to problems on Android
        if (suspendHere() == "OK") {
            s = "OK"
        }
        else {
            s = "fail 2"
        }

        result = s
    }

    return result
}
