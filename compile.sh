# var
Bin="IRModel/VSM/bin"
ClassPath="IRModel/VSM/src"
MainClass="test/demo/DemoIRSystem"

# make binary file folder
mkdir -p $Bin

# compile
javac -cp $ClassPath -d $Bin $ClassPath"/"$MainClass".java"
