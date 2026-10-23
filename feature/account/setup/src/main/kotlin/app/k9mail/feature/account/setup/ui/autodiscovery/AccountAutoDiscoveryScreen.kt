package app.k9mail.feature.account.setup.ui.autodiscovery

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.k9mail.feature.account.oauth.ui.AccountOAuthView
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.AutoDiscoveryUiResult
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.Effect
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.Event
import app.k9mail.feature.account.setup.ui.autodiscovery.AccountAutoDiscoveryContract.ViewModel
import net.thunderbird.components.ui.bolt.template.Scaffold
import net.thunderbird.core.common.provider.BrandNameProvider
import net.thunderbird.core.ui.contract.mvi.observe

@Composable
internal fun AccountAutoDiscoveryScreen(
    onNext: (AutoDiscoveryUiResult) -> Unit,
    onBack: () -> Unit,
    onThundermailClick: () -> Unit,
    onScanQrCodeClick: () -> Unit,
    viewModel: ViewModel,
    brandNameProvider: BrandNameProvider,
    modifier: Modifier = Modifier,
) {
    val (state, dispatch) = viewModel.observe { effect ->
        when (effect) {
            Effect.NavigateBack -> onBack()
            is Effect.NavigateNext -> onNext(effect.result)
        }
    }

    val googleAccountPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val emailAddress = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
        if (result.resultCode == Activity.RESULT_OK && !emailAddress.isNullOrBlank()) {
            dispatch(Event.GoogleAccountPicked(emailAddress))
        }
    }

    BackHandler {
        dispatch(Event.OnBackClicked)
    }

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        AccountAutoDiscoveryContent(
            state = state.value,
            onEvent = { dispatch(it) },
            onGoogleAccountClick = {
                googleAccountPicker.launch(createGoogleAccountPickerIntent())
            },
            onThundermailClick = onThundermailClick,
            onScanQrCodeClick = onScanQrCodeClick,
            oAuthContent = { isEnabled ->
                AccountOAuthView(
                    onOAuthResult = { result -> dispatch(Event.OnOAuthResult(result)) },
                    viewModel = viewModel.oAuthViewModel,
                    isEnabled = isEnabled,
                    autoStartSignIn = state.value.isGoogleAccountPicked && isEnabled,
                )
            },
            brandName = brandNameProvider.brandName,
            contentPadding = innerPadding,
        )
    }
}

@Suppress("DEPRECATION")
private fun createGoogleAccountPickerIntent(): Intent = AccountManager.newChooseAccountIntent(
    null,
    null,
    arrayOf("com.google"),
    null,
    null,
    null,
    null,
)
