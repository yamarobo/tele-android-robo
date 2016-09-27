# アプリの登録

## 資材の作成

実行可能jarファイルの作成する。以下のコマンドで`java`のコンパイル, `jar`を作成する。

今回は次の構成のアプリケーションを登録することを考える。

```sh

```

```sh
javac [java files] -classpath '[libraly/files/path/*]' -d [target directory for compiled files]
jar cfm [jar file name] [manifest file] (resource1 resource2 ...) -C [target directory for compiled files] [target class files directory relative path]
```

例:

```sh
javac src/yamarobo/*.java -classpath 'lib/*' -d bin
jar cvfm LipSynching.jar META-INF/MANIFEST.MF title.wav sound -C bin yamarobo
```

`manifest file` は以下のフォーマットに則る。特にクラスパスの部分は必須。(配置先の環境が決まっているため)

```
Manifest-Version: 1.0
Class-Path: . /home/vstone/lib/core-2.2.jar /home/vstone/lib/gson-2.6.1.jar /home/vstone/lib/javase-2.2.jar /home/vstone/lib/jna-4.1.0.jar /home/vstone/lib/opencv-310.jar /home/vstone/lib/sotalib.jar /home/vstone/lib/SRClientHelper.jar ./lib/LinphonecConnector.jar 
Created-By: 1.8.0_51 (Oracle Corporation)
Main-Class: yamarobo.Main
```

(起動クラス`Main-Class`の記述はパッケージ名を含むFQDN)


## 資材の配置 & 登録


作成した`jar`を配置する。scpで配備後、sshで登録対象にログインする。

```sh
scp [jar/file/path] [user]@[host]:[/target/directory/path]
ssh [user]@[host]:[/target/directory/path]
```

`title.wav`を`jar`内部から抜き出す。

```sh
jar xvf [jar file name] title.wav
```

`app.properties` に次の設定を記述する。(app2を追加すると仮定する)

```
length=1
Debug=false
TimeZone=Asia/Tokyo
Startup=MyTel2/MyTel2.jar

app1.title=通話プログラム
app1.workingdir=MyTel2/
app1.jar=MyTel2.jar
app1.type=app
app1.trigger=
app1.triggeroption=

+ app2.title=LipSynching
+ app2.workingdir=LipSynching/
+ app2.jar=LipSynching.jar
+ app2.type=app
+ app2.trigger=
+ app2.triggeroption=
```

以上でアプリの登録が完了する。


