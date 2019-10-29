package dev.sunnyday.core.mvvm.binding

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import dev.sunnyday.core.mvvm.R
import dev.sunnyday.core.runtime.alsoDo
import java.util.*
import kotlin.properties.Delegates

object RadioGroupBindings : Bindings() {
    
    @BindingAdapter( "selectedValue")
    @JvmStatic fun <T> bindSelectedValue(
        view: RadioGroup,
        value: T
    ) {
        val core: RadioGroupCore<T> = view.core()
        core.bindSelectedValue(value)
    }
    
    @BindingAdapter("selectedValueAttrChanged")
    @JvmStatic fun <T> bindSelectedValueInverse(
        view: RadioGroup,
        valueInverse: InverseBindingListener?
    ) {
        val core: RadioGroupCore<T> = view.core()
        core.bindSelectedValueInverse(valueInverse)
    }
    
    @BindingAdapter("value")
    @JvmStatic fun <T> bindValue(view: RadioButton, value: T) {
        val parent = (view.parent as? RadioGroup) ?: return
        parent.core<T>().bindValue(view, value)
    }

    fun <T> RadioGroup.selectedValue(): T? = core<T>().selectedValue

    private fun <T> RadioGroup.core(): RadioGroupCore<T> =
        getOrSetListener(R.id.binding__radio_group__core) {
            RadioGroupCore<T>(this).also {
                setOnCheckedChangeListener(it)
            }
        }
    
    private class RadioGroupCore<T>(
        private val radioGroup: RadioGroup
    ) : RadioGroup.OnCheckedChangeListener {
        
        private var selectedValueInverse: InverseBindingListener? = null
        
        var selectedValue: T?
            by Delegates.observable(null as T?) { _, _, newValue ->
                newValue ?: return@observable
                onSelectedValueChanged(newValue)
            }
            private set
        
        private val values: MutableMap<RadioButton, T> = WeakHashMap<RadioButton, T>()
        
        private var internalChanges = false
    
        fun bindSelectedValue(value: T?) {
            if (selectedValue != value) {
                radioGroup.post {
                    internalChange {
                        selectedValue = value
                    }
                }
            }
        }
    
        fun bindSelectedValueInverse(valueInverse: InverseBindingListener?) {
            if (valueInverse != selectedValueInverse) {
                selectedValueInverse = valueInverse
            }
        }
        
        fun bindValue(button: RadioButton, value: T) {
            values[button] = value
        }
        
        override fun onCheckedChanged(view: RadioGroup, id: Int) {
            if (internalChanges) return

            val button = view.findViewById<RadioButton>(id)

            internalChange {
                selectedValue = button?.let { values[it] }
            }

            selectedValueInverse?.onChange()
        }
        
        private fun onSelectedValueChanged(newValue: T) {
            
            val checkedButton = values.asSequence()
                .find { (_, value) -> value == newValue }
                ?.let { (view, _) -> view }
                ?: return Unit alsoDo {
                    radioGroup.clearCheck()
                }
    
            if (checkedButton.id == radioGroup.checkedRadioButtonId) return
    
            checkedButton.isChecked = true
            
        }

        private inline fun internalChange(action: () -> Unit) {
            internalChanges = true
            action()
            internalChanges = false
        }
    
    }
    
}