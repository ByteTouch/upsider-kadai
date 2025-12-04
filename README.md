## 課題内容

[Link](https://upsiders.notion.site/com-0dec58e566b04530bf3ba3001b4411e0)

## Tech stack

- Language: Java 25
- Framework: SpringBoot 4
- ORM: Spring Data JDBC
- Database migration: Flyway
- Test: JUnit, Testcontainers, AssertJ

## 説明・アピール

最初は推奨テックスタック（Kotlin, Ktor, Exposed）でチャレンジするつもりでしたが、やっている中でこの課題が広い範囲の技術力を要求すると感じて、自分の慣れていないテックスタックでは短期間で品質の良いものを作るのが難しいと判断し、Javaに切り替えました。

この Java プロジェクトは自慢のプラクティスで作りました。開発体験、アーキテクトなどをしっかり工夫しました。Java と SpringBoot のバージョンが最新です。Ktor と比較して頂ける良いチャンスだと思います。

以下は一部の特徴です。
- Java 25 [Compact Object Header](https://openjdk.org/jeps/519) を利用し、メモリコストを削減
- Java 21 [Virtual Thread](https://openjdk.org/jeps/444) を利用し、リクエスト処理能力を向上
- Java 25 [Scoped Value](https://openjdk.org/jeps/506) を利用し、ThreadLocal より軽い RequestContext を実現
- 独自のDDDへの理解とコード実現
- Testcontainers によるテスト実行時のミドルウェア（postgres）コンテナの自動立上げ・削除
- 自作の便利な VSCode Java 開発設定

それら以外、request validation、schema migration などのプロダクション機能も入っています。

課題内容に関して、自分は User → Login → Invoice の流れで実装しました。それぞれの考慮点を展開します。

#### User

- ユーザ管理、認証のような機能は、請求書機能から切り離すべきだと考えています。現実システムなら単独なサービスにするのが一般的、このプロジェクトでは単独な `auth` モジュール配下に置きました。
- パスワードは平文ではなく、暗号化した内容を保存しています。ログインは、入力値を同様に暗号化し、保存内容と一致するかチェックしています。その思想に関しては、[Spring Security PasswordEncoder](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage) をご参考ください。（Spring Security メイン機能を使いませんでした）

#### Login

- 課題要件に無いですが、一般的ではログイン操作はログイン履歴を生成するので、`Login` エンティティを加えました。それに User に依存しますが、ユーザ情報管理とは別物なので、モジュールを分けました。
- 現実世界では、OAuth2、OIDC などの規約をサポートする複雑な認証システムが使われ、iat、exp などの claims や refreshToken の発行などがあります。それは短期間で手動実現できないし、Spring Security で実現するなら Spring Security の理解度テストになるので、User の基本情報を含む JWT を HMAC 署名して返すという形にしました。
- アプリのコアレイヤー（Jakarta Servlet Filter API）で JWT の解析・検証を行い、ユーザ情報を RequestContext に保存します。認証失敗の場合 401 を返します。

#### Invoice

- お金の計算は浮動小数点演算ではなく BigDecimal で行う。
- 手数料率、税率は変わる可能性があるので、プロパティ化（環境変数化）にしました。
- スキーマの DECIMAL(precision, scale) をきちんと考慮した例外処理を実装しました。
- 請求書検索エンドポイントには、膨大データを防ぐためにページングを入れました。

## 開発環境準備

### JDK

- [Temurin JDK 25](https://adoptium.net/temurin/releases?version=25&os=any&arch=any) をインストール
- 環境変数が自動的に設定されなかった場合、手動設定
    - JDK の `bin` フォルダまでのパスを `Path` 環境変数に追加（既に別 JDK を設定している場合は置換）
    - JDK フォルダまでのパス（`/bin`要らない）を `JAVA_HOME` 環境変数として設定
- コマンド `java -version` 実行確認

### VSCode

初めて開く場合（例えば git clone 後）、`.vscode/settings.json` を作って、`.vscode/settings-default.json` の内容をコピペ

`.vscode/settings.json` が `.gitignore` に入っているので各開発者は好みに合わせて自由変更可能

VSCode でプロジェクトを開くとき（.vscode/extensions.json に設定した）推奨 Extensions が提示される。

### Docker

Docker が動いている状態

## ローカル起動

1. Postgres 起動
    ```
    docker compose docker-compose.yml up -d
    ```
2. Schema migration 実行

    ```
    ./mvnw flyway:migrate
    ```
3. App 起動：`src/main/java/com/example/upsider_kadai/UpsiderKadaiApplication.java` を開いて、`main` メソッド上の `Run` ボタンを押下

## テスト実行

方法が二つあり

1. Command Line 実行：
    ```
    ./mvnw clean test
    ```
2. UI 実行：
    - VSCode 左側のフラスコ
    - テストファイル内の実行アイコン

## 実行例（Windows CMD 環境）

### 1. ユーザ登録

request
```
curl -XPOST -H "Content-Type: application/json" -d "{\"companyName\": \"upsider\", \"name\":\"upsider\", \"email\": \"upsider@gmail.com\", \"password\":\"letmein\"}" localhost:8080/users/register
```

response body
```json
{
    "id":1,
    "companyName":"upsider",
    "name":"upsider",
    "email":"upsider@gmail.com",
    "encodedPassword":"{bcrypt}$2a$10$KUpyjYC3goNxc97.KnQunuUCstw0VK1hedKaTGtRdHBmFm9qVdQ5q","createdAt":"2025-12-04T17:06:15.806Z",
    "updatedAt":"2025-12-04T17:06:15.806Z"
}
```

### 2. ログイン

request
```
curl -XPOST -H "Content-Type: application/json" -d "{\"email\": \"upsider@gmail.com\", \"password\":\"letmein\"}" localhost:8080/login
```

response body
```json
{
    "accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImNvbXBhbnlOYW1lIjoidXBzaWRlciIsIm5hbWUiOiJ1cHNpZGVyIiwiZW1haWwiOiJ1cHNpZGVyQGdtYWlsLmNvbSJ9.NAMBZZJAzslJAjbsZwmHSiYz7SMflwtbw6dG4U2wSXw"
}
```
You can check the accessToken on https://www.jwt.io/ . The secret used to verify signature is `UgV4moJCxcu7KVfYNrnaHHbnYiISiKSBx`.

### 3. 請求書登録

request
```
curl -XPOST -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImNvbXBhbnlOYW1lIjoidXBzaWRlciIsIm5hbWUiOiJ1cHNpZGVyIiwiZW1haWwiOiJ1cHNpZGVyQGdtYWlsLmNvbSJ9.NAMBZZJAzslJAjbsZwmHSiYz7SMflwtbw6dG4U2wSXw" -d "{\"paymentAmount\":10000, \"paymentDueDate\":\"2025/12/20\"}" localhost:8080/invoices
```

response body
```json
{
    "id":1,
    "userId":1,
    "issueDate":"2025-12-05",
    "paymentAmount":10000.00,
    "fee":400.00,
    "feeRate":4.0,
    "taxAmount":40.00,
    "taxRate":10.0,
    "totalAmount":10440.00,
    "paymentDueDate":"2025-12-20",
    "createdAt":"2025-12-04T17:11:43.628Z",
    "updatedAt":"2025-12-04T17:11:43.628Z"
}
```

### 4. 請求書検索

request
```
curl -XPOST -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImNvbXBhbnlOYW1lIjoidXBzaWRlciIsIm5hbWUiOiJ1cHNpZGVyIiwiZW1haWwiOiJ1cHNpZGVyQGdtYWlsLmNvbSJ9.NAMBZZJAzslJAjbsZwmHSiYz7SMflwtbw6dG4U2wSXw" -d "{\"userId\":1, \"paymentDueDateFrom\":\"2025/12/20\", \"paymentDueDateTo\":\"2025/12/20\", \"pageNumber\":0, \"pageSize\":20}" localhost:8080/invoices/search
```

response body
```json
[{
    "id":1,
    "userId":1,
    "issueDate":"2025-12-05",
    "paymentAmount":10000.00,
    "fee":400.00,
    "feeRate":4.00,
    "taxAmount":40.00,
    "taxRate":10.00,
    "totalAmount":10440.00,
    "paymentDueDate":"2025-12-20",
    "createdAt":"2025-12-04T17:11:43.628Z",
    "updatedAt":"2025-12-04T17:11:43.628Z"
}]
```

## How to ...

### 1. Connect to local database

```
docker run -it --rm --network upsider-kadai_default postgres:16.3-alpine psql -h postgres -d kadai -U kadai
```
password: `letmein`