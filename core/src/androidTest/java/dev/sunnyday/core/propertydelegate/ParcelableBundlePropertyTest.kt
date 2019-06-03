package dev.sunnyday.core.propertydelegate

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParcelableBundlePropertyTest {

    var Bundle.named: Value by bundleParcelable("test", Value(1))
    var Bundle.default: Value by bundleParcelable(default = Value(3))
    var Bundle.optional: Value? by bundleParcelable()

    data class Value(val value: Int) : Parcelable {

        constructor(parcel: Parcel) : this(parcel.readInt())

        override fun writeToParcel(p0: Parcel, p1: Int) {
            p0.writeInt(value)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<Value> {
            override fun createFromParcel(parcel: Parcel): Value {
                return Value(parcel)
            }

            override fun newArray(size: Int): Array<Value?> {
                return arrayOfNulls(size)
            }
        }

    }

    @Test
    fun testDefaultValue() {

        val bundle = Bundle()

        assertEquals(Value(3), bundle.default)

    }

    @Test
    fun testValueChanged() {

        val bundle = Bundle()

        bundle.default = Value(5)

        assertEquals(Value(5), bundle.default)

    }

    @Test
    fun testName() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("test"))

        bundle.named = Value(0)

        assertTrue(bundle.containsKey("test"))

    }

    @Test
    fun testNameByPropertyName() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("default"))

        bundle.default = Value(0)

        assertTrue(bundle.containsKey("default"))

    }

    @Test
    fun optionalIsNullWhenUnexists() {

        val bundle = Bundle()
        assertNull(bundle.optional)

    }

    @Test
    fun optionalRemoveKeyIfSetNull() {

        val bundle = Bundle()

        bundle.optional = Value(1)

        assertTrue(bundle.containsKey("optional"))

        bundle.optional = null

        assertFalse(bundle.containsKey("optional"))

    }

}