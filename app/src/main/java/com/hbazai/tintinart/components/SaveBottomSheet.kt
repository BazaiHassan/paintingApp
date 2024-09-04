package com.hbazai.tintinart.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hbazai.tintinart.utils.GlobalVariables.IMAGE_NAME
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveBottomSheet(onclick:()->Unit, onSave:()->Unit) {
    val sheetState = rememberModalBottomSheetState()
    var textFieldValue by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(sheetState = sheetState, onDismissRequest = { onclick() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                label = { Text("Enter name") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    // Handle save action here
                    IMAGE_NAME = textFieldValue
                    scope.launch {
                        onSave()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(Color.Black)
            ) {
                Text("Save Image")
            }
        }

    }


}