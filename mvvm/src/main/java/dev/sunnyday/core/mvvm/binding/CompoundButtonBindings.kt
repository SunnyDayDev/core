package dev.sunnyday.core.mvvm.binding

import android.widget.CompoundButton
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

object CompoundButtonBindings : Bindings() {

    @JvmStatic
    @BindingAdapter(
        value = ["checked", "checkedAttrChanged"],
        requireAll = false
    )
    fun bindChecked(
        view: CompoundButton,
        isChecked: Boolean,
        inverse: InverseBindingListener?
    ) {
        val binding: CheckChangedBinding = view.extras.getOrSet(CheckChangedBinding::class) {
            CheckChangedBinding(inverse)
                .apply { currentValue = view.isChecked }
                .also(view::setOnCheckedChangeListener)
        }

        if (binding.currentValue != isChecked) {
            binding.currentValue = isChecked
            view.isChecked = isChecked
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "checked")
    fun bindCheckedInverse(view: CompoundButton): Boolean {
        val binding: CheckChangedBinding = view.extras[CheckChangedBinding::class]
            ?: return view.isChecked

        return binding.currentValue
    }

    private class CheckChangedBinding(
        private val inverse: InverseBindingListener?
    ) : CompoundButton.OnCheckedChangeListener {

        var currentValue: Boolean = false

        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (isChecked != currentValue) {
                currentValue = isChecked
                inverse?.onChange()
            }
        }

    }

}