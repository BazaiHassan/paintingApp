package com.hbazai.tintinart.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hbazai.tintinart.R
import com.hbazai.tintinart.components.SaveBottomSheet
import com.hbazai.tintinart.model.BrushButton
import com.hbazai.tintinart.model.ColorButton
import com.hbazai.tintinart.model.Line
import com.hbazai.tintinart.utils.GlobalVariables.IMAGE_NAME
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DrawScreen() {

    val context = LocalContext.current

    // New from claude
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val canvasBitmap = remember {
        Bitmap.createBitmap(1080,1080,Bitmap.Config.ARGB_8888).asImageBitmap()
    }

    val path = remember {
        Path()
    }
    val paint = remember {
        Paint().apply {
            isAntiAlias=true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    val imageBitmap = remember { ImageBitmap(1080, 1080) }

    var isBottomSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var currentColor by remember {
        mutableStateOf(Color.Black)
    }

    var currentStrockWidth by remember {
        mutableFloatStateOf(5f)
    }


    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                selectedImageBitmap = bitmap.asImageBitmap()

            }
        }


    val colorButtonsTop = listOf(
        ColorButton(color = Color(0xFF00B2FF.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFF0084FF.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFF0048FF.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFF8800FF.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFFEA00FF.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFFFF0084.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFF682D03.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFF000000.toInt()), size = 30.dp),
    )

    val colorButtonsBottom = listOf(
        ColorButton(color = Color(0xFFFF0000.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFFFC9300.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFFFFD900.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFFA6FF00.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFF1F7A3A.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFF757575.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFFBF6A02.toInt()), size = 30.dp),
        ColorButton(color = Color(0xFFD9D9D9.toInt()), size = 30.dp),
    )

    val brushButtons = listOf(
        BrushButton(
            name = "Brush 20",
            icon = painterResource(id = R.drawable.ic_brush_1),
            strokeValue = 20f,
            size = 40.dp
        ),
        BrushButton(
            name = "Brush 40",
            icon = painterResource(id = R.drawable.ic_brush_2),
            strokeValue = 40f,
            size = 60.dp
        ),
        BrushButton(
            name = "Brush 80",
            icon = painterResource(id = R.drawable.ic_brush_3),
            strokeValue = 80f,
            size = 60.dp
        ),
        BrushButton(
            name = "Pencil",
            icon = painterResource(id = R.drawable.ic_pencil),
            strokeValue = 5f,
            size = 60.dp
        ),
        BrushButton(
            name = "Eraser",
            icon = painterResource(id = R.drawable.ic_erase),
            strokeValue = 30f,
            size = 60.dp
        ),
    )


    val lines = remember {
        mutableStateListOf<Line>()
    }

    if (isBottomSheetVisible) {
        SaveBottomSheet(onclick = { isBottomSheetVisible = false }) {
            saveCanvasAsImage(lines, imageBitmap, context)
        }
    }

    Column(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxSize() // Fill the entire screen
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.LightGray),
            title = { Text(text = "TinTin Art", color = Color.Black, fontSize = 18.sp) },
            actions = {
                Button(
                    colors = ButtonDefaults.buttonColors(Color.Black),
                    onClick = {
                        // Get Image from local
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .padding(3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Add Image")
                    }
                }
                Button(
                    colors = ButtonDefaults.buttonColors(Color.Black),
                    onClick = { isBottomSheetVisible = true },
                    modifier = Modifier
                        .padding(3.dp)
                ) {
                    Text(text = "Save")
                }
            }
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            items(brushButtons) { brushBtn ->
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = brushBtn.icon,
                    contentDescription = "",
                    modifier = Modifier
                        .size(brushBtn.size)
                        .combinedClickable(
                            onClick = {
                                if (brushBtn.name == "Eraser") {
                                    if (lines.isNotEmpty()) {
                                        lines.removeAt(lines.size - 1)
                                    }
                                } else {
                                    currentStrockWidth = brushBtn.strokeValue
                                }
                            },
                            onLongClick = {
                                if (brushBtn.name == "Eraser") {
                                    lines.clear()
                                }
                            }
                        ))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                items(colorButtonsTop) { colorBtn ->
                    Spacer(modifier = Modifier.width(14.dp))
                    Button(
                        onClick = { currentColor = colorBtn.color },
                        colors = ButtonDefaults.buttonColors(colorBtn.color),
                        modifier = Modifier
                            .size(colorBtn.size)
                            .clip(CircleShape)
                    ) {

                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                items(colorButtonsBottom) { colorBtn ->
                    Spacer(modifier = Modifier.width(14.dp))
                    Button(
                        onClick = { currentColor = colorBtn.color },
                        colors = ButtonDefaults.buttonColors(colorBtn.color),
                        modifier = Modifier
                            .size(colorBtn.size)
                            .clip(CircleShape)
                    ) {

                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(width = 1.dp, color = Color.LightGray)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(state = transformableState)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val scaledDragAmount = dragAmount / scale
                                val scaledPosition = (change.position - offset) / scale
                                val line = Line(
                                    start = scaledPosition - scaledDragAmount,
                                    end = scaledPosition,
                                    color = currentColor,
                                    strokeWidth = currentStrockWidth / scale
                                )
                                lines.add(line)
                            }
                        }
                ) {
                    scale(scale){
                        translate(offset.x, offset.y) {
                            selectedImageBitmap?.let { image ->
                                // Desired size
                                val desiredWidth = 1000f
                                val desiredHeight = 1000f

                                // Calculate the scale factor
                                val scaleX = desiredWidth / image.width
                                val scaleY = desiredHeight / image.height

                                // Offset to position the image
                                val imageOffset = Offset(10f, 10f)

                                // Draw the scaled image
                                drawIntoCanvas { canvas ->
                                    canvas.withSave {
                                        // Apply scaling and translation
                                        canvas.scale(scaleX, scaleY)
                                        canvas.translate(imageOffset.x / scaleX, imageOffset.y / scaleY)

                                        // Draw the image at the origin, which will be scaled and translated
                                        drawImage(image)
                                    }
                                }
                            }
                            lines.forEach { line ->
                                drawLine(
                                    color = line.color,
                                    start = line.start,
                                    end = line.end,
                                    strokeWidth = line.strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            }
                            drawImage(imageBitmap)
                        }
                    }

                }
            }
        }

    }


}

