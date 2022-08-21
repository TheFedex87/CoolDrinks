package it.thefedex87.cooldrinks.presentation.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import java.io.IOException

@Composable
fun GalleryPictureSelector(
    onPicturePicked: (Bitmap) -> Unit,
    selectedPicture: Bitmap?
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val imageData = remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            imageData.value = it
        }

    LaunchedEffect(key1 = imageData.value) {
        imageData.let {
            val bitmap: Bitmap?
            val uri = it.value
            if (uri != null) {
                bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images
                        .Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder
                        .createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
                bitmap?.let(onPicturePicked)
            }
        }
    }


    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                launcher.launch(
                    "image/*"
                )
            },
            modifier = Modifier.padding(spacing.spaceMedium)
        ) {
            Text(text = "Select")
        }

        selectedPicture?.let { btm ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(
                        Bitmap.createScaledBitmap(
                            btm,
                            (btm.width * 0.3).toInt(),
                            (btm.height * 0.3).toInt(),
                            false
                        )
                    )
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .padding(spacing.spaceMedium)
            )
        }
    }
}

fun Bitmap.saveToLocalStorage(context: Context, filename: String): Boolean {
    return try {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { stream ->
            if (!this.compress(Bitmap.CompressFormat.JPEG, 70, stream)) {
                throw IOException("Couldn't save image")
            }
            stream.close()
        }

        /*val cw = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/imageDir
        // path to /data/data/yourapp/app_data/imageDir
        val directory: File = cw.getDir("imageDir", MODE_PRIVATE)
        // Create imageDir
        // Create imageDir
        val mypath = File(directory, filename)

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            this.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }*/
        true
    } catch (ex: IOException) {
        false
    }
}