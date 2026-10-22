package app.k9mail.feature.account.setup.ui.autodiscovery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.k9mail.feature.account.setup.ui.autodiscovery.fake.fakeAutoDiscoveryResultSettings
import net.thunderbird.components.ui.bolt.PreviewWithTheme
import net.thunderbird.core.validation.input.StringInputField

@Composable
@Preview(showBackground = true)
internal fun AccountAutoDiscoveryContentPreview() {
    PreviewWithTheme {
        AccountAutoDiscoveryContent(
            state = AccountAutoDiscoveryContract.State(),
            onEvent = {},
            onGoogleAccountClick = {},
            onThundermailClick = {},
            onScanQrCodeClick = {},
            oAuthContent = {},
            brandName = "BrandName",
            contentPadding = PaddingValues(),
        )
    }
}

@Composable
@Preview(showBackground = true)
internal fun AccountAutoDiscoveryContentEmailPreview() {
    PreviewWithTheme {
        AccountAutoDiscoveryContent(
            state = AccountAutoDiscoveryContract.State(
                emailAddress = StringInputField(value = "test@example.com"),
            ),
            onEvent = {},
            onGoogleAccountClick = {},
            onThundermailClick = {},
            onScanQrCodeClick = {},
            oAuthContent = {},
            brandName = "BrandName",
            contentPadding = PaddingValues(),
        )
    }
}

@Composable
@Preview(showBackground = true)
internal fun AccountAutoDiscoveryContentPasswordPreview() {
    PreviewWithTheme {
        AccountAutoDiscoveryContent(
            state = AccountAutoDiscoveryContract.State(
                configStep = AccountAutoDiscoveryContract.ConfigStep.PASSWORD,
                emailAddress = StringInputField(value = "test@example.com"),
                autoDiscoverySettings = fakeAutoDiscoveryResultSettings(isTrusted = true),
            ),
            onEvent = {},
            onGoogleAccountClick = {},
            onThundermailClick = {},
            onScanQrCodeClick = {},
            oAuthContent = {},
            brandName = "BrandName",
            contentPadding = PaddingValues(),
        )
    }
}

@Composable
@Preview(showBackground = true)
internal fun AccountAutoDiscoveryContentPasswordUntrustedSettingsPreview() {
    PreviewWithTheme {
        AccountAutoDiscoveryContent(
            state = AccountAutoDiscoveryContract.State(
                configStep = AccountAutoDiscoveryContract.ConfigStep.PASSWORD,
                emailAddress = StringInputField(value = "test@example.com"),
                autoDiscoverySettings = fakeAutoDiscoveryResultSettings(isTrusted = false),
            ),
            onEvent = {},
            onGoogleAccountClick = {},
            onThundermailClick = {},
            onScanQrCodeClick = {},
            oAuthContent = {},
            brandName = "BrandName",
            contentPadding = PaddingValues(),
        )
    }
}

@Composable
@Preview(showBackground = true)
internal fun AccountAutoDiscoveryContentPasswordNoSettingsPreview() {
    PreviewWithTheme {
        AccountAutoDiscoveryContent(
            state = AccountAutoDiscoveryContract.State(
                configStep = AccountAutoDiscoveryContract.ConfigStep.PASSWORD,
                emailAddress = StringInputField(value = "test@example.com"),
            ),
            onEvent = {},
            onGoogleAccountClick = {},
            onThundermailClick = {},
            onScanQrCodeClick = {},
            oAuthContent = {},
            brandName = "BrandName",
            contentPadding = PaddingValues(),
        )
    }
}

@Composable
@Preview(showBackground = true)
internal fun AccountAutoDiscoveryContentOAuthPreview() {
    PreviewWithTheme {
        AccountAutoDiscoveryContent(
            state = AccountAutoDiscoveryContract.State(
                configStep = AccountAutoDiscoveryContract.ConfigStep.OAUTH,
                emailAddress = StringInputField(value = "test@example.com"),
                autoDiscoverySettings = fakeAutoDiscoveryResultSettings(isTrusted = true),
            ),
            onEvent = {},
            onGoogleAccountClick = {},
            onThundermailClick = {},
            onScanQrCodeClick = {},
            oAuthContent = {},
            brandName = "BrandName",
            contentPadding = PaddingValues(),
        )
    }
}