// Function to save the image to storage
fun saveCanvasAsImage(lines: List<Line>, selectedImageBitmap: ImageBitmap?, context: Context) {
    // Create a Bitmap with desired dimensions
    val bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // Draw the image and lines on the Bitmap's canvas
    selectedImageBitmap?.let { image ->
        // Desired size
        val desiredWidth = 1000f
        val desiredHeight = 1000f

        // Calculate the scale factor
        val scaleX = desiredWidth / image.width
        val scaleY = desiredHeight / image.height

        // Offset to position the image
        val imageOffset = android.graphics.PointF(10f, 10f)

        // Draw the scaled image
        canvas.save()
        canvas.scale(scaleX, scaleY)
        canvas.translate(imageOffset.x / scaleX, imageOffset.y / scaleY)
        canvas.drawBitmap(image.asAndroidBitmap(), 0f, 0f, null)
        canvas.restore()
    }

    // Draw the lines on the Bitmap's canvas
    val paint = android.graphics.Paint()
    lines.forEach { line ->
        paint.color = line.color.toArgb()
        paint.strokeWidth = line.strokeWidth
        paint.strokeCap = android.graphics.Paint.Cap.ROUND
        canvas.drawLine(line.start.x, line.start.y, line.end.x, line.end.y, paint)
    }

    // Save the Bitmap as a PNG file
    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val file = File(path, "${IMAGE_NAME}.png")
    FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
    }

    // Notify the user
    Toast.makeText(context, "Image Saved Successfully!", Toast.LENGTH_SHORT).show()

}

