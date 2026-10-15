package com.app.shoply.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.app.shoply.R
import com.app.shoply.ui.theme.ShoplyColors
import com.app.shoply.ui.theme.ShoplyType

@Composable
fun ProductSearchBar(
    query: String,
    isClearButtonVisible: Boolean,
    isCancelButtonVisible: Boolean,
    onQueryChange: (String) -> Unit,
    onFocused: () -> Unit,
    onClearClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val fieldShape = RoundedCornerShape(28.dp)

    val hint = stringResource(R.string.search_hint)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(fieldShape)
                .background(ShoplyColors.Surface)
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) ShoplyColors.Accent else ShoplyColors.FieldBorder,
                    shape = fieldShape,
                )
                .padding(start = 16.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = ShoplyColors.Icon,
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        if (focusState.isFocused) onFocused()
                    }
                    .semantics { contentDescription = hint },
                singleLine = true,
                textStyle = ShoplyType.Input.copy(color = ShoplyColors.TextPrimary),
                cursorBrush = SolidColor(ShoplyColors.Accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(
                                text = hint,
                                style = ShoplyType.Input,
                                color = ShoplyColors.TextPlaceholder
                            )
                        }
                        innerTextField()
                    }
                },
            )
            if (isClearButtonVisible) {
                ClearButton(onClick = onClearClick)
            }
        }

        if (isCancelButtonVisible) {
            TextButton(
                onClick = onCancelClick,
                modifier = Modifier.height(48.dp),
                contentPadding = PaddingValues(start = 10.dp, end = 6.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = ShoplyColors.Accent),
            ) {
                Text(text = stringResource(R.string.search_cancel), style = ShoplyType.Button)
            }
        }
    }
}

@Composable
private fun ClearButton(onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(ShoplyColors.ClearButton),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close_small),
                contentDescription = stringResource(R.string.search_clear),
                modifier = Modifier.size(12.dp),
                tint = ShoplyColors.Surface,
            )
        }
    }
}