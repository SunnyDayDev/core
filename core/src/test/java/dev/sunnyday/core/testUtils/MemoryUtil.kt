package dev.sunnyday.core.testUtils


object MemoryUtil {

    fun eatAllMemory() {
        try {
            (0..Int.MAX_VALUE).map { ByteArray(Int.MAX_VALUE - 2) }
        }
        catch (ignored: OutOfMemoryError) { }
        finally {
            System.gc()
        }
    }

}