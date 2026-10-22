package app.k9mail.feature.account.setup.ui.autodiscovery

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import app.k9mail.feature.account.common.ui.AppTitleTopHeader
import app.k9mail.feature.account.common.ui.WizardNavigationBar
import app.k9mail.feature.account.common.ui.WizardNavigationBarState
import app.k9mail.feature.account.oauth.ui.AccountOAuthContract
import app.k9mail.feature.account.oauth.ui.AccountOAuthView
import app.k9mail.feature.account.oauth.ui.view.SignInWithGoogleButton
import app.k9mail.feature.account.setup.R
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.Event
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.State
import app.k9mail.feature.account.setup.ui.autodiscovery.view.AutoDiscoveryResultApprovalView
import app.k9mail.feature.account.setup.ui.autodiscovery.view.AutoDiscoveryResultView
import net.thunderbird.components.ui.bolt.atom.button.ButtonText
import net.thunderbird.components.ui.bolt.molecule.ContentLoadingErrorView
import net.thunderbird.components.ui.bolt.molecule.ErrorView
import net.thunderbird.components.ui.bolt.molecule.LoadingView
import net.thunderbird.components.ui.bolt.molecule.input.EmailAddressInput
import net.thunderbird.components.ui.bolt.molecule.input.PasswordInput
import net.thunderbird.components.ui.bolt.template.ResponsiveWidthContainer
import net.thunderbird.components.ui.bolt.theme.BoltTheme
import net.thunderbird.feature.thundermail.ui.component.ThundermailButtonPanel

private data class AccountSetupActions(
    val onEvent: (Event) -> Unit,
    val onGoogleAccountClick: () -> Unit,
    val onShowOtherEmailSetup: () -> Unit,
    val onThundermailClick: () -> Unit,
    val onScanQrCodeClick: () -> Unit,
)

