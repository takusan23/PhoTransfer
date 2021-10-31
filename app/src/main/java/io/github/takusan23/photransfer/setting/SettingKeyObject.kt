package io.github.takusan23.photransfer.setting

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DataStoreに格納する値のキーを列挙しています
 * */
object SettingKeyObject {

    /** client か server。D.C.4の恋するMODEは神曲 */
    val MODE = stringPreferencesKey("mode")

    /** ポート番号 */
    val PORT_NUMBER = intPreferencesKey("port_number")

    /** 起動状態。起動中ならtrueにして。サービス側で変更される */
    val IS_RUNNING = booleanPreferencesKey("is_running")

    /** サーバー情報をServiceからActivityへ渡したいので。サーバー起動時はデバイス名が入る */
    val SERVER_SIDE_DEVICE_NAME = stringPreferencesKey("server_side_device_name")

    /** クライアント側、最後に転送した時刻を入れています。初回起動時はセットアップ完了時 */
    val CLIENT_LATEST_TRANSFER_DATE = longPreferencesKey("client_latest_transfer_date")

    /** 充電中のみ転送する場合はtrue。サーバーでもクライアントでも動きます */
    val SETTING_TRANSFER_REQUIRE_CHARGING = booleanPreferencesKey("transfer_require_charging")

    /** 定期実行間隔 */
    val CLIENT_TRANSFER_INTERVAL_MINUTE = longPreferencesKey("client_transfer_interval_minute")

    /** [mode]がクライアントモードだった場合 */
    const val MODE_CLIENT = "client_mode"

    /** [mode]がサーバーモードだった場合 */
    const val MODE_SERVER = "server_mode"

    /** デフォポート番号 */
    const val DEFAULT_PORT_NUMBER = 4649

    /** デフォルト定期実行間隔（単位：分） */
    const val DEFAULT_CLIENT_TRANSFER_INTERVAL_MINUTE = 60L

}