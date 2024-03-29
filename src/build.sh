current_working_dir=`pwd`

# 対象ごとに変更(特にapplication_name, package_name)
application_name=FaceDetect
package_name=jp.co.yamarobo
application_root=/Users/ushumpei/Documents/telenoid/src/yamarobo/
main_class=${package_name}.Main

cd ${application_root}

mkdir -p -v ./META-INF
cat << MANIFEST > META-INF/MANIFEST.MF
Manifest-Version: 1.0
Class-Path: . /home/vstone/lib/core-2.2.jar /home/vstone/lib/javase-2.2.jar /home/vstone/lib/jna-4.1.0.jar /home/vstone/lib/opencv-310.jar /home/vstone/lib/sotalib.jar /home/vstone/lib/SRClientHelper.jar ./commons-io-2.5.jar 
Created-By: 1.8.0_51 (Oracle Corporation)
Main-Class: ${main_class}

MANIFEST

mkdir -p -v ./bin
javac -verbose src/${package_name//.//}/*.java -classpath 'lib/*' -d bin
jar cfm ${application_name}.jar ./META-INF/MANIFEST.MF ./resource -C ./bin ${package_name//.//}

rm -rvf ./META-INF ./bin
mv ${application_name}.jar ${current_working_dir}