@Composable
internal fun AccountAutoDiscoveryContent(
    state: State,
    onEvent: (Event) -> Unit,
    onGoogleAccountClick: () -> Unit,
    onThundermailClick: () -> Unit,
    onScanQrCodeClick: () -> Unit,
    oAuthViewModel: AccountOAuthContract.ViewModel,
    brandName: String,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    var showOtherEmailSetup by remember { mutableStateOf(false) }

    ResponsiveWidthContainer(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .consumeWindowInsets(contentPadding)
            .imePadding()
            .testTag("AccountAutoDiscoveryContent"),
    ) { responsiveWidthPadding ->
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(responsiveWidthPadding),
            ) {
                AppTitleTopHeader(
                    title = brandName,
                )
                Spacer(modifier = Modifier.weight(1f))
                AutoDiscoveryContent(
                    state = state,
                    onEvent = onEvent,
                    onGoogleAccountClick = onGoogleAccountClick,
                    onThundermailClick = onThundermailClick,
                    onScanQrCodeClick = onScanQrCodeClick,
                    showOtherEmailSetup = showOtherEmailSetup,
                    onShowOtherEmailSetup = { showOtherEmailSetup = true },
                )
                if (state.configStep == AccountAutoDiscoveryContract.ConfigStep.OAUTH) {
                    val isAutoDiscoverySettingsTrusted = state.autoDiscoverySettings?.isTrusted ?: false
                    val isConfigurationApproved = state.configurationApproved.value ?: false
                    val isOAuthEnabled = isAutoDiscoverySettingsTrusted || isConfigurationApproved
                    Spacer(modifier = Modifier.height(BoltTheme.spacings.double))
                    AccountOAuthView(
                        onOAuthResult = { result -> onEvent(Event.OnOAuthResult(result)) },
                        viewModel = oAuthViewModel,
                        isEnabled = isOAuthEnabled,
                        autoStartSignIn = state.isGoogleAccountPicked && isOAuthEnabled,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            WizardNavigationBar(
                onNextClick = { onEvent(Event.OnNextClicked) },
                onBackClick = { onEvent(Event.OnBackClicked) },
                state = WizardNavigationBarState(
                    showNext = state.isNextButtonVisible &&
                        (state.emailAddress.value.isNotBlank() || showOtherEmailSetup),
                ),
            )
        }
    }
}

@Composable
internal fun AutoDiscoveryContent(
    state: State,
    onEvent: (Event) -> Unit,
    onGoogleAccountClick: () -> Unit,
    onThundermailClick: () -> Unit,
    onScanQrCodeClick: () -> Unit,
    showOtherEmailSetup: Boolean,
    onShowOtherEmailSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = LocalResources.current

    ContentLoadingErrorView(
        state = state,
        loading = {
            LoadingView(
                message = stringResource(id = R.string.account_setup_auto_discovery_loading_message),
                modifier = Modifier.fillMaxSize(),
            )
        },
        error = { error ->
            ErrorView(
                title = stringResource(id = R.string.account_setup_auto_discovery_loading_error),
                message = error.toAutoDiscoveryErrorString(resources),
                onRetry = { onEvent(Event.OnRetryClicked) },
                modifier = Modifier.fillMaxSize(),
            )
        },
        content = { contentState ->
            ContentView(
                state = contentState,
                onEvent = onEvent,
                onGoogleAccountClick = onGoogleAccountClick,
                onThundermailClick = onThundermailClick,
                onScanQrCodeClick = onScanQrCodeClick,
                showOtherEmailSetup = showOtherEmailSetup,
                onShowOtherEmailSetup = onShowOtherEmailSetup,
                resources = resources,
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    )
}

@Composable
internal fun ContentView(
    state: State,
    onEvent: (Event) -> Unit,
    onGoogleAccountClick: () -> Unit,
    onThundermailClick: () -> Unit,
    onScanQrCodeClick: () -> Unit,
    showOtherEmailSetup: Boolean,
    onShowOtherEmailSetup: () -> Unit,
    resources: Resources,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(BoltTheme.spacings.quadruple)
            .then(modifier),
    ) {
        if (state.configStep != AccountAutoDiscoveryContract.ConfigStep.EMAIL_ADDRESS) {
            AutoDiscoveryResultView(
                settings = state.autoDiscoverySettings,
                onEditConfigurationClick = { onEvent(Event.OnEditConfigurationClicked) },
            )
            if (state.autoDiscoverySettings != null && state.autoDiscoverySettings.isTrusted.not()) {
                AutoDiscoveryResultApprovalView(
                    approvalState = state.configurationApproved,
                    onApprovalChange = { onEvent(Event.ResultApprovalChanged(it)) },
                )
            }
            Spacer(modifier = Modifier.height(BoltTheme.spacings.double))
        }

        GoogleFirstAccountContent(
            state = state,
            showOtherEmailSetup = showOtherEmailSetup,
            resources = resources,
            actions = AccountSetupActions(
                onEvent = onEvent,
                onGoogleAccountClick = onGoogleAccountClick,
                onShowOtherEmailSetup = onShowOtherEmailSetup,
                onThundermailClick = onThundermailClick,
                onScanQrCodeClick = onScanQrCodeClick,
            ),
        )

        if (state.configStep == AccountAutoDiscoveryContract.ConfigStep.PASSWORD) {
            Spacer(modifier = Modifier.height(BoltTheme.spacings.double))
            PasswordInput(
                password = state.password.value,
                errorMessage = state.password.error?.toAutoDiscoveryValidationErrorString(resources),
                onPasswordChange = { onEvent(Event.PasswordChanged(it)) },
                contentPadding = PaddingValues(),
                modifier = Modifier.testTag("account_setup_password_input"),
            )
        }
    }
}

@Composable
private fun GoogleFirstAccountContent(
    state: State,
    showOtherEmailSetup: Boolean,
    resources: Resources,
    actions: AccountSetupActions,
) {
    if (state.emailAddress.value.isBlank() && !showOtherEmailSetup) {
        Column {
            SignInWithGoogleButton(
                onClick = actions.onGoogleAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_setup_sign_in_with_google"),
            )
            ButtonText(
                text = stringResource(R.string.account_setup_other_email_provider_button),
                onClick = actions.onShowOtherEmailSetup,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_setup_other_email_provider"),
            )
        }
    } else {
        AnimatedVisibility(state.emailAddress.value.isBlank()) {
            ThundermailButtonPanel(
                onThundermailClick = actions.onThundermailClick,
                onScanQrCodeClick = actions.onScanQrCodeClick,
                modifier = Modifier
                    .testTag("thundermail_panel")
                    .padding(bottom = BoltTheme.spacings.quadruple),
            )
        }

        if (!state.isGoogleAccountPicked ||
            state.configStep != AccountAutoDiscoveryContract.ConfigStep.OAUTH
        ) {
            EmailAddressInput(
                emailAddress = state.emailAddress.value,
                errorMessage = state.emailAddress.error?.toAutoDiscoveryValidationErrorString(resources),
                onEmailAddressChange = { actions.onEvent(Event.EmailAddressChanged(it)) },
                contentPadding = PaddingValues(),
                modifier = Modifier.testTag("account_setup_email_address_input"),
            )
        }
    }
}
