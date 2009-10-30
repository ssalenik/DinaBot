#/bin/bash
mkdir bin
nxjc dinaBOT/DinaBOTMaster.java -d bin
cd bin
nxjlink dinaBOT/DinaBOTMaster -cp ./ -o ../DinaBOTMaster.nxj
cd ..
rm -rf bin