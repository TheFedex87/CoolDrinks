package it.thefedex87.cooldrinks.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import it.thefedex87.cooldrinks.presentation.components.cocktail.model.DrinkUiModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun calcDominantColor(
    drawable: Drawable,
    drink: MutableState<DrinkUiModel>?,
    onFinish: (Color) -> Unit
) {
    val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
    Palette.from(bmp).generate { palette ->
        palette?.dominantSwatch?.rgb?.let { colorValue ->
            drink?.let {
                drink.value = drink.value.copy(dominantColor = colorValue)
            }
            onFinish(Color(colorValue))
        }
    }
}

suspend fun Uri.toBitmap(context: Context): Bitmap {
    /*if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images
            .Media.getBitmap(
                context.contentResolver,
                this
            )
    } else {
        val source = ImageDecoder
            .createSource(
                context.contentResolver,
                this
            )
        ImageDecoder.decodeBitmap(source)
    }*/

    /*val parcelFileDescriptor = context.contentResolver.openFileDescriptor(this, "r")
    val fileDescriptor = parcelFileDescriptor?.fileDescriptor
    val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor?.close()
    return image*/
    val bitMap = suspendCoroutine<Bitmap> {
        Glide.with(context)
            .asBitmap()
            .load(this)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    it.resume(bitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("Not yet implemented")
                }
            })
    }
    return bitMap

}