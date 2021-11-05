# プライバシーポリシー

# 今北産業。三行で
- ローカルネットワーク（LAN）で完結します。
- 受け取り側は自身のIPアドレスを送信側へ通知します。
- 送信側は受け取り側へ画像を転送するために、画像データへアクセスします。
- オプトアウトはできません。

## 前提
**転送処理はローカルネットワーク内で完結します**（同じLAN、同じWi-Fi）

## 個人情報 のお話

- 写真
    - このアプリでは写真を転送するのが主な目的なため、基本的にはオプトアウトできません。
    - このアプリには定期的に転送する機能がありますが、この機能はOFFに出来ます。

- ローカル IPアドレス (とポート番号)
    - デバイス識別子として一応記載しておきます。
    - このアプリでは送信側にローカルのIPアドレスを送信します。(IPアドレス：ネットワーク上で場所を表す値)
    - 送信側はローカルIPアドレスを元に転送を行うので、このIPアドレスの送信はオプトアウト出来ません。

# その他権限など

- android.permission.INTERNET
    - ネットワークソケットを開くため
    - 転送にはHTTPを使っています。暗号化はされません。
        - ローカルネットワークで完結するので特に問題は無いはず
- android.permission.RECEIVE_BOOT_COMPLETED
    - デバイス起動時に写真受け取り待機を始めるため
- android.permission.FOREGROUND_SERVICE
    - 写真受け取り側が常に受け取りできるようにバックグラウンドで動作させるために必要です。
- android.permission.WRITE_EXTERNAL_STORAGE
    - Android 10 以降は利用していません。
    - Android 9 以前で受け取った写真を保存するために使っています
- android.permission.READ_EXTERNAL_STORAGE
    - 端末のすべての写真へアクセスするために使います
    - ファイルの転送木底以外では利用しません
- android.permission.ACCESS_NETWORK_STATE
    - Wi-Fiネットワークに接続中かどうかを受信するために使っています
