<p align="center">
<img src="https://imgur.com/oE9eIj5.png" width="100">
</p>

<h3>
<p align="center">
PhoTransfer
</p>
</h3>

<p align="center">
写真転送アプリ。Android 7 以降で使えます。
</p>

# できること
ローカルネットワークを利用して一方的に写真を転送出来ます。（手動/定期的）
写真アプリ等で共有画面を開いてもらうとこのアプリがあるのでそこからアップロードも可能です。

## いるこれ？
`Pixel 5`を含む以前のPixel端末にはGoogleフォトの無制限アップロードの特典があるので、
Pixelに写真を転送する→Googleフォトにアップロードする これで無制限アップロード古事記ができる。
(正直無制限アップロードよりもその先にあるGoogleフォトへ写真を集約できるのが便利。PCに転送するのめんどいし)

(対策されるかもしれないけど、現状はPixelからアップロードした写真が無制限アップロードの対象になる。Exifとか写真のメタデータとかは関係ないみたい)

# 必要なもの
- 送信、受信共にAndroid 7以降
  - やろうと思えば送信側はAndroid 7以前も対応できたと思う。けど別々に開発するのもなぁ
- Wi-Fi環境
  - 内部サーバーを使って受信するので

# 仕組み
受信側（サーバー側）は、バックグラウンドで内部サーバーを立ち上げます。
それから、IPアドレスが動的に変更される可能性があるので、Androidのネットワークサービスディスカバリで見つけられるようにしてあります。
認証とか実装してないので注意してください(同じWi-Fiならアップロードできる)。あと同じLAN上に一つまでしかサーバー側は作れないと思います。

送信側は、定期的に写真を受信側へ転送しています。
AndroidのWorkManagerにやらせてます。よって最低送信間隔は15分であり、送信間隔はあくまでも最低値みたいな所あるので多分スマホの状態によっては遅れると思います。
**中華系スマホはバックグラウンドプロセスくっっっそ厳しい** ので定期実行できないかもしれない。受信側を見つけられない場合なんかも転送出来ないです。
手動で選んで転送もできます。

一応両者ともに充電中のみ動くように設定可能です。

# 使い方
## サーバー側
- 初回起動時に聞かれるので受信側を選ぶ
- 準備ができると、デバイス名の上の`---`が`PhoTransfer_${デバイス名}`になる。終了するとまた`---`に戻る

## クライアント側
- 初回起動時に送信側を選ぶ
- 他アプリの作成した写真にアクセスしたいのでストレージ読み取り権限を与えてやってください
- 設定が終わり、送信側が検出できた場合は手動で選択できます。このとき複数選ぶことも出来ます
- 手動で転送するから勝手に転送しないで！！！って場合「送信側として実行中です」の部分を押してOFFにすればいいです

## PC版クライアント
`http://{IPアドレス}:{ポート番号}/browser`へアクセスすることで、ブラウザ版投稿画面を開くことが出来ます。
クリップボードから画像を投稿できます。

# スクリーンショット
UI作るのって大変。Material Youに対応してますがそもそも色あんまり使ってないからわからんな

<p align="center">
<img src="https://imgur.com/DV24XGs.png" width="200">
<img src="https://imgur.com/ZUUlzI6.png" width="200">
<img src="https://imgur.com/wnukal0.png" width="200">
</p>

# よくある質問（想定）

- 定期的に転送出来てない
    - 定期実行時にサーバー側を見つけられないと転送できません。多分高確率で見つけられると思うんだけどよく失敗してる（え？）
- サーバー側の準備ができない
    - ライブラリがネイティブコード？使っているので私にもわからん。
    - そもそもIPアドレス固定化しとけばこんな事する必要ないのにね。
- Android 7以降である必要性
    - 内部サーバーのライブラリがAndroid 7以降にしか対応してなかった(Ktorってやつ。めちゃ書きやすい)

# 開発者向け情報
読んでる人いるんか？

## 実行
このリポジトリをクローンして、多分安定版Android Studioでビルドできるはずです。
~~Jetpack Composeとか使ってるのでPCとお部屋が温まる（？）~~

## モジュール
`server`、`client`、`app`モジュールがあります。

- server
    - Ktorでリクエストを待ち受ける
        - 一応疎通確認用に`http://${IPアドレス}:${ポート番号}/`にGETリクエストすると雑な説明が返ってきます。
        - API詳細は後述
        - テストコードあるのでAPI検証で使ってください
- client
    - 内部サーバーにデータをPOSTする関数を置いている
    - `OkHttp`使ってる
- app
    - `server`、`client`を読み込んでる。上記2つはAndroid依存から切り離したものになっているので、Android依存はここにまとまってる。
    - UIはJetpack Compose。雰囲気（なぜか変換~~できない~~できてる）で使ってるので間違ってるかも。
    - SharedPreferenceの代わりにDataStoreを利用。Flowで受け取れるの便利～
    - 定期実行はWorkManager、サーバー側はForegroundなService
    - ローカルネットワークからサーバー側を検出できるように、`android.net.nsd`あたりを使おうと思ったんだけど、なんか調子悪い
        - サーバー側は有志のライブラリを利用：`com.github.andriydruk:dnssd:0.9.15`
        - クライアント側はAndroidの方を使ってる。

```
+--------+      +-------+       +--------+
| server | ---> |  app  |  <--- | client |
+--------+      +-------+       +--------+
```

## 内部サーバーのAPI

- 疎通確認用API
- `http://${IPアドレス}:${ポート番号}/`
    - GETリクエスト
    - 内部サーバーが生きてるかの確認で使えます
    - 適当な説明が返ってくれば成功

- PC版クライアント
- `http://${IPアドレス}:${ポート番号}/browser`
    - クリップボードから画像を投稿できるHTMLを返します
        - HTMLは`server`モジュール内`resources`にある`index.html`です

- 転送用API
- `http://${IPアドレス}:${ポート番号}/upload`
    - リクエストヘッダ
        - POSTリクエスト
        - User-Agent (必須)
            - デバイス名（`Build.MODEL`）
            - フォルダ分けする際にデバイス名をつけるので必須です
            - もし、User-Agentを変更できない場合（ブラウザ版がそう）は、「Folder-Name」という名前でヘッダーを追加してください。
        - Content-Type（必須？）
            - Multipart/form-data
    - リクエストボディ
        - Multipart/form-data 形式で送信する必要があります。
        - ファイルを添付する際の`name`は使ってません。
            - 代わりにファイルの名前がそのまま使われます

## ローカルから検出出来ているのか？
`mDNS`あたりの技術だと思います。

登録時は以下の値をセットしています。`サービスタイプ`

| name           | value                              |
|----------------|------------------------------------|
| ポート番号     | 初回起動時に変更可能なので各自違う |
| サービス名     | `PhoTransfer_${デバイス名}`        |
| サービスタイプ | `_photransfer._tcp`                |

`Apple`の`iTunes`とかを入れている場合は以下のコマンドをターミナル(コマンドプロンプト/PowerShell/コンソール)に入れると検索してくれます。
(入っていない場合は`Bonjour`を手動で導入してもいい)

```
dns-sd -B _photransfer._tcp
```

# その他

転送時に、一時的にアプリ固有ストレージにコピーします。AndroidのUriじゃなくてFileのパスが必要だったため。

以上です。