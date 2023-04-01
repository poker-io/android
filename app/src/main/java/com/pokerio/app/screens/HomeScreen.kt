package com.pokerio.app.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.pokerio.app.R

@Preview
@Composable
fun HomeScreen(
    @PreviewParameter(UnitProvider::class) navigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StartGameCard()
        BottomRow(navigateToSettings = navigateToSettings)
    }
}

@Preview
@Composable
private fun BottomRow(
    modifier: Modifier = Modifier,
    @PreviewParameter(UnitProvider::class) navigateToSettings: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navigateToSettings() }) {
            Icon(
                Icons.Rounded.Settings,
                contentDescription = stringResource(id = R.string.contentDescription_settings_icon)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun StartGameCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var gameCode by remember { mutableStateOf("") }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            OutlinedTextField(
                value = gameCode,
                onValueChange = { gameCode = it },
                label = { Text(stringResource(R.string.label_game_code)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedIconButton(
                onClick = { joinGame(context, gameCode) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription =
                    stringResource(id = R.string.contentDescription_join_game_button)
                )
            }
            Button(
                onClick = { createGame(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(top = 10.dp)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.new_game))
            }
        }
    }
}

private fun joinGame(context: Context, gameCode: String) {
    if (gameCode.isBlank()) {
        Toast
            .makeText(context, context.getText(R.string.error_game_code_empty), Toast.LENGTH_LONG)
            .show()
    } else {
        Toast
            .makeText(context, "TODO: Joining game", Toast.LENGTH_LONG)
            .show()
    }
}

private fun createGame(context: Context) {
    Toast
        .makeText(context, "TODO: Creating game", Toast.LENGTH_LONG)
        .show()
}

private class UnitProvider : PreviewParameterProvider<() -> Unit> {
    override val values = listOf({}).asSequence()
}
