## Tech stack

- Language: Java 25
- Framework: SpringBoot 4
- ORM: Spring Data JDBC
- Database migration: Flyway
- Test: JUnit, Testcontainers, AssertJ

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

## 実行例

### 1. ユーザ登録

request
```
curl -XPOST -H "Content-Type: application/json" -d "{\"companyName\": \"upsider\", \"name\":\"upsider\", \"email\": \"upsider@gmail.com\", \"password\":\"letmein\"}" localhost:8080/users/register
```

response body
```json
{
    "companyName":"upsider",
    "createdAt":"2025-12-03T03:41:20.842218900Z",
    "email":"upsider@gmail.com",
    "encodedPassword":"letmein",
    "id":1,
    "name":"upsider",
    "updatedAt":"2025-12-03T03:41:20.842218900Z"
}
```

### 2. ログイン

request
```
curl -XPOST -H "Content-Type: application/json" -d "{\"email\": \"upsider@gmail.com\", \"password\":\"letmein\"}" localhost:8080/login
```

response body
```
{
    "accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJjb21wYW55TmFtZSI6InVwc2lkZXIiLCJuYW1lIjoidXBzaWRlciIsImVtYWlsIjoidXBzaWRlckBnbWFpbC5jb20ifQ.skDOs48k6DmYg4GPaOBKWyBtcjGN_9FjDZS_LlwshuA"
}
```
You can check the accessToken on https://www.jwt.io/ . The secret used to verify signature is `UgV4moJCxcu7KVfYNrnaHHbnYiISiKSBx`.

## How to ...

### 1. Connect to local database

```
docker run -it --rm --network upsider-kadai_default postgres:16.3-alpine psql -h postgres -d kadai -U kadai
```
password: `letmein`