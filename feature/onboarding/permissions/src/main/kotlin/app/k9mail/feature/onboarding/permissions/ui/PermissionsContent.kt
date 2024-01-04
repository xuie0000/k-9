package app.k9mail.feature.onboarding.permissions.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.k9mail.core.ui.compose.common.PreviewDevices
import app.k9mail.core.ui.compose.common.visibility.hide
import app.k9mail.core.ui.compose.designsystem.atom.DelayedCircularProgressIndicator
import app.k9mail.core.ui.compose.designsystem.atom.Surface
import app.k9mail.core.ui.compose.designsystem.atom.button.Button
import app.k9mail.core.ui.compose.designsystem.atom.button.ButtonText
import app.k9mail.core.ui.compose.designsystem.atom.text.TextHeadline5
import app.k9mail.core.ui.compose.designsystem.template.ResponsiveWidthContainer
import app.k9mail.core.ui.compose.designsystem.template.Scaffold
import app.k9mail.core.ui.compose.theme.IconsWithBottomRightOverlay
import app.k9mail.core.ui.compose.theme.K9Theme
import app.k9mail.core.ui.compose.theme.MainTheme
import app.k9mail.feature.account.common.ui.AppTitleTopHeader
import app.k9mail.feature.onboarding.permissions.R
import app.k9mail.feature.onboarding.permissions.ui.PermissionsContract.Event
import app.k9mail.feature.onboarding.permissions.ui.PermissionsContract.State
import app.k9mail.feature.onboarding.permissions.ui.PermissionsContract.UiPermissionState
import app.k9mail.feature.account.common.R as CommonR

@Composable
internal fun PermissionsContent(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            BottomBar(state, onEvent, scrollState)
        },
    ) { innerPadding ->
        ResponsiveWidthContainer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(state = scrollState),
            ) {
                HeaderArea()

                ContentArea(state, onEvent)

                // This provides some bottom padding but is also necessary to make the vertical arrangement have the
                // desired effect of putting ContentArea() in the middle.
                Spacer(modifier = Modifier.height(MainTheme.spacings.double))
            }
        }
    }
}

@Composable
private fun HeaderArea() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppTitleTopHeader()

        TextHeadline5(
            text = stringResource(R.string.onboarding_permissions_screen_title),
            modifier = Modifier.padding(horizontal = MainTheme.spacings.double),
        )

        Spacer(modifier = Modifier.height(MainTheme.spacings.double))
    }
}

@Composable
private fun ContentArea(state: State, onEvent: (Event) -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxHeight()
            .padding(MainTheme.spacings.double),
    ) {
        if (state.isLoading) {
            DelayedCircularProgressIndicator()
        } else {
            PermissionBoxes(state, onEvent)
        }
    }
}

@Composable
private fun PermissionBoxes(
    state: State,
    onEvent: (Event) -> Unit,
) {
    PermissionBox(
        icon = IconsWithBottomRightOverlay.person,
        permissionState = state.contactsPermissionState,
        title = stringResource(R.string.onboarding_permissions_contacts_title),
        description = stringResource(R.string.onboarding_permissions_contacts_description),
        onAllowClick = { onEvent(Event.AllowContactsPermissionClicked) },
    )

    if (state.isNotificationsPermissionVisible) {
        Spacer(modifier = Modifier.height(MainTheme.spacings.quadruple))

        PermissionBox(
            icon = IconsWithBottomRightOverlay.notification,
            permissionState = state.notificationsPermissionState,
            title = stringResource(R.string.onboarding_permissions_notifications_title),
            description = stringResource(R.string.onboarding_permissions_notifications_description),
            onAllowClick = { onEvent(Event.AllowNotificationsPermissionClicked) },
        )
    }
}

@Composable
private fun BottomBar(
    state: State,
    onEvent: (Event) -> Unit,
    scrollState: ScrollState,
) {
    // Elevate the bottom bar when some scrollable content is "underneath" it
    val elevation by animateDpAsState(
        targetValue = if (scrollState.canScrollForward) 8.dp else 0.dp,
        label = "BottomBarElevation",
    )

    Surface(elevation = elevation) {
        ResponsiveWidthContainer(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        start = MainTheme.spacings.double,
                        end = MainTheme.spacings.double,
                        top = MainTheme.spacings.half,
                        bottom = MainTheme.spacings.half,
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Crossfade(
                    targetState = state.isNextButtonVisible,
                    label = "NextButton",
                ) { isNextButtonVisible ->
                    Button(
                        text = stringResource(CommonR.string.account_common_button_next),
                        onClick = { onEvent(Event.NextClicked) },
                        modifier = Modifier.hide(!isNextButtonVisible),
                    )

                    ButtonText(
                        text = stringResource(R.string.onboarding_permissions_skip_button),
                        onClick = { onEvent(Event.NextClicked) },
                        modifier = Modifier.hide(isNextButtonVisible),
                    )
                }
            }
        }
    }
}

@PreviewDevices
@Composable
internal fun PermissionContentPreview() {
    K9Theme {
        PermissionsContent(
            state = State(
                isLoading = false,
                contactsPermissionState = UiPermissionState.Granted,
                notificationsPermissionState = UiPermissionState.Denied,
                isNotificationsPermissionVisible = true,
                isNextButtonVisible = false,
            ),
            onEvent = {},
        )
    }
}