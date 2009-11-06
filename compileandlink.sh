#/bin/bash
mkdir bin
if [ $1 = "m" ]
then
	nxjc dinaBOT/DinaBOTMaster.java -d bin
	cd bin
	nxjlink dinaBOT/DinaBOTMaster -cp ./ -o ../DinaBOTMaster.nxj
	cd ..
	rm -rf bin
	if [ $2 = "f" ]
	then
		nxjupload DinaBOTSlave.nxj -u
	fi
fi
if [ $1 = "s" ]
then
	nxjc dinaBOT/DinaBOTSlave.java -d bin
	cd bin
	nxjlink dinaBOT/DinaBOTSlave -cp ./ -o ../DinaBOTSlave.nxj
	cd ..
	rm -rf bin
	if [ $2 = "f" ]
	then
		nxjupload DinaBOTSlave.nxj -u
	fi
fi
