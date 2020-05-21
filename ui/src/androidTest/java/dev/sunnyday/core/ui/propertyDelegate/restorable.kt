package dev.sunnyday.core.ui.propertyDelegate

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.sunnyday.core.propertydelegate.delegateTo
import dev.sunnyday.core.propertydelegate.delegateGetTo
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class RestorableTest {

    @Test
    fun restorablePropertyTest() {
        // Context of the app under test.
        val scenario = launchActivity<RestorableTestActivity>()
        lateinit var restorableUuid: UUID
        scenario.onActivity {
            restorableUuid = it.restorableUUID
            assertEquals(1, it.onCreateCounter)
        }
        
        scenario.recreate()

        scenario.onActivity {
            assertEquals(restorableUuid, it.restorableUUID)
            assertEquals(2, it.onCreateCounter)
        }
    }

}

class RestorableTestActivity : AppCompatActivity() {

    private val restorableState: RestorableState by restorable { RestorableState() }

    val restorableUUID by delegateGetTo { restorableState::uuid }
    var onCreateCounter by delegateTo { restorableState::onCreateCounter }
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateCounter++
    }

    private class RestorableState(
        val uuid: UUID = UUID.randomUUID(),
        var onCreateCounter: Int = 0
    ) : Parcelable {
        constructor(parcel: Parcel) : this(UUID.fromString(parcel.readString()), parcel.readInt())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(uuid.toString())
            parcel.writeInt(onCreateCounter)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<RestorableState> {
            override fun createFromParcel(parcel: Parcel): RestorableState {
                return RestorableState(parcel)
            }

            override fun newArray(size: Int): Array<RestorableState?> {
                return arrayOfNulls(size)
            }
        }

    }

}