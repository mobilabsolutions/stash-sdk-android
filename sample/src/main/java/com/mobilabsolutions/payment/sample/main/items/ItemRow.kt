package com.mobilabsolutions.payment.sample.main.items

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.mobilabsolutions.payment.sample.R
import kotlinx.android.synthetic.main.layout_item.view.*

/**
 * @author <a href="biju@mobilabsolutions.com">Biju Parvathy</a> on 10-04-2019.
 */

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ItemRow @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_item, this, true)
    }

    @ModelProp
    fun setItem(item: Item) {
        image.setImageResource(item.image)
        title.text = item.title
        desc.text = item.desc
        price.text = item.price.toPlainString()
    }

    @CallbackProp
    fun setClickListener(listener: View.OnClickListener?) {
        setOnClickListener(listener)
    }
}