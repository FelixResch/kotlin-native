// IGNORE_BACKEND: NATIVE
// WITH_RUNTIME
// WITH_COROUTINES
import kotlin.coroutines.*

class Controller {
    suspend fun suspendHere(): Unit = CoroutineIntrinsics.suspendCoroutineOrReturn { x ->
        x.resume(Unit)
        CoroutineIntrinsics.SUSPENDED
    }
}

fun builder(c: suspend Controller.() -> Unit) {
    c.startCoroutine(Controller(), EmptyContinuation)
}

private var booleanResult = false
fun setBooleanRes(x: Boolean, ignored: Unit) {
    booleanResult = x
}

fun box(): String {
    builder {
        // 'true' value is spilled into variable and saved to field before suspension point
        // It's important that there is no type info about this variable in local var table,
        // so we should infer that ICONST_1 is a boolean value from it's usage
        setBooleanRes(true, suspendHere())
    }

    if (!booleanResult) return "fail 1"

    return "OK"
}
