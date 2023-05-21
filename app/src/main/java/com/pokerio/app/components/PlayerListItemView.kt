package com.pokerio.app.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.pokerio.app.R
import com.pokerio.app.utils.GameState
import com.pokerio.app.utils.Player
import com.pokerio.app.utils.PlayerProvider
import com.pokerio.app.utils.PokerioLogger
import java.lang.Integer.min

const val ID_CUTOFF = 7

@Preview
@Composable
fun PlayerListItemView(
    @PreviewParameter(PlayerProvider::class) player: Player
) {
    val context = LocalContext.current

    Card {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = player.nickname,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("nickname")
                )
                Text(
                    text = "ID: ${player.playerID.substring(0, min(player.playerID.length, ID_CUTOFF))}",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.testTag("player_id")
                )
            }
            if (player.isAdmin) {
                IconButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.testTag("admin_icon")
                ) {
                    Icon(
                        Icons.Outlined.Star,
                        contentDescription = stringResource(id = R.string.contentDescription_admin)
                    )
                }
            } else if (GameState.thisPlayer.isAdmin) {
                IconButton(
                    onClick = { kickPlayer(context, player.playerID) },
                    modifier = Modifier.testTag("kick_button")
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = stringResource(id = R.string.contentDescription_kickUser)
                    )
                }
            }
        }
    }
}

fun kickPlayer(context: Context, playerID: String) {
    val onSuccess = {
        PokerioLogger.debug("Kicked player")
    }

    val onError = {
        ContextCompat.getMainExecutor(context).execute {
            Toast.makeText(context, context.getString(R.string.failed_kick), Toast.LENGTH_LONG).show()
        }
    }

    GameState.launchTask {
        GameState.kickPlayerRequest(playerID, onSuccess, onError)
    }
}
