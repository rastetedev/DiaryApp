package com.androiddevhispano.diaryapp.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.ui.theme.Size.extraLarge

@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    onSignOutClicked: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onSignOutClicked = onSignOutClicked
            )
        }
    ) {
        content()
    }
}

@Composable
fun DrawerContent(
    onSignOutClicked: () -> Unit
) {
    ModalDrawerSheet {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null
        )
        NavigationDrawerItem(
            label = {
                Row(
                    modifier = Modifier
                        .padding(horizontal = extraLarge)
                ) {
                    //Icon( // In case you want a gray icon
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        //tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(extraLarge))
                    Text(
                        text = stringResource(id = R.string.sign_out),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            selected = false,
            onClick = onSignOutClicked
        )
    }
}