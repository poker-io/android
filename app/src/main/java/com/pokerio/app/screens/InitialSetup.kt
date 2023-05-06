package com.pokerio.app.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.pokerio.app.R
import com.pokerio.app.utils.UnitUnitProvider

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun InitialSetupScreen(
    @PreviewParameter(UnitUnitProvider::class) exitInitialSetup: () -> Unit
) {
    val sharedPreferences = LocalContext.current.getSharedPreferences(
        stringResource(id = R.string.shared_preferences_file),
        Context.MODE_PRIVATE
    )

    var nickname by remember { mutableStateOf("") }
    var nicknameCorrect by remember { mutableStateOf(false) }
    val nicknameSharedKey = stringResource(id = R.string.sharedPreferences_nickname)
    val onNicknameUpdate = { newValue: String ->
        nickname = newValue
        nicknameCorrect = nickname.isNotBlank() && nickname.length <= 20
    }

    val onContinue = {
        with(sharedPreferences.edit()) {
            putString(nicknameSharedKey, nickname)
            apply()
        }

        exitInitialSetup()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.select_nickname))
        OutlinedTextField(
            value = nickname,
            onValueChange = { onNicknameUpdate(it) },
            label = { Text(stringResource(id = R.string.nickname)) },
            modifier = Modifier.testTag("nickname_input"),
            isError = !nicknameCorrect,
            supportingText = {
                if (!nicknameCorrect) {
                    Text(stringResource(id = R.string.nickname_error))
                }
            }
        )
        IconButton(
            onClick = { onContinue() },
            enabled = nicknameCorrect,
            modifier = Modifier.testTag("continue_button")
        ) {
            Icon(
                Icons.Rounded.ArrowForward,
                contentDescription = null
            )
        }
    }
}
