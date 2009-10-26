#/bin/bash
mkdir bin
nxjc dinaBOT/DinaBOT.java -d bin
cd bin
nxjlink dinaBOT/DinaBOT -cp ./ -o ../DinaBOT.nxj
cd ..
rm -rf bin