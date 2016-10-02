# アプリの登録

## 資材の作成

登録するアプリとして、**実行可能jarファイル**を作成する。

今回は次の構成のアプリを登録することを考える。

```sh
.
├── META-INF
│   └── MANIFEST.MF
├── README.md
├── bin
├── lib
│   ├── LinphonecConnector.jar
│   ├── SRClientHelper.jar
│   ├── core-2.2.jar
│   ├── javase-2.2.jar
│   ├── jna-4.1.0.jar
│   ├── opencv-310.jar
│   └── sotalib.jar
├── sound
│   └── weather_report.wav
├── src
│   └── yamarobo
│       ├── LipSynching.java
│       └── Main.java
└── title.wav

6 directories, 13 files

```

`META-INF/MANIFEST.MF` は以下の内容。特にクラスパスの部分は必須。(配置先の環境が決まっているため)

```
Manifest-Version: 1.0
Class-Path: . /home/vstone/lib/core-2.2.jar /home/vstone/lib/gson-2.6.1.jar /home/vstone/lib/javase-2.2.jar /home/vstone/lib/jna-4.1.0.jar /home/vstone/lib/opencv-310.jar /home/vstone/lib/sotalib.jar /home/vstone/lib/SRClientHelper.jar ./lib/LinphonecConnector.jar 
Created-By: 1.8.0_51 (Oracle Corporation)
Main-Class: yamarobo.Main
```
(起動クラス`Main-Class`の記述はパッケージ名を含むFQDN)

アプリのディレクトリに移動して以下のコマンドを実行。

```sh
javac src/yamarobo/*.java -classpath 'lib/*' -d bin
jar cfm LipSynching.jar META-INF/MANIFEST.MF title.wav sound -C bin yamarobo
```

説明)

```sh
javac [java files] -classpath '[libraly/files/path/*]' -d [target directory for compiled files]
jar cfm [jar file name] [manifest file] (resource1 resource2 ...) -C [target directory for compiled files] [target class files directory relative path]
```

## 資材の配置 & 登録

作成した`jar`を配置する。
`scp`で配備後、`ssh`で登録対象にログインし設定を行う。

```sh
scp ./LipSynching.jar root@192.168.100.113:/home/vstone/vstonemagic/app/jar/LipSynching/
ssh root@192.168.100.113
cd  /home/vstone/vstonemagic/app/jar/LipSynching/
```

`title.wav`を`jar`内部から抜き出す。

```sh
jar xvf LipSynching.jar title.wav sound
```
説明)

```sh
jar xvf [jar file name] [target file names]
```

`app.properties` に次の設定を記述する。(`app2`を追加すると仮定する)

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

