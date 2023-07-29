package io.github.takusan23.photransfer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.photransfer.R

/**
 * ライセンス画面
 *
 * @param onBack 戻るボタン押したら呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen(
    onBack: () -> Unit,
) {
    val licenseList = listOf(rxDnssd, coil, materialIcon, ktor, kotlinCoroutine, okHttp)

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(painter = painterResource(id = R.drawable.ic_outline_arrow_back_24), contentDescription = null) } },
                title = { Text(text = stringResource(id = R.string.license)) }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(vertical = 10.dp),
            content = {
                items(licenseList) { data ->
                    Surface {
                        Column {
                            Text(text = data.libraryName, fontSize = 18.sp)
                            Text(text = data.license)
                        }
                    }
                    Divider()
                }
            }
        )
    }
}

private val coil = LicenseData(
    libraryName = "coil-kt/coil",
    license = """
        Copyright 2021 Coil Contributors

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

           https://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
    """.trimIndent(),
)

private val rxDnssd = LicenseData(
    libraryName = "andriydruk/RxDNSSD",
    license = """
        Copyright (C) 2021 Andriy Druk

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
    """.trimIndent()
)

private val materialIcon = LicenseData(
    libraryName = "google/material-design-icons",
    license = """
        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
    """.trimIndent(),
)

private val ktor = LicenseData(
    libraryName = "ktorio/ktor",
    license = """
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
    """.trimIndent()
)

private val kotlinCoroutine = LicenseData(
    libraryName = "Kotlin/kotlinx.coroutines",
    license = """
           Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.

           Licensed under the Apache License, Version 2.0 (the "License");
           you may not use this file except in compliance with the License.
           You may obtain a copy of the License at

               http://www.apache.org/licenses/LICENSE-2.0

           Unless required by applicable law or agreed to in writing, software
           distributed under the License is distributed on an "AS IS" BASIS,
           WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
           See the License for the specific language governing permissions and
           limitations under the License.
    """.trimIndent()
)

private val okHttp = LicenseData(
    libraryName = "square/okhttp",
    license = """
        Copyright 2019 Square, Inc.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
    """.trimIndent()
)

/**
 * @param libraryName ライブラリ名
 * @param license ライセンス
 * */
private data class LicenseData(
    val libraryName: String,
    val license: String,
)