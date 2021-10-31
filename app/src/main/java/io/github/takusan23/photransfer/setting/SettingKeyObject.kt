package io.github.takusan23.photransfer.setting

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DataStoreに格納する値のキーを列挙しています
 * */
object SettingKeyObject {

    /** 初期設定が終わったらtrue */
    val IS_ALREADY_SETUP = booleanPreferencesKey("is_already_setup")

    /** client か server。D.C.4の恋するMODEは神曲 */
    val MODE = stringPreferencesKey("mode")

    /** ポート番号 */
    val PORT_NUMBER = intPreferencesKey("port_number")

    /** 起動状態。起動中ならtrueにして */
    val IS_RUNNING = booleanPreferencesKey("is_running")

    /** サーバー情報をServiceからActivityへ渡したいので。サーバー起動時はデバイス名が入る */
    val SERVER_SIDE_DEVICE_NAME = stringPreferencesKey("server_side_device_name")

    /** クライアント側、最後に転送した時刻を入れています。初回起動時はセットアップ完了時 */
    val CLIENT_LATEST_UPLOAD_DATE = longPreferencesKey("client_latest_upload_date")

    /** [mode]がクライアントモードだった場合 */
    const val MODE_CLIENT = "client_mode"

    /** [mode]がサーバーモードだった場合 */
    const val MODE_SERVER = "server_mode"

    /** デフォポート番号 */
    const val DEFAULT_PORT_NUMBER = 4649

}